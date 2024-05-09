package dev.turtywurty.fabrictechmodtesting.common.menu.slot;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.function.Predicate;

public class PredicateSlot extends Slot {
    private final Predicate<ItemStack> predicate;

    public PredicateSlot(Container container, int index, int x, int y, Predicate<ItemStack> predicate) {
        super(container, index, x, y);
        this.predicate = predicate;
    }

    public PredicateSlot(SimpleContainer container, int index, int x, int y) {
        this(container, index, x, y, container::canAddItem);
    }

    @Override
    public boolean mayPlace(ItemStack itemStack) {
        return this.predicate.test(itemStack);
    }
}
