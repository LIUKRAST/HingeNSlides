package net.liukrast.hns.world.level.block;

import it.unimi.dsi.fastutil.floats.Float2FloatFunction;
import net.liukrast.hns.world.level.block.entity.AnimatedDoorBlockEntity;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import org.lwjgl.system.NonnullDefault;

import java.util.ArrayList;
import java.util.List;

@NonnullDefault
public abstract class AnimatedDoorBlock<T extends AnimatedDoorBlockEntity<A>, A extends Enum<A> & Float2FloatFunction> extends DoorBlock<T> {
    protected final DoorProperty<T, A> animation;
    protected final DoorProperty<T, Float> speed;

    public AnimatedDoorBlock(Properties properties, boolean manual, boolean redstone, A defaultAnimation, float defaultSpeed) {
        super(properties, manual, redstone);
        this.animation = createProperty("animation", "visual", defaultAnimation,
                AnimatedDoorBlockEntity::getAnimation, AnimatedDoorBlockEntity::setAnimation);
        this.speed = createProperty("animation_speed", "visual", defaultSpeed, 0.25f, 10f, 0.25f,
                AnimatedDoorBlockEntity::getSpeed, AnimatedDoorBlockEntity::setSpeed);
    }

    @Override
    public List<DoorProperty<T, ?>> getDoorProperties() {
        List<DoorProperty<T, ?>> list = new ArrayList<>(super.getDoorProperties());
        list.add(animation);
        list.add(speed);
        return list;
    }

    @Override
    public boolean hasControllerState() { return false; }

    @Override
    protected RenderShape getRenderShape(BlockState state) { return RenderShape.ENTITYBLOCK_ANIMATED; }
}
