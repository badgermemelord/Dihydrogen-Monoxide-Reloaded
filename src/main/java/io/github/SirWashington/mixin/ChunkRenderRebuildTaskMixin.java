package io.github.SirWashington.mixin;

import com.ewoudje.lasagna.chunkstorage.ExtraStorageSectionContainer;
import io.github.SirWashington.WaterSection;
import io.github.SirWashington.WaterVolume;
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
            //System.out.println("getblockstate water not null: " + (state.isAir() ? WaterVolume.getWaterState(fluidVolume).getBlockState() : state));
            return state.isAir() ? WaterVolume.getWaterState(fluidVolume).getBlockState() : state;
        } else {
            //System.out.println("empty");
            fluidVolume = 0;
        }

        return state;
    }

    @Redirect(
            method = "performBuild",
            at = @At(value = "INVOKE", target = "Lme/jellysquid/mods/sodium/client/render/pipeline/FluidRenderer;render(Lnet/minecraft/world/BlockRenderView;Lnet/minecraft/fluid/FluidState;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/BlockPos;Lme/jellysquid/mods/sodium/client/render/chunk/compile/buffers/ChunkModelBuilder;)Z")
    )
    boolean getFluidState(FluidRenderer instance, BlockRenderView world, FluidState state, BlockPos pos, BlockPos rel, ChunkModelBuilder builder) {
        return io.github.SirWashington.renderer.FluidRenderer.getInstance(instance).render(world, pos, rel, builder);
    }

}
