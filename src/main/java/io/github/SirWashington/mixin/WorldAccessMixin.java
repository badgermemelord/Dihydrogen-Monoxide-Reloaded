package io.github.SirWashington.mixin;

import io.github.SirWashington.scheduling.ChunkHandlingMethods;
import net.minecraft.fluid.Fluid;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.TickPriority;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldAccess.class)
public interface WorldAccessMixin {

    //@Inject(at = @At("HEAD"), method = "createAndScheduleFluidTick", cancellable = true)
    @Overwrite
    default void createAndScheduleFluidTick(BlockPos pos, Fluid fluid, int delay) {
        if (this instanceof ServerWorld) {
            World world = (ServerWorld) this;
            System.out.println("e");
            ChunkHandlingMethods.scheduleFluidBlockExternal(pos, world);
        }
    }

}
