package net.lakazatong.compactcircuitsmod.block;

import net.lakazatong.compactcircuitsmod.CompactCircuitsMod;
import net.lakazatong.compactcircuitsmod.block.entity.HubBlockEntity;
import net.lakazatong.compactcircuitsmod.block.entity.LampBlockEntity;
import net.lakazatong.compactcircuitsmod.block.entity.PortBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
        DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, CompactCircuitsMod.MOD_ID);

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }

    public static final RegistryObject<BlockEntityType<HubBlockEntity>> HUB_BLOCK =
            BLOCK_ENTITIES.register("hub_block",
                    () -> BlockEntityType.Builder.of(HubBlockEntity::new, ModBlocks.HUB_BLOCK.get())
                            .build(null));

    public static final RegistryObject<BlockEntityType<PortBlockEntity>> PORT_BLOCK =
            BLOCK_ENTITIES.register("port_block",
                    () -> BlockEntityType.Builder.of(PortBlockEntity::new, ModBlocks.PORT_BLOCK.get())
                            .build(null));

    public static final RegistryObject<BlockEntityType<LampBlockEntity>> LAMP_BLOCK =
            BLOCK_ENTITIES.register("lamp_block",
                    () -> BlockEntityType.Builder.of(LampBlockEntity::new, ModBlocks.LAMP_BLOCK.get())
                            .build(null));
}
