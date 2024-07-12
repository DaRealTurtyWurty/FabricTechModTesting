package dev.turtywurty.fabrictechmodtesting.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.turtywurty.fabrictechmodtesting.client.model.WindTurbineModel;
import dev.turtywurty.fabrictechmodtesting.common.block.WindTurbineBlock;
import dev.turtywurty.fabrictechmodtesting.common.blockentity.WindTurbineBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;

public class WindTurbineBlockEntityRenderer implements BlockEntityRenderer<WindTurbineBlockEntity> {
    private final WindTurbineModel model;
    private final RenderType renderType;

    public WindTurbineBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.model = new WindTurbineModel(context.bakeLayer(WindTurbineModel.LAYER_LOCATION));
        this.renderType = this.model.renderType(WindTurbineModel.TEXTURE_LOCATION);
    }

    @Override
    public void render(WindTurbineBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight, int packedOverlay) {
        poseStack.pushPose();
        poseStack.translate(0.5D, 1.5D, 0.5D);
        poseStack.scale(1F, -1F, 1F);

        Direction direction = blockEntity.getBlockState().getValue(WindTurbineBlock.FACING);
        if(direction == Direction.NORTH || direction == Direction.SOUTH)
            direction = direction.getOpposite();

        poseStack.mulPose(Axis.YP.rotationDegrees(direction.toYRot()));

        this.model.getPropellers().zRot += blockEntity.getEnergyOutput() * 0.005F * partialTick;
        this.model.renderToBuffer(poseStack, multiBufferSource.getBuffer(this.renderType), packedLight, packedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
        poseStack.popPose();
    }
}
