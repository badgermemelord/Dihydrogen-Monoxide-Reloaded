package io.github.SirWashington.mixin;

import net.minecraft.world.gen.chunk.GenerationShapeConfig;
import net.minecraft.world.gen.noise.SimpleNoiseRouter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(net.minecraft.world.gen.densityfunction.DensityFunctions.class)
public interface DensityFunctionInvoker {
/*    @Accessor("method_41118")
    public static SimpleNoiseRouter getMethod_41118() {
        throw new AssertionError();
    }*/
    @Invoker("method_41118")
    public static SimpleNoiseRouter invokeMethod_41118(GenerationShapeConfig generationShapeConfig) {
        throw new AssertionError();
    }
    @Invoker("method_41103")
    public static SimpleNoiseRouter invokeMethod_41103(GenerationShapeConfig generationShapeConfig, boolean bl) {
        throw new AssertionError();
    }

}
