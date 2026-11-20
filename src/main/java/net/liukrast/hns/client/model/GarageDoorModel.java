package net.liukrast.hns.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.liukrast.hns.HNSConstants;
import net.liukrast.hns.world.level.block.BasicAnimationSet;
import net.liukrast.hns.world.level.block.entity.GarageDoorBlockEntity;
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
import static net.liukrast.hns.util.AMth.*;

public class GarageDoorModel implements DoorModel<GarageDoorBlockEntity> {
    private static final ResourceLocation TEXTURE = HNSConstants.id("textures/entity/garage_door.png");
    private static final ResourceLocation TEXTURE_CONNECTED = HNSConstants.id("textures/entity/garage_door_connected.png");

    private final ModelPart top, middle, bottom;

    public GarageDoorModel() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition part = mesh.getRoot();

        part.addOrReplaceChild("top", CubeListBuilder.create()
                .texOffs(0, 0)
                .addBox(0,0,0, 80, 16, 4),
                PartPose.offset(-8, -24, -8));
        part.addOrReplaceChild("middle", CubeListBuilder.create()
                .texOffs(0, 20)
                .addBox(0,0,0,80,16,4),
                PartPose.offset(-8, -8, -8));
        part.addOrReplaceChild("bottom", CubeListBuilder.create()
                        .texOffs(0, 20)
                        .addBox(0,0,0,80,16,4),
                PartPose.offset(-8, 8, -8));

        var root = LayerDefinition.create(mesh, 256, 64).bakeRoot();
        top = root.getChild("top");
        middle = root.getChild("middle");
        bottom = root.getChild("bottom");
    }


    @Override
    public void setupAnim(GarageDoorBlockEntity blockEntity, float partialTick, float progress) {
        float e = blockEntity.getAnimation().get(progress);
        top.xRot = -sec(e, 0, 1/3f)*Mth.PI/2;
        top.z = -8 + sec(e, 0, 1/3f)*16 + sec(e, 1/3f, 1)*32;

        middle.y = -8 - sec(e, 0, 1/3f)*16;
        middle.xRot = -sec(e, 1/3f, 2/3f)*Mth.PI/2;
        middle.z = -8 + sec(e, 1/3f, 2/3f)*16 + sec(e, 2/3f, 1)*16;

        bottom.y = 8 - sec(e, 0, 1/3f)*16 - sec(e, 1/3f, 2/3f)*16;
        bottom.xRot = -sec(e, 2/3f, 1)*Mth.PI/2;
        bottom.z = -8 + sec(e, 2/3f, 1)*16;
    }

    @Override
    public int animationDuration(GarageDoorBlockEntity blockEntity) {
        if(blockEntity.getAnimation() == BasicAnimationSet.VANILLA) return 1;
        return (int) (20 * blockEntity.getSpeed());
    }

    @Override
    public void render(PoseStack poseStack, VertexConsumer consumer, int packedLight, int packedOverlay) {
        top.render(poseStack, consumer, packedLight, packedOverlay);
        middle.render(poseStack, consumer, packedLight, packedOverlay);
        bottom.render(poseStack, consumer, packedLight, packedOverlay);
    }

    @Override
    public ResourceLocation getTexture(GarageDoorBlockEntity blockEntity) {
        return switch (blockEntity.getTexture()) {
            case NORMAL -> TEXTURE;
            case CONNECTED -> TEXTURE_CONNECTED;
        };
    }

    @Override
    public AABB getRenderBound(AABB original, GarageDoorBlockEntity be, BlockState state, Direction facing) {
        return simpleDoorBound(original, facing, 5, 3, -2);
    }
}
