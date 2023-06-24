package io.github.SirWashington.features;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class FlowFeatureHR {

    public static BlockPos[] blocks = new BlockPos[4];

    public static void execute(BlockPos center) {
        if (!Features.FLOW_FEATURE_ENABLED) return;

        // What is this arraylist?

        for (Direction dir : Direction.Type.HORIZONTAL) {
            blocks[CachedWater.countMa()%4] = (center.offset(dir));
        }

        int[] waterVolumes = new int[4];
        //Arrays.fill(waterVolumes, -1);
        int volume = CachedWater.getWaterVolume(center);
        for (int i = 0; i < 4; i++) {
            waterVolumes[i] = CachedWater.getWaterVolume(blocks[i]);
        }

        int count = 0;
        int internalVolume;
        int iterations = 10;
        int adjacentVolume;

        for (int i = 0; i < 4; i++) {
            adjacentVolume = waterVolumes[i];
            if (adjacentVolume >= 0) {
                if (volume > adjacentVolume + 1) {
                    waterVolumes[i] += 1;
                    volume -=1;
                }
            }
        }



/*        while (count < 4) {
            for (int i = 0; i < 4; i++) {
                internalVolume = waterVolumes[i];
                if (internalVolume != -1) {
                    if ((volume >= (internalVolume + 2))) {
                        internalVolume += 1;
                        waterVolumes[i] = internalVolume;
                        volume -= 1;
                        System.out.println("internal : " + volume + "iv: " + internalVolume);
                    } else {
                        count += 1;
                    }
                } else {
                    count += 1;
                }
            }
        }*/
        for (int i = 0; i < 4; i++) {
            System.out.println("a " + waterVolumes[i]);
            CachedWater.setWaterVolume(waterVolumes[i], blocks[i]);
        }
        System.out.println("b " + volume);
        CachedWater.setWaterVolume(volume, center);
    }

}