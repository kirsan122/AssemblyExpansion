package kirsan122.assemblyexpansion.common.blockEntity.machines;

import kirsan122.assemblyexpansion.api.RedstoneModes;
import kirsan122.assemblyexpansion.network.packet.PacketUpdateRedstone;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class RedstoneConfigurableBlockEntity extends BlockEntity implements PacketUpdateRedstone.IRedstoneConfigurable {

    protected RedstoneModes redstoneMode = RedstoneModes.IGNORED;

    public RedstoneConfigurableBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public RedstoneModes getRedstoneMode() {
        return this.redstoneMode;
    }

    @Override
    public void setRedstoneMode(RedstoneModes mode) {
        this.redstoneMode = mode;
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        tag.putString("RedstoneMode", this.redstoneMode.getSerializedName());
        super.saveAdditional(tag);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("RedstoneMode")) {
            this.redstoneMode = RedstoneModes.valueOf(tag.getString("RedstoneMode").toUpperCase());
        }
    }
}
