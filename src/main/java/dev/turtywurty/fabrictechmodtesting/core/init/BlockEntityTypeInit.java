package dev.turtywurty.fabrictechmodtesting.core.init;

import dev.turtywurty.fabrictechmodtesting.FabricTechModTesting;
import dev.turtywurty.fabrictechmodtesting.common.block.BatteryBlock;
import dev.turtywurty.fabrictechmodtesting.common.blockentity.*;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiCache;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class BlockEntityTypeInit {
    public static <T extends BlockEntity> BlockEntityType<T> register(String name, BlockEntityType.BlockEntitySupplier<T> blockEntitySupplier, Block... blocks) {
        return Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, FabricTechModTesting.id(name),
                BlockEntityType.Builder.of(blockEntitySupplier, blocks).build(null));
    }

    public static void init() {
    }

    public static final BlockEntityType<AlloyFurnaceBlockEntity> ALLOY_FURNACE =
            register("alloy_furnace", AlloyFurnaceBlockEntity::new, BlockInit.ALLOY_FURNACE);

    public static final BlockEntityType<CrusherBlockEntity> CRUSHER =
            register("crusher", CrusherBlockEntity::new, BlockInit.CRUSHER);

    public static final BlockEntityType<BatteryBlockEntity> BATTERY =
            register("battery", (level, state) -> new BatteryBlockEntity(level, state, BatteryBlock.BatteryLevel.BASIC),
                    BlockInit.BASIC_BATTERY, BlockInit.ADVANCED_BATTERY, BlockInit.ELITE_BATTERY, BlockInit.ULTIMATE_BATTERY);

    public static final BlockEntityType<CombustionGeneratorBlockEntity> COMBUSTION_GENERATOR =
            register("combustion_generator", CombustionGeneratorBlockEntity::new, BlockInit.COMBUSTION_GENERATOR);

    public static final BlockEntityType<CableBlockEntity> CABLE =
            register("cable", CableBlockEntity::new, BlockInit.CABLE);

    public static final BlockEntityType<CableFacadeBlockEntity> CABLE_FACADE =
            register("cable_facade", CableFacadeBlockEntity::new, BlockInit.CABLE_FACADE);

    public static final BlockEntityType<SolarPanelBlockEntity> SOLAR_PANEL =
            register("solar_panel", SolarPanelBlockEntity::new, BlockInit.SOLAR_PANEL);
}
