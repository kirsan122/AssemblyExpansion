package kirsan122.assemblyexpansion.common.blockEntity.hevea;

import kirsan122.assemblyexpansion.common.init.AEBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class HeveaSignBlockEntity extends SignBlockEntity {
    public HeveaSignBlockEntity(BlockPos pos, BlockState state) {

        super(AEBlockEntities.HEVEA_SIGN_BE.get(), pos, state);
    }

    @Override
    public BlockEntityType<?> getType() {
        return AEBlockEntities.HEVEA_SIGN_BE.get();
    }
    @Override
    public void load(net.minecraft.nbt.CompoundTag tag) {
        super.load(tag);

        this.setChanged();
    }

    @Override
    protected void saveAdditional(net.minecraft.nbt.CompoundTag tag) {
        super.saveAdditional(tag);
    }


    @Override
    public net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket getUpdatePacket() {
        return net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public net.minecraft.nbt.CompoundTag getUpdateTag() {
        return this.saveWithoutMetadata();
    }

}
