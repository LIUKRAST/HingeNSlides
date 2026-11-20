package net.liukrast.hns.world.level.block.entity;

import net.liukrast.hns.registry.HNSBlockEntityTypes;
import net.liukrast.hns.world.level.block.BasicAnimationSet;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class BlastDoorBlockEntity extends AnimatedDoorBlockEntity<BasicAnimationSet> {
    public BlastDoorBlockEntity(BlockPos pos, BlockState state) {
        super(HNSBlockEntityTypes.BLAST_DOOR.get(), pos, state, BasicAnimationSet.NORMAL);
    }
}
