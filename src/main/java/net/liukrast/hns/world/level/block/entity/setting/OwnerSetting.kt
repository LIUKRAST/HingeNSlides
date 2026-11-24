package net.liukrast.hns.world.level.block.entity.setting

import com.mojang.serialization.Codec
import gg.essential.elementa.dsl.constrain
import gg.essential.elementa.dsl.percent
import gg.essential.elementa.dsl.pixels
import net.liukrast.blocknet.setting.BlockNetSetting
import net.minecraft.client.Minecraft
import net.minecraft.core.UUIDUtil
import net.minecraft.world.entity.player.Player
import net.swzo.brass.ui.kit.base.BrassAccent
import net.swzo.brass.ui.kit.input.BrassButton
import net.swzo.brass.ui.kit.input.BrassConfirmSlider
import net.swzo.brass.ui.kit.layout.BrassSpacing
import net.swzo.brass.ui.kit.layout.BrassVBox
import net.swzo.brass.ui.kit.media.BrassPlayerHead
import net.swzo.brass.ui.kit.media.BrassPlayerHead.Source
import net.swzo.brass.ui.kit.net.BrassSkins
import net.swzo.brass.ui.kit.surface.BrassListRow
import net.swzo.brass.ui.kit.text.BrassTagStyle
import java.util.*

class OwnerSetting(
    category: String,
    getter: () -> UUID,
    setter: (UUID) -> Unit
) : BlockNetSetting<UUID>(category, UUID::class, getter, setter) {
    companion object {
        val NONE = UUID(0L, 0L)
        val CLAIM = UUID(1L, 0L)
        val RELEASE = UUID(2L, 0L)
    }

    override fun ui(context: Context<UUID>): (Result<UUID>) -> Unit {
        val me = Minecraft.getInstance().player?.uuid

        val box = BrassVBox(gap = BrassSpacing.GAP, bleed = false)
            .constrain { width = 100.percent() }
        context.form.add(box)

        var shown: UUID? = null

        fun build(owner: UUID, name: String?) {
            box.clearChildren()

            if (owner == NONE) {
                box.add(
                    BrassButton("Claim door") { context.updater(CLAIM) }
                        .constrain { width = 100.percent(); height = 20.pixels() }
                )
                return
            }

            val mine = owner == me
            box.add(
                BrassListRow(name ?: owner.toString().take(8), if (mine) "You own this door" else "Owner")
                    .leading(BrassPlayerHead(owner.toString(), source = Source.REMOTE, size = 16f), 16f)
                    .tag(if (mine) "YOU" else null, BrassTagStyle.SUCCESS)
                    .constrain { width = 100.percent() }
            )

            if (mine) {
                box.add(
                    BrassConfirmSlider("Release door", "Releasing…", BrassAccent.DANGER, 2f) {
                        context.updater(RELEASE)
                    }.constrain { width = 100.percent(); height = 20.pixels() }
                )
            }
        }

        fun refresh(owner: UUID) {
            if (owner == shown) return
            shown = owner
            build(owner, null)

            if (owner == NONE) return
            BrassSkins.load(owner.toString()).thenAccept { skin ->
                Minecraft.getInstance().execute {
                    if (shown == owner) build(owner, skin?.name ?: "Unknown")
                }
            }
        }

        refresh(context.value)
        return { res -> refresh(res.value) }
    }

    override fun prepareSet(player: Player, raw: Any?): (() -> Unit)? {
        val value = fromNetwork(raw) ?: return null
        val current = getter()
        return when (value) {
            CLAIM -> if (current == NONE) ({ setter(player.uuid) }) else null
            RELEASE -> if (current == player.uuid || player.hasPermissions(2)) ({ setter(NONE) }) else null
            else -> null
        }
    }

    override fun codec(): Codec<UUID> = UUIDUtil.CODEC

    override fun fromNetwork(raw: Any?): UUID? = when (raw) {
        is UUID -> raw
        is String -> runCatching { UUID.fromString(raw) }.getOrNull()
        else -> null
    }

    override fun toNetwork(value: UUID): Any = value.toString()
}