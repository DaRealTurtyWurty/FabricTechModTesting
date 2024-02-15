package dev.turtywurty.fabrictechmodtesting.common.menu;

import dev.turtywurty.fabrictechmodtesting.common.blockentity.AlloyFurnaceBlockEntity;
import dev.turtywurty.fabrictechmodtesting.common.blockentity.util.WrappedStorage;
import dev.turtywurty.fabrictechmodtesting.common.recipe.AlloyFurnaceRecipe;
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

public class AlloyFurnaceMenu extends RecipeBookMenu<SimpleContainer> {
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

        WrappedStorage<SimpleContainer> wrappedStorage = alloyFurnaceBlockEntity.getWrappedStorage();
        wrappedStorage.checkSize(4);
        checkContainerDataCount(data, 4);

        wrappedStorage.startOpen(playerInv.player);

        this.blockEntity = alloyFurnaceBlockEntity;
        this.levelAccess = ContainerLevelAccess.create(this.blockEntity.getLevel(), this.blockEntity.getBlockPos());
        this.data = data;

        addOurSlots(wrappedStorage);
        addPlayerSlots(playerInv);

        addDataSlots(data);
    }

    private void addOurSlots(WrappedStorage<SimpleContainer> wrappedStorage) {
        addSlot(new Slot(wrappedStorage.getContainer(AlloyFurnaceBlockEntity.INPUT_SLOT_0), 0, 42, 17));
        addSlot(new Slot(wrappedStorage.getContainer(AlloyFurnaceBlockEntity.INPUT_SLOT_1), 0, 70, 17));
        addSlot(new Slot(wrappedStorage.getContainer(AlloyFurnaceBlockEntity.FUEL_SLOT), 0, 56, 53) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return this.container.canPlaceItem(0, stack);
            }
        });
        addSlot(new Slot(wrappedStorage.getContainer(AlloyFurnaceBlockEntity.OUTPUT_SLOT), 0, 116, 35) {
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
        this.blockEntity.getWrappedStorage().stopOpen(player);
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

    public int getBurnTime() {
        return this.data.get(2);
    }

    public int getMaxBurnTime() {
        return this.data.get(3);
    }

    public float getProgressPercent() {
        float progress = getProgress();
        float maxProgress = getMaxProgress();
        if (maxProgress == 0 || progress == 0)
            return 0.0F;

        return Mth.clamp(progress / maxProgress, 0.0F, 1.0F);
    }

    public float getBurnTimePercent() {
        float progress = getBurnTime();
        float maxProgress = getMaxBurnTime();
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
        getSlot(AlloyFurnaceBlockEntity.INPUT_SLOT_0).set(ItemStack.EMPTY);
        getSlot(AlloyFurnaceBlockEntity.INPUT_SLOT_1).set(ItemStack.EMPTY);
        getSlot(AlloyFurnaceBlockEntity.OUTPUT_SLOT).set(ItemStack.EMPTY);
    }

    @Override
    public boolean recipeMatches(RecipeHolder<? extends Recipe<SimpleContainer>> recipeHolder) {
        return recipeHolder.value().matches(this.blockEntity.getInventory(), this.blockEntity.getLevel());
    }

    @Override
    public int getResultSlotIndex() {
        return AlloyFurnaceBlockEntity.OUTPUT_SLOT;
    }

    @Override
    public int getGridWidth() {
        return 2;
    }

    @Override
    public int getGridHeight() {
        return 1;
    }

    @Override
    public int getSize() {
        return 4;
    }

    @Override
    public @NotNull RecipeBookType getRecipeBookType() {
        return RecipeBookType.valueOf("ALLOY_FURNACE");
    }

    @Override
    public boolean shouldMoveToInventory(int slot) {
        return slot != AlloyFurnaceBlockEntity.OUTPUT_SLOT;
    }

    public AlloyFurnaceBlockEntity getBlockEntity() {
        return blockEntity;
    }

    public void setRecipeUsed(RecipeHolder<AlloyFurnaceRecipe> matchingRecipe) {
        // TODO: Figure out what to do here
    }
}
