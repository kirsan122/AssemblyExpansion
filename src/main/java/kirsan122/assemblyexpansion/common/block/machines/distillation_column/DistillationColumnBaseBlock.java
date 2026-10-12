package kirsan122.assemblyexpansion.common.block.machines.distillation_column;

import kirsan122.assemblyexpansion.common.blockEntity.machines.distillation_column.DistillationColumnBlockEntity;
import kirsan122.assemblyexpansion.common.init.AEBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
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
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;


public class DistillationColumnBaseBlock extends BaseEntityBlock {
    public static final BooleanProperty IS_STRUCTURE_VALID = BooleanProperty.create("is_structure_valid");
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final VoxelShape SHAPE = Shapes.or(
            Block.box(0.0, 0.0, 0.0, 16.0, 12.0, 16.0),
            Block.box(2.0, 13.0, 2.0, 14.0, 16.0, 14.0),
            Block.box(3.0, 13.0, 1.0, 13.0, 15.0, 2.0),
            Block.box(3.0, 13.0, 14.0, 13.0, 15.0, 15.0),
            Block.box(1.0, 13.0, 3.0, 2.0, 15.0, 13.0),
            Block.box(14.0, 13.0, 3.0, 15.0, 15.0, 13.0),
            Block.box(1.0, 12.0, 14.0, 15.0, 13.0, 15.0),
            Block.box(1.0, 12.0, 1.0, 15.0, 13.0, 2.0),
            Block.box(1.0, 12.0, 2.0, 2.0, 13.0, 14.0),
            Block.box(14.0, 12.0, 2.0, 15.0, 13.0, 14.0)
    ).optimize();

    public DistillationColumnBaseBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(IS_STRUCTURE_VALID, false).setValue(FACING, Direction.NORTH));
    }
    @Override
    public VoxelShape getShape(BlockState state, net.minecraft.world.level.BlockGetter level, BlockPos pos, net.minecraft.world.phys.shapes.CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, IS_STRUCTURE_VALID);
    }

    @Override public RenderShape getRenderShape(BlockState state) { return RenderShape.MODEL; }


    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {

        return AEBlockEntities.DISTILLATION_COLUMN_BE.get().create(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {

        return createTickerHelper(type, AEBlockEntities.DISTILLATION_COLUMN_BE.get(), DistillationColumnBlockEntity::tick);
    }
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {

        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide()) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof DistillationColumnBlockEntity baseColumn) {

                if (!baseColumn.isStructureValid()) {
                    return InteractionResult.FAIL;
                }


                net.minecraftforge.network.NetworkHooks.openScreen((ServerPlayer) player, baseColumn, pos);
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }



}

