package net.lakazatong.compactcircuitsmod.server;

import net.lakazatong.compactcircuitsmod.block.entity.HubBlockEntity;
import net.minecraft.network.FriendlyByteBuf;

import static net.lakazatong.compactcircuitsmod.block.entity.HubBlockEntity.*;

public class UpdateHubPacket extends BaseModPacket<HubBlockEntity, UpdateHubPacket> {
    public final int frontPortState;
    public final int backPortState;
    public final int rightPortState;
    public final int leftPortState;
    public final int upPortState;
    public final int downPortState;

    public final String circuitId;
    public final int[] portNumbers;

    public UpdateHubPacket(HubBlockEntity be) {
        super(be.getBlockPos());

        this.frontPortState = be.portStateOf(FRONT);
        this.backPortState = be.portStateOf(BACK);
        this.rightPortState = be.portStateOf(RIGHT);
        this.leftPortState = be.portStateOf(LEFT);
        this.upPortState = be.portStateOf(UP);
        this.downPortState = be.portStateOf(DOWN);

        this.circuitId = be.getCircuitId();
        this.portNumbers = be.getPortNumbers();
    }

    public UpdateHubPacket(FriendlyByteBuf buf) {
        super(buf.readBlockPos());

        this.frontPortState = buf.readInt();
        this.backPortState = buf.readInt();
        this.rightPortState = buf.readInt();
        this.leftPortState = buf.readInt();
        this.upPortState = buf.readInt();
        this.downPortState = buf.readInt();

        this.circuitId = buf.readUtf();
        this.portNumbers = buf.readVarIntArray();
    }

    void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);

        buf.writeInt(frontPortState);
        buf.writeInt(backPortState);
        buf.writeInt(rightPortState);
        buf.writeInt(leftPortState);
        buf.writeInt(upPortState);
        buf.writeInt(downPortState);

        buf.writeUtf(circuitId);
        buf.writeVarIntArray(portNumbers);
    }
}