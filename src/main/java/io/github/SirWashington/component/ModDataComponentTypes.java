package io.github.SirWashington.component;

import io.github.SirWashington.WaterPhysics;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;

import java.util.function.UnaryOperator;

public class ModDataComponentTypes {

    public static final DataComponentType<Integer> BUCKET_FILL_LEVEL = register("bucket_fill_level", integerBuilder -> integerBuilder);


    private static <T>DataComponentType<T> register(String name, UnaryOperator<DataComponentType.Builder<T>> builderOperator) {
        return Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, ResourceLocation.fromNamespaceAndPath(WaterPhysics.MODID, name),
                builderOperator.apply(DataComponentType.builder()).build());
    }

    public static void registerDataComponentTypes() {

    }

}
