package io.github.SirWashington.scheduling;

import io.github.SirWashington.mixin.ChunkMapAccessor;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.WorldChunk;
import org.apache.commons.compress.utils.Lists;

import java.util.*;

public class ChunkHandling {
    public static void chunkTick(ServerWorld world) {
        //System.out.println("Tick start");

        //LongSet ChunkCache = ((MixinInterfaces.DuckInterface)world).getChunkListCache().ChunkList;
        ServerChunkManager chunkSource = world.toServerWorld().getChunkManager();
        ((ChunkMapAccessor) chunkSource.threadedAnvilChunkStorage).callGetChunkHolder();
        final List<ChunkHolder> loadedChunksList = Lists.newArrayList(
                ((ChunkMapAccessor) chunkSource.threadedAnvilChunkStorage).callGetChunkHolder().iterator());
        //System.out.println(loadedChunksList.isEmpty());
        for (final ChunkHolder chunkHolder : loadedChunksList) {
            final Optional<WorldChunk> worldChunkOptional =
                    chunkHolder.getTickingFuture().getNow(ChunkHolder.UNLOADED_WORLD_CHUNK).left();
            if (worldChunkOptional.isPresent()) {
                final WorldChunk worldChunk = worldChunkOptional.get();
                ((MixinInterfaces.DuckInterface)world).getChunkListCache().ChunkList.add(worldChunk.getPos().toLong());
            }
        }
        for(Long longe : ((MixinInterfaces.DuckInterface)world).getChunkListCache().ChunkList) {
            WaterTickScheduler.checkIfPresent(longe, world);
        }
        WaterTickScheduler.checkForAbsent(((MixinInterfaces.DuckInterface)world).getChunkListCache().ChunkList, world);






    }



}
