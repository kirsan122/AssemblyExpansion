package kirsan122.assemblyexpansion.common.blockEntity.machines.distillation_column;

import kirsan122.assemblyexpansion.common.block.machines.distillation_column.DistillationColumnBaseBlock;
import kirsan122.assemblyexpansion.common.inventory.DistillationColumnMenu;
import kirsan122.assemblyexpansion.common.recipe.DistillationColumnRecipe;
import kirsan122.assemblyexpansion.common.init.AEBlockEntities;
import kirsan122.assemblyexpansion.common.init.AEMachineBlocks;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DistillationColumnBlockEntity extends BlockEntity implements MenuProvider {
    private final FluidTank[] tanks = new FluidTank[5];
    private final LazyOptional<IFluidHandler>[] handlers = new LazyOptional[5];


    private final EnergyStorage energyStorage = new EnergyStorage(7000, 50, 50) {
        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            int received = super.receiveEnergy(maxReceive, simulate);
            if (received > 0 && !simulate) {
                setChanged();
            }
            return received;
        }
    };
    private final LazyOptional<IEnergyStorage> energyHandler = LazyOptional.of(() -> energyStorage);
    private final ContainerData data;
    private int progress = 0;
    private final int maxProgress = 100;
    private boolean structureValid = false;

    public DistillationColumnBlockEntity(BlockPos pos, BlockState state) {
        super(AEBlockEntities.DISTILLATION_COLUMN_BE.get(), pos, state);


        this.data = new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> DistillationColumnBlockEntity.this.progress;
                    case 1 -> DistillationColumnBlockEntity.this.maxProgress;
                    case 2 -> DistillationColumnBlockEntity.this.energyStorage.getEnergyStored();
                    case 3 -> DistillationColumnBlockEntity.this.energyStorage.getMaxEnergyStored();
                    default -> 0;
                };
            }
            @Override
            public void set(int index, int value) {
                if (index == 0) {
                    DistillationColumnBlockEntity.this.progress = value;
                }
            }

            @Override
            public int getCount() {
                return 4;
            }
        };

        for (int i = 0; i < 5; i++) {
            final int index = i;
            tanks[i] = new FluidTank(5000) {
                @Override protected void onContentsChanged() { setChanged();
                    if (level != null && !level.isClientSide) {

                        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
                    } }
            };

            handlers[i] = LazyOptional.of(() -> tanks[index]);
        }
    }

    public boolean isStructureValid() {
        return structureValid;
    }
    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        for (int i = 0; i < 5; i++) handlers[i].invalidate();
        energyHandler.invalidate();
    }

    public LazyOptional<IFluidHandler> getTankCapability(int tankIndex) {
        if (!this.structureValid) return LazyOptional.empty();
        return handlers[tankIndex];
    }


    public FluidStack getInputTankFluid() {
        if (this.tanks[0] != null) {
            return this.tanks[0].getFluid();
        }
        return FluidStack.EMPTY;
    }


    public FluidStack[] getOutputTankFluids() {
        FluidStack[] outputs = new FluidStack[4];
        for (int i = 0; i < 4; i++) {

            if (this.tanks[i + 1] != null) {
                outputs[i] = this.tanks[i + 1].getFluid();
            } else {
                outputs[i] = FluidStack.EMPTY;
            }
        }
        return outputs;
    }


    @Override
    public Component getDisplayName() {
        return Component.translatable("tooltip.screen.assembly_expansion.distillation_column");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {

        if (!this.structureValid) {
            return null;
        }
        return new DistillationColumnMenu(id, inventory, this, this.data);
    }


    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.FLUID_HANDLER) {
            if (!this.structureValid) return LazyOptional.empty();
            if (side == Direction.UP || side == Direction.DOWN) return handlers[0].cast();
            return handlers[4].cast();
        }
        if (cap == ForgeCapabilities.ENERGY) {
            if (!this.structureValid) return LazyOptional.empty();
            return energyHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    public void updateStructure() {
        if (this.level == null || level.isClientSide) return;

        boolean oldState = this.structureValid;

        boolean check1 = level.getBlockState(worldPosition.above(1)).is(AEMachineBlocks.DISTILLATION_COLUMN_SECTION.get());
        boolean check2 = level.getBlockState(worldPosition.above(2)).is(AEMachineBlocks.DISTILLATION_COLUMN_SECTION.get());
        boolean check3 = level.getBlockState(worldPosition.above(3)).is(AEMachineBlocks.DISTILLATION_COLUMN_TOP.get());

        this.structureValid = check1 && check2 && check3;


        if (oldState != this.structureValid) {
            setChanged();
            BlockState currentState = level.getBlockState(worldPosition);
            if (currentState.hasProperty(DistillationColumnBaseBlock.IS_STRUCTURE_VALID)) {
                level.setBlock(worldPosition, currentState.setValue(DistillationColumnBaseBlock.IS_STRUCTURE_VALID, this.structureValid), 3);
            }


            for (int i = 1; i <= 3; i++) {
                BlockPos componentPos = worldPosition.above(i);

                level.updateNeighborsAt(componentPos, level.getBlockState(componentPos).getBlock());
            }
        }

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

    public static void tick(Level level, BlockPos pos, BlockState state, DistillationColumnBlockEntity blockEntity) {
        if (level.isClientSide) return;



        if (level.getGameTime() % 20 == 0) {
            blockEntity.updateStructure();
        }

        if (!blockEntity.structureValid) {
            blockEntity.progress = 0;
            return;
        }

        if (blockEntity.tanks[0].getFluid().isEmpty()) {
            blockEntity.progress = 0;
            return;
        }

        DistillationColumnRecipe recipe = level.getRecipeManager()
                .getAllRecipesFor(AERecipes.DISTILLATION_COLUMN_TYPE.get())
                .stream()
                .filter(r -> r.getInputFluid().isFluidEqual(blockEntity.tanks[0].getFluid())
                        && blockEntity.tanks[0].getFluidAmount() >= r.getInputFluid().getAmount())
                .findFirst().orElse(null);


        if (recipe != null && blockEntity.canOutput(recipe.getOutputs()) && blockEntity.energyStorage.getEnergyStored() >= 50) {
            blockEntity.energyStorage.extractEnergy(50, false);

            blockEntity.progress++;
            setChanged(level, pos, state);

            if (blockEntity.progress >= blockEntity.maxProgress) {
                blockEntity.tanks[0].drain(recipe.getInputFluid().getAmount(), IFluidHandler.FluidAction.EXECUTE);
                for (int i = 0; i < 4; i++) {
                    if (!recipe.getOutputs()[i].isEmpty()) {
                        blockEntity.tanks[i + 1].fill(recipe.getOutputs()[i].copy(), IFluidHandler.FluidAction.EXECUTE);
                    }
                }
                blockEntity.progress = 0;
            }
        } else {

            if (blockEntity.progress > 0) {
                blockEntity.progress--;
                setChanged(level, pos, state);
            }
        }

    }

    private boolean canOutput(FluidStack[] outputs) {
        for (int i = 0; i < 4; i++) {
            if (outputs[i].isEmpty()) continue;
            FluidTank targetTank = tanks[i + 1];
            if (!targetTank.getFluid().isEmpty() && !targetTank.getFluid().isFluidEqual(outputs[i])) return false;
            if (targetTank.getFluidAmount() + outputs[i].getAmount() > targetTank.getCapacity()) return false;
        }
        return true;
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        tag.put("inputTank", tanks[0].writeToNBT(new CompoundTag()));
        for (int i = 1; i < 5; i++) {
            tag.put("Tank" + i, tanks[i].writeToNBT(new CompoundTag()));
        }
        tag.put("energy", energyStorage.serializeNBT());
        tag.putInt("progress", progress);
        tag.putBoolean("structureValid", structureValid);
        super.saveAdditional(tag);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        tanks[0].readFromNBT(tag.getCompound("inputTank"));
        for (int i = 1; i < 5; i++) {
            tanks[i].readFromNBT(tag.getCompound("Tank" + i));
        }
        if (tag.contains("energy")) {
            energyStorage.deserializeNBT(tag.get("energy"));
        }
        progress = tag.getInt("progress");
        structureValid = tag.getBoolean("structureValid");
    }
}
