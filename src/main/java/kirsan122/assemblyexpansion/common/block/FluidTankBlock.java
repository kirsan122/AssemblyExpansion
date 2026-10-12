package kirsan122.assemblyexpansion.common.block;

import kirsan122.assemblyexpansion.common.blockEntity.FluidTankBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FluidTankBlock extends BaseEntityBlock {
    protected static final VoxelShape SHAPE = Shapes.or(Block.box(3.0, 0.0, 3.0, 13.0, 16.0, 13.0)).optimize();
    public final int maxCapacity;

    public FluidTankBlock(int maxCapacity, Properties properties) {
        super(properties);
        this.maxCapacity = maxCapacity;
    }

    public int getMaxCapacity() {
        return this.maxCapacity;
    }

    @Override
    public @NotNull RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public VoxelShape getShape(BlockState state, net.minecraft.world.level.BlockGetter level, BlockPos pos, net.minecraft.world.phys.shapes.CollisionContext context) {
        return SHAPE;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new FluidTankBlockEntity(this.maxCapacity, pos, state);
    }

    @Override
    public @NotNull InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof FluidTankBlockEntity tankEntity)) {
            return InteractionResult.PASS;
        }

        ItemStack heldItem = player.getItemInHand(hand);

        var itemFluidCap = heldItem.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM);
        var tankFluidCap = tankEntity.getCapability(ForgeCapabilities.FLUID_HANDLER, hit.getDirection());

        if (itemFluidCap.isPresent() && tankFluidCap.isPresent()) {
            if (!level.isClientSide) {
                IFluidHandlerItem itemHandler = itemFluidCap.orElseThrow(IllegalStateException::new);
                IFluidHandler tankHandler = tankFluidCap.orElseThrow(IllegalStateException::new);

                FluidStack fluidInItem = itemHandler.getFluidInTank(0);
                boolean hasChanged = false;

                if (!fluidInItem.isEmpty()) {



                    int maxToDrain = Math.min(1000, fluidInItem.getAmount());
                    FluidStack simulatedDrain = itemHandler.drain(maxToDrain, IFluidHandler.FluidAction.SIMULATE);

                    if (!simulatedDrain.isEmpty()) {
                        int filledIntoTank = tankHandler.fill(simulatedDrain, IFluidHandler.FluidAction.SIMULATE);

                        if (filledIntoTank > 0) {
                            FluidStack finalFluid = new FluidStack(simulatedDrain.getFluid(), filledIntoTank);
                            tankHandler.fill(finalFluid, IFluidHandler.FluidAction.EXECUTE);

                            if (!player.isCreative()) {
                                itemHandler.drain(finalFluid.getAmount(), IFluidHandler.FluidAction.EXECUTE);
                                player.setItemInHand(hand, itemHandler.getContainer());
                            }


                            level.playSound(null, pos, net.minecraft.sounds.SoundEvents.BUCKET_EMPTY, net.minecraft.sounds.SoundSource.BLOCKS, 1.0F, 1.0F);
                            hasChanged = true;
                        }
                    }
                } else {



                    FluidStack fluidInTank = tankHandler.getFluidInTank(0);

                    if (!fluidInTank.isEmpty()) {
                        int maxToDrainFromTank = Math.min(1000, fluidInTank.getAmount());
                        FluidStack simulatedTankDrain = tankHandler.drain(maxToDrainFromTank, IFluidHandler.FluidAction.SIMULATE);

                        if (!simulatedTankDrain.isEmpty()) {
                            ItemStack singleItemCopy = heldItem.copyWithCount(1);
                            var copyCap = singleItemCopy.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM);

                            if (copyCap.isPresent()) {
                                IFluidHandlerItem copyHandler = copyCap.orElseThrow(IllegalStateException::new);
                                int filledIntoItem = copyHandler.fill(simulatedTankDrain, IFluidHandler.FluidAction.SIMULATE);

                                if (filledIntoItem > 0) {
                                    FluidStack finalFluid = new FluidStack(simulatedTankDrain.getFluid(), filledIntoItem);
                                    copyHandler.fill(finalFluid, IFluidHandler.FluidAction.EXECUTE);

                                    if (player.isCreative()) {
                                        player.setItemInHand(hand, copyHandler.getContainer());
                                    } else {
                                        tankHandler.drain(finalFluid.getAmount(), IFluidHandler.FluidAction.EXECUTE);

                                        if (heldItem.getCount() == 1) {
                                            player.setItemInHand(hand, copyHandler.getContainer());
                                        } else {
                                            heldItem.shrink(1);
                                            ItemStack filledContainer = copyHandler.getContainer();
                                            if (!player.getInventory().add(filledContainer)) {
                                                player.drop(filledContainer, false);
                                            }
                                        }
                                    }


                                    level.playSound(null, pos, net.minecraft.sounds.SoundEvents.BUCKET_FILL, net.minecraft.sounds.SoundSource.BLOCKS, 1.0F, 1.0F);
                                    hasChanged = true;
                                }
                            }
                        }
                    }
                }

                if (hasChanged) {
                    tankEntity.setChangedAndNotify();
                }
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }




        if (!level.isClientSide && player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
            NetworkHooks.openScreen(serverPlayer, tankEntity, pos);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }


    @Override
    public void playerDestroy(@NotNull Level level, @NotNull Player player, @NotNull BlockPos pos, @NotNull BlockState state, BlockEntity blockEntity, @NotNull ItemStack tool) {
        if (!level.isClientSide && blockEntity instanceof FluidTankBlockEntity tankEntity) {

            ItemStack tankItem = new ItemStack(this);

            CompoundTag nbt = new CompoundTag();
            CompoundTag blockEntityTag = new CompoundTag();


            tankEntity.getFluidTank().writeToNBT(blockEntityTag);



            blockEntityTag.put("inventory", tankEntity.getItemHandler().serializeNBT());


            nbt.put("BlockEntityTag", blockEntityTag);
            tankItem.setTag(nbt);


            ItemEntity itemEntity = new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, tankItem);
            itemEntity.setDefaultPickUpDelay();
            level.addFreshEntity(itemEntity);
        }


        super.playerDestroy(level, player, pos, state, blockEntity, tool);
    }
}