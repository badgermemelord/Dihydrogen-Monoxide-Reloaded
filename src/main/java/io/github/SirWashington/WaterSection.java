package io.github.SirWashington;

import io.github.SirWashington.features.CachedWater;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.ChunkSection;
import org.jetbrains.annotations.NotNull;
import org.mashed.lasagna.chunkstorage.ExtraSectionStorage;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.util.Arrays;

public class WaterSection implements ExtraSectionStorage {
    public static final Identifier ID = WaterPhysics.resource("water");
    private final short[] water = new short[16*16*16];

    public WaterSection(ChunkSection section) {
        Arrays.fill(water, Short.MIN_VALUE);
        if (section.hasAny((bs) -> !bs.getFluidState().isEmpty())) {
            for (int x = 0; x < 16; x++) {
                for (int y = 0; y < 16; y++) {
                    for (int z = 0; z < 16; z++) {
                        if (!section.getFluidState(x, y, z).isEmpty()) {
                            setWaterVolume(x, y, z, WaterVolume.volumePerBlock);
                        } else {
                            setWaterVolume(x, y, z, (short) (section.getBlockState(x, y, z).isAir() ? 0 : -1));
                        }
                    }
                }
            }
        }
    }

    private WaterSection(ShortBuffer shorts, ChunkSection section) {
        shorts.get(water);
    }

    @NotNull
    @Override
    public NbtCompound writeNBT(@NotNull NbtCompound nbtCompound, @NotNull ChunkSection section) {
        byte[] buffer = new byte[water.length * 2];
        for (int i = 0; i < water.length; i++) {
            buffer[i * 2] = (byte) (water[i] & 0xFF);
            buffer[i * 2 + 1] = (byte) ((water[i] >> 8) & 0xFF);
        }

        nbtCompound.putByteArray("water", buffer);
        return nbtCompound;
    }

    @Override
    public PacketByteBuf writePacket(@NotNull PacketByteBuf packetByteBuf, @NotNull ChunkSection chunkSection) {
        return packetByteBuf;
    }

    public static WaterSection read(NbtCompound nbt, ChunkSection section) {
        var bytes = nbt.getByteArray("water");
        return new WaterSection(ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer(), section);
    }

    public static WaterSection readPacket(PacketByteBuf nbt, ChunkSection section) {
        return new WaterSection(section); // TODO
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
