package net.liukrast.hns.world.level.block.entity;

import net.liukrast.hns.registry.HNSBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class DoorBlockEntity extends BlockEntity {
    public DoorBlockEntity(BlockPos pos, BlockState blockState) {
        super(HNSBlockEntityTypes.DOOR.get(), pos, blockState);
    }
}
