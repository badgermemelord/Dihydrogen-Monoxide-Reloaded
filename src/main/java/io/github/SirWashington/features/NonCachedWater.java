package io.github.SirWashington.features;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
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

    public static boolean setWaterLevel(int level, BlockPos pos, Level world) {
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

    public static int getWaterLevel(BlockPos pos, Level world) {
        setup(world);
        try {
            return CachedWater.getWaterLevel(pos);
        } finally {
            unSetup();
        }
    }

    //Pushing related backported code
    public static int addWaterLevelAndReturnRemaining(BlockPos pos, int WaterLevel, Level level) {
        int oldWaterLevel = getWaterLevel(pos, level);
        if (oldWaterLevel < 0) {
            //System.out.println();("Tried to add water WaterLevel to a non-air block");
            return WaterLevel;
        }

        int remainder;
        int newWaterLevel = oldWaterLevel + WaterLevel;
        if (newWaterLevel > 8) {
            setWaterLevel(8, pos, level);
            remainder = addWaterLevelAndReturnRemaining(pos.above(), newWaterLevel - 8, level);
        } else {
            remainder = 0;
            setWaterLevel(newWaterLevel, pos, level);
        }
        return remainder;
    }

    public static int addWaterLevelAndReturnRemainingImaginary(BlockPos pos, int WaterLevel, ServerLevel level) {
        int oldWaterLevel = getWaterLevel(pos, level);
        if (oldWaterLevel < 0) {
            //WaterMod.LOGGER.warn("Tried to add water WaterLevel to a non-air block");
            return WaterLevel;
        }

        int remainder;
        int newWaterLevel = oldWaterLevel + WaterLevel;
        if (newWaterLevel > 8) {
            //setWaterLevel(level, pos, WaterInfo.WaterLevelPerBlock);
            remainder = addWaterLevelAndReturnRemainingImaginary(pos.above(), newWaterLevel - 8, level);
        } else {
            remainder = 0;
            //setWaterLevel(level, pos, newWaterLevel);
        }
        return remainder;
    }

    //END of pushing code

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
