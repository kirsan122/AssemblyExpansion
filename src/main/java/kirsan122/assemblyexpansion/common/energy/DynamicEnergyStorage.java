package kirsan122.assemblyexpansion.common.energy;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

public class DynamicEnergyStorage extends net.minecraftforge.energy.EnergyStorage {

    public DynamicEnergyStorage(int capacity, int maxReceive, int maxExtract) {
        super(capacity, maxReceive, maxExtract);
    }


    public void setCapacity(int newCapacity) {
        this.capacity = newCapacity;
        if (this.energy > newCapacity) {
            this.energy = newCapacity;
        }
    }

    public void setEnergyDirect(int amount) {

        this.energy = Math.max(0, Math.min(amount, this.capacity));
    }

    public void consumeEnergyDirect(int amount) {
        this.energy = Math.max(0, this.energy - amount);
    }


    @Override
    public void deserializeNBT(Tag nbt) {
        if (!(nbt instanceof CompoundTag compound)) return;


        int savedEnergy = compound.getInt("energy");




        this.energy = Math.max(0, Math.min(savedEnergy, this.capacity));
    }
}
