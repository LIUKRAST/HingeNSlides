package net.liukrast.hns.registry;

import net.liukrast.hns.HNSConstants;
import net.liukrast.hns.world.level.item.WrenchItem;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class HNSItems {
    private static final DeferredRegister.Items REGISTER = DeferredRegister.createItems(HNSConstants.MOD_ID);

    public static final DeferredItem<Item> WRENCH = REGISTER.register("wrench", () -> new WrenchItem(new Item.Properties()));

    private HNSItems() {}

    public static void init(IEventBus bus) {
        REGISTER.register(bus);
    }
}
