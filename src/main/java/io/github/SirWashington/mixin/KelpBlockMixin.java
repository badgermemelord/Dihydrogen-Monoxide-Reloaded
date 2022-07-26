package io.github.SirWashington.mixin;

import io.github.SirWashington.FlowWater;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.KelpBlock;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(KelpBlock.class)
public abstract class KelpBlockMixin extends AbstractBlock {

    public KelpBlockMixin(Settings settings) {
        super(settings);
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
        world.createAndScheduleFluidTick(pos, state.getFluidState().getFluid(), Fluids.WATER.getTickRate(world));
        super.neighborUpdate(state, world, pos, block, fromPos, notify);
    }

}
