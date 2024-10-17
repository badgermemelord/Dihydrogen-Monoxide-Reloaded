package io.github.SirWashington.mixin;

import io.github.SirWashington.features.CachedWater;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;
import net.minecraft.server.level.ServerLevel;

@Mixin(ServerLevel.class)
public class ServerWorldMixin {

    @Inject(at = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/world/ticks/LevelTicks;tick(JILjava/util/function/BiConsumer;)V",
                //target = "Lnet/minecraft/world/tick/WorldTickScheduler;tick(JILjava/util/function/BiConsumer;)V",
                ordinal = 1,
                shift = At.Shift.BEFORE),
            method = "tick")
    private void beforeFluidTick(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
        CachedWater.beforeTick((ServerLevel) (Object) this);
    }

    @Inject(at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/ticks/LevelTicks;tick(JILjava/util/function/BiConsumer;)V",
            //target = "Lnet/minecraft/world/tick/WorldTickScheduler;tick(JILjava/util/function/BiConsumer;)V",
            ordinal = 1,
            shift = At.Shift.AFTER),
            method = "tick")
    private void afterFluidTick(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
        CachedWater.afterTick((ServerLevel) (Object) this);
    }

}
