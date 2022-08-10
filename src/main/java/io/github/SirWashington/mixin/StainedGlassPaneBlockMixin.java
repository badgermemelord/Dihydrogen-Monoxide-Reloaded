package io.github.SirWashington.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.StainedGlassPaneBlock;
import net.minecraft.state.property.Property;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import static io.github.SirWashington.WaterPhysics.WATER_LEVEL;
import static net.minecraft.state.property.Properties.WATERLOGGED;

@Mixin(StainedGlassPaneBlock.class)
public class StainedGlassPaneBlockMixin {

    @Redirect(
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/BlockState;with(Lnet/minecraft/state/property/Property;Ljava/lang/Comparable;)Ljava/lang/Object;"
            ),
            method = "<init>"
    )
    Object setDefault(BlockState instance, Property property, Comparable comparable) {
        if (property == WATERLOGGED) {
            property = WATER_LEVEL;
            comparable = 0;
        }

        return instance.with(property, comparable);
    }

}
