package net.liukrast.hns.registry

import net.liukrast.hns.HNSConstants
import net.liukrast.hns.world.level.block.BlastDoorBlock
import net.liukrast.hns.world.level.block.GarageDoorBlock
import net.liukrast.hns.world.level.block.LargeDoorBlock
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockBehaviour
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredBlock
import net.neoforged.neoforge.registries.DeferredRegister

object HNSBlocks {
    private val BLOCKS = DeferredRegister.createBlocks(HNSConstants.MOD_ID)
    private val ITEMS = DeferredRegister.createItems(HNSConstants.MOD_ID)

    val GARAGE_DOOR: DeferredBlock<GarageDoorBlock> = BLOCKS.register("garage_door") { -> GarageDoorBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_DOOR)) }
    val BLAST_DOOR: DeferredBlock<BlastDoorBlock> = BLOCKS.register("blast_door") { -> BlastDoorBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_DOOR)) }

    val LARGE_SPRUCE_DOOR: DeferredBlock<LargeDoorBlock> = BLOCKS.register("large_spruce_door") { -> LargeDoorBlock(
        SoundEvents.WOODEN_DOOR_OPEN,
        SoundEvents.WOODEN_DOOR_CLOSE,
        HNSConstants.id("textures/entity/large_spruce_door.png"),
        true,
        BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_PLANKS)
    )}

    val LARGE_IRON_DOOR: DeferredBlock<LargeDoorBlock> = BLOCKS.register("large_iron_door") { -> LargeDoorBlock(
        SoundEvents.IRON_DOOR_OPEN,
        SoundEvents.IRON_DOOR_CLOSE,
        HNSConstants.id("textures/entity/large_iron_door.png"),
        false,
        BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)
    )}

    init {
        ITEMS.register("garage_door") { -> BlockItem(GARAGE_DOOR.get(), Item.Properties()) }
        ITEMS.register("blast_door") { -> BlockItem(BLAST_DOOR.get(), Item.Properties()) }
        ITEMS.register("large_spruce_door") { -> BlockItem(LARGE_SPRUCE_DOOR.get(), Item.Properties()) }
        ITEMS.register("large_iron_door") { -> BlockItem(LARGE_IRON_DOOR.get(), Item.Properties()) }
    }

    fun init(bus: IEventBus) {
        BLOCKS.register(bus)
        ITEMS.register(bus)
    }
}