package dev.turtywurty.fabrictechmodtesting.client;

import dev.turtywurty.fabrictechmodtesting.FabricTechModTesting;
import dev.turtywurty.fabrictechmodtesting.client.screen.AlloyFurnaceScreen;
import dev.turtywurty.fabrictechmodtesting.client.screen.CombustionGeneratorScreen;
import dev.turtywurty.fabrictechmodtesting.client.screen.CrusherScreen;
import dev.turtywurty.fabrictechmodtesting.client.screen.SolarPanelScreen;
import dev.turtywurty.fabrictechmodtesting.core.init.MenuTypeInit;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screens.MenuScreens;

public class FabricTechModTestingClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        FabricTechModTesting.LOGGER.info("Loading client for {}!", FabricTechModTesting.MOD_ID);
        MenuScreens.register(MenuTypeInit.ALLOY_FURNACE, AlloyFurnaceScreen::new);
        MenuScreens.register(MenuTypeInit.CRUSHER, CrusherScreen::new);
        MenuScreens.register(MenuTypeInit.COMBUSTION_GENERATOR, CombustionGeneratorScreen::new);
        MenuScreens.register(MenuTypeInit.SOLAR_PANEL, SolarPanelScreen::new);
    }
}
