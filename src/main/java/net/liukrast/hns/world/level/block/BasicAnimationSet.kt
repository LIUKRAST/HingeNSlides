package net.liukrast.hns.world.level.block

import it.unimi.dsi.fastutil.floats.Float2FloatFunction
import net.liukrast.blocknet.setting.LabeledEnum
import net.liukrast.hns.util.AMth
import net.minecraft.network.chat.Component

enum class BasicAnimationSet(
    private val curve: (Float) -> Float
) : Float2FloatFunction, LabeledEnum {
    NORMAL({ key -> AMth.sinIO(key) }),
    VANILLA({ key -> if (key > 0.001f) 1f else 0f }),
    SLAM({ key -> AMth.bouI(AMth.polI(key, 3), 0.1f, 3) });

    override fun get(key: Float): Float = curve(key)

    override fun getSerializedName(): String = name.lowercase()

    override val label: Component
        get() = Component.translatable("blocknet.setting.animation.$serializedName")
}