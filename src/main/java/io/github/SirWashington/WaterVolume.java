package io.github.SirWashington;

import io.github.SirWashington.features.ConfigVariables;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;

import static io.github.SirWashington.properties.WaterFluidProperties.VOLUME;

public class WaterVolume {
    public static short volumePerBlock = ConfigVariables.volumePerBlock;
    public static short divisionValue = (short) (volumePerBlock / 8);
    public static short cutOffValue = (short) (divisionValue * 7);

    public static short getWaterVolumeOfState(BlockState state) {
        if (state.isAir())
            return 0;
/*        if (state.getBlock() != Blocks.WATER)
            return 0;*/

        if (state.contains(VOLUME))
            return (short) (int) state.get(VOLUME);

        FluidState fluidstate = state.getFluidState();
        if (fluidstate == Fluids.EMPTY.getDefaultState())
            return -1;

        short waterVolume;
        if (fluidstate.isStill()) {
            waterVolume = volumePerBlock;
        } else {
            waterVolume = (short) (int) fluidstate.get(VOLUME);
            //System.out.println("e");
        }

        return waterVolume;
    }
}
