package net.liukrast.hns.world.level.block

import net.liukrast.hns.world.level.block.entity.LargeDoorBlockEntity
import net.liukrast.hns.world.level.block.shape.HRShape
import net.minecraft.core.BlockPos
import net.minecraft.resources.ResourceLocation
import net.minecraft.sounds.SoundEvent
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape

class LargeDoorBlock(
    override val openSound: SoundEvent,
    override val closeSound: SoundEvent,
    val texture: ResourceLocation,
    val manual: Boolean,
    properties: Properties
) : DoorBlock(properties) {
    companion object {
        private val SHAPE = HRShape(box(0.0, 0.0, 0.0, 16.0, 16.0, 3.0))
    }

    init {
        registerDefaultState(defaultBlockState().setValue(partsProperty, 3))
    }

    override fun defineParts(builder: Builder) {
        builder.apply { repeat(3) { x -> repeat(3) { y -> define(x,y,0) } } }
    }

    override fun newBlockEntity(
        pos: BlockPos,
        state: BlockState
    ): BlockEntity? = if(isController(state)) LargeDoorBlockEntity(pos, state, manual) else null

    override fun getShape(state: BlockState, level: BlockGetter, pos: BlockPos, context: CollisionContext): VoxelShape = SHAPE[state.getValue(FACING)]
}