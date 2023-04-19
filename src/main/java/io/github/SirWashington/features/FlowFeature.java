package io.github.SirWashington.features;

import io.github.SirWashington.scheduling.ChunkHandlingMethods;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class FlowFeature {

    public static BlockPos[] blocks = new BlockPos[4];

    public static void execute(BlockPos center, World world) {
        if (!Features.FLOW_FEATURE_ENABLED) return;

        Boolean didSomething = false;

        for (Direction dir : Direction.Type.HORIZONTAL) {
            blocks[CachedWater.countMa()%4] = (center.offset(dir));
        }

        int[] waterLevels = new int[4];
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
                        didSomething = true;
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
        if (didSomething) {
            for (int i = 0; i < 4; i++) {
                CachedWater.setWaterLevel(waterLevels[i], blocks[i]);
            }
        }
        else {
            ChunkHandlingMethods.subtractTickTicket(center, world);
        }

        CachedWater.setWaterLevel(level, center);
    }

}