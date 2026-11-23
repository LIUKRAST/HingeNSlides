package net.liukrast.hns.client.model

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import net.liukrast.hns.HNSConstants
import net.liukrast.hns.world.level.block.entity.BlastDoorBlockEntity
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.client.model.geom.PartPose
import net.minecraft.client.model.geom.builders.CubeListBuilder
import net.minecraft.client.model.geom.builders.LayerDefinition
import net.minecraft.client.model.geom.builders.MeshDefinition
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.core.Direction
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3

class BlastDoorModel : DoorModel<BlastDoorBlockEntity> {
    companion object {
        private val BASE = HNSConstants.id("textures/entity/blast_door/base.png")
        private val OVERLAY = HNSConstants.id("textures/entity/blast_door/overlay.png")
    }

    private val right: ModelPart
    private val left: ModelPart

    init {
        val mesh = MeshDefinition()
        val part = mesh.root

        part.addOrReplaceChild(
            "right",
            CubeListBuilder.create()
                .texOffs(0,0)
                .addBox(0f, -48f, -7f, 48f, 48f, 7f),
            PartPose.offset(-8f, 24f, -1f)
        )

        part.addOrReplaceChild(
            "left",
            CubeListBuilder.create()
                .texOffs(0, 55)
                .addBox(0f, -48f, -7f, 48f, 48f, 7f),
            PartPose.offset(40f, 24f, -1f)
        )

        val root = LayerDefinition.create(mesh, 128, 128).bakeRoot()
        right = root.getChild("right")
        left = root.getChild("left")
    }

    override fun setupAnim(
        blockEntity: BlastDoorBlockEntity,
        partialTick: Float,
        progress: Float
    ) {
        val e = blockEntity.animation.get(progress)
        right.x = -8f -e*48f
        left.x = 40f + e*48
    }

    override fun render(
        poseStack: PoseStack,
        bufferSource: MultiBufferSource,
        blockEntity: BlastDoorBlockEntity,
        packedLight: Int,
        packedOverlay: Int
    ) {
        val color = (0xFF000000.toInt()) or blockEntity.color
        val baseConsumer = bufferSource.getBuffer(RenderType.entityCutout(BASE))
        renderParts(poseStack, baseConsumer, packedLight, packedOverlay, color)

        val glassConsumer = bufferSource.getBuffer(RenderType.entityCutout(OVERLAY))
        renderParts(poseStack, glassConsumer, packedLight, packedOverlay, 0xFFFFFFFF.toInt())
    }

    private fun renderParts(ps: PoseStack, vc: VertexConsumer, light: Int, overlay: Int, color: Int) {
        right.render(ps, vc, light, overlay, color)
        left.render(ps, vc, light, overlay, color)
    }

    override fun getRenderBound(
        original: AABB,
        be: BlastDoorBlockEntity,
        state: BlockState,
        facing: Direction
    ): AABB {
        val cl = facing.counterClockWise
        return original
            .expandTowards(Vec3(cl.stepX*8.0, 2.0, cl.stepZ*8.0))
            .expandTowards(Vec3(cl.stepX*-3.0, 0.0, cl.stepZ*-3.0))
    }
}