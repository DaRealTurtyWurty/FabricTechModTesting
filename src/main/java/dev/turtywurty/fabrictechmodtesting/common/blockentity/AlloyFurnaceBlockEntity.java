package dev.turtywurty.fabrictechmodtesting.common.blockentity;

import dev.turtywurty.fabrictechmodtesting.FabricTechModTesting;
import dev.turtywurty.fabrictechmodtesting.common.blockentity.util.*;
import dev.turtywurty.fabrictechmodtesting.common.menu.AlloyFurnaceMenu;
import dev.turtywurty.fabrictechmodtesting.common.recipe.AlloyFurnaceRecipe;
import dev.turtywurty.fabrictechmodtesting.core.init.BlockEntityTypeInit;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.entity.FurnaceBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class AlloyFurnaceBlockEntity extends UpdatableBlockEntity implements TickableBlockEntity, ExtendedScreenHandlerFactory {
    public static final Component TITLE = Component.translatable("container." + FabricTechModTesting.MOD_ID + ".alloy_furnace");
    public static final int INPUT_SLOT_0 = 0, INPUT_SLOT_1 = 1, FUEL_SLOT = 2, OUTPUT_SLOT = 3;
    private final WrappedInventoryStorage<SimpleContainer> wrappedInventoryStorage = new WrappedInventoryStorage<>();
    private int progress, maxProgress, burnTime, maxBurnTime;
    private final ContainerData containerData = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> AlloyFurnaceBlockEntity.this.progress;
                case 1 -> AlloyFurnaceBlockEntity.this.maxProgress;
                case 2 -> AlloyFurnaceBlockEntity.this.burnTime;
                case 3 -> AlloyFurnaceBlockEntity.this.maxBurnTime;
                default -> throw new UnsupportedOperationException("Unsupported container data index: " + index);
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> AlloyFurnaceBlockEntity.this.progress = value;
                case 1 -> AlloyFurnaceBlockEntity.this.maxProgress = value;
                case 2 -> AlloyFurnaceBlockEntity.this.burnTime = value;
                case 3 -> AlloyFurnaceBlockEntity.this.maxBurnTime = value;
            }
        }

        @Override
        public int getCount() {
            return 4;
        }
    };
    private ResourceLocation currentRecipeId;

    public AlloyFurnaceBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(BlockEntityTypeInit.ALLOY_FURNACE, blockPos, blockState);

        this.wrappedInventoryStorage.addContainer(new SyncingSimpleContainer(this, 1), Direction.EAST);
        this.wrappedInventoryStorage.addContainer(new SyncingSimpleContainer(this, 1), Direction.WEST);
        this.wrappedInventoryStorage.addContainer(new PredicateSimpleContainer(this, (integer, itemStack) -> isFuel(itemStack), 1), Direction.UP);
        this.wrappedInventoryStorage.addContainer(new OutputSimpleContainer(this, 1), Direction.DOWN);
    }

    public static boolean isFuel(ItemStack stack) {
        return FurnaceBlockEntity.isFuel(stack);
    }

    public InventoryStorage getInventoryProvider(Direction direction) {
        return this.wrappedInventoryStorage.getStorage(direction);
    }

    @Override
    public void tick() {
        if (this.level == null || this.level.isClientSide)
            return;

        if (this.burnTime > 0) {
            this.burnTime--;
            update();
        }

        if (this.currentRecipeId == null) {
            Optional<RecipeHolder<AlloyFurnaceRecipe>> recipeHolder = getCurrentRecipe();
            if (recipeHolder.isPresent()) {
                this.currentRecipeId = recipeHolder.get().id();
                this.maxProgress = recipeHolder.get().value().cookTime();
                this.progress = 0;
                update();
            }

            return;
        }

        Optional<RecipeHolder<AlloyFurnaceRecipe>> currentRecipe = getCurrentRecipe();
        if (currentRecipe.isEmpty() || !currentRecipe.get().id().equals(this.currentRecipeId) || !canOutput(currentRecipe.get().value().output())) {
            reset();
            return;
        }

        if (this.burnTime <= 0) {
            ItemStack fuel = this.wrappedInventoryStorage.getContainer(FUEL_SLOT).getItem(0);
            if (isFuel(fuel)) {
                int burnTime = getBurnTime(fuel);
                this.maxBurnTime = burnTime;
                this.burnTime = burnTime;
                this.wrappedInventoryStorage.getContainer(FUEL_SLOT).removeItem(0, 1);
                update();
            } else {
                reset();
                this.maxBurnTime = 0;
                return;
            }
        }

        this.progress++;

        AlloyFurnaceRecipe recipe = currentRecipe.get().value();
        if (this.progress >= this.maxProgress) {
            this.progress = 0;
            this.maxProgress = 0;
            this.currentRecipeId = null;
            this.wrappedInventoryStorage.getContainer(INPUT_SLOT_0).removeItem(0, recipe.inputA().count());
            this.wrappedInventoryStorage.getContainer(INPUT_SLOT_1).removeItem(0, recipe.inputB().count());
            this.wrappedInventoryStorage.getContainer(OUTPUT_SLOT).addItem(recipe.output());
            update();
        }
    }

    private int getBurnTime(ItemStack fuel) {
        return FurnaceBlockEntity.getFuel().getOrDefault(fuel.getItem(), 1);
    }

    private boolean canOutput(ItemStack output) {
        return this.wrappedInventoryStorage.getContainer(OUTPUT_SLOT).canAddItem(output);
    }

    private void reset() {
        this.currentRecipeId = null;
        this.progress = 0;
        this.maxProgress = 0;
        update();
    }

    public SimpleContainer getInventory() {
        return new SimpleContainer(this.wrappedInventoryStorage.getStacks().toArray(new ItemStack[0]));
    }

    private Optional<RecipeHolder<AlloyFurnaceRecipe>> getCurrentRecipe() {
        if (this.level == null || this.level.isClientSide)
            return Optional.empty();

        var inventory = getInventory();
        return this.level.getRecipeManager().getRecipeFor(AlloyFurnaceRecipe.Type.INSTANCE, inventory, this.level);
    }

    @Override
    protected void saveAdditional(CompoundTag compoundTag) {
        super.saveAdditional(compoundTag);

        var modidData = new CompoundTag();
        modidData.putInt("Progress", this.progress);
        modidData.putInt("MaxProgress", this.maxProgress);
        modidData.putInt("BurnTime", this.burnTime);
        modidData.putInt("MaxBurnTime", this.maxBurnTime);
        modidData.putString("CurrentRecipe", this.currentRecipeId == null ? "" : this.currentRecipeId.toString());
        modidData.put("Inventory", this.wrappedInventoryStorage.writeNBT());

        compoundTag.put(FabricTechModTesting.MOD_ID, modidData);
    }

    @Override
    public void load(CompoundTag compoundTag) {
        super.load(compoundTag);

        if (!compoundTag.contains(FabricTechModTesting.MOD_ID, Tag.TAG_COMPOUND))
            return;

        CompoundTag modidData = compoundTag.getCompound(FabricTechModTesting.MOD_ID);
        if (modidData.contains("Progress", Tag.TAG_INT))
            this.progress = modidData.getInt("Progress");

        if (modidData.contains("MaxProgress", Tag.TAG_INT))
            this.maxProgress = modidData.getInt("MaxProgress");

        if (modidData.contains("BurnTime", Tag.TAG_INT))
            this.burnTime = modidData.getInt("BurnTime");

        if (modidData.contains("MaxBurnTime", Tag.TAG_INT))
            this.maxBurnTime = modidData.getInt("MaxBurnTime");

        if (modidData.contains("CurrentRecipe", Tag.TAG_STRING)) {
            String currentRecipe = modidData.getString("CurrentRecipe");
            this.currentRecipeId = currentRecipe.isEmpty() ? null : ResourceLocation.tryParse(currentRecipe);
        }

        if (modidData.contains("Inventory", Tag.TAG_LIST))
            this.wrappedInventoryStorage.readNBT(modidData.getList("Inventory", Tag.TAG_COMPOUND));
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        saveAdditional(tag);
        return tag;
    }

    @Override
    public @NotNull Component getDisplayName() {
        return TITLE;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new AlloyFurnaceMenu(id, inventory, this, this.containerData);
    }

    public WrappedInventoryStorage<SimpleContainer> getWrappedStorage() {
        return this.wrappedInventoryStorage;
    }

    @Override
    public void writeScreenOpeningData(ServerPlayer player, FriendlyByteBuf buf) {
        buf.writeBlockPos(this.worldPosition);
    }
}
