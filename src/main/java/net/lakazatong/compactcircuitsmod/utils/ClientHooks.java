package net.lakazatong.compactcircuitsmod.utils;

import net.lakazatong.compactcircuitsmod.screen.HubScreen;
import net.lakazatong.compactcircuitsmod.screen.LampScreen;
import net.lakazatong.compactcircuitsmod.screen.PortScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;

public class ClientHooks {
    public static void openHubScreen(BlockPos pos) {
        Minecraft.getInstance().setScreen(new HubScreen(pos));
    }
    public static void openPortScreen(BlockPos pos) {
        Minecraft.getInstance().setScreen(new PortScreen(pos));
    }
    public static void openLampScreen(BlockPos pos) {
        Minecraft.getInstance().setScreen(new LampScreen(pos));
    }
}
