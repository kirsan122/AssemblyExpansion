package kirsan122.assemblyexpansion.client.gui.inventory;

import kirsan122.assemblyexpansion.AssemblyExpansion;
import kirsan122.assemblyexpansion.client.gui.GuiData;
import kirsan122.assemblyexpansion.common.inventory.GeneratorMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class GeneratorScreen extends AbstractContainerScreen<GeneratorMenu> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(AssemblyExpansion.MOD_ID, "textures/gui/generator_gui.png");

    public GeneratorScreen(GeneratorMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int x = this.leftPos;
        int y = this.topPos;


        graphics.blit(TEXTURE, x, y, 0, 0, 176, 166);




        int h = menu.getEnergyScaled();

        graphics.blit(GuiData.ENERGY_LINE, x + 147, y + 18 + (50 - h), 0, 50 - h, 5, h, 5, 50);


        if (menu.isBurning()) {
            int k = menu.getBurnScaled();

            if (k > 0) {

                graphics.blit(TEXTURE, x + 56, y + 36 + (14 - k), 176, 14 - k, 14, k);
            }
        }
    }



    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, delta);
        renderTooltip(graphics, mouseX, mouseY);


        int relX = mouseX - leftPos;
        int relY = mouseY - topPos;
        if (relX >= 146 && relX <= 152 && relY >= 17 && relY <= 68) {
            graphics.renderTooltip(this.font, Component.literal(menu.getEnergy() + " / 5000 FE"), mouseX, mouseY);
        }

    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(this.font, this.title, (this.imageWidth / 2) - (this.font.width(this.title) / 2), this.titleLabelY, 4210752, false);
        graphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 4210752, false);
    }
}

