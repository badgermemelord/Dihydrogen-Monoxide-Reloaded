package io.github.SirWashington.features;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;

public class SpecialFlow {


    static int maxPistonPushingDistance = 8;
    public static boolean tryPushWater(ServerLevel level, BlockPos origin, Direction direction) {
        int maxDistance = maxPistonPushingDistance;
        BlockPos newPos = origin.relative(direction);
        int currentDistance = 0;
        int volumeToDisplace = NonCachedWater.getWaterLevel(newPos, level);

        while (volumeToDisplace > 0 && currentDistance < maxDistance) {
            newPos = newPos.relative(direction);
            currentDistance++;
            volumeToDisplace = NonCachedWater.addWaterLevelAndReturnRemaining(newPos, volumeToDisplace, level);
        }

        return volumeToDisplace == 0;
    }

    public static boolean checkIfCanPushWater(ServerLevel level, BlockPos origin, Direction direction) {
        int maxDistance = maxPistonPushingDistance;
        BlockPos newPos = origin.relative(direction);
        int currentDistance = 0;
        int volumeToDisplace = NonCachedWater.getWaterLevel(newPos, level);



        while (volumeToDisplace > 0 && currentDistance < maxDistance) {
            newPos = newPos.relative(direction);
            if (NonCachedWater.getWaterLevel(newPos, level) < 0) {
                return false;
            }
            currentDistance++;
            volumeToDisplace = NonCachedWater.addWaterLevelAndReturnRemainingImaginary(newPos, volumeToDisplace, level);
        }

        return volumeToDisplace == 0;
    }
}
