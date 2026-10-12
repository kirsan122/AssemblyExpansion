package kirsan122.assemblyexpansion.common.util;

import kirsan122.assemblyexpansion.AssemblyExpansion;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class AETags {
    public static class Items {
        public static final TagKey<Item> ALL_UPGRADES = ItemTags.create(new ResourceLocation(AssemblyExpansion.MOD_ID, "all_upgrades"));
    }
}
