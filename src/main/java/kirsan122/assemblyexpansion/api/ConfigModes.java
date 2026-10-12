package kirsan122.assemblyexpansion.api;

public enum ConfigModes {
    NONE("tooltip.assembly_expansion.mode.none"),
    INPUT("tooltip.assembly_expansion.mode.input"),
    OUTPUT("tooltip.assembly_expansion.mode.output");

    public final String translationKey;

    ConfigModes(String translationKey) {
        this.translationKey = translationKey;
    }

    public enum ConfigType {
        ENERGY,
        ITEM,
        FLUID
    }
}



