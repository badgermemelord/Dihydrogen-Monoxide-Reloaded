package io.github.SirWashington.properties;

import io.github.SirWashington.features.CachedWater;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;

public class WaterFluidProperties {
    public static final BooleanProperty ISFINITE = BooleanProperty.of("isfinite");
    public static final IntProperty VOLUME = IntProperty.of("volume", 1, CachedWater.volumePerBlock);
}
