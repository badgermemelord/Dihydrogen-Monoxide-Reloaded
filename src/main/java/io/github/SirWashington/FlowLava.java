package io.github.SirWashington;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidFillable;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.LavaFluid;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldAccess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class FlowLava {
    private FlowLava() { }

    public static void flowlava(WorldAccess world, BlockPos fluidPos, FluidState state) {
        if (world.getBlockState(fluidPos).getBlock() instanceof FluidFillable) {
            return;
        }
        if ((world.getBlockState(fluidPos.down()).canBucketPlace(Fluids.LAVA)) && (getLavaLevel(fluidPos.down(), world) != 8)) {
            int centerlevel = getLavaLevel(fluidPos, world);
            world.setBlockState(fluidPos, Blocks.AIR.getDefaultState(), 11);
            addLava(centerlevel, fluidPos.down(), world);
        } else {
            ArrayList<BlockPos> blocks = new ArrayList<>(4);
            for (Direction dir : Direction.Type.HORIZONTAL) {
                blocks.add(fluidPos.offset(dir));
            }
            blocks.removeIf(pos -> !world.getBlockState(pos).canBucketPlace(Fluids.LAVA));
            Collections.shuffle(blocks);
            equalizeLava(blocks, fluidPos, world);
        }
    }

    public static int getLavaLevel(BlockPos pos, WorldAccess world) {
        BlockState blockstate = world.getBlockState(pos);
        FluidState fluidstate = blockstate.getFluidState();
        int lavalevel = 0;
        if (fluidstate.getFluid() instanceof LavaFluid.Still){
            lavalevel = 8;
        } else if (fluidstate.getFluid() instanceof LavaFluid.Flowing) {
            lavalevel = fluidstate.getLevel();
        }
        return lavalevel;
    }

    public static void setLavaLevel(int level, BlockPos pos, WorldAccess world) {
        if (level == 8) {
            if (!(world.getBlockState(pos).getBlock() instanceof FluidFillable)) { // Don't fill kelp etc
                world.setBlockState(pos, Fluids.LAVA.getDefaultState().getBlockState(), 11);
            }
        } else if (level == 0) {
            world.setBlockState(pos, Blocks.AIR.getDefaultState(), 11);
        } else if (level < 8) {
            world.setBlockState(pos, Fluids.FLOWING_LAVA.getFlowing(level, false).getBlockState(), 11);
        } else {
            System.out.println("Can't set lava >8 something went very wrong!");
        }
    }

    public static void addLava(int level, BlockPos pos, WorldAccess world) {
        int existinglava = getLavaLevel(pos, world);
        int totallava = existinglava + level;
        if (totallava > 8) {
            setLavaLevel(totallava - 8, pos.up(), world);
            setLavaLevel(8, pos, world);
        } else {
            setLavaLevel(totallava, pos, world);
        }
    }

    public static void equalizeLava(ArrayList<BlockPos> blocks, BlockPos center, WorldAccess world) {
        int[] lavalevels = new int[4];
        Arrays.fill(lavalevels, -1);
        int centerlavalevel = getLavaLevel(center, world);
        for (BlockPos block : blocks) {
            lavalevels[blocks.indexOf(block)] = getLavaLevel(block, world);
        }

        int lavalevelsnum = lavalevels.length;
        int lavadidnothings = 0;
        int lavalevel;
        while (lavadidnothings < lavalevelsnum) {
            lavadidnothings = 0;
            for (int i = 0; i < 4; i++) {
                lavalevel = lavalevels[i];
                if (lavalevel != -1) {
                    if ((centerlavalevel >= (lavalevel + 2))) {
                        lavalevel += 1;
                        lavalevels[i] = lavalevel;
                        centerlavalevel -= 1;
                    } else {
                        lavadidnothings += 1;
                    }
                } else {
                    lavadidnothings += 1;
                }
            }
        }
        for (BlockPos block : blocks) {
            int newlavalevel = lavalevels[blocks.indexOf(block)];
            setLavaLevel(newlavalevel, block, world);
        }
        setLavaLevel(centerlavalevel, center, world);
    }
}
