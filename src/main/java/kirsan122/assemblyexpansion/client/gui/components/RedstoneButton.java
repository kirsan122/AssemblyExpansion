package kirsan122.assemblyexpansion.client.gui.components;

import kirsan122.assemblyexpansion.api.RedstoneModes;
import kirsan122.assemblyexpansion.client.gui.GuiData;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

import java.util.function.Supplier;

public class RedstoneButton extends Button {

    private final Supplier<RedstoneModes> modeSupplier;

    public RedstoneButton(int x, int y, Supplier<RedstoneModes> modeSupplier, OnPress onPress) {
        super(x, y, 16, 16, Component.empty(), onPress, DEFAULT_NARRATION);
        this.modeSupplier = modeSupplier;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        RedstoneModes currentMode = modeSupplier.get();
        RenderSystem.setShaderTexture(0, GuiData.REDSTONE_MODES_TEXTURE);
        guiGraphics.blit(GuiData.REDSTONE_MODES_TEXTURE, this.getX(), this.getY(), currentMode.getTextureX(), 0, 18, 18, 79, 26);
    }

    public Component getTooltipText() {
        return this.modeSupplier.get().getTooltip();
    }
}