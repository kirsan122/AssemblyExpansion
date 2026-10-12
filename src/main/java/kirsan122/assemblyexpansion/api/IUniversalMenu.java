package kirsan122.assemblyexpansion.api;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * Universal interface for menus that support automation side configuration, redstone configuration and upgrades.
 */
public interface IUniversalMenu {
    int getSideMode(int sideId);
    void setSideMode(int sideId, int mode);
    RedstoneModes getRedstoneMode();
    BlockPos getBlockEntityPos();
    BlockEntity getBlockEntity();
    void setUpgradeTabFullyOpen(boolean open);
}
