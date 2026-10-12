package kirsan122.assemblyexpansion.common.inventory;

import kirsan122.assemblyexpansion.api.IUniversalMenu;
import kirsan122.assemblyexpansion.api.RedstoneModes;
import kirsan122.assemblyexpansion.common.blockEntity.machines.ConfigurableBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public abstract class UniversalMenu<T extends ConfigurableBlockEntity> extends AbstractContainerMenu implements IUniversalMenu {

    protected final T blockEntity;
    protected final ContainerData data;
    protected final BlockPos blockPos;
    public boolean isUpgradeTabFullyOpen = false;

    public UniversalMenu(@Nullable MenuType<?> type, int containerId, T entity, ContainerData data) {
        super(type, containerId);
        this.blockEntity = entity;
        this.data = data;
        this.blockPos = entity.getBlockPos();

        this.addDataSlots(data);

        IItemHandler upgradeHandler = this.blockEntity.getUpgradeSlots();
        int startX = -58;
        int startY = 28;

        this.addSlot(new DynamicUpgradeSlot(upgradeHandler, 0, startX, startY, () -> this.isUpgradeTabFullyOpen));
        this.addSlot(new DynamicUpgradeSlot(upgradeHandler, 1, startX + 18, startY, () -> this.isUpgradeTabFullyOpen));
        this.addSlot(new DynamicUpgradeSlot(upgradeHandler, 2, startX, startY + 18, () -> this.isUpgradeTabFullyOpen));
        this.addSlot(new DynamicUpgradeSlot(upgradeHandler, 3, startX + 18, startY + 18, () -> this.isUpgradeTabFullyOpen));
    }

    @Override
    public boolean clickMenuButton(@NotNull Player player, int id) {
        if (id == 0) {
            this.isUpgradeTabFullyOpen = !this.isUpgradeTabFullyOpen;
            return true;
        }
        return super.clickMenuButton(player, id);
    }

    public int getEnergy() { return this.data.get(2); }

    public int getMaxEnergy() { return this.data.get(3); }

    public int getScaledEnergy() {
        int energy = getEnergy();
        int maxEnergy = getMaxEnergy();
        return maxEnergy != 0 && energy != 0 ? (energy * 50) / maxEnergy : 0;
    }

    @Override
    public RedstoneModes getRedstoneMode() {
        int modeId = this.data.get(4);
        return RedstoneModes.values()[modeId % RedstoneModes.values().length];
    }

    @Override
    public void setUpgradeTabFullyOpen(boolean open) {
        this.isUpgradeTabFullyOpen = open;
    }

    public boolean isCrafting() { return data.get(0) > 0; }

    public int getScaledProgress() {
        int progress = this.data.get(0);
        int maxProgress = this.data.get(1);
        int progressArrowSize = 24;
        return maxProgress != 0 && progress != 0 ? progress * progressArrowSize / maxProgress : 0;
    }

    @Override
    public T getBlockEntity() { return this.blockEntity; }

    @Override
    public BlockPos getBlockEntityPos() { return this.blockPos; }

    @Override
    public int getSideMode(int sideId) {
        return this.data.get(5 + sideId);
    }
    @Override
    public void setSideMode(int sideId, int mode) {
        this.data.set(5 + sideId, mode % 3);
    }
}
