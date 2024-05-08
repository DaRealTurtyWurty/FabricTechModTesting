package dev.turtywurty.fabrictechmodtesting.common.block;

import dev.turtywurty.fabrictechmodtesting.common.blockentity.CableFacadeBlockEntity;
import dev.turtywurty.fabrictechmodtesting.common.item.CableFacadeBlockItem;
import dev.turtywurty.fabrictechmodtesting.core.init.BlockEntityTypeInit;
import dev.turtywurty.fabrictechmodtesting.core.init.BlockInit;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CableFacadeBlock extends CableBlock {
    public CableFacadeBlock(Properties properties) {
        super(properties);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return BlockEntityTypeInit.CABLE_FACADE.create(blockPos, blockState);
    }

    @Override
    public void playerDestroy(@NotNull Level level, @NotNull Player player, @NotNull BlockPos pos, @NotNull BlockState state, @Nullable BlockEntity blockEntity, @NotNull ItemStack stack) {
        var item = new ItemStack(BlockInit.CABLE_FACADE);

        BlockState mimicBlock;
        if (blockEntity instanceof CableFacadeBlockEntity cableFacadeBlockEntity) {
            mimicBlock = cableFacadeBlockEntity.getMimicBlock();
        } else {
            mimicBlock = Blocks.STONE.defaultBlockState();
        }

        CableFacadeBlockItem.setMimicBlock(item, mimicBlock);
        popResource(level, pos, item);
    }

    @Override
    public @NotNull BlockState playerWillDestroy(Level level, BlockPos pos, BlockState blockState, Player player) {
        BlockState defaultState = BlockInit.CABLE_FACADE.defaultBlockState();
        BlockState newState = CableBlock.calculateState(level, pos, defaultState);
        level.setBlock(pos, newState, level.isClientSide() ? Block.UPDATE_ALL + Block.UPDATE_IMMEDIATE : Block.UPDATE_ALL);
        return newState;
    }
}
