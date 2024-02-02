package dev.turtywurty.fabrictechmodtesting;

import dev.turtywurty.fabrictechmodtesting.common.blockentity.AlloyFurnaceBlockEntity;
import dev.turtywurty.fabrictechmodtesting.core.init.*;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FabricTechModTesting implements ModInitializer {
    public static final String MOD_ID = "fabrictechmodtesting";
    public static final Logger LOGGER = LoggerFactory.getLogger(FabricTechModTesting.class);

    public static ResourceLocation id(String name) {
        return new ResourceLocation(MOD_ID, name);
    }

    @Override
    public void onInitialize() {
        LOGGER.info("Loading common for {}!", MOD_ID);
        ItemInit.init();
        BlockInit.init();
        BlockEntityTypeInit.init();
        MenuTypeInit.init();
        CreativeTabInit.init();

        ItemStorage.SIDED.registerForBlockEntity(AlloyFurnaceBlockEntity::getProviderHandler, BlockEntityTypeInit.ALLOY_FURNACE);
    }
}
