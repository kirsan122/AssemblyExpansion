package kirsan122.assemblyexpansion.api;

import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;

public enum RedstoneModes implements StringRepresentable {
    IGNORED("ignored", 25, "tooltip.assembly_expansion.redstone.ignored"),
    HIGH("high", 43, "tooltip.assembly_expansion.redstone.high"),
    LOW("low", 61, "tooltip.assembly_expansion.redstone.low");

    private final String name;
    private final int textureX;
    private final String tooltipKey;

    RedstoneModes(String name, int textureX, String tooltipKey) {
        this.name = name;
        this.textureX = textureX;
        this.tooltipKey = tooltipKey;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }

    public int getTextureX() {
        return this.textureX;
    }

    public Component getTooltip() {
        return Component.translatable(this.tooltipKey);
    }

    public RedstoneModes next() {
        return values()[(this.ordinal() + 1) % values().length];
    }

    public boolean canWork(boolean hasSignal) {
        return switch (this) {
            case IGNORED -> true;
            case HIGH -> hasSignal;
            case LOW -> !hasSignal;
        };
    }
}

