package dev.turtywurty.fabrictechmodtesting.common.menu;

import dev.turtywurty.fabrictechmodtesting.common.blockentity.AlloyFurnaceBlockEntity;
import dev.turtywurty.fabrictechmodtesting.core.init.BlockInit;
import dev.turtywurty.fabrictechmodtesting.core.init.MenuTypeInit;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;

public class AlloyFurnaceMenu extends AbstractContainerMenu {
    private final AlloyFurnaceBlockEntity blockEntity;
    private final ContainerLevelAccess levelAccess;
    private final ContainerData data;

    public AlloyFurnaceMenu(int id, Inventory playerInv, FriendlyByteBuf buf) {
        this(id, playerInv, playerInv.player.level().getBlockEntity(buf.readBlockPos()), new SimpleContainerData(4));
    }

    public AlloyFurnaceMenu(int id, Inventory playerInv, BlockEntity blockEntity, ContainerData data) {
        super(MenuTypeInit.ALLOY_FURNACE, id);
        if (!(blockEntity instanceof AlloyFurnaceBlockEntity alloyFurnaceBlockEntity))
            throw new IllegalArgumentException("Block entity is not an instance of AlloyFurnaceBlockEntity!");

        Container inputSlot0 = alloyFurnaceBlockEntity.getInputSlot0();
        Container inputSlot1 = alloyFurnaceBlockEntity.getInputSlot1();
        Container fuelSlot = alloyFurnaceBlockEntity.getFuelSlot();
        Container outputSlot = alloyFurnaceBlockEntity.getOutputSlot();
        checkContainerSize(inputSlot0, 1);
        checkContainerSize(inputSlot1, 1);
        checkContainerSize(fuelSlot, 1);
        checkContainerSize(outputSlot, 1);
        checkContainerDataCount(data, 4);

        inputSlot0.startOpen(playerInv.player);
        inputSlot1.startOpen(playerInv.player);
        fuelSlot.startOpen(playerInv.player);
        outputSlot.startOpen(playerInv.player);

        this.blockEntity = alloyFurnaceBlockEntity;
        this.levelAccess = ContainerLevelAccess.create(this.blockEntity.getLevel(), this.blockEntity.getBlockPos());
        this.data = data;

        addOurSlots(inputSlot0, inputSlot1, fuelSlot, outputSlot);
        addPlayerSlots(playerInv);

        addDataSlots(data);
    }

    private void addOurSlots(Container inputSlot0, Container inputSlot1, Container fuelSlot, Container outputSlot) {
        addSlot(new Slot(inputSlot0, 0, 42, 17));
        addSlot(new Slot(inputSlot1, 0, 70, 17));
        addSlot(new Slot(fuelSlot, 0, 56, 53) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return this.container.canPlaceItem(0, stack);
            }
        });
        addSlot(new Slot(outputSlot, 0, 116, 35) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return this.container.canPlaceItem(0, stack);
            }
        });
    }

    private void addPlayerSlots(Inventory playerInv) {
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                addSlot(new Slot(playerInv,
                        9 + column + (row * 9),
                        8 + (column * 18),
                        84 + (row * 18)));
            }
        }

        for (int column = 0; column < 9; column++) {
            addSlot(new Slot(playerInv,
                    column,
                    8 + (column * 18),
                    142));
        }
    }

    @Override
    public @NotNull ItemStack quickMoveStack(Player player, int slotIndex) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(slotIndex);

        if (slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            itemStack = slotStack.copy();

            if (slotIndex < 4) {
                if (!moveItemStackTo(slotStack, 4, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!moveItemStackTo(slotStack, 0, 4, false)) {
                return ItemStack.EMPTY;
            }

            if (slotStack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return itemStack;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        this.blockEntity.getInputSlot0().stopOpen(player);
        this.blockEntity.getInputSlot1().stopOpen(player);
        this.blockEntity.getFuelSlot().stopOpen(player);
        this.blockEntity.getOutputSlot().stopOpen(player);
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(this.levelAccess, player, BlockInit.ALLOY_FURNACE);
    }

    public int getProgress() {
        return this.data.get(0);
    }

    public int getMaxProgress() {
        return this.data.get(1);
    }

    public int getFuelProgress() {
        return this.data.get(2);
    }

    public int getMaxFuelProgress() {
        return this.data.get(3);
    }

    public float getProgressPercent() {
        float progress = getProgress();
        float maxProgress = getMaxProgress();
        if (maxProgress == 0)
            return 0.0F;

        return Mth.clamp(progress / maxProgress, 0.0F, 1.0F);
    }

    public float getFuelProgressPercent() {
        float progress = getFuelProgress();
        float maxProgress = getMaxFuelProgress();
        if (maxProgress == 0)
            return 0.0F;

        return Mth.clamp(progress / maxProgress, 0.0F, 1.0F);
    }
}
