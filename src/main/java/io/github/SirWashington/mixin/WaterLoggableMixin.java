package io.github.SirWashington.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.Waterloggable;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static io.github.SirWashington.WaterPhysics.WATER_LEVEL;

@Mixin(Waterloggable.class)
public interface WaterLoggableMixin {

    @Inject(at = @At("HEAD"), method = "canFillWithFluid", cancellable = true)
    default void canFill(BlockView world, BlockPos pos, BlockState state, Fluid fluid, CallbackInfoReturnable<Boolean> cir) {
        if (state.contains(WATER_LEVEL)) {
            cir.setReturnValue(state.get(WATER_LEVEL) < 8);
        }
    }

    @Inject(at = @At("HEAD"), method = "tryFillWithFluid", cancellable = true)
    default void tryFill(WorldAccess world, BlockPos pos, BlockState state, FluidState fluidState, CallbackInfoReturnable<Boolean> cir) {
        if (state.contains(WATER_LEVEL)) {
            cir.setReturnValue(false);
            if (state.get(WATER_LEVEL) < 8) {
                world.setBlockState(pos, state.with(WATER_LEVEL, 8), 3);
                cir.setReturnValue(true);
            }
        }
    }

    @Inject(at = @At("HEAD"), method = "tryDrainFluid", cancellable = true)
    default void tryDrain(WorldAccess world, BlockPos pos, BlockState state, CallbackInfoReturnable<ItemStack> cir) {
        if (state.contains(WATER_LEVEL)) {
            cir.setReturnValue(ItemStack.EMPTY);
            if (state.get(WATER_LEVEL) == 8) {
                world.setBlockState(pos, state.with(WATER_LEVEL, 0), 3);
                cir.setReturnValue(new ItemStack(Items.WATER_BUCKET));
            }
        }
    }


}
