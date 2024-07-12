package dev.turtywurty.fabrictechmodtesting.common.block;

import dev.turtywurty.fabrictechmodtesting.common.blockentity.CrusherBlockEntity;
import dev.turtywurty.fabrictechmodtesting.common.blockentity.util.TickableBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("deprecation")
public class CrusherBlock extends Block implements EntityBlock {
    public static final AABB PICKUP_AREA = new AABB(0, 0, 0, 1, 0.7, 1);
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty RUNNING = BooleanProperty.create("running");

    private static final VoxelShape SHAPE = Shapes.box(0, 0, 0, 1, 0.625, 1);

    public CrusherBlock(Properties properties) {
        super(properties);
        registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(RUNNING, false));
    }

    @Override
    public @NotNull InteractionResult use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
        if (!level.isClientSide()) {
            BlockEntity blockEntity = level.getBlockEntity(blockPos);
            if (blockEntity instanceof CrusherBlockEntity crusherBlockEntity) {
                player.openMenu(crusherBlockEntity);
            }
        }

        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    @Override
    public void onRemove(BlockState blockState, Level level, BlockPos blockPos, BlockState blockState2, boolean bl) {
        if (blockState.getBlock() != blockState2.getBlock()) {
            BlockEntity blockEntity = level.getBlockEntity(blockPos);
            if (blockEntity instanceof CrusherBlockEntity crusherBlockEntity) {
                crusherBlockEntity.getInventoryStorage().dropContents(level, blockPos);
                level.updateNeighbourForOutputSignal(blockPos, this);
            }

            super.onRemove(blockState, level, blockPos, blockState2, bl);
        }
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState blockState) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState blockState, Level level, BlockPos blockPos) {
        return AbstractContainerMenu.getRedstoneSignalFromBlockEntity(level.getBlockEntity(blockPos));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING, RUNNING);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
        return defaultBlockState().setValue(FACING, blockPlaceContext.getHorizontalDirection().getOpposite()).setValue(RUNNING, false);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new CrusherBlockEntity(blockPos, blockState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState blockState, BlockEntityType<T> blockEntityType) {
        return TickableBlockEntity.createTicker(level);
    }

    @Override
    public @NotNull RenderShape getRenderShape(BlockState blockState) {
        return RenderShape.INVISIBLE;
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        return SHAPE;
    }

    @Override
    public void animateTick(BlockState pState, Level pLevel, BlockPos pPos, RandomSource pRandom) {
        if (pLevel.getBlockEntity(pPos) instanceof CrusherBlockEntity blockEntity) {
            if (blockEntity.getProgress() > 0) {
                ItemStack stack = blockEntity.getWrappedInventoryStorage().getContainer(CrusherBlockEntity.INPUT_SLOT).getItem(0);
                var particle = new ItemParticleOption(ParticleTypes.ITEM, stack);
                for (int i = 0; i < pRandom.nextInt(2) + 1; i++) {
                    pLevel.addParticle(particle, pPos.getX() + 0.25 + nextFloat(pRandom, -0.1f, 0.1f),
                            pPos.getY() + 0.55 + nextFloat(pRandom, -0.1f, 0.1f),
                            pPos.getZ() + 0.25 + nextFloat(pRandom, -0.1f, 0.1f), 0, 0, 0);
                }

                for (int i = 0; i < pRandom.nextInt(2) + 1; i++) {
                    pLevel.addParticle(particle, pPos.getX() + 0.75 + nextFloat(pRandom, -0.1f, 0.1f),
                            pPos.getY() + 0.55 + nextFloat(pRandom, -0.1f, 0.1f),
                            pPos.getZ() + 0.25 + nextFloat(pRandom, -0.1f, 0.1f), 0, 0, 0);
                }

                for (int i = 0; i < pRandom.nextInt(2) + 1; i++) {
                    pLevel.addParticle(particle, pPos.getX() + 0.25 + nextFloat(pRandom, -0.1f, 0.1f),
                            pPos.getY() + 0.55 + nextFloat(pRandom, -0.1f, 0.1f),
                            pPos.getZ() + 0.75 + nextFloat(pRandom, -0.1f, 0.1f), 0, 0, 0);
                }

                for (int i = 0; i < pRandom.nextInt(2) + 1; i++) {
                    pLevel.addParticle(particle, pPos.getX() + 0.75 + nextFloat(pRandom, -0.1f, 0.1f),
                            pPos.getY() + 0.55 + nextFloat(pRandom, -0.1f, 0.1f),
                            pPos.getZ() + 0.75 + nextFloat(pRandom, -0.1f, 0.1f), 0, 0, 0);
                }
            }
        }
    }

    private static float nextFloat(RandomSource random, float min, float max) {
        return min + random.nextFloat() * (max - min);
    }
}
