package net.liukrast.hns.client.gui.screens

import gg.essential.elementa.UIComponent
import gg.essential.elementa.dsl.*
import net.liukrast.hns.HNSConstants
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.swzo.brass.ui.BrassAppScreen
import net.swzo.brass.ui.kit.input.BrassCheckbox
import net.swzo.brass.ui.kit.input.BrassDropdown
import net.swzo.brass.ui.kit.input.BrassSlider
import net.swzo.brass.ui.kit.layout.*
import net.swzo.brass.ui.kit.media.BrassIcons
import net.swzo.brass.ui.kit.net.BrassNet
import net.swzo.brass.ui.kit.net.payloadAs
import java.awt.Color

class DoorConfigScreen(pos: BlockPos) : BrassAppScreen(
    title = Component.translatable("screen.${HNSConstants.MOD_ID}.door_config").string,
    maxWidth = 430f,
    maxHeight = 270f,
    backdrop = Color(0, 0, 0, 72),
) {
    private val doorPos = DoorPos.of(pos)

    private val current = mutableMapOf<String, Any>()
    private var options: Map<String, List<String>> = emptyMap()
    private var built = false

    private val checkboxes = mutableMapOf<String, MutableList<BrassCheckbox>>()
    private val dropdowns = mutableMapOf<String, MutableList<BrassDropdown>>()
    private var ranges: Map<String, DoorRange> = emptyMap()
    private val sliders = mutableMapOf<String, MutableList<BrassSlider>>()

    init {
        refresh()
    }

    //region Network
    private fun refresh() {
        BrassNet.send(DoorActions.query, doorPos) { result ->
            val snap = if (result.ok) result.payloadAs(DoorSnapshot::class.java) else null
            snap?.let {
                if (!built) {
                    built = true
                    build(it)
                } else {
                    sync(it.values)
                }
            }
        }
    }

    private fun update(key: String, value: Any) {
        BrassNet.send(DoorActions.update, DoorUpdate(doorPos, mapOf(key to value))) {
            refresh()
        }
    }

    private fun sync(data: Map<String, Any>) {
        current.putAll(data)
        for ((key, v) in data) {
            when (v) {
                is Boolean -> checkboxes[key]?.forEach { it.setSilently(v) }
                is String -> dropdowns[key]?.forEach { it.setSilently(v) }
                is Double -> sliders[key]?.forEach { it.setSilently(v.toFloat()) }
            }
        }
    }
    //endregion

    //region Building
    private fun build(snap: DoorSnapshot) {
        current.putAll(snap.values)
        options = snap.options
        ranges = snap.ranges

        val grouped = linkedMapOf<String, MutableList<String>>()
        for (key in snap.values.keys) {
            grouped.getOrPut(snap.categories[key] ?: "general") { mutableListOf() }.add(key)
        }

        val pages = BrassPages(slide = BrassPages.Slide.VERTICAL)

        pages.page(translateCategory("overview"), BrassIcons.HOME) { root ->
            fill(form(grouped.values.flatten())) childOf root
        }

        for ((category, keys) in grouped) {
            pages.page(translateCategory(category), iconFor(category)) { root ->
                fill(form(keys)) childOf root
            }
        }

        val rail = pages.navRail(pinnedTop = 1).constrain {
            x = 0.pixels(); y = 0.pixels()
            width = BrassBreakpoint.proportional(fraction = 0.26f, min = 92f, max = 124f)
            height = 100.percent()
        } childOf body

        pages.constrain {
            x = basicXConstraint { rail.getRight() + RAIL_GAP }; y = 0.pixels()
            width = basicWidthConstraint { c -> (c.parent.getRight() - rail.getRight() - RAIL_GAP).coerceAtLeast(0f) }
            height = 100.percent()
        } childOf body

        BrassDivider.between(body, rail, pages, span = rail, inset = 0f)
    }

    private fun form(keys: List<String>): BrassForm {
        val form = BrassForm()
        for (key in keys) {
            val value = current[key] ?: continue
            when (value) {
                is Boolean -> {
                    val box = BrassCheckbox(initial = value) { v -> update(key, v) }
                    checkboxes.getOrPut(key) { mutableListOf() }.add(box)
                    form.addRow(translate(key), 18, box)
                }

                is String if options[key] != null -> {
                    val items = options.getValue(key).map { id -> id to translate("$key.$id") }
                    val dropdown = BrassDropdown(items, initial = value) { chosen -> update(key, chosen) }
                    dropdowns.getOrPut(key) { mutableListOf() }.add(dropdown)
                    form.addField(translate(key), dropdown)
                }

                is Double if ranges[key] != null -> {
                    val range = ranges.getValue(key)
                    var pending = value.toFloat()
                    var dirty = false
                    val slider = BrassSlider(range.min, range.max, pending, format = { fmt(it, range.step) }) { v ->
                        pending = v
                        dirty = true
                    }
                    slider.onMouseRelease {
                        if (dirty) {
                            dirty = false
                            update(key, pending)
                        }
                    }
                    sliders.getOrPut(key) { mutableListOf() }.add(slider)
                    form.addField(translate(key), slider)
                }
            }
        }
        return form
    }

    private fun <T : UIComponent> fill(c: T): T =
        c.constrain { x = 0.pixels(); y = 0.pixels(); width = 100.percent(); height = 100.percent() }
    //endregion

    //region Text & Icons
    private fun translate(key: String) = Component.translatable("door.property.$key").string

    private fun translateCategory(category: String) =
        Component.translatable("door.category.$category").string

    private fun iconFor(category: String) = when (category) {
        "visual" -> BrassIcons.IMAGE
        else -> BrassIcons.GEAR
    }

    private fun fmt(v: Float, step: Float) =
        if (step >= 1f) v.toInt().toString() else "%.2f".format(v)
    //endregion

    private companion object {
        const val RAIL_GAP = 12f
    }
}