package io.github.SirWashington.mixin;

import io.github.SirWashington.features.CachedWater;
import io.github.SirWashington.scheduling.MixinInterfaces;
import io.github.SirWashington.scheduling.WorldCache;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;

public abstract class ServerWorldMixin {
    @Mixin(targets = "net.minecraft.server.world.ServerWorld.class")
    public class ServerWorld implements MixinInterfaces.DuckInterface {
        public static WorldCache perWorldCache = new WorldCache();
        @Override
        public WorldCache getWorldCache() {
            return perWorldCache;
        }
    }
}

