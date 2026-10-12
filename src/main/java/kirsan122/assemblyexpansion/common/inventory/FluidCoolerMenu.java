package kirsan122.assemblyexpansion.common.inventory;

import kirsan122.assemblyexpansion.client.gui.GuiData;
import kirsan122.assemblyexpansion.common.blockEntity.machines.FluidCoolerBlockEntity;
import kirsan122.assemblyexpansion.common.init.AEMachineBlocks;
import kirsan122.assemblyexpansion.common.init.AEMenues;
import kirsan122.assemblyexpansion.common.util.AETags;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class FluidCoolerMenu extends UniversalMenu<FluidCoolerBlockEntity> implements IFluidScreenRender<FluidCoolerBlockEntity> {

    public FluidCoolerMenu(int containerId, Inventory inv, FriendlyByteBuf extraData) {
        this(containerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(11));
    }

    public FluidCoolerMenu(int containerId, Inventory inv, BlockEntity entity, ContainerData data) {
        super(AEMenues.FLUID_COOLER_MENU.get(), containerId, (FluidCoolerBlockEntity) entity, data);

        IItemHandler handler = this.blockEntity.getItemHandler();

        this.addSlot(new SlotItemHandler(handler, 0, 32, 18) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return FluidUtil.getFluidHandler(stack).isPresent();
            }
        });

        this.addSlot(new SlotItemHandler(handler, 1, 32, 54) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });

        this.addSlot(new SlotItemHandler(handler, 2, 128, 18) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return FluidUtil.getFluidHandler(stack).isPresent();
            }
        });
        this.addSlot(new SlotItemHandler(handler, 3, 128, 54) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });

        this.addSlot(new SlotItemHandler(handler, 4, 156, 36) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.getCapability(ForgeCapabilities.ENERGY).isPresent();
            }
        });

        GuiData.layoutPlayerInventory(inv, 14, 84, this::addSlot);
    }

    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {

        Slot sourceSlot = slots.get(index);
        if (!sourceSlot.hasItem()) return ItemStack.EMPTY;

        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copyStack = sourceStack.copy();


        if (index < 9) {

            if (!moveItemStackTo(sourceStack, 9, 45, true)) {
                return ItemStack.EMPTY;
            }
        }

        else {

            if (sourceStack.is(AETags.Items.ALL_UPGRADES)) {

                if (this.isUpgradeTabFullyOpen) {

                    if (!moveItemStackTo(sourceStack, 5, 9, false)) {


                        return ItemStack.EMPTY;
                    }
                } else {

                    return ItemStack.EMPTY;
                }
            }

            else {
                if (sourceStack.getCapability(ForgeCapabilities.ENERGY).isPresent()) {
                    if (!moveItemStackTo(sourceStack, 4, 5, false)) return ItemStack.EMPTY;
                } else if (sourceStack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).isPresent()) {
                    if (!moveItemStackTo(sourceStack, 0, 4, false)) return ItemStack.EMPTY;
                }
            }
        }

        if (sourceStack.isEmpty()) {
            sourceSlot.setByPlayer(ItemStack.EMPTY);
        } else {
            sourceSlot.setChanged();
        }

        return copyStack;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos()),
                player, AEMachineBlocks.FLUID_COOLER.get());
    }
}
