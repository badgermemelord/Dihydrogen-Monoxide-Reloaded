package io.github.SirWashington.mixin;

import net.minecraft.block.Blocks;
import net.minecraft.world.gen.chunk.GenerationShapeConfig;
import net.minecraft.world.gen.surfacebuilder.VanillaSurfaceRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(net.minecraft.world.gen.chunk.ChunkGeneratorSettings.class)
public abstract class ChunkGeneratorSettingsMixin {
    @Overwrite
    private static net.minecraft.world.gen.chunk.ChunkGeneratorSettings createNetherSettings() {
        return new net.minecraft.world.gen.chunk.ChunkGeneratorSettings(GenerationShapeConfigAccessor.getField_37138(), Blocks.NETHERRACK.getDefaultState(), Blocks.GOLD_BLOCK.getDefaultState(), DensityFunctionInvoker.invokeMethod_41118(GenerationShapeConfigAccessor.getField_37138()), VanillaSurfaceRules.createNetherSurfaceRule(), 32, false, false, false, true);
    }
}
