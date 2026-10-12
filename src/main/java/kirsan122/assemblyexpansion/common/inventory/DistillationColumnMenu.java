package kirsan122.assemblyexpansion.common.inventory;

import kirsan122.assemblyexpansion.common.blockEntity.machines.distillation_column.DistillationColumnBlockEntity;
import kirsan122.assemblyexpansion.common.init.AEMachineBlocks;
import kirsan122.assemblyexpansion.common.init.AEMenues;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;

public class DistillationColumnMenu extends AbstractContainerMenu implements IFluidScreenRender<DistillationColumnBlockEntity> {
    private final DistillationColumnBlockEntity blockEntity;
    private final BlockPos blockPos;
    private final ContainerData data;

    public DistillationColumnMenu(int containerId, Inventory inv, FriendlyByteBuf extraData) {
        this(containerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(4));
    }

    public DistillationColumnMenu(int containerId, Inventory inv, BlockEntity entity, ContainerData data) {
        super(AEMenues.DISTILLATION_COLUMN_MENU.get(), containerId);
        this.addDataSlots(data);
        this.data = data;
        this.blockEntity = (DistillationColumnBlockEntity) entity;
        this.blockPos = entity.getBlockPos();
    }

    public boolean isCrafting() { return data.get(0) > 0; }

    public int getScaledProgress() {
        int progress = data.get(0);
        int maxProgress = data.get(1);
        int progressArrowSize = 27;
        return maxProgress != 0 && progress != 0 ? progress * progressArrowSize / maxProgress : 0;
    }

    public int getScaledEnergy(int energyBarHeight) {
        int energy = data.get(2);
        int maxEnergy = data.get(3);
        return maxEnergy != 0 && energy != 0 ? (energy * energyBarHeight) / maxEnergy : 0;
    }

    @Override
    public DistillationColumnBlockEntity getBlockEntity() { return this.blockEntity; }

    public BlockPos getBlockEntityPos() { return this.blockPos; }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos()),
                player, AEMachineBlocks.DISTILLATION_COLUMN_BASE.get());
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player playerIn, int index) {

        ItemStack itemstack = ItemStack.EMPTY;
        net.minecraft.world.inventory.Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (index < 27) {
                if (!this.moveItemStackTo(itemstack1, 27, 36, false)) return ItemStack.EMPTY;
            } else if (index < 36) {
                if (!this.moveItemStackTo(itemstack1, 0, 27, false)) return ItemStack.EMPTY;
            }
            if (itemstack1.isEmpty()) slot.setByPlayer(ItemStack.EMPTY);
            else slot.setChanged();
        }
        return itemstack;
    }
}
