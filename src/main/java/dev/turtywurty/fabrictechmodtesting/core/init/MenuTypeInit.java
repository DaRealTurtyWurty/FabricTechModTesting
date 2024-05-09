package dev.turtywurty.fabrictechmodtesting.core.init;

import dev.turtywurty.fabrictechmodtesting.FabricTechModTesting;
import dev.turtywurty.fabrictechmodtesting.common.menu.AlloyFurnaceMenu;
import dev.turtywurty.fabrictechmodtesting.common.menu.CombustionGeneratorMenu;
import dev.turtywurty.fabrictechmodtesting.common.menu.CrusherMenu;
import dev.turtywurty.fabrictechmodtesting.common.menu.SolarPanelMenu;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

public class MenuTypeInit {
    public static <T extends AbstractContainerMenu> MenuType<T> register(String name, MenuType.MenuSupplier<T> menuSupplier) {
        return Registry.register(BuiltInRegistries.MENU, FabricTechModTesting.id(name), new MenuType<>(menuSupplier, FeatureFlagSet.of()));
    }

    public static <T extends AbstractContainerMenu> ExtendedScreenHandlerType<T> registerExtended(String name, ExtendedScreenHandlerType.ExtendedFactory<T> menuSupplier) {
        return Registry.register(BuiltInRegistries.MENU, FabricTechModTesting.id(name), new ExtendedScreenHandlerType<>(menuSupplier));
    }

    public static void init() {
    }

    public static final ExtendedScreenHandlerType<AlloyFurnaceMenu> ALLOY_FURNACE =
            registerExtended("alloy_furnace", AlloyFurnaceMenu::new);

    public static final ExtendedScreenHandlerType<CrusherMenu> CRUSHER =
            registerExtended("crusher", CrusherMenu::new);

    public static final ExtendedScreenHandlerType<CombustionGeneratorMenu> COMBUSTION_GENERATOR =
            registerExtended("combustion_generator", CombustionGeneratorMenu::new);

    public static final ExtendedScreenHandlerType<SolarPanelMenu> SOLAR_PANEL =
            registerExtended("solar_panel", SolarPanelMenu::new);
}
