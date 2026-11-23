package net.liukrast.hns.datagen

import net.liukrast.hns.HNSConstants
import net.liukrast.hns.registry.HNSBlocks
import net.liukrast.hns.registry.HNSItems
import net.minecraft.data.PackOutput
import net.neoforged.neoforge.client.model.generators.ItemModelProvider
import net.neoforged.neoforge.common.data.ExistingFileHelper

class HNSItemModelProvider(
    output: PackOutput,
    helper: ExistingFileHelper
) : ItemModelProvider(output, HNSConstants.MOD_ID, helper) {
    override fun registerModels() {
        basicItem(HNSItems.KEY.get())
        basicItem(HNSBlocks.GARAGE_DOOR.asItem())
        basicItem(HNSBlocks.BLAST_DOOR.asItem())
        basicItem(HNSBlocks.LARGE_SPRUCE_DOOR.asItem())
    }
}