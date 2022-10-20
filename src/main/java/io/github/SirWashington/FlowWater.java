package io.github.SirWashington;

import io.github.SirWashington.features.CachedWater;
import io.github.SirWashington.features.FlowFeature;
import io.github.SirWashington.features.PuddleFeature;
import io.github.SirWashington.scheduling.WaterTickScheduler;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidDrainable;
import net.minecraft.block.FluidFillable;
import net.minecraft.client.render.DimensionEffects;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;


public class FlowWater {
    public static int worldMinY = -64;
    public static BlockPos ce24;
    public static ServerWorld world;
    private FlowWater() {
    }

    public static void flowwater(WorldAccess world, BlockPos fluidPos, FluidState state) {

        //System.out.println("new beginning");
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
                equalizeWater(fluidPos, centerlevel);


            }

            //CachedWater.unlock();
        }
    }

    public static boolean testTick(World world, BlockPos BP) {
        world.setBlockState(BP, Blocks.GOLD_BLOCK.getDefaultState(), 11);
        //System.out.println("ticked: " + BP.getX() + ", " + BP.getZ());
        updateNeighbours(BP);
        return false;
    }

    public static void updateNeighbours(BlockPos BP) {
        //ArrayList<BlockPos> neighbours = new ArrayList<>();

        for (Direction dir : Direction.Type.HORIZONTAL) {
            //System.out.println(dir);
            //neighbours.add(BP.offset(dir));
            WaterTickScheduler.scheduleFluidBlock(BP.offset(dir));
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
            //System.out.println("nuffin");
        } else {
            if (isX || isZ || isY) {
                isWithin = false;
            }
        }

        //System.out.println(isWithin);
        return isWithin;
    }


    public static void equalizeWater(BlockPos center, int level) {

        int radius = 2;
        int diameter = (radius * 2) + 1;
        int[][] data = new int[diameter][diameter];
        int[][] newData = new int[diameter][diameter];

        //int centerLevel = level + 10;

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



    public static void waterLoggedFlow(BlockPos fluidPos, BlockState fpBS, ArrayList<BlockPos> blocks) {

        int count = 0;
        boolean nonFullFluidBlock = false;
        int totalWaterLevel = 0;
        int centerWaterLevel = 8;

        for (BlockPos block : blocks) {
            int level = CachedWater.getWaterLevel(block);
            if (level >= 0) {
                count += 1;
                totalWaterLevel += level;
            }
            //System.out.println("sex");
            //System.out.println(level);
            //System.out.println("tot " + totalWaterLevel);
        }
        if (totalWaterLevel <= (count - 1) * 8) {
            nonFullFluidBlock = true;
            //System.out.println("sex2");
        }
        /*
        if (nonFullFluidBlock) {
            while (centerWaterLevel > 0) {
                for (BlockPos block : blocks) {
                    int blockLevel = CachedWater.getWaterLevel(block);
                    if (isNotFull(blockLevel)) {
                        blockLevel += 1;
                        centerWaterLevel -= 1;
                        CachedWater.setWaterLevel(blockLevel, block);
                    }
                }
            }
            CachedWater.setBlockState(fluidPos, fpBS.with(Properties.WATERLOGGED, false));
        }*/
    }

    public static void KelpFlow(BlockPos fluidPos, BlockState fpBS, ArrayList<BlockPos> blocks) {

        int count = 0;
        boolean nonFullFluidBlock = false;
        int totalWaterLevel = 0;
        int centerWaterLevel = 8;

        for (BlockPos block : blocks) {
            BlockState internalBS = CachedWater.getBlockState(block);
            if (internalBS.getBlock() == Blocks.WATER || internalBS.getBlock() == Blocks.AIR) {
                count += 1;
                int level = internalBS.getFluidState().getLevel();
                totalWaterLevel += level;
            }
            //System.out.println("sex");
            int level = CachedWater.getWaterLevel(block);
            //System.out.println(level);
            //System.out.println("tot " + totalWaterLevel);
        }
        if (totalWaterLevel <= (count - 1) * 8) {
            nonFullFluidBlock = true;
            //System.out.println("sex2");
        }
        if (nonFullFluidBlock) {
            world.breakBlock(fluidPos, true);
        }
    }
}
