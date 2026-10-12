package kirsan122.assemblyexpansion.common.blockEntity;

import kirsan122.assemblyexpansion.common.init.AEBlockEntities;
import kirsan122.assemblyexpansion.common.init.AEFluids;
import kirsan122.assemblyexpansion.common.init.AEItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class ClayBowlBlockEntity extends BlockEntity {

    private final FluidTank fluidTank = new FluidTank(1000);


    private final ItemStackHandler itemHandler = new ItemStackHandler(1) {
        @Override
        public int getSlotLimit(int slot) {
            return 4;
        }

        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            setChanged();
        }
    };

    private final LazyOptional<IFluidHandler> fluidHandlerLazyOptional = LazyOptional.of(() -> fluidTank);
    private final LazyOptional<ItemStackHandler> itemHandlerLazyOptional = LazyOptional.of(() -> itemHandler);

    public ClayBowlBlockEntity(BlockPos pos, BlockState state) {
        super(AEBlockEntities.CLAY_BOWL_BE.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, ClayBowlBlockEntity entity) {

        if (entity.fluidTank.getFluidAmount() >= 1000) {


            ItemStack remainder = entity.itemHandler.insertItem(0, new ItemStack(AEItems.LATEX_DISC.get(), 4), true);


            if (remainder.isEmpty()) {

                entity.fluidTank.drain(1000, IFluidHandler.FluidAction.EXECUTE);


                entity.itemHandler.insertItem(0, new ItemStack(AEItems.LATEX_DISC.get(), 4), false);


                entity.setChanged();
                level.sendBlockUpdated(pos, state, state, 3);


                level.playSound(null, pos, SoundEvents.ITEM_FRAME_ADD_ITEM, net.minecraft.sounds.SoundSource.BLOCKS, 0.6F, 1.0F);
            }

        }
    }

    public int getFluidAmount() {
        return fluidTank.getFluidAmount();
    }

    public ItemStack extractItems() {

        ItemStack extracted = itemHandler.extractItem(0, 4, false);

        if (!extracted.isEmpty()) {

            setChanged();
            if (level != null) {
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
            }
        }
        return extracted;
    }

    public ItemStackHandler getItemHandler() {
        return this.itemHandler;
    }

    public void saveToItem(CompoundTag tag) {
        fluidTank.writeToNBT(tag);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("inventory", itemHandler.serializeNBT());
        fluidTank.writeToNBT(tag);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        fluidTank.readFromNBT(tag);
        if (tag.contains("inventory")) {
            itemHandler.deserializeNBT(tag.getCompound("inventory"));


            ItemStack stack = itemHandler.getStackInSlot(0);
            if (!stack.isEmpty() && stack.getCount() > 4) {
                stack.setCount(4);
            }
        }
    }


    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        saveAdditional(tag);
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        super.handleUpdateTag(tag);
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public void fill(int amount) {
        fluidTank.fill(new FluidStack(AEFluids.LATEX.getSource(), amount), IFluidHandler.FluidAction.EXECUTE);
        setChanged();
        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.FLUID_HANDLER) {
            return fluidHandlerLazyOptional.cast();
        }

        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return itemHandlerLazyOptional.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        fluidHandlerLazyOptional.invalidate();
        itemHandlerLazyOptional.invalidate();
    }
}
