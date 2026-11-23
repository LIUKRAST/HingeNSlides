package net.liukrast.hns.world.level.block;

import net.liukrast.hns.registry.HNSItems;
import net.liukrast.hns.world.level.block.entity.DoorBlockEntity;
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

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;
import java.util.function.Function;

@NonnullDefault
public abstract class DoorBlock<T extends DoorBlockEntity> extends AbstractFacingMultipartBlock implements EntityBlock, LiquidBlockContainer {
    public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    protected final DoorProperty<T, Boolean> manual;
    protected final DoorProperty<T, Boolean> redstone;

    public DoorBlock(Properties properties, boolean manual, boolean redstone) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(OPEN, false).setValue(POWERED, false));
        this.manual = createProperty("manual", manual, DoorBlockEntity::isManual, DoorBlockEntity::setManual);
        this.redstone = createProperty("redstone", redstone, DoorBlockEntity::isRedstone, DoorBlockEntity::setRedstone);
    }

    public <V> DoorProperty<T, V> createProperty(String key, V defaultValue, Function<T, V> get, BiConsumer<T, V> set) {
        return createProperty(key, DoorProperty.GENERAL, defaultValue, get, set);
    }

    public <V> DoorProperty<T, V> createProperty(String key, String category, V defaultValue, Function<T, V> get, BiConsumer<T, V> set) {
        return new DoorProperty<>(key, category, getBlockEntityClass(), defaultValue, get, set, null);
    }

    public <V extends Number> DoorProperty<T, V> createProperty(String key, String category, V defaultValue,
                                                                float min, float max, float step, Function<T, V> get, BiConsumer<T, V> set) {
        return new DoorProperty<>(key, category, getBlockEntityClass(), defaultValue, get, set,
                new DoorProperty.Range(min, max, step));
    }

    public Map<String, DoorProperty.Range> fetchRanges() {
        Map<String, DoorProperty.Range> ranges = new LinkedHashMap<>();
        for (DoorProperty<T, ?> p : getDoorProperties()) {
            if (p.isConfigurable() && p.range() != null) ranges.put(p.key(), p.range());
        }
        return ranges;
    }

    public Map<String, Object> fetchData(Level level, BlockPos pos) {
        BlockPos ctrl = getControllerPos(pos, level.getBlockState(pos));
        Map<String, Object> data = new LinkedHashMap<>();
        for (DoorProperty<T, ?> p : getDoorProperties()) {
            if (!p.isConfigurable()) continue;
            data.put(p.key(), DoorProperty.serialize(p.fetch(level, ctrl)));
        }
        return data;
    }

    public Map<String, String> fetchCategories() {
        Map<String, String> categories = new LinkedHashMap<>();
        for (DoorProperty<T, ?> p : getDoorProperties()) {
            if (p.isConfigurable()) categories.put(p.key(), p.category());
        }
        return categories;
    }

    public Map<String, List<String>> fetchSchema() {
        Map<String, List<String>> schema = new LinkedHashMap<>();
        for (DoorProperty<T, ?> p : getDoorProperties()) {
            if (p.isEnum() && p.isConfigurable()) schema.put(p.key(), p.options());
        }
        return schema;
    }

    public boolean updateData(Level level, BlockPos pos, Map<String, Object> values) {
        Map<String, DoorProperty<T, ?>> byKey = new HashMap<>();
        for(DoorProperty<T, ?> p : getDoorProperties()) byKey.put(p.key(), p);
        if(!byKey.keySet().containsAll(values.keySet())) return false;

        BlockPos ctrl = getControllerPos(pos, level.getBlockState(pos));
        setControllerActive(level, ctrl, true);
        var be = level.getBlockEntity(ctrl);
        if(!getBlockEntityClass().isInstance(be)) return false;

        boolean ok = true;
        for(var e : values.entrySet()) ok &= byKey.get(e.getKey()).apply(level, ctrl, e.getValue());
        be.setChanged();

        pruneIfDefault(level, ctrl);
        return ok;
    }

    private void pruneIfDefault(Level level, BlockPos ctrl) {
        if(!hasControllerState()) return;
        for (DoorProperty<T, ?> p : getDoorProperties()) {
            if (!p.isDefault(level, ctrl)) return;
        }
        setControllerActive(level, ctrl, false);
        level.removeBlockEntity(ctrl);
    }

    public List<DoorProperty<T, ?>> getDoorProperties() {
        return List.of(manual, redstone);
    }

    @Override
    public boolean hasControllerState() {
        return true;
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

    public abstract Class<T> getBlockEntityClass();
    public abstract T createDoorBE(BlockPos pos, BlockState state);

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        DoorBlockEntity be = null;
        if(isControllerActive(state)) be = createDoorBE(pos, state);
        else if(!hasControllerState() && state.getValue(getPartsProperty()) == 0) be = createDoorBE(pos, state);
        if(be != null) for (DoorProperty<T, ?> p : getDoorProperties()) p.applyDefault(be);
        return be;
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
        BlockPos ctrl = getControllerPos(pos, state);
        if(stack.is(HNSItems.WRENCH)) return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
        if(!manual.fetch(level, ctrl)) return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
        level.gameEvent(player, state.getValue(OPEN) ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, pos);
        level.setBlock(pos, state.cycle(OPEN), 10);
        return ItemInteractionResult.SUCCESS;
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
        super.neighborChanged(state, level, pos, neighborBlock, neighborPos, movedByPiston);
        if(!redstone.fetch(level, getControllerPos(pos, state))) return;
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
