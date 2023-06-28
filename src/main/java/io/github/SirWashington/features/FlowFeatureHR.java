package io.github.SirWashington.features;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import static java.lang.Integer.signum;

public class FlowFeatureHR {

    public static BlockPos[] blocks = new BlockPos[4];

    public static void execute(BlockPos center) {
        if (!Features.FLOW_FEATURE_ENABLED) return;
        int centerVolume = CachedWater.getWaterVolume(center);
        fallFeature(center, centerVolume);
        spreadFeature(center);
    }
    public static void spreadFeature(BlockPos center) {
        BlockPos[] pair = new BlockPos[2];
        pair[0] = center;
        for (int a = 0; a < 4; a++) {
            pair[1] = center.offset(CachedWater.getRandomDirection());
            equalisePair(pair);
        }
    }
    public static void equalisePair(BlockPos[] pair) {
        int volumeA = CachedWater.getWaterVolume(pair[0]);
        int volumeB = CachedWater.getWaterVolume(pair[1]);
        if (volumeA >= 0 && volumeB >= 0) {
            int difference = volumeA - volumeB;
            if (difference >= ConfigVariables.equalisingRate) {
                volumeA -= difference >> ConfigVariables.equalisingDivider;
                volumeB += difference >> ConfigVariables.equalisingDivider;
            } else if (difference >= ConfigVariables.minimumFlowDifference) {
                volumeA -= signum(difference);
                volumeB += signum(difference);
            }
            CachedWater.setWaterVolume(volumeA, pair[0]);
            CachedWater.setWaterVolume(volumeB, pair[1]);
        }
    }
    public static void fallFeature(BlockPos center, int centerVolume) {
        if(CachedWater.isNotFull(center.down())) {
            CachedWater.setWaterVolume(0, center);
            CachedWater.addVolume(centerVolume, center.down());
        }
    }
}