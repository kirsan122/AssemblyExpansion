package kirsan122.assemblyexpansion.api;

import net.minecraftforge.items.ItemStackHandler;

public interface IUpgradableBE {
    ItemStackHandler getUpgradeSlots();

    int getBaseProcessTime();

    int getMaxEnergy();
}


