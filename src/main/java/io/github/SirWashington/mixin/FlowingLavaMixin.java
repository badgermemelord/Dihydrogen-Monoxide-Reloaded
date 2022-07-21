package io.github.SirWashington.mixin;

import io.github.SirWashington.FlowLava;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.LavaFluid;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(net.minecraft.fluid.FlowableFluid.class)
public class FlowingLavaMixin {
    @Inject(at = @At("HEAD"), method = "canFlowThrough", cancellable = true)
    private void canFlowThrough(BlockView world, Fluid fluid, BlockPos pos, BlockState state, Direction face, BlockPos fromPos, BlockState fromState, FluidState fluidState, CallbackInfoReturnable<Boolean> lbruh) {
        if (fluid instanceof LavaFluid) {
            lbruh.setReturnValue(false);
        }
    }

    @Inject(at = @At("HEAD"), method = "canFlow", cancellable = true)
    private void canFlow(BlockView world, BlockPos fluidPos, BlockState fluidBlockState, Direction flowDirection, BlockPos flowTo, BlockState flowToBlockState, FluidState fluidState, Fluid fluid, CallbackInfoReturnable<Boolean> lbruh) {
        if (fluid instanceof LavaFluid) {
            lbruh.setReturnValue(false);
        }
    }

    @Inject(at = @At("HEAD"), method = "tryFlow", cancellable = true)
    private void tryFlow(WorldAccess world, BlockPos fluidPos, FluidState state, CallbackInfo lbruh) {
        if ((state.getFluid() instanceof LavaFluid.Flowing) || (state.getFluid() instanceof LavaFluid.Still)) {
            FlowLava.flowlava(world, fluidPos, state);
            lbruh.cancel();
        }
    }

    @Inject(at = @At("HEAD"), method = "getUpdatedState", cancellable = true)
    private void getUpdatedState(WorldView world, BlockPos pos, BlockState state, CallbackInfoReturnable<FluidState> lbruh) {
        FluidState fluidstate = state.getFluidState();
        if (fluidstate.getFluid() instanceof LavaFluid.Flowing) {
            lbruh.setReturnValue(Fluids.FLOWING_LAVA.getFlowing(state.getFluidState().getLevel(), false));
        }
    }
}