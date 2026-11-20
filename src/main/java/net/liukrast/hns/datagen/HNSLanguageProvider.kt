package net.liukrast.hns.datagen

import net.liukrast.hns.HNSConstants
import net.liukrast.hns.registry.HNSBlocks
import net.liukrast.hns.registry.HNSItems
import net.minecraft.data.PackOutput
import net.neoforged.neoforge.common.data.LanguageProvider

class HNSLanguageProvider(output: PackOutput) : LanguageProvider(output, HNSConstants.MOD_ID, "en_us") {
    override fun addTranslations() {
        add("screen.${HNSConstants.MOD_ID}.door_config", "Door Configuration")
        add("door.category.overview", "Overview")
        add("door.category.general", "General")
        add("door.property.manual", "Manual Interaction")
        add("door.property.redstone", "Redstone enable")
        add("door.category.visual", "Visual")

        add("door.property.animation", "Animation Type")
        add("door.property.animation.normal", "Normal")
        add("door.property.animation.vanilla", "Vanilla")
        add("door.property.animation.slam", "Slam")

        add("door.property.animation_speed", "Animation speed")

        add("door.property.texture", "Texture")
        add("door.property.texture.normal", "Normal")
        add("door.property.texture.connected", "Connected")

        add(HNSItems.WRENCH.get(), "Wrench")
        add(HNSBlocks.GARAGE_DOOR.get(), "Garage Door")
        add("itemGroup.${HNSConstants.MOD_ID}", "Hinge & Slides")
    }
}