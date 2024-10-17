package io.github.SirWashington.features;

import it.unimi.dsi.fastutil.longs.Long2ByteMap;
import it.unimi.dsi.fastutil.longs.Long2ByteMap.Entry;
import it.unimi.dsi.fastutil.longs.Long2ByteOpenHashMap;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.CrashReportDetail;
import net.minecraft.ReportedException;
import net.minecraft.block.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import java.util.HashMap;
import java.util.Map;
import java.util.function.LongToIntFunction;

import static io.github.SirWashington.WaterPhysics.WATER_LEVEL;
import static io.github.SirWashington.properties.WaterFluidProperties.ISFINITE;

public class CachedWater {

    public static boolean useSections = true;
    public static boolean useCache = true;
    private static final Long2ByteMap cache = new Long2ByteOpenHashMap();
    private static final Map<SectionPos, LevelChunkSection> sections = new HashMap<>();
    public static Level world;

    public static int a = 0;
    public static int countMa() {
        a += 1;
        return a;
    }

    public static int getWaterLevel(BlockPos ipos) {
        LongToIntFunction func = pos -> {
            BlockState state = getBlockState(BlockPos.of(pos));
            return (byte) getWaterLevelOfState(state);
        };

        if (useCache) {
            return cache.computeIfAbsent(ipos.asLong(), func);
        } else return func.applyAsInt(ipos.asLong());
    }

    public static boolean isInfinite(BlockPos pos) {
        BlockState state = getBlockState(pos);
        return (state.hasProperty(ISFINITE) && !state.getValue(ISFINITE));
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
        if (state.hasProperty(ISFINITE) && !state.getValue(ISFINITE)) {
            return (byte) -2;
        }
        if (state.hasProperty(WATER_LEVEL))
            return state.getValue(WATER_LEVEL);

        FluidState fluidstate = state.getFluidState();
        if (fluidstate == Fluids.EMPTY.defaultFluidState())
            return (byte) -1;

        int waterLevel;
        if (fluidstate.isSource()) {
            waterLevel = 8;
        } else {
            waterLevel = fluidstate.getAmount();
        }
        return waterLevel;
    }

    public static int getWaterLevelForPF(BlockPos pos) {
        BlockState state = getBlockState(pos);
        if (state.isAir())
            return (byte) 0;
        if (state.hasProperty(ISFINITE) && !state.getValue(ISFINITE)) {
            return (byte) 1;
        }
        if (state.hasProperty(WATER_LEVEL))
            return state.getValue(WATER_LEVEL);

        FluidState fluidstate = state.getFluidState();
        if (fluidstate == Fluids.EMPTY.defaultFluidState())
            return (byte) -1;

        int waterLevel;
        if (fluidstate.isSource()) {
            waterLevel = 8;
        } else {
            waterLevel = fluidstate.getAmount();
        }
        return waterLevel;
    }


    public static boolean isWater(BlockState state) {
        return !state.isAir() && (state.getFluidState() != Fluids.EMPTY.defaultFluidState()) && !state.hasProperty(BlockStateProperties.WATERLOGGED);
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
            prev.hasProperty(WATER_LEVEL) ||
            !prev.getFluidState().isEmpty() ||
            level < 0;

        if (prev.hasProperty(WATER_LEVEL)) {
            setBlockStateNoNeighbors(pos, prev, prev.setValue(WATER_LEVEL, level));
        } else {
            if (level == 0) {
                setBlockStateNoNeighbors(pos, prev, Blocks.AIR.defaultBlockState());
            } else if (level < 0) {
                // System.out.println("Trying to set waterlevel " + level);
            } else if (level <= 8) {
                if (level == 8) {
                    if (!(prev.getBlock() instanceof LiquidBlockContainer)) { // Don't fill kelp etc
                        setBlockStateNoNeighbors(pos, prev, Blocks.WATER.defaultBlockState());
                    }
                } else {
                    if (!(prev.getBlock() instanceof BucketPickup)) {
                        world.destroyBlock(pos, true);
                    } else {
                        if (prev.getBlock() instanceof SimpleWaterloggedBlock) {
                            //TODO proper waterlogged flow
                        }
                    }

                    setBlockStateNoNeighbors(pos, prev, Fluids.FLOWING_WATER.getFlowing(level, false).createLegacyBlock());
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
            addWater(totalWater - 8, pos.above());
            setWaterLevel(8, pos);
        } else {
            setWaterLevel(totalWater, pos);
        }
    }

    public static BlockState getBlockState(BlockPos pos) {
        if (useSections) {
            if (pos.getY() < world.getMinBuildHeight() || pos.getY() > world.getMaxBuildHeight()) {
                return Blocks.AIR.defaultBlockState();
            }

            return getBlockStateSection(pos);
        } else {
            return world.getBlockState(pos);
        }
    }

    public static BlockState getBlockStateSection(BlockPos pos) {
        return sections.computeIfAbsent(SectionPos.of(pos), CachedWater::getChunkSection)
                .getBlockState(pos.getX() & 15, pos.getY() & 15, pos.getZ() & 15);
    }

    public static BlockState setBlockStateSection(BlockPos pos, BlockState state) {
        ((ServerLevel) world).getChunkSource().blockChanged(pos);
        return sections.computeIfAbsent(SectionPos.of(pos), CachedWater::getChunkSection)
                .setBlockState(pos.getX() & 15, pos.getY() & 15, pos.getZ() & 15, state, false);
    }

    public static LevelChunkSection getChunkSection(SectionPos pos) {
        LevelChunkSection result = world.getChunk(pos.center()).getSections()[world.getSectionIndexFromSectionY(pos.getY())];
        result.release(); // FIXME
        result.acquire();
        return result;
    }

    public static void main(String[] args) {
        System.out.println(Block.UPDATE_IMMEDIATE | Block.UPDATE_CLIENTS | Block.UPDATE_NEIGHBORS);
    }

    public static void setBlockStateNoNeighbors(BlockPos pos, BlockState oldState, BlockState state) {
        if (!state.getFluidState().isEmpty() && useSections) {
            setBlockStateSection(pos, state);
            world.sendBlockUpdated(pos, oldState, state, Block.UPDATE_IMMEDIATE | Block.UPDATE_CLIENTS | Block.UPDATE_KNOWN_SHAPE);

            fluidsToUpdate.put(pos, state);
        } else if (state.isAir()) {
            setBlockStateSection(pos, state);
            world.sendBlockUpdated(pos, oldState, state, Block.UPDATE_IMMEDIATE | Block.UPDATE_CLIENTS | Block.UPDATE_KNOWN_SHAPE);
        } else {
            // We could just make everything use setBlockStateSection but non fluid/air should be taken more care of
            world.setBlock(pos, state, Block.UPDATE_IMMEDIATE | Block.UPDATE_CLIENTS);
        }
    }

    public static void setup(ServerLevel world, BlockPos fluidPos) {
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

    public static void beforeTick(ServerLevel serverWorld) {
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
                neighborState.neighborChanged(world, pos, sourceBlock, neighborPos, false);
            } catch (Throwable var8) {
                CrashReport crashReport = CrashReport.forThrowable(var8, "Exception while updating neighbours");
                CrashReportCategory crashReportSection = crashReport.addCategory("Block being updated");
                crashReportSection.setDetail("Source block type", (CrashReportDetail<String>)(() -> {
                    try {
                        return String.format("ID #%s (%s // %s)", Registry.BLOCK.getKey(sourceBlock), sourceBlock.getDescriptionId(), sourceBlock.getClass().getCanonicalName());
                    } catch (Throwable var2x) {
                        return "ID #" + Registry.BLOCK.getKey(sourceBlock);
                    }
                }));
                CrashReportCategory.populateBlockDetails(crashReportSection, world, pos, neighborState);
                throw new ReportedException(crashReport);
            }
        }
    }

    public static void afterTick(ServerLevel serverWorld) {
        // TODO cache per dimension
        cache.clear();

        for (var entry : queuedWaterLevels.long2ByteEntrySet()) {
            BlockPos pos = BlockPos.of(entry.getLongKey());
            setWaterLevelDirect(entry.getByteValue(), pos);

            Block block = getBlockState(pos).getBlock();
            updateNeighbor(pos.west(), block, pos);
            updateNeighbor(pos.east(), block, pos);
            updateNeighbor(pos.below(), block, pos);
            updateNeighbor(pos.above(), block, pos);
            updateNeighbor(pos.north(), block, pos);
            updateNeighbor(pos.south(), block, pos);
        }

        for (var entry : fluidsToUpdate.entrySet()) {
            var state = entry.getValue();
            var pos = entry.getKey();

            world.scheduleTick(pos, state.getFluidState().getType(), state.getFluidState().getType().getTickDelay(world));
        }

        sections.forEach((sectionPos, section) -> section.release());

        fluidsToUpdate.clear();
        queuedWaterLevels.clear();
        sections.clear();
    }
}
