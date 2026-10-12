package kirsan122.assemblyexpansion.common.blockEntity.machines;

import kirsan122.assemblyexpansion.api.IConfigurableBE;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class ConfigurableBlockEntity extends UpgradableBlockEntity implements IConfigurableBE {

    protected final int[] sideConfigModes = new int[6];

    public ConfigurableBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public int getDeviceSideMode(int sideId) {

        if (sideId < 0 || sideId >= 6) return 0;
        return this.sideConfigModes[sideId];
    }

    @Override
    public void setDeviceSideMode(int sideId, int mode) {
        if (sideId >= 0 && sideId < 6) {
            this.sideConfigModes[sideId] = mode;
            this.setChanged();
        }
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        tag.putIntArray("SideConfigs", this.sideConfigModes);
        super.saveAdditional(tag);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("SideConfigs", net.minecraft.nbt.Tag.TAG_INT_ARRAY)) {
            int[] savedModes = tag.getIntArray("SideConfigs");
            if (savedModes.length == 6) {
                System.arraycopy(savedModes, 0, this.sideConfigModes, 0, 6);
            }
        }
    }
}
