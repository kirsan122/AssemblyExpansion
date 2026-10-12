package kirsan122.assemblyexpansion.client.gui.inventory;

import kirsan122.assemblyexpansion.AssemblyExpansion;
import kirsan122.assemblyexpansion.client.gui.GuiData;
import kirsan122.assemblyexpansion.common.inventory.DistillationColumnMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class DistillationColumnScreen extends AbstractContainerScreen<DistillationColumnMenu> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(AssemblyExpansion.MOD_ID, "textures/gui/distillation_column_gui.png");

    public DistillationColumnScreen(DistillationColumnMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        com.mojang.blaze3d.systems.RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        int x = this.leftPos;
        int y = this.topPos;


        graphics.blit(TEXTURE, x, y, 0, 0, this.imageWidth, this.imageHeight);


        if (menu.isCrafting()) {
            graphics.blit(TEXTURE, x + 46, y + 38, 176, 0, menu.getScaledProgress(), 16);
        }


        int scaledEnergy = menu.getScaledEnergy(50);
        graphics.blit(GuiData.ENERGY_LINE, x + 164, y + 18 + (50 - scaledEnergy), 0, (50 - scaledEnergy), 5, scaledEnergy, 5, 50);



        GuiData.renderFluidInTank(graphics, this.menu, 0, x + 26,  y + 25, 16, 37);
        GuiData.renderFluidInTank(graphics, this.menu, 1, x + 76,  y + 25, 16, 37);
        GuiData.renderFluidInTank(graphics, this.menu, 2, x + 98,  y + 25, 16, 37);
        GuiData.renderFluidInTank(graphics, this.menu, 3, x + 120, y + 25, 16, 37);
        GuiData.renderFluidInTank(graphics, this.menu, 4, x + 142, y + 25, 16, 37);


        graphics.blit(GuiData.DISTILLATION_DIVISIONS, x + 26, y + 25, 0, 0, 16, 37, 16, 37);
        graphics.blit(GuiData.DISTILLATION_DIVISIONS,  x + 76,  y + 25, 0, 0, 16, 37, 16, 37);
        graphics.blit(GuiData.DISTILLATION_DIVISIONS,  x + 98,  y + 25, 0, 0, 16, 37, 16, 37);
        graphics.blit(GuiData.DISTILLATION_DIVISIONS,  x + 120,  y + 25, 0, 0, 16, 37, 16, 37);
        graphics.blit(GuiData.DISTILLATION_DIVISIONS,  x + 142,  y + 25, 0, 0, 16, 37, 16, 37);
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {

        this.renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
        this.renderTooltip(graphics, mouseX, mouseY);

        int x = this.leftPos;
        int y = this.topPos;



        int[][] tanksCoords = {
                {x + 25,  y + 24, 18, 39},
                {x + 75,  y + 24, 18, 39},
                {x + 97,  y + 24, 18, 39},
                {x + 119, y + 24, 18, 39},
                {x + 141, y + 24, 18, 39}
        };


        for (int i = 0; i < 5; i++) {
            int tX = tanksCoords[i][0];
            int tY = tanksCoords[i][1];
            int tW = tanksCoords[i][2];
            int tH = tanksCoords[i][3];

            if (mouseX >= tX && mouseX < tX + tW && mouseY >= tY && mouseY < tY + tH) {
                var handler = this.menu.getBlockEntity().getTankCapability(i);

                handler.ifPresent(tank -> {
                    net.minecraftforge.fluids.FluidStack fluidStack = tank.getFluidInTank(0);
                    java.util.List<Component> tooltip = new java.util.ArrayList<>();

                    if (fluidStack.isEmpty()) {
                        tooltip.add(Component.translatable("tooltip.assembly_expansion.empty"));
                    } else {

                        tooltip.add(Component.translatable(fluidStack.getFluid().getFluidType().getDescriptionId()));

                        tooltip.add(Component.literal(fluidStack.getAmount() + " / " + tank.getTankCapacity(0) + " mB")
                                .withStyle(net.minecraft.ChatFormatting.GRAY));
                    }


                    graphics.renderComponentTooltip(this.font, tooltip, mouseX, mouseY);
                });
            }
        }
        if (mouseX >= x + 163 && mouseX <= x + 169 && mouseY >= y + 17 && mouseY <= y + 68) {
            String energyText = menu.getEnergy() + " / " + menu.getMaxEnergy() + " FE";
            graphics.renderComponentTooltip(font, java.util.List.of(Component.literal(energyText)), mouseX, mouseY);
        }
    }


    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(this.font, this.title, (this.imageWidth / 2) - (this.font.width(this.title) / 2), this.titleLabelY, 4210752, false);
        graphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 4210752, false);
    }
}
