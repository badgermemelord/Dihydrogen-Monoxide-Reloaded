package io.github.SirWashington.features;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;


public class NonCachedWater {

    public static boolean addWater(int level, BlockPos pos, ServerWorld world) {
        CachedWater.useCache = false;
        CachedWater.useSections = false;
        CachedWater.world = world;
        try {
            CachedWater.addWater(level, pos);
            return true;
        } catch (IllegalStateException e) {
            return false;
        }
    }

    public static int getLevel(BlockPos pos, ServerWorld world) {
        setup(world);
        try {
            return CachedWater.getWaterLevel(pos);
        } finally {
            unSetup();
        }
    }

    private static void setup(ServerWorld world) {
        CachedWater.useCache = false;
        CachedWater.useSections = false;
        CachedWater.world = world;
    }

    private static void unSetup() {
        CachedWater.useCache = true;
        CachedWater.useSections = true;
    }
}
