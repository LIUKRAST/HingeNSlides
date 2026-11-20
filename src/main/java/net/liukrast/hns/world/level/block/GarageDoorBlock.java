package net.liukrast.hns.world.level.block;

import net.liukrast.hns.world.level.block.entity.GarageDoorBlockEntity;
import net.liukrast.hns.world.level.block.shape.HRShape;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.lwjgl.system.NonnullDefault;

import java.util.List;

@NonnullDefault
public class GarageDoorBlock extends AnimatedDoorBlock<GarageDoorBlockEntity, BasicAnimationSet> {

    private static final HRShape SHAPE = new HRShape(box(0,0,0,16,16,4));

    protected final DoorProperty<GarageDoorBlockEntity, GarageDoorTexture> texture;

    public GarageDoorBlock(Properties properties) {
        super(properties, false, true, BasicAnimationSet.NORMAL, 1);
        this.texture = createProperty("texture", "visual", GarageDoorTexture.NORMAL,
                GarageDoorBlockEntity::getTexture, GarageDoorBlockEntity::setTexture);
    }

    @Override
    public List<DoorProperty<GarageDoorBlockEntity, ?>> getDoorProperties() {
        var li = super.getDoorProperties();
        li.add(texture);
        return li;
    }

    @Override
    public void defineParts(Builder builder) {
        for(int x = 0; x < 5; x++) {
            for(int y = 0; y < 3; y++) {
                builder.define(x, y, 0);
            }
        }
    }

    @Override
    public Class<GarageDoorBlockEntity> getBlockEntityClass() {
        return GarageDoorBlockEntity.class;
    }

    @Override
    public GarageDoorBlockEntity createDoorBE(BlockPos pos, BlockState state) {
        return new GarageDoorBlockEntity(pos, state);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE.get(state.getValue(FACING));
    }
}
