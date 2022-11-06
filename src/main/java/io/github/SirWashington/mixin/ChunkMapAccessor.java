package io.github.SirWashington.mixin;

import net.minecraft.server.world.ChunkHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/*@Mixin(net.minecraft.server.world.ServerChunkManager.class)
    public interface ChunkMapAccessor {
*//*
        @Invoker("updateChunkTracking")
        void callUpdateChunkTracking(ServerPlayerEntity player, ChunkPos pos, Packet<?>[] packets,
                                     boolean withinMaxWatchDistance, boolean withinViewDistance);
*//*

*//*        @Invoker("getChunks")
        Iterable<ChunkHolder> callGetChunks();*//*
    *//*@Invoker("getChunkHolder")
    Iterable<ChunkHolder> callGetChunkHolder();*//*

    @Invoker
    Iterable<ChunkHolder> callGetChunkHolder(long pos);
    }*/

@Mixin(net.minecraft.server.world.ThreadedAnvilChunkStorage.class)
    public interface ChunkMapAccessor {

    @Invoker("entryIterator")
    Iterable<ChunkHolder> callGetChunkHolder();
}
