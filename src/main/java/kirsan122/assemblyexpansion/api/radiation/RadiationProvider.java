package kirsan122.assemblyexpansion.api.radiation;

import kirsan122.assemblyexpansion.AssemblyExpansion;
import net.minecraft.core.Direction;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Mod.EventBusSubscriber(modid = AssemblyExpansion.MOD_ID)
public class RadiationProvider implements ICapabilityProvider, INBTSerializable<Tag> {
    public static final Capability<IRadiation> RADIATION_CAP = CapabilityManager.get(new CapabilityToken<>() {});
    public static final ResourceLocation IDENTIFIER = new ResourceLocation(AssemblyExpansion.MOD_ID, "radiation");

    private final IRadiation backend = new Radiation();
    private final LazyOptional<IRadiation> optional = LazyOptional.of(() -> backend);

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return RADIATION_CAP.orEmpty(cap, optional);
    }

    @Override
    public Tag serializeNBT() {
        return IntTag.valueOf(backend.getRadiation());
    }

    @Override
    public void deserializeNBT(Tag nbt) {
        if (nbt instanceof IntTag intTag) {
            backend.setRadiation(intTag.getAsInt());
        }
    }

    @SubscribeEvent
    public static void attachCapability(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) {
            event.addCapability(IDENTIFIER, new RadiationProvider());
        }
    }
}

