package dev.turtywurty.fabrictechmodtesting.client;

import dev.turtywurty.fabrictechmodtesting.FabricTechModTesting;
import dev.turtywurty.fabrictechmodtesting.client.model.CrusherModel;
import dev.turtywurty.fabrictechmodtesting.client.model.WindTurbineModel;
import dev.turtywurty.fabrictechmodtesting.client.renderer.CrusherBlockEntityRenderer;
import dev.turtywurty.fabrictechmodtesting.client.renderer.WindTurbineBlockEntityRenderer;
import dev.turtywurty.fabrictechmodtesting.client.screen.*;
import dev.turtywurty.fabrictechmodtesting.core.init.BlockEntityTypeInit;
import dev.turtywurty.fabrictechmodtesting.core.init.MenuTypeInit;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;

public class FabricTechModTestingClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        FabricTechModTesting.LOGGER.info("Loading client for {}!", FabricTechModTesting.MOD_ID);

        // Registering Screens
        MenuScreens.register(MenuTypeInit.ALLOY_FURNACE, AlloyFurnaceScreen::new);
        MenuScreens.register(MenuTypeInit.CRUSHER, CrusherScreen::new);
        MenuScreens.register(MenuTypeInit.COMBUSTION_GENERATOR, CombustionGeneratorScreen::new);
        MenuScreens.register(MenuTypeInit.SOLAR_PANEL, SolarPanelScreen::new);
        MenuScreens.register(MenuTypeInit.BATTERY, BatteryScreen::new);
        MenuScreens.register(MenuTypeInit.WIND_TURBINE, WindTurbineScreen::new);

        // Registering Block Entity Renderers
        BlockEntityRenderers.register(BlockEntityTypeInit.CRUSHER, CrusherBlockEntityRenderer::new);
        BlockEntityRenderers.register(BlockEntityTypeInit.WIND_TURBINE, WindTurbineBlockEntityRenderer::new);

        // Registering Model Layers
        EntityModelLayerRegistry.registerModelLayer(CrusherModel.LAYER_LOCATION, CrusherModel::createMainLayer);
        EntityModelLayerRegistry.registerModelLayer(WindTurbineModel.LAYER_LOCATION, WindTurbineModel::createMainLayer);

        FabricTechModTesting.LOGGER.info("Client loaded for {}!", FabricTechModTesting.MOD_ID);
    }
}
