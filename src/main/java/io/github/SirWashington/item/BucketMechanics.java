package io.github.SirWashington.item;

import io.github.SirWashington.features.NonCachedWater;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.minecraft.entity.player.PlayerEntity;


public class BucketMechanics {


    public static boolean precisionBucketPlace(World level, BlockPos pos, ItemStack itemStack, PlayerEntity player) {

        int bucketFillLevel = itemStack.getNbt().getInt("washwater:bucketFillWorld");
        int newBucketFillLevel = 0;

        if (bucketFillLevel > 0 && !level.isClient) {
            BlockHitResult blockHitResult = getPlayerEntityPOVHitResult(level, player, RaycastContext.FluidHandling.NONE);
            BlockPos blockPos = blockHitResult.getBlockPos();
            Direction direction = blockHitResult.getSide();
            BlockPos blockPos2 = blockPos.offset(direction);
            NonCachedWater.addWater(bucketFillLevel, blockPos2, level);
            NbtCompound tag = new NbtCompound();
            tag.putInt("washwater:bucketFillWorld", newBucketFillLevel);
            itemStack.setNbt(tag);
        }
        return true;
    }
    public static boolean precisionBucketPickup(World level, BlockPos pos, ItemStack itemStack, PlayerEntity player) {
        int bucketFillLevel = itemStack.getNbt().getInt("washwater:bucketFillWorld");
        int bucketRemainingSpace = 8 - bucketFillLevel;
        if (!level.isClient) {
            BlockHitResult blockHitResult = getPlayerEntityPOVHitResult(level, player, RaycastContext.FluidHandling.NONE);
            BlockPos blockPos = blockHitResult.getBlockPos();
            Direction direction = blockHitResult.getSide();
            BlockPos blockPos2 = blockPos.offset(direction);
            int oldVolume = NonCachedWater.getLevel(blockPos2, level);
            int newVolume = 0;
            int newBucketFillLevel;
            if (oldVolume > bucketRemainingSpace) {
                newVolume = oldVolume - bucketRemainingSpace;
                newBucketFillLevel = 8;
            }
            else {
                newBucketFillLevel = bucketFillLevel + oldVolume;
            }
            NonCachedWater.setLevel(newVolume, blockPos2, level);
            NbtCompound tag = new NbtCompound();
            tag.putInt("washwater:bucketFillWorld", newBucketFillLevel);
            itemStack.setNbt(tag);
        }
        return true;
    }


    protected static BlockHitResult getPlayerEntityPOVHitResult(World level, PlayerEntity player, RaycastContext.FluidHandling fluid) {
        float f = player.getPitch();
        float g = player.getYaw();
        Vec3d vec3 = player.getEyePos();
        float h = MathHelper.cos(-g * 0.017453292F - 3.1415927F);
        float i = MathHelper.sin(-g * 0.017453292F - 3.1415927F);
        float j = -MathHelper.cos(-f * 0.017453292F);
        float k = MathHelper.sin(-f * 0.017453292F);
        float l = i * j;
        float n = h * j;
        double d = 5.0;
        Vec3d vec32 = vec3.add((double)l * 5.0, (double)k * 5.0, (double)n * 5.0);
        return level.raycast(new RaycastContext(vec3, vec32, RaycastContext.ShapeType.OUTLINE, fluid, player));
    }

}
