package net.liukrast.hns.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.liukrast.hns.client.model.DoorModel;
import net.liukrast.hns.world.level.block.entity.AnimatedDoorBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.phys.AABB;
import org.lwjgl.system.NonnullDefault;

@NonnullDefault
public class DoorRenderer<T extends AnimatedDoorBlockEntity> implements BlockEntityRenderer<T> {

    protected DoorModel<T> model;

    public DoorRenderer(BlockEntityRendererProvider.Context ignored, DoorModel<T> model) {
        this.model = model;
    }

    @Override
    public void render(T blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        poseStack.scale(-1, -1, 1);
        var facing = blockEntity.getBlockState().getValue(DoorBlock.FACING);
        poseStack.translate(facing.getStepZ() == 0 ? -0.5 : facing.getStepZ() < 0 ? -1 : 0, -1.5, facing.getStepX() == 0 ? 0.5 : facing.getStepX() > 0 ? 1 : 0);
        poseStack.mulPose(Axis.YP.rotationDegrees(180 + facing.toYRot()));
        poseStack.translate(0.5, 0, 0);
        boolean flag = blockEntity.getBlockState().getValue(DoorBlock.OPEN);

        long gameTime = blockEntity.getLevel() == null ? 0 : blockEntity.getLevel().getGameTime();
        int animDuration = model.animationDuration(blockEntity);
        if(blockEntity.lastStateTime < 0) {
            blockEntity.lastState = flag;
            blockEntity.lastStateTime = 0;
        }
        if(blockEntity.lastState != flag) {
            blockEntity.lastStateTime = gameTime- animDuration + (Mth.clamp(gameTime - blockEntity.lastStateTime, 0, animDuration));
            blockEntity.lastState = flag;
        }
        float linear = Mth.clamp(Mth.clamp(gameTime - blockEntity.lastStateTime, 0, animDuration) + partialTick, 0, animDuration) / animDuration;
        float progress = flag ? linear : 1 - linear;
        model.setupAnim(blockEntity, partialTick, progress);
        model.render(poseStack, model.getConsumer(blockEntity, bufferSource), packedLight, packedOverlay);
    }

    @Override
    public AABB getRenderBoundingBox(T blockEntity) {
        var state = blockEntity.getBlockState();
        var dir = state.getValue(DoorBlock.FACING);
        return model.getRenderBound(BlockEntityRenderer.super.getRenderBoundingBox(blockEntity), blockEntity, state, dir);
    }
}
