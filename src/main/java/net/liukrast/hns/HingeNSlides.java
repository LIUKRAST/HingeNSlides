package net.liukrast.hns;

import net.liukrast.hns.datagen.HNSItemModelProvider;
import net.liukrast.hns.datagen.HNSLanguageProvider;
import net.liukrast.hns.registry.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

@Mod(HNSConstants.MOD_ID)
public class HingeNSlides {

    public HingeNSlides(IEventBus bus) {
        HNSBlocks.init(bus);
        HNSBlockEntityTypes.init(bus);
        HNSItems.init(bus);
        HNSCreativeModeTabs.init(bus);
        bus.register(this);
        bus.addListener(HNSNetworking::register);
    }

    @SubscribeEvent
    private void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        ExistingFileHelper helper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> provider = event.getLookupProvider();

        generator.addProvider(event.includeClient(), new HNSItemModelProvider(output, helper));
        generator.addProvider(event.includeClient(), new HNSLanguageProvider(output));
    }
}
