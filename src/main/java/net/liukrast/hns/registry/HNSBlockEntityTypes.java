package net.liukrast.hns.registry;

import net.liukrast.hns.HNSConstants;
import net.liukrast.hns.world.level.block.entity.DoorBlockEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class HNSBlockEntityTypes {
    private static final DeferredRegister<BlockEntityType<?>> REGISTER = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, HNSConstants.MOD_ID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<DoorBlockEntity>> DOOR = REGISTER.register("door", () -> BlockEntityType.Builder.of(DoorBlockEntity::new, HNSBlocks.GARAGE_DOOR.get()).build(null));

    private HNSBlockEntityTypes() {}

    public static void init(IEventBus bus) {
        REGISTER.register(bus);
    }
}
