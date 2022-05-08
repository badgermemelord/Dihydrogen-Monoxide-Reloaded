package io.github.CoolMineman;

import java.util.*;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidFillable;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.WaterFluid;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldAccess;
import io.github.CoolMineman.GFG;
import org.lwjgl.system.CallbackI;


public class FlowWater {
    private FlowWater() {
    }


    public static void flowwater(WorldAccess world, BlockPos fluidPos, FluidState state) {
        int centerlevel = getWaterLevel(fluidPos, world);
        if (world.getBlockState(fluidPos).getBlock() instanceof FluidFillable) {
            return;
        }
        if ((world.getBlockState(fluidPos.down()).canBucketPlace(Fluids.WATER)) && (getWaterLevel(fluidPos.down(), world) != 8)) {

            world.setBlockState(fluidPos, Blocks.AIR.getDefaultState(), 11);
            addWater(centerlevel, fluidPos.down(), world);
        } else {
            ArrayList<BlockPos> blocks = new ArrayList<>(4);
            for (Direction dir : Direction.Type.HORIZONTAL) {
                blocks.add(fluidPos.offset(dir));
            }
            blocks.removeIf(pos -> !world.getBlockState(pos).canBucketPlace(Fluids.WATER));
            Collections.shuffle(blocks);
            equalizeWater(blocks, fluidPos, world, centerlevel);
        }
    }

    public static int getWaterLevel(BlockPos pos, WorldAccess world) {
        BlockState blockstate = world.getBlockState(pos);
        FluidState fluidstate = blockstate.getFluidState();
        int waterlevel = 0;
        if (fluidstate.getFluid() instanceof WaterFluid.Still) {
            waterlevel = 8;
        } else if (fluidstate.getFluid() instanceof WaterFluid.Flowing) {
            waterlevel = fluidstate.getLevel();
        }
        return waterlevel;
    }

    public static void setWaterLevel(int level, BlockPos pos, WorldAccess world) {
        if (level == 8) {
            if (!(world.getBlockState(pos).getBlock() instanceof FluidFillable)) { // Don't fill kelp etc
                world.setBlockState(pos, Fluids.WATER.getDefaultState().getBlockState(), 11);
            }
        } else if (level == 0) {
            world.setBlockState(pos, Blocks.AIR.getDefaultState(), 11);
        } else if (level < 8) {
            world.setBlockState(pos, Fluids.FLOWING_WATER.getFlowing(level, false).getBlockState(), 11);
        }  else {
            System.out.println("Can't set water >8 something went very wrong!");
        }
        if (level == 1 && world.getBlockState(pos.down()).getBlock() != Blocks.AIR ) {

            int maxRadius = 4;
            int maxDia = (maxRadius * 2) + 1;
            //int maxArea = maxDia * 2;
            int currentRadius = 1;
            int currentDiameter = (2 * currentRadius) + 1;
            int previousRadius = currentRadius - 1;
            int x = pos.getX();
            int y = pos.getY();
            int uy = pos.getY() - 1;
            int z = pos.getZ();
            int count = 0;
            boolean didJump = false;
            int dx;
            int dz;
            boolean addZ = false;
            Boolean doHop = false;
            int perim = 4*(currentDiameter-1);
            int totalCount =  maxDia*maxDia;


            //puddle feature start

            //System.out.println("loop start");
            for (dx = x - currentRadius, dz = z - currentRadius; didJump == false && dx <= x + maxRadius && dz <= z + maxRadius; ) {

                /*System.out.println("original pos: " + pos);
                System.out.println("loop restart");
                System.out.println("initial count " + count);
                System.out.println("didjump 1 " + didJump);*/
                if (didJump == false) {
                    //System.out.println("didjump 2 " + didJump);
                    if (!(((dx > x + previousRadius || dx < x - previousRadius) || (dz > z + previousRadius || dz < z - previousRadius)) || ((dx > x + previousRadius || dx < x - previousRadius) && (dz > z + previousRadius || dz < z - previousRadius)))) {

                        dz = z + currentRadius;
                    } else {
                        addZ = true;
                    }


                    //code start

                    int currDiameter = (currentRadius * 2) + 1;


                    //System.out.println("current radius: " + currentRadius);
                    //System.out.println("dx: " + dx);
                    //System.out.println("dz: " + dz);

                    if (world.getBlockState(pos.down()).getBlock() != Blocks.AIR) {
                        //System.out.println("catch 1");
                        BlockPos currentPos = new BlockPos(dx, y, dz);
                        BlockPos checkBelow = currentPos.down();
                        BlockPos newWaterPos = new BlockPos(0, 0, 0);
                        String direction = "";
                        //Boolean doHop = false;

                        if (checkBelow != pos.down() && currentPos != pos) {
                            //BlockState below =
                            if ((world.getBlockState(checkBelow).isAir() == true || (world.getBlockState(checkBelow).getBlock() == Blocks.WATER) && world.getFluidState(checkBelow).getLevel() != 8)) {
                                //System.out.println("catch 2");
                                doHop = true;
                            }
                            if (doHop == true) {
                                if (currentPos.getX() > pos.getX()) {
                                    direction = "east";
                                } else {
                                    if (currentPos.getX() < pos.getX()) {
                                        direction = "west";
                                    } else {
                                        if (currentPos.getZ() > pos.getZ()) {
                                            direction = "south";
                                        } else {
                                            if (currentPos.getZ() < pos.getZ()) {
                                                direction = "north";
                                            }
                                        }
                                    }
                                }
                                if (direction.equals("north")) {
                                    newWaterPos = pos.north();
                                }
                                if (direction.equals("south")) {
                                    newWaterPos = pos.south();
                                }
                                if (direction.equals("east")) {
                                    newWaterPos = pos.east();
                                }
                                if (direction.equals("west")) {
                                    newWaterPos = pos.west();
                                }
                                //System.out.println("catch 3");
                                if (world.getBlockState(pos).getBlock() == Blocks.WATER && newWaterPos.getY() == pos.getY() && world.getBlockState(newWaterPos).getBlock() == Blocks.AIR) {
                                    //System.out.println("dir: " + direction);
                                    //System.out.println("jumping");
                                    world.setBlockState(newWaterPos, Fluids.FLOWING_WATER.getFlowing(1, false).getBlockState(), 11);
                                    world.setBlockState(pos, Blocks.AIR.getDefaultState(), 11);
                                    didJump = true;
                                    doHop = false;
                                    direction = "";
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
                        }
                        if (addZ == true) {
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
        //Puddle Feature End
    }








    public static void addWater(int level, BlockPos pos, WorldAccess world) {
        int existingwater = getWaterLevel(pos, world);
        int totalwater = existingwater + level;
        if (totalwater > 8) {
            setWaterLevel(totalwater - 8, pos.up(), world);
            setWaterLevel(8, pos, world);
        } else {
            setWaterLevel(totalwater, pos, world);
        }
    }


    public static void equalizeWater(ArrayList<BlockPos> blocks, BlockPos center, WorldAccess world, int level) {
        int[] waterlevels = new int[4];
        Arrays.fill(waterlevels, -1);
        int centerwaterlevel = level;
        for (BlockPos block : blocks) {
            waterlevels[blocks.indexOf(block)] = getWaterLevel(block, world);
        }
/*        int waterlevelsnum = waterlevels.length;
        int didnothings = 0;
        int waterlevel;*/
       // List<Integer> matrixLevels = new ArrayList<>(Arrays.asList());

        //FloodFill Matrix Initiation
        int radius = 2;
        int diameter = (radius*2)+1;
        int area = diameter*diameter;
        int data[][] = new int[diameter][diameter];
        int newData[] = new int[area];

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
                Block internalBlock = world.getBlockState(internalPos).getBlock();

                    if (internalBlock == Blocks.WATER || internalBlock == Blocks.AIR) {
                        int ilevel = world.getFluidState(internalPos).getLevel();
                        //System.out.println("dataAir: " + ilevel);
                        data[dx+radius][dz+radius] = ilevel;
                        count +=1;
                    }
                    else {
                        //System.out.println("dataSolid: -1");
                        data[dx+radius][dz+radius] = -1;
                        count +=1;
                    }
                }
            }


                        if (count == area) {
                            //System.out.println("data as sent: " + Arrays.deepToString(data));
                            newData = GFG.printma(data, diameter, radius);
                            //System.out.println("newData original: " + Arrays.toString(newData));

                            for (int i  =  0; i < area-1; i++) {
                                if (newData[i] >= 10) {
                                    //System.out.println("newdata " + newData[i]);
                                    if (newData[i] > maxLevel) {
                                        maxLevel = newData[i];
                                    }
                                    if (newData[i] < minLevel) {
                                        minLevel = newData[i];
                                    }
                                }
                            }



                            //System.out.println(matrixLevels);

                            int range = centerLevel - minLevel;
                            //System.out.println("max " + maxLevel);
                            //System.out.println("min " + minLevel);
                            //System.out.println("range " + range);



                            if (range == 1) {
                                method2(blocks, center, world, level);
                            }
                            if (range > 1) {
                                method1(blocks, center, data, world);
                            }
                        }
                    }

                //Matrix Check End







    public static void method1(ArrayList<BlockPos> blocks, BlockPos center, int[][] data, WorldAccess world) {

        int[] waterlevels = new int[4];
        Arrays.fill(waterlevels, -1);
        int centerwaterlevel = getWaterLevel(center, world);
        for (BlockPos block : blocks) {
            waterlevels[blocks.indexOf(block)] = getWaterLevel(block, world);
        }
        int waterlevelsnum = waterlevels.length;
        int didnothings = 0;
        int waterlevel;

        while (didnothings < waterlevelsnum) {
            for (int i = 0; i < 4; i++) {
                waterlevel = waterlevels[i];
                if (waterlevel != -1) {
                    if ((centerwaterlevel >= (waterlevel + 1))) {
                        waterlevel += 1;
                        waterlevels[i] = waterlevel;
                        centerwaterlevel -= 1;
                    } else {
                        didnothings += 1;
                    }
                } else {
                    didnothings += 1;
                }
            }
        }
        for (BlockPos block : blocks) {
            int newwaterlevel = waterlevels[blocks.indexOf(block)];
            setWaterLevel(newwaterlevel, block, world);
        }
        setWaterLevel(centerwaterlevel, center, world);
    }

    public static void method2(ArrayList<BlockPos> blocks, BlockPos center, WorldAccess world, int level) {
        setWaterLevel(level, center, world);
    }
}
