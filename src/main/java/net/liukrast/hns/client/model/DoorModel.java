package net.liukrast.hns.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.liukrast.hns.client.renderer.DoorRenderer;
import net.liukrast.hns.world.level.block.entity.AnimatedDoorBlockEntity;
import net.liukrast.hns.world.level.block.entity.DoorBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.function.Function;

public interface DoorModel<T extends AnimatedDoorBlockEntity> {
    void setupAnim(T blockEntity, float partialTick, float progress);
    void render(PoseStack poseStack, VertexConsumer consumer, int packedLight, int packedOverlay);
    ResourceLocation getTexture(T blockEntity);
    default VertexConsumer getConsumer(T be, MultiBufferSource source) {
        return source.getBuffer(RenderType.entityCutoutNoCull(getTexture(be)));
    }

    int animationDuration(T blockEntity);

    default DoorRenderer<T> renderer(BlockEntityRendererProvider.Context ctx) {
        return new DoorRenderer<>(ctx, this);
    }

    AABB getRenderBound(AABB original, T be, BlockState state, Direction facing);

    default AABB simpleDoorBound(AABB original, Direction facing, int width, int height, int z) {
        var cl = facing.getCounterClockWise();
        int w = width-1;
        return original.expandTowards(new Vec3(facing.getStepX()*z, height-1, facing.getStepZ()*z))
                .expandTowards(new Vec3(cl.getStepX()*w, 0, cl.getStepZ()*w));
    }

}
