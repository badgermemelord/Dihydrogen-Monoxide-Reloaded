package io.github.SirWashington.properties;

import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;

public class WaterFluidProperties {
    public static final BooleanProperty ISFINITE = BooleanProperty.of("isfinite");
    public static final IntProperty INTERNALLEVEL = IntProperty.of("internallevel", 1, 255);
}
