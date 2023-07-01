package io.github.SirWashington;

import io.github.SirWashington.features.ConfigVariables;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;

public class WaterVolume {
    public static short volumePerBlock = ConfigVariables.volumePerBlock;
    public static short volumePerLevel = (short) (volumePerBlock / 8);
    public static short cutOffValue = (short) (volumePerLevel * 7);

    public static short getWaterVolumeOfState(BlockState state) {
        if (state.isAir())
            return 0;

        FluidState fluidstate = state.getFluidState();
        if (fluidstate.isEmpty())
            return -1;

        return (short) (fluidstate.getLevel() * volumePerLevel);
    }
}
