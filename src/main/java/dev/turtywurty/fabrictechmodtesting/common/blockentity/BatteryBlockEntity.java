package dev.turtywurty.fabrictechmodtesting.common.blockentity;

import dev.turtywurty.fabrictechmodtesting.FabricTechModTesting;
import dev.turtywurty.fabrictechmodtesting.common.block.BatteryBlock;
import dev.turtywurty.fabrictechmodtesting.common.blockentity.util.*;
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
import team.reborn.energy.api.base.SimpleEnergyStorage;

import java.util.ArrayList;
import java.util.List;

public class BatteryBlockEntity extends UpdatableBlockEntity implements TickableBlockEntity, EnergySpreader {
    private final BatteryBlock.BatteryLevel batteryLevel;
    private final WrappedEnergyStorage wrappedEnergyStorage = new WrappedEnergyStorage();

    public BatteryBlockEntity(BlockPos blockPos, BlockState blockState, BatteryBlock.BatteryLevel batteryLevel) {
        super(BlockEntityTypeInit.BATTERY, blockPos, blockState);
        this.batteryLevel = batteryLevel;

        this.wrappedEnergyStorage.addStorage(new SyncingEnergyStorage(this, batteryLevel.getCapacity(), batteryLevel.getCapacity(), batteryLevel.getCapacity()));
    }

    @Override
    public void tick() {
        if(this.level == null || this.level.isClientSide)
            return;

        SimpleEnergyStorage thisStorage = this.wrappedEnergyStorage.getStorage(null);
        spread(this.level, this.worldPosition, thisStorage);
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

    public SimpleEnergyStorage getEnergyProvider(Direction direction) {
        return this.wrappedEnergyStorage.getStorage(direction);
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
}
