package net.lakazatong.compactcircuitsmod.server;

import net.lakazatong.compactcircuitsmod.block.entity.UpdatableBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.network.CustomPayloadEvent;

import java.util.Objects;

public class BaseModPacket<E extends UpdatableBlockEntity<P>, P> {
    public final BlockPos pos;

    public BaseModPacket(BlockPos pos) {
        this.pos = pos;
    }

    @SuppressWarnings("unchecked")
    private void handle(ServerPlayer player) {
        if (player != null && player.level() instanceof ServerLevel serverLevel) {
            ((E) Objects.requireNonNull(serverLevel.getBlockEntity(pos))).handleUpdatePacket(serverLevel, (P) this);
        }
    }

    public void handle(CustomPayloadEvent.Context context) {
        handle(context.getSender());
    }

    public BlockPos getPos() {
        return this.pos;
    }
}
