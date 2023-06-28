package io.github.SirWashington.mixin;


import io.github.SirWashington.features.CachedWater;
import io.github.SirWashington.scheduling.ServerLoadedChunkInterface;
import io.github.SirWashington.scheduling.TickSpeedHandler;
import net.minecraft.fluid.Fluid;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.ChunkManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;

@Mixin(net.minecraft.server.world.ServerWorld.class)

public abstract class FlowingWaterloggedMixin {

    @Shadow
    public abstract ServerWorld toServerWorld();

    @Shadow
    public abstract ChunkManager getChunkManager();

    @Shadow
    public abstract boolean isChunkLoaded(long chunkPos);

    @Shadow
    @Final
    private static int MAX_TICKS;

    @Inject(at = @At("HEAD"), method = "tick", cancellable = true)
    public void tick(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
        ServerWorld.class.cast(this);
        if(TickSpeedHandler.shouldTick()) {
            ServerWorld.class.cast(this);
            ServerLoadedChunkInterface.getActiveWorldChunks(this.toServerWorld());
            CachedWater.tickFluidsInWorld(this.toServerWorld());
            CachedWater.afterTick(this.toServerWorld());
        }
    }

        /**
         * @author SirWashington
         * @reason I am de captain now
         */
        @Overwrite
        private void tickFluid (BlockPos pos, Fluid fluid){

        }

}
