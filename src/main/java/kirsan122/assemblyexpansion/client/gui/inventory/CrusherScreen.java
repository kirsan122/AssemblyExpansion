package kirsan122.assemblyexpansion.client.gui.inventory;

import kirsan122.assemblyexpansion.AssemblyExpansion;
import kirsan122.assemblyexpansion.client.gui.GuiData;
import kirsan122.assemblyexpansion.common.inventory.CrusherMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class CrusherScreen extends UniversalScreen<CrusherMenu> implements IProgressGetter {
    private static final ResourceLocation TEXTURE = new ResourceLocation(AssemblyExpansion.MOD_ID, "textures/gui/crusher_gui.png");

    public CrusherScreen(CrusherMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        super.renderBg(graphics, partialTick, mouseX, mouseY);
        int x = this.leftPos;
        int y = this.topPos;

        graphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);

        if (menu.isCrafting()) {
            graphics.blit(TEXTURE, x + 79, y + 35, 176, 14, menu.getScaledProgress(), 16);
        }

        int scaledEnergy = menu.getScaledEnergy();
        graphics.blit(GuiData.ENERGY_LINE, x + 162, y + 18 + (50 - scaledEnergy), 0, (50 - scaledEnergy), 5, scaledEnergy, 5, 50);
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        super.render(graphics, mouseX, mouseY, delta);

        GuiData.renderEnergyTooltip(graphics, this.font, this.menu, this.leftPos, this.topPos, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(this.font, this.title, (this.imageWidth / 2) - (this.font.width(this.title) / 2), this.titleLabelY, 4210752, false);
        graphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 4210752, false);
    }
}
