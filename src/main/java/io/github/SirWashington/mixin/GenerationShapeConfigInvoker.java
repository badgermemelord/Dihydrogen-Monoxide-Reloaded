package io.github.SirWashington.mixin;

import net.minecraft.world.biome.source.util.VanillaTerrainParametersCreator;
import net.minecraft.world.gen.chunk.GenerationShapeConfig;
import net.minecraft.world.gen.chunk.NoiseSamplingConfig;
import net.minecraft.world.gen.chunk.SlideConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(net.minecraft.world.gen.chunk.GenerationShapeConfig.class)
public interface GenerationShapeConfigInvoker {

    @Invoker("method_41126")
    public static GenerationShapeConfig invokeMethod_41126(boolean bl) {
        throw new AssertionError();
    }
}

