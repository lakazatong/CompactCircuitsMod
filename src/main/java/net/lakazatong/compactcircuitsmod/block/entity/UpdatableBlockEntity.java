package net.lakazatong.compactcircuitsmod.block.entity;

import net.lakazatong.compactcircuitsmod.server.PacketHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.lang.reflect.InvocationTargetException;

public class UpdatableBlockEntity<P> extends BlockEntity {
    private final Class<P> packetClass;

    public UpdatableBlockEntity(Class<P> p, BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
        packetClass = p;
    }

    public void handleUpdatePacket(ServerLevel serverLevel, P packet) {}

    public void update() {
        try {
            PacketHandler.sendToServer(packetClass.getConstructor(this.getClass()).newInstance(this));
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
