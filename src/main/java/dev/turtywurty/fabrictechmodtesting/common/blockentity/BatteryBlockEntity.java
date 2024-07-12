package dev.turtywurty.fabrictechmodtesting.common.blockentity;

import dev.turtywurty.fabrictechmodtesting.FabricTechModTesting;
import dev.turtywurty.fabrictechmodtesting.common.block.BatteryBlock;
import dev.turtywurty.fabrictechmodtesting.common.blockentity.util.*;
import dev.turtywurty.fabrictechmodtesting.common.menu.BatteryMenu;
import dev.turtywurty.fabrictechmodtesting.core.init.BlockEntityTypeInit;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
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
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;
import team.reborn.energy.api.EnergyStorage;
import team.reborn.energy.api.base.SimpleEnergyStorage;

import java.util.List;

public class BatteryBlockEntity extends UpdatableBlockEntity implements TickableBlockEntity, EnergySpreader, ExtendedScreenHandlerFactory {
    public static final Component TITLE = Component.translatable("container." + FabricTechModTesting.MOD_ID + ".battery");

    private final BatteryBlock.BatteryLevel batteryLevel;
    private final WrappedEnergyStorage wrappedEnergyStorage = new WrappedEnergyStorage();
    private final WrappedInventoryStorage<SimpleContainer> wrappedInventoryStorage = new WrappedInventoryStorage<>();

    private final ContainerItemContext containerItemContext = new ContainerItemContext() {
        @Override
        public SingleSlotStorage<ItemVariant> getMainSlot() {
            InventoryStorage storage = BatteryBlockEntity.this.wrappedInventoryStorage.getStorage(null);
            return storage == null ? null : storage.getSlot(0);
        }

        @Override
        public long insertOverflow(ItemVariant itemVariant, long maxAmount, TransactionContext transactionContext) {
            return 0;
        }

        @Override
        public @UnmodifiableView List<SingleSlotStorage<ItemVariant>> getAdditionalSlots() {
            return List.of();
        }
    };

    public BatteryBlockEntity(BlockPos blockPos, BlockState blockState, BatteryBlock.BatteryLevel batteryLevel) {
        super(BlockEntityTypeInit.BATTERY, blockPos, blockState);
        this.batteryLevel = batteryLevel;

        this.wrappedEnergyStorage.addStorage(new SyncingEnergyStorage(
                this, batteryLevel.getCapacity(), batteryLevel.getCapacity(), batteryLevel.getCapacity()));

        this.wrappedInventoryStorage.addContainer(new PredicateSimpleContainer(this, (index, stack) -> {
            var storage = EnergyStorage.ITEM.find(stack, this.containerItemContext);
            return storage != null && storage.supportsInsertion() && storage.getAmount() < storage.getCapacity();
        }, 1));
    }

    @Override
    public void tick() {
        if (this.level == null || this.level.isClientSide)
            return;

        SimpleEnergyStorage thisStorage = this.wrappedEnergyStorage.getStorage(null);

        ItemStack stack = getInventory().getItem(0);
        if (!stack.isEmpty()) {
            var itemEnergyStorage = EnergyStorage.ITEM.find(stack, this.containerItemContext);
            if (itemEnergyStorage != null && itemEnergyStorage.supportsInsertion()) {
                try (Transaction transaction = Transaction.openOuter()) {
                    long inserted = itemEnergyStorage.insert(thisStorage.amount, transaction);
                    if (inserted > 0) {
                        thisStorage.amount -= inserted;
                        update();
                        transaction.commit();
                    } else
                        transaction.abort();
                }
            }
        }

        spread(this.level, this.worldPosition, thisStorage);
    }

    @Override
    protected void saveAdditional(CompoundTag compoundTag) {
        super.saveAdditional(compoundTag);

        var modidData = new CompoundTag();
        modidData.put("Energy", this.wrappedEnergyStorage.writeNBT());
        modidData.put("Inventory", this.wrappedInventoryStorage.writeNBT());

        compoundTag.put(FabricTechModTesting.MOD_ID, modidData);
    }

    @Override
    public void load(CompoundTag compoundTag) {
        super.load(compoundTag);

        if (!compoundTag.contains(FabricTechModTesting.MOD_ID, Tag.TAG_COMPOUND))
            return;

        var modidData = compoundTag.getCompound(FabricTechModTesting.MOD_ID);

        if (modidData.contains("Energy", Tag.TAG_LIST))
            this.wrappedEnergyStorage.readNBT(modidData.getList("Energy", Tag.TAG_COMPOUND));

        if (modidData.contains("Inventory", Tag.TAG_LIST))
            this.wrappedInventoryStorage.readNBT(modidData.getList("Inventory", Tag.TAG_COMPOUND));
    }

    public SimpleEnergyStorage getEnergyProvider(Direction direction) {
        return this.wrappedEnergyStorage.getStorage(direction);
    }

    public InventoryStorage getInventoryProvider(Direction direction) {
        return this.wrappedInventoryStorage.getStorage(direction);
    }

    public WrappedInventoryStorage<SimpleContainer> getWrappedInventory() {
        return this.wrappedInventoryStorage;
    }

    public SimpleEnergyStorage getEnergy() {
        return this.wrappedEnergyStorage.getStorage(null);
    }

    public SimpleContainer getInventory() {
        return this.wrappedInventoryStorage.getContainer(0);
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        saveAdditional(tag);
        return tag;
    }

    public BatteryBlock.BatteryLevel getBatteryLevel() {
        return this.batteryLevel;
    }

    @Override
    public void writeScreenOpeningData(ServerPlayer player, FriendlyByteBuf buf) {
        buf.writeBlockPos(this.worldPosition);
    }

    @Override
    public @NotNull Component getDisplayName() {
        return TITLE;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new BatteryMenu(id, inventory, this);
    }
}
