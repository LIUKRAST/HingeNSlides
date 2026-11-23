package net.liukrast.hns

import net.liukrast.blocknet.setting.BlockNetConfigurable
import net.liukrast.hns.datagen.HNSItemModelProvider
import net.liukrast.hns.datagen.HNSLanguageProvider
import net.liukrast.hns.datagen.tags.HNSBlockTagsProvider
import net.liukrast.hns.datagen.tags.HNSItemTagsProvider
import net.liukrast.hns.registry.HNSBlockEntityTypes
import net.liukrast.hns.registry.HNSBlocks
import net.liukrast.hns.registry.HNSCreativeModeTabs
import net.liukrast.hns.registry.HNSDataComponentTypes
import net.liukrast.hns.registry.HNSItems
import net.liukrast.hns.world.level.block.DoorBlock
import net.minecraft.world.ItemInteractionResult
import net.neoforged.bus.api.IEventBus
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.Mod
import net.neoforged.neoforge.common.NeoForge
import net.neoforged.neoforge.data.event.GatherDataEvent
import net.neoforged.neoforge.event.entity.player.UseItemOnBlockEvent

@Mod(HNSConstants.MOD_ID)
class HingeNSlides(bus: IEventBus) {
    init {
        HNSBlocks.init(bus)
        HNSBlockEntityTypes.init(bus)
        HNSItems.init(bus)
        HNSCreativeModeTabs.init(bus)
        HNSDataComponentTypes.init(bus)
        bus.register(this)
        NeoForge.EVENT_BUS.addListener(this::useItemOnBlock)
    }

    @SubscribeEvent
    private fun gatherData(event : GatherDataEvent) {
        val generator = event.generator
        val output = generator.packOutput
        val helper = event.existingFileHelper
        val provider = event.lookupProvider

        generator.addProvider(event.includeClient(), HNSItemModelProvider(output, helper))
        generator.addProvider(event.includeClient(), HNSLanguageProvider(output))

        val blockTags = HNSBlockTagsProvider(output, provider, helper)
        generator.addProvider(event.includeServer(), blockTags)
        generator.addProvider(event.includeServer(), HNSItemTagsProvider(output, provider, blockTags.contentsGetter(), helper))


    }

    private fun useItemOnBlock(event: UseItemOnBlockEvent) {
        if (event.usePhase != UseItemOnBlockEvent.UsePhase.BLOCK) return
        val level = event.level
        if (level.isClientSide) return
        val player = event.player ?: return

        val pos = event.pos
        val state = level.getBlockState(pos)
        val block = state.block as? DoorBlock ?: return

        val ctrl = block.getControllerPos(pos, state)
        if (ctrl == pos) return

        val be = level.getBlockEntity(ctrl) as? BlockNetConfigurable ?: return
        if (!player.isHolding(be::allowsConfigMenu)) return

        event.cancelWithResult(ItemInteractionResult.CONSUME)
    }
}