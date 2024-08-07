package dev.turtywurty.fabrictechmodtesting.common.blockentity;

import dev.turtywurty.fabrictechmodtesting.FabricTechModTesting;
import dev.turtywurty.fabrictechmodtesting.common.blockentity.util.*;
import dev.turtywurty.fabrictechmodtesting.common.menu.WindTurbineMenu;
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
import net.minecraft.util.Mth;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;
import team.reborn.energy.api.EnergyStorage;
import team.reborn.energy.api.base.SimpleEnergyStorage;

import java.util.List;
import java.util.Random;

public class WindTurbineBlockEntity extends UpdatableBlockEntity implements TickableBlockEntity, ExtendedScreenHandlerFactory, EnergySpreader {
    public static final Component TITLE = Component.translatable("container." + FabricTechModTesting.MOD_ID + ".wind_turbine");

    private final WrappedInventoryStorage<SimpleContainer> wrappedInventoryStorage = new WrappedInventoryStorage<>();
    private final WrappedEnergyStorage wrappedEnergyStorage = new WrappedEnergyStorage();
    private final ContainerItemContext containerItemContext = new ContainerItemContext() {
        @Override
        public SingleSlotStorage<ItemVariant> getMainSlot() {
            InventoryStorage storage = WindTurbineBlockEntity.this.wrappedInventoryStorage.getStorage(null);
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

    private final float wind = new Random(this.worldPosition.asLong()).nextFloat();

    public WindTurbineBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(BlockEntityTypeInit.WIND_TURBINE, blockPos, blockState);

        this.wrappedInventoryStorage.addContainer(new PredicateSimpleContainer(this, (index, stack) -> {
            var storage = EnergyStorage.ITEM.find(stack, this.containerItemContext);
            return storage != null && storage.supportsInsertion() && storage.getAmount() < storage.getCapacity();
        }, 1));

        this.wrappedEnergyStorage.addStorage(new SyncingEnergyStorage(this, 10000, 0, 1000));
    }

    public static int getEnergyOutput(Level level, BlockPos pos, float wind) {
        if (level == null)
            return 0;

        if(!level.canSeeSky(pos.above()))
            return 0;

        int y = pos.getY();
        if(y < 50 || y > 255)
            return 0;

        int multiplier = 1;
        if(y > 60 && y < 100)
            multiplier = 2;
        else if(y > 100 && y < 150)
            multiplier = 3;
        else if(y > 150 && y < 200)
            multiplier = 4;
        else if(y > 200)
            multiplier = 5;

        return (int) Mth.clamp((multiplier + wind) * 10, 0, getMaxEnergyOutput());
    }

    public static int getMaxEnergyOutput() {
        return 100;
    }

    @Override
    public void tick() {
        if (this.level == null || this.level.isClientSide)
            return;

        boolean dirty = false;

        SimpleEnergyStorage energyStorage = getEnergyStorage();
        long currentEnergy = energyStorage.amount;
        if (currentEnergy < energyStorage.getCapacity()) {
            int outputSignal = getEnergyOutput();
            energyStorage.amount += outputSignal;
            if (energyStorage.amount > energyStorage.getCapacity())
                energyStorage.amount = energyStorage.getCapacity();

            if (currentEnergy != energyStorage.amount)
                dirty = true;
        }

        ItemStack stack = getInventory().getItem(0);
        if (!stack.isEmpty()) {
            var itemEnergyStorage = EnergyStorage.ITEM.find(stack, this.containerItemContext);
            if (itemEnergyStorage != null && itemEnergyStorage.supportsInsertion()) {
                try (Transaction transaction = Transaction.openOuter()) {
                    long inserted = itemEnergyStorage.insert(energyStorage.amount, transaction);
                    if (inserted > 0) {
                        energyStorage.amount -= inserted;
                        dirty = true;
                        transaction.commit();
                    } else
                        transaction.abort();
                }
            }
        }

        if (dirty)
            update();

        spread(this.level, this.worldPosition, energyStorage);
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
        return new WindTurbineMenu(id, inventory, this);
    }

    @Override
    protected void saveAdditional(CompoundTag compoundTag) {
        super.saveAdditional(compoundTag);

        var modidData = new CompoundTag();
        modidData.put("Inventory", this.wrappedInventoryStorage.writeNBT());
        modidData.put("Energy", this.wrappedEnergyStorage.writeNBT());
        compoundTag.put(FabricTechModTesting.MOD_ID, modidData);
    }

    @Override
    public void load(CompoundTag compoundTag) {
        super.load(compoundTag);

        if (!compoundTag.contains(FabricTechModTesting.MOD_ID, Tag.TAG_COMPOUND))
            return;

        var modidData = compoundTag.getCompound(FabricTechModTesting.MOD_ID);
        if (!modidData.contains("Inventory", Tag.TAG_LIST))
            return;

        this.wrappedInventoryStorage.readNBT(modidData.getList("Inventory", Tag.TAG_COMPOUND));

        if (!modidData.contains("Energy", Tag.TAG_LIST))
            return;

        this.wrappedEnergyStorage.readNBT(modidData.getList("Energy", Tag.TAG_COMPOUND));
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag() {
        var tag = super.getUpdateTag();
        saveAdditional(tag);
        return tag;
    }

    public SimpleContainer getInventory() {
        return this.wrappedInventoryStorage.getContainer(0);
    }

    public SimpleEnergyStorage getEnergyStorage() {
        return this.wrappedEnergyStorage.getStorage(null);
    }

    public SimpleEnergyStorage getEnergy() {
        return this.wrappedEnergyStorage.getStorage(null);
    }

    public WrappedInventoryStorage<SimpleContainer> getWrappedInventory() {
        return this.wrappedInventoryStorage;
    }

    public int getEnergyOutput() {
        if (this.level == null)
            return 0;

        return getEnergyOutput(this.level, this.worldPosition, this.wind);
    }

    public InventoryStorage getInventoryProvider(Direction direction) {
        return this.wrappedInventoryStorage.getStorage(direction);
    }

    public EnergyStorage getEnergyProvider(Direction direction) {
        return this.wrappedEnergyStorage.getStorage(direction);
    }
}