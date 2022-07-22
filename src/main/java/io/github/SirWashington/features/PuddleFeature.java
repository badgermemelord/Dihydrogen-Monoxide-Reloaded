package io.github.SirWashington.features;

import io.github.SirWashington.PathfinderBFS;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.ArrayList;

public class PuddleFeature {

    public static final int PUDDLE_RADIUS = 4;
    public static final int PUDDLE_DIAMETER = PUDDLE_RADIUS * 2 + 1;
    private static BlockPos pos;
    private static int bfsMatrix[][] = new int[PUDDLE_DIAMETER][PUDDLE_DIAMETER];
    public static void execute(ArrayList<BlockPos> blocks, BlockPos center, int level, int[][] data, int[][] newData) {
        //setWaterLevel(level, center, world);
        pos = center;

        if (level == 1 && CachedWater.getWaterLevel(pos.down()) != 0) {
            int x = pos.getX();
            int y = pos.getY();
            int z = pos.getZ();

            // fill in the bfsMatrix
            int xX = x - PUDDLE_RADIUS;
            int zZ = z - PUDDLE_RADIUS;
            for (int iX = 0; iX < PUDDLE_DIAMETER; iX++) {
                for (int iZ = 0; iZ < PUDDLE_DIAMETER; iZ++) {
                    BlockPos internalPos = new BlockPos(iX + xX, y, iZ + zZ);
                    bfsMatrix[iX][iZ] = CachedWater.getWaterLevel(internalPos) == 0 ? 0 : -1;
                }
            }


            //union start
            for (int currentRadius = 1; currentRadius <= PUDDLE_RADIUS; currentRadius++) {
                int xL = x - currentRadius;
                int xR = x + currentRadius;
                int zT = z + currentRadius;
                int zB = z - currentRadius;

                BlockPos found;
                if ((found = testLine(xL, zT, xR, zT)) == null)
                    if ((found = testLine(xL, zB, xR, zB)) == null)
                        if ((found = testLine(xL, zB, xL, zT)) == null)
                            if ((found = testLine(xR, zB, xR, zT)) == null)
                                continue;

                bfsMatrix[4][4] = -3;

                int cx = found.getX() - xX;
                int cz = found.getZ() - zZ;
                holeFound(cx, cz);
            }
        }
    }

    private static void holeFound(int cx, int cz) {
        bfsMatrix[cx][cz] = -2;

        for(int a = bfsMatrix.length - 1; a >= 0; a--) {
            for(int b = bfsMatrix.length - 1; b >= 0; b--) {
                System.out.print((bfsMatrix[b][a] == 0 ? " " : "") + bfsMatrix[b][a] + " ");
            }
            System.out.println();
        }

        int[][] result = PathfinderBFS.distanceMapperBFS(bfsMatrix, cx, cz);

        // print result of bfs
        for(int a = result.length - 1; a >= 0; a--) {
            for(int b = result.length - 1; b >= 0; b--) {
                System.out.print(" " + result[b][a] + " ");
            }
            System.out.println();
        }

        int minDistance = 255;
        Direction direction = null;

        if (result[4][3] < minDistance && result[4][3] > 0) {
            minDistance = result[4][3];
            direction = Direction.NORTH;
        }
        if (result[3][4] < minDistance && result[3][4] > 0) {
            minDistance = result[3][4];
            direction = Direction.WEST;
        }
        if (result[4][5] < minDistance && result[4][5] > 0) {
            minDistance = result[4][5];
            direction = Direction.SOUTH;
        }
        if (result[5][4] < minDistance && result[5][4] > 0) {
            minDistance = result[5][4];
            direction = Direction.EAST;
        }

        if (minDistance <= 4) {
            if (direction == null) return;
            move(direction);
        }
    }

    private static void move(Direction direction) {
        int level = CachedWater.getWaterLevel(pos);
        CachedWater.setWaterLevel(0, pos);
        CachedWater.addWater(level, pos.offset(direction));
    }

    // its actual test rect but ssssh...
    private static BlockPos testLine(int x, int z, int toX, int toZ) {
        BlockPos testPos;

        for (int iX = x; iX <= toX; iX++) {
            for (int iZ = z; iZ <= toZ; iZ++) {
                testPos = new BlockPos(iX, pos.getY() - 1, iZ);
                if (CachedWater.isNotFull(CachedWater.getWaterLevel(testPos))) {
                    return testPos;
                }
            }
        }

        return null;
    }

}
