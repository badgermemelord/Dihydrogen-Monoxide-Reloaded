package io.github.SirWashington.item;


import io.github.SirWashington.WaterPhysics;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;


public class ModItems {

    public static final Item PRECISION_BUCKET = registerItem("precision_bucket", new PrecisionBucketItem(new FabricItemSettings()));

    private static void addItemsToCreativeModeTab(FabricItemGroupEntries entries) {
        entries.prepend(PRECISION_BUCKET);
    }
    private static Item registerItem(String name, Item item) {
        return Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(WaterPhysics.MODID, name), item);
    }

    public static void RegisterModItems() {
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.COMBAT).register(ModItems::addItemsToCreativeModeTab);
    }

}
