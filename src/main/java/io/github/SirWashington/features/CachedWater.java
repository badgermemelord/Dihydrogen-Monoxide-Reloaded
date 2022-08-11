package io.github.SirWashington.features;

import it.unimi.dsi.fastutil.longs.Long2ByteMap;
import it.unimi.dsi.fastutil.longs.Long2ByteOpenHashMap;
import net.minecraft.block.*;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkSection;

import java.util.function.LongToIntFunction;

import static io.github.SirWashington.WaterPhysics.WATER_LEVEL;

public class CachedWater {

    public static boolean useSections = true;
    public static boolean useCache = true;
    private static final Long2ByteMap cache = new Long2ByteOpenHashMap();
    private static final ChunkSection[] cachedSections = new ChunkSection[8];
    public static int borX = 0;
    public static int borZ = 0;
    public static int borY = 0;
    public static int fpY = 0;
    public static int worldMinY = -64;
    public static World world;

    public static int getWaterLevel(BlockPos ipos) {
        LongToIntFunction func = pos -> {
            BlockState blockstate = getBlockState(BlockPos.fromLong(pos));

            if (blockstate == Blocks.AIR.getDefaultState()) return (byte) 0;
            if (blockstate.contains(WATER_LEVEL)) return blockstate.get(WATER_LEVEL);

            FluidState fluidstate = blockstate.getFluidState();
            if (fluidstate == Fluids.EMPTY.getDefaultState()) return (byte) -1;

            int waterlevel;
            if (fluidstate.isStill()) {
                waterlevel = 8;
            } else {
                waterlevel = fluidstate.getLevel();
            }
            return (byte) waterlevel;
        };

        if (useCache) {
            int result = cache.computeIfAbsent(ipos.asLong(), func);

            assert result == func.applyAsInt(ipos.asLong());
            return result;
        } else return func.applyAsInt(ipos.asLong());
    }

    public static boolean isNotFull(int waterLevel) {
        return waterLevel < 8 && waterLevel >= 0;
    }

    public static boolean isNotFull(BlockPos pos) {
        return isNotFull(getWaterLevel(pos));
    }


    public static void setWaterLevel(int level, BlockPos pos) {
        BlockState prev = getBlockState(pos);

        assert  prev.isAir() ||
                prev.contains(WATER_LEVEL) ||
                prev.getBlock() == Blocks.WATER ||
                level < 0;

        if (prev.contains(WATER_LEVEL)) {
            setBlockState(pos, prev.with(WATER_LEVEL, level));
        } else {
            if (level == 0) {
                setBlockState(pos, Blocks.AIR.getDefaultState());
            } else if (level < 0) {
                // System.out.println("Trying to set waterlevel " + level);
            } else if (level <= 8) {
                if (level == 8) {
                    if (!(prev.getBlock() instanceof FluidFillable)) { // Don't fill kelp etc
                        setBlockState(pos, Blocks.WATER.getDefaultState());
                    }
                } else {
                    if (prev.getBlock() != Blocks.WATER) {
                        world.breakBlock(pos, true);
                        setBlockState(pos, Fluids.FLOWING_WATER.getFlowing(level, false).getBlockState());
                    }
                    setBlockState(pos, Fluids.FLOWING_WATER.getFlowing(level, false).getBlockState());
                }
            } else {
                System.out.println("HELP THY SOUL Trying to set waterlevel " + level);
            }
        }

        if (useCache)
            cache.put(pos.asLong(), (byte) level);
    }


    public static void addWater(int level, BlockPos pos) {
        int existingwater = getWaterLevel(pos);
        if (existingwater == -1) throw new IllegalStateException("Tried to add water to a full block");

        int totalwater = existingwater + level;
        if (totalwater > 8) {
            addWater(totalwater - 8, pos.up());
            setWaterLevel(8, pos);
        } else {
            setWaterLevel(totalwater, pos);
        }
    }

    public static BlockState getBlockState(BlockPos pos) {
        if (useSections)
            return getSection(pos).getBlockState(pos.getX() & 15, pos.getY() & 15, pos.getZ() & 15);
        else
            return world.getBlockState(pos);
    }

    /**
     * @deprecated Use setWaterLevel!
     */
    @Deprecated
    public static void setBlockState(BlockPos pos, BlockState state) {
        if (useSections) {
            ChunkSection section = getSection(pos);
            BlockState old = section.getBlockState(pos.getX() & 15, pos.getY() & 15, pos.getZ() & 15);
            if (state == old) return;

            ((ServerWorld) world).getChunkManager().markForUpdate(pos);
            world.updateNeighbors(pos, old.getBlock());
            Fluid fluid = state.getFluidState().getFluid();
            world.createAndScheduleFluidTick(pos, fluid, fluid.getTickRate(world));

            section.setBlockState(pos.getX() & 15, pos.getY() & 15, pos.getZ() & 15, state, false);
        } else {
            world.setBlockState(pos, state, 11);
        }
    }

    private static ChunkSection getSection(BlockPos pos) {
        String sectionName = "";
        int sectionID = 0;
        BlockState internalBS;

        int posX = pos.getX();
        int posZ = pos.getZ();
        int posY = pos.getY();
        //System.out.println("bors XYZ: " + borX + " " + borY + " " + borZ);
        //System.out.println("x: " + posX + " z: " + posZ + " y: " + posY);

        if (posX < borX) {
            if (posZ < borZ) {
                if (posY >= borY) {
                    sectionID = 3;
                } else {
                    sectionID = 7;
                }
            } else {
                if (posY >= borY) {
                    sectionID = 1;
                } else {
                    sectionID = 5;
                }
            }
        } else {
            if (posZ < borZ) {
                if (posY >= borY) {
                    sectionID = 2;
                } else {
                    sectionID = 6;
                }
            } else {
                if (posY >= borY) {
                    sectionID = 0;
                } else {
                    sectionID = 4;
                }
            }
        }

        //Getting relative position of pos
        int relX;
        int relZ;
        int relY;
        // int relZ2;

        if (pos.getX() >= 0) {
            relX = pos.getX() % 16;
        } else {
            relX = 16 + pos.getX() % 16;
            if ((pos.getX() % 16) == 0) {
                relX = 0;
            }

        }
        if (pos.getZ() >= 0) {
            relZ = pos.getZ() % 16;
        } else {
            relZ = 16 + pos.getZ() % 16;
            if ((pos.getZ() % 16) == 0) {
                relZ = 0;
            }
        }
        if (posY >= 0) {
            relY = posY % 16;
        } else {
            posY = posY + 64;
            relY = posY % 16;
        }

        //System.out.println("sectionID: " + sectionID + " sectionName : " + sectionName);
        //System.out.println("rel coords: " + relX + " " + relY + " " + relZ);
        //System.out.println(Arrays.stream(SectionList).toList());
        //System.out.println("BS: " + internalBS.getBlock());

        return cachedSections[sectionID];
    }

    public static void setup(ServerWorld world, BlockPos fluidPos) {
        CachedWater.world = world;

        //System.out.println("SUGOMA NUTZ");
        int gmr = 4; //generalMaxRange, the maximum range that will ever be used in checks
        // int posX = fluidPos.getX();
        int posY = fluidPos.getY();
        // int posZ = fluidPos.getZ();
        int secY;
        int secY2;
        secY = (posY + 64) / 16;
        secY2 = (posY + 63) / 16;
        //System.out.println("sec " + secY + " sec2" + secY2);

        int[] secList = new int[2];
        secList[0] = secY;
        secList[1] = secY2;


        //Clockwise corner calculation (8 corners for a cuboid)

        BlockPos[] cornerList = new BlockPos[8];

        BlockPos c0 = fluidPos;
        BlockPos c2 = fluidPos.add(0, -1, 0);
        BlockPos c11 = fluidPos.add(gmr, 0, gmr);
        cornerList[0] = c11;
        BlockPos c12 = fluidPos.add(-gmr, 0, gmr);
        cornerList[1] = c12;
        BlockPos c13 = fluidPos.add(gmr, 0, -gmr);
        cornerList[2] = c13;
        BlockPos c14 = fluidPos.add(-gmr, 0, -gmr);
        cornerList[3] = c14;

        BlockPos c21 = fluidPos.add(gmr, -1, gmr);
        cornerList[4] = c21;
        //System.out.println("c21 " + c21.getY());
        BlockPos c22 = fluidPos.add(-gmr, -1, gmr);
        cornerList[5] = c22;
        BlockPos c23 = fluidPos.add(gmr, -1, -gmr);
        cornerList[6] = c23;
        BlockPos c24 = fluidPos.add(-gmr, -1, -gmr);
        cornerList[7] = c24;

        //Getting the starting chunks
        ChunkSection s1 = world.getChunk(c0).getSection(secY);

        //Calculating the border coordinates of the s11 chunk

        int relX;
        int relZ;
        int relY;

        if (c11.getX() > 0) {
            relX = c11.getX() % 16;
        } else {
            relX = 16 + c11.getX() % 16;
        }
        if (c11.getZ() > 0) {
            relZ = c11.getZ() % 16;
        } else {
            relZ = 16 + c11.getZ() % 16;
        }
        if (c11.getY() > 0) {
            relY = c11.getY() % 16;
        } else {
            relY = (c11.getY() + 64) % 16;
        }

        int fpX = c11.getX();
        int fpZ = c11.getZ();
        int fpYa = c11.getY();
        // int restX = 15 - relX;
        // int restZ = 15 - relZ;
        // int restY = 15 - relY;

        //borX = 0;
        //borZ = 0;
        //borY = 0;

        borX = fpX - relX;
        borZ = fpZ - relZ;
        borY = fpYa - relY;
        //System.out.println("el bor " + borY + " el fpa + el rely " + fpYa + " " + relY);


        fpY = fluidPos.getY();

        //Adding origin chunk if missing

        int oriID = getOriSectionID(fluidPos);
        // int oriBelowID = oriID - 4;

        for (int a = 0; a < 8; a++) {

            int localSecY = 0;
            int secID = getOriSectionID(cornerList[a]);

            if (a < 4) {
                localSecY = secY;
            }
            if (a > 3) {
                localSecY = secY2;
            }
            cachedSections[secID] = world.getChunk(cornerList[a]).getSection(localSecY);
            //System.out.println("sector " + secID + " " + secY);
        }

        if (cachedSections[oriID] == null) {
            cachedSections[oriID] = s1;
            //System.out.println("aa + " + oriID);
            //System.out.println("aaaa");
        }
/*
        BlockPos cornerPos = new BlockPos(borX, fluidPos.getY(), borZ);
        System.out.println("bpX: " + cornerPos.getX() + " bpZ: " + cornerPos.getZ());

        BlockState bla = sectionGetBlockState(fluidPos);
        int levele = bla.getFluidState().getLevel();
        System.out.println("levele: " + levele);
*/


    }

    public static int getOriSectionID(BlockPos pos) {

        String sectionName = "";
        int sectionID = 0;

        int posX = pos.getX();
        int posZ = pos.getZ();
        int posY = pos.getY();
        //System.out.println("Border XYZ: " + borX + " " + borY + " " + borZ);
        //System.out.println("Origin x: " + posX + " z: " + posZ + " y: " + posY);

        if (posX < borX) {
            if (posZ < borZ) {
                if (posY >= borY) {
                    sectionName = "14";
                    sectionID = 3;
                } else {
                    sectionName = "24";
                    sectionID = 7;
                }
            } else {
                if (posY >= borY) {
                    sectionName = "12";
                    sectionID = 1;
                } else {
                    sectionName = "22";
                    sectionID = 5;
                }
            }
        } else {
            if (posZ < borZ) {
                if (posY >= borY) {
                    sectionName = "13";
                    sectionID = 2;
                } else {
                    sectionName = "23";
                    sectionID = 6;
                }
            } else {
                if (posY >= borY) {
                    sectionName = "11";
                    sectionID = 0;
                } else {
                    sectionName = "21";
                    sectionID = 4;
                }
            }
        }


        //System.out.println(" ORIGIN sectionID: " + sectionID + " sectionName : " + sectionName);

        return sectionID;

    }

    public static void lock() {
        for (ChunkSection chunkSection : cachedSections) {
            if (chunkSection != null) {
                chunkSection.unlock(); // for some reason there is no isLocked method
                chunkSection.lock();
            }
        }
    }

    public static void unlock() {
        for (ChunkSection chunkSection : cachedSections) {
            if (chunkSection != null) {
                chunkSection.unlock();
            }
        }
    }

    public static void tick(ServerWorld serverWorld) {
        // TODO cache per dimension
        cache.clear();
    }
}
