package io.github.SirWashington;

import io.github.SirWashington.features.NonCachedWater;
import io.github.SirWashington.item.ModItems;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class WaterPhysics implements ModInitializer {

    public static final String MODID = "immersivefluids";
    public static final IntegerProperty WATER_LEVEL = IntegerProperty.create("water_level", 0, 8);


    @Override
    public void onInitialize() {

        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.
        ModItems.RegisterModItems();

        System.out.println("Immersive Fluids has loaded!");

        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            dispatcher.register(Commands.literal("waterlevel")
                    .then(Commands.argument("pos", BlockPosArgument.blockPos())
                            .executes(context -> {
                                try {
                                    int result = NonCachedWater.getLevel(BlockPosArgument.getSpawnablePos(context, "pos"), context.getSource().getLevel());
                                    context.getSource().sendSuccess(Component.nullToEmpty("Water level at " + BlockPosArgument.getSpawnablePos(context, "pos") + " is " + result), false);
                                    return result;
                                } catch (Exception e) {
                                    context.getSource().sendFailure(Component.nullToEmpty("AA Something went wrong"));
                                    e.printStackTrace();
                                    return -9999;
                                }
                            })));
        });
        //PerfTestsOld.init();
    }


}
