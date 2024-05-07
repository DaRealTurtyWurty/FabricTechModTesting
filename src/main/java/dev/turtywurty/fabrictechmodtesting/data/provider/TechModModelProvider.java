package dev.turtywurty.fabrictechmodtesting.data.provider;

import dev.turtywurty.fabrictechmodtesting.core.init.BlockInit;
import dev.turtywurty.fabrictechmodtesting.core.init.ItemInit;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.data.models.model.ModelTemplates;
import net.minecraft.data.models.model.TexturedModel;

public class TechModModelProvider extends FabricModelProvider {
    public TechModModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators blockStateModelGenerator) {
        blockStateModelGenerator.createTrivialCube(BlockInit.STEEL_BLOCK);
        blockStateModelGenerator.createFurnace(BlockInit.ALLOY_FURNACE, TexturedModel.ORIENTABLE_ONLY_TOP);
        blockStateModelGenerator.createHorizontallyRotatedBlock(BlockInit.CRUSHER, TexturedModel.ORIENTABLE_ONLY_TOP); // TODO: Custom java model
        blockStateModelGenerator.createTrivialCube(BlockInit.BASIC_BATTERY);
        blockStateModelGenerator.createTrivialCube(BlockInit.ADVANCED_BATTERY);
        blockStateModelGenerator.createTrivialCube(BlockInit.ELITE_BATTERY);
        blockStateModelGenerator.createTrivialCube(BlockInit.ULTIMATE_BATTERY);
        blockStateModelGenerator.createFurnace(BlockInit.COMBUSTION_GENERATOR, TexturedModel.ORIENTABLE_ONLY_TOP);
    }

    @Override
    public void generateItemModels(ItemModelGenerators itemModelGenerator) {
        itemModelGenerator.generateFlatItem(ItemInit.STEEL_INGOT, ModelTemplates.FLAT_ITEM);
    }
}
