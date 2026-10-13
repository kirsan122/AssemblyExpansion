package kirsan122.assemblyexpansion.client.gui.inventory;

import kirsan122.assemblyexpansion.AssemblyExpansion;
import kirsan122.assemblyexpansion.client.gui.GuiData;
import kirsan122.assemblyexpansion.client.gui.components.RedstoneButton;
import kirsan122.assemblyexpansion.client.gui.components.RedstoneTabWidget;
import kirsan122.assemblyexpansion.client.gui.components.UpgradeButtonWidget;
import kirsan122.assemblyexpansion.common.inventory.ElectrolyzerMenu;
import kirsan122.assemblyexpansion.network.AEPacketHandler;
import kirsan122.assemblyexpansion.network.packet.PacketUpdateRedstone;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class ElectrolyzerScreen extends AbstractContainerScreen<ElectrolyzerMenu> {
    private RedstoneButton redstoneButton;
    private UpgradeButtonWidget upgradeButton;
    private boolean isUpgradeTabOpen = false;
    private float upgradeAnimationProgress = 0.0f;
    private final float animationSpeed = 0.1f;
    private static final ResourceLocation TEXTURE = new ResourceLocation(AssemblyExpansion.MOD_ID, "textures/gui/electrolyzer_gui.png");

    public ElectrolyzerScreen(ElectrolyzerMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
    }
    @Override
    protected void init() {
        super.init();

        int tabX = this.leftPos + 176;
        int tabY = this.topPos + 115;
        RedstoneTabWidget tab = new RedstoneTabWidget(tabX, tabY);
        this.addRenderableOnly(tab);

        this.redstoneButton = new RedstoneButton(tabX + 3, tabY + 4,
                this.menu::getRedstoneMode,
                (button) -> AEPacketHandler.sendToServer(new PacketUpdateRedstone(this.menu.getBlockEntityPos()))
        );
        this.addRenderableWidget(this.redstoneButton);

        this.upgradeButton = new UpgradeButtonWidget(
                this.leftPos - GuiData.ALL_CONFIG_BUTTONS_W,
                this.topPos + GuiData.UPGRADE_AND_CONFIG_BUTTONS_OFFSET,
                GuiData.ALL_CONFIG_BUTTONS_W, GuiData.ALL_CONFIG_BUTTONS_H,
                () -> this.isUpgradeTabOpen,
                (btn) -> {
                    this.isUpgradeTabOpen = !this.isUpgradeTabOpen;
                    if (this.minecraft.gameMode != null) {
                        this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, 0);
                    }
                }
        );
        this.addRenderableWidget(this.upgradeButton);
    }
    @Override
    protected void containerTick() {
        super.containerTick();

        if (this.isUpgradeTabOpen && this.upgradeAnimationProgress < 1.0f) {
            this.upgradeAnimationProgress = Math.min(1.0f, this.upgradeAnimationProgress + animationSpeed);
        } else if (!this.isUpgradeTabOpen && this.upgradeAnimationProgress > 0.0f) {
            this.upgradeAnimationProgress = Math.max(0.0f, this.upgradeAnimationProgress - animationSpeed);
        }

        this.menu.isUpgradeTabFullyOpen = (this.upgradeAnimationProgress >= 1.0f);
    }
    @Override
    protected boolean isHovering(int slotX, int slotY, int sizeX, int sizeY, double mouseX, double mouseY) {
        if (slotX < 0) {
            if (this.upgradeAnimationProgress < 0.95f) return false;

            int currentOffsetX = (int) (GuiData.UPGRADE_AND_CONFIG_PANELS_WH * this.upgradeAnimationProgress);
            int animatedX = slotX + (GuiData.UPGRADE_AND_CONFIG_PANELS_WH - currentOffsetX);

            return mouseX >= (this.leftPos + animatedX) && mouseX < (this.leftPos + animatedX + sizeX)
                    && mouseY >= (this.topPos + slotY) && mouseY < (this.topPos + slotY + sizeY);
        }
        return super.isHovering(slotX, slotY, sizeX, sizeY, mouseX, mouseY);
    }

    @Override
    protected void slotClicked(net.minecraft.world.inventory.Slot slot, int slotId, int mouseButton, net.minecraft.world.inventory.ClickType clickType) {
        if (clickType == net.minecraft.world.inventory.ClickType.QUICK_MOVE) {
            if (this.isUpgradeTabOpen && this.upgradeAnimationProgress < 0.95f) {
                return;
            }
        }
        super.slotClicked(slot, slotId, mouseButton, clickType);
    }

    public float getUpgradeAnimationProgress() {
        return this.upgradeAnimationProgress;
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        com.mojang.blaze3d.systems.RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        int x = this.leftPos;
        int y = this.topPos;
        GuiData.renderUpgradePanel(graphics, this.font, this.menu, x, y, this.upgradeAnimationProgress, 4);



        graphics.blit(TEXTURE, x, y, 0, 0, this.imageWidth, this.imageHeight);




        int scaledEnergy = menu.getScaledEnergy(50);
        graphics.blit(GuiData.ENERGY_LINE, x + 158, y + 10 + (50 - scaledEnergy), 0, (50 - scaledEnergy), 5, scaledEnergy, 5, 50);



        renderSingleTank(graphics, 0, x + 8,  y + 13, 16, 45);
        renderSingleTank(graphics, 1, x + 80,  y + 13, 16, 45);
        renderSingleTank(graphics, 2, x + 116,  y + 13, 16, 45);


        graphics.blit(GuiData.ELECTROLYZER_DIVISIONS, x + 8, y + 13, 0, 0, 16, 45, 16, 45);
        graphics.blit(GuiData.ELECTROLYZER_DIVISIONS,  x + 80,  y + 13, 0, 0, 16, 45, 16, 45);
        graphics.blit(GuiData.ELECTROLYZER_DIVISIONS,  x + 116,  y + 13, 0, 0, 16, 45, 16, 45);
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {

        this.renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
        this.renderTooltip(graphics, mouseX, mouseY);

        int x = this.leftPos;
        int y = this.topPos;
        if (this.redstoneButton != null && this.redstoneButton.isMouseOver(mouseX, mouseY)) {
            graphics.renderComponentTooltip(this.font, java.util.List.of(this.redstoneButton.getTooltipText()), mouseX, mouseY);
        }



        int[][] tanksCoords = {
                {x + 8,  y + 13, 18, 47},
                {x + 80,  y + 13, 18, 47},
                {x + 116,  y + 13, 18, 47}
        };


        for (int i = 0; i < 3; i++) {
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
        this.renderTooltip(graphics, mouseX, mouseY);
        if (mouseX >= x + 157 && mouseX <= x + 163 && mouseY >= y + 9 && mouseY <= y + 60) {
            String energyText = menu.getEnergy() + " / " + menu.getMaxEnergy() + " FE";
            graphics.renderComponentTooltip(font, java.util.List.of(Component.literal(energyText)), mouseX, mouseY);
        }
    }


    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(this.font, this.title, (this.imageWidth / 2) - (this.font.width(this.title) / 2), this.titleLabelY - 2, 4210752, false);
    }
    protected void renderSingleTank(GuiGraphics guiGraphics, int tankIndex, int tankX, int tankY, int tankWidth, int tankMaxHeight) {
        var baseBE = this.menu.getBlockEntity();

        baseBE.getTankCapability(tankIndex).ifPresent(handler -> {
            net.minecraftforge.fluids.FluidStack fluidStack = handler.getFluidInTank(0);
            int amount = fluidStack.getAmount();
            int capacity = handler.getTankCapacity(0);

            if (!fluidStack.isEmpty() && amount > 0) {
                int fluidHeight = (int) ((float) amount / capacity * tankMaxHeight);
                if (fluidHeight > tankMaxHeight) fluidHeight = tankMaxHeight;
                if (fluidHeight <= 0) return;

                net.minecraft.world.level.material.Fluid fluid = fluidStack.getFluid();
                net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions extensions =
                        net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions.of(fluid);
                ResourceLocation fluidTexture = extensions.getStillTexture(fluidStack);

                net.minecraft.client.renderer.texture.TextureAtlasSprite sprite = net.minecraft.client.Minecraft.getInstance()
                        .getTextureAtlas(net.minecraft.world.inventory.InventoryMenu.BLOCK_ATLAS)
                        .apply(fluidTexture != null ? fluidTexture : new ResourceLocation("block/water_still"));

                int color = extensions.getTintColor(fluidStack);
                float r = ((color >> 16) & 0xFF) / 255.0F;
                float g = ((color >> 8) & 0xFF) / 255.0F;
                float b = (color & 0xFF) / 255.0F;
                float a = ((color >> 24) & 0xFF) / 255.0F;


                com.mojang.blaze3d.systems.RenderSystem.setShader(net.minecraft.client.renderer.GameRenderer::getPositionTexShader);
                com.mojang.blaze3d.systems.RenderSystem.setShaderTexture(0, net.minecraft.world.inventory.InventoryMenu.BLOCK_ATLAS);
                com.mojang.blaze3d.systems.RenderSystem.setShaderColor(r, g, b, a);
                com.mojang.blaze3d.systems.RenderSystem.enableBlend();


                com.mojang.blaze3d.vertex.Tesselator tesselator = com.mojang.blaze3d.vertex.Tesselator.getInstance();
                com.mojang.blaze3d.vertex.BufferBuilder bufferbuilder = tesselator.getBuilder();


                bufferbuilder.begin(com.mojang.blaze3d.vertex.VertexFormat.Mode.QUADS, com.mojang.blaze3d.vertex.DefaultVertexFormat.POSITION_TEX);

                int renderStartY = tankY + tankMaxHeight;
                int renderTargetY = tankY + (tankMaxHeight - fluidHeight);

                for (int currentX = tankX; currentX < tankX + tankWidth; currentX += 16) {
                    int widthToDraw = Math.min(16, (tankX + tankWidth) - currentX);

                    for (int currentY = renderStartY; currentY > renderTargetY; currentY -= 16) {
                        int heightToDraw = Math.min(16, currentY - renderTargetY);
                        int drawAtY = currentY - heightToDraw;

                        float uMin = sprite.getU0();
                        float uMax = sprite.getU1();
                        float vMin = sprite.getV0();
                        float vMax = sprite.getV1();

                        if (widthToDraw < 16) {
                            uMax = uMin + (uMax - uMin) * ((float) widthToDraw / 16.0F);
                        }

                        if (heightToDraw < 16) {
                            int pixelsCutFromTop = 16 - heightToDraw;
                            vMin = vMin + (vMax - vMin) * ((float) pixelsCutFromTop / 16.0F);
                        }


                        org.joml.Matrix4f matrix = guiGraphics.pose().last().pose();


                        bufferbuilder.vertex(matrix, currentX, drawAtY + heightToDraw, 0).uv(uMin, vMax).endVertex();
                        bufferbuilder.vertex(matrix, currentX + widthToDraw, drawAtY + heightToDraw, 0).uv(uMax, vMax).endVertex();
                        bufferbuilder.vertex(matrix, currentX + widthToDraw, drawAtY, 0).uv(uMax, vMin).endVertex();
                        bufferbuilder.vertex(matrix, currentX, drawAtY, 0).uv(uMin, vMin).endVertex();
                    }
                }


                tesselator.end();

                com.mojang.blaze3d.systems.RenderSystem.disableBlend();
                com.mojang.blaze3d.systems.RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            }
        });
    }
}
