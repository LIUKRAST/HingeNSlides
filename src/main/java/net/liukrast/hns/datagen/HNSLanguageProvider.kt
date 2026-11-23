package net.liukrast.hns.datagen

import net.liukrast.hns.HNSConstants
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.data.PackOutput
import net.neoforged.neoforge.common.data.LanguageProvider

class HNSLanguageProvider(output: PackOutput) : LanguageProvider(output, HNSConstants.MOD_ID, "en_us") {
    override fun addTranslations() {
        HNSConstants.getRegistryEntries(BuiltInRegistries.ITEM)
            .forEach { e -> add(e.value.descriptionId, autoName(e.key)) }

        add("itemGroup.${HNSConstants.MOD_ID}", "Hinge & Slides")

        add("blocknet.category.behaviour", "Behaviour")
        add("blocknet.category.security", "Security")
        add("blocknet.category.visual", "Visual")

        add("blocknet.setting.manual", "Manual")
        add("blocknet.setting.redstone", "Redstone")
        add("blocknet.setting.animation_duration", "Animation Duration")
        add("blocknet.setting.animation", "Animation")
        add("blocknet.setting.color", "Color")

        add("blocknet.setting.animation.normal", "Normal")
        add("blocknet.setting.animation.vanilla", "Vanilla")
        add("blocknet.setting.animation.slam", "Slam")

        add("blocknet.setting.texture", "Texture")
        add("blocknet.setting.texture.normal", "Normal")
        add("blocknet.setting.texture.connected", "Connected")

        add("message.${HNSConstants.MOD_ID}.door.locked", "This door is locked")
        add("message.${HNSConstants.MOD_ID}.key.already_bound", "This key is already bound")
        add("message.${HNSConstants.MOD_ID}.key.max_reached", "You cannot bound another door to this key")

        add("blocknet.setting.hinge", "Hinge")
    }

    fun autoName(id: String) : String {
        return id.split("_")
            .filter { it.isNotEmpty() }
            .joinToString(" ") { word -> word.replaceFirstChar { it.uppercase() } }
    }
}