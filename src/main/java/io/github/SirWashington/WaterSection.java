package io.github.SirWashington;

import com.ewoudje.lasagna.chunkstorage.ExtraSectionStorage;
import com.ewoudje.lasagna.chunkstorage.ExtraStorageSectionContainer;
import com.ewoudje.lasagna.networking.LasagnaNetworking;
import com.ewoudje.lasagna.networking.TrackingChunkPacketTarget;
import io.github.SirWashington.features.CachedWater;
import io.github.SirWashington.mixin.BuiltChunkStorageAccessor;
import io.github.SirWashington.mixin.WorldRendererAccessor;
import io.github.SirWashington.networking.DeltaWaterSectionPacket;
import kotlin.Unit;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.BuiltChunkStorage;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.WorldChunk;
import org.jetbrains.annotations.NotNull;


import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WaterSection implements ExtraSectionStorage {
    public static final Identifier ID = WaterPhysics.resource("water");
    public static final List<WaterSection> dirtySections = new ArrayList<>();
    private final short[] water = new short[16*16*16];
    private int[] dirty = null;
    private int dirtyCount = 0;
    private boolean anyDirt = false;
    private final WorldChunk chunk;
    private final int sectionIndex;

    public WaterSection(WorldChunk chunk, int sectionIndex) {
        this.chunk = chunk;
        this.sectionIndex = sectionIndex;

        Arrays.fill(water, Short.MIN_VALUE);

        if (!chunk.getWorld().isClient()) {
            dirty = new int[128]; // 16*16*16 / 32 = 128
            dirtyCount = 16*16*16;
            Arrays.fill(dirty, 0xFFFFFFFF);
        }

        var section = chunk.getSectionArray()[sectionIndex];
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

    private WaterSection(ShortBuffer shorts, @NotNull WorldChunk chunk, int sectionIndex) {
        shorts.get(water);

        this.chunk = chunk;
        this.sectionIndex = sectionIndex;

        if (!chunk.getWorld().isClient()) {
            dirty = new int[128]; // 16*16*16 / 32 = 128
            dirtyCount = 16*16*16;
            Arrays.fill(dirty, 0xFFFFFFFF);
        }
    }

    private byte[] makeBuffer() {
        byte[] buffer = new byte[water.length * 2];
        for (int i = 0; i < water.length; i++) {
            buffer[i * 2] = (byte) (water[i] & 0xFF);
            buffer[i * 2 + 1] = (byte) ((water[i] >> 8) & 0xFF);
        }

        return buffer;
    }

    @NotNull
    @Override
    public NbtCompound writeNBT(@NotNull NbtCompound nbtCompound, @NotNull WorldChunk chunk, int sectionIndex) {
        nbtCompound.putByteArray("water", makeBuffer());
        return nbtCompound;
    }

    @Override
    public PacketByteBuf writePacket(@NotNull PacketByteBuf packetByteBuf, @NotNull WorldChunk chunk, int sectionIndex) {
        packetByteBuf.writeByteArray(makeBuffer());
        return packetByteBuf;
    }

    public static WaterSection read(NbtCompound nbt, @NotNull WorldChunk chunk, int sectionIndex) {
        var bytes = nbt.getByteArray("water");
        return new WaterSection(ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer(), chunk, sectionIndex);
    }

    public static WaterSection readPacket(PacketByteBuf buf, @NotNull WorldChunk chunk, int sectionIndex) {
        return new WaterSection(ByteBuffer.wrap(buf.readByteArray()).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer(), chunk, sectionIndex);
    }

    public short getWaterVolume(BlockPos pos) {
        int x = pos.getX() & 15;
        int y = pos.getY() & 15;
        int z = pos.getZ() & 15;

        return getWaterVolume(x, y, z);
    }

    public short getWaterVolume(int x, int y, int z) {
        return water[(x*16*16) + (y * 16) + z];
    }

    public void setWaterVolume(BlockPos pos, short value) {
        setWaterVolume(pos.getX() & 15, pos.getY() & 15, pos.getZ() & 15, value);
    }

    // relative coordinates
    public void setWaterVolume(int x, int y, int z, short value) {
        water[(x*16*16) + (y * 16) + z] = value;

        if (dirty != null) {
            dirty[x * 8 + y / 2] |= 1 << (z + (y % 4));
            dirtyCount++;
            if (!anyDirt) {
                anyDirt = true;
                dirtySections.add(this);
            }
        }
    }


    public void setWaterVolumeByState(BlockPos pos, BlockState state) {
        setWaterVolumeByState(pos.getX() & 15, pos.getY() & 15, pos.getZ() & 15, state);
    }

    // relative coordinates
    public void setWaterVolumeByState(int x, int y, int z, BlockState state) {
        setWaterVolume(x, y, z, WaterVolume.getWaterVolumeOfState(state));
    }

    private DeltaWaterSectionPacket makeDelta() {
        // Happens if 2 times in the list or incorrectly added to the list?
        if (!anyDirt) throw new IllegalStateException("No dirty section expected to make delta packet");
        if (dirtyCount == 0) throw new IllegalStateException("No dirtyCount section expected to make delta packet");

        DeltaWaterSectionPacket delta = new DeltaWaterSectionPacket(
                this.chunk.getPos().x,
                this.chunk.getPos().z,
                this.sectionIndex,
                this.dirty, this.water, this.dirtyCount
        );

        anyDirt = false;
        dirtyCount = 0;
        Arrays.fill(dirty, 0);

        return delta;
    }

    public static void sendUpdates() {
        for (var section : dirtySections) {
            LasagnaNetworking.send(
                    new TrackingChunkPacketTarget(section.chunk),
                    DeltaWaterSectionPacket.class,
                    section.makeDelta()
            );
        }

        dirtySections.clear();
    }

    private void applyDelta(DeltaWaterSectionPacket packet) {
        if (packet.positions == null) {
            ByteBuffer.wrap(packet.water).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(this.water);
        } else {
            for (int i = 0; i < (packet.positions.length / 2); i++) {
                if (packet.positions[i * 2] == -1 && packet.positions[i * 2 + 1] == -1) break;
                int position = (packet.positions[i * 2] & 0xFF) | ((packet.positions[(i * 2) + 1] & 0xFF) << 8);
                this.water[position] = (short) ((packet.water[i * 2] & 0xFF) | ((packet.water[(i * 2) + 1] & 0xFF) << 8));
            }
        }
    }

    public static void register() {
        DeltaWaterSectionPacket.register();
        ExtraSectionStorage.Companion.register(WaterSection.ID, WaterSection::read, true, WaterSection.class, WaterSection::readPacket);

        LasagnaNetworking.packetClient(DeltaWaterSectionPacket.class, false, (packet, context) -> {
            var chunk = context.world.getChunk(
                    packet.chunkX,
                    packet.chunkZ
            );
            var section = chunk.getSectionArray()[packet.sectionY];

            WaterSection storage = (WaterSection) ((ExtraStorageSectionContainer) section).getSectionStorage(WaterSection.ID);

            if (storage == null) {
                storage = new WaterSection(chunk, packet.sectionY);
                ((ExtraStorageSectionContainer) section).setSectionStorage(WaterSection.ID, storage);
            }

            storage.applyDelta(packet);

            ((WorldRendererAccessor) context.worldRenderer).invokeChunkRender(
                    packet.chunkX,
                    context.world.sectionIndexToCoord(packet.sectionY),
                    packet.chunkZ,
                    true
            );
            return Unit.INSTANCE;
        });
    }

    @Override
    public boolean isDirty() {
        return true;
    }

    @Override
    public void saved() {

    }
}
