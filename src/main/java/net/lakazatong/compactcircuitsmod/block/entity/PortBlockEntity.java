package net.lakazatong.compactcircuitsmod.block.entity;

import net.lakazatong.compactcircuitsmod.block.ModBlockEntities;
import net.lakazatong.compactcircuitsmod.block.ModBlocks;
import net.lakazatong.compactcircuitsmod.block.custom.PortBlock;
import net.lakazatong.compactcircuitsmod.circuits.Circuit;
import net.lakazatong.compactcircuitsmod.server.UpdatePortPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Optional;

import static net.lakazatong.compactcircuitsmod.CompactCircuitsMod.CIRCUITS;
import static net.lakazatong.compactcircuitsmod.circuits.Circuit.DEFAULT_CIRCUIT_ID;

public class PortBlockEntity extends UpdatableBlockEntity<UpdatePortPacket> {
    // dynamic block states

    public static final boolean DEFAULT_IS_INPUT = true;
    public static final boolean DEFAULT_IS_OPEN = false;
    public static final int DEFAULT_SIDE = 6;

    // block properties

    public static final int DEFAULT_PORT_NUMBER = 0; // closed
    public static final int DEFAULT_BUS_SIZE = 1;
    public static final int DEFAULT_SIGNAL_STRENGTH = 0;

    // -----------------------------------------------------------------------------------------------------------------

    private boolean isInput;

    private String circuitId = DEFAULT_CIRCUIT_ID;
    private int portNumber = DEFAULT_PORT_NUMBER;
    private int busSize = DEFAULT_BUS_SIZE;
    private int signalStrength = DEFAULT_SIGNAL_STRENGTH;

    public PortBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(UpdatePortPacket.class, pType, pPos, pBlockState);
        if (!(pBlockState.getBlock() instanceof PortBlock)) return;
        this.isInput = pBlockState.getValue(PortBlock.IS_INPUT);
    }

    public PortBlockEntity(BlockPos pPos, BlockState pBlockState) {
        this(ModBlockEntities.PORT_BLOCK.get(), pPos, pBlockState);
    }

    @Override
    protected void saveAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.saveAdditional(pTag, pRegistries);
        pTag.putBoolean("isInput", this.isInput);
        pTag.putString("circuitId", this.circuitId);
        pTag.putInt("portNumber", this.portNumber);
        pTag.putInt("busSize", this.busSize);
        pTag.putInt("signalStrength", this.signalStrength);
    }

    @Override
    protected void loadAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.loadAdditional(pTag, pRegistries);
        this.isInput = pTag.getBoolean("isInput");
        this.circuitId = pTag.getString("circuitId");
        this.portNumber = pTag.getInt("portNumber");
        this.busSize = pTag.getInt("busSize");
        this.signalStrength = pTag.getInt("signalStrength");
    }

    @Override
    public void handleUpdatePacket(ServerLevel serverLevel, UpdatePortPacket packet) {
        isInput = packet.isInput;

        serverLevel.setBlockAndUpdate(packet.pos, serverLevel.getBlockState(packet.pos)
                .setValue(PortBlock.IS_INPUT, packet.isInput)
                .setValue(PortBlock.IS_OPEN, packet.isOpen)
                .setValue(PortBlock.SIDE, packet.side)
        );

        circuitId = packet.circuitId;
        portNumber = packet.portNumber;
        busSize = packet.busSize;

        serverLevel.updateNeighborsAt(getBlockPos(), ModBlocks.PORT_BLOCK.get());
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.Provider pRegistries) {
        CompoundTag pTag = new CompoundTag();
        saveAdditional(pTag, pRegistries);
        return pTag;
    }

    public void resetIsInput() { isInput = DEFAULT_IS_INPUT; }

    public void resetCircuitId() { circuitId = DEFAULT_CIRCUIT_ID; }
    public void resetPortNumber() { portNumber = DEFAULT_PORT_NUMBER; }
    public void resetBusSize() { busSize = DEFAULT_BUS_SIZE; }
    public void resetSignalStrength() { signalStrength = DEFAULT_SIGNAL_STRENGTH; }

    public void reset() {
        resetIsInput();

        resetCircuitId();
        resetPortNumber();
        resetBusSize();
        resetSignalStrength();
    }

    public @NotNull BlockPos getBlockPos() { return this.worldPosition; }

    public boolean getIsInput() { return this.isInput; }

    public void setIsInput(boolean v) { this.isInput = v; }

    public HubBlockEntity getHub() {
        if (level == null) return null;
        Circuit circuit = CIRCUITS.get(circuitId);
        if (circuit == null) return null;
        return Optional.ofNullable(circuit.hubBlocks.get(portNumber))
            .map(pos -> level.getBlockEntity(pos) instanceof HubBlockEntity be ? be : null)
            .orElse(null);
    }

    public boolean getIsOpen() {
        return getHub() != null;
    }

    public int getSide() {
        return Optional.ofNullable(getHub())
            .map(be -> be.getSideOf(portNumber))
            .orElse(DEFAULT_SIDE);
    }

    public String getCircuitId() { return this.circuitId; }
    public void setCircuitId(String v) { this.circuitId = v; }

    public int getPortNumber() { return this.portNumber; }
    public void setPortNumber(int v) { this.portNumber = v; }

    public int getBusSize() { return this.busSize; }
    public void setBusSize(int v) { this.busSize = v; }

    public int getSignalStrength() {
        return signalStrength;
    }

    public void updateSignalStrength(Level pLevel) {
        BlockPos pos = getBlockPos();
        signalStrength = Arrays.stream(net.minecraft.core.Direction.values())
                .map(direction -> pLevel.getSignal(pos.relative(direction), direction))
                .reduce(0, Integer::max);
    }
}
