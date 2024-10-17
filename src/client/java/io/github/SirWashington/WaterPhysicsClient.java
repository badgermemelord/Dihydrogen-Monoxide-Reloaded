package io.github.SirWashington;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;

import static io.github.SirWashington.item.ModItems.PRECISION_BUCKET;

public class WaterPhysicsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        registerItemProperties();
    }

    public static void registerItemProperties() {
        // For versions before 1.21, replace 'Identifier.ofVanilla' with 'new Identifier'.
        ItemProperties.register(PRECISION_BUCKET, new ResourceLocation("bucketlevel"), (itemStack, clientWorld, livingEntity, seed) -> {
            return itemStack.getOrCreateTag().getInt("washwater:bucketFillLevel") / 8f;
        });
    }
}