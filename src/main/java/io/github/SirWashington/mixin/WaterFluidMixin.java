package io.github.SirWashington.mixin;

import net.minecraft.world.level.LevelReader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(net.minecraft.world.level.material.WaterFluid.class)
public class WaterFluidMixin {
    @Inject(at = @At("HEAD"), method = "canConvertToSource", cancellable = true)
    private void isInfinite(CallbackInfoReturnable<Boolean> bruh) {
        bruh.setReturnValue(false);
    }

    @Inject(at = @At("HEAD"), method = "getTickDelay", cancellable = true)
    private void getTickRate(LevelReader world, CallbackInfoReturnable<Integer> bruh) {
        bruh.setReturnValue(2);
    }
}