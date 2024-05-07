package dev.turtywurty.fabrictechmodtesting.common.menu.slot;

import net.minecraft.world.Container;

public class OutputSlot extends PredicateSlot {
    public OutputSlot(Container container, int index, int x, int y) {
        super(container, index, x, y, $ -> false);
    }
}
