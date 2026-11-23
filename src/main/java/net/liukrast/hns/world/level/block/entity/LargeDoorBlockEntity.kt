package net.liukrast.hns.world.level.block.entity;

import net.liukrast.hns.registry.HNSBlockEntityTypes;
import net.liukrast.hns.world.level.block.BasicAnimationSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;
import org.lwjgl.system.NonnullDefault;

@NonnullDefault
public class LargeDoorBlockEntity extends AnimatedDoorBlockEntity<BasicAnimationSet> {
    private float hingeAngle;

    public LargeDoorBlockEntity(BlockPos pos, BlockState state) {
        super(HNSBlockEntityTypes.LARGE_DOOR.get(), pos, state, BasicAnimationSet.NORMAL);
    }

    public float getHingeAngle() {
        return hingeAngle;
    }

    public void setHingeAngle(float hingeAngle) {
        this.hingeAngle = hingeAngle;
        flashClient();
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putFloat("HingeAngle", hingeAngle);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        hingeAngle = tag.getFloat("HingeAngle");
    }
}
