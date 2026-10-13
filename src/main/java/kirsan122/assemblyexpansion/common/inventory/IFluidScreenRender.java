package kirsan122.assemblyexpansion.common.inventory;

import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * You only need to implement this interface if your Screen renders fluids in FluidTanks
 * and you want to use my `renderFluidInTank()` method
 * from class `kirsan122.assemblyexpansion.client.gui.GuiData` for the rendering.
 */

public interface IFluidScreenRender<T extends BlockEntity> {
    T getBlockEntity();
}
