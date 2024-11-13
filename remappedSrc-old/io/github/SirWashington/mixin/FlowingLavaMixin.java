package io.github.SirWashington.mixin;

import io.github.SirWashington.FlowLava;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.LavaFluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(net.minecraft.world.level.material.FlowingFluid.class)
public class FlowingLavaMixin {
    @Inject(at = @At("HEAD"), method = "canFlowThrough", cancellable = true)
    private void canFlowThrough(BlockGetter world, Fluid fluid, BlockPos pos, BlockState state, Direction face, BlockPos fromPos, BlockState fromState, FluidState fluidState, CallbackInfoReturnable<Boolean> lbruh) {
        if (fluid instanceof LavaFluid) {
            lbruh.setReturnValue(false);
        }
    }

    @Inject(at = @At("HEAD"), method = "canFlow", cancellable = true)
    private void canFlow(BlockGetter world, BlockPos fluidPos, BlockState fluidBlockState, Direction flowDirection, BlockPos flowTo, BlockState flowToBlockState, FluidState fluidState, Fluid fluid, CallbackInfoReturnable<Boolean> lbruh) {
        if (fluid instanceof LavaFluid) {
            lbruh.setReturnValue(false);
        }
    }

    @Inject(at = @At("HEAD"), method = "tryFlow", cancellable = true)
    private void tryFlow(LevelAccessor world, BlockPos fluidPos, FluidState state, CallbackInfo lbruh) {
        if ((state.getType() instanceof LavaFluid.Flowing) || (state.getType() instanceof LavaFluid.Source)) {
            FlowLava.flowlava(world, fluidPos, state);
            lbruh.cancel();
        }
    }

    @Inject(at = @At("HEAD"), method = "getUpdatedState", cancellable = true)
    private void getUpdatedState(LevelReader world, BlockPos pos, BlockState state, CallbackInfoReturnable<FluidState> lbruh) {
        FluidState fluidstate = state.getFluidState();
        if (fluidstate.getType() instanceof LavaFluid.Flowing) {
            lbruh.setReturnValue(Fluids.FLOWING_LAVA.getFlowing(state.getFluidState().getAmount(), false));
        }
    }
}