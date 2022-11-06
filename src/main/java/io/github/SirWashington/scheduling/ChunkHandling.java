package io.github.SirWashington.scheduling;

import io.github.SirWashington.mixin.ChunkMapAccessor;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkCache;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.WorldChunk;
import org.apache.commons.compress.utils.Lists;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ChunkHandling {

    public static List<Chunk> localChunkCache = new ArrayList<>();

    public static void ChunkFetcher(ServerWorld world) {

        //ChunkCache localCache = new ChunkCache()


        ServerChunkManager chunkSource = world.toServerWorld().getChunkManager();
        ((ChunkMapAccessor) chunkSource.threadedAnvilChunkStorage).callGetChunkHolder();
        final List<ChunkHolder> loadedChunksList = Lists.newArrayList(
                ((ChunkMapAccessor) chunkSource.threadedAnvilChunkStorage).callGetChunkHolder().iterator());

        for (ChunkHolder a : loadedChunksList) {
            if (a.getCurrentChunk() instanceof WorldChunk) {
                //System.out.println("chunk holder: " + a.getCurrentChunk());
            }
        }
        //System.out.println("end");

        for (final ChunkHolder chunkHolder : loadedChunksList) {
            final Optional<WorldChunk> worldChunkOptional =
                    chunkHolder.getTickingFuture().getNow(ChunkHolder.UNLOADED_WORLD_CHUNK).left();
            //System.out.println("wtf is this: " + chunkHolder.getTickingFuture().getNow(ChunkHolder.UNLOADED_WORLD_CHUNK).left());
            if (worldChunkOptional.isPresent()) {
                final WorldChunk worldChunk = worldChunkOptional.get();
                if (!localChunkCache.contains(worldChunk)) {
                    loadWaterFromChunk(worldChunk);
                }
                localChunkCache.add(worldChunk);
            }
        }
    }

    public static void loadWaterFromChunk(WorldChunk chunk) {

        //TODO cache the entire chunk or this will annihilate your computer

        World localWorld = chunk.getWorld();
        int chunkStartX = chunk.getPos().getStartX();
        int chunkStartZ = chunk.getPos().getStartZ();

        int worldMinY = localWorld.getBottomY();
        int worldMaxY = localWorld.getTopY();
        int sectionNo = (worldMaxY - worldMinY) / 16;


        List<ChunkSection> sectionList = new ArrayList<>(sectionNo);

        for (int a = 0; a < sectionNo; a++) {
            sectionList.add(chunk.getSection(a));
        }

        for (ChunkSection section : sectionList) {
            for (int a = 0; a < 15; a++) {
                for (int b = 0; b < 15; b++) {
                    for (int c = 0; c < 15; c++) {
                        BlockState internalBS = section.getBlockState(a, b, c);
                        BlockPos realWorldPos = new BlockPos(chunkStartX + a, chunkStartZ + b, section.getYOffset() + c);
                        Block internalBlock = internalBS.getBlock();
                        if (internalBlock == Blocks.WATER) {
                            WaterTickScheduler.scheduleFluidBlock(realWorldPos);
                        }
                    }
                }
            }

        /*for (int a = 0; a < 15; a++) {
            for (int b = 0; b < 15; b++) {
                for (int c = worldMinY; c <= worldMaxY; c++) {
                    BlockPos bp = new BlockPos(a, b, c);
                    BlockState bs = localWorld.getBlockState(bp);
                    Block block = bs.getBlock();
                    if (block == Blocks.WATER) {
                        WaterTickScheduler.scheduleFluidBlock(bp);
                    }
                }
            }
        }*/
        }


    }
}
