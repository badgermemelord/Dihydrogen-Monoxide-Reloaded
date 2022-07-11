package io.github.CoolMineman.mixin;

import io.github.CoolMineman.FlowWater;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;

@Mixin(ServerWorld.class)
public class ServerWorldMixin {

    @Inject(at = @At("HEAD"), method = "tick")
    private void tick(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
        FlowWater.tick((ServerWorld) (Object) this);
    }

}
