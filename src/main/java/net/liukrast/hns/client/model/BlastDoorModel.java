package net.liukrast.hns.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.liukrast.hns.HNSConstants;
import net.liukrast.hns.world.level.block.entity.BlastDoorBlockEntity;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import static net.liukrast.hns.util.AMth.*;

public class BlastDoorModel implements DoorModel<BlastDoorBlockEntity> {
    private static final ResourceLocation TEXTURE = HNSConstants.id("textures/entity/blast_door.png");

    private final ModelPart right, left;

    public BlastDoorModel() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition part = mesh.getRoot();

        part.addOrReplaceChild(
                "right",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(0, -48, -7, 48, 48, 7),
                PartPose.offset(-8, 24, -1)
        );

        part.addOrReplaceChild(
                "left",
                CubeListBuilder.create()
                        .texOffs(0, 55)
                        .addBox(0, -48, -7, 48, 48, 7),
                PartPose.offset(40, 24, -1));

        var root = LayerDefinition.create(mesh, 128, 128).bakeRoot();
        right = root.getChild("right");
        left = root.getChild("left");
    }

    @Override
    public void setupAnim(BlastDoorBlockEntity blockEntity, float partialTick, float progress) {
        float e = blockEntity.getAnimation().get(progress);
        right.x = -8 - e*48;
        left.x = 40 + e*48;
    }

    @Override
    public void render(PoseStack poseStack, VertexConsumer consumer, int packedLight, int packedOverlay) {
        left.render(poseStack, consumer, packedLight, packedOverlay);
        right.render(poseStack, consumer, packedLight, packedOverlay);
    }

    @Override
    public ResourceLocation getTexture(BlastDoorBlockEntity blockEntity) {
        return TEXTURE;
    }

    @Override
    public AABB getRenderBound(AABB original, BlastDoorBlockEntity be, BlockState state, Direction facing) {
        var cl = facing.getCounterClockWise();
        return original
                .expandTowards(new Vec3(cl.getStepX()*8, 2, cl.getStepZ()*8))
                .expandTowards(new Vec3(cl.getStepX()*-3, 0, cl.getStepZ()*-3));
    }
}
