package io.github.SirWashington.properties;

import io.github.SirWashington.WaterVolume;
import io.github.SirWashington.features.CachedWater;
import io.github.SirWashington.features.ConfigVariables;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;

public class WaterFluidProperties {
    public static final BooleanProperty ISFINITE = BooleanProperty.of("isfinite");
}
