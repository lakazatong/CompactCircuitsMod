package net.lakazatong.compactcircuitsmod.item;

import net.lakazatong.compactcircuitsmod.CompactCircuitsMod;
import net.lakazatong.compactcircuitsmod.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import static net.lakazatong.compactcircuitsmod.utils.Utils.ctranslate;

public class ModCreativeModTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CompactCircuitsMod.MOD_ID);
    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
    public static final RegistryObject<CreativeModeTab> COMPACT_CIRCUITS_MOD_TAB =
            CREATIVE_MODE_TABS.register("compact_circuits_mod_tab", () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(ModItems.ICON.get()))
                    .title(ctranslate("tab", "name"))
                    .displayItems((itemDisplayParameters, output) -> {
                        output.accept(ModBlocks.HUB_BLOCK.get());
                        output.accept(ModBlocks.PORT_BLOCK.get());
                        output.accept(ModBlocks.LAMP_BLOCK.get());
                        output.accept(Items.NAME_TAG);
                        output.accept(ModItems.MAGIC_WAND.get());
                    })
                    .build());
}
