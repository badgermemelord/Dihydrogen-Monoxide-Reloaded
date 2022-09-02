package io.github.SirWashington.features;

import it.unimi.dsi.fastutil.longs.Long2ByteMap;
import it.unimi.dsi.fastutil.longs.Long2ByteOpenHashMap;
import net.minecraft.block.*;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkSection;

import java.util.function.LongToIntFunction;

import static io.github.SirWashington.WaterPhysics.WATER_LEVEL;

public class CachedWater {

    public static boolean useSections = true;
    public static boolean useCache = true;
    private static final Long2ByteMap cache = new Long2ByteOpenHashMap();
    public static World world;

    public static int getWaterLevel(BlockPos ipos) {
        LongToIntFunction func = pos -> {
            BlockState blockstate = getBlockState(BlockPos.fromLong(pos));

            if (blockstate.isAir()) return (byte) 0;
            if (blockstate.contains(WATER_LEVEL)) return blockstate.get(WATER_LEVEL);

            FluidState fluidstate = blockstate.getFluidState();
            if (fluidstate == Fluids.EMPTY.getDefaultState()) return (byte) -1;

            int waterlevel;
            if (fluidstate.isStill()) {
                waterlevel = 8;
            } else {
                waterlevel = fluidstate.getLevel();
            }
            return (byte) waterlevel;
        };

        if (useCache) {
            int result = cache.computeIfAbsent(ipos.asLong(), func);

            assert result == func.applyAsInt(ipos.asLong());
            return result;
        } else return func.applyAsInt(ipos.asLong());
    }

    public static boolean isNotFull(int waterLevel) {
        return waterLevel < 8 && waterLevel >= 0;
    }

    public static boolean isNotFull(BlockPos pos) {
        return isNotFull(getWaterLevel(pos));
    }

    private static final Long2ByteMap queuedWaterLevels = new Long2ByteOpenHashMap();

    static {
        queuedWaterLevels.defaultReturnValue((byte) -1);
    }

    public static void setWaterLevel(int level, BlockPos pos) {
        if (useCache) {
            cache.put(pos.asLong(), (byte) level);
            queuedWaterLevels.put(pos.asLong(), (byte) level);
        } else {
            cache.remove(pos.asLong());
        }
    }

    private static void setWaterLevelDirect(int level, BlockPos pos) {
        BlockState prev = getBlockState(pos);

        assert  prev.isAir() ||
            prev.contains(WATER_LEVEL) ||
            !prev.getFluidState().isEmpty() ||
            level < 0;

        if (prev.contains(WATER_LEVEL)) {
            setBlockState(pos, prev.with(WATER_LEVEL, level));
        } else {
            if (level == 0) {
                setBlockState(pos, Blocks.AIR.getDefaultState());
            } else if (level < 0) {
                // System.out.println("Trying to set waterlevel " + level);
            } else if (level <= 8) {
                if (level == 8) {
                    if (!(prev.getBlock() instanceof FluidFillable)) { // Don't fill kelp etc
                        setBlockState(pos, Blocks.WATER.getDefaultState());
                    }
                } else {
                    if (!(prev.getBlock() instanceof FluidDrainable)) {
                        world.breakBlock(pos, true);
                    } else {
                        if (prev.getBlock() instanceof Waterloggable) {
                            //TODO proper waterlogged flow
                        }
                    }

                    setBlockState(pos, Fluids.FLOWING_WATER.getFlowing(level, false).getBlockState());
                }
            } else {
                System.out.println("HELP THY SOUL Trying to set waterlevel " + level);
            }
        }
    }


    public static void addWater(int level, BlockPos pos) {
        int existingwater = getWaterLevel(pos);
        if (existingwater == -1) throw new IllegalStateException("Tried to add water to a full block");

        int totalwater = existingwater + level;
        if (totalwater > 8) {
            addWater(totalwater - 8, pos.up());
            setWaterLevel(8, pos);
        } else {
            setWaterLevel(totalwater, pos);
        }
    }

    public static BlockState getBlockState(BlockPos pos) {
        return world.getBlockState(pos);
    }

    public static void main(String[] args) {
        System.out.println(Block.REDRAW_ON_MAIN_THREAD | Block.NOTIFY_LISTENERS | Block.NOTIFY_NEIGHBORS);
    }


    /**
     * @deprecated Use setWaterLevel!
     */
    @Deprecated
    public static void setBlockState(BlockPos pos, BlockState state) {
        world.setBlockState(pos, state, 11);
    }

    private static ChunkSection getSection(BlockPos pos) {
        //ChunkSection section = cachedSections[getSectionId(pos)];

        //assert world.getChunk(pos).getSection(world.getChunk(pos).getSectionIndex(pos.getY())) == section;

        return world.getChunk(pos).getSection(world.getChunk(pos).getSectionIndex(pos.getY()));
    }

    public static void setup(ServerWorld world, BlockPos fluidPos) {
        CachedWater.world = world;
/*
        int gmr = 7; //generalMaxRange, the maximum range that will ever be used in checks

        BlockPos[] cornerList = new BlockPos[8];

        BlockPos c11 = fluidPos.add(gmr, 0, gmr);
        cornerList[0] = c11;
        BlockPos c12 = fluidPos.add(-gmr, 0, gmr);
        cornerList[1] = c12;
        BlockPos c13 = fluidPos.add(gmr, 0, -gmr);
        cornerList[2] = c13;
        BlockPos c14 = fluidPos.add(-gmr, 0, -gmr);
        cornerList[3] = c14;

        BlockPos c21 = fluidPos.add(gmr, -1, gmr);
        cornerList[4] = c21;
        BlockPos c22 = fluidPos.add(-gmr, -1, gmr);
        cornerList[5] = c22;
        BlockPos c23 = fluidPos.add(gmr, -1, -gmr);
        cornerList[6] = c23;
        BlockPos c24 = fluidPos.add(-gmr, -1, -gmr);
        cornerList[7] = c24;

        int smallestX = Integer.MAX_VALUE;
        int smallestY = Integer.MAX_VALUE;
        int smallestZ = Integer.MAX_VALUE;

        for (BlockPos blockPos : cornerList) {
            smallestX = Math.min(smallestX, blockPos.getX());
            smallestY = Math.min(smallestY, blockPos.getY());
            smallestZ = Math.min(smallestZ, blockPos.getZ());
        }

        xChunkA = smallestX / 16;
        yChunkA = smallestY / 16;
        zChunkA = smallestZ / 16;

        for (BlockPos blockPos : cornerList) {
            Chunk chunk = world.getChunk(blockPos);
            ChunkSection section = chunk.getSection(chunk.getSectionIndex(blockPos.getY()));

            cachedSections[getSectionId(blockPos)] = section;
        }
        */
    }

    public static void beforeTick(ServerWorld serverWorld) {
        assert cache.isEmpty();
    }

    public static void afterTick(ServerWorld serverWorld) {
        // TODO cache per dimension
        cache.clear();

        for (var entry : queuedWaterLevels.long2ByteEntrySet()) {
            setWaterLevelDirect(entry.getByteValue(), BlockPos.fromLong(entry.getLongKey()));
        }

        queuedWaterLevels.clear();
    }
}
