package net.liukrast.hns.world.level.block;

import net.liukrast.hns.world.level.block.entity.BlastDoorBlockEntity;
import net.liukrast.hns.world.level.block.shape.HRShape;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.lwjgl.system.NonnullDefault;

@NonnullDefault
public class BlastDoorBlock extends AnimatedDoorBlock<BlastDoorBlockEntity, BasicAnimationSet> {
    private static final HRShape SHAPE = new HRShape(box(0,0,0,16,16,7));

    public BlastDoorBlock(Properties properties) {
        super(properties, false, true, BasicAnimationSet.NORMAL, 5);
    }

    @Override
    public Class<BlastDoorBlockEntity> getBlockEntityClass() {
        return BlastDoorBlockEntity.class;
    }

    @Override
    public BlastDoorBlockEntity createDoorBE(BlockPos pos, BlockState state) {
        return new BlastDoorBlockEntity(pos, state);
    }

    @Override
    public void defineParts(Builder builder) {
        for(int x = 0; x < 6; x++) {
            for(int y = 0; y < 3; y++) {
                builder.define(x, y, 0);
            }
        }
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE.get(state.getValue(FACING));
    }
}
