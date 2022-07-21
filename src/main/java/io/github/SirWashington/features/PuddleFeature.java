package io.github.SirWashington.features;

import io.github.SirWashington.PathfinderBFS;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.ArrayList;

public class PuddleFeature {

    public static void execute(ArrayList<BlockPos> blocks, BlockPos center, int level, int[][] data, int[][] newData) {
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

                        if (CachedWater.isNotFull(CachedWater.getWaterLevel(checkBelow))) {
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
