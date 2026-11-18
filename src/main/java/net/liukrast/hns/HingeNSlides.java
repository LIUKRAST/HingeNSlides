package net.liukrast.hns;

import net.liukrast.hns.registry.HNSBlockEntityTypes;
import net.liukrast.hns.registry.HNSBlocks;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(HNSConstants.MOD_ID)
public class HingeNSlides {

    public HingeNSlides(IEventBus bus) {
        HNSBlocks.init(bus);
        HNSBlockEntityTypes.init(bus);
    }
}
