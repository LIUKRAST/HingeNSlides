package net.liukrast.hns.world.level.block.entity.setting

import com.mojang.serialization.Codec
import gg.essential.elementa.dsl.constrain
import gg.essential.elementa.dsl.percent
import gg.essential.elementa.dsl.pixels
import net.liukrast.blocknet.setting.BlockNetSetting
import net.minecraft.core.UUIDUtil
import net.swzo.brass.ui.kit.base.BrassAccent
import net.swzo.brass.ui.kit.input.BrassConfirmSlider
import net.swzo.brass.ui.kit.layout.BrassForm
import java.util.*

class KeySetting(
    category: String,
    getter: () -> UUID,
    setter: (UUID) -> Unit
) : BlockNetSetting<UUID>(
    category,
    UUID::class,
    getter,
    setter
) {
    companion object {
        val NONE = UUID(0L, 0L)
    }

    override fun ui(context: Context<UUID>): (Result<UUID>) -> Unit {
        //TODO("Translate")
        val box = BrassConfirmSlider("Unbind key", "Key unbound", BrassAccent.DANGER, 2f) {
            context.updater(NONE)
        }.constrain {
            x = 0.pixels(); y = 0.pixels()
            width = 100.percent(); height = 20.pixels()
        }
        if (context.value == NONE) box.hide()
        context.form.add(box)

        return { res -> if (res.value == NONE) box.hide() else box.unhide() }
    }

    override fun codec(): Codec<UUID> = UUIDUtil.CODEC

    override fun fromNetwork(raw: Any?): UUID? = when (raw) {
        is UUID -> raw
        is String -> runCatching { UUID.fromString(raw) }.getOrNull()
        else -> null
    }

    override fun toNetwork(value: UUID): Any? = value.toString()
}