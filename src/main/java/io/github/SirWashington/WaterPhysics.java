package io.github.SirWashington;

import io.github.SirWashington.features.NonCachedWater;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.text.Text;

public class WaterPhysics implements ModInitializer {

    public static final IntProperty WATER_LEVEL = IntProperty.of("water_level", 0, 8);


    @Override
    public void onInitialize() {

        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        System.out.println("Dihydrogen Monoxide Reloaded has loaded!");

        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            dispatcher.register(CommandManager.literal("waterlevel")
                    .then(CommandManager.argument("pos", BlockPosArgumentType.blockPos())
                            .executes(context -> {
                                try {
                                    int result = NonCachedWater.getLevel(BlockPosArgumentType.getBlockPos(context, "pos"), context.getSource().getWorld());
                                    context.getSource().sendFeedback(Text.of("Water level at " + BlockPosArgumentType.getBlockPos(context, "pos") + " is " + result), false);
                                    return result;
                                } catch (Exception e) {
                                    context.getSource().sendError(Text.of("AA Something went wrong"));
                                    e.printStackTrace();
                                    return -9999;
                                }
                            })));
        });
        PerfTests.init();
    }
}
