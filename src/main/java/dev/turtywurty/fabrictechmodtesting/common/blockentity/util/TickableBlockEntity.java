package dev.turtywurty.fabrictechmodtesting.common.blockentity.util;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;

public interface TickableBlockEntity {
    static <T extends BlockEntity> BlockEntityTicker<T> createTicker(Level pLevel) {
        return !pLevel.isClientSide() ? (level, blockPos, blockState, blockEntity) -> ((TickableBlockEntity) blockEntity).tick() : null;
    }

    void tick();
}
