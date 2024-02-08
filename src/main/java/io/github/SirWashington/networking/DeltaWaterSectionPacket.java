package io.github.SirWashington.networking;

import com.ewoudje.lasagna.networking.SerializationBuilder;
import io.github.SirWashington.WaterPhysics;
import kotlin.Unit;
import net.minecraft.util.Identifier;


public class DeltaWaterSectionPacket {
    public final byte[] positions;
    public final byte[] water;
    public final int chunkX;
    public final int chunkZ;
    public final int sectionY;

    // Note dirtyCount can be bigger then the actual amount of dirty blocks
    public DeltaWaterSectionPacket(
            int chunkX, int chunkZ, int sectionY,
            int[] dirty, short[] water, int dirtyCount
    ) {
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
        this.sectionY = sectionY;

        if (dirtyCount > 32) {
            this.positions = null;
            this.water = new byte[water.length * 2];
            for (int i = 0; i < water.length; i++) {
                this.water[i * 2] = (byte) (water[i] & 0xFF);
                this.water[i * 2 + 1] = (byte) ((water[i] >> 8) & 0xFF);
            }
        } else {
            this.positions = new byte[(dirtyCount + 1) * 2];
            this.water = new byte[dirtyCount * 2];

            int index = 0;
            for (int i = 0; i < dirty.length; i++) {
                if (dirty[i] != 0) {
                    for (int b = 0; b < 32; b++) {
                        if ((dirty[i] & (1 << b)) != 0) {
                            int position = i * 32 + b;
                            this.positions[index] = (byte) (position & 0xFF);
                            this.positions[index + 1] = (byte) ((position >> 8) & 0xFF);

                            this.water[index] = (byte) (water[position] & 0xFF);
                            this.water[index + 1] = (byte) ((water[position] >> 8) & 0xFF);

                            index += 2;
                        }
                    }
                }
            }

            this.positions[index++] = -1;
            this.positions[index] = -1;
        }
    }

    private DeltaWaterSectionPacket(
            int chunkX, int chunkZ, int sectionY,
            byte[] positions, byte[] water
    ) {
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
        this.sectionY = sectionY;

        this.positions = positions;
        this.water = water;
    }

    public static void register() {
        new SerializationBuilder<>(
                new Identifier(WaterPhysics.ID, "delta_packet"),
                (packet, buf) -> {
                    buf.writeInt(packet.chunkX);
                    buf.writeInt(packet.chunkZ);
                    buf.writeInt(packet.sectionY);

                    if (packet.positions == null) {
                        buf.writeBoolean(false);
                        buf.writeByteArray(packet.water);
                    } else {
                        buf.writeBoolean(true);
                        buf.writeByteArray(packet.positions);
                        buf.writeByteArray(packet.water);
                    }

                    return Unit.INSTANCE;
                },
                DeltaWaterSectionPacket.class
        ).decode((buf) -> {
            int chunkX = buf.readInt();
            int chunkZ = buf.readInt();
            int sectionY = buf.readInt();

            boolean hasPositions = buf.readBoolean();
            if (hasPositions) {
                return new DeltaWaterSectionPacket(chunkX, chunkZ, sectionY, buf.readByteArray(), buf.readByteArray());
            } else {
                return new DeltaWaterSectionPacket(chunkX, chunkZ, sectionY, null, buf.readByteArray());
            }
        });
    }


}
