package dev.turtywurty.fabrictechmodtesting.common.blockentity.util;

import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;

public class SyncingSimpleContainer extends SimpleContainer {
    private final UpdatableBlockEntity blockEntity;

    public SyncingSimpleContainer(UpdatableBlockEntity blockEntity, int size) {
        super(size);
        this.blockEntity = blockEntity;
    }

    public SyncingSimpleContainer(UpdatableBlockEntity blockEntity, ItemStack... stacks) {
        super(stacks);
        this.blockEntity = blockEntity;
    }

    @Override
    public void setChanged() {
        super.setChanged();
        this.blockEntity.update();
    }

    public UpdatableBlockEntity getBlockEntity() {
        return this.blockEntity;
    }
}
