package io.github.SirWashington.mixin.perf_test;

import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerList.class)
public class MixinPlayerManager {

    @Inject(
        at = @At("TAIL"),
        method = "onPlayerConnect"
    )
    private void afterPlayerConnect(Connection connection, ServerPlayer player, CallbackInfo ci) {
        //PerfTestsOld.onPlayerConnect(connection, player);
    }

}
