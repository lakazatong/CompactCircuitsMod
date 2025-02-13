package in.lakazatong.compactcircuitsmod.registries;

import in.lakazatong.compactcircuitsmod.platform.Services;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

import java.util.function.Supplier;

public class ModTabs {
    public static final Supplier<CreativeModeTab> EXAMPLE_TAB = Services.REGISTRY.registerCreativeModeTab(
            "compact_circuits_mod",
            Component.translatable("itemGroup.compact_circuits_mod"),
            Items.GRASS_BLOCK::getDefaultInstance, () -> Items.GRASS_BLOCK);

    public static void trigger() { }
}
