package kirsan122.assemblyexpansion.common.blockEntity.machines;

import kirsan122.assemblyexpansion.api.GuiConfigHelper;
import kirsan122.assemblyexpansion.api.RedstoneModes;
import kirsan122.assemblyexpansion.common.block.machines.AirCollectorBlock;
import kirsan122.assemblyexpansion.common.energy.DynamicEnergyStorage;
import kirsan122.assemblyexpansion.common.inventory.AirCollectorMenu;
import kirsan122.assemblyexpansion.common.item.upgrades.EnergyUpgradeItem;
import kirsan122.assemblyexpansion.common.init.AEBlockEntities;
import kirsan122.assemblyexpansion.common.init.AEFluids;
import kirsan122.assemblyexpansion.common.init.AEItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AirCollectorBlockEntity extends ConfigurableBlockEntity implements MenuProvider {
    private final int maxEnergyStorage = 10000;
    private boolean hasValidBatteryInSlot = false;
    protected final ContainerData data;

    public boolean inventoryChanged = true;

    private final FluidTank tank = new FluidTank(8000) {
        @Override
        protected void onContentsChanged() {
            setChanged();
            if (level != null && !level.isClientSide) {
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
            }
        }
    };
    private LazyOptional<IFluidHandler> handler = LazyOptional.of(() -> tank);

    private final ItemStackHandler itemHandler = new ItemStackHandler(3) {
        @Override
        protected void onContentsChanged(int slot) {
            onInventorySlotChanged(slot);
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            if (slot < 2) return stack.getCapability(ForgeCapabilities.FLUID_HANDLER).isPresent();
            return stack.getCapability(ForgeCapabilities.ENERGY).isPresent();
        }
    };

    private void onInventorySlotChanged(int slot) {

        this.inventoryChanged = true;


        setChanged();


        if (slot == 2) {
            ItemStack stack = this.itemHandler.getStackInSlot(2);
            if (!stack.isEmpty()) {
                this.hasValidBatteryInSlot = stack.getCapability(net.minecraftforge.common.capabilities.ForgeCapabilities.ENERGY)
                        .map(net.minecraftforge.energy.IEnergyStorage::canExtract)
                        .orElse(false);
            } else {
                this.hasValidBatteryInSlot = false;
            }
        }
    }

    public AirCollectorBlockEntity(BlockPos pos, BlockState state) {
        super(AEBlockEntities.AIR_COLLECTOR_BE.get(), pos, state);
        this.data = new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 2 -> AirCollectorBlockEntity.this.energyStorage.getEnergyStored();
                    case 3 -> AirCollectorBlockEntity.this.energyStorage.getMaxEnergyStored();
                    case 4 -> AirCollectorBlockEntity.this.redstoneMode.ordinal();
                    case 5, 6, 7, 8, 9, 10 -> AirCollectorBlockEntity.this.sideConfigModes[index - 5];
                    default -> 2;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 4 ->
                            AirCollectorBlockEntity.this.redstoneMode = RedstoneModes.values()[value % RedstoneModes.values().length];
                    case 5, 6, 7, 8, 9, 10 -> {
                        AirCollectorBlockEntity.this.sideConfigModes[index - 5] = value;
                        AirCollectorBlockEntity.this.setChanged();

                        if (AirCollectorBlockEntity.this.level != null && !AirCollectorBlockEntity.this.level.isClientSide) {
                            BlockState state = AirCollectorBlockEntity.this.getBlockState();
                            AirCollectorBlockEntity.this.level.sendBlockUpdated(AirCollectorBlockEntity.this.worldPosition, state, state, 3);
                            AirCollectorBlockEntity.this.level.updateNeighborsAt(AirCollectorBlockEntity.this.worldPosition, state.getBlock());
                        }
                    }
                }
            }

            @Override
            public int getCount() {
                return 11;
            }

        };
    }

    @Override
    public int getBaseProcessTime() {


        return 1;
    }

    private final DynamicEnergyStorage energyStorage = new DynamicEnergyStorage(maxEnergyStorage, 1000, 1000) {
        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {

            int received = super.receiveEnergy(maxReceive, simulate);
            if (received > 0 && !simulate) {
                setChanged();
            }
            return received;
        }
    };


    private LazyOptional<IItemHandler> itemHandlerLazyOptional = LazyOptional.of(() -> itemHandler);
    private LazyOptional<IEnergyStorage> energyStorageLazyOptional = LazyOptional.of(this::createEnergyStorageWrapper);



    public LazyOptional<IFluidHandler> getTankCapability() {
        return handler;
    }
    public LazyOptional<IFluidHandler> getTankCapability(int tankIndex) {
        return this.handler;
    }

    @Override
    public boolean isItemInUpgradeHandlerValid(ItemStack stack) {
        return super.isItemInUpgradeHandlerValid(stack) && !stack.is(AEItems.UPGRADE_SPEED.get());
    }


    private IEnergyStorage createEnergyStorageWrapper() {
        return new IEnergyStorage() {
            @Override
            public int receiveEnergy(int maxReceive, boolean simulate) {
                return energyStorage.receiveEnergy(maxReceive, simulate);
            }

            @Override
            public int extractEnergy(int maxExtract, boolean simulate) {
                return 0;
            }

            @Override
            public int getEnergyStored() {
                return energyStorage.getEnergyStored();
            }

            @Override
            public int getMaxEnergyStored() {
                return energyStorage.getMaxEnergyStored();
            }

            @Override
            public boolean canExtract() {
                return false;
            }

            @Override
            public boolean canReceive() {
                return true;
            }
        };
    }

    @Override
    public void refreshCaps() {

        this.itemHandlerLazyOptional.invalidate();
        this.energyStorageLazyOptional.invalidate();
        this.handler.invalidate();


        this.itemHandlerLazyOptional = LazyOptional.of(() -> itemHandler);
        this.energyStorageLazyOptional = LazyOptional.of(this::createEnergyStorageWrapper);
        this.handler = LazyOptional.of(() -> tank);
    }

    public IItemHandler getItemHandler() {
        return itemHandler;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.assembly_expansion.air_collector");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInv, Player player) {
        return new AirCollectorMenu(containerId, playerInv, this, this.data);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, AirCollectorBlockEntity entity) {
        if (level.isClientSide()) return;


        boolean isActive = false;


        if (entity.hasValidBatteryInSlot) {
            entity.chargeFromEnergySlot();
        }


        entity.energyStorage.setCapacity(EnergyUpgradeItem.getMaxEnergy(entity));


        entity.handleFluidTransfer();


        boolean canWork = entity.redstoneMode.canWork(level.hasNeighborSignal(pos));
        if (canWork) {


            if (entity.energyStorage.getEnergyStored() >= 30) {

                FluidStack stack = new FluidStack(AEFluids.AIR.getSource(), 5);


                if (entity.canOutput(stack)) {

                    isActive = true;


                    entity.energyStorage.extractEnergy(30, false);


                    entity.tank.fill(stack, IFluidHandler.FluidAction.EXECUTE);


                    entity.setChanged();
                }
            }
        }

        if (state.getValue(AirCollectorBlock.ACTIVE) != isActive) {


            level.setBlock(pos, state.setValue(AirCollectorBlock.ACTIVE, isActive), 3);
        }

    }


    private void handleFluidTransfer() {
        ItemStack inputBucket = this.itemHandler.getStackInSlot(0);
        if (inputBucket.isEmpty() || this.tank.isEmpty()) {
            return;
        }


        ItemStack singleContainer = inputBucket.copyWithCount(1);

        singleContainer.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).ifPresent(fluidHandler -> {

            int fillableAmount = fluidHandler.fill(this.tank.getFluid(), IFluidHandler.FluidAction.SIMULATE);


            if (fillableAmount > 0 && this.tank.getFluidAmount() >= fillableAmount) {


                FluidStack drained = this.tank.drain(fillableAmount, IFluidHandler.FluidAction.SIMULATE);
                fluidHandler.fill(drained, IFluidHandler.FluidAction.EXECUTE);


                ItemStack resultContainer = fluidHandler.getContainer();


                ItemStack outputSlotStack = this.itemHandler.getStackInSlot(1);

                boolean canInsert = outputSlotStack.isEmpty() ||
                        (ItemStack.isSameItemSameTags(outputSlotStack, resultContainer) &&
                                outputSlotStack.getCount() + resultContainer.getCount() <= outputSlotStack.getMaxStackSize());


                if (canInsert) {

                    this.tank.drain(fillableAmount, IFluidHandler.FluidAction.EXECUTE);


                    this.itemHandler.extractItem(0, 1, false);


                    this.itemHandler.insertItem(1, resultContainer, false);

                    this.setChanged();
                }
            }
        });
    }

    private boolean canOutput(FluidStack output) {
        if (output.isEmpty()) return true;
        FluidTank targetTank = tank;
        if (!targetTank.getFluid().isEmpty() && !targetTank.getFluid().isFluidEqual(output)) return false;
        if (targetTank.getFluidAmount() + output.getAmount() > targetTank.getCapacity()) return false;

        return true;
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        handler.invalidate();
        itemHandlerLazyOptional.invalidate();
        energyStorageLazyOptional.invalidate();
    }

    private void chargeFromEnergySlot() {
        int spaceLeft = this.energyStorage.getMaxEnergyStored() - this.energyStorage.getEnergyStored();


        if (spaceLeft <= 0) return;

        ItemStack batteryStack = this.itemHandler.getStackInSlot(2);


        batteryStack.getCapability(ForgeCapabilities.ENERGY).ifPresent(batteryEnergy -> {
            int maxTransfer = Math.min(spaceLeft, 1000);
            int extracted = batteryEnergy.extractEnergy(maxTransfer, true);

            if (extracted > 0) {
                int accepted = this.energyStorage.receiveEnergy(extracted, false);
                batteryEnergy.extractEnergy(accepted, false);
                this.setChanged();
            } else {


                this.hasValidBatteryInSlot = false;
            }
        });
    }

    @Override
    public int getMaxEnergy() {
        return maxEnergyStorage;
    }


    @Override
    public void saveAdditional(CompoundTag tag) {
        tag.put("Tank", tank.writeToNBT(new CompoundTag()));
        tag.put("energy", energyStorage.serializeNBT());
        tag.put("inventory", itemHandler.serializeNBT());
        super.saveAdditional(tag);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        tank.readFromNBT(tag.getCompound("Tank"));
        this.energyStorage.setCapacity(EnergyUpgradeItem.getMaxEnergy(this));
        this.energyStorage.setEnergyDirect(tag.getInt("energy"));
        itemHandler.deserializeNBT(tag.getCompound("inventory"));
        ItemStack stack = this.itemHandler.getStackInSlot(2);
        this.hasValidBatteryInSlot = !stack.isEmpty() && stack.getCapability(ForgeCapabilities.ENERGY)
                .map(IEnergyStorage::canExtract)
                .orElse(false);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        Direction blockFacing = this.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
        Direction relativeSide = GuiConfigHelper.getRelativeSide(blockFacing, side);

        if (cap == ForgeCapabilities.ENERGY) {
            if (side != null) {
                int sideId = relativeSide.get3DDataValue();
                int sideMode = this.sideConfigModes[sideId];
                if (sideMode == 0 || sideMode == 2) return LazyOptional.empty();
            }
            return energyStorageLazyOptional.cast();
        }

        if (cap == ForgeCapabilities.FLUID_HANDLER) {
            if (side != null) {
                int sideId = relativeSide.get3DDataValue();
                int sideMode = this.sideConfigModes[sideId];

                if (sideMode == 0 || sideMode == 1) return LazyOptional.empty();
            }
            return handler.cast();
        }

        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            if (side != null) {
                int sideId = relativeSide.get3DDataValue();
                int sideMode = this.sideConfigModes[sideId];
                if (sideMode == 0) return LazyOptional.empty();
            }
            return itemHandlerLazyOptional.cast();
        }

        return super.getCapability(cap, side);
    }


    @Override
    public net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket getUpdatePacket() {

        return net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag() {

        CompoundTag tag = new CompoundTag();
        saveAdditional(tag);
        return tag;
    }

    @Override
    public void onDataPacket(net.minecraft.network.Connection net, net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket pkt) {

        CompoundTag tag = pkt.getTag();
        if (tag != null) {
            load(tag);
        }
    }
}