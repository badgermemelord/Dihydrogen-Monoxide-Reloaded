package io.github.SirWashington.mixin;

import io.github.SirWashington.features.ConfigVariables;
import io.github.SirWashington.features.NonCachedWater;
import io.github.SirWashington.scheduling.ChunkHandlingMethods;
import net.minecraft.block.BlockState;
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
    private boolean bucketPlace(World world, BlockPos pos, BlockState state, int flags) {
        boolean returnValue = false;
        if (!world.isClient) {
            //System.out.println("BucketTool at pos: " + pos + " with volume: " + CachedWater.getWaterVolume(pos));
            returnValue = NonCachedWater.addVolume(ConfigVariables.volumePerBlock, pos, world);
            ChunkHandlingMethods.addDefaultTickets(pos.asLong(), world);
            ChunkHandlingMethods.scheduleFluidBlock(pos, world);
            return returnValue;
        }
        else {
            return returnValue;
        }
    }
}
