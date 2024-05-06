package dev.turtywurty.fabrictechmodtesting.common.menu;

import dev.turtywurty.fabrictechmodtesting.common.blockentity.CrusherBlockEntity;
import dev.turtywurty.fabrictechmodtesting.common.blockentity.util.WrappedInventoryStorage;
import dev.turtywurty.fabrictechmodtesting.common.menu.slot.OutputSlot;
import dev.turtywurty.fabrictechmodtesting.common.recipe.CrusherRecipe;
import dev.turtywurty.fabrictechmodtesting.core.init.BlockInit;
import dev.turtywurty.fabrictechmodtesting.core.init.MenuTypeInit;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;

public class CrusherMenu extends RecipeBookMenu<SimpleContainer> {
    private final CrusherBlockEntity blockEntity;
    private final ContainerLevelAccess levelAccess;
    private final ContainerData data;

    public CrusherMenu(int id, Inventory playerInv, FriendlyByteBuf buf) {
        this(id, playerInv, playerInv.player.level().getBlockEntity(buf.readBlockPos()), new SimpleContainerData(4));
    }

    public CrusherMenu(int id, Inventory playerInv, BlockEntity blockEntity, ContainerData data) {
        super(MenuTypeInit.CRUSHER, id);
        if (!(blockEntity instanceof CrusherBlockEntity crusherBlockEntity))
            throw new IllegalArgumentException("Block entity is not an instance of CrusherBlockEntity!");

        WrappedInventoryStorage<SimpleContainer> wrappedStorage = crusherBlockEntity.getInventoryStorage();
        wrappedStorage.checkSize(3);
        checkContainerDataCount(data, 2);

        wrappedStorage.startOpen(playerInv.player);

        this.blockEntity = crusherBlockEntity;
        this.levelAccess = ContainerLevelAccess.create(this.blockEntity.getLevel(), this.blockEntity.getBlockPos());
        this.data = data;

        addOurSlots(wrappedStorage);
        addPlayerSlots(playerInv);

        addDataSlots(data);
    }

    private void addOurSlots(WrappedInventoryStorage<SimpleContainer> wrappedStorage) {
        addSlot(new Slot(wrappedStorage.getContainer(CrusherBlockEntity.INPUT_SLOT), 0, 44, 35));
        addSlot(new OutputSlot(wrappedStorage.getContainer(CrusherBlockEntity.OUTPUT_SLOT), 0, 98, 35));
        addSlot(new OutputSlot(wrappedStorage.getContainer(CrusherBlockEntity.OUTPUT_SLOT), 1, 116, 35));
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

            if (slotIndex < 3) {
                if (!moveItemStackTo(slotStack, 3, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!moveItemStackTo(slotStack, 0, 3, false)) {
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
        this.blockEntity.getInventoryStorage().stopOpen(player);
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(this.levelAccess, player, BlockInit.CRUSHER);
    }

    public int getProgress() {
        return this.data.get(0);
    }

    public int getMaxProgress() {
        return this.data.get(1);
    }

    public float getProgressPercent() {
        float progress = getProgress();
        float maxProgress = getMaxProgress();
        if (maxProgress == 0 || progress == 0)
            return 0.0F;

        return Mth.clamp(progress / maxProgress, 0.0F, 1.0F);
    }

    @Override
    public void fillCraftSlotsStackedContents(StackedContents stackedContents) {
        this.blockEntity.getInventory().fillStackedContents(stackedContents);
    }

    @Override
    public void clearCraftingContent() {
        getSlot(CrusherBlockEntity.INPUT_SLOT).set(ItemStack.EMPTY);
        getSlot(CrusherBlockEntity.OUTPUT_SLOT).set(ItemStack.EMPTY);
        getSlot(CrusherBlockEntity.OUTPUT_SLOT + 1).set(ItemStack.EMPTY);
    }

    @Override
    public boolean recipeMatches(RecipeHolder<? extends Recipe<SimpleContainer>> recipeHolder) {
        return recipeHolder.value().matches(this.blockEntity.getInventory(), this.blockEntity.getLevel());
    }

    @Override
    public int getResultSlotIndex() {
        return CrusherBlockEntity.OUTPUT_SLOT; // TODO: Find a way to make this work for multiple output slots
    }

    @Override
    public int getGridWidth() {
        return 1;
    }

    @Override
    public int getGridHeight() {
        return 1;
    }

    @Override
    public int getSize() {
        return 3;
    }

    @Override
    public @NotNull RecipeBookType getRecipeBookType() {
        return RecipeBookType.valueOf("CRUSHER");
    }

    @Override
    public boolean shouldMoveToInventory(int index) {
        return index != CrusherBlockEntity.OUTPUT_SLOT && index != CrusherBlockEntity.OUTPUT_SLOT + 1;
    }

    public CrusherBlockEntity getBlockEntity() {
        return blockEntity;
    }

    public void setRecipeUsed(RecipeHolder<CrusherRecipe> matchingRecipe) {
        // TODO: Figure out what to do here
    }
}
