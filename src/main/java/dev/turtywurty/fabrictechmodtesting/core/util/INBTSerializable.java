package dev.turtywurty.fabrictechmodtesting.core.util;

import net.minecraft.nbt.Tag;

public interface INBTSerializable<T extends Tag> {
    T writeNBT();

    void readNBT(T nbt);
}
