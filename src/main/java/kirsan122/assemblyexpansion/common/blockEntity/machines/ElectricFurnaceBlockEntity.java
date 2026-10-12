package kirsan122.assemblyexpansion.common.blockEntity.machines;

import kirsan122.assemblyexpansion.api.GuiConfigHelper;
import kirsan122.assemblyexpansion.api.RedstoneModes;
import kirsan122.assemblyexpansion.common.block.machines.ElectricFurnaceBlock;
import kirsan122.assemblyexpansion.common.energy.DynamicEnergyStorage;
import kirsan122.assemblyexpansion.common.item.upgrades.EnergyUpgradeItem;
import kirsan122.assemblyexpansion.common.item.upgrades.SpeedUpgradeItem;
import kirsan122.assemblyexpansion.common.inventory.ElectricFurnaceMenu;
import kirsan122.assemblyexpansion.common.init.AEBlockEntities;
import kirsan122.assemblyexpansion.common.init.AEMachineBlocks;
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
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
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

public class ElectricFurnaceBlockEntity extends ConfigurableBlockEntity implements MenuProvider {
    private boolean hasValidBatteryInSlot = false;
    private final SimpleContainer inventoryWrapper = new SimpleContainer(3);
    private SmeltingRecipe cachedRecipe = null;
    public boolean inventoryChanged = true;

    private final ItemStackHandler itemHandler = new ItemStackHandler(3) {
        @Override
        protected void onContentsChanged(int slot) {
            onInventorySlotChanged(slot);
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return slot != 2;
        }
    };

    private final DynamicEnergyStorage energyStorage = new DynamicEnergyStorage(10000, 1000, 0) {
        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {

            if (level != null) {
                boolean hasSignal = level.hasNeighborSignal(worldPosition);
                if (!redstoneMode.canWork(hasSignal)) return 0;
            }

            int received = super.receiveEnergy(maxReceive, simulate);
            if (received > 0 && !simulate) {
                setChanged();
            }
            return received;
        }
    };
    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.of(() -> itemHandler);
    private LazyOptional<IEnergyStorage> lazyEnergyHandler = LazyOptional.of(this::createEnergyStorageWrapper);

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

    protected final ContainerData data;
    private int progress = 0;
    private int maxProgress = 200;

    public ElectricFurnaceBlockEntity(BlockPos pos, BlockState state) {
        super(AEBlockEntities.ELECTRIC_FURNACE_BE.get(), pos, state);
        this.data = new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> ElectricFurnaceBlockEntity.this.progress;
                    case 1 -> ElectricFurnaceBlockEntity.this.maxProgress;
                    case 2 -> ElectricFurnaceBlockEntity.this.energyStorage.getEnergyStored();
                    case 3 -> ElectricFurnaceBlockEntity.this.energyStorage.getMaxEnergyStored();
                    case 4 -> ElectricFurnaceBlockEntity.this.redstoneMode.ordinal();
                    case 5, 6, 7, 8, 9, 10 -> ElectricFurnaceBlockEntity.this.sideConfigModes[index - 5];
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> ElectricFurnaceBlockEntity.this.progress = value;
                    case 1 -> ElectricFurnaceBlockEntity.this.maxProgress = value;
                    case 4 ->
                            ElectricFurnaceBlockEntity.this.redstoneMode = RedstoneModes.values()[value % RedstoneModes.values().length];
                    case 5, 6, 7, 8, 9, 10 -> {
                        ElectricFurnaceBlockEntity.this.sideConfigModes[index - 5] = value;
                        ElectricFurnaceBlockEntity.this.setChanged();

                        if (ElectricFurnaceBlockEntity.this.level != null && !ElectricFurnaceBlockEntity.this.level.isClientSide) {
                            BlockState state = ElectricFurnaceBlockEntity.this.getBlockState();
                            ElectricFurnaceBlockEntity.this.level.sendBlockUpdated(ElectricFurnaceBlockEntity.this.worldPosition, state, state, 3);
                            ElectricFurnaceBlockEntity.this.level.updateNeighborsAt(ElectricFurnaceBlockEntity.this.worldPosition, state.getBlock());
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
    public Component getDisplayName() {
        return Component.translatable("block.assembly_expansion.electric_furnace");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {

        return new ElectricFurnaceMenu(id, inventory, this, this.data);
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

    public static void tick(Level level, BlockPos pos, BlockState state, ElectricFurnaceBlockEntity entity) {
        if (level.isClientSide()) return;
        if (!state.is(AEMachineBlocks.ELECTRIC_FURNACE.get())) return;

        if (entity.hasValidBatteryInSlot) {
            entity.chargeFromEnergySlot();
        }


        entity.energyStorage.setCapacity(EnergyUpgradeItem.getMaxEnergy(entity));

        boolean canWork = entity.redstoneMode.canWork(level.hasNeighborSignal(pos));
        boolean isCrafting = false;

        if (canWork) {

            Optional<SmeltingRecipe> recipe = entity.getCurrentRecipe();
            boolean canCraft = recipe.isPresent() && entity.canInsertIntoOutputSlot(recipe.get());

            if (canCraft) {
                SmeltingRecipe currentRecipe = recipe.get();

                int currentMaxProgress = SpeedUpgradeItem.getCookTime(entity);
                int currentEnergyConsumption = SpeedUpgradeItem.getEnergyUsage(20, entity.getUpgradeSlots());

                boolean hasEnoughEnergy = entity.energyStorage.getEnergyStored() >= currentEnergyConsumption;

                if (hasEnoughEnergy) {
                    isCrafting = true;
                    entity.energyStorage.consumeEnergyDirect(currentEnergyConsumption);

                    entity.progress++;
                    setChanged(level, pos, state);

                    if (entity.progress >= currentMaxProgress) {
                        entity.craftItem(currentRecipe);
                        entity.progress = 0;


                        Optional<SmeltingRecipe> nextRecipe = entity.getCurrentRecipe();
                        boolean canContinue = nextRecipe.isPresent() && entity.canInsertIntoOutputSlot(nextRecipe.get());

                        isCrafting = canContinue && entity.energyStorage.getEnergyStored() >= SpeedUpgradeItem.getEnergyUsage(20, entity.getUpgradeSlots());
                    }
                }
            } else {
                entity.progress = 0;
            }
        }


        if (state.getValue(ElectricFurnaceBlock.ACTIVE) != isCrafting) {
            level.setBlock(pos, state.setValue(ElectricFurnaceBlock.ACTIVE, isCrafting), 3);
        }
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

    private Optional<SmeltingRecipe> getCurrentRecipe() {
        assert this.level != null;


        if (!this.inventoryChanged && this.cachedRecipe != null) {
            return Optional.of(this.cachedRecipe);
        }


        for (int i = 0; i < this.itemHandler.getSlots(); i++) {
            this.inventoryWrapper.setItem(i, this.itemHandler.getStackInSlot(i));
        }


        Optional<SmeltingRecipe> recipeMatch = this.level.getRecipeManager().getRecipeFor(RecipeType.SMELTING, this.inventoryWrapper, this.level);


        this.cachedRecipe = recipeMatch.orElse(null);
        this.inventoryChanged = false;

        return recipeMatch;
    }

    private boolean canInsertIntoOutputSlot(SmeltingRecipe recipe) {
        assert this.level != null;
        ItemStack resultItem = recipe.getResultItem(this.level.registryAccess());
        ItemStack outputSlot = this.itemHandler.getStackInSlot(2);


        if (outputSlot.isEmpty()) return true;


        if (!ItemStack.isSameItemSameTags(outputSlot, resultItem)) return false;


        return (outputSlot.getCount() + resultItem.getCount()) <= outputSlot.getMaxStackSize();
    }

    private void craftItem(SmeltingRecipe recipe) {
        assert this.level != null;
        ItemStack resultItem = recipe.getResultItem(this.level.registryAccess()).copy();
        if (resultItem.isEmpty()) return;


        this.itemHandler.extractItem(0, 1, false);

        ItemStack currentOutput = this.itemHandler.getStackInSlot(2);
        if (currentOutput.isEmpty()) {
            this.itemHandler.setStackInSlot(2, resultItem);
        } else {
            currentOutput.grow(resultItem.getCount());
            this.itemHandler.setStackInSlot(2, currentOutput);
        }


        this.inventoryChanged = true;
    }



    @Override
    public void saveAdditional(CompoundTag tag) {
        tag.put("inventory", itemHandler.serializeNBT());
        tag.putInt("energy", energyStorage.getEnergyStored());
        tag.putInt("progress", progress);
        super.saveAdditional(tag);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        itemHandler.deserializeNBT(tag.getCompound("inventory"));
        this.energyStorage.receiveEnergy(tag.getInt("energy"), false);
        this.progress = tag.getInt("progress");
        this.energyStorage.setCapacity(EnergyUpgradeItem.getMaxEnergy(this));
    }

    public IItemHandler getItemHandler() {
        return itemHandler;
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
        lazyEnergyHandler.invalidate();
    }

    public ItemStackHandler getInventory() {
        return this.itemHandler;
    }

    @Override
    public int getMaxEnergy() {
        return this.data.get(3);
    }

    @Override
    public int getBaseProcessTime() {
        return this.maxProgress;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull net.minecraftforge.common.capabilities.Capability<T> cap, @Nullable net.minecraft.core.Direction side) {


        Direction blockFacing = this.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
        Direction relativeSide = GuiConfigHelper.getRelativeSide(blockFacing, side);



        if (cap == net.minecraftforge.common.capabilities.ForgeCapabilities.ITEM_HANDLER) {
            if (side == null) {
                return lazyItemHandler.cast();
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
                    return lazyEnergyHandler.cast();
                }
            }
            return lazyEnergyHandler.cast();
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
    public void refreshCaps() {

        this.lazyItemHandler.invalidate();
        this.lazyEnergyHandler.invalidate();


        this.lazyItemHandler = LazyOptional.of(() -> itemHandler);
        this.lazyEnergyHandler = LazyOptional.of(this::createEnergyStorageWrapper);
    }

}