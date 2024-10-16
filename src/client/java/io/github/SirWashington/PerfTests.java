package io.github.SirWashington;

import carpet.helpers.TickSpeed;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.SaveLevelScreen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.network.ClientConnection;
import net.minecraft.resource.DataPackSettings;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.FileNameUtil;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.level.LevelInfo;

import java.io.IOException;
import java.util.OptionalLong;

public class PerfTests {

    public static boolean isPerfTest() {
        return perfTest;
    }

    private static volatile boolean perfTest = false;
    private static long ticksToExecute = 800;
    private static float testTickRate = 20f;

    private static final String WORLD_NAME = "Perf Test World";

    private static boolean startTesting = false;
    private static long tickStart = -1;
    private static long numTicks = 0;
    private static double avgTickTime = -1;

    private static volatile double mspt = -1;

    private static int simulationDistance = 5;

    public static void init() {
        perfTest = "default".equalsIgnoreCase(System.getProperty("washwater.perftest"));

        if (perfTest) {
            ServerLifecycleEvents.SERVER_STARTED.register(s -> s.getPlayerManager().setSimulationDistance(simulationDistance));
            ServerTickEvents.END_SERVER_TICK.register(PerfTests::onTickEnd);
            ServerTickEvents.START_SERVER_TICK.register(e -> tickStart = System.nanoTime());
            ClientLifecycleEvents.CLIENT_STARTED.register(PerfTests::onClientStart);
            ClientTickEvents.END_CLIENT_TICK.register(client -> {
                if (mspt > 0) {
                    mspt = -1;
                    perfTest = false;
                    client.world.disconnect();
                    client.disconnect(new SaveLevelScreen(new TranslatableText("menu.savingLevel")));
                    client.setScreen(new TitleScreen());
                    TickSpeed.tickrate(20f);
                }
            });
        }
    }

    private static void onClientStart(MinecraftClient client) {
        client.setScreenAndRender(new SaveLevelScreen(new TranslatableText("createWorld.preparing")));
        GeneratorOptions generatorOptions = GeneratorOptions.getDefaultOptions(DynamicRegistryManager.BUILTIN.get())
            .withHardcore(false, OptionalLong.of(3820716206651411877L));

        LevelInfo levelInfo = new LevelInfo(
            "New World", GameMode.CREATIVE, false, Difficulty.PEACEFUL, true, new GameRules(), DataPackSettings.SAFE_MODE
        );

        String saveDirectoryName;
        try {
             saveDirectoryName = FileNameUtil.getNextUniqueName(client.getLevelStorage().getSavesDirectory(), WORLD_NAME, "");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        client.createWorld(saveDirectoryName, levelInfo, DynamicRegistryManager.BUILTIN.get(), generatorOptions);
    }

    private static void onTickEnd(MinecraftServer server) {
        ServerPlayerEntity player = server.getPlayerManager().getPlayer(server.getSinglePlayerName());
        if (!startTesting || player == null) return;
        if (numTicks++ == 0) {
            int chunkX = player.getBlockX() >> 4;
            int chunkZ = player.getBlockZ() >> 4;

            for (int x = -simulationDistance; x <= simulationDistance; x++) {
                for (int z = -simulationDistance; z <= simulationDistance; z++) {
                    player.world.getChunk(chunkX + x, chunkZ + z);
                }
            }

            player.getServer().getCommandManager().execute(player.getCommandSource(), "/fill ~-5 ~0 ~5 ~5 ~-10 ~40 air replace stone");
            player.getServer().getCommandManager().execute(player.getCommandSource(), "/fill ~-5 ~0 ~5 ~5 ~-10 ~40 air replace granite");
            return;
        }

        double tickTime = System.nanoTime() - tickStart;
        if (avgTickTime < 0) {
            avgTickTime = tickTime;
        } else {
            avgTickTime = avgTickTime * (numTicks - 1) / numTicks +  tickTime / numTicks;
        }

        if (numTicks == ticksToExecute) {
            mspt = avgTickTime / 1e6;
            System.out.printf("Performance Result: MSPT = %.1f%n", mspt);
        }

    }

    public static void onPlayerConnect(ClientConnection connection, ServerPlayerEntity player) {
        if (perfTest) {
            player.teleport(361, 65, 766);

            startTesting = true;
            if (testTickRate != 20f) {
                TickSpeed.tickrate(testTickRate);
            }
        }
    }

}
