package dev.turtywurty.fabrictechmodtesting.core.init;

import dev.turtywurty.fabrictechmodtesting.FabricTechModTesting;
import dev.turtywurty.fabrictechmodtesting.common.block.BatteryBlock;
import dev.turtywurty.fabrictechmodtesting.common.blockentity.AlloyFurnaceBlockEntity;
import dev.turtywurty.fabrictechmodtesting.common.blockentity.BatteryBlockEntity;
import dev.turtywurty.fabrictechmodtesting.common.blockentity.CombustionGeneratorBlockEntity;
import dev.turtywurty.fabrictechmodtesting.common.blockentity.CrusherBlockEntity;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class BlockEntityTypeInit {
    public static final BlockEntityType<BatteryBlockEntity> BATTERY =
            register("battery", (level, state) -> new BatteryBlockEntity(level, state, BatteryBlock.BatteryLevel.BASIC),
                    BlockInit.BASIC_BATTERY, BlockInit.ADVANCED_BATTERY, BlockInit.ELITE_BATTERY, BlockInit.ULTIMATE_BATTERY);    public static final BlockEntityType<AlloyFurnaceBlockEntity> ALLOY_FURNACE =
            register("alloy_furnace", AlloyFurnaceBlockEntity::new, BlockInit.ALLOY_FURNACE);

    public static <T extends BlockEntity> BlockEntityType<T> register(String name, BlockEntityType.BlockEntitySupplier<T> blockEntitySupplier, Block... blocks) {
        return Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, FabricTechModTesting.id(name),
                BlockEntityType.Builder.of(blockEntitySupplier, blocks).build(null));
    }    public static final BlockEntityType<CrusherBlockEntity> CRUSHER =
            register("crusher", CrusherBlockEntity::new, BlockInit.CRUSHER);

    public static void init() {
    }

    public static final BlockEntityType<CombustionGeneratorBlockEntity> COMBUSTION_GENERATOR =
            register("combustion_generator", CombustionGeneratorBlockEntity::new, BlockInit.COMBUSTION_GENERATOR);




}
