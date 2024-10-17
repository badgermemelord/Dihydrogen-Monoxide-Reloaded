package io.github.SirWashington.mixin;


import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(net.minecraft.server.level.ServerLevel.class)

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
     * @author Dn
     * @reason Mald
     */
    @Overwrite
    private void tickFluid(BlockPos pos, Fluid fluid) {
        //World.class.cast(this);
        BlockState blockState = Level.class.cast(this).getBlockState(pos);
        FluidState fluidState = Level.class.cast(this).getFluidState(pos);
        boolean isWaterLoggable = blockState.getBlock() instanceof SimpleWaterloggedBlock;

        if (fluidState.is(fluid)) {
            fluidState.tick(Level.class.cast(this), pos);
            System.out.println("deez");
        }
        if (isWaterLoggable && blockState.getValue(BlockStateProperties.WATERLOGGED)) {
            fluidState.tick(Level.class.cast(this), pos);
            System.out.println("deez2");
        }


/*        if (fluidState.isOf(fluid) || blockState.get(Properties.WATERLOGGED)) {
            fluidState.onScheduledTick(World.class.cast(this), pos);
            System.out.println("deez");
        }*/
    }
}
