package dev.turtywurty.fabrictechmodtesting.common.blockentity;

import dev.turtywurty.fabrictechmodtesting.FabricTechModTesting;
import dev.turtywurty.fabrictechmodtesting.common.block.BatteryBlock;
import dev.turtywurty.fabrictechmodtesting.common.blockentity.util.SyncingEnergyStorage;
import dev.turtywurty.fabrictechmodtesting.common.blockentity.util.TickableBlockEntity;
import dev.turtywurty.fabrictechmodtesting.common.blockentity.util.UpdatableBlockEntity;
import dev.turtywurty.fabrictechmodtesting.common.blockentity.util.WrappedEnergyStorage;
import dev.turtywurty.fabrictechmodtesting.core.init.BlockEntityTypeInit;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;

import java.util.ArrayList;
import java.util.List;

public class BatteryBlockEntity extends UpdatableBlockEntity implements TickableBlockEntity {
    private final BatteryBlock.BatteryLevel batteryLevel;
    private final WrappedEnergyStorage wrappedEnergyStorage = new WrappedEnergyStorage();

    public BatteryBlockEntity(BlockPos blockPos, BlockState blockState, BatteryBlock.BatteryLevel batteryLevel) {
        super(BlockEntityTypeInit.BATTERY, blockPos, blockState);
        this.batteryLevel = batteryLevel;

        this.wrappedEnergyStorage.addStorage(new SyncingEnergyStorage(this, batteryLevel.getCapacity(), batteryLevel.getCapacity(), batteryLevel.getCapacity()));
    }

    @Override
    public void tick() {
        EnergyStorage thisStorage = this.wrappedEnergyStorage.getStorage(null);

        List<EnergyStorage> storages = new ArrayList<>();
        for (Direction direction : Direction.values()) {
            EnergyStorage storage = EnergyStorage.SIDED.find(this.level, this.worldPosition.relative(direction), direction.getOpposite());
            if (storage == null || !storage.supportsInsertion() || storage.getAmount() >= storage.getCapacity())
                continue;

            storages.add(storage);
        }

        if (storages.isEmpty())
            return;

        try (Transaction transaction = Transaction.openOuter()) {
            long currentEnergy = thisStorage.getAmount();
            long totalExtractable = thisStorage.extract(Long.MAX_VALUE, transaction);
            long totalInserted = 0;

            for (EnergyStorage storage : storages) {
                long amount = totalExtractable - totalInserted;
                if (amount <= 0)
                    break;

                long inserted = simulateInsertion(storage, amount, transaction);
                totalInserted += inserted;
            }

            long remaining = totalExtractable - totalInserted;
            thisStorage.insert(remaining, transaction);
            transaction.commit();

            if (currentEnergy != thisStorage.getAmount())
                update();
        }
    }

    @Override
    protected void saveAdditional(CompoundTag compoundTag) {
        super.saveAdditional(compoundTag);

        var modidData = new CompoundTag();
        modidData.put("Energy", this.wrappedEnergyStorage.writeNBT());

        compoundTag.put(FabricTechModTesting.MOD_ID, modidData);
    }

    @Override
    public void load(CompoundTag compoundTag) {
        super.load(compoundTag);

        if (!compoundTag.contains(FabricTechModTesting.MOD_ID, Tag.TAG_COMPOUND))
            return;
        var modidData = compoundTag.getCompound(FabricTechModTesting.MOD_ID);

        if (!modidData.contains("Energy", Tag.TAG_LIST))
            return;

        this.wrappedEnergyStorage.readNBT(modidData.getList("Energy", Tag.TAG_COMPOUND));
    }

    public EnergyStorage getEnergyProvider(Direction direction) {
        return this.wrappedEnergyStorage.getStorage(direction);
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

    private long simulateInsertion(EnergyStorage storage, long amount, Transaction outer) {
        try (Transaction inner = outer.openNested()) {
            long max = storage.insert(amount, inner);
            inner.abort();
            return max;
        }
    }

    public BatteryBlock.BatteryLevel getBatteryLevel() {
        return this.batteryLevel;
    }
}
