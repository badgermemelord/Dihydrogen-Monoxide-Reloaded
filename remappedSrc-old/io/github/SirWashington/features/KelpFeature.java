package io.github.SirWashington.features;

import java.util.ArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import static io.github.SirWashington.FlowWater.world;

public class KelpFeature {

    public static void Execute(BlockPos kelpPos, BlockState kpBS) {

        System.out.println("called");
        int count = 0;
        boolean nonFullFluidBlock = false;
        int totalWaterLevel = 0;
        int centerWaterLevel = 8;

        ArrayList<BlockPos> blocks = new ArrayList<>(4);
        for (Direction dir : Direction.Plane.HORIZONTAL) {
            blocks.add(kelpPos.relative(dir));

            for (BlockPos block : blocks) {
                BlockState internalBS = CachedWater.getBlockState(block);
                if (internalBS.getBlock() == Blocks.WATER || internalBS.getBlock() == Blocks.AIR) {
                    count += 1;
                    int level = internalBS.getFluidState().getAmount();
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
                world.destroyBlock(kelpPos, true);
            }
        }
    }
}
