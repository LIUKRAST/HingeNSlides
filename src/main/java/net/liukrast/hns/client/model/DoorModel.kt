package net.liukrast.hns.client.model

import com.mojang.blaze3d.vertex.PoseStack
import net.liukrast.hns.client.renderer.DoorRenderer
import net.liukrast.hns.world.level.block.BasicAnimationSet
import net.liukrast.hns.world.level.block.entity.DoorBlockEntity
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.core.Direction
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3

interface DoorModel<T : DoorBlockEntity> {
    fun setupAnim(blockEntity: T, partialTick: Float, progress: Float)
    fun render(poseStack: PoseStack, bufferSource: MultiBufferSource, blockEntity: T, packedLight: Int, packedOverlay: Int)

    fun animationDuration(blockEntity: T) : Int {
        if(blockEntity.animation == BasicAnimationSet.VANILLA) return 1
        return (20 * blockEntity.animationDuration).toInt()
    }

    fun renderer(ctx: BlockEntityRendererProvider.Context) : DoorRenderer<T> = DoorRenderer(ctx, this)

    fun getRenderBound(original: AABB, be: T, state: BlockState, facing: Direction) : AABB

    fun simpleDoorBound(original: AABB, facing: Direction, width: Int, height: Int, z: Int): AABB =
        original
            .expandTowards(Vec3.atLowerCornerOf(facing.normal).scale(z.toDouble()))
            .expandTowards(Vec3.atLowerCornerOf(facing.counterClockWise.normal).scale((width - 1).toDouble()))
            .expandTowards(0.0, (height - 1).toDouble(), 0.0)
}