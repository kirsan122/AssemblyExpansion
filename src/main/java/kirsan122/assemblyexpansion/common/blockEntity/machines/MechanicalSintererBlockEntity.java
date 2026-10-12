package kirsan122.assemblyexpansion.common.blockEntity.machines;

import kirsan122.assemblyexpansion.api.GuiConfigHelper;
import kirsan122.assemblyexpansion.api.RedstoneModes;
import kirsan122.assemblyexpansion.common.energy.DynamicEnergyStorage;
import kirsan122.assemblyexpansion.common.item.upgrades.EnergyUpgradeItem;
import kirsan122.assemblyexpansion.common.item.upgrades.SpeedUpgradeItem;
import kirsan122.assemblyexpansion.common.inventory.MechanicalSintererMenu;
import kirsan122.assemblyexpansion.common.recipe.MechanicalSintererRecipe;
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
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RangedWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class MechanicalSintererBlockEntity extends ConfigurableBlockEntity implements MenuProvider {
    private final int maxEnergyStorage = 8000;
    private boolean hasValidBatteryInSlot = false;
    protected final ContainerData data;
    private int progress = 0;
    private int maxProgress = 200;

    private final SimpleContainer inventoryWrapper = new SimpleContainer(4);



    private MechanicalSintererRecipe cachedRecipe = null;


    public boolean inventoryChanged = true;

    public MechanicalSintererBlockEntity(BlockPos pos, BlockState state) {
        super(AEBlockEntities.MECHANICAL_SINTERER_BE.get(), pos, state);
        this.data = new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> MechanicalSintererBlockEntity.this.progress;
                    case 1 -> SpeedUpgradeItem.getCookTime(MechanicalSintererBlockEntity.this);
                    case 2 -> MechanicalSintererBlockEntity.this.energyStorage.getEnergyStored();
                    case 3 -> MechanicalSintererBlockEntity.this.energyStorage.getMaxEnergyStored();
                    case 4 -> MechanicalSintererBlockEntity.this.redstoneMode.ordinal();
                    case 5, 6, 7, 8, 9, 10 -> MechanicalSintererBlockEntity.this.sideConfigModes[index - 5];
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> MechanicalSintererBlockEntity.this.progress = value;
                    case 1 -> MechanicalSintererBlockEntity.this.maxProgress = value;
                    case 4 ->
                            MechanicalSintererBlockEntity.this.redstoneMode = RedstoneModes.values()[value % RedstoneModes.values().length];
                    case 5, 6, 7, 8, 9, 10 -> {
                        MechanicalSintererBlockEntity.this.sideConfigModes[index - 5] = value;
                        MechanicalSintererBlockEntity.this.setChanged();

                        if (MechanicalSintererBlockEntity.this.level != null && !MechanicalSintererBlockEntity.this.level.isClientSide) {
                            net.minecraft.world.level.block.state.BlockState state = MechanicalSintererBlockEntity.this.getBlockState();
                            MechanicalSintererBlockEntity.this.level.sendBlockUpdated(MechanicalSintererBlockEntity.this.worldPosition, state, state, 3);
                            MechanicalSintererBlockEntity.this.level.updateNeighborsAt(MechanicalSintererBlockEntity.this.worldPosition, state.getBlock());
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

            if (slot == 3) return stack.getCapability(ForgeCapabilities.ENERGY).isPresent();

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
    private LazyOptional<net.minecraftforge.energy.IEnergyStorage> energyStorageLazyOptional = LazyOptional.of(this::createEnergyStorageWrapper);


    private net.minecraftforge.energy.IEnergyStorage createEnergyStorageWrapper() {
        return new net.minecraftforge.energy.IEnergyStorage() {
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
        return Component.translatable("block.assembly_expansion.mechanical_sinterer");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInv, Player player) {
        return new MechanicalSintererMenu(containerId, playerInv, this, this.data);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, MechanicalSintererBlockEntity entity) {
        if (level.isClientSide()) return;
        if (!state.is(AEMachineBlocks.MECHANICAL_SINTERER.get())) return;

        if (entity.hasValidBatteryInSlot) {
            entity.chargeFromEnergySlot();
        }


        entity.energyStorage.setCapacity(EnergyUpgradeItem.getMaxEnergy(entity));

        boolean canWork = entity.redstoneMode.canWork(level.hasNeighborSignal(pos));


        if (canWork) {
            Optional<MechanicalSintererRecipe> recipe = entity.getCurrentRecipe();
            boolean canCraft = recipe.isPresent() && entity.canInsertIntoOutputSlot(recipe.get());

            if (canCraft) {
                MechanicalSintererRecipe currentRecipe = recipe.get();

                int currentMaxProgress = SpeedUpgradeItem.getCookTime(entity);
                int currentEnergyConsumption = SpeedUpgradeItem.getEnergyUsage(20, entity.getUpgradeSlots());

                boolean hasEnoughEnergy = entity.energyStorage.getEnergyStored() >= currentEnergyConsumption;

                if (hasEnoughEnergy) {
                    entity.energyStorage.consumeEnergyDirect(currentEnergyConsumption);

                    entity.progress++;
                    setChanged(level, pos, state);

                    if (entity.progress >= currentMaxProgress) {
                        entity.craftItem(currentRecipe);
                        entity.progress = 0;
                    }
                }
            } else {
                entity.progress = 0;
            }
        }
    }


    private Optional<MechanicalSintererRecipe> getCurrentRecipe() {
        assert this.level != null;


        if (!this.inventoryChanged && this.cachedRecipe != null) {
            return Optional.of(this.cachedRecipe);
        }


        this.inventoryWrapper.setItem(0, this.itemHandler.getStackInSlot(0));
        this.inventoryWrapper.setItem(1, this.itemHandler.getStackInSlot(1));
        this.inventoryWrapper.setItem(2, this.itemHandler.getStackInSlot(2));
        this.inventoryWrapper.setItem(3, this.itemHandler.getStackInSlot(3));


        Optional<MechanicalSintererRecipe> recipeMatch = this.level.getRecipeManager().getRecipeFor(AERecipes.MECHANICAL_SINTERER_TYPE.get(), this.inventoryWrapper, this.level);


        this.cachedRecipe = recipeMatch.orElse(null);
        this.inventoryChanged = false;

        return recipeMatch;
    }

    private boolean canInsertIntoOutputSlot(MechanicalSintererRecipe recipe) {

        ItemStack mainOut = this.itemHandler.getStackInSlot(2);

        return mainOut.isEmpty() || (ItemStack.isSameItemSameTags(mainOut, recipe.getResult()) &&
                (mainOut.getCount() + recipe.getResult().getCount() <= mainOut.getMaxStackSize()));
    }

    private void craftItem(MechanicalSintererRecipe recipe) {
        assert this.level != null;

        this.itemHandler.extractItem(0, recipe.getBaseInput().getCount(), false);
        this.itemHandler.extractItem(1, recipe.getInfusorInput().getCount(), false);

        this.itemHandler.insertItem(2, recipe.getResult().copy(), false);


        this.inventoryChanged = true;
    }


    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull net.minecraftforge.common.capabilities.Capability<T> cap, @Nullable net.minecraft.core.Direction side) {


        Direction blockFacing = this.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
        Direction relativeSide = GuiConfigHelper.getRelativeSide(blockFacing, side);



        if (cap == net.minecraftforge.common.capabilities.ForgeCapabilities.ITEM_HANDLER) {
            if (side == null) {
                return itemHandlerLazyOptional.cast();
            }


            int sideId = relativeSide.get3DDataValue();
            int sideMode = this.sideConfigModes[sideId];

            if (sideMode == 0) return net.minecraftforge.common.util.LazyOptional.empty();

            if (sideMode == 1) {
                return net.minecraftforge.common.util.LazyOptional.of(() -> new net.minecraftforge.items.wrapper.RangedWrapper(itemHandler, 0, 1)).cast();
            }

            if (sideMode == 2) {
                return net.minecraftforge.common.util.LazyOptional.of(() -> new net.minecraftforge.items.wrapper.RangedWrapper(itemHandler, 2, 4) {
                    @Override
                    public @org.jetbrains.annotations.NotNull net.minecraft.world.item.ItemStack insertItem(int slot, @org.jetbrains.annotations.NotNull net.minecraft.world.item.ItemStack stack, boolean simulate) {
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