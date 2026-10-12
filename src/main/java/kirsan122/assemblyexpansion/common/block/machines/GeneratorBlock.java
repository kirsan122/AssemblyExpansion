package kirsan122.assemblyexpansion.common.block.machines;

import kirsan122.assemblyexpansion.common.blockEntity.machines.GeneratorBlockEntity;
import kirsan122.assemblyexpansion.common.init.AEBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class GeneratorBlock extends BaseEntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");

    public GeneratorBlock(Properties p) {
        super(p.lightLevel(state -> state.getValue(ACTIVE) ? 13 : 0));
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(ACTIVE, false)
        );
    }


    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection().getOpposite())
                .setValue(ACTIVE, false);
    }



    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, ACTIVE);
    }

    @Override
    public @NotNull RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }


    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new GeneratorBlockEntity(pos, state);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide()) {
            BlockEntity entity = level.getBlockEntity(pos);
            if (entity instanceof GeneratorBlockEntity) {
                NetworkHooks.openScreen((ServerPlayer) player, (MenuProvider) entity, pos);

            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {

        return createTickerHelper(type, AEBlockEntities.GENERATOR_BE.get(), GeneratorBlockEntity::tick);
    }

    @Override
    public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack tool) {
        if (!level.isClientSide && !player.isCreative()) {
            ItemStack dropItem = new ItemStack(this);

            if (blockEntity instanceof GeneratorBlockEntity generatorBe) {
                CompoundTag blockEntityTag = new CompoundTag();


                generatorBe.saveAdditional(blockEntityTag);




                if (blockEntityTag.contains("energy")) {
                    CompoundTag energyTag = blockEntityTag.getCompound("energy");






                    generatorBe.getCapability(net.minecraftforge.common.capabilities.ForgeCapabilities.ENERGY).ifPresent(storage -> {
                        energyTag.putInt("Energy", storage.getEnergyStored());
                    });
                }

                dropItem.getOrCreateTag().put("BlockEntityTag", blockEntityTag);
            }

            Block.popResource(level, pos, dropItem);
        }

        super.playerDestroy(level, player, pos, state, blockEntity, tool);
    }



    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {


        return Collections.emptyList();
    }


}

