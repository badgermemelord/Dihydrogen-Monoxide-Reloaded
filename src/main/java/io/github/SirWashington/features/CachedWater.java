package io.github.SirWashington.features;

import io.github.SirWashington.FlowWater;
import io.github.SirWashington.scheduling.ChunkHandlingMethods;
import io.github.SirWashington.scheduling.MixinInterfaces;
import it.unimi.dsi.fastutil.longs.Long2ByteMap;
import it.unimi.dsi.fastutil.longs.Long2ByteOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.block.*;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.crash.CrashCallable;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkSection;

import java.util.HashMap;
import java.util.Map;
import java.util.function.LongToIntFunction;

import static io.github.SirWashington.WaterPhysics.WATER_LEVEL;
import static io.github.SirWashington.properties.WaterFluidProperties.ISFINITE;

public class CachedWater {

    public static boolean useSections = true;
    public static boolean useCache = true;
    private static final Long2ByteMap cache = new Long2ByteOpenHashMap();
    private static final Map<ChunkSectionPos, ChunkSection> sections = new HashMap<>();
    public static World cacheWorld;

    public static int a = 0;
    public static int countMa() {
        a += 1;
        return a;
    }
    public static void tickFluidsInWorld(World world) {
        cacheWorld = world;
        //System.out.println("fluidtick with following non-empty chunk longs: ");
        for (long worldChunkLong : ((MixinInterfaces.DuckInterface)world).getWorldCache().Chunk2BlockMap.keySet()) {
            if(!((MixinInterfaces.DuckInterface)world).getWorldCache().Chunk2BlockMap.get(worldChunkLong).isEmpty()) {
                LongSet value = ((MixinInterfaces.DuckInterface)world).getWorldCache().Chunk2BlockMap.get(worldChunkLong);
                value.forEach((long l) -> setIterator(l, world));
            }
        }
    }
    public static void setIterator(long l,  World world) {
        //System.out.println(((MixinInterfaces.DuckInterface)world).getWorldCache().block2TicketMap);
        //System.out.println(l);
        if (((MixinInterfaces.DuckInterface)world).getWorldCache().block2TicketMap.get(l) > 0) {
            BlockPos BP;
            BP = BlockPos.fromLong(l);
            TickThisBlock(world, BP);
        }
    }
    public static void TickThisBlock(World world, BlockPos pos) {
        BlockState BS = getBlockState(pos);
        if (BS.getBlock() == Blocks.WATER) {
            FlowWater.flowWater(world, pos, BS.getFluidState());
        }
    }
    
    

    public static int getWaterLevel(BlockPos ipos) {
        LongToIntFunction func = pos -> {
            BlockState state = getBlockState(BlockPos.fromLong(pos));
            return (byte) getWaterLevelOfState(state);
        };

        if (useCache) {
            return cache.computeIfAbsent(ipos.asLong(), func);
        } else return func.applyAsInt(ipos.asLong());
    }

    public static boolean isInfinite(BlockPos pos) {
        BlockState state = getBlockState(pos);
        return (state.contains(ISFINITE) && !state.get(ISFINITE));
    }

    public static boolean isNotFull(int waterLevel) {
        return waterLevel < 8 && waterLevel >= 0;
    }

    public static boolean isNotFull(BlockPos pos) {
        return isNotFull(getWaterLevel(pos));
    }

    public static int getWaterLevelOfState(BlockState state) {
        if (state.isAir())
            return (byte) 0;
        if (state.contains(ISFINITE) && !state.get(ISFINITE)) {
            return (byte) -2;
        }
        if (state.contains(WATER_LEVEL))
            return state.get(WATER_LEVEL);

        FluidState fluidstate = state.getFluidState();
        if (fluidstate == Fluids.EMPTY.getDefaultState())
            return (byte) -1;

        int waterLevel;
        if (fluidstate.isStill()) {
            waterLevel = 8;
        } else {
            waterLevel = fluidstate.getLevel();
        }
        return waterLevel;
    }

    public static int getWaterLevelForPF(BlockPos pos) {
        BlockState state = getBlockState(pos);
        if (state.isAir())
            return (byte) 0;
        if (state.contains(ISFINITE) && !state.get(ISFINITE)) {
            return (byte) 1;
        }
        if (state.contains(WATER_LEVEL))
            return state.get(WATER_LEVEL);

        FluidState fluidstate = state.getFluidState();
        if (fluidstate == Fluids.EMPTY.getDefaultState())
            return (byte) -1;

        int waterLevel;
        if (fluidstate.isStill()) {
            waterLevel = 8;
        } else {
            waterLevel = fluidstate.getLevel();
        }
        return waterLevel;
    }


    public static boolean isWater(BlockState state) {
        return !state.isAir() && (state.getFluidState() != Fluids.EMPTY.getDefaultState()) && !state.contains(Properties.WATERLOGGED);
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
            setWaterLevelDirect(level, pos);
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
                        cacheWorld.breakBlock(pos, true);
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
        int existingWater = getWaterLevel(pos);
        if (existingWater == -1) throw new IllegalStateException("Tried to add water to a full block");

        int totalWater = existingWater + level;
        if (totalWater > 8) {
            addWater(totalWater - 8, pos.up());
            setWaterLevel(8, pos);
        } else {
            setWaterLevel(totalWater, pos);
        }
    }

    public static BlockState getBlockState(BlockPos pos) {
        if (useSections) {
            if (pos.getY() < cacheWorld.getBottomY() || pos.getY() > cacheWorld.getTopY()) {
                return Blocks.AIR.getDefaultState();
            }

            return getBlockStateSection(pos);
        } else {
            return cacheWorld.getBlockState(pos);
        }
    }

    public static BlockState getBlockStateSection(BlockPos pos) {
        return sections.computeIfAbsent(ChunkSectionPos.from(pos), CachedWater::getChunkSection)
                .getBlockState(pos.getX() & 15, pos.getY() & 15, pos.getZ() & 15);
    }

    public static BlockState setBlockStateSection(BlockPos pos, BlockState state) {
        ((ServerWorld) cacheWorld).getChunkManager().markForUpdate(pos);
        return sections.computeIfAbsent(ChunkSectionPos.from(pos), CachedWater::getChunkSection)
                .setBlockState(pos.getX() & 15, pos.getY() & 15, pos.getZ() & 15, state, false);
    }

    public static ChunkSection getChunkSection(ChunkSectionPos pos) {
        ChunkSection result = cacheWorld.getChunk(pos.getCenterPos()).getSectionArray()[cacheWorld.sectionCoordToIndex(pos.getY())];
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
            cacheWorld.updateListeners(pos, oldState, state, Block.REDRAW_ON_MAIN_THREAD | Block.NOTIFY_LISTENERS | Block.FORCE_STATE);

            fluidsToUpdate.put(pos, state);
        } else if (state.isAir()) {
            setBlockStateSection(pos, state);
            cacheWorld.updateListeners(pos, oldState, state, Block.REDRAW_ON_MAIN_THREAD | Block.NOTIFY_LISTENERS | Block.FORCE_STATE);
        } else {
            // We could just make everything use setBlockStateSection but non fluid/air should be taken more care of
            cacheWorld.setBlockState(pos, state, Block.REDRAW_ON_MAIN_THREAD | Block.NOTIFY_LISTENERS);
        }
    }

    public static void setup(ServerWorld world, BlockPos fluidPos) {
        CachedWater.cacheWorld = world;
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

    public static final Map<BlockPos, BlockState> fluidsToUpdate = new HashMap<>();

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
                neighborState.neighborUpdate(cacheWorld, pos, sourceBlock, neighborPos, false);
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
                CrashReportSection.addBlockInfo(crashReportSection, cacheWorld, pos, neighborState);
                throw new CrashException(crashReport);
            }
        }
    }

    public static void afterTick(ServerWorld serverWorld) {
        // TODO cache per dimension
        cache.clear();

        for (var entry : queuedWaterLevels.long2ByteEntrySet()) {
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
            ChunkHandlingMethods.scheduleFluidBlock(pos, serverWorld);
            //cacheWorld.createAndScheduleFluidTick(pos, state.getFluidState().getFluid(), state.getFluidState().getFluid().getTickRate(cacheWorld));
        }

        sections.forEach((sectionPos, section) -> section.unlock());

        fluidsToUpdate.clear();
        queuedWaterLevels.clear();
        sections.clear();
    }
}
