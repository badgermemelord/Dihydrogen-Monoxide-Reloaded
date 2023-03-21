package io.github.SirWashington.mixin;

import net.minecraft.block.Blocks;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.chunk.GenerationShapeConfig;
import net.minecraft.world.gen.densityfunction.DensityFunctions;
import net.minecraft.world.gen.surfacebuilder.VanillaSurfaceRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(net.minecraft.world.gen.chunk.ChunkGeneratorSettings.class)
public abstract class ChunkGeneratorSettingsMixin {
/*    @Overwrite
    private static net.minecraft.world.gen.chunk.ChunkGeneratorSettings createNetherSettings() {
        return new net.minecraft.world.gen.chunk.ChunkGeneratorSettings(GenerationShapeConfigAccessor.getField_37138(), Blocks.NETHERRACK.getDefaultState(), Blocks.GOLD_BLOCK.getDefaultState(), DensityFunctionInvoker.invokeMethod_41118(GenerationShapeConfigAccessor.getField_37138()), VanillaSurfaceRules.createNetherSurfaceRule(), 32, false, false, false, true);
    }*/
    @Overwrite
    private static ChunkGeneratorSettings createSurfaceSettings(boolean amplified, boolean largeBiomes) {
        GenerationShapeConfig generationShapeConfig = GenerationShapeConfigInvoker.invokeMethod_41126(amplified);
        return new ChunkGeneratorSettings(generationShapeConfig, Blocks.STONE.getDefaultState(), Blocks.WATER.getDefaultState(), DensityFunctionInvoker.invokeMethod_41103(generationShapeConfig, largeBiomes), VanillaSurfaceRules.createOverworldSurfaceRule(), 63, false, true, true, false);
    }
}
