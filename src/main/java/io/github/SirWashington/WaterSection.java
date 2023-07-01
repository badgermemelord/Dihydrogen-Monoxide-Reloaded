package io.github.SirWashington;

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
        int x = pos.getX() & 15;
        int y = pos.getY() & 15;
        int z = pos.getZ() & 15;

        water[(x*16*16) + (y * 16) + z] = value;
    }
}
