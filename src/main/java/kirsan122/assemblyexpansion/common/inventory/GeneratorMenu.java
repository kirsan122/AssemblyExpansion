package kirsan122.assemblyexpansion.common.inventory;

import kirsan122.assemblyexpansion.client.gui.GuiData;
import kirsan122.assemblyexpansion.common.blockEntity.machines.GeneratorBlockEntity;
import kirsan122.assemblyexpansion.common.init.AEMachineBlocks;
import kirsan122.assemblyexpansion.common.init.AEMenues;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.items.SlotItemHandler;

public class GeneratorMenu extends AbstractContainerMenu {
    private final GeneratorBlockEntity blockEntity;
    private final ContainerData data;


    public GeneratorMenu(int id, Inventory inv, BlockEntity entity, ContainerData data) {
        super(AEMenues.GENERATOR_MENU.get(), id);
        this.blockEntity = (GeneratorBlockEntity) entity;
        this.data = data;
        this.addDataSlots(data);


        this.addSlot(new SlotItemHandler(blockEntity.getItemHandler(), 0, 37, 35));

        this.addSlot(new SlotItemHandler(blockEntity.getItemHandler(), 1, 112, 35));

        GuiData.layoutPlayerInventory(inv, 8, 84, this::addSlot);
    }

    public int getMaxEnergy() { return this.data.get(1); }

    public boolean isBurning() { return this.data.get(2) > 0; }

    public int getBurnScaled() {
        int litTime = this.data.get(2);
        int maxLitTime = this.data.get(3);

        if (litTime <= 0) return 0;


        int scaled = (int)((float)litTime * 14 / maxLitTime);



        if (litTime > 0 && scaled < 2) {
            return 2;
        }

        return scaled;
    }

    public int getEnergy() { return this.data.get(0); }
    public int getEnergyScaled() {
        int energy = getEnergy();

        return energy > 0 ? (energy * 50 / 5000) : 0;

    }

    @Override
    public boolean stillValid(Player player) {

        return stillValid(ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos()),
                player, AEMachineBlocks.GENERATOR.get());
    }


    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        Slot sourceSlot = slots.get(index);
        if (sourceSlot == null || !sourceSlot.hasItem()) return ItemStack.EMPTY;
        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copyStack = sourceStack.copy();


        if (index < 2) {
            if (!moveItemStackTo(sourceStack, 2, 38, true)) return ItemStack.EMPTY;
        }

        else {

            if (ForgeHooks.getBurnTime(sourceStack, null) > 0) {
                if (!moveItemStackTo(sourceStack, 0, 1, false)) return ItemStack.EMPTY;
            }

            else if (!moveItemStackTo(sourceStack, 1, 2, false)) {
                return ItemStack.EMPTY;
            }
        }

        if (sourceStack.isEmpty()) sourceSlot.setByPlayer(ItemStack.EMPTY);
        else sourceSlot.setChanged();

        return copyStack;
    }
}

