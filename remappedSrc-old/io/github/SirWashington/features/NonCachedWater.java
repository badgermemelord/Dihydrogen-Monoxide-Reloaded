package io.github.SirWashington.features;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;


public class NonCachedWater {

    public static boolean addWater(int level, BlockPos pos, Level world) {
        setup(world);
        try {
            CachedWater.addWater(level, pos);
            return true;
        } catch (IllegalStateException e) {
            return false;
        } finally {
            unSetup();
        }
    }

    public static boolean setLevel(int level, BlockPos pos, Level world) {
        setup(world);
        try {
            CachedWater.setWaterLevel(level, pos);
            return true;
        } catch (IllegalStateException e) {
            return false;
        } finally {
            unSetup();
        }
    }

    public static int getLevel(BlockPos pos, Level world) {
        setup(world);
        try {
            return CachedWater.getWaterLevel(pos);
        } finally {
            unSetup();
        }
    }

    private static void setup(Level world) {
        CachedWater.useCache = false;
        CachedWater.useSections = false;
        CachedWater.world = world;
    }

    private static void unSetup() {
        CachedWater.useCache = true;
        CachedWater.useSections = true;
    }
}
