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
    }
}
