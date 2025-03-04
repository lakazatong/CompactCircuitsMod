package net.lakazatong.compactcircuitsmod.server;

import net.lakazatong.compactcircuitsmod.CompactCircuitsMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.SimpleChannel;

public class PacketHandler {
    private static final SimpleChannel INSTANCE = ChannelBuilder.named(
                ResourceLocation.fromNamespaceAndPath(CompactCircuitsMod.MOD_ID, "main")
            )
            .serverAcceptedVersions((status, version) -> true)
            .clientAcceptedVersions((status, version) -> true)
            .networkProtocolVersion(1)
            .simpleChannel();

    public static void Register() {
        INSTANCE.messageBuilder(UpdatePortPacket.class, NetworkDirection.PLAY_TO_SERVER)
                .encoder(UpdatePortPacket::encode)
                .decoder(UpdatePortPacket::new)
                .consumerMainThread(UpdatePortPacket::handle)
                .add();
        INSTANCE.messageBuilder(UpdateHubPacket.class, NetworkDirection.PLAY_TO_SERVER)
                .encoder(UpdateHubPacket::encode)
                .decoder(UpdateHubPacket::new)
                .consumerMainThread(UpdateHubPacket::handle)
                .add();
        INSTANCE.messageBuilder(UpdateLampPacket.class, NetworkDirection.PLAY_TO_SERVER)
                .encoder(UpdateLampPacket::encode)
                .decoder(UpdateLampPacket::new)
                .consumerMainThread(UpdateLampPacket::handle)
                .add();
    }

    public static void sendToServer(Object msg) {
        INSTANCE.send(msg, PacketDistributor.SERVER.noArg());
    }

    public static void sendToAllClients(Object msg) {
        INSTANCE.send(msg, PacketDistributor.ALL.noArg());
    }
}
