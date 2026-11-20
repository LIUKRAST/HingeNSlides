package net.liukrast.hns.world.level.block.entity;

import it.unimi.dsi.fastutil.floats.Float2FloatFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.lwjgl.system.NonnullDefault;

@NonnullDefault
public class AnimatedDoorBlockEntity<A extends Enum<A> & Float2FloatFunction> extends DoorBlockEntity {
    private final A defaultAnimation;
    private A animation;
    private float speed;

    public AnimatedDoorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, A defaultAnimation) {
        super(type, pos, state);
        this.defaultAnimation = defaultAnimation;
        this.animation = defaultAnimation;
    }

    public A getAnimation() {
        return animation;
    }

    public float getSpeed() {
        return speed;
    }

    public void setAnimation(A animation) {
        this.animation = animation;
        flashClient();
    }

    public void setSpeed(float speed) {
        this.speed = speed;
        flashClient();
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putString("Animation", animation.name());
        tag.putFloat("Speed", speed);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        A loaded = defaultAnimation;
        if (tag.contains("Animation")) {
            try {
                loaded = Enum.valueOf(defaultAnimation.getDeclaringClass(), tag.getString("Animation"));
            } catch (IllegalArgumentException ignored) {}
        }
        animation = loaded;
        speed = tag.getFloat("Speed");
    }
}
