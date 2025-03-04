package net.lakazatong.compactcircuitsmod;

import com.mojang.logging.LogUtils;
import net.lakazatong.compactcircuitsmod.block.ModBlockEntities;
import net.lakazatong.compactcircuitsmod.block.ModBlocks;
import net.lakazatong.compactcircuitsmod.block.custom.PortBlock;
import net.lakazatong.compactcircuitsmod.circuits.CircuitsSavedData;
import net.lakazatong.compactcircuitsmod.datacomponents.custom.LastPosDataComponent;
import net.lakazatong.compactcircuitsmod.item.ModCreativeModTabs;
import net.lakazatong.compactcircuitsmod.item.ModItems;
import net.lakazatong.compactcircuitsmod.screen.NameTagRenameScreen;
import net.lakazatong.compactcircuitsmod.server.PacketHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

import java.util.function.Supplier;

import static net.lakazatong.compactcircuitsmod.block.entity.HubBlockEntity.*;

@Mod(CompactCircuitsMod.MOD_ID)
public class CompactCircuitsMod {
    public static final String MOD_ID = "compactcircuitsmod";
    public static final Logger LOGGER = LogUtils.getLogger();

    protected static final String circuitsSavedDataKey = MOD_ID + "_circuits";
    public static CircuitsSavedData CIRCUITS;

    public static final ResourceKey<Level> CIRCUIT_DIMENSION = ResourceKey.create(Registries.DIMENSION, ResourceLocation.fromNamespaceAndPath(MOD_ID, "circuit_dimension"));

    public CompactCircuitsMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        MinecraftForge.EVENT_BUS.register(this);

        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModBlockEntities.register(modEventBus);

        ModCreativeModTabs.register(modEventBus);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ForgeModEvents {
        @SubscribeEvent
        public static void onLevelLoad(LevelEvent.Load event) {
            if (event.getLevel() instanceof ServerLevel level)
                CIRCUITS = level.getDataStorage().computeIfAbsent(
                    new SavedData.Factory<>(CircuitsSavedData::new, CircuitsSavedData::load, null),
                    circuitsSavedDataKey);
        }
        @SubscribeEvent
        public static void onRightClick(PlayerInteractEvent.RightClickItem event) {
            Player player = event.getEntity();
            if (player == null || !player.isCreative() || !event.getLevel().isClientSide()) return;
            ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);
            if (stack.getItem() == Items.NAME_TAG)
                Minecraft.getInstance().setScreen(new NameTagRenameScreen(stack));
        }
    }

    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void commonSetup(FMLClientSetupEvent event) {
            event.enqueueWork(PacketHandler::Register);
        }

        @SubscribeEvent
        public static void onRegisterBlockColors(RegisterColorHandlersEvent.Block event) {
            event.register(
                    (state, world, pos, tintIndex) -> {
                        if (world == null || pos == null) return 0xFFFFFF;
                        int side = state.getValue(PortBlock.SIDE);
                        return getColorForSide(side);
                    },
                    ModBlocks.PORT_BLOCK.get()
            );
            event.register(
                    (state, world, pos, tintIndex) -> {
                        if (world == null || pos == null) return 0xFFFFFF;
                        return getColorForSide(tintIndex);
                    },
                    ModBlocks.HUB_BLOCK.get()
            );
        }

        public static int getColorForSide(int side) {
            return switch (side) {
                case FRONT -> 0xFF0000; // Red
                case BACK -> 0x00FF00; // Green
                case RIGHT -> 0x0000FF; // Blue
                case LEFT -> 0xFFFF00; // Yellow
                case UP -> 0xFF00FF; // Magenta
                case DOWN -> 0x00FFFF; // Cyan
                default -> 0xFFFFFF; // None: White
            };
        }
    }

    public static ResourceLocation rl(String name) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, name);
    }
}
