package net.lakazatong.compactcircuitsmod.block;

import net.lakazatong.compactcircuitsmod.block.renderer.HubBlockEntityRenderer;
import net.lakazatong.compactcircuitsmod.block.renderer.PortBlockEntityRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static net.lakazatong.compactcircuitsmod.CompactCircuitsMod.MOD_ID;

@Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModBlockEntityRenderers {
    @SubscribeEvent
    public static void registerBER(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.HUB_BLOCK.get(), HubBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.PORT_BLOCK.get(), PortBlockEntityRenderer::new);
    }
}