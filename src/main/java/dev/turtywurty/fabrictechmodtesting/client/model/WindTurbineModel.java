package dev.turtywurty.fabrictechmodtesting.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.turtywurty.fabrictechmodtesting.FabricTechModTesting;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public class WindTurbineModel extends Model {
    public static final ModelLayerLocation LAYER_LOCATION =
            new ModelLayerLocation(FabricTechModTesting.id("wind_turbine"), "main");
    public static final ResourceLocation TEXTURE_LOCATION = FabricTechModTesting.id("textures/block/wind_turbine.png");

    private final ModelPart main;
    private final ModelPart foundation;
    private final ModelPart propellers;

    public WindTurbineModel(ModelPart root) {
        super(RenderType::entityCutoutNoCull);
        this.main = root.getChild("main");
        this.foundation = this.main.getChild("foundation");
        this.propellers = this.main.getChild("propellers");
    }

    public static LayerDefinition createMainLayer() {
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition partDefinition = meshDefinition.getRoot();

        PartDefinition main = partDefinition.addOrReplaceChild("main", CubeListBuilder.create(),
                PartPose.offset(0.0F, 24.0F, 0.0F));

        main.addOrReplaceChild("foundation", CubeListBuilder.create()
                        .texOffs(9, 16)
                        .addBox(-1.0F, -7.25F, -1.0F, 2.0F, 9.0F, 2.0F, new CubeDeformation(0.0F))
                        .texOffs(0, 0)
                        .addBox(-4.0F, 1.75F, -4.0F, 8.0F, 2.0F, 8.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, -3.75F, 0.0F));

        main.addOrReplaceChild("propellers", CubeListBuilder.create()
                        .texOffs(0, 11)
                        .addBox(-6.0F, -1.0F, -1.0F, 12.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                        .texOffs(0, 16)
                        .addBox(-1.0F, -6.0F, -1.0F, 2.0F, 12.0F, 2.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, -10.0F, -2.0F));

        return LayerDefinition.create(meshDefinition, 32, 32);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        this.main.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    public ModelPart getPropellers() {
        return this.propellers;
    }
}