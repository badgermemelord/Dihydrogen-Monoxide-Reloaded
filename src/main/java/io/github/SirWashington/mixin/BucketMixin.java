package io.github.SirWashington.mixin;

import io.github.SirWashington.features.NonCachedWater;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(net.minecraft.item.BucketItem.class)
public abstract class BucketMixin{

    @Redirect(
            method = "placeFluid",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z"
            )
    )
    private boolean bucketPlace(World instance, BlockPos pos, BlockState state, int flags) {
        boolean returnValue = true;
        if (!instance.isClient) {
            returnValue = NonCachedWater.addWater(8, pos, instance);
            return returnValue;
        }
        else {
            return returnValue;
        }
    }


}
