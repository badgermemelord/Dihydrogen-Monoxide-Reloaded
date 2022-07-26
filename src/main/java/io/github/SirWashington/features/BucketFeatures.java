package io.github.SirWashington.features;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;


public class BucketFeatures {

    public static boolean addWater(int level, BlockPos pos, World world) {
        int existingwater = world.getFluidState(pos).getLevel();
        BlockState blockAbove = world.getBlockState(pos.up());
        boolean isAboveClear = blockAbove == Blocks.AIR.getDefaultState();
        if (existingwater == -1) throw new IllegalStateException("Tried to add water to a full block");

        int totalwater = existingwater + level;
        if (totalwater > 8 && isAboveClear) {
            addWater(totalwater - 8, pos.up(), world);
            world.setBlockState(pos, Blocks.WATER.getDefaultState(), 11);
            return true;
        }
        else if(totalwater > 8 && !isAboveClear) {
            return false;
        }
        else {
            world.setBlockState(pos, Fluids.FLOWING_WATER.getFlowing(totalwater, false).getBlockState(), 11);
            return true;
        }
    }
}
