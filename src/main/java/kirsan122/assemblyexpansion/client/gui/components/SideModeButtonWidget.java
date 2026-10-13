package kirsan122.assemblyexpansion.client.gui.components;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

public class SideModeButtonWidget extends AbstractWidget {

    private final Consumer<SideModeButtonWidget> onPress;
    private final int buttonIndex;

    public SideModeButtonWidget(int x, int y, int width, int height, int buttonIndex, Consumer<SideModeButtonWidget> onPress) {
        super(x, y, width, height, Component.empty());
        this.buttonIndex = buttonIndex;
        this.onPress = onPress;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        this.onPress.accept(this);
    }

    public int getButtonIndex() {
        return this.buttonIndex;
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationOutput) {
    }
}
