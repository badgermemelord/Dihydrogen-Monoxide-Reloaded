package io.github.SirWashington.features;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

public class FlowFeatureInfinite {
    public static void execute(BlockPos center) {
        if (!Features.FLOW_FEATURE_ENABLED) return;

        for (Direction dir : Direction.Plane.HORIZONTAL) {
            if(CachedWater.isNotFull(center.relative(dir))) {
                CachedWater.setWaterLevel(8, center.relative(dir));
                //System.out.println("set flowfeature");
            };
        }
    }
}
