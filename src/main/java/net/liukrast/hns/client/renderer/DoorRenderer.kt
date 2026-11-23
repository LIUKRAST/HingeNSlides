package net.liukrast.hns.client.renderer

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import net.liukrast.hns.client.model.DoorModel
import net.liukrast.hns.world.level.block.entity.DoorBlockEntity
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.core.Direction
import net.minecraft.util.Mth
import net.minecraft.world.level.block.DoorBlock
import net.minecraft.world.phys.AABB

class DoorRenderer<T : DoorBlockEntity>(ignored: BlockEntityRendererProvider.Context, private val model: DoorModel<T>) : BlockEntityRenderer<T> {
    override fun render(
        blockEntity: T,
        partialTick: Float,
        poseStack: PoseStack,
        bufferSource: MultiBufferSource,
        packedLight: Int,
        packedOverlay: Int
    ) {
        poseStack.pushPose()

        poseStack.scale(-1f, -1f, 1f)
        val facing: Direction = blockEntity.blockState.getValue(DoorBlock.FACING)
        poseStack.translate(
            if (facing.stepZ == 0) -0.5 else (if (facing.stepZ < 0) -1 else 0).toDouble(),
            -1.5,
            if (facing.stepX == 0) 0.5 else (if (facing.stepX > 0) 1 else 0).toDouble()
        )
        poseStack.mulPose(Axis.YP.rotationDegrees(180 + facing.toYRot()))
        poseStack.translate(0.5, 0.0, 0.0)

        val flag: Boolean = blockEntity.blockState.getValue(DoorBlock.OPEN)

        val gameTime = if (blockEntity.getLevel() == null) 0 else blockEntity.getLevel()!!.gameTime
        val animDuration = model.animationDuration(blockEntity)
        if (blockEntity.lastStateTime < 0) {
            blockEntity.lastState = flag
            blockEntity.lastStateTime = 0
        }
        if (blockEntity.lastState != flag) {
            blockEntity.lastStateTime =
                gameTime - animDuration + Mth.clamp(gameTime - blockEntity.lastStateTime, 0, animDuration.toLong())
            blockEntity.lastState = flag
        }
        val linear = Mth.clamp(
            Mth.clamp(gameTime - blockEntity.lastStateTime, 0, animDuration.toLong()) + partialTick,
            0f,
            animDuration.toFloat()
        ) / animDuration
        val progress = if (flag) linear else 1 - linear

        model.setupAnim(blockEntity, partialTick, progress)
        model.render(poseStack, bufferSource, blockEntity, packedLight, packedOverlay)

        poseStack.popPose()
    }

    override fun getRenderBoundingBox(blockEntity: T): AABB {
        val state = blockEntity.blockState
        val dir = state.getValue(DoorBlock.FACING)
        return model.getRenderBound(super.getRenderBoundingBox(blockEntity), blockEntity, state, dir)
    }
}