package io.github.SirWashington.features;

import net.minecraft.util.math.BlockPos;

import static java.lang.Integer.signum;

public class FlowFeatureHR {

    public static BlockPos[] blocks = new BlockPos[4];
    public static boolean hasFlowed = false;

    public static boolean execute(BlockPos center) {
        hasFlowed = false;
        if (!Features.FLOW_FEATURE_ENABLED) return false;
        int centerVolume = CachedWater.getWaterVolume(center);
        System.out.println("flowfeature, Pos: " + center + " Volume: " + centerVolume);
        fallFeature(center, centerVolume);
        spreadFeature(center);
        return hasFlowed;
    }
    public static void spreadFeature(BlockPos center) {
        BlockPos[] pair = new BlockPos[2];
        pair[0] = center;
        //TODO make this better, not repeating directions
        for (int a = 0; a < 4; a++) {
            pair[1] = center.offset(CachedWater.getRandomDirection());
            equalisePair(pair);
        }
    }
    public static void equalisePair(BlockPos[] pair) {
        int volumeA = CachedWater.getWaterVolume(pair[0]);
        int volumeB = CachedWater.getWaterVolume(pair[1]);
        int difference = volumeA - volumeB;
        if (volumeA > 0 && volumeB > 0) {
            if (difference >= ConfigVariables.equalisingRate) {
                volumeA -= difference >> ConfigVariables.equalisingDivider;
                volumeB += difference >> ConfigVariables.equalisingDivider;
                hasFlowed = true;
            } else if (difference >= ConfigVariables.minimumFlowDifference) {
                volumeA -= signum(difference);
                volumeB += signum(difference);
                hasFlowed = true;
            }
            CachedWater.setWaterVolume(volumeA, pair[0]);
            CachedWater.setWaterVolume(volumeB, pair[1]);
        }
        else {
            if (volumeA == 0 || volumeB == 0){
                if(difference >> ConfigVariables.equalisingDivider >= ConfigVariables.surfaceTension) {
                    volumeA -= difference >> ConfigVariables.equalisingDivider;
                    volumeB += difference >> ConfigVariables.equalisingDivider;
                    CachedWater.setWaterVolume(volumeA, pair[0]);
                    CachedWater.setWaterVolume(volumeB, pair[1]);
                    hasFlowed = true;
                }
            }
        }
    }
    public static void fallFeature(BlockPos center, int centerVolume) {
        int minY = -63;
        if (center.getY() == minY) {
            CachedWater.setWaterVolume(0, center);
            return;
        }
        if(CachedWater.isNotFull(center.down())) {
            CachedWater.setWaterVolume(0, center);
            CachedWater.addVolume(centerVolume, center.down());
            hasFlowed = true;
        }
    }
}