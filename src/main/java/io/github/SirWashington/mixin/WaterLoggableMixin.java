package io.github.SirWashington.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static io.github.SirWashington.WaterPhysics.WATER_LEVEL;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;

@Mixin(SimpleWaterloggedBlock.class)
public interface WaterLoggableMixin {

    @Inject(at = @At("HEAD"), method = "canPlaceLiquid", cancellable = true)
    default void canFill(BlockGetter world, BlockPos pos, BlockState state, Fluid fluid, CallbackInfoReturnable<Boolean> cir) {
        if (state.hasProperty(WATER_LEVEL)) {
            cir.setReturnValue(state.getValue(WATER_LEVEL) < 8);
        }
    }

    @Inject(at = @At("HEAD"), method = "placeLiquid", cancellable = true)
    default void tryFill(LevelAccessor world, BlockPos pos, BlockState state, FluidState fluidState, CallbackInfoReturnable<Boolean> cir) {
        if (state.hasProperty(WATER_LEVEL)) {
            cir.setReturnValue(false);
            if (state.getValue(WATER_LEVEL) < 8) {
                world.setBlock(pos, state.setValue(WATER_LEVEL, 8), 3);
                cir.setReturnValue(true);
            }
        }
    }

    @Inject(at = @At("HEAD"), method = "pickupBlock", cancellable = true)
    default void tryDrain(LevelAccessor world, BlockPos pos, BlockState state, CallbackInfoReturnable<ItemStack> cir) {
        if (state.hasProperty(WATER_LEVEL)) {
            cir.setReturnValue(ItemStack.EMPTY);
            if (state.getValue(WATER_LEVEL) == 8) {
                world.setBlock(pos, state.setValue(WATER_LEVEL, 0), 3);
                cir.setReturnValue(new ItemStack(Items.WATER_BUCKET));
            }
        }
    }


}
