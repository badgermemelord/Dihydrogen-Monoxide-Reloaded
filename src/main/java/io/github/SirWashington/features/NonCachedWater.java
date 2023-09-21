package io.github.SirWashington.features;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;


public class NonCachedWater {

    public static boolean addVolume(int volume, BlockPos pos, World world) {
        setup(world);
        try {
            CachedWater.addVolume(volume, pos);
            return true;
        } catch (IllegalStateException e) {
            return false;
        } finally {
            unSetup();
        }
    }

    private static void setup(World world) {
        CachedWater.useCache = false;
        CachedWater.useSections = false;
        CachedWater.cacheWorld = world;
    }

    private static void unSetup() {
        CachedWater.useCache = true;
        CachedWater.useSections = true;
    }
}
