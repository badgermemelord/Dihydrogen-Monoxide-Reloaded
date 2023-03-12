package io.github.SirWashington;

import io.github.SirWashington.features.CachedWater;
import io.github.SirWashington.features.FlowFeature;
import io.github.SirWashington.features.PuddleFeature;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

import java.util.ArrayList;


public class FlowWater {
    public static int worldMinY = -64;
    public static BlockPos ce24;
    public static ServerWorld world;
    private FlowWater() {
    }

    public static void flowWater(WorldAccess world, BlockPos fluidPos, FluidState state) {

        if (fluidPos.getY() == worldMinY) {
            // TODO INSECURE
            CachedWater.setWaterLevel(0, fluidPos);
        } else {
            FlowWater.world = (ServerWorld) world;
            CachedWater.setup(FlowWater.world, fluidPos);
            //CachedWater.lock();

            ArrayList<BlockPos> blockse = new ArrayList<>(4);
            for (Direction dir : Direction.Type.HORIZONTAL) {
                blockse.add(fluidPos.offset(dir));
            }

            int centerlevel = CachedWater.getWaterLevel(fluidPos);
            if ((CachedWater.getBlockState(fluidPos.down()).canBucketPlace(Fluids.WATER)) && isNotFull(CachedWater.getWaterLevel(fluidPos.down()))) {

                CachedWater.setWaterLevel(0, fluidPos);
                CachedWater.addWater(centerlevel, fluidPos.down());
            } else {
                equalizeWater(fluidPos, centerlevel, world);


            }

            //CachedWater.unlock();
        }
    }

    public static boolean isNotFull(int waterLevel) {
        return waterLevel < 8 && waterLevel >= 0;
    }

    public static boolean isWithinChunk(BlockPos pos, BlockPos origin) {

        //System.out.println("pos" + pos.getY());
        boolean isWithin = true;
        boolean isX = false;
        boolean isY = false;
        boolean isZ = false;

        //System.out.println("pos " + pos);
        //System.out.println("ce24 " + ce24);
        if (pos.getX() == ce24.getX() && pos.getZ() == ce24.getZ() && pos.getY() == ce24.getY()) {
            //System.out.println("C24 was here");
        }

        int originSecY = (origin.getY() + 64) / 16;
        int posSecY = (pos.getY() + 64) / 16;


        if (!(origin.getX() >> 4 == pos.getX() >> 4)) {
            isX = true;
        }
        if (!(origin.getY() >> 4 == pos.getY() >> 4)) {
            isY = true;
        }
        if (!(origin.getZ() >> 4 == pos.getZ() >> 4)) {
            isZ = true;
        }

        if (posSecY == originSecY) {
            if (pos.getY() == origin.getY()) {
                if (isX || isZ) {
                    isWithin = false;
                }
            }
        } else {
            if (isX || isZ || isY) {
                isWithin = false;
            }
        }
        return isWithin;
    }


    public static void equalizeWater(BlockPos center, int level, WorldAccess world) {

        int radius = 2;
        int diameter = (radius * 2) + 1;
        int[][] data = new int[diameter][diameter];
        int[][] newData = new int[diameter][diameter];

        int x = center.getX();
        int y = center.getY();
        int z = center.getZ();

        int newX = 0;
        int newZ = 0;
        int minLevel = 99;

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                newX = x + dx;
                newZ = z + dz;
                BlockPos internalPos = new BlockPos(newX, y, newZ);
                data[dx + radius][dz + radius] = CachedWater.getWaterLevel(internalPos);
            }
        }
        newData = FloodFill.flood(data, radius, radius);
        for (int i = 0; i < diameter - 1; i++) {
            for (int j = 0; j < diameter - 1; j++) {
                if (newData[i][j] >= 10) {
                    if (newData[i][j] < minLevel) {
                        minLevel = newData[i][j];
                    }
                }
            }
        }
        int range = level + 10 - minLevel;
        if (range == 1) {
            PuddleFeature.execute(center, level);
        }
        if (range > 1) {
            FlowFeature.execute(center);
        }
    }
}
