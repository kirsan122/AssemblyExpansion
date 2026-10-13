package kirsan122.assemblyexpansion.client.gui.inventory;

import kirsan122.assemblyexpansion.api.GuiConfigHelper;
import kirsan122.assemblyexpansion.api.IUniversalMenu;
import kirsan122.assemblyexpansion.api.ConfigModes;
import kirsan122.assemblyexpansion.client.gui.GuiData;
import kirsan122.assemblyexpansion.client.gui.components.*;
import kirsan122.assemblyexpansion.network.AEPacketHandler;
import kirsan122.assemblyexpansion.network.packet.PacketUpdateConfig;
import kirsan122.assemblyexpansion.network.packet.PacketUpdateRedstone;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public abstract class UniversalScreen<T extends AbstractContainerMenu & IUniversalMenu> extends AbstractContainerScreen<T> {

    protected ConfigModes.ConfigType currentTab = ConfigModes.ConfigType.ITEM;
    protected UpgradeButtonWidget upgradeButton;
    private RedstoneButton redstoneButton;
    protected ConfigTabButtonWidget configTabButton;
    protected final List<SideModeButtonWidget> sideButtons = new ArrayList<>();

    protected boolean isUpgradeTabOpen = false;
    protected boolean isConfigTabOpen = false;

    protected float configAnimationProgress = 0.0f;
    protected float upgradeAnimationProgress = 0.0f;
    protected final float animationSpeed = 0.1f;

    public UniversalScreen(T menu, Inventory inv, Component title) {
        super(menu, inv, title);
    }

    @Override
    protected void init() {
        super.init();
        this.sideButtons.clear();

        int tabX = this.leftPos + this.imageWidth;
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


        this.configTabButton = new ConfigTabButtonWidget(
                this.leftPos + this.imageWidth,
                this.topPos + GuiData.UPGRADE_AND_CONFIG_BUTTONS_OFFSET,
                25, 26,
                () -> this.isConfigTabOpen,
                (btn) -> this.isConfigTabOpen = !this.isConfigTabOpen
        );
        this.addRenderableWidget(this.configTabButton);


        for (int i = 0; i < 6; i++) {
            final int index = i;
            SideModeButtonWidget sideBtn = new SideModeButtonWidget(
                    0, 0,
                    18, 18,
                    index,
                    (btn) -> {
                        if (this.menu.getBlockEntity() != null) {
                            Direction facing = this.menu.getBlockEntity().getBlockState().getValue(
                                    net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING
                            );
                            Direction absoluteSide = GuiConfigHelper.getAbsoluteSide(facing, index);
                            AEPacketHandler.sendToServer(new PacketUpdateConfig(this.menu.getBlockEntityPos(), this.currentTab, absoluteSide));
                        }
                    }
            );
            this.sideButtons.add(sideBtn);
            this.addRenderableWidget(sideBtn);
        }
    }

    @Override
    protected void containerTick() {
        super.containerTick();


        if (this.isUpgradeTabOpen && this.upgradeAnimationProgress < 1.0f) {
            this.upgradeAnimationProgress = Math.min(1.0f, this.upgradeAnimationProgress + animationSpeed);
        } else if (!this.isUpgradeTabOpen && this.upgradeAnimationProgress > 0.0f) {
            this.upgradeAnimationProgress = Math.max(0.0f, this.upgradeAnimationProgress - animationSpeed);
        }


        if (this.isConfigTabOpen && this.configAnimationProgress < 1.0f) {
            this.configAnimationProgress = Math.min(1.0f, this.configAnimationProgress + animationSpeed);
        } else if (!this.isConfigTabOpen && this.configAnimationProgress > 0.0f) {
            this.configAnimationProgress = Math.max(0.0f, this.configAnimationProgress - animationSpeed);
        }


        int currentPanelWidth = (int) (GuiData.UPGRADE_AND_CONFIG_PANELS_WH * this.configAnimationProgress);
        int currentLeftX = this.leftPos + this.imageWidth + (currentPanelWidth - GuiData.UPGRADE_AND_CONFIG_PANELS_WH);

        for (int i = 0; i < 6; i++) {
            SideModeButtonWidget btn = this.sideButtons.get(i);
            btn.setX(currentLeftX + GuiData.BUTTON_OFFSETS[i][0]);
            btn.setY(this.topPos + GuiData.BUTTON_OFFSETS[i][1] + 4);

            btn.visible = (this.configAnimationProgress >= 0.95f);
            btn.active = (this.configAnimationProgress >= 0.95f);
        }
        this.menu.setUpgradeTabFullyOpen(this.upgradeAnimationProgress >= 1.0f);

    }

    @Override
    protected void renderBg(@NotNull GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        GuiData.renderUpgradePanel(graphics, this.font, this.menu, this.leftPos, this.topPos, this.upgradeAnimationProgress, 4);
        GuiData.renderConfigPanel(graphics, this.menu, this.leftPos, this.topPos, this.imageWidth, this.configAnimationProgress);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        this.renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, delta);
        this.renderTooltip(graphics, mouseX, mouseY);
        if (this.redstoneButton != null && this.redstoneButton.isMouseOver(mouseX, mouseY)) {
            graphics.renderComponentTooltip(this.font, java.util.List.of(this.redstoneButton.getTooltipText()), mouseX, mouseY);
        }
        GuiConfigHelper.Holder configHolder = new GuiConfigHelper.Holder(this.isConfigTabOpen);
        GuiData.renderButtonsTooltips(graphics, configHolder, this.font, this.leftPos, this.topPos, mouseX, mouseY, this.isUpgradeTabOpen);
        GuiConfigHelper.renderSideModesTooltips(graphics, this, mouseX, mouseY, configAnimationProgress);
    }

    public float getUpgradeAnimationProgress() {
        return this.upgradeAnimationProgress;
    }

    public float getConfigAnimationProgress() {
        return this.configAnimationProgress;
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
}
