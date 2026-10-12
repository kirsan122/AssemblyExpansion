package kirsan122.assemblyexpansion.common.block;

import kirsan122.assemblyexpansion.common.blockEntity.ClayBowlBlockEntity;
import kirsan122.assemblyexpansion.common.init.AEBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

public class ClayBowlBlock extends BaseEntityBlock {

    private static final VoxelShape SHAPE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 4.0D, 14.0D);

    public ClayBowlBlock(Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ClayBowlBlockEntity(pos, state);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide) {
            if (level.getBlockEntity(pos) instanceof ClayBowlBlockEntity bowlEntity) {
                ItemStack heldItem = player.getItemInHand(hand);


                if (!heldItem.isEmpty()) {

                    ItemStack toInsert = heldItem.copyWithCount(1);
                    ItemStack remainder = bowlEntity.getItemHandler().insertItem(0, toInsert, false);


                    if (remainder.isEmpty()) {
                        if (!player.getAbilities().instabuild) {
                            heldItem.shrink(1);
                        }


                        bowlEntity.setChanged();
                        level.sendBlockUpdated(pos, state, state, 3);

                        return InteractionResult.SUCCESS;
                    }
                }

                else {
                    ItemStack extracted = bowlEntity.extractItems();
                    if (!extracted.isEmpty()) {
                        net.minecraftforge.items.ItemHandlerHelper.giveItemToPlayer(player, extracted);


                        bowlEntity.setChanged();
                        level.sendBlockUpdated(pos, state, state, 3);

                        return InteractionResult.SUCCESS;
                    }
                }
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }


    @Override
    public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack tool) {

        player.awardStat(net.minecraft.stats.Stats.BLOCK_MINED.get(this));
        player.causeFoodExhaustion(0.005F);


        if (!level.isClientSide && blockEntity instanceof ClayBowlBlockEntity bowl && !player.isCreative()) {
            ItemStack drop = new ItemStack(this);
            CompoundTag tag = new CompoundTag();


            bowl.saveToItem(tag);

            if (!tag.isEmpty()) {
                CompoundTag blockEntityTag = new CompoundTag();
                blockEntityTag.put("BlockEntityTag", tag);
                drop.setTag(blockEntityTag);
            }


            Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), drop);
        }
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {

        if (!state.is(newState.getBlock())) {

            super.onRemove(state, level, pos, newState, isMoving);
        }
    }


    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos groundPos = pos.below();
        return level.getBlockState(groundPos).isFaceSturdy(level, groundPos, Direction.UP);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos currentPos, BlockPos neighborPos) {
        if (direction == Direction.DOWN && !state.canSurvive(level, currentPos)) {

            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(state, direction, neighborState, level, currentPos, neighborPos);
    }
    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, net.minecraft.world.level.block.entity.BlockEntityType<T> type) {

        return !level.isClientSide ? createTickerHelper(type, AEBlockEntities.CLAY_BOWL_BE.get(), ClayBowlBlockEntity::tick) : null;
    }

}
