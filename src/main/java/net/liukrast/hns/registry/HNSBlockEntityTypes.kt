package net.liukrast.hns.registry;

import net.liukrast.hns.HNSConstants;
import net.liukrast.hns.client.model.BlastDoorModel;
import net.liukrast.hns.client.model.GarageDoorModel;
import net.liukrast.hns.client.model.LargeDoorModel;
import net.liukrast.hns.world.level.block.entity.BlastDoorBlockEntity;
import net.liukrast.hns.world.level.block.entity.DoorBlockEntity;
import net.liukrast.hns.world.level.block.entity.GarageDoorBlockEntity;
import net.liukrast.hns.world.level.block.entity.LargeDoorBlockEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class HNSBlockEntityTypes {
    private static final DeferredRegister<BlockEntityType<?>> REGISTER = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, HNSConstants.MOD_ID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<DoorBlockEntity>> DOOR = REGISTER.register("door", () -> BlockEntityType.Builder.of(DoorBlockEntity::new).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<GarageDoorBlockEntity>> GARAGE_DOOR = REGISTER.register("garage_door", () -> BlockEntityType.Builder.of(GarageDoorBlockEntity::new, HNSBlocks.GARAGE_DOOR.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BlastDoorBlockEntity>> BLAST_DOOR = REGISTER.register("blast_door", () -> BlockEntityType.Builder.of(BlastDoorBlockEntity::new, HNSBlocks.BLAST_DOOR.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<LargeDoorBlockEntity>> LARGE_DOOR = REGISTER.register("large_door", () -> BlockEntityType.Builder.of(LargeDoorBlockEntity::new, HNSBlocks.LARGE_SPRUCE_DOOR.get()).build(null));

    private HNSBlockEntityTypes() {}

    @OnlyIn(Dist.CLIENT)
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(GARAGE_DOOR.get(), new GarageDoorModel()::renderer);
        event.registerBlockEntityRenderer(BLAST_DOOR.get(), new BlastDoorModel()::renderer);
        event.registerBlockEntityRenderer(LARGE_DOOR.get(), new LargeDoorModel()::renderer);
    }

    public static void init(IEventBus bus) {
        REGISTER.register(bus);
    }
}
