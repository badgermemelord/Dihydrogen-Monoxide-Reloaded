package io.github.SirWashington.mixin;

import io.github.SirWashington.properties.WaterFluidProperties;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//@Mixin(WaterFluid.Still.class)
@Mixin(LiquidBlock.class)
public abstract class StillWaterPropertiesMixin {
    @Inject(at = @At("TAIL"), method = "appendProperties", cancellable = true)
    protected void appendProperties(StateDefinition.Builder<Fluid, FluidState> builder, CallbackInfo Ci) {
        builder.add(WaterFluidProperties.ISFINITE);
    }
/*    @Inject(at = @At("HEAD"), method = "appendProperties", cancellable = true)
    protected void appendProperties(StateManager.Builder<Fluid, FluidState> builder, CallbackInfo Ci) {
        builder.add(WaterFluidProperties.ISINFINITE);
    }*/
}