package net.liukrast.hns.util

import net.minecraft.util.Mth
import kotlin.math.pow

object AMth {

    @JvmStatic
    fun sec(x: Float, from: Float, to: Float): Float {
        if (x <= from) return 0f
        if (x >= to) return 1f
        return (x - from) / (to - from)
    }

    @JvmStatic
    fun sinIO(x: Float): Float = (1f - Mth.cos(x * Mth.PI)) / 2f

    @JvmStatic
    fun sinI(x: Float): Float = 1f - Mth.cos(x * Mth.PI / 2f)

    @JvmStatic
    fun sinO(x: Float): Float = Mth.sin(x * Mth.PI / 2f)

    @JvmStatic
    fun polIO(x: Float, p: Int): Float {
        if (x < 0.5f) return (2.0.pow(p - 1) * x.toDouble().pow(p)).toFloat()
        return (1.0 - (-2.0 * x + 2.0).pow(p) / 2.0).toFloat()
    }

    @JvmStatic
    fun polI(x: Float, p: Int): Float = x.toDouble().pow(p).toFloat()

    @JvmStatic
    fun polO(x: Float, p: Int): Float = 1f - (x - 1f).toDouble().pow(p).toFloat()

    @JvmStatic
    fun bouO(t: Float, bounciness: Float, bounces: Int): Float {
        val tc = t.coerceIn(0f, 1f)
        if (tc >= 1f) return 1f

        val e = bounciness.coerceIn(0f, 0.99f)
        if (e == 0f || bounces <= 0) return tc * tc

        var total = 1f
        var w = e
        repeat(bounces) {
            total += 2f * w
            w *= e
        }

        var s = tc * total

        if (s < 1f) return s * s
        s -= 1f

        w = e
        repeat(bounces) {
            val duration = 2f * w
            if (s < duration) {
                val x = s / w - 1f
                return 1f - w * w * (1f - x * x)
            }
            s -= duration
            w *= e
        }
        return 1f
    }

    @JvmStatic
    fun bouI(t: Float, bounciness: Float, bounces: Int): Float =
        1f - bouO(1f - t, bounciness, bounces)

    @JvmStatic
    fun bouIO(t: Float, bounciness: Float, bounces: Int): Float {
        val tc = t.coerceIn(0f, 1f)
        return if (tc < 0.5f)
            bouI(tc * 2f, bounciness, bounces) * 0.5f
        else
            bouO(tc * 2f - 1f, bounciness, bounces) * 0.5f + 0.5f
    }
}