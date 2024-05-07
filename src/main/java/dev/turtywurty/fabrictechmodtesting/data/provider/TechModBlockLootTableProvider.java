package dev.turtywurty.fabrictechmodtesting.data.provider;

import dev.turtywurty.fabrictechmodtesting.core.init.BlockInit;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;

public class TechModBlockLootTableProvider extends FabricBlockLootTableProvider {
    public TechModBlockLootTableProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generate() {
        dropSelf(BlockInit.STEEL_BLOCK);
        dropSelf(BlockInit.ALLOY_FURNACE);
        dropSelf(BlockInit.CRUSHER);
        dropSelf(BlockInit.BASIC_BATTERY);
        dropSelf(BlockInit.ADVANCED_BATTERY);
        dropSelf(BlockInit.ELITE_BATTERY);
        dropSelf(BlockInit.ULTIMATE_BATTERY);
        dropSelf(BlockInit.COMBUSTION_GENERATOR);
    }
}
