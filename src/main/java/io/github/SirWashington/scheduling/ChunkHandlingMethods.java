package io.github.SirWashington.scheduling;

import it.unimi.dsi.fastutil.longs.*;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.*;

import java.util.*;

public class ChunkHandlingMethods {

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
        //ChunkHandling.localCache.Chunk2BlockMap.remove(posToUnload);
        ((MixinInterfaces.DuckInterface)world).getWorldCache().Chunk2BlockMap.remove(posToUnload);

    }
    public static void checkForNoLongerPresent(LongSet ChunkList, World world) {
        //TODO concurrent this
        for(long keyLong : ((MixinInterfaces.DuckInterface)world).getWorldCache().Chunk2BlockMap.keySet()) {
            if(!ChunkList.contains(keyLong)) {
                removeTicketsForChunk(keyLong, world);
                ((MixinInterfaces.DuckInterface)world).getWorldCache().Chunk2BlockMap.remove(keyLong);
                //System.out.println("removed " + keyLong);
            }
        }
    }
    public static void checkForAlreadyPresent(LongSet ChunkList, World world) {
        for(long keyLong : ChunkList) {
            if(!((MixinInterfaces.DuckInterface)world).getWorldCache().Chunk2BlockMap.keySet().contains(keyLong)) {
                preLoadChunk(keyLong, world);
            }
        }
    }
    public static void checkForTicketLess(World world) {
        for (long fluidPos :  ((MixinInterfaces.DuckInterface)world).getWorldCache().block2TicketMap.keySet()) {
            if(((MixinInterfaces.DuckInterface)world).getWorldCache().block2TicketMap.get(fluidPos) == (short) 0) {
                //((MixinInterfaces.DuckInterface)world).getWorldCache().block2TicketMap.remove(fluidPos);
                unScheduleFluidBlock(fluidPos, world);
            }
        }
    }
    public static boolean checkIfTicketLess(long fluidPos, World world) {
        //System.out.println("tickets: " + ((MixinInterfaces.DuckInterface)world).getWorldCache().block2TicketMap.get(fluidPos));
        if(((MixinInterfaces.DuckInterface)world).getWorldCache().block2TicketMap.get(fluidPos) == (short) 0) {
            //System.out.println("ticketless");
            unScheduleFluidBlock(fluidPos, world);
            return true;
        }
        return false;
    }
    public static void checkIfPresent(long chunkPosLong, World world) {
        //System.out.println("ddn: " + ((MixinInterfaces.DuckInterface)world).getWorldCache().Chunk2BlockMap.keySet());
        if (!((MixinInterfaces.DuckInterface)world).getWorldCache().Chunk2BlockMap.containsKey(chunkPosLong)){
            //System.out.println(((MixinInterfaces.DuckInterface)world).getWorldCache().Chunk2BlockMap);
            //System.out.println("chunkpos that was not contained: " + chunkPosLong);
            //System.out.println("not contained");
            preLoadChunk(chunkPosLong, world);
        }
    }
    public static void preLoadChunk(long chunkPosLong, World world) {
        int posX = ChunkPos.getPackedX(chunkPosLong);
        int posZ = ChunkPos.getPackedZ(chunkPosLong);
        ChunkPos pos = new ChunkPos(posX, posZ);
        WorldChunk chunk = world.getWorldChunk(pos.getStartPos());
        LongSet waterBlocksSet = getWaterInChunk(chunk, world);
        loadChunk(chunkPosLong, waterBlocksSet, world);
    }

    public static void loadChunk(long posToLoad, LongSet waterBlocksSet, World world) {
        //System.out.println("pos to load: " + posToLoad);
        ((MixinInterfaces.DuckInterface)world).getWorldCache().Chunk2BlockMap.put(posToLoad, waterBlocksSet);
    }

    public static void scheduleFluidBlock(BlockPos pos, World world) {

        ChunkPos chunkPos = world.getChunk(pos).getPos();
        long chunkPosAsLong = chunkPos.toLong();
        long blockPosAsLong = pos.asLong();

        if(((MixinInterfaces.DuckInterface)world).getWorldCache().Chunk2BlockMap.containsKey(chunkPosAsLong)) {
            //Chunk2BlockMap.computeIfAbsent(chunkPosAsLong, s -> getLongSet(blockPosAsLong));
            LongSet oldSet = ((MixinInterfaces.DuckInterface)world).getWorldCache().Chunk2BlockMap.get(chunkPosAsLong);
            oldSet.add(blockPosAsLong);
            ((MixinInterfaces.DuckInterface)world).getWorldCache().Chunk2BlockMap.put(chunkPosAsLong, oldSet);
            registerTickTickets(blockPosAsLong, world);
        }
/*        else {
            System.out.println("tried to load new chunk");
            LongSet putValue = new LongOpenHashSet();
            putValue.add(blockPosAsLong);
            ((MixinInterfaces.DuckInterface)world).getWorldCache().Chunk2BlockMap.put(chunkPosAsLong, putValue);
            registerTickTickets(blockPosAsLong, world);
        }*/
    }
    public static void unScheduleFluidBlock(long blockPosAsLong, World world) {
        ChunkPos chunkPos = world.getChunk(BlockPos.fromLong(blockPosAsLong)).getPos();
        long chunkPosAsLong = chunkPos.toLong();
        //TODO optimise this, try new approach
/*        if(((MixinInterfaces.DuckInterface)world).getWorldCache().Chunk2BlockMap.containsValue(blockPosAsLong)) {
            LongSet oldSet = ((MixinInterfaces.DuckInterface)world).getWorldCache().Chunk2BlockMap.get(chunkPosAsLong);
            if(oldSet.contains(blockPosAsLong)) {
                oldSet.remove(blockPosAsLong);
                ((MixinInterfaces.DuckInterface)world).getWorldCache().Chunk2BlockMap.put(chunkPosAsLong, oldSet);
                ((MixinInterfaces.DuckInterface)world).getWorldCache().block2TicketMap.remove(blockPosAsLong);
            }
        }*/
        LongSet oldSet = ((MixinInterfaces.DuckInterface)world).getWorldCache().Chunk2BlockMap.get(chunkPosAsLong);
        if(oldSet.contains(blockPosAsLong)) {
            oldSet.remove(blockPosAsLong);
            ((MixinInterfaces.DuckInterface)world).getWorldCache().Chunk2BlockMap.put(chunkPosAsLong, oldSet);
            ((MixinInterfaces.DuckInterface)world).getWorldCache().block2TicketMap.remove(blockPosAsLong);
        }
    }
    public static LongSet getLongSet(long pos) {
        LongSet set = new LongOpenHashSet();
        set.add(pos);
        return set;
    }

    public static LongSet getWaterInChunk(WorldChunk chunk, World world) {

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
                            registerTickTicketsOnLoad(realWorldPosLong, world);
                            //System.out.println("wotah");
                            //System.out.println(realWorldPos);
                        }
                    }
                }
            }
        }
        return blockSet;
    }

    public static void registerTickTickets(long fluidPos, World world) {
        Short defaultStartingTickets = 3;
        //if(!((MixinInterfaces.DuckInterface)world).getWorldCache().block2TicketMap.keySet().contains(fluidPos))
            ((MixinInterfaces.DuckInterface)world).getWorldCache().block2TicketMap.put(fluidPos, defaultStartingTickets);
    }
    public static void registerTickTicketsOnLoad(long fluidPos, World world) {
        Short defaultStartingTickets = 1;
        //if(!((MixinInterfaces.DuckInterface)world).getWorldCache().block2TicketMap.keySet().contains(fluidPos))
        ((MixinInterfaces.DuckInterface)world).getWorldCache().block2TicketMap.put(fluidPos, defaultStartingTickets);
    }
    public static void subtractTickTicket(BlockPos blockPos, World world) {
        long fluidPos = blockPos.asLong();
        Short oldTickets = ((MixinInterfaces.DuckInterface)world).getWorldCache().block2TicketMap.get(fluidPos);
        Short newTickets = (short) (oldTickets - 1);
        ((MixinInterfaces.DuckInterface)world).getWorldCache().block2TicketMap.put(fluidPos, newTickets);
    }
    public static void subtractTickTickets(World world) {
        ((MixinInterfaces.DuckInterface)world).getWorldCache().block2TicketMap.replaceAll((block, tickets) -> subtractFromShort(tickets));
        ((MixinInterfaces.DuckInterface)world).getWorldCache().block2TicketMap.forEach((block, tickets) -> removeIfNoTickets(block, tickets, world));
    }
    public static void removeTicketsForChunk(long chunkPos, World world) {
        LongSet set = ((MixinInterfaces.DuckInterface)world).getWorldCache().Chunk2BlockMap.get(chunkPos);
        set.forEach((long l) -> removeFromTicketMap(l, world));
    }
    public static void removeFromTicketMap(long l, World world) {
        if(((MixinInterfaces.DuckInterface)world).getWorldCache().block2TicketMap.containsKey(l)) {
            ((MixinInterfaces.DuckInterface)world).getWorldCache().block2TicketMap.remove(l);
        }

    }
    public static void removeIfNoTickets(long blockPos, short tickets, World world) {
        if (tickets < 1) {
            unScheduleFluidBlock(blockPos, world);
        }
    }
    public static short subtractFromShort(short s) {
        return (short) (s - 1);
    }
    public static void subtractTicketFromBlock(long blockPos, World world) {
        //Short oldTickets = ((MixinInterfaces.DuckInterface)world).getWorldCache().block2TicketMap.get(blockPos);
        Short newTickets = (short) (((MixinInterfaces.DuckInterface)world).getWorldCache().block2TicketMap.get(blockPos) - 1);
        if (newTickets < 1) {
            unScheduleFluidBlock(blockPos, world);
        }
        else{
            ((MixinInterfaces.DuckInterface)world).getWorldCache().block2TicketMap.put(blockPos, newTickets);
        }

    }


}
