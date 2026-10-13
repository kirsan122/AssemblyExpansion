package kirsan122.assemblyexpansion.client.gui.inventory;

import kirsan122.assemblyexpansion.AssemblyExpansion;
import kirsan122.assemblyexpansion.client.gui.GuiData;
import kirsan122.assemblyexpansion.common.blockEntity.FluidTankBlockEntity;
import kirsan122.assemblyexpansion.common.inventory.FluidTankMenu;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;

public class FluidTankScreen extends AbstractContainerScreen<FluidTankMenu> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(AssemblyExpansion.MOD_ID, "textures/gui/fluid_tank_gui.png");

    public FluidTankScreen(FluidTankMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
        renderFluidTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        int x = this.leftPos;
        int y = this.topPos;


        guiGraphics.blit(TEXTURE, x, y, 0, 0, this.imageWidth, this.imageHeight);


        FluidTankBlockEntity tankBE = this.menu.getBlockEntity();
        FluidStack fluidStack = tankBE.getFluidTank().getFluid();
        int amount = tankBE.getFluidTank().getFluidAmount();
        int capacity = tankBE.getFluidTank().getCapacity();


        int tankX = x + 62;
        int tankY = y + 19;
        int tankWidth = 52;
        int tankMaxHeight = 47;


        if (!fluidStack.isEmpty() && amount > 0) {
            int fluidHeight = (int) ((float) amount / capacity * tankMaxHeight);
            if (fluidHeight > tankMaxHeight) fluidHeight = tankMaxHeight;

            Fluid fluid = fluidStack.getFluid();
            IClientFluidTypeExtensions extensions = IClientFluidTypeExtensions.of(fluid);
            ResourceLocation fluidTexture = extensions.getStillTexture(fluidStack);

            TextureAtlasSprite sprite = Minecraft.getInstance()
                    .getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
                    .apply(fluidTexture != null ? fluidTexture : new ResourceLocation("block/water_still"));

            int color = extensions.getTintColor(fluidStack);
            float r = ((color >> 16) & 0xFF) / 255.0F;
            float g = ((color >> 8) & 0xFF) / 255.0F;
            float b = (color & 0xFF) / 255.0F;
            float a = ((color >> 24) & 0xFF) / 255.0F;

            RenderSystem.setShaderColor(r, g, b, a);
            RenderSystem.enableBlend();


            int drawY = tankY + (tankMaxHeight - fluidHeight);


            guiGraphics.enableScissor(tankX, drawY, tankX + tankWidth, tankY + tankMaxHeight);



            for (int currentX = tankX; currentX < tankX + tankWidth; currentX += 16) {
                for (int currentY = drawY; currentY < tankY + tankMaxHeight; currentY += 16) {



                    guiGraphics.blit(currentX, currentY, 0, 16, 16, sprite);
                }
            }


            guiGraphics.disableScissor();



            RenderSystem.disableBlend();
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        }



        guiGraphics.blit(GuiData.FLUID_TANK_DIVISIONS, tankX, tankY, 0, 0, tankWidth, tankMaxHeight, 52, 47);
    }

    private void renderFluidTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        int x = this.leftPos;
        int y = this.topPos;

        if (mouseX >= x + 61 && mouseX < x + 114 && mouseY >= y + 18 && mouseY < y + 66) {
            FluidTankBlockEntity tankBE = this.menu.getBlockEntity();
            FluidStack fluidStack = tankBE.getFluidTank().getFluid();

            Component text;
            if (fluidStack.isEmpty() || tankBE.getFluidTank().getFluidAmount() <= 0) {
                text = Component.translatable("tooltip.assembly_expansion.empty");
            } else {
                String fluidName = fluidStack.getDisplayName().getString();
                text = Component.literal(fluidName + ": " + tankBE.getFluidTank().getFluidAmount() + " / " + tankBE.getFluidTank().getCapacity() + " mB");
            }

            guiGraphics.renderTooltip(this.font, text, mouseX, mouseY);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(this.font, this.title, (this.imageWidth / 2) - (this.font.width(this.title) / 2), this.titleLabelY, 4210752, false);
        graphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 4210752, false);
    }
}
