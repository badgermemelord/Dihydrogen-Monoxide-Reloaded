package io.github.SirWashington;

import com.ewoudje.lasagna.chunkstorage.ExtraStorageSectionContainer;
import io.github.SirWashington.features.ConfigVariables;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.world.World;

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
        //return Fluids.WATER.getFlowing(value / volumePerLevel + 1, false);
        //System.out.println("amogus returned water");
        //return Fluids.WATER.getFlowing(8, false);
        return Fluids.WATER.getDefaultState();
    }

    public static short getWaterVolume(World world, int x, int y, int z) {
        WaterSection section = (WaterSection) ((ExtraStorageSectionContainer) world.getChunk(x >> 4, z >> 4)
                .getSectionArray()[world.getSectionIndex(y)]).getSectionStorage(WaterSection.ID);

        if (section == null) return -1;
        return section.getWaterVolume(x & 15, y & 15, z & 15);
    }

    public static float getWaterHeight(short volume) {
        if (volume == -1) return 0;
        return (float) volume / volumePerBlock;
    }
}
