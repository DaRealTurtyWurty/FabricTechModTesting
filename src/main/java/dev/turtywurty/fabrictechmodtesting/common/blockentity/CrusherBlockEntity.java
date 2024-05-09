package dev.turtywurty.fabrictechmodtesting.common.blockentity;

import dev.turtywurty.fabrictechmodtesting.FabricTechModTesting;
import dev.turtywurty.fabrictechmodtesting.common.blockentity.util.*;
import dev.turtywurty.fabrictechmodtesting.common.menu.CrusherMenu;
import dev.turtywurty.fabrictechmodtesting.common.recipe.CrusherRecipe;
import dev.turtywurty.fabrictechmodtesting.core.init.BlockEntityTypeInit;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
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
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;

import java.util.Optional;

public class CrusherBlockEntity extends UpdatableBlockEntity implements TickableBlockEntity, ExtendedScreenHandlerFactory {
    public static final Component TITLE = Component.translatable("container." + FabricTechModTesting.MOD_ID + ".crusher");
    public static final int INPUT_SLOT = 0, OUTPUT_SLOT = 1;

    private final WrappedInventoryStorage<SimpleContainer> wrappedInventoryStorage = new WrappedInventoryStorage<>();
    private final WrappedEnergyStorage wrappedEnergyStorage = new WrappedEnergyStorage();
    private int progress, maxProgress;
    private final ContainerData containerData = new ContainerData() {
        @Override
        public int get(int value) {
            return switch (value) {
                case 0 -> CrusherBlockEntity.this.progress;
                case 1 -> CrusherBlockEntity.this.maxProgress;
                default -> throw new UnsupportedOperationException("Unsupported container data index: " + value);
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> CrusherBlockEntity.this.progress = value;
                case 1 -> CrusherBlockEntity.this.maxProgress = value;
                default -> throw new UnsupportedOperationException("Unsupported container data index: " + index);
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    };
    private ResourceLocation currentRecipeId;

    public CrusherBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(BlockEntityTypeInit.CRUSHER, blockPos, blockState);

        this.wrappedInventoryStorage.addContainer(new SyncingSimpleContainer(this, 1), Direction.UP);
        this.wrappedInventoryStorage.addContainer(new SyncingSimpleContainer(this, 2), Direction.DOWN);

        this.wrappedEnergyStorage.addStorage(new SyncingEnergyStorage(this, 10000, 1000, 0));
    }

    public InventoryStorage getInventoryProvider(Direction direction) {
        return this.wrappedInventoryStorage.getStorage(direction);
    }

    public EnergyStorage getEnergyProvider(Direction direction) {
        return this.wrappedEnergyStorage.getStorage(direction);
    }

    public EnergyStorage getEnergy() {
        return this.wrappedEnergyStorage.getStorage(Direction.SOUTH);
    }

    @Override
    public void tick() {
        if (this.level == null || this.level.isClientSide)
            return;

        if (this.currentRecipeId == null) {
            Optional<RecipeHolder<CrusherRecipe>> recipeHolder = getCurrentRecipe();
            if (recipeHolder.isPresent()) {
                this.currentRecipeId = recipeHolder.get().id();
                this.maxProgress = recipeHolder.get().value().processTime();
                this.progress = 0;
                update();
            }

            return;
        }

        Optional<RecipeHolder<CrusherRecipe>> currentRecipe = getCurrentRecipe();
        if (currentRecipe.isEmpty() || !currentRecipe.get().id().equals(this.currentRecipeId) || !canOutput(currentRecipe.get().value().outputA()) || !canOutput(currentRecipe.get().value().outputB())) {
            reset();
            return;
        }

        CrusherRecipe recipe = currentRecipe.get().value();
        if (this.progress >= this.maxProgress) {
            if (hasEnergy()) {
                ItemStack outputA = recipe.outputA().copy();
                ItemStack outputB = recipe.outputB().copy();
                if (!canOutput(outputA) || !canOutput(outputB))
                    return;

                consumeEnergy();
                this.wrappedInventoryStorage.getContainer(INPUT_SLOT).removeItem(0, recipe.input().count());

                if (!outputA.isEmpty())
                    this.wrappedInventoryStorage.getContainer(OUTPUT_SLOT).addItem(outputA);

                if (!outputB.isEmpty())
                    this.wrappedInventoryStorage.getContainer(OUTPUT_SLOT).addItem(outputB);

                reset();
            }
        } else {
            if (hasEnergy()) {
                consumeEnergy();
                this.progress++;
                update();
            }
        }
    }

    private boolean canOutput(ItemStack output) {
        return this.wrappedInventoryStorage.getContainer(OUTPUT_SLOT).canAddItem(output);
    }

    // TODO: Create getEnergy method instead of hardcoding 10
    private boolean hasEnergy() {
        return getEnergy().getAmount() >= 10;
    }

    // TODO: Create getEnergy method instead of hardcoding 10
    private void consumeEnergy() {
        this.wrappedEnergyStorage.getStorage(null).amount -= 10;
    }

    private void reset() {
        this.progress = 0;
        this.maxProgress = 0;
        this.currentRecipeId = null;
        update();
    }

    public SimpleContainer getInventory() {
        return new SimpleContainer(this.wrappedInventoryStorage.getStacks().toArray(new ItemStack[0]));
    }

    private Optional<RecipeHolder<CrusherRecipe>> getCurrentRecipe() {
        if (this.level == null || this.level.isClientSide)
            return Optional.empty();

        var inventory = getInventory();
        return this.level.getRecipeManager().getRecipeFor(CrusherRecipe.Type.INSTANCE, inventory, this.level);
    }

    @Override
    protected void saveAdditional(CompoundTag compoundTag) {
        super.saveAdditional(compoundTag);

        var modidData = new CompoundTag();
        modidData.putInt("Progress", this.progress);
        modidData.putInt("MaxProgress", this.maxProgress);
        modidData.putString("CurrentRecipeId", this.currentRecipeId == null ? "" : this.currentRecipeId.toString());
        modidData.put("Inventory", this.wrappedInventoryStorage.writeNBT());
        modidData.put("Energy", this.wrappedEnergyStorage.writeNBT());

        compoundTag.put(FabricTechModTesting.MOD_ID, modidData);
    }

    @Override
    public void load(CompoundTag compoundTag) {
        super.load(compoundTag);

        if (!compoundTag.contains(FabricTechModTesting.MOD_ID, CompoundTag.TAG_COMPOUND))
            return;

        CompoundTag modidData = compoundTag.getCompound(FabricTechModTesting.MOD_ID);
        if (modidData.contains("Progress", CompoundTag.TAG_INT))
            this.progress = modidData.getInt("Progress");

        if (modidData.contains("MaxProgress", CompoundTag.TAG_INT))
            this.maxProgress = modidData.getInt("MaxProgress");

        if (modidData.contains("CurrentRecipeId", CompoundTag.TAG_STRING))
            this.currentRecipeId = ResourceLocation.tryParse(modidData.getString("CurrentRecipeId"));

        if (modidData.contains("Inventory", CompoundTag.TAG_LIST))
            this.wrappedInventoryStorage.readNBT(modidData.getList("Inventory", CompoundTag.TAG_COMPOUND));

        if (modidData.contains("Energy", CompoundTag.TAG_LIST))
            this.wrappedEnergyStorage.readNBT(modidData.getList("Energy", CompoundTag.TAG_COMPOUND));
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
        return new CrusherMenu(id, inventory, this, this.containerData);
    }

    public WrappedInventoryStorage<SimpleContainer> getInventoryStorage() {
        return this.wrappedInventoryStorage;
    }

    public WrappedEnergyStorage getEnergyStorage() {
        return this.wrappedEnergyStorage;
    }

    @Override
    public void writeScreenOpeningData(ServerPlayer player, FriendlyByteBuf buf) {
        buf.writeBlockPos(this.worldPosition);
    }
}
