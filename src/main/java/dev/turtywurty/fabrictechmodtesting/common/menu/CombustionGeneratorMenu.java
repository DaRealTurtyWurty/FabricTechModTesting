package dev.turtywurty.fabrictechmodtesting.common.menu;

import dev.turtywurty.fabrictechmodtesting.common.blockentity.CombustionGeneratorBlockEntity;
import dev.turtywurty.fabrictechmodtesting.common.blockentity.util.WrappedInventoryStorage;
import dev.turtywurty.fabrictechmodtesting.common.menu.slot.PredicateSlot;
import dev.turtywurty.fabrictechmodtesting.core.init.BlockInit;
import dev.turtywurty.fabrictechmodtesting.core.init.MenuTypeInit;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;

public class CombustionGeneratorMenu extends AbstractContainerMenu {
    private final CombustionGeneratorBlockEntity blockEntity;
    private final ContainerLevelAccess levelAccess;
    private final ContainerData containerData;

    public CombustionGeneratorMenu(int id, Inventory playerInv, FriendlyByteBuf buf) {
        this(id, playerInv, playerInv.player.level().getBlockEntity(buf.readBlockPos()), new SimpleContainerData(2));
    }

    public CombustionGeneratorMenu(int id, Inventory playerInv, BlockEntity blockEntity, ContainerData containerData) {
        super(MenuTypeInit.COMBUSTION_GENERATOR, id);
        if (!(blockEntity instanceof CombustionGeneratorBlockEntity combustionGeneratorBlockEntity))
            throw new IllegalArgumentException("Block entity is not an instance of CombustionGeneratorBlockEntity!");

        WrappedInventoryStorage<SimpleContainer> wrappedStorage = combustionGeneratorBlockEntity.getWrappedInventory();
        wrappedStorage.checkSize(1);
        checkContainerDataCount(containerData, 2);

        wrappedStorage.startOpen(playerInv.player);

        this.blockEntity = combustionGeneratorBlockEntity;
        this.levelAccess = ContainerLevelAccess.create(this.blockEntity.getLevel(), this.blockEntity.getBlockPos());
        this.containerData = containerData;

        addOurSlots(wrappedStorage);
        addPlayerSlots(playerInv);

        addDataSlots(containerData);
    }

    private void addOurSlots(WrappedInventoryStorage<SimpleContainer> wrappedStorage) {
        addSlot(new PredicateSlot(wrappedStorage.getContainer(0), 0, 81, 42, CombustionGeneratorBlockEntity::isFuel));
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
        return stillValid(this.levelAccess, player, BlockInit.COMBUSTION_GENERATOR);
    }

    public int getBurnTime() {
        return this.containerData.get(0);
    }

    public int getMaxBurnTime() {
        return this.containerData.get(1);
    }

    public float getBurnTimePercent() {
        float burnTime = getBurnTime();
        float maxBurnTime = getMaxBurnTime();
        if (maxBurnTime == 0 || burnTime == 0) return 0;

        return Mth.clamp(burnTime / maxBurnTime, 0.0F, 1.0F);
    }

    public CombustionGeneratorBlockEntity getBlockEntity() {
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
}
