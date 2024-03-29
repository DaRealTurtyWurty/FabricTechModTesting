package dev.turtywurty.fabrictechmodtesting.core.init;

import dev.turtywurty.fabrictechmodtesting.FabricTechModTesting;
import dev.turtywurty.fabrictechmodtesting.common.block.AlloyFurnaceBlock;
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
