package dev.turtywurty.fabrictechmodtesting.core.init;

import dev.turtywurty.fabrictechmodtesting.FabricTechModTesting;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;

import java.util.Optional;

public class CreativeTabInit {
    public static final Component TAB_TITLE = Component.translatable(FabricTechModTesting.id("creative_tab").toString());

    public static final CreativeModeTab TAB = register("creative_tab",
            FabricItemGroup.builder()
                    .title(TAB_TITLE)
                    .displayItems((itemDisplayParameters, output) -> BuiltInRegistries.ITEM.keySet()
                            .stream()
                            .filter(key -> key.getNamespace().equals(FabricTechModTesting.MOD_ID))
                            .map(BuiltInRegistries.ITEM::getOptional)
                            .map(Optional::orElseThrow)
                            .forEach(output::accept))
                    .icon(ItemInit.STEEL_INGOT::getDefaultInstance)
                    .build());

    public static <T extends CreativeModeTab> T register(String name, T creativeTab) {
        return Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, FabricTechModTesting.id(name), creativeTab);
    }

    public static void init() {
    }
}
