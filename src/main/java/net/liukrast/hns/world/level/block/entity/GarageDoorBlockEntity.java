package net.liukrast.hns.world.level.block.entity;

import net.liukrast.hns.registry.HNSBlockEntityTypes;
import net.liukrast.hns.world.level.block.BasicAnimationSet;
import net.liukrast.hns.world.level.block.GarageDoorTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;
import org.lwjgl.system.NonnullDefault;

@NonnullDefault
public class GarageDoorBlockEntity extends AnimatedDoorBlockEntity<BasicAnimationSet> {
    private GarageDoorTexture texture = GarageDoorTexture.NORMAL;

    public GarageDoorBlockEntity(BlockPos pos, BlockState state) {
        super(HNSBlockEntityTypes.GARAGE_DOOR.get(), pos, state, BasicAnimationSet.NORMAL);
    }

    public GarageDoorTexture getTexture() {
        return texture;
    }

    public void setTexture(GarageDoorTexture texture) {
        this.texture = texture;
        flashClient();
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putString("Texture", texture.name());
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        GarageDoorTexture loaded = GarageDoorTexture.NORMAL;
        if (tag.contains("Texture")) {
            try {
                loaded = Enum.valueOf(GarageDoorTexture.NORMAL.getDeclaringClass(), tag.getString("Texture"));
            } catch (IllegalArgumentException ignored) {}
        }
        texture = loaded;
    }
}
