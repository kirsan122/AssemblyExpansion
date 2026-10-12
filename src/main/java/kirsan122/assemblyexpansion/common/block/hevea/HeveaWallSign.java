package kirsan122.assemblyexpansion.common.block.hevea;

import kirsan122.assemblyexpansion.common.init.AEBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;

public class HeveaWallSign extends WallSignBlock {
    public HeveaWallSign(Properties properties, WoodType type) {
        super(properties, type);
    }
    @Override
    public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return true;
    }


    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {

        return AEBlockEntities.HEVEA_SIGN_BE.get().create(pos, state);
    }
    @Override
    public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return 5;
    }

    @Override
    public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return 20;
    }
}
