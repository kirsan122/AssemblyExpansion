package kirsan122.assemblyexpansion.common.init;

import kirsan122.assemblyexpansion.AssemblyExpansion;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageType;

public class AEDamageTypes {
    public static final ResourceKey<DamageType> ELECTRIC_SHOCK = ResourceKey.create(
            Registries.DAMAGE_TYPE,
            new ResourceLocation(AssemblyExpansion.MOD_ID, "electric_shock")
    );
    public static final ResourceKey<DamageType> RADIATION = ResourceKey.create(
            Registries.DAMAGE_TYPE,
            new ResourceLocation(AssemblyExpansion.MOD_ID, "radiation")
    );
}
