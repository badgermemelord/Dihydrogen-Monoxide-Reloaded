package io.github.SirWashington;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.ModelPredicateProviderRegistry;

import net.minecraft.util.Identifier;

import static io.github.SirWashington.item.ModItems.PRECISION_BUCKET;

public class WaterPhysicsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        registerItemProperties();
        System.out.println("EEEEaaaa");
    }


    public static void registerItemProperties() {
        // For versions before 1.21, replace 'Identifier.ofVanilla' with 'new Identifier'.
        ModelPredicateProviderRegistry.register(PRECISION_BUCKET, new Identifier("bucketlevel"), (itemStack, clientWorld, livingEntity, seed) -> {
            return itemStack.getOrCreateNbt().getInt("washwater:bucketFillLevel")/8f;

        });
    }

    public static void printma() {
        System.out.println("Efefesnufehbfrbui");
    }


}