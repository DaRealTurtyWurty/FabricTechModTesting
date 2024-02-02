package dev.turtywurty.fabrictechmodtesting.core.init;

import dev.turtywurty.fabrictechmodtesting.FabricTechModTesting;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;

public class ItemInit {
    public static final Item STEEL_INGOT = register("steel_ingot", new Item(defaultSettings()));

    public static <T extends Item> T register(String name, T item) {
        return Registry.register(BuiltInRegistries.ITEM, FabricTechModTesting.id(name), item);
    }

    public static Item.Properties defaultSettings() {
        return new FabricItemSettings();
    }

    public static void init() {
    }
}
