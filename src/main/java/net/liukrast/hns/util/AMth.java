package net.liukrast.hns.util;

import net.minecraft.util.Mth;

public class AMth {
    private AMth() {}

    public static float sec(float x, float from, float to) {
        if(x <= from) return 0;
        if(x >= to) return 1;
        return (x-from)/(to-from);
    }

    public static float sinIO(float x) {
        return (1- Mth.cos(x*Mth.PI))/2;
    }

    public static float sinI(float x) {
        return 1-Mth.cos(x*Mth.PI/2);
    }

    public static float sinO(float x) {
        return Mth.sin(x*Mth.PI/2);
    }

    public static float polIO(float x, int p) {
        if(x < 0.5) return (float) (Math.pow(2, p-1) * Math.pow(x, p));
        return (float) (1- (Math.pow(-2*x + 2, p))/2);
    }

    public static float polI(float x, int p) {
        return (float) Math.pow(x, p);
    }

    public static float polO(float x, int p) {
        return 1 - (float)Math.pow(x-1, p);
    }

    public static float bouO(float t, float bounciness, int bounces) {
        t = Math.clamp(t, 0f, 1f);
        if (t >= 1f) return 1f;

        float e = Math.clamp(bounciness, 0f, 0.99f);
        if (e == 0f || bounces <= 0) return t * t;

        float total = 1f;
        float w = e;
        for (int k = 0; k < bounces; k++) {
            total += 2f * w;
            w *= e;
        }

        float s = t * total;


        if (s < 1f) return s * s;
        s -= 1f;


        w = e;
        for (int k = 0; k < bounces; k++) {
            float duration = 2f * w;
            if (s < duration) {
                float x = s / w - 1f;
                return 1f - w * w * (1f - x * x);
            }
            s -= duration;
            w *= e;
        }
        return 1f;
    }

    public static float bouI(float t, float bounciness, int bounces) {
        return 1f - bouO(1f - t, bounciness, bounces);
    }

    public static float bouIO(float t, float bounciness, int bounces) {
        t = Math.clamp(t, 0f, 1f);
        return t < 0.5f
                ? bouI(t * 2f, bounciness, bounces) * 0.5f
                : bouO(t * 2f - 1f, bounciness, bounces) * 0.5f + 0.5f;
    }
}
