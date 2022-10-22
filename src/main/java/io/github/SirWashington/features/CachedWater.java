package io.github.SirWashington.features;

import io.github.SirWashington.FlowWater;
import io.github.SirWashington.scheduling.WaterTickScheduler;
import it.unimi.dsi.fastutil.longs.Long2ByteMap;
import it.unimi.dsi.fastutil.longs.Long2ByteOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.block.*;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.crash.CrashCallable;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.LongToIntFunction;
import java.util.function.Supplier;

import static io.github.SirWashington.WaterPhysics.WATER_LEVEL;

public class CachedWater {

    public static boolean useSections = true;
    public static boolean useCache = true;
    private static final Long2ByteMap cache = new Long2ByteOpenHashMap();
    private static final Map<ChunkSectionPos, ChunkSection> sections = new HashMap<>();
    public static World world;

    public static int a = 0;
    public static int countMa() {
        a += 1;
        return a;
    }

    public static void ScheduleFluidTick(World world) {

        //if(!WaterTickScheduler.BlocksToTick.isEmpty()) {
            for (Long BPasLong : WaterTickScheduler.BlocksToTick) {
                BlockPos BP = BlockPos.fromLong(BPasLong);
                System.out.println("Cache BP: " + BP);
                BlockState BS = getBlockState(BP);
                //FluidState FS = BS.getFluidState();
                if (BS.getBlock() == Blocks.WATER) {
                    FlowWater.flowwater(world, BP);
                }
            }
        //}
    }

/*    public static boolean testTick(BlockPos BP) {

        world.setBlockState(BP, Blocks.GOLD_BLOCK.getDefaultState(), 11);

        updateNeighbours(BP);
        return false;
    }

    public static void updateNeighbours(BlockPos BP) {
        ArrayList<BlockPos> neighbours = new ArrayList<>();

        for (Direction dir : Direction.Type.HORIZONTAL) {
            //neighbours.add(BP.offset(dir));
            WaterTickScheduler.scheduleFluidBlock(BP.offset(dir));
        }

    }*/

    public static int getWaterLevel(BlockPos ipos) {
        LongToIntFunction func = pos -> {
            BlockState blockstate = getBlockState(BlockPos.fromLong(pos));

            if (blockstate.isAir())
                return (byte) 0;
            if (blockstate.contains(WATER_LEVEL))
                return blockstate.get(WATER_LEVEL);

            FluidState fluidstate = blockstate.getFluidState();
            if (fluidstate == Fluids.EMPTY.getDefaultState())
                return (byte) -1;

            int waterlevel;
            if (fluidstate.isStill()) {
                waterlevel = 8;
            } else {
                waterlevel = fluidstate.getLevel();
            }
            return (byte) waterlevel;
        };

        if (useCache) {
            return cache.computeIfAbsent(ipos.asLong(), func);
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
            queueNeighbours(world, pos);
        } else {
            setWaterLevelDirect(level, pos);
            cache.remove(pos.asLong());
        }
    }

    public static void queueNeighbours(World world, BlockPos pos) {
        for (Direction dir : Direction.Type.HORIZONTAL) {
            //int level = getBlockState(pos).getFluidState().getLevel();
            //cache.put(pos.offset(dir).asLong(), (byte) level);
            WaterTickScheduler.scheduleFluidBlock(pos);
            System.out.println(pos.offset(dir));
        }
        for (Direction dir : Direction.Type.VERTICAL) {
            //int level = getBlockState(pos).getFluidState().getLevel();
            //cache.put(pos.offset(dir).asLong(), (byte) level);
            WaterTickScheduler.scheduleFluidBlock(pos);
        }
    }

    private static void setWaterLevelDirect(int level, BlockPos pos) {
        BlockState prev = getBlockState(pos);

        assert  prev.isAir() ||
            prev.contains(WATER_LEVEL) ||
            !prev.getFluidState().isEmpty() ||
            level < 0;

        if (prev.contains(WATER_LEVEL)) {
            setBlockStateNoNeighbors(pos, prev, prev.with(WATER_LEVEL, level));
        } else {
            if (level == 0) {
                setBlockStateNoNeighbors(pos, prev, Blocks.AIR.getDefaultState());
            } else if (level < 0) {
                // System.out.println("Trying to set waterlevel " + level);
            } else if (level <= 8) {
                if (level == 8) {
                    if (!(prev.getBlock() instanceof FluidFillable)) { // Don't fill kelp etc
                        setBlockStateNoNeighbors(pos, prev, Blocks.WATER.getDefaultState());
                    }
                } else {
                    if (!(prev.getBlock() instanceof FluidDrainable)) {
                        world.breakBlock(pos, true);
                    } else {
                        if (prev.getBlock() instanceof Waterloggable) {
                            //TODO proper waterlogged flow
                        }
                    }

                    setBlockStateNoNeighbors(pos, prev, Fluids.FLOWING_WATER.getFlowing(level, false).getBlockState());
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
        if (useSections) {
            if (pos.getY() < world.getBottomY() || pos.getY() > world.getTopY()) {
                return Blocks.AIR.getDefaultState();
            }

            return getBlockStateSection(pos);
        } else {
            return world.getBlockState(pos);
        }
    }

    public static BlockState getBlockStateSection(BlockPos pos) {
        return sections.computeIfAbsent(ChunkSectionPos.from(pos), CachedWater::getChunkSection)
                .getBlockState(pos.getX() & 15, pos.getY() & 15, pos.getZ() & 15);
    }

    public static BlockState setBlockStateSection(BlockPos pos, BlockState state) {
        ((ServerWorld) world).getChunkManager().markForUpdate(pos);
        return sections.computeIfAbsent(ChunkSectionPos.from(pos), CachedWater::getChunkSection)
                .setBlockState(pos.getX() & 15, pos.getY() & 15, pos.getZ() & 15, state, false);
    }

    public static ChunkSection getChunkSection(ChunkSectionPos pos) {
        ChunkSection result = world.getChunk(pos.getCenterPos()).getSectionArray()[world.sectionCoordToIndex(pos.getY())];
        result.unlock(); // FIXME
        result.lock();
        return result;
    }

    public static void main(String[] args) {
        System.out.println(Block.REDRAW_ON_MAIN_THREAD | Block.NOTIFY_LISTENERS | Block.NOTIFY_NEIGHBORS);
    }

    public static void setBlockStateNoNeighbors(BlockPos pos, BlockState oldState, BlockState state) {
        if (!state.getFluidState().isEmpty() && useSections) {
            setBlockStateSection(pos, state);
            world.updateListeners(pos, oldState, state, Block.REDRAW_ON_MAIN_THREAD | Block.NOTIFY_LISTENERS | Block.FORCE_STATE);

            fluidsToUpdate.put(pos, state);
        } else if (state.isAir()) {
            setBlockStateSection(pos, state);
            world.updateListeners(pos, oldState, state, Block.REDRAW_ON_MAIN_THREAD | Block.NOTIFY_LISTENERS | Block.FORCE_STATE);
        } else {
            // We could just make everything use setBlockStateSection but non fluid/air should be taken more care of
            world.setBlockState(pos, state, Block.REDRAW_ON_MAIN_THREAD | Block.NOTIFY_LISTENERS);
        }
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
        assert cache.isEmpty(); //FIXME
    }

    private static final Map<BlockPos, BlockState> fluidsToUpdate = new HashMap<>();

    /**
     * The majority of time spent in setBlockState is spent updating neighbors, which, when a lot of water is moving,
     * is mostly just fluid updating fluid.
     * <p>
     * Since fluid updates don't care about the source block, we can queue them all and run only once
     * (rather than each setBlock potentially running up to 6 neighbor updates)
     */
    private static void updateNeighbor(BlockPos pos, Block sourceBlock, BlockPos neighborPos) {
        BlockState neighborState = getBlockState(pos);
        if (!neighborState.getFluidState().isEmpty()) {
            fluidsToUpdate.put(pos, neighborState);
        } else {
            // Vanilla behaviour
            try {
                neighborState.neighborUpdate(world, pos, sourceBlock, neighborPos, false);
            } catch (Throwable var8) {
                CrashReport crashReport = CrashReport.create(var8, "Exception while updating neighbours");
                CrashReportSection crashReportSection = crashReport.addElement("Block being updated");
                crashReportSection.add("Source block type", (CrashCallable<String>)(() -> {
                    try {
                        return String.format("ID #%s (%s // %s)", Registry.BLOCK.getId(sourceBlock), sourceBlock.getTranslationKey(), sourceBlock.getClass().getCanonicalName());
                    } catch (Throwable var2x) {
                        return "ID #" + Registry.BLOCK.getId(sourceBlock);
                    }
                }));
                CrashReportSection.addBlockInfo(crashReportSection, world, pos, neighborState);
                throw new CrashException(crashReport);
            }
        }
    }

    public static void afterTick(ServerWorld serverWorld) {
        // TODO cache per dimension
        cache.clear();

/*        for (var entry : queuedWaterLevels.long2ByteEntrySet()) {
            BlockPos pos = BlockPos.fromLong(entry.getLongKey());
            setWaterLevelDirect(entry.getByteValue(), pos);

            Block block = getBlockState(pos).getBlock();
            updateNeighbor(pos.west(), block, pos);
            updateNeighbor(pos.east(), block, pos);
            updateNeighbor(pos.down(), block, pos);
            updateNeighbor(pos.up(), block, pos);
            updateNeighbor(pos.north(), block, pos);
            updateNeighbor(pos.south(), block, pos);
        }

        for (var entry : fluidsToUpdate.entrySet()) {
            var state = entry.getValue();
            var pos = entry.getKey();

            world.createAndScheduleFluidTick(pos, state.getFluidState().getFluid(), state.getFluidState().getFluid().getTickRate(world));
        }*/

        sections.forEach((sectionPos, section) -> section.unlock());

        fluidsToUpdate.clear();
        queuedWaterLevels.clear();
        sections.clear();
    }
}
