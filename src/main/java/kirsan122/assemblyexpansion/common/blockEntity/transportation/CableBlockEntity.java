package kirsan122.assemblyexpansion.common.blockEntity.transportation;

import kirsan122.assemblyexpansion.common.block.transportation.CableBlock;
import kirsan122.assemblyexpansion.common.init.AEBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CableBlockEntity extends BlockEntity implements IEnergyStorage {

    private boolean isDistributing = false;
    private final LazyOptional<IEnergyStorage> energyHandler = LazyOptional.of(() -> this);


    private long lastTick = -1;
    private int energyTransferredThisTick = 0;

    public CableBlockEntity(BlockPos pos, BlockState state) {
        super(AEBlockEntities.CABLE_TYPE.get(), pos, state);
    }

    private int getMaxTransferFromBlock() {
        if (this.getBlockState().getBlock() instanceof CableBlock cableBlock) {
            return cableBlock.maxTransfer;
        }
        return 0;
    }
    public int getEnergyTransferredThisTick() {
        return this.energyTransferredThisTick;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        if (this.isDistributing) return 0;
        if (this.level == null || this.level.isClientSide) return 0;


        long currentTick = this.level.getGameTime();
        if (this.lastTick != currentTick) {
            this.lastTick = currentTick;
            this.energyTransferredThisTick = 0;
        }

        int currentMaxTransfer = getMaxTransferFromBlock();


        int remainingCapacity = Math.max(0, currentMaxTransfer - this.energyTransferredThisTick);
        int allowedToTransfer = Math.min(remainingCapacity, maxReceive);
        if (allowedToTransfer <= 0) return 0;

        if (simulate) {
            return allowedToTransfer;
        }

        this.isDistributing = true;
        int energyLeftToDistribute = allowedToTransfer;
        int totalAcceptedByNetwork = 0;

        try {

            for (Direction direction : Direction.values()) {
                if (energyLeftToDistribute <= 0) break;

                BlockPos targetPos = this.worldPosition.relative(direction);
                BlockEntity targetBE = this.level.getBlockEntity(targetPos);

                if (targetBE != null) {

                    LazyOptional<IEnergyStorage> neighborCap = targetBE.getCapability(ForgeCapabilities.ENERGY, direction.getOpposite());

                    if (neighborCap.isPresent()) {
                        boolean canNeighborReceive = neighborCap.map(IEnergyStorage::canReceive).orElse(false);
                        boolean canNeighborExtract = neighborCap.map(IEnergyStorage::canExtract).orElse(false);





                        if (!canNeighborReceive || (canNeighborExtract && !canNeighborReceive)) {
                            continue;
                        }

                        boolean isNeighborCable = targetBE instanceof CableBlockEntity;
                        int finalEnergyLeftToDistribute = energyLeftToDistribute;

                        int acceptedByNeighbor = neighborCap.map(storage -> storage.receiveEnergy(finalEnergyLeftToDistribute, false)).orElse(0);

                        if (acceptedByNeighbor > 0) {
                            energyLeftToDistribute -= acceptedByNeighbor;

                            if (!isNeighborCable) {
                                totalAcceptedByNetwork += acceptedByNeighbor;
                            } else {
                                this.energyTransferredThisTick += acceptedByNeighbor;
                            }
                        }
                    }
                }
            }



        } finally {
            this.isDistributing = false;
        }


        this.energyTransferredThisTick += totalAcceptedByNetwork;


        return allowedToTransfer - energyLeftToDistribute;
    }


    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        return 0;
    }

    @Override
    public int getEnergyStored() {
        return 0;
    }

    @Override
    public int getMaxEnergyStored() {
        return getMaxTransferFromBlock();
    }

    @Override
    public boolean canExtract() {
        return false;
    }

    @Override
    public boolean canReceive() {
        return true;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ENERGY) {
            return this.energyHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        this.energyHandler.invalidate();
    }
}
