package io.github.SirWashington.mixin;

import io.github.SirWashington.WaterSection;
import io.github.SirWashington.WaterVolume;
import me.jellysquid.mods.sodium.client.render.chunk.RenderSection;
import me.jellysquid.mods.sodium.client.render.chunk.tasks.ChunkRenderRebuildTask;
import me.jellysquid.mods.sodium.client.world.WorldSlice;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
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
    private FluidState specialFluid = Fluids.EMPTY.getDefaultState();

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
        if (water != null) {
            specialFluid = WaterVolume.getWaterState(water.getWaterVolume(x & 15, y & 15, z & 15));
        }

        return slice.getBlockState(x,y,z);
    }

    @Redirect(
            method = "performBuild",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;getFluidState()Lnet/minecraft/fluid/FluidState;")
    )
    FluidState getFluidState(BlockState state) {
        FluidState fluid = state.getFluidState();
        if (fluid.isEmpty()) {
            return specialFluid;
        } else return fluid;
    }

}
