package kirsan122.assemblyexpansion.common.item;

public class RadioactiveItem extends net.minecraft.world.item.Item {
    private final int radiationSpeed;

    public RadioactiveItem(int radiationSpeed, Properties properties) {
        super(properties);
        this.radiationSpeed = radiationSpeed;
    }

    public int getRadiationSpeed() {
        return this.radiationSpeed;
    }
}
