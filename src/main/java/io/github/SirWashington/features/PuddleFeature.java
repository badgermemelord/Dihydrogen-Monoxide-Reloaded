package io.github.SirWashington.features;

import io.github.SirWashington.PathfinderBFS;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.ArrayList;
import java.util.List;

public class PuddleFeature {

    public static final int PUDDLE_RADIUS = 4;
    public static final int PUDDLE_DIAMETER = PUDDLE_RADIUS * 2 + 1;
    private static BlockPos pos;
    private static int bfsMatrix[][] = new int[PUDDLE_DIAMETER][PUDDLE_DIAMETER];
    private static List<PathfinderBFS.Node> holes = new ArrayList<>(8);
    private static int xX;
    private static int zZ;

    public static void execute(ArrayList<BlockPos> blocks, BlockPos center, int level, int[][] data, int[][] newData) {
        if (!Features.PUDDLE_FEATURE_ENABLED) return;
        //setWaterLevel(level, center, world);
        pos = center;

        if (level == 1 && CachedWater.getWaterLevel(pos.down()) != 0) {
            int x = pos.getX();
            int y = pos.getY();
            int z = pos.getZ();

            // fill in the bfsMatrix
            xX = x - PUDDLE_RADIUS;
            zZ = z - PUDDLE_RADIUS;

            for (int iX = 0; iX < PUDDLE_DIAMETER; iX++) {
                for (int iZ = 0; iZ < PUDDLE_DIAMETER; iZ++) {
                    BlockPos internalPos = new BlockPos(iX + xX, y, iZ + zZ);
                    bfsMatrix[iX][iZ] = CachedWater.getWaterLevel(internalPos) == 0 ? 9 : -1;
                }
            }


            //union start
            for (int currentRadius = 1; currentRadius <= PUDDLE_RADIUS; currentRadius++) {
                int xL = x - currentRadius;
                int xR = x + currentRadius;
                int zT = z + currentRadius;
                int zB = z - currentRadius;

                holes.clear();

                testLine(xL, zT, xR, zT);
                testLine(xL, zB, xR, zB);
                testLine(xL, zB, xL, zT);
                testLine(xR, zB, xR, zT);

                if (holes.isEmpty())
                    continue;

                bfsMatrix[4][4] = -3;

                holeFound(holes);
            }
        }
    }

    private static void holeFound(List<PathfinderBFS.Node> holes) {
        int[][] result = PathfinderBFS.distanceMapperBFS(bfsMatrix, holes);

        int minDistance = 255;
        Direction direction = null;

        if (result[4][3] < minDistance && result[4][3] >= 0) {
            minDistance = result[4][3];
            direction = Direction.NORTH;
        }
        if (result[3][4] < minDistance && result[3][4] >= 0) {
            minDistance = result[3][4];
            direction = Direction.WEST;
        }
        if (result[4][5] < minDistance && result[4][5] >= 0) {
            minDistance = result[4][5];
            direction = Direction.SOUTH;
        }
        if (result[5][4] < minDistance && result[5][4] >= 0) {
            minDistance = result[5][4];
            direction = Direction.EAST;
        }

        if (minDistance <= 4) {
            if (direction == null) return;
            move(direction);
        }
    }

    private static void move(Direction direction) {
        CachedWater.setWaterLevel(0, pos);
        CachedWater.addWater(1, pos.offset(direction));
    }

    // its actual test rect but ssssh...
    private static void testLine(int x, int z, int toX, int toZ) {
        BlockPos testPos;

        for (int iX = x; iX <= toX; iX++) {
            for (int iZ = z; iZ <= toZ; iZ++) {
                int relX = iX-xX;
                int relZ = iZ-zZ;
                testPos = new BlockPos(iX, pos.getY() - 1, iZ);
                int uLevel = CachedWater.getWaterLevel(new BlockPos(iX, pos.getY(), iZ));
                if (CachedWater.isNotFull(CachedWater.getWaterLevel(testPos)) && (uLevel == 0 || uLevel == 1)) {
                    holes.add(new PathfinderBFS.Node(relX, relZ, 0));
                }
            }
        }
    }

    private static void printMatrix(int[][] matrix) {
        // print result of bfs
        for (int a = matrix.length - 1; a >= 0; a--) {
            for (int b = matrix.length - 1; b >= 0; b--) {
                System.out.print((matrix[b][a] < 0 ? "" : " ") + matrix[b][a] + " ");
            }
            System.out.println();
        }
    }

}
