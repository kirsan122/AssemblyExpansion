package kirsan122.assemblyexpansion.common.inventory;

import kirsan122.assemblyexpansion.client.gui.GuiData;
import com.kirsan122.assembly_expansion.common.blockEntity.machines.ChemicalMixerBlockEntity;
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
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class ChemicalMixerMenu extends UniversalMenu<ChemicalMixerBlockEntity> implements IFluidScreenRender<ChemicalMixerBlockEntity> {
    public ChemicalMixerMenu(int containerId, Inventory inv, FriendlyByteBuf extraData) {
        this(containerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(11));
    }

    public ChemicalMixerMenu(int containerId, Inventory inv, BlockEntity entity, ContainerData data) {
        super(AEMenues.CHEMICAL_MIXER_MENU.get(), containerId, (ChemicalMixerBlockEntity) entity, data);

        IItemHandler handler = this.blockEntity.getItemHandler();

        this.addSlot(new SlotItemHandler(handler, 0, 32, 54) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return FluidUtil.getFluidHandler(stack).isPresent();
            }
        });

        this.addSlot(new SlotItemHandler(handler, 1, 50, 54) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return FluidUtil.getFluidHandler(stack).isPresent();
            }
        });

        this.addSlot(new SlotItemHandler(handler, 2, 122, 54) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return FluidUtil.getFluidHandler(stack).isPresent();
            }
        });

        this.addSlot(new SlotItemHandler(handler, 3, 32, 72));

        this.addSlot(new SlotItemHandler(handler, 4, 50, 72));

        this.addSlot(new SlotItemHandler(handler, 5, 122, 72));

        this.addSlot(new SlotItemHandler(handler, 6, 156, 42) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.getCapability(ForgeCapabilities.ENERGY).isPresent();
            }
        });

        GuiData.layoutPlayerInventory(inv, 14, 98, this::addSlot);
    }

    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        Slot sourceSlot = slots.get(index);
        if (!sourceSlot.hasItem()) return ItemStack.EMPTY;

        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copyStack = sourceStack.copy();


        if (index < 11) {
            if (!moveItemStackTo(sourceStack, 11, 46, true)) {
                return ItemStack.EMPTY;
            }
        }

        else {
            boolean moved = false;

            if (this.blockEntity.isItemInUpgradeHandlerValid(sourceStack)) {

                if (this.isUpgradeTabFullyOpen) {

                    if (moveItemStackTo(sourceStack, 7, 11, false)) {
                        moved = true;
                    }
                }
            }



            if (!moved) {
                if (sourceStack.getCapability(ForgeCapabilities.ENERGY).isPresent()) {
                    if (!moveItemStackTo(sourceStack, 6, 7, false)) return ItemStack.EMPTY;
                } else if (sourceStack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).isPresent()) {
                    if (!moveItemStackTo(sourceStack, 0, 3, false)) return ItemStack.EMPTY;
                } else {
                    if (!moveItemStackTo(sourceStack, 3, 6, false)) return ItemStack.EMPTY;

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
                player, AEMachineBlocks.CHEMICAL_MIXER.get());
    }
}
