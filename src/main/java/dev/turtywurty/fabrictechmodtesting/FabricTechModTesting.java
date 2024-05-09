package dev.turtywurty.fabrictechmodtesting;

import dev.turtywurty.fabrictechmodtesting.common.blockentity.*;
import dev.turtywurty.fabrictechmodtesting.core.init.*;
import dev.turtywurty.fabrictechmodtesting.core.util.CountedIngredient;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredientSerializer;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import team.reborn.energy.api.EnergyStorage;

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
        RecipeInit.init();
        CreativeTabInit.init();
        CustomIngredientSerializer.register(CountedIngredient.SERIALIZER);

        ItemStorage.SIDED.registerForBlockEntity(AlloyFurnaceBlockEntity::getInventoryProvider, BlockEntityTypeInit.ALLOY_FURNACE);

        ItemStorage.SIDED.registerForBlockEntity(CrusherBlockEntity::getInventoryProvider, BlockEntityTypeInit.CRUSHER);
        EnergyStorage.SIDED.registerForBlockEntity(CrusherBlockEntity::getEnergyProvider, BlockEntityTypeInit.CRUSHER);

        EnergyStorage.SIDED.registerForBlockEntity(BatteryBlockEntity::getEnergyProvider, BlockEntityTypeInit.BATTERY);

        ItemStorage.SIDED.registerForBlockEntity(CombustionGeneratorBlockEntity::getInventoryProvider, BlockEntityTypeInit.COMBUSTION_GENERATOR);
        EnergyStorage.SIDED.registerForBlockEntity(CombustionGeneratorBlockEntity::getEnergyProvider, BlockEntityTypeInit.COMBUSTION_GENERATOR);

        EnergyStorage.SIDED.registerForBlockEntity(CableBlockEntity::getEnergyProvider, BlockEntityTypeInit.CABLE);
        EnergyStorage.SIDED.registerForBlockEntity(CableFacadeBlockEntity::getEnergyProvider, BlockEntityTypeInit.CABLE_FACADE);

        ItemStorage.SIDED.registerForBlockEntity(SolarPanelBlockEntity::getInventoryProvider, BlockEntityTypeInit.SOLAR_PANEL);
        EnergyStorage.SIDED.registerForBlockEntity(SolarPanelBlockEntity::getEnergyProvider, BlockEntityTypeInit.SOLAR_PANEL);
    }
}
