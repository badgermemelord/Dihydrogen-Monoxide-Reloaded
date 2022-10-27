package io.github.SirWashington.mixin;


import io.github.SirWashington.FlowWater;
import io.github.SirWashington.features.CachedWater;
import io.github.SirWashington.features.MixinTest;
import io.github.SirWashington.scheduling.WaterTickScheduler;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.Waterloggable;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkManager;
import org.apache.commons.compress.utils.Lists;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.function.BooleanSupplier;

@Mixin(net.minecraft.server.world.ServerWorld.class)

public abstract class FlowingWaterloggedMixin {

    @Shadow public abstract ServerWorld toServerWorld();

    @Shadow public abstract ChunkManager getChunkManager();

    @Shadow public abstract boolean isChunkLoaded(long chunkPos);

    @Shadow @Final private static int MAX_TICKS;

    @Inject(at = @At("HEAD"), method = "tick",  cancellable = true)
            public void tick(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
            ServerWorld.class.cast(this);
            //System.out.println("ticked");
            //System.out.println("next: " + WaterTickScheduler.BlocksToTickNext);
            WaterTickScheduler.BlocksToTick.addAll(WaterTickScheduler.BlocksToTickNext);
            //System.out.println("current: " + WaterTickScheduler.BlocksToTick);
            //WaterTickScheduler.clearNext();
            CachedWater.ScheduleFluidTick(this.toServerWorld());
            CachedWater.afterTick(this.toServerWorld());
            this.getChunkManager().getLoadedChunkCount();
            ServerChunkEvents.CHUNK_LOAD.getClass()


    }

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

    /**
     * @author SirWashington
     * @reason get outta here
     */
    @Overwrite
    private void tickFluid(BlockPos pos, Fluid fluid) {

    }
}
