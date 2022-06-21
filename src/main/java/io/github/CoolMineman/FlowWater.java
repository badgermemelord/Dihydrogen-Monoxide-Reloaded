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
import net.minecraft.world.chunk.ChunkSection;

import static net.minecraft.util.math.ChunkSectionPos.*;


public class FlowWater {
    private FlowWater() {
    }


    public static int borX = 0;
    public static int borZ = 0;
    public static int borY = 0;
    public static int fpY = 0;
    public static int worldMinY = -64;
    public static ChunkSection[] SectionList = new ChunkSection[8];
    public static BlockPos ce24;


    public static void flowwater(WorldAccess world, BlockPos fluidPos, FluidState state) {

        System.out.println("new beginning");
        if (fluidPos.getY() == worldMinY) {
            world.setBlockState(fluidPos, Blocks.AIR.getDefaultState(), 11);
        }
        else {
            chunkFetcher(world, fluidPos);
            sectionGetBlockState(fluidPos);

            int centerlevel = getWaterLevel(fluidPos, world);
            if (sectionGetBlockState(fluidPos).getBlock() instanceof FluidFillable) {
                return;
            }
            if ((sectionGetBlockState(fluidPos.down()).canBucketPlace(Fluids.WATER)) && (getWaterLevel(fluidPos.down(), world) != 8)) {

                world.setBlockState(fluidPos, Blocks.AIR.getDefaultState(), 11);
                addWater(centerlevel, fluidPos.down(), world);
            } else {
                ArrayList<BlockPos> blocks = new ArrayList<>(4);
                for (Direction dir : Direction.Type.HORIZONTAL) {
                    blocks.add(fluidPos.offset(dir));
                }
                blocks.removeIf(pos -> !sectionGetBlockState(pos).canBucketPlace(Fluids.WATER));
                Collections.shuffle(blocks);
                equalizeWater(blocks, fluidPos, world, centerlevel);
            }
        }
    }

    public static void chunkFetcher(WorldAccess world, BlockPos fluidPos) {

        System.out.println("SUGOMA NUTZ");
        int gmr = 4; //generalMaxRange, the maximum range that will ever be used in checks
        int posX = fluidPos.getX();
        int posY = fluidPos.getY();
        int posZ = fluidPos.getZ();
        int secY;
        int secY2;
        secY = (posY + 64) / 16;
        secY2 = (posY + 63) / 16;
        System.out.println("sec " + secY);

        boolean coc = true;
        boolean cac = true;
        boolean cec = true;
        boolean bals = false;
        if (coc || cac || cec) {
            bals = true;
        }

        System.out.println("bals: " + bals);

        //Clockwise corner calculation (8 corners for a cuboid)

        BlockPos[] cornerList = new BlockPos[4];

        BlockPos c0 = fluidPos;
        BlockPos c2 = fluidPos.add(0,-1,0);
        BlockPos c11 = fluidPos.add(gmr,0,gmr);
        cornerList[0] = c11;
        BlockPos c12 = fluidPos.add(-gmr,0,gmr);
        cornerList[1] = c12;
        BlockPos c13 = fluidPos.add(gmr,0,-gmr);
        cornerList[2] = c13;
        BlockPos c14 = fluidPos.add(-gmr,0,-gmr);
        cornerList[3] = c14;

        BlockPos c21 = fluidPos.add(gmr,-1,gmr);
        BlockPos c22 = fluidPos.add(-gmr,-1,gmr);
        BlockPos c23 = fluidPos.add(gmr,-1,-gmr);
        BlockPos c24 = fluidPos.add(-gmr,-1,-gmr);
        ce24 = fluidPos.add(-gmr,-1,-gmr);

        //Getting the starting chunks
        ChunkSection s1 = world.getChunk(c0).getSection(secY);
        ChunkSection s2 = world.getChunk(c2).getSection(secY2);


        //Calculating whether the corners are inside the chunk, either distance > Dist or distance < -relc0
        boolean isc11in = isWithinChunk(c11, fluidPos);
        boolean isc12in = isWithinChunk(c12, fluidPos);
        boolean isc13in = isWithinChunk(c13, fluidPos);
        boolean isc14in = isWithinChunk(c14, fluidPos);

        boolean isc21in = isWithinChunk(c21, fluidPos);
        boolean isc22in = isWithinChunk(c22, fluidPos);
        boolean isc23in = isWithinChunk(c23, fluidPos);
        boolean isc24in = isWithinChunk(c24, fluidPos);

        ChunkSection s11 = null;
        ChunkSection s12 = null;
        ChunkSection s13 = null;
        ChunkSection s14 = null;
        ChunkSection s21 = null;
        ChunkSection s22 = null;
        ChunkSection s23 = null;
        ChunkSection s24 = null;

        //Getting the chunks of the corners

        //s11 = s1;
        //SectionList[0] = s11;

        if (isc11in == false) {
            s11 = world.getChunk(c11).getSection(secY);
            SectionList[0] = s11;
            System.out.println("fetched s11");
        }
        if (isc12in == false) {
            s12 = world.getChunk(c12).getSection(secY);
            SectionList[1] = s12;
            System.out.println("fetched s12");
        }
        if (isc13in == false) {
            s13 = world.getChunk(c13).getSection(secY);
            SectionList[2] = s13;
            System.out.println("fetched s13");
        }
        if (isc14in == false) {
            s14 = world.getChunk(c14).getSection(secY);
            SectionList[3] = s14;
            System.out.println("fetched s14");
        }
        System.out.println("break");
        if (isc21in == false) {
             s21 = world.getChunk(c21).getSection(secY-1);
            SectionList[4] = s21;
            System.out.println("fetched s21");
        }
        if (isc22in == false) {
             s22 = world.getChunk(c22).getSection(secY-1);
            SectionList[5] = s22;
            System.out.println("fetched s22");
        }
        if (isc23in == false) {
             s23 = world.getChunk(c23).getSection(secY-1);
            SectionList[6] = s23;
            System.out.println("fetched s23");
        }
        if (isc24in == false) {
             s24 = world.getChunk(c24).getSection(secY-1);
            SectionList[7] = s24;
            System.out.println("fetched s24");
        }



        //Calculating the border coordinates of the s11 chunk

        int relX;
        int relZ;
        int relY;

        if (c11.getX() > 0) {
             relX = c11.getX() % 16;
        }
        else {
             relX =  16 + c11.getX() % 16;
        }
        if (c11.getZ() > 0) {
             relZ = c11.getZ() % 16;
        }
        else {
             relZ =  16 + c11.getZ() % 16;
        }
        if (c11.getY() > 0) {
            relY = c11.getY() % 16;
        }
        else {
            relY =  16 + c11.getY() % 16;
        }

        int fpX = c11.getX();
        int fpZ = c11.getZ();
        int fpYa = c11.getY();
        int restX = 15 - relX;
        int restZ = 15 - relZ;
        int restY = 15 - relY;

        borX = 0;
        borZ = 0;
        borY = 0;

        borX = fpX - relX;
        borZ = fpZ - relZ;
        borY = fpYa - relY;


        fpY = fluidPos.getY();

        //Adding origin chunk if missing

        int oriID = getOriSectionID(fluidPos);
        int oriBelowID = oriID - 4;


        if (SectionList[oriID] == null) {
            SectionList[oriID] = s1;
            //System.out.println("aa + " + oriID);
            //System.out.println("aaaa");
        }
/*
        BlockPos cornerPos = new BlockPos(borX, fluidPos.getY(), borZ);
        System.out.println("bpX: " + cornerPos.getX() + " bpZ: " + cornerPos.getZ());

        BlockState bla = sectionGetBlockState(fluidPos);
        int levele = bla.getFluidState().getLevel();
        System.out.println("levele: " + levele);
*/



    }


    public static BlockState sectionGetBlockState(BlockPos pos) {

        String sectionName = "";
        int sectionID = 0;
        BlockState internalBS;

        int posX = pos.getX();
        int posZ = pos.getZ();
        int posY = pos.getY();
        System.out.println("bors XYZ: " + borX + " " + borY + " " + borZ);
        System.out.println("x: " + posX + " z: " + posZ + " y: " + posY);

        if (posX < borX) {
            if (posZ < borZ) {
                if (posY >= borY) {
                    sectionName = "14";
                    sectionID = 3;
                }
                else {
                    sectionName = "24";
                    sectionID = 7;
                }
            }
            else {
                if (posY >= borY) {
                    sectionName = "12";
                    sectionID = 1;
                }
                else {
                    sectionName = "22";
                    sectionID = 5;
                }
            }
        }
        else {
            if (posZ < borZ) {
                if (posY >= borY) {
                    sectionName = "13";
                    sectionID = 2;
                }
                else {
                    sectionName = "23";
                    sectionID = 6;
                }
            }
            else {
                if (posY >= borY) {
                    sectionName = "11";
                    sectionID = 0;
                }
                else {
                    sectionName = "21";
                    sectionID = 4;
                }
            }
        }

        //Getting relative position of pos
        int relX;
        int relZ;
        int relY;
        int relZ2;

        if (pos.getX() >= 0) {
            relX = pos.getX() % 16;
        }
        else {
            relX =  16 + pos.getX() % 16;
            if ((pos.getX() % 16) == 0) {
                relX = 0;
            }

        }
        if (pos.getZ() >= 0) {
            relZ = pos.getZ() % 16;
        }
        else {
            relZ =  16 + pos.getZ() % 16;
            if ((pos.getZ() % 16) == 0)  {
                relZ = 0;
            }
        }
        if (posY >= 0) {
            relY = posY % 16;
        }
        else {
            posY = posY + 64;
            relY = posY % 16;
        }

        System.out.println("sectionID: " + sectionID + " sectionName : " + sectionName);
        System.out.println("rel coords: " + relX + " " + relY + " " + relZ);
        ChunkSection internalCS = SectionList[sectionID];

        internalBS = internalCS.getBlockState(relX, relY, relZ);
        //System.out.println("BS: " + internalBS.getBlock());

        return internalBS;
    }

    public static void sectionSetBlockState(BlockPos pos, BlockState state) {

        String sectionName = "";
        int sectionID = 0;
        BlockState newBS = state;

        int posX = pos.getX();
        int posZ = pos.getZ();
        int posY = pos.getY();
        //System.out.println("bors XYZ: " + borX + " " + borY + " " + borZ);
        //System.out.println("x: " + posX + " z: " + posZ + " y: " + posY);

        if (posX < borX) {
            if (posZ < borZ) {
                if (posY >= borY) {
                    sectionName = "14";
                    sectionID = 3;
                }
                else {
                    sectionName = "24";
                    sectionID = 7;
                }
            }
            else {
                if (posY >= borY) {
                    sectionName = "12";
                    sectionID = 1;
                }
                else {
                    sectionName = "22";
                    sectionID = 5;
                }
            }
        }
        else {
            if (posZ < borZ) {
                if (posY >= borY) {
                    sectionName = "13";
                    sectionID = 2;
                }
                else {
                    sectionName = "23";
                    sectionID = 6;
                }
            }
            else {
                if (posY >= borY) {
                    sectionName = "11";
                    sectionID = 0;
                }
                else {
                    sectionName = "21";
                    sectionID = 4;
                }
            }
        }

        //Getting relative position of pos
        int relX;
        int relZ;
        int relY;

        if (pos.getX() > 0) {
            relX = pos.getX() % 16;
        }
        else {
            relX =  16 + pos.getX() % 16;
        }
        if (pos.getZ() > 0) {
            relZ = pos.getZ() % 16;
        }
        else {
            relZ =  16 + pos.getZ() % 16;
        }
        if (posY > 0) {
            relY = posY % 16;
        }
        else {
            relY =  16 + posY % 16;
        }

        //System.out.println("sectionID: " + sectionID + " sectionName : " + sectionName);
        ChunkSection internalCS = SectionList[sectionID];

        internalCS.setBlockState(relX, relY, relZ, newBS);
        //System.out.println("tried to set " + newBS.getBlock());

    }

    public static int getOriSectionID(BlockPos pos) {

        String sectionName = "";
        int sectionID = 0;

        int posX = pos.getX();
        int posZ = pos.getZ();
        int posY = pos.getY();
        //System.out.println("Border XYZ: " + borX + " " + borY + " " + borZ);
        //System.out.println("Origin x: " + posX + " z: " + posZ + " y: " + posY);

        if (posX < borX) {
            if (posZ < borZ) {
                if (posY >= borY) {
                    sectionName = "14";
                    sectionID = 3;
                }
                else {
                    sectionName = "24";
                    sectionID = 7;
                }
            }
            else {
                if (posY >= borY) {
                    sectionName = "12";
                    sectionID = 1;
                }
                else {
                    sectionName = "22";
                    sectionID = 5;
                }
            }
        }
        else {
            if (posZ < borZ) {
                if (posY >= borY) {
                    sectionName = "13";
                    sectionID = 2;
                }
                else {
                    sectionName = "23";
                    sectionID = 6;
                }
            }
            else {
                if (posY >= borY) {
                    sectionName = "11";
                    sectionID = 0;
                }
                else {
                    sectionName = "21";
                    sectionID = 4;
                }
            }
        }


        //System.out.println(" ORIGIN sectionID: " + sectionID + " sectionName : " + sectionName);

        return sectionID;

    }

    public static boolean isWithinChunk(BlockPos pos, BlockPos origin) {

        //System.out.println("pos" + pos.getY());
        boolean isWithin = true;
        boolean isX = false;
        boolean isY = false;
        boolean isZ = false;

        System.out.println("pos " + pos);
        System.out.println("ce24 " + ce24);
        if(pos.getX() == ce24.getX() && pos.getZ() == ce24.getZ() && pos.getY() == ce24.getY())
        {
            System.out.println("C24 was here");
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
            System.out.println("nuffin");
        }
        else {
            if (isX || isZ || isY) {
                isWithin = false;
            }
        }

        //System.out.println(isWithin);
        return isWithin;
    }


    public static int getWaterLevel(BlockPos pos, WorldAccess world) {
        BlockState blockstate = sectionGetBlockState(pos);
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
            if (!(sectionGetBlockState(pos).getBlock() instanceof FluidFillable)) { // Don't fill kelp etc
                world.setBlockState(pos, Fluids.WATER.getDefaultState().getBlockState(),11);
            }
        } else if (level == 0) {
            world.setBlockState(pos, Blocks.AIR.getDefaultState(), 11);
        } else if (level < 8) {
            world.setBlockState(pos, Fluids.FLOWING_WATER.getFlowing(level, false).getBlockState(),11);
        }  else {
            System.out.println("Can't set water >8 something went very wrong!");
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
                Block internalBlock = sectionGetBlockState(internalPos).getBlock();

                if (internalBlock == Blocks.WATER || internalBlock == Blocks.AIR) {
                    int ilevel = sectionGetBlockState(internalPos).getFluidState().getLevel();
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
            //System.out.println("newData original: " + Arrays.deepToString(newData));

            for (int i  =  0; i < diameter-1; i++) {
                for (int j  =  0; j < diameter-1; j++) {
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
                method2(blocks, center, world, level, data, newData);
            }
            if (range > 1) {
                method1(blocks, center, world);
            }
        }
    }

                //Matrix Check End







    public static void method1(ArrayList<BlockPos> blocks, BlockPos center, WorldAccess world) {

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

    public static void method2(ArrayList<BlockPos> blocks, BlockPos center, WorldAccess world, int level, int[][] data, int[][] newData) {
        //setWaterLevel(level, center, world);
        BlockPos pos = center;

        if (level == 1 && sectionGetBlockState(pos.down()).getBlock() != Blocks.AIR ) {
        //System.out.println(level);
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
            boolean doneExtendedCheck = false;
            boolean doExtendedCheck = false;
            int dataPF[][] = new int[maxDia][maxDia];


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

                    int originalData[][] = new int[5][5];
                    int puddleData[][] = new int[maxDia][maxDia];

                    int matrixRadius;
                    if (currentRadius <= 2) {
                        matrixRadius = 2;
                    }
                    else {
                        matrixRadius = 4;
                        doExtendedCheck = true;
                    }

                    //System.out.println("Current radius: " + currentRadius);
                    //System.out.println("Matrix radius: " + matrixRadius);

                    int relX = dx-x;
                    int absX = relX + matrixRadius;
                    //System.out.println("relX: " + relX + " " + "absX: " + absX);
                    int relZ= dz-z;
                    int absZ = relZ + matrixRadius;


                    if (doExtendedCheck == true && doneExtendedCheck == false) {

                        for (int dx2 = x-maxRadius; dx2 <= x+maxRadius; dx2++) {
                            for (int dz2 = z-maxRadius; dz2 <= z+maxRadius; dz2++) {

                                int relXfp = dx2-x;
                                int absXfp = relXfp + maxRadius;
                                int relZfp = dz2-z;
                                int absZfp = relZfp + maxRadius;

                                BlockPos internalPos = new BlockPos(dx2, y, dz2);
                                Block internalBlock = sectionGetBlockState(internalPos).getBlock();

                                if (internalBlock == Blocks.WATER || internalBlock == Blocks.AIR) {
                                    int ilevel = sectionGetBlockState(internalPos).getFluidState().getLevel();
                                    //System.out.println("dataAir: " + ilevel);
                                    dataPF[absXfp][absZfp] = ilevel;
                                } else {
                                    //System.out.println("dataSolid: -1");
                                    dataPF[absXfp][absZfp] = -1;
                                }
                            }
                        }
                        dataPF = GFG.printma(dataPF, maxDia, maxRadius);
                        doneExtendedCheck = true;
                        //System.out.println("data collected: " + Arrays.deepToString(dataPF));
                    }



                    int currDiameter = (currentRadius * 2) + 1;

                    if (sectionGetBlockState(pos.down()).getBlock() != Blocks.AIR) {
                        //System.out.println("catch 1");
                        BlockPos currentPos = new BlockPos(dx, y, dz);
                        BlockPos checkBelow = currentPos.down();
                        BlockPos newWaterPos = new BlockPos(0, 0, 0);
                        //String direction = "";
                        Direction direction;
                        //Boolean doHop = false;

                        if (checkBelow != pos.down() && currentPos != pos) {
                            //BlockState below =
                            if ((sectionGetBlockState(checkBelow).isAir() == true || (sectionGetBlockState(checkBelow).getBlock() == Blocks.WATER) && sectionGetBlockState(checkBelow).getFluidState().getLevel() != 8)) {
                                //System.out.println("hole found, " + currentRadius);

                                if (matrixRadius == 2) {
                                    //System.out.println("smalhop: " + Arrays.deepToString(newData));
                                    if (newData[absX][absZ] >= 10) {
                                        //System.out.println("newdat: " + newData[absX][absZ]);
                                        doHop = true;
                                    }
                                }

                                if (currentRadius > 2) {
                                    //System.out.println("bighop: "  + Arrays.deepToString(dataPF));
                                    //System.out.println("check 1");
                                    //System.out.println(Arrays.deepToString(dataPF));
                                    if (dataPF[absX][absZ] >= 10) {
                                        //System.out.println("datPF: " + dataPF[absX][absZ]);
                                        //System.out.println("check 2");

                                        doHop = true;
                                    }
                                }

                                //System.out.println("catch 2");
                            }
                            if (doHop == true) {
                                //System.out.println("dohop true");

                                Direction.getFacing(x-dx,0, z-dz);
                                direction = Direction.getFacing(dx - x, 0, dz - z);
                                newWaterPos = pos.offset(direction);

                                //System.out.println("catch 3");
                                if (sectionGetBlockState(pos).getBlock() == Blocks.WATER && newWaterPos.getY() == pos.getY() && sectionGetBlockState(newWaterPos).getBlock() == Blocks.AIR) {
                                    //System.out.println("dir: " + direction);
                                    //System.out.println("jumping");
                                    world.setBlockState(newWaterPos, Fluids.FLOWING_WATER.getFlowing(1, false).getBlockState(),11);
                                    world.setBlockState(pos, Blocks.AIR.getDefaultState(),11);
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
    }
}
