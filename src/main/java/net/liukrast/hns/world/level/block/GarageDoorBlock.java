package net.liukrast.hns.world.level.block;

public class GarageDoorBlock extends DoorBlock {
    public GarageDoorBlock(Properties properties) {
        super(properties, false, true);
    }

    @Override
    public void defineParts(Builder builder) {
        for(int x = 0; x < 4; x++) {
            for(int y = 0; y < 3; y++) {
                builder.define(x, y, 0);
            }
        }
    }
}
