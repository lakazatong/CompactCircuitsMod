package net.lakazatong.compactcircuitsmod.datacomponents;

import net.lakazatong.compactcircuitsmod.datacomponents.custom.LastPosDataComponent;
import net.lakazatong.compactcircuitsmod.utils.Services;
import net.minecraft.core.component.DataComponentType;

import java.util.function.Supplier;

public class ModDataComponents {
    public static final Supplier<DataComponentType<LastPosDataComponent>> LAST_POS = Services.REGISTRY.registerDataComponent("last_pos", LastPosDataComponent::getBuilder);
}
