package io.github.SirWashington;

import io.github.SirWashington.features.ConfigVariables;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.WaterFluid;

public class WaterVolume {
    public static short volumePerBlock = ConfigVariables.volumePerBlock;
    public static short volumePerLevel = (short) ((volumePerBlock / 8) + 1);
    public static short cutOffValue = (short) (volumePerLevel * 7);

    public static short getWaterVolumeOfState(BlockState state) {
        if (state.isAir())
            return 0;

        FluidState fluidstate = state.getFluidState();
        if (fluidstate.isEmpty())
            return -1;

        return (short) (fluidstate.getLevel() * volumePerLevel);
    }

    public static FluidState getWaterState(short value) {
        //TODO if -1 we want to check state?
        if (value == 0 || value == Short.MIN_VALUE || value == -1) return Fluids.EMPTY.getDefaultState();
        return Fluids.WATER.getFlowing(value / volumePerLevel + 1, false);
    }
}
