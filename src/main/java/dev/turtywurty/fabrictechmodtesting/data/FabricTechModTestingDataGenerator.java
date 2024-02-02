package dev.turtywurty.fabrictechmodtesting.data;

import dev.turtywurty.fabrictechmodtesting.data.provider.TechModBlockLootTableProvider;
import dev.turtywurty.fabrictechmodtesting.data.provider.TechModLanguageProvider;
import dev.turtywurty.fabrictechmodtesting.data.provider.TechModModelProvider;
import dev.turtywurty.fabrictechmodtesting.data.provider.TechModRecipeProvider;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class FabricTechModTestingDataGenerator implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator generator) {
        FabricDataGenerator.Pack pack = generator.createPack();
        pack.addProvider(TechModModelProvider::new);
        pack.addProvider(TechModBlockLootTableProvider::new);
        pack.addProvider(TechModRecipeProvider::new);
        pack.addProvider(TechModLanguageProvider::new);
    }
}
