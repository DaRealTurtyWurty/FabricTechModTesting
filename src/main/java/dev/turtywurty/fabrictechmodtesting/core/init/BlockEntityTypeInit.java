package dev.turtywurty.fabrictechmodtesting.core.init;

import dev.turtywurty.fabrictechmodtesting.FabricTechModTesting;
import dev.turtywurty.fabrictechmodtesting.common.blockentity.AlloyFurnaceBlockEntity;
import dev.turtywurty.fabrictechmodtesting.common.blockentity.CrusherBlockEntity;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class BlockEntityTypeInit {
    public static final BlockEntityType<AlloyFurnaceBlockEntity> ALLOY_FURNACE =
            register("alloy_furnace", AlloyFurnaceBlockEntity::new, BlockInit.ALLOY_FURNACE);

    public static final BlockEntityType<CrusherBlockEntity> CRUSHER =
            register("crusher", CrusherBlockEntity::new, BlockInit.CRUSHER);

    public static <T extends BlockEntity> BlockEntityType<T> register(String name, BlockEntityType.BlockEntitySupplier<T> blockEntitySupplier, Block... blocks) {
        return Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, FabricTechModTesting.id(name),
                BlockEntityType.Builder.of(blockEntitySupplier, blocks).build(null));
    }

    public static void init() {
    }
}
