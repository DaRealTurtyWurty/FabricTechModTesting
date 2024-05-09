package dev.turtywurty.fabrictechmodtesting.common.blockentity;

import dev.turtywurty.fabrictechmodtesting.FabricTechModTesting;
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
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import team.reborn.energy.api.EnergyStorage;
import team.reborn.energy.api.base.SimpleEnergyStorage;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class CableBlockEntity extends UpdatableBlockEntity implements TickableBlockEntity {
    private final WrappedEnergyStorage wrappedEnergyStorage = new WrappedEnergyStorage();

    private Set<BlockPos> connectedBlocks = null;

    protected CableBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    public CableBlockEntity(BlockPos blockPos, BlockState blockState) {
        this(BlockEntityTypeInit.CABLE, blockPos, blockState);

        this.wrappedEnergyStorage.addStorage(new SyncingEnergyStorage(this, 1000, 100, 0));
    }

    private void checkOutputs() {
        if (this.connectedBlocks == null && level != null) {
            this.connectedBlocks = new HashSet<>();
            traverse(worldPosition, cable -> {
                // Check for all energy receivers around this position (ignore cables)
                for (Direction direction : Direction.values()) {
                    BlockPos pos = cable.getBlockPos().relative(direction);
                    BlockEntity blockEntity = level.getBlockEntity(pos);
                    EnergyStorage storage = EnergyStorage.SIDED.find(level, pos, direction.getOpposite());
                    if (storage != null && storage.supportsInsertion() && !(blockEntity instanceof CableBlockEntity)) {
                        this.connectedBlocks.add(pos);
                    }
                }
            });
        }
    }

    public void markDirty() {
        traverse(worldPosition, cable -> cable.connectedBlocks = null);
    }

    // This is a generic function that will traverse all cables connected to this cable and call the given consumer for each cable.
    private void traverse(BlockPos pos, Consumer<CableBlockEntity> consumer) {
        Set<BlockPos> traversed = new HashSet<>();
        traversed.add(pos);
        consumer.accept(this);
        traverse(pos, traversed, consumer);
    }

    private void traverse(BlockPos pos, Set<BlockPos> traversed, Consumer<CableBlockEntity> consumer) {
        if(level == null)
            return;

        for (Direction direction : Direction.values()) {
            BlockPos offset = pos.relative(direction);
            if (!traversed.contains(offset)) {
                traversed.add(offset);
                if (level.getBlockEntity(offset) instanceof CableBlockEntity cable) {
                    consumer.accept(cable);
                    cable.traverse(offset, traversed, consumer);
                }
            }
        }
    }

    public SimpleEnergyStorage getEnergy() {
        return this.wrappedEnergyStorage.getStorage(null);
    }

    @Override
    public void tick() {
        if(level == null || level.isClientSide)
            return;

        SimpleEnergyStorage energy = getEnergy();
        if(energy.getAmount() > 0) {
            checkOutputs();
            if(this.connectedBlocks.isEmpty())
                return;

            long amount = energy.getAmount() / this.connectedBlocks.size();
            try(Transaction transaction = Transaction.openOuter()) {
                for (BlockPos pos : this.connectedBlocks) {
                    Direction direction = Direction.fromDelta(worldPosition.getX() - pos.getX(), worldPosition.getY() - pos.getY(), worldPosition.getZ() - pos.getZ());
                    EnergyStorage storage = EnergyStorage.SIDED.find(level, pos, direction);
                    if (storage != null && storage.supportsInsertion()) {
                        energy.amount -= storage.insert(amount, transaction);
                    }
                }

                transaction.commit();
            }
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

        if(!compoundTag.contains(FabricTechModTesting.MOD_ID, Tag.TAG_COMPOUND))
            return;

        var modidData = compoundTag.getCompound(FabricTechModTesting.MOD_ID);
        if(modidData.contains("Energy", Tag.TAG_LIST))
            this.wrappedEnergyStorage.readNBT(modidData.getList("Energy", Tag.TAG_COMPOUND));
    }

    public SimpleEnergyStorage getEnergyProvider(Direction direction) {
        return this.wrappedEnergyStorage.getStorage(direction);
    }
}
