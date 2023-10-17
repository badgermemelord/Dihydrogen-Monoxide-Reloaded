package io.github.SirWashington.mixin;

import me.jellysquid.mods.sodium.client.model.light.LightPipelineProvider;
import me.jellysquid.mods.sodium.client.model.quad.blender.ColorBlender;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(me.jellysquid.mods.sodium.client.render.pipeline.FluidRenderer.class)
public interface FluidRendererAccessor {

    @Accessor("lighters")
    LightPipelineProvider getLighters();
    @Accessor("colorBlender")
    ColorBlender getColorBlender();
}
