package io.github.SirWashington.features;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.ArrayList;

import static io.github.SirWashington.FlowWater.world;

public class KelpFeature {

/*
    public static void Execute(BlockPos kelpPos, BlockState kpBS) {

        System.out.println("called");
        int count = 0;
        boolean nonFullFluidBlock = false;
        int totalWaterLevel = 0;
        int centerWaterLevel = 8;

        ArrayList<BlockPos> blocks = new ArrayList<>(4);
        for (Direction dir : Direction.Type.HORIZONTAL) {
            blocks.add(kelpPos.offset(dir));

            for (BlockPos block : blocks) {
                BlockState internalBS = CachedWater.getBlockState(block);
                if (internalBS.getBlock() == Blocks.WATER || internalBS.getBlock() == Blocks.AIR) {
                    count += 1;
                    int level = internalBS.getFluidState().getLevel();
                    totalWaterLevel += level;
                }
                int level = CachedWater.getWaterLevel(block);
                //System.out.println(level);
                //System.out.println("tot " + totalWaterLevel);
            }
            if (totalWaterLevel <= (count - 1) * 8) {
                nonFullFluidBlock = true;
            }
            if (nonFullFluidBlock) {
                world.breakBlock(kelpPos, true);
            }
        }
    }
*/
}
