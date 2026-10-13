package kirsan122.assemblyexpansion.common.structures;

import kirsan122.assemblyexpansion.common.init.AEPlacers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class HeveaTrunkPlacer extends TrunkPlacer {
    public static final Codec<HeveaTrunkPlacer> CODEC = RecordCodecBuilder.create(instance ->
            trunkPlacerParts(instance).apply(instance, HeveaTrunkPlacer::new)
    );

    public HeveaTrunkPlacer(int baseHeight, int heightRandA, int heightRandB) {
        super(baseHeight, heightRandA, heightRandB);
    }

    @Override
    protected TrunkPlacerType<?> type() {
        return AEPlacers.HEVEA_TRUNK_PLACER.get();
    }

    @Override
    public List<FoliagePlacer.FoliageAttachment> placeTrunk(LevelSimulatedReader level, BiConsumer<BlockPos, BlockState> setter,
                                                            RandomSource random, int freeTreeHeight, BlockPos pos, TreeConfiguration config) {

        setDirtAt(level, setter, random, pos.below(), config);


        int finalTrunkHeight = freeTreeHeight + random.nextInt(3) - 1;
        if (finalTrunkHeight < 2) finalTrunkHeight = 2;

        for (int y = 0; y < finalTrunkHeight; ++y) {
            placeLog(level, setter, random, pos.above(y), config);
        }

        List<FoliagePlacer.FoliageAttachment> attachments = new ArrayList<>();

        attachments.add(new FoliagePlacer.FoliageAttachment(pos.above(finalTrunkHeight), 0, false));

        return attachments;
    }
}
