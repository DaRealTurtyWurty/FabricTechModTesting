package dev.turtywurty.fabrictechmodtesting.common.block;

import dev.turtywurty.fabrictechmodtesting.common.blockentity.BatteryBlockEntity;
import dev.turtywurty.fabrictechmodtesting.common.blockentity.util.TickableBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class BatteryBlock extends Block implements EntityBlock {
    private final BatteryLevel level;

    public BatteryBlock(Properties properties, BatteryLevel level) {
        super(properties);
        this.level = level;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new BatteryBlockEntity(blockPos, blockState, this.level);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState blockState, BlockEntityType<T> blockEntityType) {
        return TickableBlockEntity.createTicker(level);
    }

    public enum BatteryLevel {
        BASIC(10000),
        ADVANCED(50000),
        ELITE(100000),
        ULTIMATE(500000),
        CREATIVE(Long.MAX_VALUE);

        private final long capacity;

        BatteryLevel(long capacity) {
            this.capacity = capacity;
        }

        public long getCapacity() {
            return this.capacity;
        }
    }
}
