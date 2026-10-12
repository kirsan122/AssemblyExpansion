package kirsan122.assemblyexpansion.common.structures;

import kirsan122.assemblyexpansion.common.init.AEPlacers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;

public class HeveaFoliagePlaced extends FoliagePlacer {
    public static final Codec<HeveaFoliagePlaced> CODEC = RecordCodecBuilder.create(instance ->
            foliagePlacerParts(instance).and(
                    Codec.INT.fieldOf("tree_type").forGetter(placer -> placer.treeType)
            ).apply(instance, HeveaFoliagePlaced::new)
    );

    private final int treeType;

    public HeveaFoliagePlaced(IntProvider radius, IntProvider offset, int treeType) {
        super(radius, offset);
        this.treeType = treeType;
    }

    @Override
    protected FoliagePlacerType<?> type() {
        return AEPlacers.HEVEA_FOLIAGE_PLACER.get();
    }

    @Override
    public int foliageHeight(RandomSource random, int height, TreeConfiguration config) {
        return this.treeType == 0 ? 5 : 6;
    }

    @Override
    protected void createFoliage(LevelSimulatedReader level, FoliageSetter setter, RandomSource random,
                                 TreeConfiguration config, int maxFreeTreeHeight, FoliageAttachment attachment,
                                 int foliageHeight, int radius, int offset) {


        BlockPos center = attachment.pos();


        int trunkHeight = this.treeType == 0 ? 4 : 5;
        for (int y = 0; y < trunkHeight; y++) {
            setter.set(center.above(y), config.trunkProvider.getState(random, center.above(y)));
        }

        if (this.treeType == 0) {





            this.placeOctagon5x5(level, setter, random, config, center, 0);


            this.placeOctagon5x5(level, setter, random, config, center, 1);


            this.placeSquare3x3(level, setter, random, config, center, 2);


            this.placeCross3x3(level, setter, random, config, center, 3);


            FoliagePlacer.tryPlaceLeaf(level, setter, random, config, center.above(4));

        } else {





            this.placeOctagon5x5(level, setter, random, config, center, 0);


            this.placeOctagon5x5(level, setter, random, config, center, 1);


            this.placeSpecialLayer(level, setter, random, config, center, 2);


            this.placeSquare3x3(level, setter, random, config, center, 3);


            this.placeCross3x3(level, setter, random, config, center, 4);


            FoliagePlacer.tryPlaceLeaf(level, setter, random, config, center.above(5));
        }
    }


    private void placeOctagon5x5(LevelSimulatedReader level, FoliageSetter setter, RandomSource random,
                                 TreeConfiguration config, BlockPos center, int yOffset) {
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                if (Math.abs(x) == 2 && Math.abs(z) == 2) {
                    continue;
                }
                mutablePos.setWithOffset(center, x, yOffset, z);
                FoliagePlacer.tryPlaceLeaf(level, setter, random, config, mutablePos);
            }
        }
    }


    private void placeSquare3x3(LevelSimulatedReader level, FoliageSetter setter, RandomSource random,
                                TreeConfiguration config, BlockPos center, int yOffset) {
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                mutablePos.setWithOffset(center, x, yOffset, z);
                FoliagePlacer.tryPlaceLeaf(level, setter, random, config, mutablePos);
            }
        }
    }


    private void placeSpecialLayer(LevelSimulatedReader level, FoliageSetter setter, RandomSource random,
                                   TreeConfiguration config, BlockPos center, int yOffset) {
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                if (Math.abs(x) + Math.abs(z) > 2) {
                    continue;
                }
                mutablePos.setWithOffset(center, x, yOffset, z);
                FoliagePlacer.tryPlaceLeaf(level, setter, random, config, mutablePos);
            }
        }
    }


    private void placeCross3x3(LevelSimulatedReader level, FoliageSetter setter, RandomSource random,
                               TreeConfiguration config, BlockPos center, int yOffset) {
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                if (Math.abs(x) == 1 && Math.abs(z) == 1) {
                    continue;
                }
                mutablePos.setWithOffset(center, x, yOffset, z);
                FoliagePlacer.tryPlaceLeaf(level, setter, random, config, mutablePos);
            }
        }
    }

    @Override
    protected boolean shouldSkipLocation(RandomSource random, int localX, int localY, int localZ, int radius, boolean doubleTrunk) {
        return false;
    }
}
