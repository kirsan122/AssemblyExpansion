package kirsan122.assemblyexpansion.common.block.hevea;

import kirsan122.assemblyexpansion.common.blockEntity.ClayBowlBlockEntity;
import kirsan122.assemblyexpansion.common.item.tools.KnifeItem;
import kirsan122.assemblyexpansion.common.init.AEBlocks;
import kirsan122.assemblyexpansion.common.init.AEParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.ToolActions;

public class HeveaBlock extends RotatedPillarBlock {
    public static final BooleanProperty PLACED_BY_PLAYER = BooleanProperty.create("placed_by_player");
    public static final DirectionProperty SCRATCHED_SIDE = DirectionProperty.create("scratched_side", Direction.Plane.HORIZONTAL);
    public static final IntegerProperty DRY_STAGE = IntegerProperty.create("dry_stage", 0, 3);
    public static final int MAX_FILLING = 250;

    public HeveaBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(AXIS, Direction.Axis.Y)
                .setValue(SCRATCHED_SIDE, Direction.NORTH)
                .setValue(DRY_STAGE, 0)
                .setValue(PLACED_BY_PLAYER, false));

    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.canPerformAction(KnifeItem.KNIFE_SCRATCH)) {
            if (state.getValue(PLACED_BY_PLAYER)) {
                if (level.isClientSide) {
                    player.displayClientMessage(Component.translatable("tooltip.assembly_expansion.cannot_collect_latex").withStyle(net.minecraft.ChatFormatting.RED), true);
                }
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
            if (state.getValue(DRY_STAGE) == 0 && hit.getDirection().getAxis().isHorizontal()) {
                if (!level.isClientSide) {
                    level.setBlock(pos, state.setValue(SCRATCHED_SIDE, hit.getDirection()).setValue(DRY_STAGE, 3), 3);
                    stack.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(hand));
                }
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
        }
        return InteractionResult.PASS;
    }
    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        int stage = state.getValue(DRY_STAGE);
        if (stage == 0) return;
        Direction dir = state.getValue(SCRATCHED_SIDE);
        Direction sideDir = dir.getClockWise();

        int spawnChance;
        double offsetXZ;
        double offsetY;

        if (stage == 3) {
            spawnChance = 7;
            offsetXZ = (random.nextFloat() - 0.5f) * 0.4f;
            offsetY = offsetXZ;
        } else if (stage == 2) {
            spawnChance = 9;
            offsetXZ = (random.nextFloat() - 0.5f) * 0.3f;
            offsetY = offsetXZ;
        } else {
            spawnChance = 12;
            offsetXZ = (random.nextFloat() - 0.5f) * 0.2f;
            offsetY = offsetXZ;
        }

        if (random.nextInt(spawnChance) == 0) {


            double x = pos.getX() + 0.5 + (dir.getStepX() * 0.5625d) + (sideDir.getStepX() * offsetXZ);
            double y = pos.getY() + 0.4 + offsetY;

            double z = pos.getZ() + 0.5 + (dir.getStepZ() * 0.5625d) + (sideDir.getStepZ() * offsetXZ);

            level.addParticle(AEParticles.LATEX_DROP.get(), x, y, z, 0, 0, 0);
        }

    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {

        if (state.getValue(PLACED_BY_PLAYER)) return;

        int stage = state.getValue(DRY_STAGE);
        if (stage > 0) {
            Direction dir = state.getValue(SCRATCHED_SIDE);


            BlockPos.MutableBlockPos checkPos = pos.relative(dir).below().mutable();


            for (int i = 0; i < 10; i++) {

                if (level.getBlockEntity(checkPos) instanceof ClayBowlBlockEntity bowl) {

                    bowl.fill(MAX_FILLING);
                    break;
                }


                if (level.getBlockState(checkPos).isCollisionShapeFullBlock(level, checkPos)) {
                    break;
                }


                checkPos.move(Direction.DOWN);
            }


            if (random.nextInt(35) == 0) {
                int nextStage = stage - 1;
                BlockState newState = state.setValue(DRY_STAGE, nextStage);

                if (nextStage == 0) {
                    newState = newState.setValue(SCRATCHED_SIDE, Direction.NORTH);
                }
                level.setBlock(pos, newState, 3);
            }
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {

        builder.add(AXIS, SCRATCHED_SIDE, DRY_STAGE, PLACED_BY_PLAYER);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {

        return super.getStateForPlacement(context).setValue(PLACED_BY_PLAYER, true);
    }

    @Override
    public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return true;
    }

    @Override
    public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return 5;
    }

    @Override
    public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return 5;
    }


    @Override
    public BlockState getToolModifiedState(BlockState state, UseOnContext context, net.minecraftforge.common.ToolAction toolAction, boolean simulate) {
        if (toolAction == ToolActions.AXE_STRIP) {

            return AEBlocks.STRIPPED_HEVEA_LOG.get().defaultBlockState().setValue(AXIS, state.getValue(AXIS));
        }
        return super.getToolModifiedState(state, context, toolAction, simulate);
    }

}