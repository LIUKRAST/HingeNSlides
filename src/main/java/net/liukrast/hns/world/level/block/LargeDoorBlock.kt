package net.liukrast.hns.world.level.block;

import net.liukrast.hns.world.level.block.entity.AnimatedDoorBlockEntity;
import net.liukrast.hns.world.level.block.entity.LargeDoorBlockEntity;
import net.liukrast.hns.world.level.block.shape.HRShape;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.lwjgl.system.NonnullDefault;

import java.util.List;

@NonnullDefault
public class LargeDoorBlock extends AnimatedDoorBlock<LargeDoorBlockEntity, BasicAnimationSet> {
    private static final HRShape SHAPE = new HRShape(box(0,0,0,16,16,3));

    private final ResourceLocation texture;

    protected final DoorProperty<LargeDoorBlockEntity, Float> hingeAngle;

    public LargeDoorBlock(Properties properties, boolean manual, ResourceLocation texture) {
        this(properties, manual, true, texture);
    }

    public LargeDoorBlock(Properties properties, boolean manual, boolean redstone, ResourceLocation texture) {
        super(properties, manual, redstone, BasicAnimationSet.NORMAL, 1);
        this.texture = texture;
        this.hingeAngle = createProperty("hinge_angle", "visual", 90f, 0, 180, 1,
                LargeDoorBlockEntity::getHingeAngle, LargeDoorBlockEntity::setHingeAngle);
    }

    @Override
    public Class<LargeDoorBlockEntity> getBlockEntityClass() {
        return LargeDoorBlockEntity.class;
    }

    @Override
    public LargeDoorBlockEntity createDoorBE(BlockPos pos, BlockState state) {
        return new LargeDoorBlockEntity(pos, state);
    }

    @Override
    public void defineParts(Builder builder) {
        for(int x = 0; x < 3; x++) {
            for(int y = 0; y < 3; y++) {
                builder.define(x, y, 0);
            }
        }
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE.get(state.getValue(FACING));
    }

    public ResourceLocation getTexture(LargeDoorBlockEntity blockEntity) {
        return texture;
    }

    @Override
    public List<DoorProperty<LargeDoorBlockEntity, ?>> getDoorProperties() {
        var li = super.getDoorProperties();
        li.add(hingeAngle);
        return li;
    }
}
