package io.github.SirWashington.mixin;

import io.github.SirWashington.FlowWater;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(net.minecraft.fluid.FlowableFluid.class)
public class FlowingMixin {
    @Inject(at = @At("HEAD"), method = "canFlowThrough", cancellable = true)
    private void canFlowThrough(BlockView world, Fluid fluid, BlockPos pos, BlockState state, Direction face, BlockPos fromPos, BlockState fromState, FluidState fluidState, CallbackInfoReturnable<Boolean> bruh) {
        if (isWater(fluid)) {
            bruh.setReturnValue(false);
        }
    }

    @Inject(at = @At("HEAD"), method = "canFlow", cancellable = true)
    private void canFlow(BlockView world, BlockPos fluidPos, BlockState fluidBlockState, Direction flowDirection, BlockPos flowTo, BlockState flowToBlockState, FluidState fluidState, Fluid fluid, CallbackInfoReturnable<Boolean> bruh) {
        if (isWater(fluid)) {
            bruh.setReturnValue(false);
        }
    }

    @Inject(at = @At("HEAD"), method = "tryFlow", cancellable = true)
    private void tryFlow(World world, BlockPos fluidPos, FluidState state, CallbackInfo ci) {
        if (isWater(state.getFluid())) {
            FlowWater.flowWater(world, fluidPos, state);
            ci.cancel();
        }
    }

    @Inject(at = @At("HEAD"), method = "getUpdatedState", cancellable = true)
    private void getUpdatedState(World world, BlockPos pos, BlockState state, CallbackInfoReturnable<FluidState> cir) {
        FluidState fluidstate = state.getFluidState();
        if (isWater(fluidstate.getFluid())) {
            cir.setReturnValue(Fluids.FLOWING_WATER.getFlowing(state.getFluidState().getLevel(), false));
        }
    }

    /**
     * @author ewoudje
     * @reason fck flowing animation
     */
    @Overwrite
    public Vec3d getVelocity(BlockView world, BlockPos pos, FluidState state) {
        return Vec3d.ZERO;
    }

    @Unique
    public boolean isWater(Fluid fluid) {
        return fluid == Fluids.WATER || fluid == Fluids.FLOWING_WATER;
    }
}