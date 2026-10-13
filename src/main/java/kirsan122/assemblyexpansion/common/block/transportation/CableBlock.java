package kirsan122.assemblyexpansion.common.block.transportation;


import kirsan122.assemblyexpansion.common.blockEntity.transportation.CableBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import org.jetbrains.annotations.NotNull;

import static kirsan122.assemblyexpansion.common.init.AEDamageTypes.ELECTRIC_SHOCK;

public class CableBlock extends BaseEntityBlock {
    public static final BooleanProperty NORTH = BlockStateProperties.NORTH;
    public static final BooleanProperty SOUTH = BlockStateProperties.SOUTH;
    public static final BooleanProperty EAST = BlockStateProperties.EAST;
    public static final BooleanProperty WEST = BlockStateProperties.WEST;
    public static final BooleanProperty UP = BlockStateProperties.UP;
    public static final BooleanProperty DOWN = BlockStateProperties.DOWN;

    private static final VoxelShape SHAPE_CENTER = Block.box(6.0D, 6.0D, 6.0D, 10.0D, 10.0D, 10.0D);
    private static final VoxelShape SHAPE_UP = Block.box(6.0D, 10.0D, 6.0D, 10.0D, 16.0D, 10.0D);
    private static final VoxelShape SHAPE_DOWN = Block.box(6.0D, 0.0D, 6.0D, 10.0D, 6.0D, 10.0D);
    private static final VoxelShape SHAPE_NORTH = Block.box(6.0D, 6.0D, 0.0D, 10.0D, 10.0D, 6.0D);
    private static final VoxelShape SHAPE_SOUTH = Block.box(6.0D, 6.0D, 10.0D, 10.0D, 10.0D, 16.0D);
    private static final VoxelShape SHAPE_WEST = Block.box(0.0D, 6.0D, 6.0D, 6.0D, 10.0D, 10.0D);
    private static final VoxelShape SHAPE_EAST = Block.box(10.0D, 6.0D, 6.0D, 16.0D, 10.0D, 10.0D);

    public final int maxTransfer;
    public final boolean dealsDamage;

    public CableBlock(int maxTransfer, boolean dealsDamage, Properties properties) {
        super(properties.noOcclusion().dynamicShape());
        this.maxTransfer = maxTransfer;
        this.dealsDamage = dealsDamage;
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(NORTH, false).setValue(SOUTH, false)
                .setValue(EAST, false).setValue(WEST, false)
                .setValue(UP, false).setValue(DOWN, false));
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CableBlockEntity(pos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }


    private boolean canConnectTo(LevelAccessor level, BlockPos currentPos, Direction direction) {
        BlockPos neighborPos = currentPos.relative(direction);
        BlockEntity neighborBE = level.getBlockEntity(neighborPos);
        if (neighborBE == null) return false;


        return neighborBE.getCapability(ForgeCapabilities.ENERGY, direction.getOpposite()).isPresent();
    }


    @Override
    public BlockState getStateForPlacement(net.minecraft.world.item.context.BlockPlaceContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        return this.defaultBlockState()
                .setValue(NORTH, canConnectTo(level, pos, Direction.NORTH))
                .setValue(SOUTH, canConnectTo(level, pos, Direction.SOUTH))
                .setValue(EAST, canConnectTo(level, pos, Direction.EAST))
                .setValue(WEST, canConnectTo(level, pos, Direction.WEST))
                .setValue(UP, canConnectTo(level, pos, Direction.UP))
                .setValue(DOWN, canConnectTo(level, pos, Direction.DOWN));
    }


    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos currentPos, BlockPos neighborPos) {

        switch (direction) {
            case NORTH -> {
                return state.setValue(NORTH, canConnectTo(level, currentPos, Direction.NORTH));
            }
            case SOUTH -> {
                return state.setValue(SOUTH, canConnectTo(level, currentPos, Direction.SOUTH));
            }
            case EAST -> {
                return state.setValue(EAST, canConnectTo(level, currentPos, Direction.EAST));
            }
            case WEST -> {
                return state.setValue(WEST, canConnectTo(level, currentPos, Direction.WEST));
            }
            case UP -> {
                return state.setValue(UP, canConnectTo(level, currentPos, Direction.UP));
            }
            case DOWN -> {
                return state.setValue(DOWN, canConnectTo(level, currentPos, Direction.DOWN));
            }
        }
        return super.updateShape(state, direction, neighborState, level, currentPos, neighborPos);
    }


    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(NORTH, SOUTH, EAST, WEST, UP, DOWN);
    }


    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (!this.dealsDamage) return;

        if (!level.isClientSide() && entity instanceof LivingEntity livingEntity) {
            BlockEntity blockEntity = level.getBlockEntity(pos);

            if (blockEntity instanceof CableBlockEntity cableBE) {
                if (cableBE.getEnergyTransferredThisTick() > 0) {



                    float baseDamage = 2.0F;

                    Difficulty difficulty = level.getDifficulty();

                    if (difficulty == Difficulty.EASY) {
                        baseDamage = 1.0F;
                    } else if (difficulty == Difficulty.HARD) {
                        baseDamage = 3.0F;
                    }



                    float metalPenalty = 0.0F;

                    for (ItemStack armorStack : livingEntity.getArmorSlots()) {
                        if (!armorStack.isEmpty() && armorStack.getItem() instanceof ArmorItem armorItem) {
                            var material = armorItem.getMaterial();


                            if (material == ArmorMaterials.IRON ||
                                    material == ArmorMaterials.GOLD ||
                                    material == ArmorMaterials.CHAIN) {



                                metalPenalty += 0.75F;
                            }
                        }
                    }


                    float finalDamage = baseDamage + metalPenalty;


                    var damageTypeHolder = level.registryAccess()
                            .registryOrThrow(net.minecraft.core.registries.Registries.DAMAGE_TYPE)
                            .getHolderOrThrow(ELECTRIC_SHOCK);

                    var damageSource = new net.minecraft.world.damagesource.DamageSource(damageTypeHolder);






                    boolean hasInsulation = false;
                    for (ItemStack armorStack : livingEntity.getArmorSlots()) {
                        if (!armorStack.isEmpty() && armorStack.getItem() instanceof ArmorItem armorItem) {
                            var mat = armorItem.getMaterial();
                            if (mat == ArmorMaterials.DIAMOND || mat == ArmorMaterials.NETHERITE) {
                                hasInsulation = true;
                            }
                        }
                    }



                    if (hasInsulation) {
                        finalDamage *= 0.4F;
                    }


                    if (finalDamage < 0.5F) finalDamage = 0.5F;

                    livingEntity.hurt(damageSource, finalDamage);
                }
            }
        }
        super.entityInside(state, level, pos, entity);
    }



    @Override
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        return super.getAnalogOutputSignal(state, level, pos);
    }


    @Override
    public BlockState rotate(BlockState state, net.minecraft.world.level.block.Rotation rotation) {
        return super.rotate(state, rotation);
    }

    @Override
    public float getShadeBrightness(BlockState state, net.minecraft.world.level.BlockGetter world, BlockPos pos) {
        return 1.0F;
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, net.minecraft.world.phys.shapes.CollisionContext context) {

        VoxelShape shape = SHAPE_CENTER;


        if (state.getValue(UP))    shape = net.minecraft.world.phys.shapes.Shapes.or(shape, SHAPE_UP);
        if (state.getValue(DOWN))  shape = net.minecraft.world.phys.shapes.Shapes.or(shape, SHAPE_DOWN);
        if (state.getValue(NORTH)) shape = net.minecraft.world.phys.shapes.Shapes.or(shape, SHAPE_NORTH);
        if (state.getValue(SOUTH)) shape = net.minecraft.world.phys.shapes.Shapes.or(shape, SHAPE_SOUTH);
        if (state.getValue(WEST))  shape = net.minecraft.world.phys.shapes.Shapes.or(shape, SHAPE_WEST);
        if (state.getValue(EAST))  shape = net.minecraft.world.phys.shapes.Shapes.or(shape, SHAPE_EAST);

        return shape;
    }
}
