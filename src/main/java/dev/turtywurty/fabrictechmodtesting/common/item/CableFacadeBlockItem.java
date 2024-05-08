package dev.turtywurty.fabrictechmodtesting.common.item;

import dev.turtywurty.fabrictechmodtesting.FabricTechModTesting;
import dev.turtywurty.fabrictechmodtesting.common.block.CableBlock;
import dev.turtywurty.fabrictechmodtesting.common.block.CableFacadeBlock;
import dev.turtywurty.fabrictechmodtesting.common.blockentity.CableFacadeBlockEntity;
import dev.turtywurty.fabrictechmodtesting.core.init.BlockInit;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class CableFacadeBlockItem extends BlockItem {
    public CableFacadeBlockItem(CableFacadeBlock block, Item.Properties settings) {
        super(block, settings);
    }

    private static String getMimickingString(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null) {
            CompoundTag mimic = tag.getCompound("Mimic");
            Optional<Block> value = BuiltInRegistries.BLOCK.getOptional(new ResourceLocation(mimic.getString("Name")));
            if (value.isPresent()) {
                var blockStack = new ItemStack(value.get(), 1);
                return blockStack.getHoverName().getString();
            }
        }

        return "<unset>";
    }

    private static void userSetMimicBlock(@NotNull ItemStack item, BlockState mimicBlock, UseOnContext context) {
        Level world = context.getLevel();
        Player player = context.getPlayer();
        setMimicBlock(item, mimicBlock);
        if (world.isClientSide && player != null) {
            player.displayClientMessage(Component.translatable(FabricTechModTesting.MOD_ID + ".facade.is_mimicking", mimicBlock.getBlock().getDescriptionId()), false);
        }
    }

    public static void setMimicBlock(@NotNull ItemStack item, BlockState mimicBlock) {
        var tagCompound = new CompoundTag();
        CompoundTag nbt = NbtUtils.writeBlockState(mimicBlock);
        tagCompound.put("Mimic", nbt);
        item.setTag(tagCompound);
    }

    public static BlockState getMimicBlock(Level level, @NotNull ItemStack stack) {
        CompoundTag tagCompound = stack.getTag();
        if (tagCompound == null || !tagCompound.contains("Mimic")) {
            return Blocks.STONE.defaultBlockState();
        } else {
            return NbtUtils.readBlockState(BuiltInRegistries.BLOCK.asLookup(), tagCompound.getCompound("Mimic"));
        }
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();
        BlockState state = level.getBlockState(pos);
        Block block = state.getBlock();
        ItemStack itemstack = context.getItemInHand();

        if (!itemstack.isEmpty()) {
            if (block == BlockInit.CABLE) {
                // We are hitting a cable block. We want to replace it with a facade block
                CableFacadeBlock facadeBlock = (CableFacadeBlock) getBlock();
                BlockPlaceContext blockContext = new ReplaceBlockItemUseContext(context);
                BlockState placementState = facadeBlock.getStateForPlacement(blockContext);
                if(placementState == null)
                    return InteractionResult.FAIL;

                placementState = placementState.setValue(CableBlock.NORTH, state.getValue(CableBlock.NORTH))
                        .setValue(CableBlock.SOUTH, state.getValue(CableBlock.SOUTH))
                        .setValue(CableBlock.WEST, state.getValue(CableBlock.WEST))
                        .setValue(CableBlock.EAST, state.getValue(CableBlock.EAST))
                        .setValue(CableBlock.UP, state.getValue(CableBlock.UP))
                        .setValue(CableBlock.DOWN, state.getValue(CableBlock.DOWN));

                if (placeBlock(blockContext, placementState)) {
                    SoundType soundtype = level.getBlockState(pos).getSoundType();
                    level.playSound(player, pos, soundtype.getPlaceSound(), SoundSource.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);

                    BlockEntity blockEntity = level.getBlockEntity(pos);
                    if (blockEntity instanceof CableFacadeBlockEntity facade) {
                        facade.setMimicBlock(getMimicBlock(level, itemstack));
                    }

                    itemstack.shrink(1);
                }
            } else if (block == BlockInit.CABLE_FACADE) {
                // We are hitting a facade block. We want to copy the block it is mimicking
                BlockEntity blockEntity = level.getBlockEntity(pos);
                if (!(blockEntity instanceof CableFacadeBlockEntity facade))
                    return InteractionResult.FAIL;

                if (facade.getMimicBlock() == null)
                    return InteractionResult.FAIL;

                userSetMimicBlock(itemstack, facade.getMimicBlock(), context);
            } else {
                // We are hitting something else. We want to set that block as what we are going to mimic
                userSetMimicBlock(itemstack, state, context);
            }

            return InteractionResult.SUCCESS;
        } else {
            return InteractionResult.FAIL;
        }
    }

    public static class ReplaceBlockItemUseContext extends BlockPlaceContext {
        public ReplaceBlockItemUseContext(UseOnContext context) {
            super(context);
            this.replaceClicked = true;
        }
    }
}
