package io.github.SirWashington.mixin.perf_test;

import io.github.SirWashington.PerfTests;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerManager.class)
public class MixinPlayerManager {

    @Inject(
        at = @At("TAIL"),
        method = "onPlayerConnect"
    )
    private void afterPlayerConnect(ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci) {
        PerfTests.onPlayerConnect(connection, player);
    }

}
