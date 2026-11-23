package net.liukrast.hns.registry

import net.liukrast.hns.HNSConstants
import net.liukrast.hns.world.level.item.KeyItem
import net.minecraft.world.item.Item
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredItem
import net.neoforged.neoforge.registries.DeferredRegister

object HNSItems {
    private val REGISTER = DeferredRegister.createItems(HNSConstants.MOD_ID)

    val KEY: DeferredItem<KeyItem> = REGISTER.register("key") { -> KeyItem(1, Item.Properties()) }

    fun init(bus : IEventBus) = REGISTER.register(bus)
}