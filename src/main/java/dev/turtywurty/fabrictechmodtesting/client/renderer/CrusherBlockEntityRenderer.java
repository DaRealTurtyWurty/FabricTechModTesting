package dev.turtywurty.fabrictechmodtesting.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.turtywurty.fabrictechmodtesting.FabricTechModTesting;
import dev.turtywurty.fabrictechmodtesting.client.model.CrusherModel;
import dev.turtywurty.fabrictechmodtesting.common.blockentity.CrusherBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class CrusherBlockEntityRenderer implements BlockEntityRenderer<CrusherBlockEntity> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(FabricTechModTesting.MOD_ID, "textures/block/crusher.png");

    private final CrusherModel model;

    public CrusherBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.model = new CrusherModel(context.bakeLayer(CrusherModel.LAYER_LOCATION));
    }

    @Override
    public void render(CrusherBlockEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
        pPoseStack.pushPose();
        pPoseStack.translate(0.5, 0.225, 0.5);
        pPoseStack.scale(1.0F, -1.0F, -1.0F);

        if (pBlockEntity.getProgress() > 0) {
            this.model.getParts().bottomLeft().zRot += 0.1F;
            this.model.getParts().bottomRight().zRot -= 0.1F;
            this.model.getParts().topLeft().zRot += 0.1F;
            this.model.getParts().topRight().zRot -= 0.1F;
        } else {
            this.model.getParts().bottomLeft().zRot = 0;
            this.model.getParts().bottomRight().zRot = 0;
            this.model.getParts().topLeft().zRot = 0;
            this.model.getParts().topRight().zRot = 0;
        }

        VertexConsumer vertexConsumer = pBufferSource.getBuffer(this.model.renderType(TEXTURE));
        this.model.renderToBuffer(pPoseStack, vertexConsumer, pPackedLight, pPackedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
        this.model.getParts().left().render(pPoseStack, vertexConsumer, pPackedLight, pPackedOverlay);
        this.model.getParts().right().render(pPoseStack, vertexConsumer, pPackedLight, pPackedOverlay);

        pPoseStack.popPose();
    }
}