package io.github.SirWashington.mixin;

import io.github.SirWashington.properties.WaterFluidProperties;
import net.minecraft.block.FluidBlock;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.state.StateManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//@Mixin(WaterFluid.Still.class)
@Mixin(FluidBlock.class)
public abstract class StillWaterPropertiesMixin {
    @Inject(at = @At("TAIL"), method = "appendProperties", cancellable = true)
    protected void appendProperties(StateManager.Builder<Fluid, FluidState> builder, CallbackInfo Ci) {
        builder.add(WaterFluidProperties.ISFINITE);
        builder.add(WaterFluidProperties.VOLUME);
    }
/*    @Inject(at = @At("HEAD"), method = "appendProperties", cancellable = true)
    protected void appendProperties(StateManager.Builder<Fluid, FluidState> builder, CallbackInfo Ci) {
        builder.add(WaterFluidProperties.ISINFINITE);
    }*/
}