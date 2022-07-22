package io.github.SirWashington.mixin;

import net.minecraft.world.WorldView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(net.minecraft.fluid.WaterFluid.class)
public class WaterFluidMixin {
    @Inject(at = @At("HEAD"), method = "isInfinite", cancellable = true)
    private void isInfinite(CallbackInfoReturnable<Boolean> bruh) {
        bruh.setReturnValue(false);
    }
	@Inject(at = @At("HEAD"), method = "getTickRate", cancellable = true)
	private void getTickRate(WorldView world, CallbackInfoReturnable<Integer> bruh) {
		bruh.setReturnValue(2);
	}
}