package io.github.SirWashington.scheduling;

import io.github.SirWashington.FlowWater;
import io.github.SirWashington.features.CachedWater;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;

import static io.github.SirWashington.FlowWater.world;

public class WaterTickScheduler {

    public static ArrayList<BlockPos> CurrentToTick = new ArrayList<>();
    public static ArrayList<BlockPos> NextToTick = new ArrayList<>();


    public static void scheduleFluidBlock(BlockPos FluidPosToSchedule) {
        NextToTick.add(FluidPosToSchedule);
    }

    public static void tickFluid(World world) {
        System.out.println("curr " +  CurrentToTick);
        System.out.println("next " +  NextToTick);

        for(BlockPos iterator : NextToTick) {
            CurrentToTick.add(iterator);
        }
        //CurrentToTick = NextToTick;
        NextToTick.clear();
        //System.out.println("curr2 " +  CurrentToTick);
        for(BlockPos BP : CurrentToTick) {
            //FluidState FS = CachedWater.getBlockState(BP).getFluidState();
            System.out.println("bp: " + BP);
            //FlowWater.flowwater(world, BP, FS);
            FlowWater.testTick(world, BP);
        }
        CurrentToTick.clear();
    }

}
