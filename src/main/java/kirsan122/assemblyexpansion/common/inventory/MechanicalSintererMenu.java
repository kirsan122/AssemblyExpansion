package kirsan122.assemblyexpansion.common.inventory;

import kirsan122.assemblyexpansion.client.gui.GuiData;
import kirsan122.assemblyexpansion.common.blockEntity.machines.MechanicalSintererBlockEntity;
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
import org.jetbrains.annotations.NotNull;

public class MechanicalSintererMenu extends UniversalMenu<MechanicalSintererBlockEntity> {

    private static final int UPGRADE_SLOT_START = 4;
    private static final int PLAYER_INV_START = 8;



    public MechanicalSintererMenu(int containerId, Inventory inv, FriendlyByteBuf extraData) {
        this(containerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(11));
    }


    public MechanicalSintererMenu(int containerId, Inventory inv, BlockEntity entity, ContainerData data) {
        super(AEMenues.MECHANICAL_SINTERER_MENU.get(), containerId, (MechanicalSintererBlockEntity) entity, data);

        IItemHandler handler = this.blockEntity.getItemHandler();

        this.addSlot(new SlotItemHandler(handler, 0, 53, 35));

        this.addSlot(new SlotItemHandler(handler, 1, 32, 35));

        this.addSlot(new SlotItemHandler(handler, 2, 113, 35) {
            @Override
            public boolean mayPlace(ItemStack stack) { return false; }
        });
        this.addSlot(new SlotItemHandler(handler, 3, 143, 52) {
            @Override
            public boolean mayPlace(ItemStack stack) { return stack.getCapability(ForgeCapabilities.ENERGY).isPresent(); }
        });

        GuiData.layoutPlayerInventory(inv, 8, 84, this::addSlot);
    }

    @Override
    public @NotNull ItemStack quickMoveStack(Player playerIn, int index) {
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

            if (sourceStack.is(AETags.Items.ALL_UPGRADES)) {

                if (this.isUpgradeTabFullyOpen) {

                    if (!moveItemStackTo(sourceStack, UPGRADE_SLOT_START, PLAYER_INV_START, false)) {
                        return ItemStack.EMPTY;
                    }
                } else {

                    return ItemStack.EMPTY;
                }
            }

            else {

                if (sourceStack.getCapability(ForgeCapabilities.ENERGY).isPresent()) {


                    if (!moveItemStackTo(sourceStack, 3, 4, false)) {
                        return ItemStack.EMPTY;
                    }
                } else {

                    if (!moveItemStackTo(sourceStack, 0, 1, false)) {

                        if (!moveItemStackTo(sourceStack, 1, 2, false)) {
                            return ItemStack.EMPTY;
                        }
                    }
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
                player, AEMachineBlocks.MECHANICAL_SINTERER.get());
    }
}
