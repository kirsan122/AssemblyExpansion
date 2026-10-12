package kirsan122.assemblyexpansion.common.inventory;

import kirsan122.assemblyexpansion.client.gui.GuiData;
import kirsan122.assemblyexpansion.common.blockEntity.FluidTankBlockEntity;
import kirsan122.assemblyexpansion.common.init.AEMachineBlocks;
import kirsan122.assemblyexpansion.common.init.AEMenues;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class FluidTankMenu extends AbstractContainerMenu {
    private final FluidTankBlockEntity blockEntity;
    private final BlockPos blockPos;


    public FluidTankMenu(int containerId, Inventory inv, FriendlyByteBuf extraData) {
        this(containerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()));
    }


    public FluidTankMenu(int containerId, Inventory inv, BlockEntity entity) {
        super(AEMenues.FLUID_TANK_MENU.get(), containerId);

        this.blockEntity = (FluidTankBlockEntity) entity;
        this.blockPos = entity.getBlockPos();

        IItemHandler handler = this.blockEntity.getItemHandler();


        this.addSlot(new SlotItemHandler(handler, 0, 35, 43) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return FluidUtil.getFluidHandler(stack).isPresent();
            }
        });


        this.addSlot(new SlotItemHandler(handler, 1, 124, 43) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return FluidUtil.getFluidHandler(stack).isPresent();
            }
        });


        GuiData.layoutPlayerInventory(inv, 8, 84, this::addSlot);
    }

    public FluidTankBlockEntity getBlockEntity() {
        return this.blockEntity;
    }

    @Override
    public boolean stillValid(Player player) {


        return stillValid(ContainerLevelAccess.create(player.level(), blockEntity.getBlockPos()), player, AEMachineBlocks.FLUID_TANK.get());
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);


        if (slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();


            if (index < 2) {


                if (!this.moveItemStackTo(itemstack1, 2, 38, false)) {
                    return ItemStack.EMPTY;
                }
            }

            else {



                if (FluidUtil.getFluidHandler(itemstack1).isPresent()) {




                    if (!this.moveItemStackTo(itemstack1, 0, 1, false)) {
                        return ItemStack.EMPTY;
                    }

                }

                else {


                    if (!this.moveItemStackTo(itemstack1, 1, 2, false)) {
                        return ItemStack.EMPTY;
                    }

                }
            }



            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }


            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }


            slot.onTake(player, itemstack1);
        }

        return itemstack;
    }
}
