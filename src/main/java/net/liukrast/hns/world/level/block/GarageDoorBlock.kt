package net.liukrast.hns.world.level.block

import net.liukrast.hns.world.level.block.entity.GarageDoorBlockEntity
import net.liukrast.hns.world.level.block.shape.HRShape
import net.minecraft.core.BlockPos
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape

class GarageDoorBlock(properties: Properties) : DoorBlock(properties) {
    companion object {
        private val SHAPE = HRShape(box(0.0, 0.0, 0.0, 16.0, 16.0, 4.0))
    }

    init {
        registerDefaultState(defaultBlockState().setValue(partsProperty, 6))
    }

    override fun defineParts(builder: Builder) {
        builder.apply { repeat(5) { x -> repeat(3) { y -> define(x,y,0) } } }
    }

    override fun newBlockEntity(
        pos: BlockPos,
        state: BlockState
    ): BlockEntity? = if(isController(state)) GarageDoorBlockEntity(pos, state) else null

    override fun getShape(state: BlockState, level: BlockGetter, pos: BlockPos, context: CollisionContext): VoxelShape = SHAPE[state.getValue(FACING)]

    override val openSound: SoundEvent get() = SoundEvents.IRON_DOOR_OPEN

    override val closeSound: SoundEvent get() = SoundEvents.IRON_DOOR_CLOSE
}