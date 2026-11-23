package net.liukrast.hns.client.model

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import net.liukrast.hns.HNSConstants
import net.liukrast.hns.util.AMth
import net.liukrast.hns.world.level.block.entity.GarageDoorBlockEntity
import net.liukrast.hns.world.level.block.entity.GarageDoorTexture
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.client.model.geom.PartPose
import net.minecraft.client.model.geom.builders.CubeListBuilder
import net.minecraft.client.model.geom.builders.LayerDefinition
import net.minecraft.client.model.geom.builders.MeshDefinition
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.core.Direction
import net.minecraft.util.Mth
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.AABB

class GarageDoorModel : DoorModel<GarageDoorBlockEntity> {

    companion object {
        private val BASE_NORMAL = HNSConstants.id("textures/entity/garage_door/base_normal.png")
        private val GLASS_NORMAL = HNSConstants.id("textures/entity/garage_door/glass_normal.png")
        private val BASE_CONNECTED = HNSConstants.id("textures/entity/garage_door/base_connected.png")
        private val GLASS_CONNECTED = HNSConstants.id("textures/entity/garage_door/glass_connected.png")
    }

    private val top: ModelPart
    private val middle: ModelPart
    private val bottom: ModelPart

    init {
        val mesh = MeshDefinition()
        val part = mesh.root

        part.addOrReplaceChild(
            "top", CubeListBuilder.create()
                .texOffs(0, 0)
                .addBox(0f, 0f, 0f, 80f, 16f, 4f),
            PartPose.offset(-8f, -24f, -8f)
        )
        part.addOrReplaceChild(
            "middle", CubeListBuilder.create()
                .texOffs(0, 20)
                .addBox(0f, 0f, 0f, 80f, 16f, 4f),
            PartPose.offset(-8f, -8f, -8f)
        )
        part.addOrReplaceChild(
            "bottom", CubeListBuilder.create()
                .texOffs(0, 40)
                .addBox(0f, 0f, 0f, 80f, 16f, 4f),
            PartPose.offset(-8f, 8f, -8f)
        )

        val root = LayerDefinition.create(mesh, 256, 64).bakeRoot()
        top = root.getChild("top")
        middle = root.getChild("middle")
        bottom = root.getChild("bottom")
    }

    override fun setupAnim(blockEntity: GarageDoorBlockEntity, partialTick: Float, progress: Float) {
        val e = blockEntity.animation.get(progress)
        top.xRot = -AMth.sec(e, 0f, 1 / 3f) * Mth.PI / 2
        top.z = -8 + AMth.sec(e, 0f, 1 / 3f) * 16 + AMth.sec(e, 1 / 3f, 1f) * 32

        middle.y = -8 - AMth.sec(e, 0f, 1 / 3f) * 16
        middle.xRot = -AMth.sec(e, 1 / 3f, 2 / 3f) * Mth.PI / 2
        middle.z = -8 + AMth.sec(e, 1 / 3f, 2 / 3f) * 16 + AMth.sec(e, 2 / 3f, 1f) * 16

        bottom.y = 8 - AMth.sec(e, 0f, 1 / 3f) * 16 - AMth.sec(e, 1 / 3f, 2 / 3f) * 16
        bottom.xRot = -AMth.sec(e, 2 / 3f, 1f) * Mth.PI / 2
        bottom.z = -8 + AMth.sec(e, 2 / 3f, 1f) * 16
    }

    override fun render(
        poseStack: PoseStack,
        bufferSource: MultiBufferSource,
        blockEntity: GarageDoorBlockEntity,
        packedLight: Int,
        packedOverlay: Int
    ) {
        val connected = blockEntity.texture == GarageDoorTexture.CONNECTED

        val baseTex = if(connected) BASE_CONNECTED else BASE_NORMAL
        val glassTex = if(connected) GLASS_CONNECTED else GLASS_NORMAL

        val color = (0xFF000000.toInt()) or blockEntity.color
        val baseConsumer = bufferSource.getBuffer(RenderType.entityCutout(baseTex))
        renderParts(poseStack, baseConsumer, packedLight, packedOverlay, color)

        val glassConsumer = bufferSource.getBuffer(RenderType.entityCutout(glassTex))
        renderParts(poseStack, glassConsumer, packedLight, packedOverlay, 0xFFFFFFFF.toInt())

    }

    private fun renderParts(ps: PoseStack, vc: VertexConsumer, light: Int, overlay: Int, color: Int) {
        top.render(ps, vc, light, overlay, color)
        middle.render(ps, vc, light, overlay, color)
        bottom.render(ps, vc, light, overlay, color)
    }

    override fun getRenderBound(original: AABB, be: GarageDoorBlockEntity, state: BlockState, facing: Direction): AABB = simpleDoorBound(original, facing, 5, 3, -2)
}