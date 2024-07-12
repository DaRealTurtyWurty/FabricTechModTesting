package dev.turtywurty.fabrictechmodtesting.core.init;

import dev.turtywurty.fabrictechmodtesting.FabricTechModTesting;
import dev.turtywurty.fabrictechmodtesting.common.block.*;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class BlockInit {
    public static final Block STEEL_BLOCK = registerWithItem("steel_block",
            new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)));

    public static final AlloyFurnaceBlock ALLOY_FURNACE = registerWithItem("alloy_furnace",
            new AlloyFurnaceBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.FURNACE)));

    public static final CrusherBlock CRUSHER = registerWithItem("crusher",
            new CrusherBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.FURNACE).noOcclusion().lightLevel($ -> 0)));

    public static final BatteryBlock BASIC_BATTERY = registerWithItem("basic_battery",
            new BatteryBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK), BatteryBlock.BatteryLevel.BASIC));

    public static final BatteryBlock ADVANCED_BATTERY = registerWithItem("advanced_battery",
            new BatteryBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK), BatteryBlock.BatteryLevel.ADVANCED));

    public static final BatteryBlock ELITE_BATTERY = registerWithItem("elite_battery",
            new BatteryBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK), BatteryBlock.BatteryLevel.ELITE));

    public static final BatteryBlock ULTIMATE_BATTERY = registerWithItem("ultimate_battery",
            new BatteryBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK), BatteryBlock.BatteryLevel.ULTIMATE));

    public static final CombustionGeneratorBlock COMBUSTION_GENERATOR = registerWithItem("combustion_generator",
            new CombustionGeneratorBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.FURNACE)));

    public static final CableBlock CABLE = registerWithItem("cable",
            new CableBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).noOcclusion()));

    public static final CableFacadeBlock CABLE_FACADE = register("cable_facade",
            new CableFacadeBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).noOcclusion()));

    public static final SolarPanelBlock SOLAR_PANEL = registerWithItem("solar_panel",
            new SolarPanelBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).noOcclusion()));

    public static final WindTurbineBlock WIND_TURBINE = registerWithItem("wind_turbine",
            new WindTurbineBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).noOcclusion()));

    public static <T extends Block> T register(String name, T block) {
        return Registry.register(BuiltInRegistries.BLOCK, FabricTechModTesting.id(name), block);
    }

    public static <T extends Block> T registerWithItem(String name, T block) {
        T registered = register(name, block);
        ItemInit.register(name, new BlockItem(registered, ItemInit.defaultSettings()));
        return registered;
    }

    public static void init() {
    }
}
