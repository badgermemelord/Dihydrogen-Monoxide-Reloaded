package io.github.SirWashington.nbtUtil;

import io.github.SirWashington.component.ModDataComponentTypes;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.ItemStack;

public class DataComponentUtils {

    public static <Integer> java.lang.Integer getOrCreateComponent(DataComponentType<? extends Integer> dataComponentType, ItemStack itemStack) {
        if (!itemStack.has(ModDataComponentTypes.BUCKET_FILL_LEVEL)) {
            itemStack.set(ModDataComponentTypes.BUCKET_FILL_LEVEL, 0);
        }
        return itemStack.get(ModDataComponentTypes.BUCKET_FILL_LEVEL);
    }

}
