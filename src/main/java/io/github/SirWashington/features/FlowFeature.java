package io.github.SirWashington.features;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class FlowFeature {


    public static void execute(BlockPos center) {
        if (!Features.FLOW_FEATURE_ENABLED) return;

        //int randShift = CachedWater.countMa() % 4;
        int x = center.getX();
        int y = center.getY();
        int z = center.getZ();

        // What is this arraylist?
        //ArrayList<BlockPos> blocks = new ArrayList<>(4);

        BlockPos[] blocks = new BlockPos[4];

        //Collections.shuffle(blocks);

        for (Direction dir : Direction.Type.HORIZONTAL) {
            blocks[CachedWater.countMa()%4] = (center.offset(dir));
            //randShift += 1;
        }

        int[] waterLevels = new int[4];
        //Arrays.fill(waterlevels, -1);
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