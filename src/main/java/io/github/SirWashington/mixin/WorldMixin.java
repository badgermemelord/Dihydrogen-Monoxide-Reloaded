package io.github.SirWashington.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(World.class)
public abstract class WorldMixin {

    @Shadow public abstract FluidState getFluidState(BlockPos pos);

    @Inject(at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/World;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;"), method = "updateNeighbor")
    void updateNeighborFluid(BlockPos pos, Block sourceBlock, BlockPos neighborPos, CallbackInfo ci) {
        FluidState fluid = getFluidState(pos);
        if (!fluid.isEmpty())
            ((ServerWorld) (Object) (this)).createAndScheduleFluidTick(pos, fluid.getFluid(), fluid.getFluid().getTickRate((World) (Object) this));
    }

}
