package dev.turtywurty.fabrictechmodtesting.common.menu;

import dev.turtywurty.fabrictechmodtesting.common.blockentity.SolarPanelBlockEntity;
import dev.turtywurty.fabrictechmodtesting.common.blockentity.util.WrappedInventoryStorage;
import dev.turtywurty.fabrictechmodtesting.common.menu.slot.PredicateSlot;
import dev.turtywurty.fabrictechmodtesting.core.init.BlockInit;
import dev.turtywurty.fabrictechmodtesting.core.init.MenuTypeInit;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;

public class SolarPanelMenu extends AbstractContainerMenu {
    private final SolarPanelBlockEntity blockEntity;
    private final ContainerLevelAccess levelAccess;

    public SolarPanelMenu(int id, Inventory playerInv, FriendlyByteBuf buf) {
        this(id, playerInv, playerInv.player.level().getBlockEntity(buf.readBlockPos()));
    }

    public SolarPanelMenu(int id, Inventory playerInv, BlockEntity blockEntity) {
        super(MenuTypeInit.SOLAR_PANEL, id);
        if (!(blockEntity instanceof SolarPanelBlockEntity solarPanelBlockEntity))
            throw new IllegalArgumentException("Block entity is not an instance of SolarPanelBlockEntity!");

        WrappedInventoryStorage<SimpleContainer> wrappedStorage = solarPanelBlockEntity.getWrappedInventory();
        wrappedStorage.checkSize(1);

        wrappedStorage.startOpen(playerInv.player);

        this.blockEntity = solarPanelBlockEntity;
        this.levelAccess = ContainerLevelAccess.create(this.blockEntity.getLevel(), this.blockEntity.getBlockPos());

        addOurSlots(wrappedStorage);
        addPlayerSlots(playerInv);
    }

    private void addOurSlots(WrappedInventoryStorage<SimpleContainer> wrappedStorage) {
        SimpleContainer container = wrappedStorage.getContainer(0);
        if (container == null)
            throw new IllegalStateException("Wrapped inventory storage does not have a container at index 0!");

        addSlot(new PredicateSlot(container, 0, 81, 36));
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

            if (slotIndex < 1) {
                if (!moveItemStackTo(slotStack, 1, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!moveItemStackTo(slotStack, 0, 1, false)) {
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
        this.blockEntity.getWrappedInventory().stopOpen(player);
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(this.levelAccess, player, BlockInit.SOLAR_PANEL);
    }

    public SolarPanelBlockEntity getBlockEntity() {
        return blockEntity;
    }

    public long getEnergy() {
        return this.blockEntity.getEnergy().getAmount();
    }

    public long getMaxEnergy() {
        return this.blockEntity.getEnergy().getCapacity();
    }

    public float getEnergyPercent() {
        long energy = getEnergy();
        long maxEnergy = getMaxEnergy();
        if (maxEnergy == 0 || energy == 0)
            return 0.0F;

        return Math.max(0, Math.min(1.0F, (float) energy / maxEnergy));
    }

    public int getEnergyOutput() {
        return this.blockEntity.getEnergyOutput();
    }

    public float getEnergyOutputPercent() {
        int output = getEnergyOutput();
        if (output == 0)
            return 0.0F;

        return Math.max(0, Math.min(1.0F, output / 35.0F));
    }
}
