package kirsan122.assemblyexpansion.common.entity.hevea;

import kirsan122.assemblyexpansion.common.init.AEItems;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.ChestBoat;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

public class HeveaChestBoatEntity extends ChestBoat {
    public HeveaChestBoatEntity(EntityType<? extends ChestBoat> type, Level level) {
        super(type, level);
    }

    @Override
    public Item getDropItem() {
        return AEItems.HEVEA_CHEST_BOAT.get();
    }
}

