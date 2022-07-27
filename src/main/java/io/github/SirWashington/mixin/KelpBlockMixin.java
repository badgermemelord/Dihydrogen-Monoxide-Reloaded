package io.github.SirWashington.mixin;

import io.github.SirWashington.FlowWater;
import io.github.SirWashington.features.NonCachedWater;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.AbstractPlantStemBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.KelpBlock;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Random;

@Mixin(KelpBlock.class)
public abstract class KelpBlockMixin extends AbstractPlantStemBlock {


    protected KelpBlockMixin(Settings settings, Direction growthDirection, VoxelShape outlineShape, boolean tickWater, double growthChance) {
        super(settings, growthDirection, outlineShape, tickWater, growthChance);
    }

    @Override
    public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
        return NonCachedWater.getLevel(pos.up(), world) == 8;
    }
}
