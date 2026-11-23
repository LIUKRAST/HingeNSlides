package net.liukrast.hns;

import net.minecraft.resources.ResourceLocation;

public class HNSConstants {

    public static final String MOD_ID = "hinge_n_slides";
    public static final String PROTOCOL_VERSION = "1.0";

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    private HNSConstants() {}
}
