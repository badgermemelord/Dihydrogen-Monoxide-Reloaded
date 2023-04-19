package io.github.SirWashington.scheduling;

import it.unimi.dsi.fastutil.longs.Long2ByteMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectArrayMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.LongSet;

public class WorldCache {
    public Long2ObjectMap<LongSet> Chunk2BlockMap = new Long2ObjectArrayMap<>();
    public Long2ObjectMap<Short> block2TicketMap = new Long2ObjectArrayMap<>();
}
