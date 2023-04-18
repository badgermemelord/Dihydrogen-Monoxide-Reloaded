package io.github.SirWashington.features;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class FlowFeatureInfinite {
    public static void execute(BlockPos center) {
        if (!Features.FLOW_FEATURE_ENABLED) return;

        for (Direction dir : Direction.Type.HORIZONTAL) {
            if(CachedWater.isNotFull(center.offset(dir))) {
                CachedWater.setWaterLevel(8, center.offset(dir));
                //System.out.println("set flowfeature");
            };
        }
    }
}
