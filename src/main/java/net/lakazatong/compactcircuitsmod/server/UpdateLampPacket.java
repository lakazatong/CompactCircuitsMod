package net.lakazatong.compactcircuitsmod.server;

import net.lakazatong.compactcircuitsmod.block.entity.LampBlockEntity;
import net.minecraft.network.FriendlyByteBuf;

public class UpdateLampPacket extends BaseModPacket<LampBlockEntity, UpdateLampPacket> {
    public final Boolean clicked;

    public UpdateLampPacket(LampBlockEntity be) {
        super(be.getBlockPos());
        this.clicked = be.getClicked();
    }

    public UpdateLampPacket(FriendlyByteBuf buf) {
        super(buf.readBlockPos());

        this.clicked = buf.readBoolean();
    }

    void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);

        buf.writeBoolean(clicked);
    }
}