package io.github.SirWashington.mixin;

import net.minecraft.client.render.BuiltChunkStorage;
import net.minecraft.client.render.WorldRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(BuiltChunkStorage.class)
public interface BuiltChunkStorageAccessor {

    @Invoker("getChunkIndex")
    int invokeGetChunkIndex(int x, int y, int z);

}
