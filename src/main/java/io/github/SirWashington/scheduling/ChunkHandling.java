package io.github.SirWashington.scheduling;

import io.github.SirWashington.mixin.ChunkMapAccessor;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.WorldChunk;
import org.apache.commons.compress.utils.Lists;

import java.util.*;

public class ChunkHandling {

    public static Set<Chunk> localChunkCache = new HashSet<>();

    public static void chunkFetcher(ServerWorld world) {

        ServerChunkManager chunkSource = world.toServerWorld().getChunkManager();
        ((ChunkMapAccessor) chunkSource.threadedAnvilChunkStorage).callGetChunkHolder();
        final List<ChunkHolder> loadedChunksList = Lists.newArrayList(
                ((ChunkMapAccessor) chunkSource.threadedAnvilChunkStorage).callGetChunkHolder().iterator());

        for (final ChunkHolder chunkHolder : loadedChunksList) {
            final Optional<WorldChunk> worldChunkOptional =
                    chunkHolder.getTickingFuture().getNow(ChunkHolder.UNLOADED_WORLD_CHUNK).left();
            if (worldChunkOptional.isPresent()) {
                final WorldChunk worldChunk = worldChunkOptional.get();
                localChunkCache.add(worldChunk);
            }
        }
        for(Chunk chunk: localChunkCache) {
            WaterTickScheduler.checkIfPresent(chunk.getPos(), world);
        }
    }



}
