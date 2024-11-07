package io.github.SirWashington.mixin;

import io.github.SirWashington.features.NonCachedWater;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(net.minecraft.world.item.BucketItem.class)
public abstract class BucketMixin{

    @Redirect(
            method = "placeFluid",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z"
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


}
