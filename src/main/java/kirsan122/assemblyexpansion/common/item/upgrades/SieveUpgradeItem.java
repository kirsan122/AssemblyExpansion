package kirsan122.assemblyexpansion.common.item.upgrades;

import net.minecraft.world.item.Item;

@SuppressWarnings("unused")
public class SieveUpgradeItem extends Item {
    private final float secondaryDropMultiplier;

    public SieveUpgradeItem(float secondaryDropMultiplier, Properties properties) {
        super(properties);
        this.secondaryDropMultiplier = secondaryDropMultiplier;
    }

    public float getSecondaryDropMultiplier() {
        return this.secondaryDropMultiplier;
    }
}
