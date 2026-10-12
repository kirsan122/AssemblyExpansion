package kirsan122.assemblyexpansion.common.inventory;

import kirsan122.assemblyexpansion.api.RedstoneModes;
import kirsan122.assemblyexpansion.client.gui.GuiData;
import kirsan122.assemblyexpansion.common.blockEntity.machines.ElectrolyzerBlockEntity;
import kirsan122.assemblyexpansion.common.init.AEMachineBlocks;
import kirsan122.assemblyexpansion.common.init.AEMenues;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class ElectrolyzerMenu extends AbstractContainerMenu {
    private final ElectrolyzerBlockEntity blockEntity;
    private final ContainerData data;
    private final BlockPos blockPos;
    public boolean isUpgradeTabFullyOpen = false;

    public ElectrolyzerMenu(int containerId, Inventory inv, FriendlyByteBuf extraData) {
        this(containerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(5));
    }

    public ElectrolyzerMenu(int containerId, Inventory inv, BlockEntity entity, ContainerData data) {
        super(AEMenues.ELECTROLYZER_MENU.get(), containerId);
        this.blockEntity = (ElectrolyzerBlockEntity) entity;

        this.data = data;
        this.blockPos = entity.getBlockPos();

        this.addDataSlots(data);

        IItemHandler handler = this.blockEntity.getItemHandler();

        this.addSlot(new SlotItemHandler(handler, 0, 8, 63) {
            @Override
            public boolean mayPlace(ItemStack stack) {return FluidUtil.getFluidHandler(stack).isPresent();}
        });

        this.addSlot(new SlotItemHandler(handler, 1, 80, 63) {
            @Override
            public boolean mayPlace(ItemStack stack) {return FluidUtil.getFluidHandler(stack).isPresent();}
        });

        this.addSlot(new SlotItemHandler(handler, 2, 116, 63) {
            @Override
            public boolean mayPlace(ItemStack stack) {return FluidUtil.getFluidHandler(stack).isPresent();}
        });
        this.addSlot(new SlotItemHandler(handler, 3, 152, 63));

        IItemHandler upgradeHandler = this.blockEntity.getUpgradeSlots();
        int startX = -58;
        int startY = 28;

        this.addSlot(new DynamicUpgradeSlot(upgradeHandler, 0, startX, startY, () -> this.isUpgradeTabFullyOpen));
        this.addSlot(new DynamicUpgradeSlot(upgradeHandler, 1, startX + 18, startY, () -> this.isUpgradeTabFullyOpen));
        this.addSlot(new DynamicUpgradeSlot(upgradeHandler, 2, startX, startY + 18, () -> this.isUpgradeTabFullyOpen));
        this.addSlot(new DynamicUpgradeSlot(upgradeHandler, 3, startX + 18, startY + 18, () -> this.isUpgradeTabFullyOpen));


        GuiData.layoutPlayerInventory(inv, 8, 84, this::addSlot);
    }

    public int getEnergy() { return this.data.get(2); }

    public int getMaxEnergy() { return this.data.get(3); }

    public int getScaledEnergy(int energyBarHeight) {
        int energy = this.data.get(2);
        int maxEnergy = this.data.get(3);
        return maxEnergy != 0 && energy != 0 ? (energy * energyBarHeight) / maxEnergy : 0;
    }

    public RedstoneModes getRedstoneMode() {
        int modeId = this.data.get(4);
        return RedstoneModes.values()[modeId % RedstoneModes.values().length];
    }

    @Override
    public boolean clickMenuButton(@NotNull Player player, int id) {
        if (id == 0) {
            this.isUpgradeTabFullyOpen = !this.isUpgradeTabFullyOpen;
            return true;
        }
        return super.clickMenuButton(player, id);
    }

    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        Slot sourceSlot = slots.get(index);
        if (!sourceSlot.hasItem()) return ItemStack.EMPTY;

        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copyStack = sourceStack.copy();


        if (index < 8) {
            if (!moveItemStackTo(sourceStack, 8, 44, true)) {
                return ItemStack.EMPTY;
            }
        }

        else {
            boolean moved = false;


            if (this.blockEntity.isItemInUpgradeHandlerValid(sourceStack)) {

                if (this.isUpgradeTabFullyOpen) {

                    if (moveItemStackTo(sourceStack, 4, 8, false)) {
                        moved = true;
                    }
                }
            }



            if (!moved) {
                if (sourceStack.getCapability(ForgeCapabilities.ENERGY).isPresent()) {
                    if (!moveItemStackTo(sourceStack, 3, 4, false)) return ItemStack.EMPTY;
                } else if (sourceStack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).isPresent()) {
                    if (!moveItemStackTo(sourceStack, 0, 3, false)) return ItemStack.EMPTY;
                } else {



                    return ItemStack.EMPTY;
                }
            }
        }


        if (sourceStack.isEmpty()) {
            sourceSlot.setByPlayer(ItemStack.EMPTY);
        } else {
            sourceSlot.setChanged();
        }



        if (sourceStack.getCount() == copyStack.getCount()) {
            return ItemStack.EMPTY;
        }

        return copyStack;
    }


    @Override
    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos()),
                player, AEMachineBlocks.ELECTROLYZER.get());
    }

    public ElectrolyzerBlockEntity getBlockEntity() { return this.blockEntity; }

    public BlockPos getBlockEntityPos() { return this.blockPos; }
}
