package net.liukrast.hns.world.level.block.entity

import net.liukrast.blocknet.setting.ColorSetting
import net.liukrast.hns.registry.HNSBlockEntityTypes
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.state.BlockState

class BlastDoorBlockEntity(
    pos: BlockPos,
    blockState: BlockState
) : DoorBlockEntity(HNSBlockEntityTypes.BLAST_DOOR.get(), pos, blockState, false, true, 5f) {
    val color by data.setting("color", 0x2A3047) { g, s -> ColorSetting("visual", g, s) }


}