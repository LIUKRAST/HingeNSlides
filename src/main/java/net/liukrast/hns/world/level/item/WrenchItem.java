package net.liukrast.hns.world.level.item;

import net.liukrast.hns.client.gui.screens.DoorConfigScreen;
import net.liukrast.hns.world.level.block.DoorBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import org.lwjgl.system.NonnullDefault;

@NonnullDefault
public class WrenchItem extends Item {
    public WrenchItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext ctx) {
        var level = ctx.getLevel();
        var state = level.getBlockState(ctx.getClickedPos());
        if(!(state.getBlock() instanceof DoorBlock<?> db)) return super.useOn(ctx);
        var controllerPos = db.getControllerPos(ctx.getClickedPos(), state);
        if(!(level.getBlockState(controllerPos).getBlock() instanceof DoorBlock<?>)) return InteractionResult.FAIL;
        if(!ctx.getLevel().isClientSide) return InteractionResult.SUCCESS;
        Minecraft.getInstance().setScreen(new DoorConfigScreen(controllerPos));
        return InteractionResult.SUCCESS;
    }
}
