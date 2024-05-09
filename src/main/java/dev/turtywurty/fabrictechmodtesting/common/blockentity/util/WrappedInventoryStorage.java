package dev.turtywurty.fabrictechmodtesting.common.blockentity.util;

import dev.turtywurty.fabrictechmodtesting.core.util.INBTSerializable;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WrappedInventoryStorage<T extends SimpleContainer> implements INBTSerializable<ListTag> {
    private final List<T> containers = new ArrayList<>();
    private final List<InventoryStorage> storages = new ArrayList<>();
    private final Map<Direction, InventoryStorage> sidedStorageMap = new HashMap<>();
    private final CombinedStorage<ItemVariant, InventoryStorage> combinedStorage = new CombinedStorage<>(storages);

    public void addContainer(@NotNull T container) {
        addContainer(container, null);
    }

    public void addContainer(@NotNull T container, @Nullable Direction direction) {
        this.containers.add(container);

        var storage = InventoryStorage.of(container, direction);
        this.storages.add(storage);
        this.sidedStorageMap.put(direction, storage);
    }

    public @NotNull List<T> getContainers() {
        return this.containers;
    }

    public @NotNull List<InventoryStorage> getStorages() {
        return this.storages;
    }

    public @NotNull Map<Direction, InventoryStorage> getSidedStorageMap() {
        return this.sidedStorageMap;
    }

    public @NotNull CombinedStorage<ItemVariant, InventoryStorage> getCombinedStorage() {
        return this.combinedStorage;
    }

    public @Nullable InventoryStorage getStorage(@Nullable Direction direction) {
        return this.sidedStorageMap.get(direction);
    }

    public @Nullable T getContainer(int index) {
        return this.containers.get(index);
    }

    public @NotNull List<ItemStack> getStacks() {
        List<ItemStack> stacks = new ArrayList<>();
        for (T container : this.containers) {
            for (int i = 0; i < container.getContainerSize(); i++) {
                stacks.add(container.getItem(i));
            }
        }

        return stacks;
    }

    @Override
    public @NotNull ListTag writeNBT() {
        var nbt = new ListTag();
        for (T container : this.containers) {
            var containerNbt = new CompoundTag();
            nbt.add(ContainerHelper.saveAllItems(containerNbt, container.getItems()));
        }

        return nbt;
    }

    @Override
    public void readNBT(@NotNull ListTag nbt) {
        for (int i = 0; i < nbt.size(); i++) {
            CompoundTag containerNbt = nbt.getCompound(i);
            this.containers.get(i).clearContent();
            ContainerHelper.loadAllItems(containerNbt, this.containers.get(i).getItems());
        }
    }

    public void checkSize(int size) {
        if (this.containers.stream().map(Container::getContainerSize).reduce(0, Integer::sum) != size)
            throw new IllegalArgumentException("Wrapped container size is not equal to " + size + "!");
    }

    public void startOpen(@NotNull Player player) {
        for (T container : this.containers) {
            container.startOpen(player);
        }
    }

    public void stopOpen(@NotNull Player player) {
        for (T container : this.containers) {
            container.stopOpen(player);
        }
    }

    public void dropContents(@NotNull Level level, @NotNull BlockPos pos) {
        for (T container : this.containers) {
            Containers.dropContents(level, pos, container);
        }
    }
}
