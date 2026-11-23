package net.liukrast.hns.datagen.tags

import net.liukrast.hns.HNSConstants
import net.minecraft.core.HolderLookup
import net.minecraft.data.PackOutput
import net.neoforged.neoforge.common.data.BlockTagsProvider
import net.neoforged.neoforge.common.data.ExistingFileHelper
import java.util.concurrent.CompletableFuture

class HNSBlockTagsProvider(
    output: PackOutput,
    lookupProvider: CompletableFuture<HolderLookup.Provider>,
    existingFileHelper: ExistingFileHelper?
) : BlockTagsProvider(output, lookupProvider, HNSConstants.MOD_ID, existingFileHelper) {
    override fun addTags(provider: HolderLookup.Provider) {

    }
}