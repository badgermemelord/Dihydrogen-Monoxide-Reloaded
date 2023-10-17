package io.github.SirWashington.mixin;

import io.github.SirWashington.WaterSection;
import io.github.SirWashington.WaterVolume;
import io.github.SirWashington.renderer.WaterRenderer;
import me.jellysquid.mods.sodium.client.render.chunk.RenderSection;
import me.jellysquid.mods.sodium.client.render.chunk.compile.buffers.ChunkModelBuilder;
import me.jellysquid.mods.sodium.client.render.chunk.tasks.ChunkRenderRebuildTask;
import me.jellysquid.mods.sodium.client.render.pipeline.FluidRenderer;
import me.jellysquid.mods.sodium.client.world.WorldSlice;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.World;
import org.mashed.lasagna.chunkstorage.ExtraStorageSectionContainer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ChunkRenderRebuildTask.class)
public class ChunkRenderRebuildTaskMixin {

    @Unique
    private ExtraStorageSectionContainer container;

    @Unique
    private short fluidVolume = 0;

    @Redirect(
            method = "performBuild",
            at = @At(value = "INVOKE", target = "Lme/jellysquid/mods/sodium/client/world/WorldSlice;getBlockState(III)Lnet/minecraft/block/BlockState;")
    )
    BlockState getBlockState(WorldSlice slice, int x, int y, int z) {
        if (container == null) {
            World world = ((WorldSliceAccessor) slice).getWorld();
            container = (ExtraStorageSectionContainer) (world.getChunk(x >> 4, z >> 4).getSection(world.getSectionIndex(y)));
        }

        WaterSection water = (WaterSection) container.getSectionStorage(WaterSection.ID);
        BlockState state = slice.getBlockState(x,y,z);

        if (water != null) {
            fluidVolume = water.getWaterVolume(x & 15, y & 15, z & 15);
            return state.isAir() ? WaterVolume.getWaterState(fluidVolume).getBlockState() : state;
        } else {
            fluidVolume = 0;
        }

        return state;
    }

    @Redirect(
            method = "performBuild",
            at = @At(value = "INVOKE", target = "Lme/jellysquid/mods/sodium/client/render/pipeline/FluidRenderer;render(Lnet/minecraft/world/BlockRenderView;Lnet/minecraft/fluid/FluidState;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/BlockPos;Lme/jellysquid/mods/sodium/client/render/chunk/compile/buffers/ChunkModelBuilder;)Z")
    )
    boolean getFluidState(FluidRenderer instance, BlockRenderView world, FluidState state, BlockPos pos, BlockPos rel, ChunkModelBuilder builder) {
        return WaterRenderer.renderWater(world,
                fluidVolume, Fluids.WATER,
                pos, rel,
                builder,
                ((FluidRendererAccessor) instance).getLighters(),
                ((FluidRendererAccessor) instance).getColorBlender()
        );
    }

}
