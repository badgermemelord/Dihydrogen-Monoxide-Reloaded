package io.github.SirWashington.mixin;

import io.github.SirWashington.features.NonCachedWater;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(net.minecraft.world.item.BucketItem.class)
public abstract class BucketMixin{

    @Redirect(
            method = "emptyContents",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"
                    //target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z"
            )
    )
    private boolean bucketPlace(Level instance, BlockPos pos, BlockState state, int flags) {
        boolean returnValue = true;
        if (!instance.isClientSide) {
            if(state.getBlock() == Blocks.WATER) {
                returnValue = NonCachedWater.addWater(8, pos, instance);
            }
            else {
                returnValue = instance.setBlockAndUpdate(pos, state);
            }
            return returnValue;
        }
        else {
            return returnValue;
        }
    }

    @Shadow
    private Fluid content;

    @Inject(
            at = @At("HEAD"), method = "emptyContents", cancellable = true)
    private void checkIfCanPlace(Player player, Level level, BlockPos blockPos, BlockHitResult bhr, CallbackInfoReturnable<Boolean> cir) {
        BlockState blockState = level.getBlockState(blockPos);
        Block block = blockState.getBlock();
        if (block instanceof LiquidBlockContainer) {
            if (blockState.getValue(BlockStateProperties.WATERLOGGED)) {
                cir.setReturnValue(false);
                return;
            }
            level.setBlock(blockPos, blockState.setValue(BlockStateProperties.WATERLOGGED, true), 3);
            cir.setReturnValue(true);
        }
    }
}
