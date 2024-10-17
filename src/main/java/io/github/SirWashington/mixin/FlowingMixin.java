package io.github.SirWashington.mixin;

import io.github.SirWashington.FlowWater;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(net.minecraft.world.level.material.FlowingFluid.class)
public class FlowingMixin {
    @Inject(at = @At("HEAD"), method = "canPassThrough", cancellable = true)
    private void canFlowThrough(BlockGetter world, Fluid fluid, BlockPos pos, BlockState state, Direction face, BlockPos fromPos, BlockState fromState, FluidState fluidState, CallbackInfoReturnable<Boolean> bruh) {
        if (isWater(fluid)) {
            bruh.setReturnValue(false);
        }
    }

    @Inject(at = @At("HEAD"), method = "canSpreadTo", cancellable = true)
    private void canFlow(BlockGetter world, BlockPos fluidPos, BlockState fluidBlockState, Direction flowDirection, BlockPos flowTo, BlockState flowToBlockState, FluidState fluidState, Fluid fluid, CallbackInfoReturnable<Boolean> bruh) {
        if (isWater(fluid)) {
            bruh.setReturnValue(false);
        }
    }

    @Inject(at = @At("HEAD"), method = "spread", cancellable = true)
    private void tryFlow(LevelAccessor world, BlockPos fluidPos, FluidState state, CallbackInfo bruh) {
        if (isWater(state.getType())) {
            FlowWater.flowWater(world, fluidPos, state);
            bruh.cancel();
        }
    }

    @Inject(at = @At("HEAD"), method = "getNewLiquid", cancellable = true)
    private void getUpdatedState(LevelReader world, BlockPos pos, BlockState state, CallbackInfoReturnable<FluidState> bruh) {
        FluidState fluidstate = state.getFluidState();
        if (isWater(fluidstate.getType())) {
            bruh.setReturnValue(Fluids.FLOWING_WATER.getFlowing(state.getFluidState().getAmount(), false));
        }
    }

    /**
     * @author ewoudje
     * @reason fck flowing animation
     */
    @Overwrite
    public Vec3 getFlow(BlockGetter world, BlockPos pos, FluidState state) {
        return Vec3.ZERO;
    }

    @Unique
    public boolean isWater(Fluid fluid) {
        return fluid == Fluids.WATER || fluid == Fluids.FLOWING_WATER;
    }
}