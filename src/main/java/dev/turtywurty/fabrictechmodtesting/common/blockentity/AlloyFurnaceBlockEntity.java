package dev.turtywurty.fabrictechmodtesting.common.blockentity;

import dev.turtywurty.fabrictechmodtesting.FabricTechModTesting;
import dev.turtywurty.fabrictechmodtesting.common.blockentity.util.TickableBlockEntity;
import dev.turtywurty.fabrictechmodtesting.common.menu.AlloyFurnaceMenu;
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
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.FurnaceBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AlloyFurnaceBlockEntity extends BlockEntity implements TickableBlockEntity, ExtendedScreenHandlerFactory {
    public static final Component TITLE = Component.translatable("container." + FabricTechModTesting.MOD_ID + ".alloy_furnace");

    public static final int INPUT_SLOT_0 = 0, INPUT_SLOT_1 = 1, FUEL_SLOT = 2, OUTPUT_SLOT = 3;

    private int progress, maxProgress, fuelProgress, maxFuelProgress;
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
                case 3 -> AlloyFurnaceBlockEntity.this.maxFuelProgress = value;
            }
        }

        @Override
        public int getCount() {
            return 4;
        }
    };

    private final SimpleContainer inputSlot0 = new SimpleContainer(1) {
        @Override
        public void setChanged() {
            super.setChanged();
            AlloyFurnaceBlockEntity.this.update();
        }
    };

    private final SimpleContainer inputSlot1 = new SimpleContainer(1) {
        @Override
        public void setChanged() {
            super.setChanged();
            AlloyFurnaceBlockEntity.this.update();
        }
    };

    private final SimpleContainer fuelSlot = new SimpleContainer(1) {
        @Override
        public boolean canPlaceItem(int slot, ItemStack itemStack) {
            return isFuel(itemStack);
        }

        @Override
        public void setChanged() {
            super.setChanged();
            AlloyFurnaceBlockEntity.this.update();
        }
    };

    private final SimpleContainer outputSlot = new SimpleContainer(1) {
        @Override
        public boolean canPlaceItem(int slot, ItemStack itemStack) {
            return false;
        }

        @Override
        public void setChanged() {
            super.setChanged();
            AlloyFurnaceBlockEntity.this.update();
        }
    };

    private final InventoryStorage inputSlot0Wrapper = InventoryStorage.of(this.inputSlot0, Direction.EAST);
    private final InventoryStorage inputSlot1Wrapper = InventoryStorage.of(this.inputSlot1, Direction.WEST);
    private final InventoryStorage fuelSlotWrapper = InventoryStorage.of(this.fuelSlot, Direction.UP);
    private final InventoryStorage outputSlotWrapper = InventoryStorage.of(this.outputSlot, Direction.DOWN);

    public static InventoryStorage getProviderHandler(BlockEntity blockEntity, Direction direction) {
        if (blockEntity instanceof AlloyFurnaceBlockEntity alloyFurnaceBlockEntity) {
            return switch (direction) {
                case EAST -> alloyFurnaceBlockEntity.inputSlot0Wrapper;
                case WEST -> alloyFurnaceBlockEntity.inputSlot1Wrapper;
                case UP -> alloyFurnaceBlockEntity.fuelSlotWrapper;
                case DOWN -> alloyFurnaceBlockEntity.outputSlotWrapper;
                default -> null;
            };
        }

        return null;
    }

    public AlloyFurnaceBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(BlockEntityTypeInit.ALLOY_FURNACE, blockPos, blockState);
    }

    public static boolean isFuel(ItemStack stack) {
        return FurnaceBlockEntity.isFuel(stack);
    }

    @Override
    public void tick() {

    }

    public void update() {
        setChanged();
        if (this.level != null) {
            this.level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag compoundTag) {
        super.saveAdditional(compoundTag);

        var modidData = new CompoundTag();
        modidData.putInt("Progress", this.progress);
        modidData.putInt("MaxProgress", this.maxProgress);
        modidData.putInt("FuelProgress", this.fuelProgress);
        modidData.putInt("MaxFuelProgress", this.maxFuelProgress);

        var inventoryData = new CompoundTag();
        var slot0Tag = new CompoundTag();
        ContainerHelper.saveAllItems(slot0Tag, this.inputSlot0.getItems());
        inventoryData.put("InputSlot0", slot0Tag);
        var slot1Tag = new CompoundTag();
        ContainerHelper.saveAllItems(slot1Tag, this.inputSlot1.getItems());
        inventoryData.put("InputSlot1", slot1Tag);
        var fuelTag = new CompoundTag();
        ContainerHelper.saveAllItems(fuelTag, this.fuelSlot.getItems());
        inventoryData.put("FuelSlot", fuelTag);
        var outputTag = new CompoundTag();
        ContainerHelper.saveAllItems(outputTag, this.outputSlot.getItems());
        inventoryData.put("OutputSlot", outputTag);
        modidData.put("Inventory", inventoryData);

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

        if (modidData.contains("MaxFuelProgress", Tag.TAG_INT))
            this.maxFuelProgress = modidData.getInt("MaxFuelProgress");

        if (modidData.contains("Inventory", Tag.TAG_COMPOUND)) {
            CompoundTag inventoryData = modidData.getCompound("Inventory");

            if (inventoryData.contains("InputSlot0", Tag.TAG_COMPOUND)) {
                CompoundTag slot0Tag = inventoryData.getCompound("InputSlot0");
                this.inputSlot0.clearContent();
                ContainerHelper.loadAllItems(slot0Tag, this.inputSlot0.getItems());
            }

            if (inventoryData.contains("InputSlot1", Tag.TAG_COMPOUND)) {
                CompoundTag slot1Tag = inventoryData.getCompound("InputSlot1");
                this.inputSlot1.clearContent();
                ContainerHelper.loadAllItems(slot1Tag, this.inputSlot1.getItems());
            }

            if (inventoryData.contains("FuelSlot", Tag.TAG_COMPOUND)) {
                CompoundTag fuelTag = inventoryData.getCompound("FuelSlot");
                this.fuelSlot.clearContent();
                ContainerHelper.loadAllItems(fuelTag, this.fuelSlot.getItems());
            }

            if (inventoryData.contains("OutputSlot", Tag.TAG_COMPOUND)) {
                CompoundTag outputTag = inventoryData.getCompound("OutputSlot");
                this.outputSlot.clearContent();
                ContainerHelper.loadAllItems(outputTag, this.outputSlot.getItems());
            }
        }
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

    public SimpleContainer getInputSlot0() {
        return this.inputSlot0;
    }

    public SimpleContainer getInputSlot1() {
        return this.inputSlot1;
    }

    public SimpleContainer getFuelSlot() {
        return this.fuelSlot;
    }

    public SimpleContainer getOutputSlot() {
        return this.outputSlot;
    }

    @Override
    public void writeScreenOpeningData(ServerPlayer player, FriendlyByteBuf buf) {
        buf.writeBlockPos(this.worldPosition);
    }
}
