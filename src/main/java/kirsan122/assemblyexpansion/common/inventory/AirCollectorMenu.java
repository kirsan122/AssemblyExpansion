package kirsan122.assemblyexpansion.common.inventory;

import kirsan122.assemblyexpansion.client.gui.GuiData;
import kirsan122.assemblyexpansion.common.blockEntity.machines.AirCollectorBlockEntity;
import kirsan122.assemblyexpansion.common.init.AEMachineBlocks;
import kirsan122.assemblyexpansion.common.init.AEMenues;
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
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class AirCollectorMenu extends UniversalMenu<AirCollectorBlockEntity> implements IFluidScreenRender<AirCollectorBlockEntity> {

    private static final int UPGRADE_SLOT_START = 3;
    private static final int PLAYER_INV_START = 7;

    public AirCollectorMenu(int containerId, Inventory inv, FriendlyByteBuf extraData) {
        this(containerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(11));
    }

    public AirCollectorMenu(int containerId, Inventory inv, BlockEntity entity, ContainerData data) {
        super(AEMenues.AIR_COLLECTOR_MENU.get(), containerId, (AirCollectorBlockEntity) entity, data);

        IItemHandler handler = this.blockEntity.getItemHandler();

        this.addSlot(new SlotItemHandler(handler, 0, 56, 17) {
            @Override
            public boolean mayPlace(ItemStack stack) { return stack.getCapability(ForgeCapabilities.FLUID_HANDLER).isPresent(); }
        });
        this.addSlot(new SlotItemHandler(handler, 1, 56, 53) {
            @Override
            public boolean mayPlace(ItemStack stack) { return stack.getCapability(ForgeCapabilities.FLUID_HANDLER).isPresent(); }

        });
        this.addSlot(new SlotItemHandler(handler, 2, 142, 35) {
            @Override
            public boolean mayPlace(ItemStack stack) { return stack.getCapability(ForgeCapabilities.ENERGY).isPresent(); }
        });

        GuiData.layoutPlayerInventory(inv, 8, 84, this::addSlot);
    }

    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        Slot sourceSlot = slots.get(index);
        if (!sourceSlot.hasItem()) return ItemStack.EMPTY;

        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copyStack = sourceStack.copy();


        if (index < PLAYER_INV_START) {
            if (!moveItemStackTo(sourceStack, PLAYER_INV_START, PLAYER_INV_START + 36, true)) {
                return ItemStack.EMPTY;
            }
        }

        else {
            boolean moved = false;


            if (this.blockEntity.isItemInUpgradeHandlerValid(sourceStack)) {
                if (this.isUpgradeTabFullyOpen) {

                    if (moveItemStackTo(sourceStack, UPGRADE_SLOT_START, PLAYER_INV_START, false)) {
                        moved = true;
                    }
                }
            }


            if (!moved) {

                if (sourceStack.getCapability(ForgeCapabilities.ENERGY).isPresent()) {
                    if (!moveItemStackTo(sourceStack, 2, 3, false)) return ItemStack.EMPTY;
                }

                else if (sourceStack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).isPresent()) {
                    if (!moveItemStackTo(sourceStack, 0, 2, false)) return ItemStack.EMPTY;
                }

                else {
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
                player, AEMachineBlocks.AIR_COLLECTOR.get());
    }
}
