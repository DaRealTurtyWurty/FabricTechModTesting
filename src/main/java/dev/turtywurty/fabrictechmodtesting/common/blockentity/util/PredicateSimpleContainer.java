package dev.turtywurty.fabrictechmodtesting.common.blockentity.util;

import net.minecraft.world.item.ItemStack;

import java.util.function.BiPredicate;

public class PredicateSimpleContainer extends SyncingSimpleContainer {
    private final BiPredicate<Integer, ItemStack> predicate;

    public PredicateSimpleContainer(UpdatableBlockEntity blockEntity, BiPredicate<Integer, ItemStack> predicate, int size) {
        super(blockEntity, size);
        this.predicate = predicate;
    }

    public PredicateSimpleContainer(UpdatableBlockEntity blockEntity, BiPredicate<Integer, ItemStack> predicate, ItemStack... stacks) {
        super(blockEntity, stacks);
        this.predicate = predicate;
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack itemStack) {
        return this.predicate.test(slot, itemStack);
    }

    public BiPredicate<Integer, ItemStack> getPredicate() {
        return this.predicate;
    }
}
