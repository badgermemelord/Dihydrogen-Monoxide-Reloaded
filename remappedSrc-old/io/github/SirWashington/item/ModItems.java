package io.github.SirWashington.item;


import io.github.SirWashington.WaterPhysics;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;


public class ModItems {

    public static final Item PRECISION_BUCKET = registerItem("precision_bucket", new PrecisionBucketItem(new Item.Properties().tab(CreativeModeTab.TAB_COMBAT)));

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registry.ITEM, new ResourceLocation(WaterPhysics.MODID, name), item);

    }

    public static void RegisterModItems() {
    }

}
