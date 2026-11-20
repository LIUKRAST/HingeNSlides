package net.liukrast.hns.registry;

import net.liukrast.hns.HNSConstants;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class HNSCreativeModeTabs {

    private static final DeferredRegister<CreativeModeTab> REGISTER = DeferredRegister.create(BuiltInRegistries.CREATIVE_MODE_TAB, HNSConstants.MOD_ID);

    static {
        REGISTER.register("main_tab", () -> CreativeModeTab.builder()
                .title(Component.translatable("itemGroup." + HNSConstants.MOD_ID))
                .icon(HNSItems.WRENCH.get()::getDefaultInstance)
                .displayItems((pars, out) -> {
                    out.accept(HNSItems.WRENCH);
                    out.accept(HNSBlocks.GARAGE_DOOR);
                })
                .build());
    }

    private HNSCreativeModeTabs() {}

    public static void init(IEventBus bus) {
        REGISTER.register(bus);
    }
}
