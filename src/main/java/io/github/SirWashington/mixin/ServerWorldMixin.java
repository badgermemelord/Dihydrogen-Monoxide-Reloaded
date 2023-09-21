package io.github.SirWashington.mixin;

import io.github.SirWashington.features.CachedWater;
import io.github.SirWashington.scheduling.*;
import net.minecraft.fluid.Fluid;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;

@Mixin(net.minecraft.server.world.ServerWorld.class)
public abstract class ServerWorldMixin implements MixinInterfaces.DuckInterface {

    @Unique
    private WorldCache perWorldCache = new WorldCache();
    @Unique
    private static ChunkListCache perWorldChunkList = new ChunkListCache();

    @Override
    public WorldCache getWorldCache() {
        return perWorldCache;
    }

    @Override
    public ChunkListCache getChunkListCache() {
        return perWorldChunkList;
    }

    @Inject(at = @At("HEAD"), method = "tick", cancellable = true)
    public void tick(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
        if (TickSpeedHandler.shouldTick()) {
            ServerLoadedChunkInterface.getActiveWorldChunks((ServerWorld) (Object) this);
            CachedWater.tickFluidsInWorld((ServerWorld) (Object) this);
            CachedWater.afterTick((ServerWorld) (Object) this);
        }
    }

    /**
     * @author SirWashington
     * @reason Vanilla fluid ticking is relieved of its duty
     */
    @Overwrite
    private void tickFluid(BlockPos pos, Fluid fluid) {

    }
}

