package kirsan122.assemblyexpansion.common.blockEntity.machines;

import kirsan122.assemblyexpansion.api.IUpgradableBE;
import kirsan122.assemblyexpansion.common.util.AETags;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemStackHandler;

public abstract class UpgradableBlockEntity extends RedstoneConfigurableBlockEntity implements IUpgradableBE {

    protected final ItemStackHandler upgradeSlots = new ItemStackHandler(4) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return isItemInUpgradeHandlerValid(stack);
        }
    };

    public UpgradableBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public boolean isItemInUpgradeHandlerValid(ItemStack stack) {
        return stack.is(AETags.Items.ALL_UPGRADES);
    }

    @Override
    public ItemStackHandler getUpgradeSlots() {
        return this.upgradeSlots;
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        tag.put("upgrade_slots", upgradeSlots.serializeNBT());
        super.saveAdditional(tag);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        upgradeSlots.deserializeNBT(tag.getCompound("upgrade_slots"));
    }
}
