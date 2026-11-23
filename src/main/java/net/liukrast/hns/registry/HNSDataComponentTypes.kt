package net.liukrast.hns.registry

import net.liukrast.hns.HNSConstants
import net.minecraft.core.UUIDUtil
import net.minecraft.core.component.DataComponentType
import net.minecraft.core.registries.BuiltInRegistries
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredRegister
import java.util.UUID

object HNSDataComponentTypes {
    private val REGISTER = DeferredRegister.create(BuiltInRegistries.DATA_COMPONENT_TYPE, HNSConstants.MOD_ID)

    val KEY_IDS: DeferredHolder<DataComponentType<*>, DataComponentType<Set<UUID>>> = REGISTER.register("key_ids") { -> DataComponentType.builder<Set<UUID>>()
        .persistent(UUIDUtil.CODEC.listOf().xmap({ it.toSet() }, { it.toList() })).build()}

    fun init(bus: IEventBus) = REGISTER.register(bus)
}