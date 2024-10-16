package io.github.SirWashington.item;


import io.github.SirWashington.WaterPhysics;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;


public class ModItems {

    public static final Item PRECISION_BUCKET = registerItem("precision_bucket", new PrecisionBucketItem(new Item.Settings().group(ItemGroup.COMBAT)));

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registry.ITEM, new Identifier(WaterPhysics.MODID, name), item);

    }

    public static void RegisterModItems() {
    }

}
