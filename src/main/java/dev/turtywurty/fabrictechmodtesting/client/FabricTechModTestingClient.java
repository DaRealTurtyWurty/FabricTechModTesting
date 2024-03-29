package dev.turtywurty.fabrictechmodtesting.client;

import dev.turtywurty.fabrictechmodtesting.FabricTechModTesting;
import dev.turtywurty.fabrictechmodtesting.client.screen.AlloyFurnaceScreen;
import dev.turtywurty.fabrictechmodtesting.core.init.MenuTypeInit;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screens.MenuScreens;

public class FabricTechModTestingClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        FabricTechModTesting.LOGGER.info("Loading client for {}!", FabricTechModTesting.MOD_ID);
        MenuScreens.register(MenuTypeInit.ALLOY_FURNACE, AlloyFurnaceScreen::new);
    }
}
