package kirsan122.assemblyexpansion.client.gui;

import kirsan122.assemblyexpansion.AssemblyExpansion;
import kirsan122.assemblyexpansion.api.GuiConfigHelper;
import kirsan122.assemblyexpansion.api.IUniversalMenu;
import kirsan122.assemblyexpansion.client.gui.inventory.IProgressGetter;
import kirsan122.assemblyexpansion.common.inventory.IFluidScreenRender;
import kirsan122.assemblyexpansion.common.inventory.UniversalMenu;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Global texture and positioning constants for the side and upgrade panels.
 */
public final class GuiData {
    private GuiData() {
    }

    /**
     * Textures used by other classes
     */
    public static final ResourceLocation CONFIG_BUTTON_TEXTURE = new ResourceLocation(AssemblyExpansion.MOD_ID, "textures/gui/elements/buttons_and_panels/gui_config_button.png");
    public static final ResourceLocation CONFIG_PANEL_TEXTURE = new ResourceLocation(AssemblyExpansion.MOD_ID, "textures/gui/elements/buttons_and_panels/gui_config_panel.png");

    public static final ResourceLocation REDSTONE_MODES_TEXTURE = new ResourceLocation(AssemblyExpansion.MOD_ID, "textures/gui/elements/buttons_and_panels/gui_redstone_modes.png");

    public static final ResourceLocation UPGRADE_BUTTON_TEXTURE = new ResourceLocation(AssemblyExpansion.MOD_ID, "textures/gui/elements/buttons_and_panels/gui_upgrade_button.png");
    public static final ResourceLocation UPGRADE_PANEL_TEXTURE = new ResourceLocation(AssemblyExpansion.MOD_ID, "textures/gui/elements/buttons_and_panels/gui_upgrade_panel.png");

    public static final ResourceLocation ENERGY_LINE = new ResourceLocation(AssemblyExpansion.MOD_ID, "textures/gui/elements/energy_line.png");
    public static final ResourceLocation FLUID_TANK_DIVISIONS = new ResourceLocation(AssemblyExpansion.MOD_ID, "textures/gui/elements/fluid_tank_divisions.png");
    public static final ResourceLocation DISTILLATION_DIVISIONS = new ResourceLocation(AssemblyExpansion.MOD_ID, "textures/gui/elements/distillation_divisions.png");
    public static final ResourceLocation ELECTROLYZER_DIVISIONS = new ResourceLocation(AssemblyExpansion.MOD_ID, "textures/gui/elements/electrolyzer_divisions.png");
    /**
     * Special variables controlling the width, height, and dimensions of buttons and configuration and upgrade panels.
     */
    public static final int PANELS_OFFSET = 4;
    public static final int UPGRADE_AND_CONFIG_BUTTONS_OFFSET = 88;
    public static final int UPGRADE_AND_CONFIG_PANELS_WH = 82;
    public static final int ALL_CONFIG_BUTTONS_W = 25;
    public static final int ALL_CONFIG_BUTTONS_H = 26;
    /**
     * Button mapping coordinates (18x18 pixels) relative to the config panel corner.
     * Ordered exactly by Minecraft's Direction.values(): DOWN, UP, NORTH, SOUTH, WEST, EAST.
     */
    public static final int[][] BUTTON_OFFSETS = {
            {32, 51},
            {32, 13},
            {32, 32},
            {13, 51},
            {51, 32},
            {13, 32}

    };
    /**
     * Names of the parties.
     */
    public static final String[] SIDE_NAMES = {
            "tooltip.assembly_expansion.side.down",
            "tooltip.assembly_expansion.side.up",
            "tooltip.assembly_expansion.side.front",
            "tooltip.assembly_expansion.side.behind",
            "tooltip.assembly_expansion.side.right",
            "tooltip.assembly_expansion.side.left"
    };

    /**
     * This excellent method renders the slide-out upgrades panel, avoiding code duplication.
     */
    public static void renderUpgradePanel(@NotNull GuiGraphics graphics, Font font, AbstractContainerMenu menu, int x, int y, float tabAnimationProgress, int upgradeSlotStart) {
        int currentOffsetX = (int) (UPGRADE_AND_CONFIG_PANELS_WH * tabAnimationProgress);

        if (tabAnimationProgress > 0.0f) {
            int panelX = x - currentOffsetX;
            graphics.blit(UPGRADE_PANEL_TEXTURE,
                    panelX, y + PANELS_OFFSET, 0, 0, UPGRADE_AND_CONFIG_PANELS_WH, GuiData.UPGRADE_AND_CONFIG_PANELS_WH, GuiData.UPGRADE_AND_CONFIG_PANELS_WH, GuiData.UPGRADE_AND_CONFIG_PANELS_WH);
        }

        if (tabAnimationProgress > 0.0f && tabAnimationProgress < 1.0f) {
            int itemRenderOffset = UPGRADE_AND_CONFIG_PANELS_WH - currentOffsetX;
            int scissorX = x - currentOffsetX;
            int scissorY = y + PANELS_OFFSET;

            graphics.enableScissor(scissorX, scissorY, scissorX + currentOffsetX, scissorY + UPGRADE_AND_CONFIG_PANELS_WH);
            graphics.pose().pushPose();
            graphics.pose().translate(itemRenderOffset, 0, 0);

            for (int i = upgradeSlotStart; i < upgradeSlotStart + 4; i++) {
                net.minecraft.world.inventory.Slot slot = menu.getSlot(i);
                if (slot.hasItem()) {
                    graphics.renderItem(slot.getItem(), x + slot.x, y + slot.y);
                    graphics.renderItemDecorations(font, slot.getItem(), x + slot.x, y + slot.y);
                }
            }

            graphics.pose().popPose();
            graphics.disableScissor();
        }
    }

    /**
     * And this excellent method renders the slide-out config panel, avoiding code duplication.
     */
    public static void renderConfigPanel(@NotNull GuiGraphics graphics, IUniversalMenu menu, int x, int y, int imageW, float configAnimationProgress) {

        int currentConfigX = (int) (82 * configAnimationProgress);

        int panelX = x + imageW;
        int panelY = y + GuiData.PANELS_OFFSET;

        if (configAnimationProgress > 0.0f) {
            graphics.enableScissor(panelX, panelY, panelX + currentConfigX, panelY + 82);

            int animatedPanelX = panelX - (82 - currentConfigX);
            graphics.blit(CONFIG_PANEL_TEXTURE, animatedPanelX, panelY, 0, 0, 82, 82, 82, 82);

            ResourceLocation configModesTexture = new ResourceLocation(AssemblyExpansion.MOD_ID, "textures/gui/elements/buttons_and_panels/gui_config_mode.png");

            for (int i = 0; i < 6; i++) {
                int btnRelX = GuiData.BUTTON_OFFSETS[i][0];
                int btnRelY = GuiData.BUTTON_OFFSETS[i][1];

                int sideMode = menu.getSideMode(i);

                if (sideMode > 0) {
                    int arrowX = animatedPanelX + btnRelX;
                    int arrowY = panelY + btnRelY;
                    int v = (sideMode == 1) ? 0 : 18;

                    graphics.blit(configModesTexture, arrowX, arrowY, 0, v, 18, 18, 18, 36);
                }
            }
            graphics.disableScissor();
        }
    }

    /**
     * This method renders energy tooltip in all devices of this mod, energy tooltip is located in
     * the same place, except for the generator and some machines.
     */
    public static void renderEnergyTooltip(@NotNull GuiGraphics graphics, Font font, UniversalMenu<?> menu, int x, int y, int mouseX, int mouseY) {
        renderEnergyTooltip(graphics, 161, 167, 17, 68, font, menu, x, y, mouseX, mouseY);
    }

    public static void renderEnergyTooltip(@NotNull GuiGraphics graphics, int startX, int endX, int startY, int endY, Font font, UniversalMenu<?> menu, int x, int y, int mouseX, int mouseY) {
        if (mouseX >= x + startX && mouseX <= x + endX && mouseY >= y + startY && mouseY <= y + endY) {
            String energyText = menu.getEnergy() + " / " + menu.getMaxEnergy() + " FE";
            graphics.renderComponentTooltip(font, java.util.List.of(Component.literal(energyText)), mouseX, mouseY);
        }
    }
    /**
     * This method renders tooltips on config and upgrade buttons
     */
    public static void renderButtonsTooltips(@NotNull GuiGraphics graphics, GuiConfigHelper.Holder configOpenHolder, Font font, int leftPos, int topPos, int mouseX, int mouseY, boolean isUpgradeTabOpen) {
        int configBtnMinX = leftPos + 176;
        int configBtnMaxX = configBtnMinX + 25;
        int btnMinY = topPos + GuiData.UPGRADE_AND_CONFIG_BUTTONS_OFFSET;
        int configBtnMaxY = btnMinY + 26;

        int btnMinX = leftPos - 25;

        int btnMaxY = topPos + GuiData.UPGRADE_AND_CONFIG_BUTTONS_OFFSET + 26;

        if (mouseX >= configBtnMinX && mouseX <= configBtnMaxX && mouseY >= btnMinY && mouseY <= configBtnMaxY) {
            Component tooltipText = configOpenHolder.value
                    ? Component.translatable("tooltip.assembly_expansion.close_config_tab")
                    : Component.translatable("tooltip.assembly_expansion.open_config_tab");
            graphics.renderComponentTooltip(font, java.util.List.of(tooltipText), mouseX, mouseY);
        }
        if (mouseX >= btnMinX && mouseX <= leftPos && mouseY >= btnMinY && mouseY <= btnMaxY) {
            Component tooltipText = isUpgradeTabOpen
                    ? Component.translatable("tooltip.assembly_expansion.close_upgrade_tab")
                    : Component.translatable("tooltip.assembly_expansion.open_upgrade_tab");


            graphics.renderComponentTooltip(font, java.util.List.of(tooltipText), mouseX, mouseY);
        }
    }

    public static void layoutPlayerInventory(Inventory playerInventory, int x, int y, Consumer<Slot> menu) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                menu.accept(new Slot(playerInventory, j + i * 9 + 9, x + j * 18, y + i * 18));
            }
        }
        for (int i = 0; i < 9; i++) {
            menu.accept(new Slot(playerInventory, i, x + i * 18, y + 58));
        }
    }

    public static <T extends AbstractContainerMenu & IFluidScreenRender<?>> void renderFluidInTank(GuiGraphics guiGraphics, T menu, int tankIndex, int tankX, int tankY, int tankWidth, int tankMaxHeight) {




        BlockEntity baseBE = menu.getBlockEntity();
        if (baseBE == null) return;

        net.minecraftforge.common.util.LazyOptional<net.minecraftforge.fluids.capability.IFluidHandler> cap = net.minecraftforge.common.util.LazyOptional.empty();

        try {
            java.lang.reflect.Method method = baseBE.getClass().getMethod("getTankCapability", int.class);
            cap = (net.minecraftforge.common.util.LazyOptional<net.minecraftforge.fluids.capability.IFluidHandler>) method.invoke(baseBE, tankIndex);
        } catch (Exception e) {
            cap = baseBE.getCapability(net.minecraftforge.common.capabilities.ForgeCapabilities.FLUID_HANDLER, null);
        }

        cap.ifPresent(handler -> {
            int internalIndex = handler.getTanks() == 1 ? 0 : tankIndex;
            if (internalIndex >= handler.getTanks()) return;

            net.minecraftforge.fluids.FluidStack fluidStack = handler.getFluidInTank(internalIndex);
            int amount = fluidStack.getAmount();
            int capacity = handler.getTankCapacity(internalIndex);

            if (!fluidStack.isEmpty() && amount > 0) {
                int fluidHeight = (int) ((float) amount / capacity * tankMaxHeight);
                if (fluidHeight > tankMaxHeight) fluidHeight = tankMaxHeight;
                if (fluidHeight <= 0) return;

                net.minecraft.world.level.material.Fluid fluid = fluidStack.getFluid();
                net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions extensions = net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions.of(fluid);
                ResourceLocation fluidTexture = extensions.getStillTexture(fluidStack);

                net.minecraft.client.renderer.texture.TextureAtlasSprite sprite = net.minecraft.client.Minecraft.getInstance()
                        .getTextureAtlas(net.minecraft.world.inventory.InventoryMenu.BLOCK_ATLAS)
                        .apply(fluidTexture != null ? fluidTexture : new ResourceLocation("block/water_still"));

                int color = extensions.getTintColor(fluidStack);
                float r = ((color >> 16) & 0xFF) / 255.0F;
                float g = ((color >> 8) & 0xFF) / 255.0F;
                float b = (color & 0xFF) / 255.0F;
                float a = ((color >> 24) & 0xFF) / 255.0F;


                guiGraphics.flush();


                com.mojang.blaze3d.systems.RenderSystem.setShader(net.minecraft.client.renderer.GameRenderer::getPositionTexShader);
                com.mojang.blaze3d.systems.RenderSystem.setShaderTexture(0, net.minecraft.world.inventory.InventoryMenu.BLOCK_ATLAS);
                com.mojang.blaze3d.systems.RenderSystem.setShaderColor(r, g, b, a);

                com.mojang.blaze3d.systems.RenderSystem.enableBlend();
                com.mojang.blaze3d.systems.RenderSystem.defaultBlendFunc();

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

                guiGraphics.flush();
            }
        });
    }
}
