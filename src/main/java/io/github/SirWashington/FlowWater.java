package io.github.SirWashington;

import java.util.*;

import io.github.SirWashington.features.CachedWater;
import io.github.SirWashington.features.FlowFeature;
import io.github.SirWashington.features.PuddleFeature;
import it.unimi.dsi.fastutil.longs.*;
import net.minecraft.block.*;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.chunk.ChunkSection;


public class FlowWater {
    private FlowWater() {
    }

    public static int worldMinY = -64;
    public static BlockPos ce24;
    public static ServerWorld world;

    public static void flowwater(WorldAccess world, BlockPos fluidPos, FluidState state) {

        //System.out.println("new beginning");
        if (fluidPos.getY() == worldMinY) {
            // TODO INSECURE
            CachedWater.setWaterLevel(0, fluidPos);
        } else {
            FlowWater.world = (ServerWorld) world;
            CachedWater.setup(FlowWater.world, fluidPos);
            CachedWater.lock();

            //sectionGetBlockState(fluidPos);
            BlockState fluidPosState = CachedWater.getBlockState(fluidPos);


            ArrayList<BlockPos> blockse = new ArrayList<>(4);
            for (Direction dir : Direction.Type.HORIZONTAL) {
                blockse.add(fluidPos.offset(dir));
            }

            boolean isFFillable = fluidPosState.getBlock() instanceof FluidFillable;
            boolean isFDrainable = fluidPosState.getBlock() instanceof FluidDrainable;


            if (isFFillable && isFDrainable) {
                //System.out.println("bal2");
                waterLoggedFlow(fluidPos, fluidPosState, blockse);
            }
            if (isFFillable && !isFDrainable) {
                //System.out.println("bal3");
                KelpFlow(fluidPos, fluidPosState, blockse);
            }

            int centerlevel = CachedWater.getWaterLevel(fluidPos);
            if (isFFillable) {
                return;
            }
            if ((CachedWater.getBlockState(fluidPos.down()).canBucketPlace(Fluids.WATER)) && isNotFull(CachedWater.getWaterLevel(fluidPos.down()))) {

                CachedWater.setWaterLevel(0, fluidPos);
                CachedWater.addWater(centerlevel, fluidPos.down());
            } else {
                ArrayList<BlockPos> blocks = new ArrayList<>(4);
                for (Direction dir : Direction.Type.HORIZONTAL) {
                    blocks.add(fluidPos.offset(dir));
                }

                //blocks.removeIf(pos -> !sectionGetBlockState(pos).canBucketPlace(Fluids.WATER));
                Collections.shuffle(blocks);
                equalizeWater(blocks, fluidPos, centerlevel);


            }

            CachedWater.unlock();
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


    public static void equalizeWater(ArrayList<BlockPos> blocks, BlockPos center, int level) {
        int[] waterlevels = new int[4];
        Arrays.fill(waterlevels, -1);
        for (BlockPos block : blocks) {
            waterlevels[blocks.indexOf(block)] = CachedWater.getWaterLevel(block);
        }
/*        int waterlevelsnum = waterlevels.length;
        int didnothings = 0;
        int waterlevel;*/
        // List<Integer> matrixLevels = new ArrayList<>(Arrays.asList());

        //FloodFill Matrix Initiation
        int radius = 2;
        int diameter = (radius * 2) + 1;
        int area = diameter * diameter;
        int data[][] = new int[diameter][diameter];
        int newData[][] = new int[diameter][diameter];

        int centerLevel = level + 10;


        //Matrix Check Start
        int x = center.getX();
        int y = center.getY();
        int z = center.getZ();

        int count = 0;
        int newX = 0;
        int newZ = 0;
        int maxLevel = 0;
        int minLevel = 99;

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                newX = x + dx;
                newZ = z + dz;
                BlockPos internalPos = new BlockPos(newX, y, newZ);
                data[dx + radius][dz + radius] = CachedWater.getWaterLevel(internalPos);
                count += 1;
            }
        }


        if (count == area) {
            //System.out.println("data as sent: " + Arrays.deepToString(data));
            newData = GFG.printma(data, diameter, radius);
            //System.out.println("newData original: " + Arrays.deepToString(newData));

            for (int i = 0; i < diameter - 1; i++) {
                for (int j = 0; j < diameter - 1; j++) {
                    if (newData[i][j] >= 10) {
                        //System.out.println("newdata " + newData[i]);
                        if (newData[i][j] > maxLevel) {
                            maxLevel = newData[i][j];
                        }
                        if (newData[i][j] < minLevel) {
                            minLevel = newData[i][j];
                        }
                    }
                }
            }

            //System.out.println(matrixLevels);

            int range = centerLevel - minLevel;
            //System.out.println("max " + maxLevel);
            //System.out.println("min " + minLevel);
            //System.out.println("range " + range);


            if (range == 1) {
                PuddleFeature.execute(blocks, center, level, data, newData);
            }
            if (range > 1) {
                FlowFeature.execute(blocks, center);
            }
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
        }
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

    public static void method2(ArrayList<BlockPos> blocks, BlockPos center, int level, int[][] data, int[][] newData) {
        //setWaterLevel(level, center, world);
        BlockPos pos = center;

        if (level == 1 && CachedWater.getWaterLevel(pos.down()) != 0) {
            //System.out.println(level);
            int maxRadius = 4;
            int maxDia = (maxRadius * 2) + 1;
            //int maxArea = maxDia * 2;
            int currentRadius = 1;
            int currentDiameter = (2 * currentRadius) + 1;
            int previousRadius = currentRadius - 1;
            int x = pos.getX();
            int y = pos.getY();
            //int uy = pos.getY() - 1;
            int z = pos.getZ();
            int count = 0;
            boolean didJump = false;
            int dx;
            int dz;
            boolean addZ = false;
            boolean doHop = false;
            int perim = 4 * (currentDiameter - 1);

            int dataPF[][] = new int[maxDia][maxDia];

            int matrixRadius;

            //puddle feature start
            System.out.println("break 1");
            //System.out.println("loop start");
            for (dx = x - currentRadius, dz = z - currentRadius; !didJump && dx <= x + maxRadius && dz <= z + maxRadius; ) {
                System.out.println("break 2");
                BlockPos currentPos = new BlockPos(dx, y, dz);


                /*System.out.println("original pos: " + pos);
                System.out.println("loop restart");
                System.out.println("initial count " + count);
                System.out.println("didjump 1 " + didJump);*/
                System.out.println("didjump " + didJump);
                if (!didJump) {
                    System.out.println("break 2.5");
                    //System.out.println("didjump 2 " + didJump);
                    boolean b = dx > x + previousRadius || dx < x - previousRadius;
                    if (!(b || dz > z + previousRadius || dz < z - previousRadius)) {
                        dz = z + currentRadius;
                    } else {
                        addZ = true;
                    }

                    //code start

                    matrixRadius = 4;

                    System.out.println("break 3");

                    //System.out.println("Current radius: " + currentRadius);
                    //System.out.println("Matrix radius: " + matrixRadius);

                    int relX = dx - x;
                    int absX = relX + matrixRadius;
                    //System.out.println("relX: " + relX + " " + "absX: " + absX);
                    int relZ = dz - z;
                    int absZ = relZ + matrixRadius;

                    for (int dx2 = x - maxRadius; dx2 <= x + maxRadius; dx2++) {
                        for (int dz2 = z - maxRadius; dz2 <= z + maxRadius; dz2++) {
                            //System.out.println("break 4");

                            int relXfp = dx2 - x;
                            int absXfp = (relXfp + maxRadius);
                            int relZfp = dz2 - z;
                            int absZfp = (relZfp + maxRadius);

                            BlockPos internalPos = new BlockPos(dx2, y, dz2);
                            int ilevel = CachedWater.getWaterLevel(internalPos);
                            dataPF[absXfp][absZfp] = ilevel;
                        }
                    }
                    //dataPF = PathfinderBFS.distanceMapperBFS(dataPF);

                    System.out.println("break 5");

                    System.out.println("break 6");

                    System.out.println("currentPos " + currentPos);
                    BlockPos checkBelow = currentPos.down();
                    BlockPos newWaterPos = new BlockPos(0, 0, 0);
                    //String direction = "";
                    Direction direction = Direction.NORTH;
                    //Boolean doHop = false;

                    if (currentPos != pos) {
                        System.out.println("break 7");

                        if (isNotFull(CachedWater.getWaterLevel(checkBelow))) {
                            System.out.println("break 8");

                            System.out.println("dx: " + dx);
                            System.out.println("dz: " + dz);

                            int cx = dx - x + 4;
                            int cz = dz - z + 4;

                            if (CachedWater.getWaterLevel(currentPos) == 0) {
                                System.out.println("cx: " + cx);
                                System.out.println("cz: " + cz);
                                dataPF[4][4] = 255;
                            }
                            dataPF[cx][cz] = -2;
                            System.out.println("start");
                            for (int aa = 0; aa < dataPF.length; aa++) {
                                for (int bb = 0; bb < dataPF.length; bb++) {
                                    System.out.print(dataPF[aa][bb] + " ");
                                }
                                System.out.println();
                            }
                            System.out.println("end");

                            dataPF = PathfinderBFS.distanceMapperBFS(dataPF, cx, cz);

                            int minDistance = 255;

                            if (dataPF[cx][cz + 1] < minDistance && dataPF[cx][cz + 1] > 0) {
                                minDistance = dataPF[cx][cz + 1];
                                direction = Direction.NORTH;
                            }
                            if (dataPF[cx + 1][cz] < minDistance && dataPF[cx + 1][cz] > 0) {
                                minDistance = dataPF[cx + 1][cz];
                                direction = Direction.EAST;
                            }
                            if (dataPF[cx][cz - 1] < minDistance && dataPF[cx][cz - 1] > 0) {
                                minDistance = dataPF[cx][cz - 1];
                                direction = Direction.SOUTH;
                            }
                            if (dataPF[cx - 1][cz] < minDistance && dataPF[cx - 1][cz] > 0) {
                                minDistance = dataPF[cx - 1][cz];
                                direction = Direction.WEST;
                            }
                            System.out.println("mindist: " + minDistance);
                            if (minDistance > 0 && minDistance < 255) {
                                doHop = true;
                            } else {
                                doHop = false;
                            }


                        }
                        System.out.println("break 9");

                        if (doHop) {
                            System.out.println("break 10");

                            //System.out.println("dohop true");
                            newWaterPos = pos.offset(direction);
                            System.out.println("dir " + direction);
                            //System.out.println("catch 3");
                            int waterlevelPos = CachedWater.getWaterLevel(pos);
                            if (waterlevelPos > 0 && newWaterPos.getY() == pos.getY() && CachedWater.getWaterLevel(newWaterPos) == 0) {
                                //System.out.println("dir: " + direction);
                                //System.out.println("jumping");
                                CachedWater.setWaterLevel(1, newWaterPos);
                                CachedWater.setWaterLevel(0, pos);
                                didJump = true;
                                doHop = false;
                                //direction = "";
                                //System.out.println("dir2: " + direction);
                            } else {
                                doHop = false;
                            }
                        }
                    }
                    //System.out.println("dir3: " + direction);
                    //code end

                    if (dz == z + currentRadius) {
                        dz = z - currentRadius;
                        dx += 1;
                        addZ = false;
                    } else if (addZ) {
                        dz += 1;
                        addZ = false;
                    }

                    //radius stuff
                    count += 1;
                        /*System.out.println("count2: " + count);
                        System.out.println("count: " + count);
                        System.out.println("perim: " + perim);*/
                    if (count == perim && (currentRadius + 1 <= maxRadius)) {
                        //System.out.println("expanded radius");
                        currentRadius += 1;
                        count = 0;
                        //System.out.println("reset count: " + count);
                        dx = x - currentRadius;
                        dz = z - currentRadius;
                    }
                    currentDiameter = (2 * currentRadius) + 1;
                    perim = 4 * (currentDiameter - 1);
                    //System.out.println("perim: " + perim);
                }
            }
        }
    }
}
