package net.liukrast.hns.world.level.block.entity;

import net.liukrast.hns.registry.HNSBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.lwjgl.system.NonnullDefault;

@NonnullDefault
public class DoorBlockEntity extends BlockEntity {

    // Client data
    public boolean lastState = false;
    public long lastStateTime = -1;

    private boolean manual;
    private boolean redstone;

    public DoorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public DoorBlockEntity(BlockPos pos, BlockState state) {
        this(HNSBlockEntityTypes.DOOR.get(), pos, state);
    }

    public void setManual(boolean manual) {
        this.manual = manual;
    }
    public void setRedstone(boolean redstone) {
        this.redstone = redstone;
    }

    public boolean isManual() {
        return manual;
    }
    public boolean isRedstone() {
        return redstone;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putBoolean("Manual", manual);
        tag.putBoolean("Redstone", redstone);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        manual = tag.getBoolean("Manual");
        redstone = tag.getBoolean("Redstone");
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
