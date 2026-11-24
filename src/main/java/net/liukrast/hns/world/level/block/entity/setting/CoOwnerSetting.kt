package net.liukrast.hns.world.level.block.entity.setting

import com.mojang.serialization.Codec
import gg.essential.elementa.dsl.childOf
import gg.essential.elementa.dsl.constrain
import gg.essential.elementa.dsl.percent
import gg.essential.elementa.dsl.pixels
import net.liukrast.blocknet.setting.BlockNetSetting
import net.minecraft.client.Minecraft
import net.minecraft.core.UUIDUtil
import net.minecraft.world.entity.player.Player
import net.swzo.brass.ui.kit.base.BrassAccent
import net.swzo.brass.ui.kit.input.BrassButton
import net.swzo.brass.ui.kit.layout.BrassScrollArea
import net.swzo.brass.ui.kit.layout.BrassSpacing
import net.swzo.brass.ui.kit.layout.BrassVBox
import net.swzo.brass.ui.kit.media.BrassIcons
import net.swzo.brass.ui.kit.media.BrassPlayerHead
import net.swzo.brass.ui.kit.net.BrassSkins
import net.swzo.brass.ui.kit.surface.BrassListRow
import net.swzo.brass.ui.kit.surface.BrassModal
import java.util.UUID
import kotlin.reflect.KClass

class CoOwnersSetting(
    category: String,
    getter: () -> Set<UUID>,
    setter: (Set<UUID>) -> Unit,
    private val isOwner: (Player) -> Boolean
) : BlockNetSetting<Set<UUID>>(category, SET_TYPE, getter, setter) {

    companion object {
        const val MAX = 16

        @Suppress("UNCHECKED_CAST")
        private val SET_TYPE = Set::class as KClass<Set<UUID>>

        val NONE: Set<UUID> = emptySet()
    }

    override fun canEdit(player: Player) = isOwner(player)

    override fun prepareSet(player: Player, raw: Any?): (() -> Unit)? {
        if (!canEdit(player)) return null
        val value = fromNetwork(raw) ?: return null
        if (value.size > MAX || OwnerSetting.NONE in value) return null
        return { setter(value) }
    }

    override fun codec(): Codec<Set<UUID>> =
        UUIDUtil.CODEC.listOf().xmap({ it.toSet() }, { it.toList() })

    override fun fromNetwork(raw: Any?): Set<UUID>? {
        if (raw !is Collection<*>) return null
        return raw.map { parse(it) ?: return null }.toSet()
    }

    override fun toNetwork(value: Set<UUID>): Any = value.map { it.toString() }

    private fun parse(x: Any?): UUID? = when (x) {
        is UUID -> x
        is String -> runCatching { UUID.fromString(x) }.getOrNull()
        else -> null
    }

    override fun ui(context: Context<Set<UUID>>): (Result<Set<UUID>>) -> Unit {
        val box = BrassVBox(gap = BrassSpacing.GAP, bleed = false).constrain { width = 100.percent() }
        context.form.add(box)

        var current: Set<UUID> = context.value
        val names = HashMap<UUID, String>()
        val loading = HashSet<UUID>()

        fun editable(): Boolean = Minecraft.getInstance().player?.let(::canEdit) ?: false
        fun push(next: Set<UUID>) = context.updater(next)

        fun openPicker() {
            val me = Minecraft.getInstance().player?.uuid
            val candidates = Minecraft.getInstance().connection?.onlinePlayers.orEmpty()
                .map { it.profile.id to it.profile.name }
                .filter { (id, _) -> id != me && id !in current }

            lateinit var m: BrassModal
            m = BrassModal("Add co-owner", 200f, 140f)
                .body { b ->
                    val list = BrassScrollArea.list().constrain {
                        x = 0.pixels(); y = 0.pixels(); width = 100.percent(); height = 100.percent()
                    } childOf b
                    if (candidates.isEmpty()) {
                        list.add(BrassListRow("No players available", "Only online players can be added"))
                    } else candidates.forEach { (id, name) ->
                        list.add(
                            BrassListRow(name, "Click to add") {
                                m.dismiss()
                                push(current + id)
                            }.leading(BrassPlayerHead(id.toString(), source = BrassPlayerHead.Source.REMOTE, size = 16f), 16f)
                        )
                    }
                }
                .footer(BrassButton("Cancel") { m.dismiss() })
            m.show(context.form)
        }

        fun build() {
            box.clearChildren()
            val canEdit = editable()

            if (current.isEmpty()) {
                box.add(BrassListRow("No co-owners", if (canEdit) "Add players you trust" else "Only the owner can add them"))
            }
            current.forEach { id ->
                val row = BrassListRow(names[id] ?: id.toString().take(8), "Co-owner")
                    .leading(BrassPlayerHead(id.toString(), source = BrassPlayerHead.Source.REMOTE, size = 16f), 16f)
                    .constrain { width = 100.percent() }
                if (canEdit) {
                    row.action(BrassIcons.TRASH, "Remove", BrassAccent.DANGER) { push(current - id) }
                    row.revealActions = true
                }
                box.add(row)

                if (id !in names && loading.add(id)) {
                    BrassSkins.load(id.toString()).thenAccept { skin ->
                        Minecraft.getInstance().execute {
                            names[id] = skin?.name ?: "Unknown"
                            if (id in current) build()
                        }
                    }
                }
            }

            if (canEdit && current.size < MAX) {
                box.add(BrassButton("Add co-owner") { openPicker() }
                    .constrain { width = 100.percent(); height = 20.pixels() })
            }
        }

        build()
        return { res -> current = res.value; build() }
    }
}