package io.github.SirWashington.scheduling;

import it.unimi.dsi.fastutil.longs.Long2ByteMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectArrayMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.LongSet;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class WorldCache {
    public Long2ObjectMap<LongSet> Chunk2BlockMap = new Long2ObjectArrayMap<>();
    //public ConcurrentHashMap<Long, LongSet> Chunk2BlockMap = new ConcurrentHashMap<>();
    //public Long2ObjectMap<Short> block2TicketMap = new Long2ObjectArrayMap<>();
    //public HashMap<Long, Short> block2TicketMap = new HashMap<>();
    public ConcurrentHashMap<Long, Short> block2TicketMap = new ConcurrentHashMap<>();
}
