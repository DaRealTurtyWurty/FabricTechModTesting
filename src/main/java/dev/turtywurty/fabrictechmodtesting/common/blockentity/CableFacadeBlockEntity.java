package dev.turtywurty.fabrictechmodtesting.common.blockentity;

import dev.turtywurty.fabrictechmodtesting.FabricTechModTesting;
import dev.turtywurty.fabrictechmodtesting.core.init.BlockEntityTypeInit;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class CableFacadeBlockEntity extends CableBlockEntity {
    private @Nullable BlockState mimicBlock = null;

    public CableFacadeBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(BlockEntityTypeInit.CABLE_FACADE, blockPos, blockState);
    }

    public void setMimicBlock(BlockState mimicBlock) {
        this.mimicBlock = mimicBlock;
        setChanged();

        if(this.level != null)
            this.level.sendBlockUpdated(this.worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS + Block.UPDATE_NEIGHBORS);
    }

    @Override
    protected void saveAdditional(CompoundTag compoundTag) {
        super.saveAdditional(compoundTag);

        var modidData = new CompoundTag();
        if(this.mimicBlock != null)
            modidData.put("MimicBlock", NbtUtils.writeBlockState(this.mimicBlock));

        compoundTag.put(FabricTechModTesting.MOD_ID, modidData);
    }

    @Override
    public void load(CompoundTag compoundTag) {
        super.load(compoundTag);

        if(!compoundTag.contains(FabricTechModTesting.MOD_ID, Tag.TAG_COMPOUND))
            return;

        CompoundTag modidData = compoundTag.getCompound(FabricTechModTesting.MOD_ID);
        if(modidData.contains("MimicBlock", Tag.TAG_COMPOUND))
            this.mimicBlock = NbtUtils.readBlockState(BuiltInRegistries.BLOCK.asLookup(), modidData.getCompound("MimicBlock"));
    }

    public @Nullable BlockState getMimicBlock() {
        return this.mimicBlock;
    }
}
