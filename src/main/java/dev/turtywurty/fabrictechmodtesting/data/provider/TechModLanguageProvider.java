package dev.turtywurty.fabrictechmodtesting.data.provider;

import dev.turtywurty.fabrictechmodtesting.common.blockentity.AlloyFurnaceBlockEntity;
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
        builder.add(((TranslatableContents)key.getContents()).getKey(), value);
    }

    @Override
    public void generateTranslations(TranslationBuilder translationBuilder) {
        // Items
        translationBuilder.add(ItemInit.STEEL_INGOT, "Steel Ingot");

        // Blocks
        translationBuilder.add(BlockInit.STEEL_BLOCK, "Block of Steel");
        translationBuilder.add(BlockInit.ALLOY_FURNACE, "Alloy Furnace");

        // Screens
        addComponent(translationBuilder, AlloyFurnaceBlockEntity.TITLE, "Alloy Furnace");
        addComponent(translationBuilder, CreativeTabInit.TAB_TITLE, "Tech Mod Testing");
    }
}
