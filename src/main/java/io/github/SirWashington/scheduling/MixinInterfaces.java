package io.github.SirWashington.scheduling;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.LongSet;

public abstract class MixinInterfaces {

    public interface DuckInterface {
        //public abstract Long2ObjectMap<LongSet> getWorldCache();
        public abstract WorldCache getWorldCache();
    }

}
