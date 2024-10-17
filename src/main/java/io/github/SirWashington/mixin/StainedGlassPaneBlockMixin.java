package io.github.SirWashington.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import static io.github.SirWashington.WaterPhysics.WATER_LEVEL;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.WATERLOGGED;

import net.minecraft.world.level.block.StainedGlassPaneBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

@Mixin(StainedGlassPaneBlock.class)
public class StainedGlassPaneBlockMixin {

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

}
