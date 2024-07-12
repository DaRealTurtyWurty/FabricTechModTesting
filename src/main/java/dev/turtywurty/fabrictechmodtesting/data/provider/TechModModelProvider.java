package dev.turtywurty.fabrictechmodtesting.data.provider;

import dev.turtywurty.fabrictechmodtesting.common.block.BatteryBlock;
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
        createBattery(blockStateModelGenerator, BlockInit.BASIC_BATTERY);
        createBattery(blockStateModelGenerator, BlockInit.ADVANCED_BATTERY);
        createBattery(blockStateModelGenerator, BlockInit.ELITE_BATTERY);
        createBattery(blockStateModelGenerator, BlockInit.ULTIMATE_BATTERY);
        blockStateModelGenerator.createFurnace(BlockInit.COMBUSTION_GENERATOR, TexturedModel.ORIENTABLE_ONLY_TOP);
    }

    private void createBattery(BlockModelGenerators blockStateModelGenerator, BatteryBlock block) {
        blockStateModelGenerator.woodProvider(block).log(block);
    }

    @Override
    public void generateItemModels(ItemModelGenerators itemModelGenerator) {
        itemModelGenerator.generateFlatItem(ItemInit.STEEL_INGOT, ModelTemplates.FLAT_ITEM);
    }
}
