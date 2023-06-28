package io.github.SirWashington;

import io.github.SirWashington.features.*;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldAccess;

import static io.github.SirWashington.properties.WaterFluidProperties.ISFINITE;


public class FlowWater {
    public static int worldMinY = -64;
    public static ServerWorld world;
    private FlowWater() {
    }

    public static void flowWater(WorldAccess world, BlockPos fluidPos, FluidState state) {
        if (fluidPos.getY() == worldMinY) {
            // TODO INSECURE
            CachedWater.setWaterVolume(0, fluidPos);
        } else {
            FlowWater.world = (ServerWorld) world;
            CachedWater.setup(FlowWater.world, fluidPos);
            FlowFeatureHR.execute(fluidPos);
        }
    }
}
