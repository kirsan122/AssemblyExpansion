package kirsan122.assemblyexpansion.client.gui.inventory;

import kirsan122.assemblyexpansion.AssemblyExpansion;
import kirsan122.assemblyexpansion.client.gui.GuiData;
import kirsan122.assemblyexpansion.common.inventory.FluidCoolerMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class FluidCoolerScreen extends UniversalScreen<FluidCoolerMenu> implements IProgressGetter{

    private static final ResourceLocation TEXTURE = new ResourceLocation(AssemblyExpansion.MOD_ID, "textures/gui/fluid_cooler_gui.png");

    public FluidCoolerScreen(FluidCoolerMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageWidth = 190;
        this.imageHeight = 166;
        this.inventoryLabelX = 14;
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        super.renderBg(graphics, partialTick, mouseX, mouseY);
        com.mojang.blaze3d.systems.RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        int x = this.leftPos;
        int y = this.topPos;


        graphics.blit(TEXTURE, x, y, 0, 0, this.imageWidth, this.imageHeight, 256, 256);

        if (menu.isCrafting()) {
            graphics.blit(TEXTURE, x + 75, y + 38, 190, 0, menu.getScaledProgress(), 20);
        }

        int scaledEnergy = menu.getScaledEnergy();
        graphics.blit(GuiData.ENERGY_LINE, x + 176, y + 18 + (50 - scaledEnergy), 0, (50 - scaledEnergy), 5, scaledEnergy, 5, 50);

        GuiData.renderFluidInTank(graphics, this.menu, 0, x + 53,  y + 18, 16, 52);
        GuiData.renderFluidInTank(graphics, this.menu, 1, x + 107,  y + 18, 16, 52);

        graphics.blit(GuiData.ELECTROLYZER_DIVISIONS, x + 53, y + 21, 0, 0, 16, 45, 16, 45);
        graphics.blit(GuiData.ELECTROLYZER_DIVISIONS,  x + 107,  y + 21, 0, 0, 16, 45, 16, 45);
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);

        int x = this.leftPos;
        int y = this.topPos;

        int[][] tanksCords = {
                {x + 52,  y + 17, 18, 54},
                {x + 106,  y + 17, 18, 54}
        };

        for (int i = 0; i < 2; i++) {
            int tX = tanksCords[i][0];
            int tY = tanksCords[i][1];
            int tW = tanksCords[i][2];
            int tH = tanksCords[i][3];

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
        this.renderTooltip(graphics, mouseX, mouseY);
        GuiData.renderEnergyTooltip(graphics, 175, 181, 17, 68, font, menu, x, y, mouseX, mouseY);
    }


    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(this.font, this.title, (this.imageWidth / 2) - (this.font.width(this.title) / 2), this.titleLabelY, 4210752, false);
        graphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 4210752, false);
    }
}
