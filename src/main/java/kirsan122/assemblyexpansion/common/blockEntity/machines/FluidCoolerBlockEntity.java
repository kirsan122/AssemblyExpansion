package kirsan122.assemblyexpansion.common.blockEntity.machines;

import kirsan122.assemblyexpansion.api.GuiConfigHelper;
import kirsan122.assemblyexpansion.api.RedstoneModes;
import kirsan122.assemblyexpansion.common.energy.DynamicEnergyStorage;
import kirsan122.assemblyexpansion.common.inventory.FluidCoolerMenu;
import kirsan122.assemblyexpansion.common.item.upgrades.EnergyUpgradeItem;
import kirsan122.assemblyexpansion.common.item.upgrades.SpeedUpgradeItem;
import kirsan122.assemblyexpansion.common.recipe.FluidCoolerRecipe;
import kirsan122.assemblyexpansion.common.init.AEBlockEntities;
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
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FluidCoolerBlockEntity extends ConfigurableBlockEntity implements MenuProvider {
    private final int maxEnergyStorage = 10000;
    private boolean hasValidBatteryInSlot = false;
    protected final ContainerData data;
    private int progress = 0;
    private int maxProgress = 100;
    public boolean inventoryChanged = true;
    private FluidCoolerRecipe cachedRecipe = null;
    private Fluid lastCheckedFluid = Fluids.EMPTY;
    private int lastCheckedAmount = 0;


    private final FluidTank[] tanks = new FluidTank[2];
    private final LazyOptional<IFluidHandler>[] handlers = new LazyOptional[2];

    private final ItemStackHandler itemHandler = new ItemStackHandler(5) {
        @Override
        protected void onContentsChanged(int slot) {
            onInventorySlotChanged(slot);
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {

            if (slot == 4) return stack.getCapability(ForgeCapabilities.ENERGY).isPresent();

            return stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).isPresent();
        }
    };

    private void onInventorySlotChanged(int slot) {
        this.inventoryChanged = true;

        setChanged();

        if (slot == 4) {
            ItemStack stack = this.itemHandler.getStackInSlot(4);
            if (!stack.isEmpty()) {
                this.hasValidBatteryInSlot = stack.getCapability(ForgeCapabilities.ENERGY)
                        .map(net.minecraftforge.energy.IEnergyStorage::canExtract)
                        .orElse(false);
            } else {
                this.hasValidBatteryInSlot = false;
            }
        }
    }

    public FluidCoolerBlockEntity(BlockPos pos, BlockState state) {
        super(AEBlockEntities.FLUID_COOLER_BE.get(), pos, state);
        this.data = new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> FluidCoolerBlockEntity.this.progress;
                    case 1 -> FluidCoolerBlockEntity.this.maxProgress;
                    case 2 -> FluidCoolerBlockEntity.this.energyStorage.getEnergyStored();
                    case 3 -> FluidCoolerBlockEntity.this.energyStorage.getMaxEnergyStored();
                    case 4 -> FluidCoolerBlockEntity.this.redstoneMode.ordinal();
                    case 5, 6, 7, 8, 9, 10 -> FluidCoolerBlockEntity.this.sideConfigModes[index - 5];
                    default -> 2;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 4 ->
                            FluidCoolerBlockEntity.this.redstoneMode = RedstoneModes.values()[value % RedstoneModes.values().length];
                    case 5, 6, 7, 8, 9, 10 -> {
                        FluidCoolerBlockEntity.this.sideConfigModes[index - 5] = value;
                        FluidCoolerBlockEntity.this.setChanged();

                        if (FluidCoolerBlockEntity.this.level != null && !FluidCoolerBlockEntity.this.level.isClientSide) {
                            BlockState state = FluidCoolerBlockEntity.this.getBlockState();
                            FluidCoolerBlockEntity.this.level.sendBlockUpdated(FluidCoolerBlockEntity.this.worldPosition, state, state, 3);
                            FluidCoolerBlockEntity.this.level.updateNeighborsAt(FluidCoolerBlockEntity.this.worldPosition, state.getBlock());
                        }
                    }
                }
            }

            @Override
            public int getCount() {
                return 11;
            }

        };
        for (int i = 0; i < 2; i++) {
            this.tanks[i] = new FluidTank(10000) {
                @Override protected void onContentsChanged() {
                    setChanged();
                    if (level != null && !level.isClientSide) {
                        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
                    }
                }
            };
        }
        this.handlers[0] = LazyOptional.of(() -> this.tanks[0]);
        this.handlers[1] = LazyOptional.of(() -> this.tanks[1]);
    }

    @Override
    public int getBaseProcessTime() {
        return this.maxProgress;
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
        return Component.translatable("block.assembly_expansion.fluid_cooler");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInv, Player player) {
        return new FluidCoolerMenu(containerId, playerInv, this, this.data);
    }


    public static void tick(Level level, BlockPos pos, BlockState state, FluidCoolerBlockEntity entity) {
        if (level.isClientSide) return;

        entity.handleFluidTransfer();

        if (entity.hasValidBatteryInSlot) {
            entity.chargeFromEnergySlot();
        }


        var inputTank = entity.tanks[0];
        if (inputTank.isEmpty()) {
            if (entity.progress > 0) {
                entity.progress = 0;
                entity.cachedRecipe = null;
                setChanged(level, pos, state);
            }
            return;
        }

        entity.energyStorage.setCapacity(EnergyUpgradeItem.getMaxEnergy(entity));

        boolean canWork = entity.redstoneMode.canWork(level.hasNeighborSignal(pos));
        if (canWork) {
            FluidStack currentFluid = inputTank.getFluid();


            if (entity.inventoryChanged
                    || currentFluid.getFluid() != entity.lastCheckedFluid
                    || currentFluid.getAmount() < entity.lastCheckedAmount) {
                entity.lastCheckedFluid = currentFluid.getFluid();
                entity.lastCheckedAmount = currentFluid.getAmount();
                entity.inventoryChanged = false;

                entity.cachedRecipe = level.getRecipeManager()
                        .getAllRecipesFor(AERecipes.FLUID_COOLER_TYPE.get())
                        .stream()
                        .filter(r -> r.getInputFluid().isFluidEqual(currentFluid))
                        .findFirst()
                        .orElse(null);
            }

        } else {


        entity.lastCheckedAmount = 0;
    }


    FluidCoolerRecipe recipe = (entity.cachedRecipe != null
            && inputTank.getFluidAmount() >= entity.cachedRecipe.getInputFluid().getAmount())
            ? entity.cachedRecipe : null;
        int currentEnergyConsumption = SpeedUpgradeItem.getEnergyUsage(35, entity.getUpgradeSlots());



        if (recipe != null && entity.canOutput(recipe.getOutputFluid()) && entity.energyStorage.getEnergyStored() >= currentEnergyConsumption) {
            entity.energyStorage.extractEnergy(currentEnergyConsumption, false);
            entity.progress++;

            int currentMaxProgress = SpeedUpgradeItem.getCookTime(entity);



            if (entity.progress >= currentMaxProgress) {
                inputTank.drain(recipe.getInputFluid().getAmount(), IFluidHandler.FluidAction.EXECUTE);

                if (!recipe.getOutputFluid().isEmpty()) {
                    entity.tanks[1].fill(recipe.getOutputFluid().copy(), IFluidHandler.FluidAction.EXECUTE);
                }

                entity.progress = 0;

            }
            setChanged(level, pos, state);
        } else {

            if (entity.progress > 0) {
                entity.progress--;
                setChanged(level, pos, state);
            }
        }
    }


    private void handleFluidTransfer() {



        ItemStack inputEmptySource = itemHandler.getStackInSlot(0);

        if (!inputEmptySource.isEmpty()) {

            FluidUtil.getFluidHandler(inputEmptySource).ifPresent(fluidHandlerItem -> {

                FluidStack fluidInItem = fluidHandlerItem.getFluidInTank(0);

                if (!fluidInItem.isEmpty()) {

                    int acceptedAmount = this.tanks[0].fill(fluidInItem, IFluidHandler.FluidAction.SIMULATE);


                    if (acceptedAmount > 0) {

                        ItemStack containerCopy = inputEmptySource.copy();
                        containerCopy.setCount(1);



                        var result = FluidUtil.tryEmptyContainer(containerCopy, this.tanks[0], acceptedAmount, null, true);

                        if (result.isSuccess()) {
                            ItemStack emptyContainer = result.getResult();


                            if (itemHandler.insertItem(1, emptyContainer, true).isEmpty()) {

                                itemHandler.insertItem(1, emptyContainer, false);
                                itemHandler.extractItem(0, 1, false);
                                setChanged();
                            }
                        }
                    }
                }
            });
        }



        ItemStack inputFillSource = itemHandler.getStackInSlot(2);

        if (!inputFillSource.isEmpty() && !this.tanks[1].isEmpty()) {

            ItemStack containerCopy = inputFillSource.copy();
            containerCopy.setCount(1);



            var result = FluidUtil.tryFillContainer(containerCopy, this.tanks[1], Integer.MAX_VALUE, null, true);

            if (result.isSuccess()) {
                ItemStack filledContainer = result.getResult();


                if (itemHandler.insertItem(3, filledContainer, true).isEmpty()) {

                    itemHandler.insertItem(3, filledContainer, false);
                    itemHandler.extractItem(2, 1, false);
                    setChanged();
                }
            }
        }
    }

    private boolean canOutput(FluidStack output) {

        if (output.isEmpty()) return true;
        FluidTank targetTank = tanks[1];
        if (!targetTank.getFluid().isEmpty() && !targetTank.getFluid().isFluidEqual(output)) return false;
        if (targetTank.getFluidAmount() + output.getAmount() > targetTank.getCapacity()) return false;

        return true;
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        for (int i = 0; i < 2; i++) handlers[i].invalidate();
        itemHandlerLazyOptional.invalidate();
        energyStorageLazyOptional.invalidate();
    }

    private void chargeFromEnergySlot() {
        int spaceLeft = this.energyStorage.getMaxEnergyStored() - this.energyStorage.getEnergyStored();


        if (spaceLeft <= 0) return;

        ItemStack batteryStack = this.itemHandler.getStackInSlot(4);


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
        tag.put("outputTank", tanks[1].writeToNBT(new CompoundTag()));
        tag.put("energy", energyStorage.serializeNBT());
        tag.put("inventory", itemHandler.serializeNBT());
        tag.putInt("progress", this.progress);
        super.saveAdditional(tag);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        tanks[0].readFromNBT(tag.getCompound("inputTank"));
        tanks[1].readFromNBT(tag.getCompound("outputTank"));
        this.progress = tag.getInt("progress");

        this.energyStorage.setCapacity(EnergyUpgradeItem.getMaxEnergy(this));
        this.energyStorage.setEnergyDirect(tag.getInt("energy"));
        itemHandler.deserializeNBT(tag.getCompound("inventory"));
        ItemStack stack = this.itemHandler.getStackInSlot(4);
        this.hasValidBatteryInSlot = !stack.isEmpty() && stack.getCapability(ForgeCapabilities.ENERGY)
                .map(IEnergyStorage::canExtract)
                .orElse(false);
        this.inventoryChanged = true;
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

                if (sideMode == 0) return LazyOptional.empty();
                switch (sideMode) {
                    case 0 -> {
                        return LazyOptional.empty();
                    }
                    case 1 -> {
                        return handlers[0].cast();
                    }
                    case 2 -> {
                        return handlers[1].cast();
                    }
                }
            }
            return handlers[1].cast();
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

    @Override
    public void refreshCaps() {
        this.itemHandlerLazyOptional.invalidate();
        this.energyStorageLazyOptional.invalidate();
        this.handlers[0].invalidate();
        this.handlers[1].invalidate();

        this.itemHandlerLazyOptional = LazyOptional.of(() -> itemHandler);
        this.energyStorageLazyOptional = LazyOptional.of(this::createEnergyStorageWrapper);
        this.handlers[0] = LazyOptional.of(() -> tanks[0]);
        this.handlers[1] = LazyOptional.of(() -> tanks[1]);
    }

}
