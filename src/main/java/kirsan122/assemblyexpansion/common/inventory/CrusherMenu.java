package kirsan122.assemblyexpansion.common.inventory;

import kirsan122.assemblyexpansion.client.gui.GuiData;
import kirsan122.assemblyexpansion.common.blockEntity.machines.CrusherBlockEntity;
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
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class CrusherMenu extends UniversalMenu<CrusherBlockEntity> {

    public CrusherMenu(int containerId, Inventory inv, FriendlyByteBuf extraData) {
        this(containerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(11));
    }

    public CrusherMenu(int containerId, Inventory inv, BlockEntity entity, ContainerData data) {
        super(AEMenues.CRUSHER_MENU.get(), containerId, (CrusherBlockEntity) entity, data);

        IItemHandler handler = this.blockEntity.getItemHandler();

        this.addSlot(new SlotItemHandler(handler, 0, 56, 17));

        this.addSlot(new SlotItemHandler(handler, 1, 56, 53));

        this.addSlot(new SlotItemHandler(handler, 2, 116, 35) {
            @Override
            public boolean mayPlace(ItemStack stack) { return false; }
        });
        this.addSlot(new SlotItemHandler(handler, 3, 116, 59) {
            @Override
            public boolean mayPlace(ItemStack stack) { return false; }
        });

        GuiData.layoutPlayerInventory(inv, 8, 84, this::addSlot);
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

            if (sourceStack.is(AETags.Items.ALL_UPGRADES)) {

                if (this.isUpgradeTabFullyOpen) {

                    if (!moveItemStackTo(sourceStack, 4, 8, false)) {


                        return ItemStack.EMPTY;
                    }
                } else {

                    return ItemStack.EMPTY;
                }
            }

            else {
                if (sourceStack.getCapability(ForgeCapabilities.ENERGY).isPresent()) {
                    if (!moveItemStackTo(sourceStack, 1, 2, false)) return ItemStack.EMPTY;
                } else {
                    if (!moveItemStackTo(sourceStack, 0, 1, false)) return ItemStack.EMPTY;
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
                player, AEMachineBlocks.CRUSHER.get());
    }
}
