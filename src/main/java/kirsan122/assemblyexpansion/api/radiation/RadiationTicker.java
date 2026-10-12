package kirsan122.assemblyexpansion.api.radiation;

import kirsan122.assemblyexpansion.AssemblyExpansion;
import kirsan122.assemblyexpansion.common.item.RadiationShieldArmor;
import kirsan122.assemblyexpansion.common.item.RadioactiveItem;
import kirsan122.assemblyexpansion.common.init.AEDamageTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = AssemblyExpansion.MOD_ID)
public class RadiationTicker {

    private static final int DAMAGE_THRESHOLD = 10000;

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {

        if (event.phase == TickEvent.Phase.END && !event.player.level().isClientSide && event.player.tickCount % 20 == 0) {
            Player player = event.player;


            if (player.isCreative() || player.isSpectator()) {
                return;
            }

            player.getCapability(RadiationProvider.RADIATION_CAP).ifPresent(rad -> {
                int totalInventoryRadiationPerSecond = 0;


                for (ItemStack itemStack : player.getInventory().items) {
                    if (!itemStack.isEmpty() && itemStack.getItem() instanceof RadioactiveItem radioactiveItem) {
                        totalInventoryRadiationPerSecond += radioactiveItem.getRadiationSpeed() * 20 * itemStack.getCount();
                    }
                }


                if (totalInventoryRadiationPerSecond > 0) {
                    int totalResistancePercent = 0;
                    for (ItemStack armorStack : player.getArmorSlots()) {
                        if (armorStack.getItem() instanceof RadiationShieldArmor radiationArmor) {
                            totalResistancePercent += radiationArmor.getRadiationResistancePercent();
                        }
                    }
                    totalResistancePercent = Math.min(100, totalResistancePercent);


                    int finalRadiation = (totalInventoryRadiationPerSecond * (100 - totalResistancePercent)) / 100;
                    if (finalRadiation > 0) {
                        rad.addRadiation(finalRadiation);
                    }
                } else {

                    if (rad.getRadiation() > 0) {
                        rad.addRadiation(-10);
                    }
                }


                int currentRad = rad.getRadiation();
                if (currentRad >= DAMAGE_THRESHOLD) {
                    var registry = player.level().registryAccess().registryOrThrow(net.minecraft.core.registries.Registries.DAMAGE_TYPE);
                    var holder = registry.getHolderOrThrow(AEDamageTypes.RADIATION);
                    net.minecraft.world.damagesource.DamageSource radiationDamage = new net.minecraft.world.damagesource.DamageSource(holder);

                    player.hurt(radiationDamage, 2.0f);
                }
            });
        }
    }
}
