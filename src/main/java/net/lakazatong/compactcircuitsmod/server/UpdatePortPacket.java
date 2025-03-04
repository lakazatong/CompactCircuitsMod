package net.lakazatong.compactcircuitsmod.server;

import net.lakazatong.compactcircuitsmod.block.entity.PortBlockEntity;
import net.minecraft.network.FriendlyByteBuf;

public class UpdatePortPacket extends BaseModPacket<PortBlockEntity, UpdatePortPacket> {
    public final boolean isInput;
    public final boolean isOpen;
    public final int side;

    public final String circuitId;
    public final int portNumber;
    public final int busSize;

    public UpdatePortPacket(PortBlockEntity be) {
        super(be.getBlockPos());

        this.isInput = be.getIsInput();
        this.isOpen = be.getIsOpen();
        this.side = be.getSide();

        this.circuitId = be.getCircuitId();
        this.portNumber = be.getPortNumber();
        this.busSize = be.getBusSize();
    }

    public UpdatePortPacket(FriendlyByteBuf buf) {
        super(buf.readBlockPos());

        this.isInput = buf.readBoolean();
        this.isOpen = buf.readBoolean();
        this.side = buf.readInt();

        this.circuitId = buf.readUtf();
        this.portNumber = buf.readInt();
        this.busSize = buf.readInt();
    }

    void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(this.pos);

        buf.writeBoolean(this.isInput);
        buf.writeBoolean(this.isOpen);
        buf.writeInt(this.side);

        buf.writeUtf(this.circuitId);
        buf.writeInt(this.portNumber);
        buf.writeInt(this.busSize);
    }
}