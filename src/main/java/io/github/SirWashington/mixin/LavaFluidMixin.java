package io.github.SirWashington.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(net.minecraft.fluid.LavaFluid.class)
public class LavaFluidMixin {
    @Inject(at = @At("HEAD"), method = "isInfinite", cancellable = true)
    private void isInfinite(CallbackInfoReturnable<Boolean> lbruh) {
        lbruh.setReturnValue(false);
    }
}