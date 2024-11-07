package io.github.SirWashington.item;

import io.github.SirWashington.features.NonCachedWater;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;


public class BucketMechanics {


    public static boolean precisionBucketPlace(Level level, BlockPos pos, ItemStack itemStack, Player player) {

        int bucketFillLevel = itemStack.getTag().getInt("washwater:bucketFillWorld");
        int newBucketFillLevel = 0;

        if (bucketFillLevel > 0 && !level.isClientSide) {
            BlockHitResult blockHitResult = getPlayerEntityPOVHitResult(level, player, ClipContext.Fluid.NONE);
            BlockPos blockPos = blockHitResult.getBlockPos();
            Direction direction = blockHitResult.getDirection();
            BlockPos blockPos2 = blockPos.relative(direction);
            NonCachedWater.addWater(bucketFillLevel, blockPos2, level);
            CompoundTag tag = new CompoundTag();
            tag.putInt("washwater:bucketFillWorld", newBucketFillLevel);
            itemStack.setTag(tag);
        }
        return true;
    }
    public static boolean precisionBucketPickup(Level level, BlockPos pos, ItemStack itemStack, Player player) {
        int bucketFillLevel = itemStack.getTag().getInt("washwater:bucketFillWorld");
        int bucketRemainingSpace = 8 - bucketFillLevel;
        if (!level.isClientSide) {
            BlockHitResult blockHitResult = getPlayerEntityPOVHitResult(level, player, ClipContext.Fluid.NONE);
            BlockPos blockPos = blockHitResult.getBlockPos();
            Direction direction = blockHitResult.getDirection();
            BlockPos blockPos2 = blockPos.relative(direction);
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
            CompoundTag tag = new CompoundTag();
            tag.putInt("washwater:bucketFillWorld", newBucketFillLevel);
            itemStack.setTag(tag);
        }
        return true;
    }


    protected static BlockHitResult getPlayerEntityPOVHitResult(Level level, Player player, ClipContext.Fluid fluid) {
        float f = player.getXRot();
        float g = player.getYRot();
        Vec3 vec3 = player.getEyePosition();
        float h = Mth.cos(-g * 0.017453292F - 3.1415927F);
        float i = Mth.sin(-g * 0.017453292F - 3.1415927F);
        float j = -Mth.cos(-f * 0.017453292F);
        float k = Mth.sin(-f * 0.017453292F);
        float l = i * j;
        float n = h * j;
        double d = 5.0;
        Vec3 vec32 = vec3.add((double)l * 5.0, (double)k * 5.0, (double)n * 5.0);
        return level.clip(new ClipContext(vec3, vec32, ClipContext.Block.OUTLINE, fluid, player));
    }

}
