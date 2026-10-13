package kirsan122.assemblyexpansion.common.item;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ShiftTooltipItem extends Item {
    private final String tooltip;
    private final String shiftTooltip;

    public ShiftTooltipItem(String tooltip, String shiftTooltip, Properties properties) {
        super(properties);
        this.tooltip = tooltip;
        this.shiftTooltip = shiftTooltip;
    }

    public ShiftTooltipItem(String tooltip, Properties properties) {
        this(tooltip, tooltip, properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        if (Screen.hasShiftDown()) {
            tooltip.add(Component.translatable(this.shiftTooltip));
        } else {
            tooltip.add(Component.translatable(this.tooltip));
        }
    }
}
