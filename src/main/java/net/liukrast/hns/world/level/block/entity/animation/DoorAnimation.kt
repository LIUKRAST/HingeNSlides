package net.liukrast.hns.world.level.block.entity.animation

import it.unimi.dsi.fastutil.floats.Float2FloatFunction
import net.liukrast.hns.util.AMth
import net.minecraft.util.StringRepresentable

fun interface DoorAnimation : Float2FloatFunction {
}

enum class BasicAnimationSet(private val curve: (Float) -> Float) : DoorAnimation, StringRepresentable {
    NORMAL({AMth.sinIO(it)}),
    VANILLA({ if(it > 0.001f) 1f else 0f}),
    SLAM({ AMth.bouI(AMth.polI(it, 3), 0.1f, 3) });

    override fun get(key: Float): Float = curve(key)
    override fun getSerializedName(): String = name.lowercase()
}