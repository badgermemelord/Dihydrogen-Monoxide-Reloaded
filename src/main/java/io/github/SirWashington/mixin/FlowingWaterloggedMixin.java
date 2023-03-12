package io.github.SirWashington.mixin;


import net.minecraft.block.BlockState;
import net.minecraft.block.Waterloggable;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(net.minecraft.server.world.ServerWorld.class)

public class FlowingWaterloggedMixin {

/*    @Inject(at = @At("HEAD"), method = "tickFluid", cancellable = true)
    private void tickFluid(BlockPos pos, Fluid fluid, CallbackInfo ci) {

    }
    @Redirect(at = @At("HEAD"), method = "tickFluid", target = "FluidState.isOf(fluid)")
    private void tickFluid(BlockPos pos, Fluid fluid) {

    }


    @Redirect(method = "tickFluid",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/fluid/FluidState;isOf(Lnet/minecraft/fluid/Fluid;)Z"))
    private void tickFluid(FluidState instance, Fluid fluid) {

    }*/

    /**
     * @author SirWashington
     * @reason not used
     */
    @Overwrite
    private void tickFluid(BlockPos pos, Fluid fluid) {
        //World.class.cast(this);
        BlockState blockState = World.class.cast(this).getBlockState(pos);
        FluidState fluidState = World.class.cast(this).getFluidState(pos);
        boolean isWaterLoggable = blockState.getBlock() instanceof Waterloggable;

        if (fluidState.isOf(fluid)) {
            fluidState.onScheduledTick(World.class.cast(this), pos);
            System.out.println("1");
        }
        if (isWaterLoggable && blockState.get(Properties.WATERLOGGED)) {
            fluidState.onScheduledTick(World.class.cast(this), pos);
            System.out.println("2");
        }


/*        if (fluidState.isOf(fluid) || blockState.get(Properties.WATERLOGGED)) {
            fluidState.onScheduledTick(World.class.cast(this), pos);
            System.out.println("deez");
        }*/
    }
}
