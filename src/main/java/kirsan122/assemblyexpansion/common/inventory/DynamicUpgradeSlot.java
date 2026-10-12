package kirsan122.assemblyexpansion.common.inventory;

import kirsan122.assemblyexpansion.common.util.AETags;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;


import net.minecraft.world.item.ItemStack;

import java.util.function.BooleanSupplier;

public class DynamicUpgradeSlot extends SlotItemHandler {
    private final BooleanSupplier isTabOpen;

    public DynamicUpgradeSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition, BooleanSupplier isTabOpen) {
        super(itemHandler, index, xPosition, yPosition);
        this.isTabOpen = isTabOpen;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {

        return stack.is(AETags.Items.ALL_UPGRADES);
    }
    @Override
    public boolean isActive() {


        return this.isTabOpen.getAsBoolean();
    }

}

