package io.github.SirWashington.mixin;

import me.jellysquid.mods.sodium.client.world.WorldSlice;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;


@Mixin(WorldSlice.class)
public interface WorldSliceAccessor {

    @Accessor("world")
    World getWorld();
}
