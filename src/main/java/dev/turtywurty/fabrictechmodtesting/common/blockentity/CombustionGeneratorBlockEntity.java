package dev.turtywurty.fabrictechmodtesting.common.blockentity;

import dev.turtywurty.fabrictechmodtesting.FabricTechModTesting;
import dev.turtywurty.fabrictechmodtesting.common.blockentity.util.*;
import dev.turtywurty.fabrictechmodtesting.common.menu.CombustionGeneratorMenu;
import dev.turtywurty.fabrictechmodtesting.core.init.BlockEntityTypeInit;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
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
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.FurnaceBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;
import team.reborn.energy.api.base.SimpleEnergyStorage;

public class CombustionGeneratorBlockEntity extends UpdatableBlockEntity implements TickableBlockEntity, ExtendedScreenHandlerFactory, EnergySpreader {
    public static final Component TITLE = Component.translatable("container." + FabricTechModTesting.MOD_ID + ".combustion_generator");

    private final WrappedInventoryStorage<SimpleContainer> wrappedInventoryStorage = new WrappedInventoryStorage<>();
    private final WrappedEnergyStorage wrappedEnergyStorage = new WrappedEnergyStorage();
    private int burnTime = 0;
    private int maxBurnTime = 0;
    private final ContainerData containerData = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> CombustionGeneratorBlockEntity.this.burnTime;
                case 1 -> CombustionGeneratorBlockEntity.this.maxBurnTime;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> CombustionGeneratorBlockEntity.this.burnTime = value;
                case 1 -> CombustionGeneratorBlockEntity.this.maxBurnTime = value;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    };

    public CombustionGeneratorBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(BlockEntityTypeInit.COMBUSTION_GENERATOR, blockPos, blockState);

        this.wrappedInventoryStorage.addContainer(new PredicateSimpleContainer(this, (integer, itemStack) -> isFuel(itemStack), 1));
        this.wrappedEnergyStorage.addStorage(new SyncingEnergyStorage(this, 100000, 0, 1000));
    }

    public static int getBurnTime(ItemStack fuel) {
        return FurnaceBlockEntity.getFuel().getOrDefault(fuel.getItem(), 1);
    }

    public static boolean isFuel(ItemStack stack) {
        return FurnaceBlockEntity.isFuel(stack);
    }

    @Override
    public void tick() {
        if (this.level == null || this.level.isClientSide)
            return;

        boolean dirty = false;

        SimpleEnergyStorage energy = getEnergy();
        if (energy.getAmount() < energy.getCapacity()) {
            if (this.burnTime > 0) {
                this.burnTime--;
                energy.amount += 10; // TODO: Have this be a variable amount depending on some factors
                if(energy.getAmount() > energy.getCapacity()) {
                    energy.amount = energy.getCapacity();
                }

                dirty = true;
            }

            if (this.burnTime <= 0 && !getInventory().getItem(0).isEmpty()) {
                this.maxBurnTime = this.burnTime = getBurnTime(getInventory().getItem(0));
                getInventory().getItem(0).shrink(1);

                dirty = true;
            } else if (this.burnTime <= 0 && energy.getAmount() <= 0) {
                int maxBurnTime = this.maxBurnTime;
                int burnTime = this.burnTime;

                this.maxBurnTime = this.burnTime = 0;

                if (maxBurnTime != this.maxBurnTime || burnTime != this.burnTime)
                    dirty = true;
            }
        }

        if (dirty)
            update();

        spread(this.level, this.worldPosition, energy);
    }

    private long simulateInsertion(EnergyStorage storage, long amount, Transaction outer) {
        try (Transaction inner = outer.openNested()) {
            long max = storage.insert(amount, inner);
            inner.abort();
            return max;
        }
    }

    public InventoryStorage getInventoryProvider(Direction direction) {
        return this.wrappedInventoryStorage.getStorage(direction);
    }

    public EnergyStorage getEnergyProvider(Direction direction) {
        return this.wrappedEnergyStorage.getStorage(direction);
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
        return new CombustionGeneratorMenu(id, inventory, this, this.containerData);
    }

    @Override
    protected void saveAdditional(CompoundTag compoundTag) {
        super.saveAdditional(compoundTag);

        var modidData = new CompoundTag();
        modidData.putInt("BurnTime", this.burnTime);
        modidData.putInt("MaxBurnTime", this.maxBurnTime);
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
        if (modidData.contains("BurnTime", Tag.TAG_INT))
            this.burnTime = modidData.getInt("BurnTime");

        if (modidData.contains("MaxBurnTime", Tag.TAG_INT))
            this.maxBurnTime = modidData.getInt("MaxBurnTime");

        if (modidData.contains("Inventory", Tag.TAG_LIST))
            this.wrappedInventoryStorage.readNBT(modidData.getList("Inventory", Tag.TAG_COMPOUND));

        if (modidData.contains("Energy", Tag.TAG_LIST))
            this.wrappedEnergyStorage.readNBT(modidData.getList("Energy", Tag.TAG_COMPOUND));
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

    public WrappedInventoryStorage<SimpleContainer> getWrappedInventory() {
        return this.wrappedInventoryStorage;
    }

    public SimpleContainer getInventory() {
        return this.wrappedInventoryStorage.getContainer(0);
    }

    public WrappedEnergyStorage getWrappedEnergy() {
        return this.wrappedEnergyStorage;
    }

    public SimpleEnergyStorage getEnergy() {
        return this.wrappedEnergyStorage.getStorage(null);
    }
}
