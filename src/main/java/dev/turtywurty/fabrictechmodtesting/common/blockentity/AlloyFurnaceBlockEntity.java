package dev.turtywurty.fabrictechmodtesting.common.blockentity;

import dev.turtywurty.fabrictechmodtesting.FabricTechModTesting;
import dev.turtywurty.fabrictechmodtesting.common.blockentity.util.*;
import dev.turtywurty.fabrictechmodtesting.common.menu.AlloyFurnaceMenu;
import dev.turtywurty.fabrictechmodtesting.common.recipe.AlloyFurnaceRecipe;
import dev.turtywurty.fabrictechmodtesting.core.init.BlockEntityTypeInit;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
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
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.FurnaceBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class AlloyFurnaceBlockEntity extends UpdatableBlockEntity implements TickableBlockEntity, ExtendedScreenHandlerFactory {
    public static final Component TITLE = Component.translatable("container." + FabricTechModTesting.MOD_ID + ".alloy_furnace");
    public static final int INPUT_SLOT_0 = 0, INPUT_SLOT_1 = 1, FUEL_SLOT = 2, OUTPUT_SLOT = 3;

    private final WrappedContainerStorage<SimpleContainer> combinedStorage = new WrappedContainerStorage<>();
    private final int maxFuelProgress = 200;
    private int progress, maxProgress, fuelProgress;
    private final ContainerData containerData = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> AlloyFurnaceBlockEntity.this.progress;
                case 1 -> AlloyFurnaceBlockEntity.this.maxProgress;
                case 2 -> AlloyFurnaceBlockEntity.this.fuelProgress;
                case 3 -> AlloyFurnaceBlockEntity.this.maxFuelProgress;
                default -> throw new UnsupportedOperationException("Unsupported container data index: " + index);
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> AlloyFurnaceBlockEntity.this.progress = value;
                case 1 -> AlloyFurnaceBlockEntity.this.maxProgress = value;
                case 2 -> AlloyFurnaceBlockEntity.this.fuelProgress = value;
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

        this.combinedStorage.addContainer(new SyncingSimpleContainer(this, 1), Direction.EAST);
        this.combinedStorage.addContainer(new SyncingSimpleContainer(this, 1), Direction.WEST);
        this.combinedStorage.addContainer(new PredicateSimpleContainer(this, (integer, itemStack) -> isFuel(itemStack), 1), Direction.UP);
        this.combinedStorage.addContainer(new OutputSimpleContainer(this, 1), Direction.DOWN);
    }

    public static InventoryStorage getProviderHandler(BlockEntity blockEntity, Direction direction) {
        if (blockEntity instanceof AlloyFurnaceBlockEntity alloyFurnaceBlockEntity) {
            return alloyFurnaceBlockEntity.combinedStorage.getStorage(direction);
        }

        return null;
    }

    public static boolean isFuel(ItemStack stack) {
        return FurnaceBlockEntity.isFuel(stack);
    }

    @Override
    public void tick() {
        if (this.level == null || this.level.isClientSide)
            return;

        if(this.fuelProgress > 0) {
            if (this.fuelProgress++ >= this.maxFuelProgress) {
                ItemStack fuelStack = this.combinedStorage.getContainer(FUEL_SLOT).getItem(0);
                this.fuelProgress = 0;
                if (isFuel(fuelStack)) {
                    this.combinedStorage.getStorage(Direction.UP).extract(ItemVariant.of(fuelStack), 1, null);
                    update();
                }
            }
        }

        if (this.currentRecipeId == null) {
            Optional<RecipeHolder<AlloyFurnaceRecipe>> match = getCurrentRecipe();

            if (match.isPresent()) {
                RecipeHolder<AlloyFurnaceRecipe> recipeHolder = match.get();
                this.currentRecipeId = recipeHolder.id();
                this.maxProgress = recipeHolder.value().cookTime();
                this.progress = 0;
                update();
            } else {
                int progress = this.progress;
                int maxProgress = this.maxProgress;
                if (progress != 0 || maxProgress != 0) {
                    reset();
                }

                return;
            }
        }

        if (this.progress++ >= this.maxProgress) {
            Optional<RecipeHolder<AlloyFurnaceRecipe>> match = this.level.getRecipeManager()
                    .getAllRecipesFor(AlloyFurnaceRecipe.Type.INSTANCE)
                    .stream()
                    .filter(recipeHolder -> recipeHolder.id().equals(this.currentRecipeId))
                    .findFirst();

            if (match.isPresent()) {
                RecipeHolder<AlloyFurnaceRecipe> recipeHolder = match.get();
                AlloyFurnaceRecipe recipe = recipeHolder.value();

                InventoryStorage output = this.combinedStorage.getStorage(Direction.DOWN);
                InventoryStorage input0 = this.combinedStorage.getStorage(Direction.EAST);
                InventoryStorage input1 = this.combinedStorage.getStorage(Direction.WEST);
                try(Transaction transaction = Transaction.openOuter()) {
                    output.insert(ItemVariant.of(recipe.output()), recipe.output().getCount(), transaction);
                    try(Transaction transaction1 = Transaction.openNested(transaction)) {
                        input0.extract(ItemVariant.of(this.combinedStorage.getContainer(INPUT_SLOT_0).getItem(0)), recipe.inputA().count(), transaction1);
                        input1.extract(ItemVariant.of(this.combinedStorage.getContainer(INPUT_SLOT_1).getItem(0)), recipe.inputB().count(), transaction1);
                        transaction1.commit();
                    }

                    transaction.commit();
                }
                reset();
                return;
            }

            Optional<RecipeHolder<AlloyFurnaceRecipe>> newMatch = getCurrentRecipe();
            if (newMatch.isPresent()) {
                RecipeHolder<AlloyFurnaceRecipe> recipeHolder = newMatch.get();
                this.currentRecipeId = recipeHolder.id();
                this.maxProgress = recipeHolder.value().cookTime();
                update();
                return;
            }

            reset();
        }
    }

    private void reset() {
        this.currentRecipeId = null;
        this.progress = 0;
        this.maxProgress = 0;
        update();
    }

    private Optional<RecipeHolder<AlloyFurnaceRecipe>> getCurrentRecipe() {
        if (this.level == null || this.level.isClientSide)
            return Optional.empty();

        var inventory = new SimpleContainer(this.combinedStorage.getStacks().toArray(new ItemStack[0]));
        return this.level.getRecipeManager().getRecipeFor(AlloyFurnaceRecipe.Type.INSTANCE, inventory, this.level);
    }

    @Override
    protected void saveAdditional(CompoundTag compoundTag) {
        super.saveAdditional(compoundTag);

        var modidData = new CompoundTag();
        modidData.putInt("Progress", this.progress);
        modidData.putInt("MaxProgress", this.maxProgress);
        modidData.putInt("FuelProgress", this.fuelProgress);
        modidData.putString("CurrentRecipe", this.currentRecipeId == null ? "" : this.currentRecipeId.toString());
        modidData.put("Inventory", this.combinedStorage.writeNBT());

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

        if (modidData.contains("FuelProgress", Tag.TAG_INT))
            this.fuelProgress = modidData.getInt("FuelProgress");

        if (modidData.contains("CurrentRecipe", Tag.TAG_STRING)) {
            String currentRecipe = modidData.getString("CurrentRecipe");
            this.currentRecipeId = currentRecipe.isEmpty() ? null : ResourceLocation.tryParse(currentRecipe);
        }

        if (modidData.contains("Inventory", Tag.TAG_LIST))
            this.combinedStorage.readNBT(modidData.getList("Inventory", Tag.TAG_COMPOUND));
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        load(tag);
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

    public WrappedContainerStorage<SimpleContainer> getCombinedStorage() {
        return this.combinedStorage;
    }

    @Override
    public void writeScreenOpeningData(ServerPlayer player, FriendlyByteBuf buf) {
        buf.writeBlockPos(this.worldPosition);
    }
}
