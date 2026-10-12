package kirsan122.assemblyexpansion.common.item.upgrades;

import kirsan122.assemblyexpansion.api.IUpgradableBE;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SpeedUpgradeItem extends Item {
    private static final double SPEED_MULTIPLIER = 0.90;
    private static final double ENERGY_MULTIPLIER = 1.15;


    public SpeedUpgradeItem(Properties properties) {
        super(properties);
    }

    public static int getUpgradeCount(ItemStackHandler upgradeSlots) {
        int count = 0;
        for (int i = 0; i < upgradeSlots.getSlots(); i++) {
            ItemStack stack = upgradeSlots.getStackInSlot(i);
            if (stack.getItem() instanceof SpeedUpgradeItem) {
                count += stack.getCount();
            }
        }
        return count;
    }

    public static int getCookTime(IUpgradableBE tile) {
        int count = getUpgradeCount(tile.getUpgradeSlots());
        double time = tile.getBaseProcessTime();
        for (int i = 0; i < count; i++) {
            time *= SPEED_MULTIPLIER;
        }
        return Math.max(1, Mth.floor(time));
    }

    public static int getEnergyUsage(int baseEnergy, ItemStackHandler upgradeSlots) {
        int count = getUpgradeCount(upgradeSlots);
        double energy = baseEnergy;
        for (int i = 0; i < count; i++) {
            energy *= ENERGY_MULTIPLIER;
        }
        return Mth.floor(energy);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        if (Screen.hasControlDown()) {
            tooltip.add(Component.translatable("tooltip.assembly_expansion.upgrades.speed", "\n"));
        } else {
            tooltip.add(Component.translatable("tooltip.assembly_expansion.universal.ctrl"));
        }
    }
}
