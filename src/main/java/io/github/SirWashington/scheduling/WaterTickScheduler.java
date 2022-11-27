package io.github.SirWashington.scheduling;

import io.github.SirWashington.FlowWater;
import io.github.SirWashington.features.CachedWater;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.longs.*;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.*;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static io.github.SirWashington.FlowWater.world;

public class WaterTickScheduler {

    //public static ArrayList<BlockPos> CurrentToTick = new ArrayList<>();
    //public static ArrayList<BlockPos> NextToTick = new ArrayList<>();


    //public static List<Long> BlocksToTickNext = new ArrayList<>();
    //public static List<List<Long>> Chunks = new ArrayList<>();

    //public static HashMap<Long, List<Long>> Chunk2BlockMap = new HashMap<>();
    //public static Long2ObjectMap<LongSet> Chunk2BlockMap = new Long2ObjectArrayMap<>();
    //public static WorldCache localCache = new WorldCache();


    //public static List<Long> BlocksToTick = new ArrayList<>();

    public static void unloadChunk(ChunkPos chunkPos, World world) {
        long posToUnload = chunkPos.toLong();
        ChunkHandling.localCache.Chunk2BlockMap.remove(posToUnload);

    }
    public static void checkForAbsent(LongSet ChunkCache, World world) {
        //System.out.println("bal");
        //System.out.println(ChunkCache);
        for(Long keyLong : ChunkHandling.localCache.Chunk2BlockMap.keySet()) {
            if(!ChunkCache.contains(keyLong)) {
                ChunkHandling.localCache.Chunk2BlockMap.remove(keyLong);
                //System.out.println("removed");
            }
        }
    }
    public static void checkIfPresent(long chunkPosLong, World world) {
        if (!ChunkHandling.localCache.Chunk2BlockMap.containsKey(chunkPosLong)){
            preLoadChunk(chunkPosLong, world);
        }
    }
    public static void preLoadChunk(long chunkPosLong, World world) {
        int posX = ChunkPos.getPackedX(chunkPosLong);
        int posZ = ChunkPos.getPackedZ(chunkPosLong);
        ChunkPos pos = new ChunkPos(posX, posZ);
        WorldChunk chunk = world.getWorldChunk(pos.getStartPos());
        LongSet waterBlocksSet = getWaterInChunk(chunk);
        loadChunk(chunkPosLong, waterBlocksSet);
    }

    public static void loadChunk(long posToLoad, LongSet waterBlocksSet) {
        ChunkHandling.localCache.Chunk2BlockMap.put(posToLoad, waterBlocksSet);
    }

    public static void scheduleFluidBlock(BlockPos pos, World localWorld) {

        ChunkPos chunkPos = localWorld.getChunk(pos).getPos();
        long chunkPosAsLong = chunkPos.toLong();
        long blockPosAsLong = pos.asLong();

        if(ChunkHandling.localCache.Chunk2BlockMap.containsKey(chunkPosAsLong)) {
            //Chunk2BlockMap.computeIfAbsent(chunkPosAsLong, s -> getLongSet(blockPosAsLong));
            LongSet oldSet = ChunkHandling.localCache.Chunk2BlockMap.get(chunkPosAsLong);
            oldSet.add(blockPosAsLong);
            ChunkHandling.localCache.Chunk2BlockMap.put(chunkPosAsLong, oldSet);
        }
        else {
            LongSet putValue = new LongOpenHashSet();
            putValue.add(blockPosAsLong);
            ChunkHandling.localCache.Chunk2BlockMap.put(chunkPosAsLong, putValue);
        }
    }

    public static LongSet getLongSet(long pos) {
        LongSet set = new LongOpenHashSet();
        set.add(pos);
        return set;
    }

    public static LongSet getWaterInChunk(WorldChunk chunk) {

        //System.out.println("getwater start");
        World localWorld = chunk.getWorld();
        int chunkStartX = chunk.getPos().getStartX();
        int chunkStartZ = chunk.getPos().getStartZ();

        int worldMinY = localWorld.getBottomY();
        int worldMaxY = localWorld.getTopY();
        int sectionNo = (worldMaxY - worldMinY) / 16;

        LongSet blockSet = new LongOpenHashSet();

        List<ChunkSection> sectionList = new ArrayList<>(sectionNo);

        for (int a = 0; a < sectionNo; a++) {
            sectionList.add(chunk.getSection(a));
        }

        for (ChunkSection section : sectionList) {
            for (int a = 0; a < 15; a++) {
                for (int b = 0; b < 15; b++) {
                    for (int c = 0; c < 15; c++) {
                        BlockState internalBS = section.getBlockState(a, b, c);
                        BlockPos realWorldPos = new BlockPos(chunkStartX + a, section.getYOffset() + b, chunkStartZ + c);
                        long realWorldPosLong = realWorldPos.asLong();
                        Block internalBlock = internalBS.getBlock();
                        if (internalBlock == Blocks.WATER) {
                            blockSet.add(realWorldPosLong);
                            //System.out.println("wotah");
                            //System.out.println(realWorldPos);
                        }
                    }
                }
            }
        }
        return blockSet;
    }
    //public static void clearQueue() { BlocksToTick.clear();}
    // static void clearNext() { BlocksToTickNext.clear();}


/*    public static void tickFluid(World world) {
        System.out.println("curr " +  CurrentToTick);
        System.out.println("next " +  NextToTick);

        for(BlockPos iterator : NextToTick) {
            CurrentToTick.add(iterator);
        }
        //CurrentToTick = NextToTick;
        NextToTick.clear();
        //System.out.println("curr2 " +  CurrentToTick);
        for(BlockPos BP : CurrentToTick) {
            //FluidState FS = CachedWater.getBlockState(BP).getFluidState();
            System.out.println("bp: " + BP);
            //FlowWater.flowwater(world, BP, FS);
            //FlowWater.testTick(world, BP);
        }
        CurrentToTick.clear();
    }*/

}
