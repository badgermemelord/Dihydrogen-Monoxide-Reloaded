package io.github.SirWashington.scheduling;

import io.github.SirWashington.FlowWater;
import io.github.SirWashington.features.CachedWater;
import it.unimi.dsi.fastutil.longs.Long2LongArrayMap;
import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrays;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.EmptyChunk;

import java.util.ArrayList;
import java.util.List;

import static io.github.SirWashington.FlowWater.world;

public class WaterTickScheduler {

    //public static ArrayList<BlockPos> CurrentToTick = new ArrayList<>();
    //public static ArrayList<BlockPos> NextToTick = new ArrayList<>();


    public static List<Long> BlocksToTickNext = new ArrayList<>();
    public static List<List<Long>> Chunks = new ArrayList<>();
    public static Long2LongArrayMap Chunk2BlockMap = new Long2LongArrayMap();
    public static List<Long> BlocksToTick = new ArrayList<>();

    public static void unloadChunk(ChunkPos chunkPos) {
        Long posToUnload = chunkPos.toLong();
        Chunk2BlockMap.remove(posToUnload);
    }
    public static void loadChunk(ChunkPos chunkPos, Long[] waterBlocksArray) {
        Long posToLoad = chunkPos.toLong();
        Chunk2BlockMap.put(posToLoad, waterBlocksArray);
        Chunk2BlockMap.
    }

    public static void scheduleFluidBlock(BlockPos pos, ChunkPos chunkPos) {

        Long chunkPosAsLong = chunkPos.toLong();

        if(!BlocksToTickNext.contains(pos.asLong())) {
            BlocksToTickNext.add(pos.asLong());
        }
    }
    public static void clearQueue() { BlocksToTick.clear();}
    public static void clearNext() { BlocksToTickNext.clear();}


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
