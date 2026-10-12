package kirsan122.assemblyexpansion.common.blockEntity.machines;

import kirsan122.assemblyexpansion.api.GuiConfigHelper;
import kirsan122.assemblyexpansion.api.RedstoneModes;
import kirsan122.assemblyexpansion.common.energy.DynamicEnergyStorage;
import kirsan122.assemblyexpansion.common.inventory.ElectrolyzerMenu;
import kirsan122.assemblyexpansion.common.item.upgrades.EnergyUpgradeItem;
import kirsan122.assemblyexpansion.common.recipe.ElectrolyzerRecipe;
import kirsan122.assemblyexpansion.common.init.AEBlockEntities;
import kirsan122.assemblyexpansion.common.init.AEItems;
import kirsan122.assemblyexpansion.common.init.AERecipes;
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

public class ElectrolyzerBlockEntity extends UpgradableBlockEntity implements MenuProvider {
    private final int maxEnergyStorage = 12000;
    private boolean hasValidBatteryInSlot = false;
    protected final ContainerData data;
    private ElectrolyzerRecipe cachedRecipe = null;
    private FluidStack lastCheckedFluid = FluidStack.EMPTY;
    public boolean inventoryChanged = true;


    private final FluidTank[] tanks = new FluidTank[3];
    private final LazyOptional<IFluidHandler>[] handlers = new LazyOptional[3];

    private final ItemStackHandler itemHandler = new ItemStackHandler(4) {
        @Override
        protected void onContentsChanged(int slot) {
            onInventorySlotChanged(slot);
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {

            if (slot == 3) return stack.getCapability(ForgeCapabilities.ENERGY).isPresent();
            else return stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).isPresent();

        }
    };

    @Override
    public boolean isItemInUpgradeHandlerValid(ItemStack stack) {
        return super.isItemInUpgradeHandlerValid(stack) && !stack.is(AEItems.UPGRADE_SPEED.get());
    }

    private void onInventorySlotChanged(int slot) {
        this.inventoryChanged = true;

        setChanged();

        if (slot == 3) {
            ItemStack stack = this.itemHandler.getStackInSlot(3);
            if (!stack.isEmpty()) {
                this.hasValidBatteryInSlot = stack.getCapability(net.minecraftforge.common.capabilities.ForgeCapabilities.ENERGY)
                        .map(net.minecraftforge.energy.IEnergyStorage::canExtract)
                        .orElse(false);
            } else {
                this.hasValidBatteryInSlot = false;
            }
        }
    }

    public ElectrolyzerBlockEntity(BlockPos pos, BlockState state) {
        super(AEBlockEntities.ELECTROLYZER_BE.get(), pos, state);
        this.data = new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 2 -> ElectrolyzerBlockEntity.this.energyStorage.getEnergyStored();
                    case 3 -> ElectrolyzerBlockEntity.this.energyStorage.getMaxEnergyStored();
                    case 4 -> ElectrolyzerBlockEntity.this.redstoneMode.ordinal();
                    default -> 2;
                };
            }

            @Override
            public void set(int index, int value) {
                if (index == 4) {
                    ElectrolyzerBlockEntity.this.redstoneMode = RedstoneModes.values()[value % RedstoneModes.values().length];
                }
            }

            @Override
            public int getCount() {
                return 5;
            }

        };
        for (int i = 0; i < 3; i++) {
            final int index = i;
            tanks[i] = new FluidTank(8000) {
                @Override
                protected void onContentsChanged() {
                    setChanged();
                    if (level != null && !level.isClientSide) {
                        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
                    }
                }
            };
            handlers[i] = LazyOptional.of(() -> tanks[index]);
        }
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


    public LazyOptional<IFluidHandler> getTankCapability(int tankIndex) {
        return handlers[tankIndex];
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

    public IItemHandler getItemHandler() {
        return itemHandler;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.assembly_expansion.electrolyzer");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInv, Player player) {
        return new ElectrolyzerMenu(containerId, playerInv, this, this.data);
    }


    public static void tick(Level level, BlockPos pos, BlockState state, ElectrolyzerBlockEntity entity) {
        if (level.isClientSide) return;
        entity.handleFluidTransfer();
        if (entity.hasValidBatteryInSlot) {
            entity.chargeFromEnergySlot();
        }
        if (entity.tanks[0].isEmpty()) {
            return;
        }
        entity.energyStorage.setCapacity(EnergyUpgradeItem.getMaxEnergy(entity));
        boolean canWork = entity.redstoneMode.canWork(level.hasNeighborSignal(pos));

        if (canWork) {

            ElectrolyzerRecipe recipe = entity.getValidatedRecipe(level);

            if (recipe != null && entity.canOutput(recipe.getOutputs()) && entity.energyStorage.getEnergyStored() >= 25) {
                entity.energyStorage.extractEnergy(25, false);



                entity.tanks[0].drain(recipe.getInputFluid().getAmount(), IFluidHandler.FluidAction.EXECUTE);
                for (int i = 0; i < 2; i++) {
                    if (!recipe.getOutputs()[i].isEmpty()) {
                        entity.tanks[i + 1].fill(recipe.getOutputs()[i].copy(), IFluidHandler.FluidAction.EXECUTE);
                    }
                }

                setChanged(level, pos, state);
            }
        }

    }

    private void handleFluidTransfer() {

        ItemStack inputBucket = this.itemHandler.getStackInSlot(0);

        if (!inputBucket.isEmpty() && inputBucket.getCount() == 1) {
            inputBucket.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).ifPresent(fluidHandler -> {

                FluidStack fluidInBucket = fluidHandler.drain(1000, IFluidHandler.FluidAction.SIMULATE);

                if (!fluidInBucket.isEmpty()) {

                    int acceptedAmount = this.tanks[0].fill(fluidInBucket, IFluidHandler.FluidAction.SIMULATE);

                    if (acceptedAmount > 0) {

                        FluidStack realDrained = fluidHandler.drain(acceptedAmount, IFluidHandler.FluidAction.EXECUTE);
                        this.tanks[0].fill(realDrained, IFluidHandler.FluidAction.EXECUTE);


                        this.itemHandler.setStackInSlot(0, fluidHandler.getContainer());
                        this.setChanged();
                    }
                }
            });
        }


        ItemStack outputBucket1 = this.itemHandler.getStackInSlot(1);
        if (!outputBucket1.isEmpty() && outputBucket1.getCount() == 1 && !this.tanks[1].isEmpty()) {
            outputBucket1.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).ifPresent(fluidHandler -> {
                int fillableAmount = fluidHandler.fill(this.tanks[1].getFluid(), IFluidHandler.FluidAction.SIMULATE);

                if (fillableAmount > 0 && this.tanks[1].getFluidAmount() >= fillableAmount) {
                    FluidStack drained = this.tanks[1].drain(fillableAmount, IFluidHandler.FluidAction.EXECUTE);
                    fluidHandler.fill(drained, IFluidHandler.FluidAction.EXECUTE);

                    this.itemHandler.setStackInSlot(1, fluidHandler.getContainer());
                    this.setChanged();
                }
            });
        }


        ItemStack outputBucket2 = this.itemHandler.getStackInSlot(2);
        if (!outputBucket2.isEmpty() && outputBucket2.getCount() == 1 && !this.tanks[2].isEmpty()) {
            outputBucket2.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).ifPresent(fluidHandler -> {
                int fillableAmount = fluidHandler.fill(this.tanks[2].getFluid(), IFluidHandler.FluidAction.SIMULATE);

                if (fillableAmount > 0 && this.tanks[2].getFluidAmount() >= fillableAmount) {
                    FluidStack drained = this.tanks[2].drain(fillableAmount, IFluidHandler.FluidAction.EXECUTE);
                    fluidHandler.fill(drained, IFluidHandler.FluidAction.EXECUTE);

                    this.itemHandler.setStackInSlot(2, fluidHandler.getContainer());
                    this.setChanged();
                }
            });
        }
    }


    private ElectrolyzerRecipe getValidatedRecipe(Level level) {
        FluidStack currentFluid = tanks[0].getFluid();


        if (this.inventoryChanged || !currentFluid.isFluidEqual(this.lastCheckedFluid)) {
            this.lastCheckedFluid = currentFluid.copy();
            this.inventoryChanged = false;

            this.cachedRecipe = level.getRecipeManager()
                    .getAllRecipesFor(AERecipes.ELECTROLYZER_TYPE.get())
                    .stream()
                    .filter(r -> r.getInputFluid().isFluidEqual(currentFluid))
                    .findFirst()
                    .orElse(null);
        }


        if (this.cachedRecipe != null && currentFluid.getAmount() >= this.cachedRecipe.getInputFluid().getAmount()) {
            return this.cachedRecipe;
        }

        return null;
    }



    private boolean canOutput(FluidStack[] outputs) {
        for (int i = 0; i < 2; i++) {
            if (outputs[i].isEmpty()) continue;
            FluidTank targetTank = tanks[i + 1];
            if (!targetTank.getFluid().isEmpty() && !targetTank.getFluid().isFluidEqual(outputs[i])) return false;
            if (targetTank.getFluidAmount() + outputs[i].getAmount() > targetTank.getCapacity()) return false;
        }
        return true;
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        for (int i = 0; i < 3; i++) handlers[i].invalidate();
        itemHandlerLazyOptional.invalidate();
        energyStorageLazyOptional.invalidate();
    }

    private void chargeFromEnergySlot() {
        int spaceLeft = this.energyStorage.getMaxEnergyStored() - this.energyStorage.getEnergyStored();

        if (spaceLeft <= 0) return;

        ItemStack batteryStack = this.itemHandler.getStackInSlot(3);

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
        tag.put("inputTank", tanks[0].writeToNBT(new CompoundTag()));
        for (int i = 1; i < 3; i++) {
            tag.put("Tank" + i, tanks[i].writeToNBT(new CompoundTag()));
        }
        tag.put("energy", energyStorage.serializeNBT());
        tag.put("inventory", itemHandler.serializeNBT());
        super.saveAdditional(tag);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        tanks[0].readFromNBT(tag.getCompound("inputTank"));
        for (int i = 1; i < 3; i++) {
            tanks[i].readFromNBT(tag.getCompound("Tank" + i));
        }
        this.energyStorage.setCapacity(EnergyUpgradeItem.getMaxEnergy(this));
        this.energyStorage.setEnergyDirect(tag.getInt("energy"));
        itemHandler.deserializeNBT(tag.getCompound("inventory"));
        ItemStack stack = this.itemHandler.getStackInSlot(3);
        this.hasValidBatteryInSlot = !stack.isEmpty() && stack.getCapability(ForgeCapabilities.ENERGY)
                .map(IEnergyStorage::canExtract)
                .orElse(false);
        this.inventoryChanged = true;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ENERGY) {
            return energyStorageLazyOptional.cast();
        }

        if (cap == ForgeCapabilities.FLUID_HANDLER) {
            if (side == null) {
                return handlers[0].cast();
            }


            Direction blockFacing = this.getBlockState().getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING);



            Direction relativeSide = GuiConfigHelper.getRelativeSide(blockFacing, side);


            switch (relativeSide) {
                case SOUTH -> {

                    return handlers[0].cast();
                }
                case WEST -> {

                    return handlers[1].cast();
                }
                case EAST -> {

                    return handlers[2].cast();
                }
                default -> {

                    return LazyOptional.empty();
                }
            }
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