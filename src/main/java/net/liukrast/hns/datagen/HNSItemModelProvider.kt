package net.liukrast.hns.datagen;

import net.liukrast.hns.HNSConstants;
import net.liukrast.hns.registry.HNSBlocks;
import net.liukrast.hns.registry.HNSItems;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class HNSItemModelProvider extends ItemModelProvider {
    public HNSItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, HNSConstants.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        handheldItem(HNSItems.WRENCH.get());
        getBuilder(BuiltInRegistries.ITEM.getKey(HNSBlocks.GARAGE_DOOR.asItem()).toString())
                .parent(new ModelFile.UncheckedModelFile("item/generated"))
                .texture("layer0", ResourceLocation.fromNamespaceAndPath(HNSConstants.MOD_ID, "block/garage_door_particle"));
    }
}
