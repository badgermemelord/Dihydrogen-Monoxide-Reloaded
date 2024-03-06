package io.github.SirWashington.renderer;

import io.github.SirWashington.WaterVolume;
import me.jellysquid.mods.sodium.client.model.light.LightMode;
import me.jellysquid.mods.sodium.client.model.light.LightPipeline;
import me.jellysquid.mods.sodium.client.model.light.LightPipelineProvider;
import me.jellysquid.mods.sodium.client.model.light.data.QuadLightData;
import me.jellysquid.mods.sodium.client.model.quad.ModelQuad;
import me.jellysquid.mods.sodium.client.model.quad.ModelQuadView;
import me.jellysquid.mods.sodium.client.model.quad.ModelQuadViewMutable;
import me.jellysquid.mods.sodium.client.model.quad.blender.ColorBlender;
import me.jellysquid.mods.sodium.client.model.quad.blender.ColorSampler;
import me.jellysquid.mods.sodium.client.model.quad.blender.FlatColorBlender;
import me.jellysquid.mods.sodium.client.model.quad.properties.ModelQuadFacing;
import me.jellysquid.mods.sodium.client.model.quad.properties.ModelQuadWinding;
import me.jellysquid.mods.sodium.client.render.chunk.compile.buffers.ChunkModelBuilder;
import me.jellysquid.mods.sodium.client.render.chunk.format.ModelVertexSink;
import me.jellysquid.mods.sodium.client.render.pipeline.FluidRenderer;
import me.jellysquid.mods.sodium.client.util.Norm3b;
import me.jellysquid.mods.sodium.client.util.color.ColorABGR;
import me.jellysquid.mods.sodium.common.util.DirectionUtil;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler;
import net.fabricmc.fabric.impl.client.rendering.fluid.FluidRenderHandlerRegistryImpl;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.texture.Sprite;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockRenderView;
import org.apache.commons.lang3.mutable.MutableFloat;
import org.apache.commons.lang3.mutable.MutableInt;
import org.jetbrains.annotations.Nullable;

public class WaterRenderer {
    private static final float EPSILON = 0.001F;
    private static final FabricFluidColorizerAdapter fabricColorProviderAdapter = new FabricFluidColorizerAdapter();
    private static final BlockPos.Mutable scratchPos = new BlockPos.Mutable();
    private static final MutableFloat scratchHeight = new MutableFloat(0.0F);
    private static final MutableInt scratchSamples = new MutableInt();
    private static final Sprite waterOverlaySprite = ModelLoader.WATER_OVERLAY.getSprite();
    private static final ModelQuadViewMutable quad = new ModelQuad();
    private static final QuadLightData quadLightData = new QuadLightData();
    private static final int[] quadColors = new int[4];

    static {
        int normal = Norm3b.pack(0.0F, 1.0F, 0.0F);
        for(int i = 0; i < 4; ++i) {
            quad.setNormal(i, normal);
        }
    }

    public static boolean renderWater(BlockRenderView world,
                                      short volume, Fluid fluid,
                                      BlockPos pos, BlockPos rel,
                                      ChunkModelBuilder buffers,
                                      LightPipelineProvider lighters,
                                      ColorBlender colorBlender
    ) {
        int posX = pos.getX();
        int posY = pos.getY();
        int posZ = pos.getZ();
        boolean sfUp = false;//this.isFluidOccluded(world, posX, posY, posZ, Direction.UP, fluid);
        boolean sfDown = false;//this.isFluidOccluded(world, posX, posY, posZ, Direction.DOWN, fluid) || !this.isSideExposed(world, posX, posY, posZ, Direction.DOWN, 0.8888889F);
        boolean sfNorth = false;//this.isFluidOccluded(world, posX, posY, posZ, Direction.NORTH, fluid);
        boolean sfSouth = false;//this.isFluidOccluded(world, posX, posY, posZ, Direction.SOUTH, fluid);
        boolean sfWest = false;//this.isFluidOccluded(world, posX, posY, posZ, Direction.WEST, fluid);
        boolean sfEast = false;//this.isFluidOccluded(world, posX, posY, posZ, Direction.EAST, fluid);
        if (sfUp && sfDown && sfEast && sfWest && sfNorth && sfSouth) return false;

        boolean isWater = fluid.isIn(FluidTags.WATER);
        FluidRenderHandler handler = FluidRenderHandlerRegistryImpl.INSTANCE.get(fluid);
        ColorSampler<FluidState> colorizer = createColorProviderAdapter(handler);
        Sprite[] sprites = handler.getFluidSprites(world, pos, fluid.getDefaultState());
        boolean rendered = false;
        System.out.println("renderer volume: " + volume + " pos: " + pos);
        //float fluidHeight = ((float) volume) / WaterVolume.volumePerBlock;
        float fluidHeight = 1.0F;
        float h1;
        float h2;
        float h3;
        float h4;
        float yOffset;
        if (fluidHeight >= 1.0F) {
            h1 = 1.0F;
            h2 = 1.0F;
            h3 = 1.0F;
            h4 = 1.0F;
        } else {
            //yOffset = fluidHeight;//this.fluidHeight(world, fluid, pos.north());
            //float south1 = this.fluidHeight(world, fluid, pos.south());
            //float east1 = this.fluidHeight(world, fluid, pos.east());
            //float west1 = this.fluidHeight(world, fluid, pos.west());
            h1 = fluidHeight;//this.fluidCornerHeight(world, fluid, fluidHeight, yOffset, west1, pos.offset(Direction.NORTH).offset(Direction.WEST));
            h2 = fluidHeight;//this.fluidCornerHeight(world, fluid, fluidHeight, south1, west1, pos.offset(Direction.SOUTH).offset(Direction.WEST));
            h3 = fluidHeight;//this.fluidCornerHeight(world, fluid, fluidHeight, south1, east1, pos.offset(Direction.SOUTH).offset(Direction.EAST));
            h4 = fluidHeight;//this.fluidCornerHeight(world, fluid, fluidHeight, yOffset, east1, pos.offset(Direction.NORTH).offset(Direction.EAST));
        }

        yOffset = sfDown ? 0.0F : 0.001F;
        ModelQuadViewMutable quad = WaterRenderer.quad;
        LightMode lightMode = isWater && MinecraftClient.isAmbientOcclusionEnabled() ? LightMode.SMOOTH : LightMode.FLAT;
        LightPipeline lighter = lighters.getLighter(lightMode);
        quad.setFlags(0);
        float c1;
        float c2;
        float x1;
        float z1;
        float x2;
        float z2;
        float u1;
        float u2;

        if (!sfUp && /*this.isSideExposed(world, posX, posY, posZ, Direction.UP, Math.min(Math.min(h1, h2), Math.min(h3, h4)))*/ true) {
            Sprite sprite = sprites[0];
            float minU = sprite.getMinU();
            float maxU = sprite.getMaxU();
            u1 = sprite.getMinV();
            c1 = sprite.getMaxV();
            quad.setSprite(sprite);
            setVertex(quad, 0, 0.0F, h1, 0.0F, minU, u1);
            setVertex(quad, 1, 0.0F, h2, 1.0F, minU, c1);
            setVertex(quad, 2, 1.0F, h3, 1.0F, maxU, c1);
            setVertex(quad, 3, 1.0F, h4, 0.0F, maxU, u1);
            calculateQuadColors(quad, world, pos, lighter, Direction.UP, 1.0F, colorizer, fluid.getDefaultState(), colorBlender);
            int vertexStart = writeVertices(buffers, rel, quad);
            buffers.getIndexBufferBuilder(ModelQuadFacing.UP).add(vertexStart, ModelQuadWinding.CLOCKWISE);
            rendered = true;
        }

        if (!sfDown) {
            Sprite sprite = sprites[0];
            float minU = sprite.getMinU();
            float maxU = sprite.getMaxU();
            u1 = sprite.getMinV();
            c1 = sprite.getMaxV();
            quad.setSprite(sprite);
            setVertex(quad, 0, 0.0F, yOffset, 1.0F, minU, c1);
            setVertex(quad, 1, 0.0F, yOffset, 0.0F, minU, u1);
            setVertex(quad, 2, 1.0F, yOffset, 0.0F, maxU, u1);
            setVertex(quad, 3, 1.0F, yOffset, 1.0F, maxU, c1);
            calculateQuadColors(quad, world, pos, lighter, Direction.DOWN, 1.0F, colorizer, fluid.getDefaultState(), colorBlender);
            int vertexStart = writeVertices(buffers, rel, quad);
            buffers.getIndexBufferBuilder(ModelQuadFacing.DOWN).add(vertexStart, ModelQuadWinding.CLOCKWISE);
            rendered = true;
        }

        quad.setFlags(1);
        Direction[] var56 = DirectionUtil.HORIZONTAL_DIRECTIONS;
        int var58 = var56.length;

        for (int var60 = 0; var60 < var58; ++var60) {
            Direction dir = var56[var60];
            switch (dir) {
                case NORTH:
                    if (sfNorth) {
                        continue;
                    }

                    c1 = h1;
                    c2 = h4;
                    x1 = 0.0F;
                    x2 = 1.0F;
                    z1 = 0.001F;
                    z2 = z1;
                    break;
                case SOUTH:
                    if (sfSouth) {
                        continue;
                    }

                    c1 = h3;
                    c2 = h2;
                    x1 = 1.0F;
                    x2 = 0.0F;
                    z1 = 0.999F;
                    z2 = z1;
                    break;
                case WEST:
                    if (sfWest) {
                        continue;
                    }

                    c1 = h2;
                    c2 = h1;
                    x1 = 0.001F;
                    x2 = x1;
                    z1 = 1.0F;
                    z2 = 0.0F;
                    break;
                case EAST:
                    if (!sfEast) {
                        c1 = h4;
                        c2 = h3;
                        x1 = 0.999F;
                        x2 = x1;
                        z1 = 0.0F;
                        z2 = 1.0F;
                        break;
                    }
                default:
                    continue;
            }

            if (true /*this.isSideExposed(world, posX, posY, posZ, dir, Math.max(c1, c2))*/) {
                int adjX = posX + dir.getOffsetX();
                int adjY = posY + dir.getOffsetY();
                int adjZ = posZ + dir.getOffsetZ();
                Sprite sprite = sprites[1];
                if (isWater) {
                    BlockPos adjPos = scratchPos.set(adjX, adjY, adjZ);
                    BlockState adjBlock = world.getBlockState(adjPos);
                    if (!adjBlock.isOpaque() && !adjBlock.isAir()) {
                        sprite = waterOverlaySprite;
                    }
                }

                u1 = sprite.getFrameU(0.0);
                u2 = sprite.getFrameU(8.0);
                float v1 = sprite.getFrameV((double) ((1.0F - c1) * 16.0F * 0.5F));
                float v2 = sprite.getFrameV((double) ((1.0F - c2) * 16.0F * 0.5F));
                float v3 = sprite.getFrameV(8.0);
                quad.setSprite(sprite);
                setVertex(quad, 0, x2, c2, z2, u2, v2);
                setVertex(quad, 1, x2, yOffset, z2, u2, v3);
                setVertex(quad, 2, x1, yOffset, z1, u1, v3);
                setVertex(quad, 3, x1, c1, z1, u1, v1);
                float br = dir.getAxis() == Direction.Axis.Z ? 0.8F : 0.6F;
                ModelQuadFacing facing = ModelQuadFacing.fromDirection(dir);
                calculateQuadColors(quad, world, pos, lighter, dir, br, colorizer, fluid.getDefaultState(), colorBlender);
                int vertexStart = writeVertices(buffers, rel, quad);
                buffers.getIndexBufferBuilder(facing).add(vertexStart, ModelQuadWinding.CLOCKWISE);
                if (sprite != waterOverlaySprite) {
                    buffers.getIndexBufferBuilder(facing.getOpposite()).add(vertexStart, ModelQuadWinding.COUNTERCLOCKWISE);
                }

                rendered = true;
            }
        }

        return rendered;
    }

    private static void setVertex(ModelQuadViewMutable quad, int i, float x, float y, float z, float u, float v) {
        quad.setX(i, x);
        quad.setY(i, y);
        quad.setZ(i, z);
        quad.setTexU(i, u);
        quad.setTexV(i, v);
    }

    private static void calculateQuadColors(
            ModelQuadView quad, BlockRenderView world, BlockPos pos, LightPipeline lighter,
            Direction dir, float brightness, ColorSampler<FluidState> colorSampler, FluidState fluidState,
            ColorBlender blender
    ) {
        QuadLightData light = quadLightData;
        lighter.calculate(quad, pos, light, dir, false);
        int[] biomeColors = blender.getColors(world, pos, quad, colorSampler, fluidState);

        for(int i = 0; i < 4; ++i) {
            quadColors[i] = ColorABGR.mul(biomeColors != null ? biomeColors[i] : -1, light.br[i] * brightness);
        }
    }

    private static ColorSampler<FluidState> createColorProviderAdapter(FluidRenderHandler handler) {
        FabricFluidColorizerAdapter adapter = fabricColorProviderAdapter;
        adapter.setHandler(handler);
        return adapter;
    }

    private static int writeVertices(ChunkModelBuilder builder, BlockPos offset, ModelQuadView quad) {
        ModelVertexSink vertices = builder.getVertexSink();
        vertices.ensureCapacity(4);
        int vertexStart = vertices.getVertexCount();

        for(int i = 0; i < 4; ++i) {
            float x = quad.getX(i);
            float y = quad.getY(i);
            float z = quad.getZ(i);
            int color = quadColors[i];
            float u = quad.getTexU(i);
            float v = quad.getTexV(i);
            int light = quadLightData.lm[i];
            vertices.writeVertex(offset, x, y, z, color, u, v, light, builder.getChunkId());
        }

        vertices.flush();
        Sprite sprite = quad.getSprite();
        if (sprite != null) {
            builder.addSprite(sprite);
        }

        return vertexStart;
    }

    private static class FabricFluidColorizerAdapter implements ColorSampler<FluidState> {
        private FluidRenderHandler handler;

        private FabricFluidColorizerAdapter() {
        }

        public void setHandler(FluidRenderHandler handler) {
            this.handler = handler;
        }

        public int getColor(FluidState state, @Nullable BlockRenderView world, @Nullable BlockPos pos, int tintIndex) {
            return this.handler == null ? -1 : this.handler.getFluidColor(world, pos, state);
        }
    }
}
