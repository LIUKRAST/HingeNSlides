package net.liukrast.hns.registry

import net.liukrast.hns.HNSConstants
import net.liukrast.hns.client.model.BlastDoorModel
import net.liukrast.hns.client.model.GarageDoorModel
import net.liukrast.hns.client.model.LargeDoorModel
import net.liukrast.hns.world.level.block.entity.BlastDoorBlockEntity
import net.liukrast.hns.world.level.block.entity.GarageDoorBlockEntity
import net.liukrast.hns.world.level.block.entity.LargeDoorBlockEntity
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.level.block.entity.BlockEntityType
import net.neoforged.api.distmarker.Dist
import net.neoforged.api.distmarker.OnlyIn
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.client.event.EntityRenderersEvent
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredRegister

object HNSBlockEntityTypes {
    private val REGISTER = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, HNSConstants.MOD_ID)

    val GARAGE_DOOR: DeferredHolder<BlockEntityType<*>, BlockEntityType<GarageDoorBlockEntity>> =
        REGISTER.register("garage_door") { ->
            BlockEntityType.Builder.of(
                { pos, state -> GarageDoorBlockEntity(pos, state) },
                HNSBlocks.GARAGE_DOOR.get()
            ).build(null) }

    val BLAST_DOOR: DeferredHolder<BlockEntityType<*>, BlockEntityType<BlastDoorBlockEntity>> =
        REGISTER.register("blast_door") { ->
            BlockEntityType.Builder.of(
                { pos, state -> BlastDoorBlockEntity(pos, state) },
                HNSBlocks.BLAST_DOOR.get()
            ).build(null) }

    val LARGE_WOODEN_DOOR: DeferredHolder<BlockEntityType<*>, BlockEntityType<LargeDoorBlockEntity>> =
        REGISTER.register("large_wooden_door") { ->
            BlockEntityType.Builder.of(
                { pos, state -> LargeDoorBlockEntity(pos, state) },
                HNSBlocks.LARGE_SPRUCE_DOOR.get()
            ).build(null) }

    val LARGE_METAL_DOOR: DeferredHolder<BlockEntityType<*>, BlockEntityType<LargeDoorBlockEntity>> =
        REGISTER.register("large_metal_door") { ->
            BlockEntityType.Builder.of(
                { pos, state -> LargeDoorBlockEntity(pos, state, false) },
                HNSBlocks.LARGE_IRON_DOOR.get()
            ).build(null) }

    @OnlyIn(Dist.CLIENT)
    fun registerRenderers(event: EntityRenderersEvent.RegisterRenderers) {
        event.registerBlockEntityRenderer(GARAGE_DOOR.get(), GarageDoorModel()::renderer)
        event.registerBlockEntityRenderer(BLAST_DOOR.get(), BlastDoorModel()::renderer)
        event.registerBlockEntityRenderer(LARGE_WOODEN_DOOR.get(), LargeDoorModel()::renderer)
        event.registerBlockEntityRenderer(LARGE_METAL_DOOR.get(), LargeDoorModel()::renderer)
    }

    fun init(bus: IEventBus) {
        REGISTER.register(bus)
    }
}