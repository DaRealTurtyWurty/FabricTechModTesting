package dev.turtywurty.fabrictechmodtesting.common.blockentity;

import dev.turtywurty.fabrictechmodtesting.FabricTechModTesting;
import dev.turtywurty.fabrictechmodtesting.common.blockentity.util.TickableBlockEntity;
import dev.turtywurty.fabrictechmodtesting.core.init.BlockEntityTypeInit;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CrusherBlockEntity extends BlockEntity implements TickableBlockEntity, MenuProvider {
    public static final Component TITLE = Component.translatable("container." + FabricTechModTesting.MOD_ID + ".crusher");

    public CrusherBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(BlockEntityTypeInit.ALLOY_FURNACE, blockPos, blockState);
    }

    @Override
    public void tick() {

    }

    @Override
    public @NotNull Component getDisplayName() {
        return TITLE;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        return new CrusherMenu(i, inventory, this, this.containerData);
    }
}
