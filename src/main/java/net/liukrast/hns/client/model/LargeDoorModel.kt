package net.liukrast.hns.client.model

import com.mojang.blaze3d.vertex.PoseStack
import net.liukrast.hns.world.level.block.entity.LargeDoorBlockEntity
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

class LargeDoorModel : DoorModel<LargeDoorBlockEntity> {

    private val right: ModelPart
    private val left: ModelPart

    init {
        val mesh = MeshDefinition()
        val part = mesh.root

        part.addOrReplaceChild(
            "right",
            CubeListBuilder.create()
                .texOffs(0, 0)
                .addBox(0f, -48f, 0f, 24f, 48f, 3f)
                .texOffs(3, 51)
                .addBox(18f, -26f, -1f, 5f, 8f, 1f),
            PartPose.offset(-8f, 24f, -8f)
        )

        part.addOrReplaceChild(
            "left",
            CubeListBuilder.create()
                .texOffs(0, 0).mirror()
                .addBox(-24f, -48f, 0f, 24f, 48f, 3f)
                .mirror(false)
                .texOffs(3, 51)
                .addBox(-23f, -26f, -1f, 5f, 8f, 1f),
            PartPose.offset(40f, 24f, -8f)
        )

        val root = LayerDefinition.create(mesh, 64, 64).bakeRoot()
        right = root.getChild("right")
        left = root.getChild("left")
    }

    override fun setupAnim(
        blockEntity: LargeDoorBlockEntity,
        partialTick: Float,
        progress: Float
    ) {
        val e = 1-blockEntity.animation.get(1-progress)
        val angle = (e * blockEntity.hinge) * Mth.PI/180
        right.yRot = angle
        left.yRot = -angle
    }

    override fun render(
        poseStack: PoseStack,
        bufferSource: MultiBufferSource,
        blockEntity: LargeDoorBlockEntity,
        packedLight: Int,
        packedOverlay: Int
    ) {
        val consumer = bufferSource.getBuffer(RenderType.entityCutout(blockEntity.texture))
        right.render(poseStack, consumer, packedLight, packedOverlay)
        left.render(poseStack, consumer, packedLight, packedOverlay)
    }

    override fun getRenderBound(
        original: AABB,
        be: LargeDoorBlockEntity,
        state: BlockState,
        facing: Direction
    ): AABB = simpleDoorBound(original, facing, 3, 3, 0)
}