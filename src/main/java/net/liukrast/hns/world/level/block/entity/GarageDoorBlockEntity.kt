package net.liukrast.hns.world.level.block.entity

import net.liukrast.blocknet.setting.ColorSetting
import net.liukrast.blocknet.setting.EnumSetting
import net.liukrast.blocknet.setting.LabeledEnum
import net.liukrast.hns.registry.HNSBlockEntityTypes
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.world.level.block.state.BlockState

class GarageDoorBlockEntity(
    pos: BlockPos,
    blockState: BlockState
) : DoorBlockEntity(HNSBlockEntityTypes.GARAGE_DOOR.get(), pos, blockState, false, true, 1f) {
    val texture by data.setting("texture", GarageDoorTexture.NORMAL) { g, s -> EnumSetting("visual", GarageDoorTexture::class, g, s) }
    val color by data.setting("color", 0xFFFFFF) { g,s -> ColorSetting("visual", g, s) }
}

enum class GarageDoorTexture : LabeledEnum {
    NORMAL, CONNECTED;

    override fun getSerializedName(): String = name.lowercase()

    override val label: Component
        get() = Component.translatable("blocknet.setting.texture.$serializedName")
}