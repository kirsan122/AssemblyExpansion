package kirsan122.assemblyexpansion.common.block.hevea;

import kirsan122.assemblyexpansion.AssemblyExpansion;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

import javax.annotation.Nullable;

public class HeveaTreeGrower extends AbstractTreeGrower {

    public static final ResourceKey<ConfiguredFeature<?, ?>> SMALL_TREE = ResourceKey.create(
            Registries.CONFIGURED_FEATURE, new ResourceLocation(AssemblyExpansion.MOD_ID, "small_hevea_tree"));

    public static final ResourceKey<ConfiguredFeature<?, ?>> BIG_TREE = ResourceKey.create(
            Registries.CONFIGURED_FEATURE, new ResourceLocation(AssemblyExpansion.MOD_ID, "big_hevea_tree"));

    @Nullable
    @Override
    protected ResourceKey<ConfiguredFeature<?, ?>> getConfiguredFeature(RandomSource random, boolean hasFlowers) {

        if (random.nextFloat() < 0.20F) {
            return BIG_TREE;
        } else {
            return SMALL_TREE;
        }
    }
}

