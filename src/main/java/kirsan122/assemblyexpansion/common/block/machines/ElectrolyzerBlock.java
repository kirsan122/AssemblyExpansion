package kirsan122.assemblyexpansion.common.block.machines;

import kirsan122.assemblyexpansion.common.blockEntity.machines.ElectrolyzerBlockEntity;
import kirsan122.assemblyexpansion.common.init.AEBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;

public class ElectrolyzerBlock extends BaseEntityBlock {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    protected static final VoxelShape SHAPE_NORTH = Shapes.or(
            Block.box(9, 0, 6, 15, 14, 12), Block.box(1, 0, 6, 7, 14, 12),
            Block.box(12, 14, 8, 13, 16, 10), Block.box(3, 14, 8, 4, 16, 10),
            Block.box(4, 15, 8, 12, 16, 10), Block.box(0, 0, 12, 16, 16, 16),
            Block.box(7, 0, 6, 9, 13, 12), Block.box(4, 9, 3, 12, 13, 6),
            Block.box(0, 0, 0, 16, 9, 6), Block.box(2, 11, 4, 3, 12, 6),
            Block.box(13, 11, 4, 14, 12, 6), Block.box(3, 11, 4, 4, 12, 5),
            Block.box(12, 11, 4, 13, 12, 5)
    ).optimize();
    protected static final VoxelShape SHAPE_EAST = Shapes.or(
            Block.box(4, 0, 9, 10, 14, 15), Block.box(4, 0, 1, 10, 14, 7),
            Block.box(6, 14, 12, 8, 16, 13), Block.box(6, 14, 3, 8, 16, 4),
            Block.box(6, 15, 4, 8, 16, 12), Block.box(0, 0, 0, 4, 16, 16),
            Block.box(4, 0, 7, 10, 13, 9), Block.box(10, 9, 4, 13, 13, 12),
            Block.box(10, 0, 0, 16, 9, 16), Block.box(10, 11, 2, 12, 12, 3),
            Block.box(10, 11, 13, 12, 12, 14), Block.box(11, 11, 3, 12, 12, 4),
            Block.box(11, 11, 12, 12, 12, 13)
    ).optimize();


    protected static final VoxelShape SHAPE_SOUTH = Shapes.or(
            Block.box(1, 0, 4, 7, 14, 10), Block.box(9, 0, 4, 15, 14, 10),
            Block.box(3, 14, 6, 4, 16, 8), Block.box(12, 14, 6, 13, 16, 8),
            Block.box(4, 15, 6, 12, 16, 8), Block.box(0, 0, 0, 16, 16, 4),
            Block.box(7, 0, 4, 9, 13, 10), Block.box(4, 9, 10, 12, 13, 13),
            Block.box(0, 0, 10, 16, 9, 16), Block.box(13, 11, 10, 14, 12, 12),
            Block.box(2, 11, 10, 3, 12, 12), Block.box(12, 11, 11, 13, 12, 12),
            Block.box(3, 11, 11, 4, 12, 12)
    ).optimize();


    protected static final VoxelShape SHAPE_WEST = Shapes.or(
            Block.box(6, 0, 1, 12, 14, 7), Block.box(6, 0, 9, 12, 14, 15),
            Block.box(8, 14, 3, 10, 16, 4), Block.box(8, 14, 12, 10, 16, 13),
            Block.box(8, 15, 4, 10, 16, 12), Block.box(12, 0, 0, 16, 16, 16),
            Block.box(6, 0, 7, 12, 13, 9), Block.box(3, 9, 4, 6, 13, 12),
            Block.box(0, 0, 0, 6, 9, 16), Block.box(4, 11, 13, 6, 12, 14),
            Block.box(4, 11, 2, 6, 12, 3), Block.box(4, 11, 12, 5, 12, 13),
            Block.box(4, 11, 3, 5, 12, 4)
    ).optimize();


    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {

        switch (state.getValue(FACING)) {
            case EAST -> {
                return SHAPE_EAST;
            }
            case SOUTH -> {
                return SHAPE_SOUTH;
            }
            case WEST -> {
                return SHAPE_WEST;
            }
            default -> {
                return SHAPE_NORTH;
            }
        }
    }

    public ElectrolyzerBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide()) {
            BlockEntity entity = level.getBlockEntity(pos);
            if (entity instanceof ElectrolyzerBlockEntity) {

                NetworkHooks.openScreen((ServerPlayer) player, (ElectrolyzerBlockEntity) entity, buf -> buf.writeBlockPos(pos));
            } else {
                throw new IllegalStateException("NotContainer");
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }



    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ElectrolyzerBlockEntity(pos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {

        return createTickerHelper(type, AEBlockEntities.ELECTROLYZER_BE.get(), ElectrolyzerBlockEntity::tick);
    }

    @Override
    public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        BlockEntity blockEntity = level.getBlockEntity(pos);

        if (blockEntity instanceof ElectrolyzerBlockEntity electrolyzerTile && !level.isClientSide() && !player.isCreative()) {

            ItemStack dropStack = new ItemStack(this);


            CompoundTag blockEntityTag = new CompoundTag();
            electrolyzerTile.saveAdditional(blockEntityTag);


            CompoundTag mainTag = new CompoundTag();
            mainTag.put("BlockEntityTag", blockEntityTag);
            dropStack.setTag(mainTag);


            ItemEntity itemEntity = new ItemEntity(
                    level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, dropStack
            );
            itemEntity.setDefaultPickUpDelay();
            level.addFreshEntity(itemEntity);
        }

        super.playerWillDestroy(level, pos, state, player);
    }


    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof ElectrolyzerBlockEntity) {


                level.removeBlockEntity(pos);
            }
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }


    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {

        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

}
