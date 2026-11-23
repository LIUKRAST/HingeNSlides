package net.liukrast.hns.datagen.tags

import net.liukrast.hns.HNSConstants
import net.liukrast.hns.registry.HNSItems
import net.minecraft.core.HolderLookup
import net.minecraft.data.PackOutput
import net.minecraft.data.tags.ItemTagsProvider
import net.minecraft.world.level.block.Block
import net.neoforged.neoforge.common.Tags
import net.neoforged.neoforge.common.data.ExistingFileHelper
import java.util.concurrent.CompletableFuture

class HNSItemTagsProvider(
    output: PackOutput,
    lookup: CompletableFuture<HolderLookup.Provider>,
    block: CompletableFuture<TagLookup<Block>>,
    helper: ExistingFileHelper
) : ItemTagsProvider(output, lookup, block, HNSConstants.MOD_ID, helper) {
    override fun addTags(provider: HolderLookup.Provider) {
    }
}