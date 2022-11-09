package io.github.SirWashington.mixin;


import io.github.SirWashington.features.CachedWater;
import io.github.SirWashington.scheduling.ChunkHandling;
import io.github.SirWashington.scheduling.TickSpeedHandler;
import io.github.SirWashington.scheduling.WaterTickScheduler;
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

        if(TickSpeedHandler.shouldTick()) {
            ServerWorld.class.cast(this);

            ChunkHandling.chunkFetcher(this.toServerWorld());

            CachedWater.ScheduleFluidTick(this.toServerWorld());

            CachedWater.afterTick(this.toServerWorld());
        }



/*        ServerChunkManager chunkSource = this.toServerWorld().getChunkManager();
        ((ChunkMapAccessor) chunkSource.threadedAnvilChunkStorage).callGetChunkHolder();
        final List<ChunkHolder> loadedChunksList = Lists.newArrayList(
                ((ChunkMapAccessor) chunkSource.threadedAnvilChunkStorage).callGetChunkHolder().iterator());

        for (final ChunkHolder chunkHolder : loadedChunksList) {
            final Optional<WorldChunk> worldChunkOptional =
                    chunkHolder.getTickingFuture().getNow(ChunkHolder.UNLOADED_WORLD_CHUNK).left();
            if (worldChunkOptional.isPresent()) {
                final WorldChunk worldChunk = worldChunkOptional.get();
            }
        }*/

/*    @Inject(at = @At("HEAD"), method = "tickFluid", cancellable = true)
    private void tickFluid(BlockPos pos, Fluid fluid, CallbackInfo ci) {

    }
    @Redirect(at = @At("HEAD"), method = "tickFluid", target = "FluidState.isOf(fluid)")
    private void tickFluid(BlockPos pos, Fluid fluid) {

    }


    @Redirect(method = "tickFluid",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/fluid/FluidState;isOf(Lnet/minecraft/fluid/Fluid;)Z"))
    private void tickFluid(FluidState instance, Fluid fluid) {

    }*/
    }
        /**
         * @author SirWashington
         * @reason I am de captain now
         */
        @Overwrite
        private void tickFluid (BlockPos pos, Fluid fluid){

        }

}
