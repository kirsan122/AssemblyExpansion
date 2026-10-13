package kirsan122.assemblyexpansion.api;

import kirsan122.assemblyexpansion.client.gui.GuiData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;

@Deprecated(since = "0.0.4")
public final class GuiConfigHelper {
    private GuiConfigHelper() {}

    public static Direction getAbsoluteSide(Direction facing, int uiSideIndex) {
        Direction localSide = Direction.from3DDataValue(uiSideIndex);
        if (localSide.getAxis().isVertical() || facing == null) {
            return localSide;
        }


        return switch (facing) {
            case SOUTH -> localSide.getOpposite();
            case WEST -> localSide.getCounterClockWise();
            case EAST -> localSide.getClockWise();
            default -> localSide;
        };
    }

    public static Direction getRelativeSide(Direction facing, Direction worldSide) {
        if (worldSide == null || worldSide.getAxis().isVertical() || facing == null) {
            return worldSide;
        }


        return switch (facing) {
            case SOUTH -> worldSide.getOpposite();
            case WEST -> worldSide.getClockWise();
            case EAST -> worldSide.getCounterClockWise();
            default -> worldSide;
        };
    }

    public static void renderSideModesTooltips(GuiGraphics guiGraphics, AbstractContainerScreen<?> screen, int mouseX, int mouseY, float animationProgress) {
        if (animationProgress < 1.0f) return;
        if (!(screen.getMenu() instanceof IUniversalMenu configMenu)) return;

        int panelX = screen.getGuiLeft() + screen.getXSize();
        int panelY = screen.getGuiTop() + GuiData.PANELS_OFFSET;

        for (int i = 0; i < 6; i++) {
            int slotX = panelX + GuiData.BUTTON_OFFSETS[i][0];
            int slotY = panelY + GuiData.BUTTON_OFFSETS[i][1];

            if (mouseX >= slotX && mouseX < slotX + 18 && mouseY >= slotY && mouseY < slotY + 18) {
                int sideMode = configMenu.getSideMode(i);

                String modeText = "tooltip.assembly_expansion.sidemode.none";
                if (sideMode == 1) modeText = "tooltip.assembly_expansion.sidemode.input";
                if (sideMode == 2) modeText = "tooltip.assembly_expansion.sidemode.output";

                Component text = Component.translatable(GuiData.SIDE_NAMES[i])
                        .append(Component.literal(" : ")
                                .append(Component.translatable(modeText)));
                guiGraphics.renderTooltip(Minecraft.getInstance().font, text, mouseX, mouseY);
                break;
            }
        }
    }

    public static class Holder {
        public boolean value;
        public Holder(boolean value) { this.value = value; }
    }
}
