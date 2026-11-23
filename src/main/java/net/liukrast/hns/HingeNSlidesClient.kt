package net.liukrast.hns

import net.liukrast.blocknet.screen.BlockNetCategoryIcons.register
import net.liukrast.blocknet.screen.BlockNetScreen
import net.liukrast.blocknet.setting.BlockNetConfigurable
import net.liukrast.hns.registry.HNSBlockEntityTypes
import net.liukrast.hns.world.level.block.DoorBlock
import net.minecraft.client.Minecraft
import net.minecraft.world.ItemInteractionResult
import net.minecraft.world.Nameable
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.IEventBus
import net.neoforged.fml.common.Mod
import net.neoforged.neoforge.common.NeoForge
import net.neoforged.neoforge.event.entity.player.UseItemOnBlockEvent
import net.swzo.brass.ui.kit.media.BrassIcons.IMAGE
import net.swzo.brass.ui.kit.media.BrassIcons.LOCK
import net.swzo.brass.ui.kit.media.BrassIcons.WRENCH

@Mod(value = HNSConstants.MOD_ID, dist = [Dist.CLIENT])
class HingeNSlidesClient(bus: IEventBus) {
    init {
        bus.addListener(HNSBlockEntityTypes::registerRenderers)
        register("behaviour", WRENCH)
        register("security", LOCK)
        register("visual", IMAGE)
        NeoForge.EVENT_BUS.addListener(this::uteItemOnBlock)
    }

    private fun uteItemOnBlock(event: UseItemOnBlockEvent) {
        if (event.usePhase != UseItemOnBlockEvent.UsePhase.BLOCK) return
        val level = event.level
        val player = event.player ?: return

        val pos = event.pos
        val state = level.getBlockState(pos)
        val block = state.block as? DoorBlock ?: return

        val ctrl = block.getControllerPos(pos, state)
        if (ctrl == pos) return

        val be = level.getBlockEntity(ctrl) as? BlockNetConfigurable ?: return
        if (!player.isHolding(be::allowsConfigMenu)) return

        val displayName = (be as? Nameable)?.displayName ?: level.getBlockState(ctrl).block.name
        Minecraft.getInstance().setScreen(BlockNetScreen(displayName, ctrl, be))
        event.cancelWithResult(ItemInteractionResult.SUCCESS)
    }
}