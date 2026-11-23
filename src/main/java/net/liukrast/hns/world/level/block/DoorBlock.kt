package net.liukrast.hns.world.level.block

import net.liukrast.hns.HNSConstants
import net.liukrast.hns.registry.HNSDataComponentTypes
import net.liukrast.hns.world.level.block.entity.DoorBlockEntity
import net.liukrast.hns.world.level.block.entity.setting.KeySetting
import net.liukrast.multipart.block.AbstractFacingMultipartBlock
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.network.chat.Component
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundSource
import net.minecraft.world.InteractionHand
import net.minecraft.world.ItemInteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.LiquidBlockContainer
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.BooleanProperty
import net.minecraft.world.level.gameevent.GameEvent
import net.minecraft.world.level.material.Fluid
import net.minecraft.world.level.material.FluidState
import net.minecraft.world.level.pathfinder.PathComputationType
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape
import java.util.*

abstract class DoorBlock(properties: Properties) : AbstractFacingMultipartBlock(properties), EntityBlock, LiquidBlockContainer {

    companion object {
        val OPEN: BooleanProperty = BlockStateProperties.OPEN
        val POWERED: BooleanProperty = BlockStateProperties.POWERED
    }

    init {
        registerDefaultState(defaultBlockState().setValue(OPEN, false).setValue(POWERED, false))
    }

    protected abstract val openSound: SoundEvent
    protected abstract val closeSound: SoundEvent

    protected open fun playSound(player: Player?, level: Level, pos: BlockPos, open: Boolean) {
        level.playSound(
            player, pos,
            if (open) openSound else closeSound,
            SoundSource.BLOCKS,
            1.0f,
            level.getRandom().nextFloat() * 0.1f + 0.9f
        )
    }

    override fun useItemOn(
        stack: ItemStack,
        state: BlockState,
        level: Level,
        pos: BlockPos,
        player: Player,
        hand: InteractionHand,
        hitResult: BlockHitResult
    ): ItemInteractionResult {
        val ctrl = getControllerPos(pos, state)
        val blockEntity = level.getBlockEntity(ctrl)
        if(blockEntity !is DoorBlockEntity) return super.useItemOn(stack, state, level, pos, player, hand, hitResult)
        if(!blockEntity.manual) return super.useItemOn(stack, state, level, pos, player, hand, hitResult)
        if(!useKey(blockEntity, stack)) {
            if(!level.isClientSide)
                player.displayClientMessage(Component.translatable("message.${HNSConstants.MOD_ID}.door.locked"), true)
            return ItemInteractionResult.FAIL
        }

        val open = !state.getValue(OPEN)
        level.setBlock(pos, state.setValue(OPEN, open), UPDATE_ALL)
        playSound(player, level, pos, open)
        level.gameEvent(player, if (open) GameEvent.BLOCK_OPEN else GameEvent.BLOCK_CLOSE, pos)
        return ItemInteractionResult.sidedSuccess(level.isClientSide)
    }

    fun useKey(be: DoorBlockEntity, stack: ItemStack) : Boolean {
        val id = be.key
        if(id == KeySetting.NONE) return true
        val ids = stack.getOrDefault(HNSDataComponentTypes.KEY_IDS, Collections.emptySet())
        return ids.contains(id)
    }

    override fun neighborChanged(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        neighborBlock: Block,
        neighborPos: BlockPos,
        movedByPiston: Boolean
    ) {
        super.neighborChanged(state, level, pos, neighborBlock, neighborPos, movedByPiston)
        if (this == neighborBlock) return
        val ctrl = getControllerPos(pos, state)
        val be = level.getBlockEntity(ctrl) as? DoorBlockEntity ?: return
        if (!be.redstone) return

        var powered = false
        forEachElement(pos, state) { p -> powered = powered || level.hasNeighborSignal(p) }
        if (powered == state.getValue(POWERED)) return

        if (powered != state.getValue(OPEN) && pos == ctrl) {
            playSound(null, level, pos, powered)
            level.gameEvent(null, if (powered) GameEvent.BLOCK_OPEN else GameEvent.BLOCK_CLOSE, pos)
        }
        level.setBlock(pos, state.setValue(POWERED, powered).setValue(OPEN, powered), UPDATE_CLIENTS)
    }

    override fun updateShape(
        state: BlockState,
        direction: Direction,
        neighborState: BlockState,
        level: LevelAccessor,
        pos: BlockPos,
        neighborPos: BlockPos
    ): BlockState {
        if (!neighborState.`is`(this)) return super.updateShape(state, direction, neighborState, level, pos, neighborPos)
        if (getControllerPos(pos, state) != getControllerPos(neighborPos, neighborState)) return state
        return state
            .setValue(OPEN, neighborState.getValue(OPEN))
            .setValue(POWERED, neighborState.getValue(POWERED))
    }

    override fun getCollisionShape(
        state: BlockState,
        level: BlockGetter,
        pos: BlockPos,
        context: CollisionContext
    ): VoxelShape = if(state.getValue(OPEN)) Shapes.empty() else super.getCollisionShape(state, level, pos, context)

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block?, BlockState?>) {
        super.createBlockStateDefinition(builder)
        builder.add(OPEN, POWERED)
    }

    override fun canPlaceLiquid(player: Player?, level: BlockGetter, pos: BlockPos, state: BlockState, fluid: Fluid): Boolean = false

    override fun placeLiquid(level: LevelAccessor, pos: BlockPos, state: BlockState, fluidState: FluidState): Boolean = false

    override fun isPathfindable(state: BlockState, pathComputationType: PathComputationType): Boolean {
        return when(pathComputationType) {
            PathComputationType.LAND, PathComputationType.AIR -> state.getValue(OPEN)
            PathComputationType.WATER -> false
        }
    }


}