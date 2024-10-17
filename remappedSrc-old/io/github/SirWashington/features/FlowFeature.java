package io.github.SirWashington.features;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

public class FlowFeature {

    public static BlockPos[] blocks = new BlockPos[4];

    public static void execute(BlockPos center) {
        if (!Features.FLOW_FEATURE_ENABLED) return;

        // What is this arraylist?

        for (Direction dir : Direction.Plane.HORIZONTAL) {
            blocks[CachedWater.countMa()%4] = (center.relative(dir));
        }

        int[] waterLevels = new int[4];
        //Arrays.fill(waterLevels, -1);
        int level = CachedWater.getWaterLevel(center);
        for (int i = 0; i < 4; i++) {
            waterLevels[i] = CachedWater.getWaterLevel(blocks[i]);
        }
        int count = 0;
        int internalLevel;

        while (count < 4) {
            for (int i = 0; i < 4; i++) {
                internalLevel = waterLevels[i];
                if (internalLevel != -1) {
                    if ((level >= (internalLevel + 1))) {
                        internalLevel += 1;
                        waterLevels[i] = internalLevel;
                        level -= 1;
                    } else {
                        count += 1;
                    }
                } else {
                    count += 1;
                }
            }
        }
        for (int i = 0; i < 4; i++) {
            CachedWater.setWaterLevel(waterLevels[i], blocks[i]);
        }
        CachedWater.setWaterLevel(level, center);
    }

}