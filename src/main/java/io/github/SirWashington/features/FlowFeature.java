package io.github.SirWashington.features;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class FlowFeature {

    // What is this arraylist?
    public static void execute(BlockPos center) {
        if (!Features.FLOW_FEATURE_ENABLED) return;

        ArrayList<BlockPos> blocks = new ArrayList<>(4);
        for (Direction dir : Direction.Type.HORIZONTAL) {
            blocks.add(center.offset(dir));
        }
        Collections.shuffle(blocks);

        int[] waterlevels = new int[4];
        Arrays.fill(waterlevels, -1);
        int centerwaterlevel = CachedWater.getWaterLevel(center);
        for (int i = 0; i < blocks.size(); i++) {
            waterlevels[i] = CachedWater.getWaterLevel(blocks.get(i));
        }
        int waterlevelsnum = waterlevels.length;
        int didnothings = 0;
        int waterlevel;

        while (didnothings < waterlevelsnum) {
            for (int i = 0; i < 4; i++) {
                waterlevel = waterlevels[i];
                if (waterlevel != -1) {
                    if ((centerwaterlevel >= (waterlevel + 1))) {
                        waterlevel += 1;
                        waterlevels[i] = waterlevel;
                        centerwaterlevel -= 1;
                    } else {
                        didnothings += 1;
                    }
                } else {
                    didnothings += 1;
                }
            }
        }
        for (int i = 0; i < blocks.size(); i++) {
            CachedWater.setWaterLevel(waterlevels[i], blocks.get(i));
        }
        CachedWater.setWaterLevel(centerwaterlevel, center);
    }

}