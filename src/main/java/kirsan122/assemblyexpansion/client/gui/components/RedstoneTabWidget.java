package kirsan122.assemblyexpansion.client.gui.components;

import kirsan122.assemblyexpansion.client.gui.GuiData;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

public class RedstoneTabWidget extends AbstractWidget {

    public RedstoneTabWidget(int x, int y) {
        super(x, y, 26, 26, Component.empty());
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.blit(GuiData.REDSTONE_MODES_TEXTURE, this.getX(), this.getY(), 0, 0, 25, 26, 79, 26);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
    }
}

