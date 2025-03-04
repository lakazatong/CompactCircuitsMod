package net.lakazatong.compactcircuitsmod.block;

import net.lakazatong.compactcircuitsmod.CompactCircuitsMod;
import net.lakazatong.compactcircuitsmod.block.custom.HubBlock;
import net.lakazatong.compactcircuitsmod.block.custom.LampBlock;
import net.lakazatong.compactcircuitsmod.block.custom.PortBlock;
import net.lakazatong.compactcircuitsmod.item.ModItems;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, CompactCircuitsMod.MOD_ID);

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }

    private static <T extends Block> void registerBlockItem(String name, RegistryObject<T> block) {
        ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block) {
        RegistryObject<T> r = BLOCKS.register(name, block);
        registerBlockItem(name, r);
        return r;
    }

    public static final RegistryObject<Block> HUB_BLOCK = registerBlock("hub_block",
            () -> new HubBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)
                    .isRedstoneConductor((a, b, c) -> false)
            ));

    public static final RegistryObject<Block> PORT_BLOCK = registerBlock("port_block",
            () -> new PortBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)
                    .isRedstoneConductor((a, b, c) -> true)
            ));

    public static final RegistryObject<Block> LAMP_BLOCK = registerBlock("lamp_block",
            () -> new LampBlock(BlockBehaviour.Properties.of().strength(3f)
                    .lightLevel(state -> state.getValue(LampBlock.CLICKED) ? 15 : 0)));
}
