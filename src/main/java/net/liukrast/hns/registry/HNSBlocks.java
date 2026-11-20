package net.liukrast.hns.registry;

import net.liukrast.hns.HNSConstants;
import net.liukrast.hns.world.level.block.BlastDoorBlock;
import net.liukrast.hns.world.level.block.GarageDoorBlock;
import net.liukrast.hns.world.level.block.LargeDoorBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class HNSBlocks {
    private static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(HNSConstants.MOD_ID);
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(HNSConstants.MOD_ID);

    public static final DeferredBlock<GarageDoorBlock> GARAGE_DOOR = BLOCKS.register("garage_door", () -> new GarageDoorBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)));
    public static final DeferredBlock<BlastDoorBlock> BLAST_DOOR = BLOCKS.register("blast_door", () -> new BlastDoorBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)));

    public static final DeferredBlock<LargeDoorBlock> LARGE_SPRUCE_DOOR = BLOCKS.register("large_spruce_door", () -> new LargeDoorBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_PLANKS), true, HNSConstants.id("textures/entity/large_spruce_door.png")));

    static {
        ITEMS.register("garage_door", () -> new BlockItem(GARAGE_DOOR.get(), new Item.Properties()));
        ITEMS.register("blast_door", () -> new BlockItem(BLAST_DOOR.get(), new Item.Properties()));
        ITEMS.register("large_spruce_door", () -> new BlockItem(LARGE_SPRUCE_DOOR.get(), new Item.Properties()));
    }
    private HNSBlocks() {}

    public static void init(IEventBus bus) {
        BLOCKS.register(bus);
        ITEMS.register(bus);
    }
}
