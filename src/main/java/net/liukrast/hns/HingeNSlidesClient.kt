package net.liukrast.hns;

import net.liukrast.hns.registry.HNSBlockEntityTypes;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(value = HNSConstants.MOD_ID, dist = Dist.CLIENT)
public class HingeNSlidesClient {
    public HingeNSlidesClient(IEventBus bus) {
        bus.addListener(HNSBlockEntityTypes::registerRenderers);
    }
}
