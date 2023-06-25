package io.github.SirWashington.features;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class FlowFeatureHR {

    public static BlockPos[] blocks = new BlockPos[4];

    public static void execute(BlockPos center) {
        if (!Features.FLOW_FEATURE_ENABLED) return;

        for (Direction dir : Direction.Type.HORIZONTAL) {
            blocks[CachedWater.countMa()%4] = (center.offset(dir));
        }

        int[] waterVolumes = new int[4];

        int volume = CachedWater.getWaterVolume(center);

        for (int i = 0; i < 4; i++) {
            waterVolumes[i] = CachedWater.getWaterVolume(blocks[i]);
        }

        int iterations = CachedWater.volumePerBlock/10;
        int adjacentVolume;

        for (int e = 0; e <= iterations; e++) {
            for (int i = 0; i < 4; i++) {
                adjacentVolume = waterVolumes[i];
                if (adjacentVolume >= 0) {
                    if (volume > adjacentVolume + 1) {
                        waterVolumes[i] += 1;
                        volume -=1;
                    }
                }
            }
        }

        for (int i = 0; i < 4; i++) {
            CachedWater.setWaterVolume(waterVolumes[i], blocks[i]);
        }
        CachedWater.setWaterVolume(volume, center);
    }

}