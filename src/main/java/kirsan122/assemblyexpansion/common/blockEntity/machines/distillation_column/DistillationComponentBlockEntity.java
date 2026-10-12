package kirsan122.assemblyexpansion.common.blockEntity.machines.distillation_column;

import kirsan122.assemblyexpansion.common.init.AEBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DistillationComponentBlockEntity extends BlockEntity {

    public DistillationComponentBlockEntity(BlockPos pos, BlockState state) {
        super(AEBlockEntities.DISTILLATION_COMPONENT_BE.get(), pos, state);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.FLUID_HANDLER && level != null) {


            for (int distance = 1; distance <= 3; distance++) {
                BlockEntity baseEntity = level.getBlockEntity(worldPosition.below(distance));

                if (baseEntity instanceof DistillationColumnBlockEntity column) {

                    int tankIndex = -1;
                    if (distance == 1) tankIndex = 3;
                    if (distance == 2) tankIndex = 2;
                    if (distance == 3) tankIndex = 1;

                    if (tankIndex != -1) {
                        return column.getTankCapability(tankIndex).cast();
                    }
                }
            }
        }
        return super.getCapability(cap, side);
    }
}
