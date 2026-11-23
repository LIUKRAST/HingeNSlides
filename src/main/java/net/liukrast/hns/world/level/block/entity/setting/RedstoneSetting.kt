package net.liukrast.hns.world.level.block.entity.setting

import gg.essential.elementa.components.UIContainer
import gg.essential.elementa.constraints.CenterConstraint
import gg.essential.elementa.constraints.SiblingConstraint
import gg.essential.elementa.dsl.childOf
import gg.essential.elementa.dsl.constrain
import gg.essential.elementa.dsl.pixels
import net.liukrast.blocknet.setting.BooleanSetting
import net.minecraft.network.chat.Component
import net.swzo.brass.ui.Colors
import net.swzo.brass.ui.component.BrassText
import net.swzo.brass.ui.kit.base.BrassAccent
import net.swzo.brass.ui.kit.base.proxiedBy
import net.swzo.brass.ui.kit.input.BrassCheckbox
import net.swzo.brass.ui.kit.surface.BrassTooltip
import net.swzo.brass.ui.kit.text.BrassTag
import net.swzo.brass.ui.kit.text.BrassTagStyle

class RedstoneSetting(category: String, getter: () -> Boolean, setter: (Boolean) -> Unit) : BooleanSetting(category, getter, setter) {
    override fun ui(context: Context<Boolean>): (Result<Boolean>) -> Unit {
        val box = BrassCheckbox(initial = context.value) { v -> context.updater.invoke(v) }
        val row = UIContainer().constrain { height = 13f.pixels() }

        box.constrain { x = 0.pixels(); y = CenterConstraint(); width = 13f.pixels(); height = 13f.pixels() } childOf row

        val tag = BrassTag("WARNING", BrassTagStyle.WARNING).constrain {
            x = SiblingConstraint(4f)
            y = CenterConstraint()
        } childOf row

        BrassTooltip.attach(tag, "Warning", "A key is configured, but anyone with a lever can open the door", BrassAccent.DANGER)

        val caption = BrassText.body(getTitle(context.id), Colors.UI_TEXT_DARK).constrain {
            x = SiblingConstraint(4f); y = CenterConstraint()
        }.fitWidth() childOf row

        box.proxiedBy(caption)

        fun applyState(values: Values) {
            val locked = values["key"] != KeySetting.NONE
            val warn = locked && box.checked
            if(warn) tag.unhide() else tag.hide()
        }

        applyState(context.values)
        context.form.add(row)

        return { res ->
            box.setSilently(res.value)
            applyState(res.values)
        }
    }
}