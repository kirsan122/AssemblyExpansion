package kirsan122.assemblyexpansion.client.gui.components;

import kirsan122.assemblyexpansion.client.gui.GuiData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

public class UpgradeButtonWidget extends AbstractWidget {

    private final BooleanSupplier stateSupplier;
    private final Consumer<UpgradeButtonWidget> onPress;

    public UpgradeButtonWidget(int x, int y, int width, int height, BooleanSupplier stateSupplier, Consumer<UpgradeButtonWidget> onPress) {
        super(x, y, width, height, Component.empty());
        this.stateSupplier = stateSupplier;
        this.onPress = onPress;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        int currentTextureY = this.stateSupplier.getAsBoolean() ? 26 : 0;

        guiGraphics.blit(
                GuiData.UPGRADE_BUTTON_TEXTURE,
                this.getX(), this.getY(),
                0, currentTextureY,
                this.width, this.height,
                25, 52
        );
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        this.onPress.accept(this);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationOutput) {
    }
}
