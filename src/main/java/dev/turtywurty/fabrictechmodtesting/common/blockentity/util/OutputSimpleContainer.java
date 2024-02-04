package dev.turtywurty.fabrictechmodtesting.common.blockentity.util;

import net.minecraft.world.item.ItemStack;

public class OutputSimpleContainer extends PredicateSimpleContainer {
    public OutputSimpleContainer(UpdatableBlockEntity blockEntity, int size) {
        super(blockEntity, (integer, itemStack) -> false, size);
    }

    public OutputSimpleContainer(UpdatableBlockEntity blockEntity, ItemStack... stacks) {
        super(blockEntity, (integer, itemStack) -> false, stacks);
    }
}
