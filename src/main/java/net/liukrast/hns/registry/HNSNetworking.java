package net.liukrast.hns.registry;

import net.liukrast.hns.HNSConstants;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

public class HNSNetworking {
    public static void register(RegisterPayloadHandlersEvent event) {
        final var registry = event.registrar(HNSConstants.MOD_ID).versioned(HNSConstants.PROTOCOL_VERSION);

    }
}
