package io.github.SirWashington;

import io.github.SirWashington.features.CachedWater;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;
import org.mashed.lasagna.chunkstorage.ExtraSectionStorage;

import java.util.Arrays;

public class WaterSection implements ExtraSectionStorage {
    public static final Identifier ID = WaterPhysics.resource("water");
    private final short[] water = new short[16*16*16];

    public WaterSection() {
        Arrays.fill(water, Short.MIN_VALUE);
    }


    @NotNull
    @Override
    public NbtCompound writeNBT(@NotNull NbtCompound nbtCompound) {
        return nbtCompound;
    }

    public static WaterSection read(NbtCompound nbt) {
        return new WaterSection();
    }

    public short getWaterVolume(BlockPos pos) {
        int x = pos.getX() & 15;
        int y = pos.getY() & 15;
        int z = pos.getZ() & 15;

        return water[(x*16*16) + (y * 16) + z];
    }

    public void setWaterVolume(BlockPos pos, short value) {
        setWaterVolume(pos.getX() & 15, pos.getY() & 15, pos.getZ() & 15, value);
    }

    // relative coordinates
    public void setWaterVolume(int x, int y, int z, short value) {
        water[(x*16*16) + (y * 16) + z] = value;
    }


    public void setWaterVolumeByState(BlockPos pos, BlockState state) {
        setWaterVolumeByState(pos.getX() & 15, pos.getY() & 15, pos.getZ() & 15, state);
    }

    // relative coordinates
    public void setWaterVolumeByState(int x, int y, int z, BlockState state) {
        setWaterVolume(x, y, z, WaterVolume.getWaterVolumeOfState(state));
    }
}
