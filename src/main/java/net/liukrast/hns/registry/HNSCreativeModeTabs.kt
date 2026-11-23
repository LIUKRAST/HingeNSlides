package net.liukrast.hns.registry

import net.liukrast.blocknet.registry.BlockNetItems
import net.liukrast.hns.HNSConstants
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.Component
import net.minecraft.world.item.CreativeModeTab
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredRegister

object HNSCreativeModeTabs {
    private val REGISTER = DeferredRegister.create(BuiltInRegistries.CREATIVE_MODE_TAB, HNSConstants.MOD_ID)

    init {
        REGISTER.register("main_tab") { -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.${HNSConstants.MOD_ID}"))
            .icon(HNSBlocks.LARGE_SPRUCE_DOOR.asItem()::getDefaultInstance)
            .displayItems { _, out ->
                out.accept(HNSBlocks.LARGE_SPRUCE_DOOR)
                out.accept(HNSBlocks.LARGE_IRON_DOOR)

                out.accept(HNSBlocks.GARAGE_DOOR)
                out.accept(HNSBlocks.BLAST_DOOR)
                out.accept(HNSItems.KEY)
                out.accept(BlockNetItems.WRENCH)
            }
            .build() }
    }

    fun init(bus: IEventBus) {
        REGISTER.register(bus)
    }
}