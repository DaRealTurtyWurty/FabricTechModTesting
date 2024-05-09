package dev.turtywurty.fabrictechmodtesting.data.provider;

import dev.turtywurty.fabrictechmodtesting.common.blockentity.AlloyFurnaceBlockEntity;
import dev.turtywurty.fabrictechmodtesting.common.blockentity.CombustionGeneratorBlockEntity;
import dev.turtywurty.fabrictechmodtesting.common.blockentity.CrusherBlockEntity;
import dev.turtywurty.fabrictechmodtesting.common.blockentity.SolarPanelBlockEntity;
import dev.turtywurty.fabrictechmodtesting.core.init.BlockInit;
import dev.turtywurty.fabrictechmodtesting.core.init.CreativeTabInit;
import dev.turtywurty.fabrictechmodtesting.core.init.ItemInit;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;

public class TechModLanguageProvider extends FabricLanguageProvider {
    public TechModLanguageProvider(FabricDataOutput output) {
        super(output);
    }

    private static void addComponent(TranslationBuilder builder, Component key, String value) {
        builder.add(((TranslatableContents) key.getContents()).getKey(), value);
    }

    @Override
    public void generateTranslations(TranslationBuilder translationBuilder) {
        // Items
        translationBuilder.add(ItemInit.STEEL_INGOT, "Steel Ingot");

        // Blocks
        translationBuilder.add(BlockInit.STEEL_BLOCK, "Block of Steel");
        translationBuilder.add(BlockInit.ALLOY_FURNACE, "Alloy Furnace");
        translationBuilder.add(BlockInit.CRUSHER, "Crusher");
        translationBuilder.add(BlockInit.BASIC_BATTERY, "Basic Battery");
        translationBuilder.add(BlockInit.ADVANCED_BATTERY, "Advanced Battery");
        translationBuilder.add(BlockInit.ELITE_BATTERY, "Elite Battery");
        translationBuilder.add(BlockInit.ULTIMATE_BATTERY, "Ultimate Battery");
        translationBuilder.add(BlockInit.COMBUSTION_GENERATOR, "Combustion Generator");
        translationBuilder.add(BlockInit.CABLE, "Cable");
        translationBuilder.add(BlockInit.CABLE_FACADE, "Cable Facade");
        translationBuilder.add(BlockInit.SOLAR_PANEL, "Solar Panel");

        // Screens
        addComponent(translationBuilder, AlloyFurnaceBlockEntity.TITLE, "Alloy Furnace");
        addComponent(translationBuilder, CrusherBlockEntity.TITLE, "Crusher");
        addComponent(translationBuilder, CombustionGeneratorBlockEntity.TITLE, "Combustion Generator");
        addComponent(translationBuilder, SolarPanelBlockEntity.TITLE, "Solar Panel");

        // Creative Tabs
        addComponent(translationBuilder, CreativeTabInit.TAB_TITLE, "Tech Mod Testing");
    }
}
