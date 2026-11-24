package net.liukrast.hns.world.level.block.entity

import net.liukrast.blocknet.setting.*
import net.liukrast.hns.world.level.block.BasicAnimationSet
import net.liukrast.hns.world.level.block.entity.setting.CoOwnersSetting
import net.liukrast.hns.world.level.block.entity.setting.KeySetting
import net.liukrast.hns.world.level.block.entity.setting.OwnerSetting
import net.liukrast.hns.world.level.block.entity.setting.RedstoneSetting
import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState

open class DoorBlockEntity(
    type: BlockEntityType<*>,
    pos: BlockPos,
    blockState: BlockState,
    manual: Boolean,
    redstone: Boolean,
    speed: Float
) : BlockEntity(type, pos, blockState), BlockNetConfigurable {

    var lastState = false
    var lastStateTime = -1L

    protected val data = BlockNetData()
    val manual by data.setting("manual", manual) { g, s  -> BooleanSetting("behaviour", g, s) }
    val redstone by data.setting("redstone", redstone) { g, s  -> RedstoneSetting("behaviour", g, s) }

    val key by data.setting("key", KeySetting.NONE) { g, s -> KeySetting("security", g, s) }
    val owner by data.setting("owner", OwnerSetting.NONE) { g, s -> OwnerSetting("security", g, s) }
    val coOwners by data.setting("co_owners", CoOwnersSetting.NONE) { g, s -> CoOwnersSetting("security", g, s) { p -> owner == p.uuid || p.hasPermissions(2) } }

    val animationDuration by data.setting("animation_duration", speed) { g, s -> FloatSetting("visual", 0f, 10f, 0.25f, g, s) }

    val animation by data.setting("animation", BasicAnimationSet.NORMAL) { g, s -> EnumSetting("visual", BasicAnimationSet::class, g, s) }


    override fun getSettings(): Map<String, BlockNetSetting<*>> = data.settings()

    override fun onSettingsChanged() {
        setChanged()
        val level = level ?: return
        if(!level.isClientSide)
            level.sendBlockUpdated(blockPos, blockState, blockState, Block.UPDATE_CLIENTS)
    }

    override fun canConfigure(player: Player): Boolean =
        owner == OwnerSetting.NONE
                || owner == player.uuid
                || player.uuid in coOwners
                || player.hasPermissions(2)

    override fun getUpdateTag(registries: HolderLookup.Provider): CompoundTag = saveWithoutMetadata(registries)

    override fun getUpdatePacket(): ClientboundBlockEntityDataPacket = ClientboundBlockEntityDataPacket.create(this)

    override fun saveAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.saveAdditional(tag, registries)
        data.save(tag)
    }

    override fun loadAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.loadAdditional(tag, registries)
        data.load(tag)
    }
}