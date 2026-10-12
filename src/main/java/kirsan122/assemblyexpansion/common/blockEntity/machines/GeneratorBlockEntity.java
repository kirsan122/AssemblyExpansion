package kirsan122.assemblyexpansion.common.blockEntity.machines;

import kirsan122.assemblyexpansion.common.inventory.GeneratorMenu;
import kirsan122.assemblyexpansion.common.recipe.GeneratorRecipe;
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
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GeneratorBlockEntity extends BlockEntity implements MenuProvider {

    private final int maxExtract = 20;


    private final ItemStackHandler itemHandler = new ItemStackHandler(2);



    private final EnergyStorage energy = new EnergyStorage(5000, 20, maxExtract) {
        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            setChanged();
            return super.receiveEnergy(maxReceive, simulate);
        }
    };



    protected final ContainerData data;
    private int litTime;
    private int maxLitTime;


    public GeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(AEBlockEntities.GENERATOR_BE.get(), pos, state);
        this.data = new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> GeneratorBlockEntity.this.energy.getEnergyStored();
                    case 1 -> GeneratorBlockEntity.this.energy.getMaxEnergyStored();
                    case 2 -> GeneratorBlockEntity.this.litTime;
                    case 3 -> GeneratorBlockEntity.this.maxLitTime;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) { /* Клиенту менять нельзя */ }

            @Override
            public int getCount() {
                return 4;
            }
        };
    }




    public static void tick(Level level, BlockPos pos, BlockState state, GeneratorBlockEntity entity) {
        if (level.isClientSide) return;

        ItemStack fuelStack = entity.itemHandler.getStackInSlot(0);
        int fuelValue = getBurnTime(level, fuelStack);


        if (entity.litTime > 0 && entity.energy.getEnergyStored() < entity.energy.getMaxEnergyStored()) {
            entity.litTime--;



            entity.energy.receiveEnergy(20, false);

            setChanged(level, pos, state);
        }


        if (entity.litTime <= 0 && fuelValue > 0 && entity.energy.getEnergyStored() < entity.energy.getMaxEnergyStored()) {
            entity.maxLitTime = fuelValue;
            entity.litTime = fuelValue;

            fuelStack.shrink(1);
            setChanged(level, pos, state);
        }


        if (entity.energy.getEnergyStored() > 0) {
            final boolean[] energyChanged = new boolean[]{false};

            for (Direction direction : Direction.values()) {
                if (entity.energy.getEnergyStored() <= 0) break;

                BlockEntity neighbor = level.getBlockEntity(pos.relative(direction));
                if (neighbor != null) {

                    neighbor.getCapability(ForgeCapabilities.ENERGY, direction.getOpposite()).ifPresent(storage -> {
                        if (storage.canReceive()) {

                            int toSend = entity.energy.extractEnergy(entity.maxExtract, true);

                            if (toSend > 0) {

                                int accepted = storage.receiveEnergy(toSend, false);

                                if (accepted > 0) {

                                    entity.energy.extractEnergy(accepted, false);
                                    energyChanged[0] = true;
                                }
                            }
                        }
                    });
                }
            }

            if (energyChanged[0]) {
                setChanged(level, pos, state);
            }
        }



        ItemStack batteryStack = entity.itemHandler.getStackInSlot(1);
        if (!batteryStack.isEmpty() && entity.energy.getEnergyStored() > 0) {
            batteryStack.getCapability(ForgeCapabilities.ENERGY).ifPresent(batteryStorage -> {
                if (batteryStorage.canReceive()) {
                    int energyAvailable = entity.energy.extractEnergy(entity.maxExtract, true);
                    if (energyAvailable > 0) {
                        int acceptedSimulate = batteryStorage.receiveEnergy(energyAvailable, true);
                        if (acceptedSimulate > 0) {
                            int realExtracted = entity.energy.extractEnergy(acceptedSimulate, false);
                            batteryStorage.receiveEnergy(realExtracted, false);
                            setChanged(level, pos, state);
                        }
                    }
                }
            });
        }
    }

    private static int getBurnTime(Level level, ItemStack stack) {

        if (stack == null || stack.isEmpty()) {
            return 0;
        }


        List<GeneratorRecipe> recipes = level.getRecipeManager()
                .getAllRecipesFor(AERecipes.GENERATOR_FUEL_TYPE.get());


        if (recipes.isEmpty()) {
            return 0;
        }

        for (GeneratorRecipe recipe : recipes) {
            int burnTime = recipe.getBurnTime(stack);

            if (burnTime > 0) {
                return burnTime;
            }
        }

        return 0;
    }



    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        inventoryCap.invalidate();
        energyCap.invalidate();
    }



    private final LazyOptional<IItemHandler> inventoryCap = LazyOptional.of(() -> itemHandler);
    private final LazyOptional<IEnergyStorage> energyCap = LazyOptional.of(() -> energy);

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) return inventoryCap.cast();
        if (cap == ForgeCapabilities.ENERGY) return energyCap.cast();
        return super.getCapability(cap, side);
    }


    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("inventory", itemHandler.serializeNBT());
        tag.put("energy", energy.serializeNBT());
        tag.putInt("litTime", litTime);
        tag.putInt("maxLitTime", maxLitTime);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        itemHandler.deserializeNBT(tag.getCompound("inventory"));
        if (tag.contains("energy")) {
            energy.deserializeNBT(tag.get("energy"));
        }
        this.litTime = tag.getInt("litTime");
        this.maxLitTime = tag.getInt("maxLitTime");
    }


    public IItemHandler getItemHandler() {
        return itemHandler;
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("block.assembly_expansion.generator");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return new GeneratorMenu(id, inv, this, this.data);
    }
}
