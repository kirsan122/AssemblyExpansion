package kirsan122.assemblyexpansion.common.entity.hevea.behavior;

import kirsan122.assemblyexpansion.common.entity.hevea.HeveaBoatEntity;
import kirsan122.assemblyexpansion.common.entity.hevea.HeveaChestBoatEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.ChestBoat;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import java.util.function.Supplier;

public class HeveaBoatDispenseBehavior extends DefaultDispenseItemBehavior {
    private final DefaultDispenseItemBehavior defaultDispenseItemBehavior = new DefaultDispenseItemBehavior();
    private final Supplier<EntityType<? extends Boat>> entityTypeSupplier;
    private final boolean hasChest;

    public HeveaBoatDispenseBehavior(Supplier<EntityType<? extends Boat>> entityTypeSupplier, boolean hasChest) {
        this.entityTypeSupplier = entityTypeSupplier;
        this.hasChest = hasChest;
    }

    @Override
    public ItemStack execute(BlockSource source, ItemStack stack) {
        Direction direction = source.getBlockState().getValue(DispenserBlock.FACING);
        Level level = source.getLevel();


        double d0 = source.x() + (double)((float)direction.getStepX() * 1.125F);
        double d1 = source.y() + (double)((float)direction.getStepY() * 1.125F);
        double d2 = source.z() + (double)((float)direction.getStepZ() * 1.125F);

        BlockPos blockpos = source.getPos().relative(direction);
        double d3;


        if (level.getFluidState(blockpos).is(FluidTags.WATER)) {
            d3 = 1.0D;
        } else {
            if (!level.getBlockState(blockpos).isAir() || !level.getFluidState(blockpos.below()).is(FluidTags.WATER)) {
                return this.defaultDispenseItemBehavior.dispense(source, stack);
            }
            d3 = 0.0D;
        }


        Boat boat = this.hasChest ?
                new HeveaChestBoatEntity((EntityType<? extends ChestBoat>) this.entityTypeSupplier.get(), level) :
                new HeveaBoatEntity(this.entityTypeSupplier.get(), level);

        boat.setPos(d0, d1 + d3, d2);
        boat.setVariant(Boat.Type.OAK);
        boat.setYRot(direction.toYRot());

        level.addFreshEntity(boat);
        stack.shrink(1);
        return stack;
    }
}

