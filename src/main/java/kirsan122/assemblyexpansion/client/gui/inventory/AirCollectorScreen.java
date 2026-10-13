package kirsan122.assemblyexpansion.client.gui.inventory;

import kirsan122.assemblyexpansion.AssemblyExpansion;
import kirsan122.assemblyexpansion.client.gui.GuiData;
import kirsan122.assemblyexpansion.common.inventory.AirCollectorMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class AirCollectorScreen extends UniversalScreen<AirCollectorMenu> implements IProgressGetter {

    private static final ResourceLocation TEXTURE = new ResourceLocation(AssemblyExpansion.MOD_ID, "textures/gui/air_collector_gui.png");

    public AirCollectorScreen(AirCollectorMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        super.renderBg(graphics, partialTicks, mouseX, mouseY);
        int x = this.leftPos;
        int y = this.topPos;

        graphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);

        GuiData.renderFluidInTank(graphics, this.menu, 0, x + 80, y + 17, 16, 52);

        graphics.blit(GuiData.ELECTROLYZER_DIVISIONS, x + 80, y + 21, 0, 0, 16, 45, 16, 45);

        int scaledEnergy = menu.getScaledEnergy();
        graphics.blit(GuiData.ENERGY_LINE, x + 162, y + 18 + (50 - scaledEnergy), 0, (50 - scaledEnergy), 5, scaledEnergy, 5, 50);
    }


    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        super.render(graphics, mouseX, mouseY, delta);

        GuiData.renderEnergyTooltip(graphics, this.font, this.menu, this.leftPos, this.topPos, mouseX, mouseY);

        int x = this.leftPos;
        int y = this.topPos;

        if (mouseX >= x + 79 && mouseX < x + 96 && mouseY >= y + 16 && mouseY < y + 69) {
            var handler = this.menu.getBlockEntity().getTankCapability();

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

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(this.font, this.title, (this.imageWidth / 2) - (this.font.width(this.title) / 2), this.titleLabelY, 4210752, false);
        graphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 4210752, false);
    }
}
