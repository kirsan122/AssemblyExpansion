package kirsan122.assemblyexpansion.common.item.upgrades;

import kirsan122.assemblyexpansion.api.IUpgradableBE;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class EnergyUpgradeItem extends Item {
    private final int additional_energy;

    public EnergyUpgradeItem(int additional_energy, Properties properties) {
        super(properties);
        this.additional_energy = additional_energy;
    }

    public int getAdditionalEnergy() {
        return this.additional_energy;
    }

    public static int getMaxEnergy(@NotNull IUpgradableBE tile) {
        int totalBonusEnergy = 0;
        ItemStackHandler upgradeSlots = tile.getUpgradeSlots();
        for (int i = 0; i < upgradeSlots.getSlots(); i++) {
            ItemStack stack = upgradeSlots.getStackInSlot(i);
            if (stack.getItem() instanceof EnergyUpgradeItem energyItem) {
                totalBonusEnergy += energyItem.getAdditionalEnergy() * stack.getCount();
            }
        }
        return tile.getMaxEnergy() + totalBonusEnergy;
    }
    public static int getMaxEnergy(int baseMaxEnergy, @Nullable CompoundTag upgradeSlotsTag) {
        int totalBonusEnergy = 0;


        if (upgradeSlotsTag != null && upgradeSlotsTag.contains("Items", net.minecraft.nbt.Tag.TAG_LIST)) {
            net.minecraft.nbt.ListTag itemsList = upgradeSlotsTag.getList("Items", net.minecraft.nbt.Tag.TAG_COMPOUND);

            for (int i = 0; i < itemsList.size(); i++) {
                CompoundTag itemTag = itemsList.getCompound(i);
                ItemStack stack = ItemStack.of(itemTag);

                if (stack.getItem() instanceof EnergyUpgradeItem energyItem) {
                    totalBonusEnergy += energyItem.getAdditionalEnergy() * stack.getCount();
                }
            }
        }
        return baseMaxEnergy + totalBonusEnergy;
    }
    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        if (Screen.hasControlDown()) {
            tooltip.add(Component.translatable("tooltip.assembly_expansion.upgrades.energy", Integer.toString(this.additional_energy)));
        } else {
            tooltip.add(Component.translatable("tooltip.assembly_expansion.universal.ctrl"));
        }
    }

}
