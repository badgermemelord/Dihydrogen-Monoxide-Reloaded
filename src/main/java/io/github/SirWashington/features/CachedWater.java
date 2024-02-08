package io.github.SirWashington.features;

import com.ewoudje.lasagna.chunkstorage.ExtraStorageSectionContainer;
import io.github.SirWashington.FlowWater;
import io.github.SirWashington.WaterSection;
import io.github.SirWashington.WaterVolume;
import io.github.SirWashington.scheduling.ChunkHandlingMethods;
import io.github.SirWashington.scheduling.MixinInterfaces;
import it.unimi.dsi.fastutil.longs.*;
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
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkSection;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.LongToIntFunction;

import static io.github.SirWashington.WaterPhysics.WATER_LEVEL;
import static io.github.SirWashington.properties.WaterFluidProperties.*;

public class CachedWater {

    // THIS IS THE REAL REPO

    public static boolean useSections = true;
    public static boolean useCache = false;
    private static final Long2IntMap volumeCache = new Long2IntOpenHashMap();
    private static final Map<ChunkSectionPos, ChunkSection> sections = new HashMap<>();
    public static ArrayList<Direction> directionList = new ArrayList<>(4);
    //Use n^2 for this setting
    //public static final int equalisingRate = ConfigVariables.equalisingRate;
    //public static final int minimumFlowDifference = 2;
    public static World cacheWorld;
    public static List<BlockPos> updateList = new ArrayList<>();

    public static int a = 0;
    public static void addToCounter() {
        a += 1;
    }

    public static void setupDirections() {
        for(Direction dir : Direction.Type.HORIZONTAL) {
            directionList.add(dir);
        }
    }
    public static Direction getRandomDirection() {
        addToCounter();
        return directionList.get((a%4));
    }
    public static void tickFluidsInWorld(World world) {
        cacheWorld = world;
        setupDirections();
        //System.out.println("world set to " + world.getDimension().getMinimumY());
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
        BlockPos BP;
        BP = BlockPos.fromLong(l);
        TickThisBlock(world, BP);
    }
    public static void TickThisBlock(World world, BlockPos pos) {
        addToCounter();
        BlockState BS = getBlockState(pos);
        //System.out.println("ticked block: " + pos);
        //TODO delete BlockState check once other systems are working reliably
        if (getWaterVolume(pos) > 0) {
            FlowWater.flowWater(world, pos, BS.getFluidState());
        }
    }

    public static boolean isInfinite(BlockPos pos) {
        BlockState state = getBlockState(pos);
        return (state.contains(ISFINITE) && !state.get(ISFINITE));
    }

    public static boolean isNotFull(int waterVolume) {
        return waterVolume < WaterVolume.volumePerBlock && waterVolume >= 0;
    }

    public static boolean isNotFull(BlockPos pos) {
        return isNotFull(getWaterVolume(pos));
    }

    public static boolean isFlowable(BlockPos pos) {
        return getWaterVolume(pos) >= 0;
    }

    public static boolean isWater(BlockState state) {
        return !state.isAir() && (state.getFluidState() != Fluids.EMPTY.getDefaultState()) && !state.contains(Properties.WATERLOGGED);
    }

    private static final Long2IntMap queuedWaterVolumes = new Long2IntOpenHashMap();





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

    // VOLUME RELATED CODE

    public static void addVolume(int volume, BlockPos pos) {
        int existingWater = getWaterVolume(pos);
        if (existingWater == -1) throw new IllegalStateException("Tried to add water to a full block");

        int totalWater = existingWater + volume;
        if (totalWater > WaterVolume.volumePerBlock) {
            addVolume(totalWater - WaterVolume.volumePerBlock, pos.up());
            setWaterVolume(WaterVolume.volumePerBlock, pos);
        } else {
            setWaterVolume(totalWater, pos);
            setWaterVolume(0, pos.up());
        }
    }

    public static void setWaterVolume(int volume, BlockPos pos) {
        //System.out.println("ebebe");
        if (useCache) {
            //System.out.println("ababa: " + pos);
            volumeCache.put(pos.asLong(), volume);
            updateList.add(pos);
            queuedWaterVolumes.put(pos.asLong(), volume);
        } else {
            setWaterVolumeDirect(volume, pos);
            volumeCache.remove(pos.asLong());
        }
        //System.out.println(updateList);
    }

    private static void setWaterVolumeDirect(int volume, BlockPos pos) {
        BlockState prev = getBlockState(pos);

        ChunkSectionPos sectionPos = ChunkSectionPos.from(pos);
        ChunkSection section = getChunkSection(sectionPos);
        WaterSection water = (WaterSection) ((ExtraStorageSectionContainer) section).getSectionStorage(WaterSection.ID);

        if (water == null) {
            water = new WaterSection(cacheWorld.getWorldChunk(pos), cacheWorld.sectionCoordToIndex(sectionPos.getY()));
            ((ExtraStorageSectionContainer) section).setSectionStorage(WaterSection.ID, water);
        }

        assert  prev.isAir() ||
                !prev.getFluidState().isEmpty() ||
                volume < 0;

        water.setWaterVolume(pos, (short) volume);
    }

    public static int getWaterVolume(BlockPos ipos) {
        LongToIntFunction func = pos -> {

            WaterSection water = (WaterSection) ((ExtraStorageSectionContainer)
                    getChunkSection(ChunkSectionPos.from(ipos))
            ).getSectionStorage(WaterSection.ID);

            if (water == null) {
                System.out.println("water null");
                BlockState state = getBlockState(BlockPos.fromLong(pos));
                return WaterVolume.getWaterVolumeOfState(state);
            }

            short volume = water.getWaterVolume(ipos);
            if (volume == Short.MIN_VALUE) {
                System.out.println("minvalued");
                BlockState state = getBlockState(BlockPos.fromLong(pos));
                volume = WaterVolume.getWaterVolumeOfState(state);
                water.setWaterVolume(ipos, volume);
            }

            return volume;
        };

        if (useCache) {
            return volumeCache.computeIfAbsent(ipos.asLong(), func);
        } else return func.applyAsInt(ipos.asLong());
    }

    //VOLUME CODE END

    public static void setup(ServerWorld world, BlockPos fluidPos) {
        CachedWater.cacheWorld = world;
    }

/*
    public static void beforeTick(ServerWorld serverWorld) {
        assert levelCache.isEmpty(); //FIXME
    }
*/

    public static final Map<BlockPos, BlockState> fluidsToUpdate = new HashMap<>();

    /**
     * The majority of time spent in setBlockState is spent updating neighbors, which, when a lot of water is moving,
     * is mostly just fluid updating fluid.
     * <p>
     * Since fluid updates don't care about the source block, we can queue them all and run only once
     * (rather than each setBlock potentially running up to 6 neighbor updates)
     */
    private static void updateNeighbor(BlockPos pos, Block sourceBlock, BlockPos neighborPos) {
        System.out.println("neigh code");
        BlockState neighborState = getBlockState(pos);
        if (isFlowable(neighborPos)) {
            System.out.println("queued: " + neighborPos);
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
/*        if(serverWorld.getDimension().getMinimumY() != 64) {
            return;
        }*/
        // TODO cache per dimension
        //volumeCache.clear();

        for (var entry : queuedWaterVolumes.long2IntEntrySet()) {
            //System.out.println("entry: " + entry.getIntValue());
            BlockPos pos = BlockPos.fromLong(entry.getLongKey());
            setWaterVolumeDirect(entry.getIntValue(), pos);

            //Old update logic
/*            Block block = getBlockState(pos).getBlock();
            updateNeighbor(pos.west(), block, pos);
            updateNeighbor(pos.east(), block, pos);
            updateNeighbor(pos.down(), block, pos);
            updateNeighbor(pos.up(), block, pos);
            updateNeighbor(pos.north(), block, pos);
            updateNeighbor(pos.south(), block, pos);*/
            ChunkHandlingMethods.scheduleFluidBlock(pos.west(), serverWorld);
            ChunkHandlingMethods.scheduleFluidBlock(pos.east(), serverWorld);
            ChunkHandlingMethods.scheduleFluidBlock(pos.down(), serverWorld);
            ChunkHandlingMethods.scheduleFluidBlock(pos.up(), serverWorld);
            ChunkHandlingMethods.scheduleFluidBlock(pos.north(), serverWorld);
            ChunkHandlingMethods.scheduleFluidBlock(pos.south(), serverWorld);

        }

        ChunkHandlingMethods.subtractTickTickets(serverWorld);

        sections.forEach((sectionPos, section) -> section.unlock());
        fluidsToUpdate.clear();
        queuedWaterVolumes.clear();
        sections.clear();
    }
}
