package net.liukrast.hns.world.level.item

import net.liukrast.hns.HNSConstants
import net.liukrast.hns.registry.HNSDataComponentTypes
import net.liukrast.hns.world.level.block.DoorBlock
import net.liukrast.hns.world.level.block.entity.DoorBlockEntity
import net.liukrast.hns.world.level.block.entity.setting.KeySetting
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.Level
import java.util.Collections
import java.util.UUID

open class KeyItem(protected val maxKeys: Int, properties: Properties) : Item(properties) {
    sealed interface KeyConfigureResult {
        data object Success : KeyConfigureResult
        data class Failure(val message: String) : KeyConfigureResult
    }

    override fun useOn(ctx: UseOnContext): InteractionResult {
        val player = ctx.player
        if (player == null || !player.isShiftKeyDown) return super.useOn(ctx)

        val level = ctx.level
        val state = level.getBlockState(ctx.clickedPos)
        val block = state.block as? DoorBlock ?: return super.useOn(ctx)
        val be = level.getBlockEntity(block.getControllerPos(ctx.clickedPos, state)) as? DoorBlockEntity
            ?: return super.useOn(ctx)

        return when (val result = bindKey(be, ctx.itemInHand, level, player)) {
            is KeyConfigureResult.Failure -> {
                if (!level.isClientSide)
                    player.displayClientMessage(Component.translatable("message.${HNSConstants.MOD_ID}.key.${result.message}"), true)
                InteractionResult.FAIL
            }
            KeyConfigureResult.Success -> InteractionResult.sidedSuccess(level.isClientSide)
        }
    }

    fun bindKey(be : DoorBlockEntity, stack: ItemStack, level: Level, player: Player) : KeyConfigureResult {
        var id = be.key
        val data = HashSet(stack.getOrDefault(HNSDataComponentTypes.KEY_IDS, Collections.emptySet()))
        if(data.contains(id))
            return KeyConfigureResult.Failure("already_bound")
        if(data.size >= maxKeys) return KeyConfigureResult.Failure("max_reached")
        if(id == KeySetting.NONE && !level.isClientSide) {
            id = UUID.randomUUID()
            be.setSettings(player, mapOf(
                "key" to id
            ))
        }
        if(level.isClientSide) return KeyConfigureResult.Success
        data.add(id)
        stack.set(HNSDataComponentTypes.KEY_IDS, data)
        return KeyConfigureResult.Success
    }

    override fun isFoil(stack: ItemStack): Boolean {
        val keys = stack.get(HNSDataComponentTypes.KEY_IDS)
        return !keys.isNullOrEmpty() || super.isFoil(stack)
    }
}