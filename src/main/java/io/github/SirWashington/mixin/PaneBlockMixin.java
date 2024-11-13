package io.github.SirWashington.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import static io.github.SirWashington.WaterPhysics.WATER_LEVEL;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CrossCollisionBlock;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

@Mixin(IronBarsBlock.class)
public abstract class PaneBlockMixin extends CrossCollisionBlock {

    protected PaneBlockMixin(float radius1, float radius2, float boundingHeight1, float boundingHeight2, float collisionHeight, Properties settings) {
        super(radius1, radius2, boundingHeight1, boundingHeight2, collisionHeight, settings);
    }

    @Redirect(
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/state/BlockState;setValue(Lnet/minecraft/world/level/block/state/properties/Property;Ljava/lang/Comparable;)Ljava/lang/Object;"
                    //target = "Lnet/minecraft/block/BlockState;with(Lnet/minecraft/state/property/Property;Ljava/lang/Comparable;)Ljava/lang/Object;"
            ),
            method = "<init>"
    )
    Object setDefault(BlockState instance, Property property, Comparable comparable) {
        if (property == WATERLOGGED) {
            property = WATER_LEVEL;
            comparable = 0;
        }

        return instance.setValue(property, comparable);
    }

    // TODO remove overwrites with smort mixining

    /**
     * @author ewoudje
     * @reason replace waterlogged with level 1 to 8
     */
    @Overwrite
    public void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(NORTH, EAST, WEST, SOUTH, WATER_LEVEL);
    }

    /**
     * @author ewoudje
     * @reason replace waterlogged with level 1 to 8
     */
    @Overwrite
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor world, BlockPos pos, BlockPos neighborPos) {
        if (direction.getAxis().isHorizontal()) {
            return state.setValue(PROPERTY_BY_DIRECTION.get(direction), this.attachsTo(neighborState, neighborState.isFaceSturdy(world, neighborPos, direction.getOpposite())));
        }
        return super.updateShape(state, direction, neighborState, world, pos, neighborPos);
    }

    /**
     * @author ewoudje
     * @reason replace waterlogged with level 1 to 8
     */
    @Overwrite
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        Level blockView = ctx.getLevel();
        BlockPos blockPos = ctx.getClickedPos();
        FluidState fluidState = ctx.getLevel().getFluidState(ctx.getClickedPos());
        BlockPos blockPos2 = blockPos.north();
        BlockPos blockPos3 = blockPos.south();
        BlockPos blockPos4 = blockPos.west();
        BlockPos blockPos5 = blockPos.east();
        BlockState blockState = blockView.getBlockState(blockPos2);
        BlockState blockState2 = blockView.getBlockState(blockPos3);
        BlockState blockState3 = blockView.getBlockState(blockPos4);
        BlockState blockState4 = blockView.getBlockState(blockPos5);
        return this.defaultBlockState().setValue(NORTH, this.attachsTo(blockState, blockState.isFaceSturdy(blockView, blockPos2, Direction.SOUTH))).setValue(SOUTH, this.attachsTo(blockState2, blockState2.isFaceSturdy(blockView, blockPos3, Direction.NORTH))).setValue(WEST, this.attachsTo(blockState3, blockState3.isFaceSturdy(blockView, blockPos4, Direction.EAST))).setValue(EAST, this.attachsTo(blockState4, blockState4.isFaceSturdy(blockView, blockPos5, Direction.WEST))).setValue(WATER_LEVEL, fluidState.getAmount());
    }

    @Shadow
    public abstract boolean attachsTo(BlockState state, boolean sideSolidFullSquare);

    @Override
    public FluidState getFluidState(BlockState state) {
        int water = state.getValue(WATER_LEVEL);
        return water == 0 ? Fluids.EMPTY.defaultFluidState() : Fluids.WATER.getFlowing(water, false);
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter world, BlockPos pos) {
        return state.getValue(WATER_LEVEL) == 0;
    }
}
