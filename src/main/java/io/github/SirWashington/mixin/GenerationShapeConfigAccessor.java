package io.github.SirWashington.mixin;

import net.minecraft.world.gen.chunk.GenerationShapeConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(net.minecraft.world.gen.chunk.GenerationShapeConfig.class)
public interface GenerationShapeConfigAccessor {

    @Accessor("field_37138")
    public static GenerationShapeConfig getField_37138() {
        throw new AssertionError();
    }

}

