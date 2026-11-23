package net.liukrast.hns

import net.minecraft.core.Registry
import net.minecraft.resources.ResourceLocation

object HNSConstants {
    const val MOD_ID = "hinge_n_slides"
    @JvmStatic
    fun id(path: String): ResourceLocation = ResourceLocation.fromNamespaceAndPath(MOD_ID, path)

    fun <T> getRegistryEntries(registry: Registry<T>): Map<String, T> {
        return registry.entrySet()
            .filter { it.key.location().namespace == MOD_ID }
            .associate { it.key.location().path to it.value }
    }
}