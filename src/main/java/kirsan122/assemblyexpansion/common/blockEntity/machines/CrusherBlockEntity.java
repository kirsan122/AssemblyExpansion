package kirsan122.assemblyexpansion.common.blockEntity.machines;

import kirsan122.assemblyexpansion.api.GuiConfigHelper;
import kirsan122.assemblyexpansion.api.RedstoneModes;
import kirsan122.assemblyexpansion.common.block.machines.CrusherBlock;
import kirsan122.assemblyexpansion.common.energy.DynamicEnergyStorage;
import kirsan122.assemblyexpansion.common.item.upgrades.EnergyUpgradeItem;
import kirsan122.assemblyexpansion.common.item.upgrades.SpeedUpgradeItem;
import kirsan122.assemblyexpansion.common.inventory.CrusherMenu;
import kirsan122.assemblyexpansion.common.recipe.CrusherRecipe;
import kirsan122.assemblyexpansion.common.init.AEBlockEntities;
import kirsan122.assemblyexpansion.common.init.AEMachineBlocks;
import kirsan122.assemblyexpansion.common.init.AERecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
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
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RangedWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class CrusherBlockEntity extends ConfigurableBlockEntity implements MenuProvider {
    private final int maxEnergyStorage = 10000;
    private boolean hasValidBatteryInSlot = false;
    protected final ContainerData data;
    private int progress = 0;
    private int maxProgress = 180;

    private final SimpleContainer inventoryWrapper = new SimpleContainer(4);

    private CrusherRecipe cachedRecipe = null;

    public boolean inventoryChanged = true;


    public CrusherBlockEntity(BlockPos pos, BlockState state) {
        super(AEBlockEntities.CRUSHER_BE.get(), pos, state);

        this.data = new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> CrusherBlockEntity.this.progress;
                    case 1 -> SpeedUpgradeItem.getCookTime(CrusherBlockEntity.this);
                    case 2 -> CrusherBlockEntity.this.energyStorage.getEnergyStored();
                    case 3 -> CrusherBlockEntity.this.energyStorage.getMaxEnergyStored();
                    case 4 -> CrusherBlockEntity.this.redstoneMode.ordinal();
                    case 5, 6, 7, 8, 9, 10 -> CrusherBlockEntity.this.sideConfigModes[index - 5];
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> CrusherBlockEntity.this.progress = value;
                    case 1 -> CrusherBlockEntity.this.maxProgress = value;
                    case 4 ->
                            CrusherBlockEntity.this.redstoneMode = RedstoneModes.values()[value % RedstoneModes.values().length];
                    case 5, 6, 7, 8, 9, 10 -> {
                        CrusherBlockEntity.this.sideConfigModes[index - 5] = value;
                        CrusherBlockEntity.this.setChanged();

                        if (CrusherBlockEntity.this.level != null && !CrusherBlockEntity.this.level.isClientSide) {
                            BlockState state = CrusherBlockEntity.this.getBlockState();
                            CrusherBlockEntity.this.level.sendBlockUpdated(CrusherBlockEntity.this.worldPosition, state, state, 3);
                            CrusherBlockEntity.this.level.updateNeighborsAt(CrusherBlockEntity.this.worldPosition, state.getBlock());
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


    private final ItemStackHandler itemHandler = new ItemStackHandler(4) {
        @Override
        protected void onContentsChanged(int slot) {
            onInventorySlotChanged(slot);
        }
        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {

            if (slot == 1) return stack.getCapability(ForgeCapabilities.ENERGY).isPresent();

            return super.isItemValid(slot, stack);
        }
    };

    @Override
    public int getBaseProcessTime() {


        return maxProgress;
    }

    private void onInventorySlotChanged(int slot) {

        this.inventoryChanged = true;


        setChanged();


        if (slot == 1) {
            ItemStack stack = this.itemHandler.getStackInSlot(1);
            if (!stack.isEmpty()) {
                this.hasValidBatteryInSlot = stack.getCapability(net.minecraftforge.common.capabilities.ForgeCapabilities.ENERGY)
                        .map(net.minecraftforge.energy.IEnergyStorage::canExtract)
                        .orElse(false);
            } else {
                this.hasValidBatteryInSlot = false;
            }
        }
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


        this.itemHandlerLazyOptional = LazyOptional.of(() -> itemHandler);
        this.energyStorageLazyOptional = LazyOptional.of(this::createEnergyStorageWrapper);
    }


    public IItemHandler getItemHandler() {
        return itemHandler;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.assembly_expansion.crusher");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInv, Player player) {
        return new CrusherMenu(containerId, playerInv, this, this.data);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, CrusherBlockEntity entity) {
        if (level.isClientSide()) return;
        if (!state.is(AEMachineBlocks.CRUSHER.get())) return;

        if (entity.hasValidBatteryInSlot) {
            entity.chargeFromEnergySlot();
        }


        entity.energyStorage.setCapacity(EnergyUpgradeItem.getMaxEnergy(entity));

        boolean canWork = entity.redstoneMode.canWork(level.hasNeighborSignal(pos));
        boolean isCrafting = false;


        if (canWork) {
            Optional<CrusherRecipe> recipe = entity.getCurrentRecipe();
            boolean canCraft = recipe.isPresent() && entity.canInsertIntoOutputSlot(recipe.get());

            if (canCraft) {
                CrusherRecipe currentRecipe = recipe.get();

                int currentMaxProgress = SpeedUpgradeItem.getCookTime(entity);
                int currentEnergyConsumption = SpeedUpgradeItem.getEnergyUsage(currentRecipe.getEnergyConsumption(), entity.getUpgradeSlots());

                boolean hasEnoughEnergy = entity.energyStorage.getEnergyStored() >= currentEnergyConsumption;

                if (hasEnoughEnergy) {
                    isCrafting = true;
                    entity.energyStorage.consumeEnergyDirect(currentEnergyConsumption);

                    entity.progress++;
                    setChanged(level, pos, state);

                    if (entity.progress >= currentMaxProgress) {
                        entity.craftItem(currentRecipe);
                        entity.progress = 0;


                        Optional<CrusherRecipe> nextRecipe = entity.getCurrentRecipe();
                        boolean canContinue = nextRecipe.isPresent() && entity.canInsertIntoOutputSlot(nextRecipe.get());

                        isCrafting = canContinue && entity.energyStorage.getEnergyStored() >= SpeedUpgradeItem.getEnergyUsage(nextRecipe.get().getEnergyConsumption(), entity.getUpgradeSlots());
                    }
                }
            } else {
                entity.progress = 0;
            }
        }


        if (state.getValue(CrusherBlock.ACTIVE) != isCrafting) {
            level.setBlock(pos, state.setValue(CrusherBlock.ACTIVE, isCrafting), 3);
        }
    }


    private Optional<CrusherRecipe> getCurrentRecipe() {
        assert this.level != null;


        if (!this.inventoryChanged && this.cachedRecipe != null) {
            return Optional.of(this.cachedRecipe);
        }


        this.inventoryWrapper.setItem(0, this.itemHandler.getStackInSlot(0));
        this.inventoryWrapper.setItem(1, this.itemHandler.getStackInSlot(1));
        this.inventoryWrapper.setItem(2, this.itemHandler.getStackInSlot(2));
        this.inventoryWrapper.setItem(3, this.itemHandler.getStackInSlot(3));


        Optional<CrusherRecipe> recipeMatch = this.level.getRecipeManager().getRecipeFor(AERecipes.CRUSHER_RECIPE_TYPE.get(), this.inventoryWrapper, this.level);


        this.cachedRecipe = recipeMatch.orElse(null);
        this.inventoryChanged = false;

        return recipeMatch;
    }

    private boolean canInsertIntoOutputSlot(CrusherRecipe recipe) {

        ItemStack mainOut = this.itemHandler.getStackInSlot(2);
        boolean canMain = mainOut.isEmpty() || (ItemStack.isSameItemSameTags(mainOut, recipe.getResultMain()) &&
                (mainOut.getCount() + recipe.getResultMain().getCount() <= mainOut.getMaxStackSize()));


        if (!recipe.getResultSecond().isEmpty()) {
            ItemStack secondOut = this.itemHandler.getStackInSlot(3);
            boolean canSecond = secondOut.isEmpty() || (ItemStack.isSameItemSameTags(secondOut, recipe.getResultSecond()) &&
                    (secondOut.getCount() + recipe.getResultSecond().getCount() <= secondOut.getMaxStackSize()));
            return canMain && canSecond;
        }

        return canMain;
    }

    private void craftItem(CrusherRecipe recipe) {
        assert this.level != null;

        this.itemHandler.extractItem(0, 1, false);


        this.itemHandler.insertItem(2, recipe.getResultMain().copy(), false);


        if (!recipe.getResultSecond().isEmpty()) {
            if (this.level.random.nextFloat() <= recipe.getSecondChance()) {
                this.itemHandler.insertItem(3, recipe.getResultSecond().copy(), false);
            }
        }


        this.inventoryChanged = true;
    }



    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable net.minecraft.core.Direction side) {


        Direction blockFacing = this.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
        Direction relativeSide = GuiConfigHelper.getRelativeSide(blockFacing, side);



        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            if (side == null) {
                return itemHandlerLazyOptional.cast();
            }


            int sideId = relativeSide.get3DDataValue();
            int sideMode = this.sideConfigModes[sideId];

            if (sideMode == 0) return LazyOptional.empty();

            if (sideMode == 1) {
                return LazyOptional.of(() -> new RangedWrapper(itemHandler, 0, 1)).cast();
            }

            if (sideMode == 2) {
                return LazyOptional.of(() -> new RangedWrapper(itemHandler, 2, 4) {
                    @Override
                    public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
                        return stack;
                    }
                }).cast();
            }

            return LazyOptional.of(() -> new RangedWrapper(itemHandler, 1, 2)).cast();
        }


        if (cap == ForgeCapabilities.ENERGY) {
            if (side != null) {
                int sideId = relativeSide.get3DDataValue();
                int sideMode = this.sideConfigModes[sideId];



                if (sideMode == 0 || sideMode == 2) {
                    return net.minecraftforge.common.util.LazyOptional.empty();
                }


                if (sideMode == 1) {
                    return energyStorageLazyOptional.cast();
                }
            }
            return energyStorageLazyOptional.cast();
        }


        if (cap == ForgeCapabilities.FLUID_HANDLER) {
            if (side == null) return net.minecraftforge.common.util.LazyOptional.empty();

            int sideId = relativeSide.get3DDataValue();
            int sideMode = this.sideConfigModes[sideId];

            if (sideMode == 0) return LazyOptional.empty();

            if (sideMode == 2) {
                return LazyOptional.of(() -> new net.minecraftforge.fluids.capability.templates.FluidTank(0) {
                    @Override
                    public int fill(net.minecraftforge.fluids.FluidStack resource, net.minecraftforge.fluids.capability.IFluidHandler.FluidAction action) {
                        return 0;
                    }
                }).cast();
            }

            return LazyOptional.empty();
        }

        String capName = cap.getName();
        if (capName.contains("IGasHandler") || capName.contains("IInfusionHandler") || capName.contains("IPigmentHandler") || capName.contains("ISlurryHandler")) {
            if (side != null) {
                int sideId = relativeSide.get3DDataValue();
                int sideMode = this.sideConfigModes[sideId];
                if (sideMode == 0) return net.minecraftforge.common.util.LazyOptional.empty();
            }
            return net.minecraftforge.common.util.LazyOptional.empty();
        }

        if (capName.contains("IStrictEnergyHandler")) {
            return net.minecraftforge.common.util.LazyOptional.empty();
        }


        return super.getCapability(cap, side);
    }


    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        itemHandlerLazyOptional.invalidate();
        energyStorageLazyOptional.invalidate();
    }

    private void chargeFromEnergySlot() {
        int spaceLeft = this.energyStorage.getMaxEnergyStored() - this.energyStorage.getEnergyStored();


        if (spaceLeft <= 0) return;

        ItemStack batteryStack = this.itemHandler.getStackInSlot(1);


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
        tag.put("inventory", itemHandler.serializeNBT());
        tag.putInt("energy", energyStorage.getEnergyStored());
        tag.putInt("progress", this.progress);
        super.saveAdditional(tag);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        itemHandler.deserializeNBT(tag.getCompound("inventory"));
        this.energyStorage.setCapacity(EnergyUpgradeItem.getMaxEnergy(this));
        this.energyStorage.setEnergyDirect(tag.getInt("energy"));
        this.progress = tag.getInt("progress");
        ItemStack stack = this.itemHandler.getStackInSlot(1);
        this.hasValidBatteryInSlot = !stack.isEmpty() && stack.getCapability(ForgeCapabilities.ENERGY)
                .map(IEnergyStorage::canExtract)
                .orElse(false);
    }
}