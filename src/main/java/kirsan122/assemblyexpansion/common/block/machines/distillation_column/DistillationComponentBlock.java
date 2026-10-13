package kirsan122.assemblyexpansion.common.block.machines.distillation_column;

import kirsan122.assemblyexpansion.common.blockEntity.machines.distillation_column.DistillationColumnBlockEntity;
import kirsan122.assemblyexpansion.common.blockEntity.machines.distillation_column.DistillationComponentBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DistillationComponentBlock extends BaseEntityBlock {
    private final DistillationComponentBlockType type;


    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    public static final VoxelShape SHAPE_SECTION = Shapes.or(Block.box(2.0, 0.0, 2.0, 14.0, 16.0, 14.0));


    public static final VoxelShape SHAPE_TOP_NORTH = Shapes.or(
            Block.box(2.0, 0.0, 2.0, 14.0, 9.0, 14.0),
            Block.box(4.0, 11.0, 4.0, 12.0, 13.0, 12.0),
            Block.box(3.0, 9.0, 3.0, 13.0, 11.0, 13.0),
            Block.box(2.0, 14.0, 7.0, 10.0, 15.0, 9.0),
            Block.box(2.0, 9.0, 7.0, 3.0, 14.0, 9.0),
            Block.box(9.0, 13.0, 7.0, 10.0, 14.0, 9.0)
    ).optimize();


    public static final VoxelShape SHAPE_TOP_EAST = Shapes.or(
            Block.box(2.0, 0.0, 2.0, 14.0, 9.0, 14.0),
            Block.box(4.0, 11.0, 4.0, 12.0, 13.0, 12.0),
            Block.box(3.0, 9.0, 3.0, 13.0, 11.0, 13.0),
            Block.box(7.0, 14.0, 2.0, 9.0, 15.0, 10.0),
            Block.box(7.0, 9.0, 2.0, 9.0, 14.0, 3.0),
            Block.box(7.0, 13.0, 9.0, 9.0, 14.0, 10.0)
    ).optimize();


    public static final VoxelShape SHAPE_TOP_SOUTH = Shapes.or(
            Block.box(2.0, 0.0, 2.0, 14.0, 9.0, 14.0),
            Block.box(4.0, 11.0, 4.0, 12.0, 13.0, 12.0),
            Block.box(3.0, 9.0, 3.0, 13.0, 11.0, 13.0),
            Block.box(6.0, 14.0, 7.0, 14.0, 15.0, 9.0),
            Block.box(13.0, 9.0, 7.0, 14.0, 14.0, 9.0),
            Block.box(6.0, 13.0, 7.0, 7.0, 14.0, 9.0)
    ).optimize();


    public static final VoxelShape SHAPE_TOP_WEST = Shapes.or(
            Block.box(2.0, 0.0, 2.0, 14.0, 9.0, 14.0),
            Block.box(4.0, 11.0, 4.0, 12.0, 13.0, 12.0),
            Block.box(3.0, 9.0, 3.0, 13.0, 11.0, 13.0),
            Block.box(7.0, 14.0, 6.0, 9.0, 15.0, 14.0),
            Block.box(7.0, 9.0, 13.0, 9.0, 14.0, 14.0),
            Block.box(7.0, 13.0, 6.0, 9.0, 14.0, 7.0)
    ).optimize();


    public DistillationComponentBlock(@NotNull DistillationComponentBlockType type, Properties properties) {
        super(properties);
        this.type = type;

        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if (type == DistillationComponentBlockType.SECTION) {
            return SHAPE_SECTION;
        }


        return switch (state.getValue(FACING)) {
            case EAST -> SHAPE_TOP_EAST;
            case SOUTH -> SHAPE_TOP_SOUTH;
            case WEST -> SHAPE_TOP_WEST;
            default -> SHAPE_TOP_NORTH;
        };
    }


    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {

        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }


    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }


    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new DistillationComponentBlockEntity(pos, state);
    }
    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide()) {
            BlockEntity be = level.getBlockEntity(pos);


            if (be instanceof DistillationComponentBlockEntity) {

                int distanceToBase = -1;

                if (this.type == DistillationComponentBlockType.TOP) {
                    distanceToBase = 3;
                } else if (this.type == DistillationComponentBlockType.SECTION) {
                    BlockState blockBelow = level.getBlockState(pos.below());
                    if (blockBelow.getBlock() instanceof DistillationComponentBlock lowerComp &&
                            lowerComp.type == DistillationComponentBlockType.SECTION) {
                        distanceToBase = 2;
                    } else {
                        distanceToBase = 1;
                    }
                }

                if (distanceToBase != -1) {

                    BlockEntity baseBE = level.getBlockEntity(pos.below(distanceToBase));


                    if (baseBE instanceof DistillationColumnBlockEntity baseColumn) {
                        NetworkHooks.openScreen((ServerPlayer) player, baseColumn, baseBE.getBlockPos());
                        return InteractionResult.SUCCESS;
                    }
                }
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }
}
