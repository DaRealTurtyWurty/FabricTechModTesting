package dev.turtywurty.fabrictechmodtesting.common.blockentity.util;

import dev.turtywurty.fabrictechmodtesting.core.util.INBTSerializable;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.base.SimpleEnergyStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WrappedEnergyStorage implements INBTSerializable<ListTag> {
    private final List<SimpleEnergyStorage> storages = new ArrayList<>();
    private final Map<Direction, SimpleEnergyStorage> sidedStorageMap = new HashMap<>();

    public void addStorage(SimpleEnergyStorage storage) {
        addStorage(storage, null);
    }

    public void addStorage(@NotNull SimpleEnergyStorage storage, @Nullable Direction direction) {
        this.storages.add(storage);

        if(direction == null) {
            for (Direction value : Direction.values()) {
                this.sidedStorageMap.put(value, storage);
            }
        } else {
            this.sidedStorageMap.put(direction, storage);
        }
    }

    public List<SimpleEnergyStorage> getStorages() {
        return this.storages;
    }

    public Map<Direction, SimpleEnergyStorage> getSidedStorageMap() {
        return this.sidedStorageMap;
    }

    public SimpleEnergyStorage getStorage(@Nullable Direction direction) {
        if(direction == null) {
            return this.storages.get(0);
        }

        return this.sidedStorageMap.get(direction);
    }

    @Override
    public ListTag writeNBT() {
        var listTag = new ListTag();
        for (SimpleEnergyStorage storage : this.storages) {
            var nbt = new CompoundTag();
            nbt.putLong("Amount", storage.getAmount());
            listTag.add(nbt);
        }

        return listTag;
    }

    @Override
    public void readNBT(ListTag nbt) {
        for (int index = 0; index < nbt.size(); index++) {
            CompoundTag tag = nbt.getCompound(index);
            this.storages.get(index).amount = tag.getLong("Amount");
        }
    }
}
