package net.liukrast.hns.world.level.block;

import net.liukrast.multipart.block.AbstractFacingMultipartBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.system.NonnullDefault;

import java.util.concurrent.atomic.AtomicBoolean;

@NonnullDefault
public abstract class DoorBlock extends AbstractFacingMultipartBlock implements EntityBlock, LiquidBlockContainer {
    public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    private final boolean manual;
    private final boolean redstone;

    public DoorBlock(Properties properties, boolean manual, boolean redstone) {
        super(properties);
        this.manual = manual;
        this.redstone = redstone;
        registerDefaultState(defaultBlockState().setValue(OPEN, false).setValue(POWERED, false));
    }

    //region Block Data
    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(OPEN) ? Shapes.empty() : super.getCollisionShape(state, level, pos, context);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(OPEN, POWERED);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return null;
    }

    @Override
    public boolean canPlaceLiquid(@Nullable Player player, BlockGetter level, BlockPos pos, BlockState state, Fluid fluid) {
        return false;
    }

    @Override
    public boolean placeLiquid(LevelAccessor level, BlockPos pos, BlockState state, FluidState fluidState) {
        return false;
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
        return switch (pathComputationType) {
            case LAND,AIR -> state.getValue(OPEN);
            case WATER -> false;
        };
    }

    //endregion

    //region Behaviour
    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if(!manual) return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
        level.gameEvent(player, state.getValue(OPEN) ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, pos);
        level.setBlock(pos, state.cycle(OPEN), 10);
        return ItemInteractionResult.SUCCESS;
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
        super.neighborChanged(state, level, pos, neighborBlock, neighborPos, movedByPiston);
        if(!redstone) return;
        AtomicBoolean flag = new AtomicBoolean(false);
        forEachElement(pos, state, pos1 -> flag.set(flag.get() | level.hasNeighborSignal(pos1)));
        if(this == neighborBlock || flag.get() == state.getValue(POWERED)) return;
        if(flag.get() != state.getValue(OPEN)) {
            level.gameEvent(null, flag.get() ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, pos);
        }
        level.setBlock(pos, state.setValue(POWERED, flag.get()).setValue(OPEN, flag.get()), 2);
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if(!neighborState.is(this)) return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
        return neighborState.setValue(FACING, state.getValue(FACING)).setValue(getPartsProperty(), state.getValue(getPartsProperty()));
    }

    //endregion
}
