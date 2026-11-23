package net.liukrast.hns.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.liukrast.hns.world.level.block.LargeDoorBlock;
import net.liukrast.hns.world.level.block.entity.LargeDoorBlockEntity;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class LargeDoorModel implements DoorModel<LargeDoorBlockEntity> {

    private final ModelPart right, left;

    public LargeDoorModel() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition part = mesh.getRoot();

        part.addOrReplaceChild(
                "right",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(0.0F, -48.0F, 0.0F, 24.0F, 48.0F, 3.0F)
                        .texOffs(3, 51)
                        .addBox(18.0F, -26.0F, -1.0F, 5.0F, 8.0F, 1.0F),
                PartPose.offset(-8.0F, 24.0F, -8.0F)
        );
        part.addOrReplaceChild(
                "left",
                CubeListBuilder.create()
                        .texOffs(0, 0).mirror()
                        .addBox(-24.0F, -48.0F, 0.0F, 24.0F, 48.0F, 3.0F)
                        .mirror(false)
                        .texOffs(3, 51)
                        .addBox(-23.0F, -26.0F, -1.0F, 5.0F, 8.0F, 1.0F),
                PartPose.offset(40.0F, 24.0F, -8.0F)
        );

        var root = LayerDefinition.create(mesh, 64, 64).bakeRoot();
        right = root.getChild("right");
        left = root.getChild("left");
    }

    @Override
    public void setupAnim(LargeDoorBlockEntity blockEntity, float partialTick, float progress) {
        float e = 1-blockEntity.getAnimation().get(1-progress);
        float angle = (e* blockEntity.getHingeAngle()) * Mth.PI/180;
        right.yRot = angle;
        left.yRot = -angle;
    }

    @Override
    public void render(PoseStack poseStack, VertexConsumer consumer, int packedLight, int packedOverlay) {
        right.render(poseStack, consumer, packedLight, packedOverlay);
        left.render(poseStack, consumer, packedLight, packedOverlay);
    }

    @Override
    public ResourceLocation getTexture(LargeDoorBlockEntity blockEntity) {
        var block = blockEntity.getBlockState().getBlock();
        if(block instanceof LargeDoorBlock ldb) return ldb.getTexture(blockEntity);
        return null; // Error
    }

    @Override
    public AABB getRenderBound(AABB original, LargeDoorBlockEntity be, BlockState state, Direction facing) {
        return simpleDoorBound(original, facing, 3, 3, 0);
    }
}
