package kirsan122.assemblyexpansion.common.blockEntity;

import kirsan122.assemblyexpansion.common.inventory.FluidTankMenu;
import kirsan122.assemblyexpansion.common.init.AEBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FluidTankBlockEntity extends BlockEntity implements MenuProvider {

    private final int maxCapacity;
    private final FluidTank fluidTank;
    public FluidTankBlockEntity(int maxCapacity, BlockPos pos, BlockState state) {
        super(AEBlockEntities.FLUID_TANK_BE.get(), pos, state);
        this.maxCapacity = maxCapacity;
        this.fluidTank  = new FluidTank(this.maxCapacity) {
            @Override
            protected void onContentsChanged() {
                super.onContentsChanged();
                setChangedAndNotify();
            }
        };
        this.fluidTankLazyOptional = LazyOptional.of(() -> fluidTank);
    }

    private final ItemStackHandler itemHandler = new ItemStackHandler(2) {
        @Override
        public void onContentsChanged(int slot) {
            super.onContentsChanged(slot);


            if (level != null && !level.isClientSide) {

                handleFluidTransfer();

                setChangedAndNotify();
            }
        }
        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).isPresent();
        }
    };


    private final LazyOptional<IFluidHandler> fluidTankLazyOptional;
    private final LazyOptional<IItemHandler> itemHandlerLazyOptional = LazyOptional.of(() -> itemHandler);


    public FluidTank getFluidTank() {
        return this.fluidTank;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, @NotNull Inventory inventory, @NotNull Player player) {
        return new FluidTankMenu(containerId, inventory, this);
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("block.assembly_expansion.fluid_tank");
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("tank", this.fluidTank.writeToNBT(new CompoundTag()));

        tag.put("inventory", this.itemHandler.serializeNBT());
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        if (tag.contains("tank")) {
            this.fluidTank.readFromNBT(tag.getCompound("tank"));
        }
        if (tag.contains("inventory")) {
            this.itemHandler.deserializeNBT(tag.getCompound("inventory"));
        }
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.FLUID_HANDLER) {
            return fluidTankLazyOptional.cast();
        }
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return itemHandlerLazyOptional.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        fluidTankLazyOptional.invalidate();
        itemHandlerLazyOptional.invalidate();
    }

    @Override
    public net.minecraft.world.phys.AABB getRenderBoundingBox() {

        return new net.minecraft.world.phys.AABB(worldPosition);
    }

    @Override
    public void onDataPacket(net.minecraft.network.Connection net, net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket pkt) {
        CompoundTag tag = pkt.getTag();
        if (tag != null) {

            if (tag.contains("tank")) {
                this.fluidTank.readFromNBT(tag.getCompound("tank"));
            }
            if (this.level != null && this.level.isClientSide) {
                this.level.sendBlockUpdated(this.worldPosition, getBlockState(), getBlockState(), 3);
            }
        }
    }


    @Override
    public @NotNull CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        saveAdditional(tag);
        return tag;
    }


    @Nullable
    @Override
    public net.minecraft.network.protocol.Packet<net.minecraft.network.protocol.game.ClientGamePacketListener> getUpdatePacket() {
        return net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket.create(this);
    }


    public void setChangedAndNotify() {
        setChanged();
        if (this.level != null) {

            this.level.sendBlockUpdated(this.worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    public ItemStackHandler getItemHandler() {
        return this.itemHandler;
    }

    private void handleFluidTransfer() {



        ItemStack inputStack = itemHandler.getStackInSlot(0);
        if (!inputStack.isEmpty()) {

            FluidUtil.getFluidHandler(inputStack).ifPresent(fluidHandlerItem -> {
                FluidStack fluidInItem = fluidHandlerItem.getFluidInTank(0);

                if (!fluidInItem.isEmpty()) {

                    int fillableAmount = fluidTank.fill(fluidInItem, IFluidHandler.FluidAction.SIMULATE);

                    if (fillableAmount > 0) {

                        FluidStack drained = fluidHandlerItem.drain(fillableAmount, IFluidHandler.FluidAction.EXECUTE);
                        if (!drained.isEmpty()) {
                            fluidTank.fill(drained, IFluidHandler.FluidAction.EXECUTE);


                            itemHandler.setStackInSlot(0, fluidHandlerItem.getContainer());
                        }
                    }
                }
            });
        }




        ItemStack outputStack = itemHandler.getStackInSlot(1);
        if (!outputStack.isEmpty() && !fluidTank.getFluid().isEmpty()) {


            FluidUtil.getFluidHandler(outputStack).ifPresent(fluidHandlerItem -> {
                FluidStack fluidInItem = fluidHandlerItem.getFluidInTank(0);
                if (!fluidInItem.isEmpty() && !fluidInItem.isFluidEqual(fluidTank.getFluid())) {
                    return;
                }


                int fillableInItem = fluidHandlerItem.fill(fluidTank.getFluid(), IFluidHandler.FluidAction.SIMULATE);

                if (fillableInItem > 0) {

                    FluidStack drainedFromTank = fluidTank.drain(fillableInItem, IFluidHandler.FluidAction.EXECUTE);

                    if (!drainedFromTank.isEmpty()) {

                        ItemStack singleItemCopy = outputStack.copyWithCount(1);
                        FluidUtil.getFluidHandler(singleItemCopy).ifPresent(handler -> {
                            handler.fill(drainedFromTank, IFluidHandler.FluidAction.EXECUTE);
                            ItemStack filledResult = handler.getContainer();


                            outputStack.shrink(1);
                            itemHandler.setStackInSlot(1, outputStack);


                            ItemStack slot0Stack = itemHandler.getStackInSlot(0);
                            if (slot0Stack.isEmpty()) {
                                itemHandler.setStackInSlot(0, filledResult);
                            } else if (ItemStack.isSameItemSameTags(slot0Stack, filledResult) && slot0Stack.getCount() < slot0Stack.getMaxStackSize()) {
                                slot0Stack.grow(1);
                                itemHandler.setStackInSlot(0, slot0Stack);
                            } else {

                                assert level != null;
                                net.minecraft.world.entity.item.ItemEntity itemEntity = new net.minecraft.world.entity.item.ItemEntity(
                                        level, worldPosition.getX() + 0.5, worldPosition.getY() + 1.1, worldPosition.getZ() + 0.5, filledResult
                                );
                                level.addFreshEntity(itemEntity);
                            }
                        });
                    }
                }
            });
        }
    }

}
