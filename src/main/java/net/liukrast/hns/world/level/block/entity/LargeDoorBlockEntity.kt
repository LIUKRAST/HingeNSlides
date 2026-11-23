package net.liukrast.hns.world.level.block.entity

import net.liukrast.blocknet.setting.FloatSetting
import net.liukrast.hns.registry.HNSBlockEntityTypes
import net.liukrast.hns.world.level.block.LargeDoorBlock
import net.minecraft.core.BlockPos
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState

class LargeDoorBlockEntity(
    type: BlockEntityType<*>,
    pos: BlockPos,
    blockState: BlockState,
    manual: Boolean, redstone: Boolean,
) : DoorBlockEntity(type, pos, blockState, manual, redstone, 1f) {
    constructor(
        pos: BlockPos,
        blockState: BlockState,
        manual: Boolean = true
    ) : this(if(manual) HNSBlockEntityTypes.LARGE_WOODEN_DOOR.get() else HNSBlockEntityTypes.LARGE_METAL_DOOR.get(), pos, blockState, manual, true)

    val hinge by data.setting("hinge", 90f) { g, s -> FloatSetting("visual", 0f, 180f, 1f, g, s) }

    val texture: ResourceLocation
        get() = (blockState.block as? LargeDoorBlock)?.texture ?: ResourceLocation.withDefaultNamespace("missingno")
}