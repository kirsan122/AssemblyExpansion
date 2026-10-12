package kirsan122.assemblyexpansion.common.item;

import kirsan122.assemblyexpansion.common.entity.hevea.HeveaBoatEntity;
import kirsan122.assemblyexpansion.common.entity.hevea.HeveaChestBoatEntity;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.ChestBoat;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.function.Supplier;

public class HeveaChestBoatItem extends Item {
    private final boolean hasChest;

    private final Supplier<EntityType<? extends Boat>> entityTypeSupplier;

    public HeveaChestBoatItem(boolean hasChest, Supplier<EntityType<? extends Boat>> entityTypeSupplier, Properties properties) {
        super(properties);
        this.hasChest = hasChest;
        this.entityTypeSupplier = entityTypeSupplier;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);

        HitResult hitresult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.ANY);

        if (hitresult.getType() == HitResult.Type.MISS) {
            return InteractionResultHolder.pass(itemstack);
        } else {
            Vec3 lookVector = player.getViewVector(1.0F);

            List<Entity> list = level.getEntities(player, player.getBoundingBox().expandTowards(lookVector.scale(5.0D)).inflate(1.0D), EntitySelector.NO_SPECTATORS.and(Entity::isPickable));

            if (!list.isEmpty()) {
                Vec3 eyePosition = player.getEyePosition();
                for(Entity entity : list) {
                    AABB aabb = entity.getBoundingBox().inflate((double)entity.getPickRadius());
                    if (aabb.contains(eyePosition)) {
                        return InteractionResultHolder.pass(itemstack);
                    }
                }
            }

            if (hitresult.getType() == HitResult.Type.BLOCK) {

                Boat boat = this.getBoat(level, hitresult);
                boat.setVariant(Boat.Type.OAK);
                boat.setYRot(player.getYRot());


                if (!level.noCollision(boat, boat.getBoundingBox())) {
                    return InteractionResultHolder.fail(itemstack);
                } else {
                    if (!level.isClientSide) {
                        level.addFreshEntity(boat);
                        level.gameEvent(player, GameEvent.ENTITY_PLACE, hitresult.getLocation());
                        if (!player.getAbilities().instabuild) {
                            itemstack.shrink(1);
                        }
                    }
                    player.awardStat(Stats.ITEM_USED.get(this));
                    return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide());
                }
            } else {
                return InteractionResultHolder.pass(itemstack);
            }
        }
    }


    private Boat getBoat(Level level, HitResult hitResult) {
        Vec3 pos = hitResult.getLocation();

        Boat boat = this.hasChest ?
                new HeveaChestBoatEntity((EntityType<? extends ChestBoat>) this.entityTypeSupplier.get(), level) :
                new HeveaBoatEntity(this.entityTypeSupplier.get(), level);



        boat.setPos(pos.x, pos.y, pos.z);
        return boat;
    }

}
