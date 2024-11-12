package io.github.SirWashington;

import io.github.SirWashington.component.ModDataComponentTypes;
import io.github.SirWashington.nbtUtil.DataComponentUtils;
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
        ItemProperties.register(PRECISION_BUCKET, ResourceLocation.parse("bucketlevel"), (itemStack, clientWorld, livingEntity, seed) -> {
            return DataComponentUtils.getOrCreateComponent(ModDataComponentTypes.BUCKET_FILL_LEVEL, itemStack) / 8f;
        });
    }
}