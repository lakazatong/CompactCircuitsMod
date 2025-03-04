package net.lakazatong.compactcircuitsmod.block.entity;

import net.lakazatong.compactcircuitsmod.block.ModBlockEntities;
import net.lakazatong.compactcircuitsmod.block.ModBlocks;
import net.lakazatong.compactcircuitsmod.block.custom.HubBlock;
import net.lakazatong.compactcircuitsmod.circuits.Circuit;
import net.lakazatong.compactcircuitsmod.server.UpdateHubPacket;
import net.lakazatong.compactcircuitsmod.utils.Direction;
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
import static net.lakazatong.compactcircuitsmod.block.entity.PortBlockEntity.DEFAULT_PORT_NUMBER;
import static net.lakazatong.compactcircuitsmod.block.entity.PortBlockEntity.DEFAULT_SIGNAL_STRENGTH;
import static net.lakazatong.compactcircuitsmod.circuits.Circuit.DEFAULT_CIRCUIT_ID;

public class HubBlockEntity extends UpdatableBlockEntity<UpdateHubPacket> {
    // dynamic block states

    // 0: isInput: true, isOpen: false (input, close)
    // 1: isInput: true, isOpen: true (input, open)
    // 2: isInput: false, isOpen: false (output, close)
    // 3: isInput: false, isOpen: true (output, open)
    public static final int DEFAULT_PORT_STATE = 0;

    // block properties

    // -----------------------------------------------------------------------------------------------------------------

    private String circuitId = DEFAULT_CIRCUIT_ID;
    private int[] portNumbers = new int[6];
    private int[] signalStrengths = new int[6];

    { reset(); }

    // magic values

    public static final int FRONT = 0;
    public static final int BACK = 1;
    public static final int RIGHT = 2;
    public static final int LEFT = 3;
    public static final int UP = 4;
    public static final int DOWN = 5;

    public HubBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(UpdateHubPacket.class, pType, pPos, pBlockState);
    }

    public HubBlockEntity(BlockPos pPos, BlockState pBlockState) {
        this(ModBlockEntities.HUB_BLOCK.get(), pPos, pBlockState);
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag pTag, HolderLookup.@NotNull Provider pRegistries) {
        super.saveAdditional(pTag, pRegistries);
        pTag.putString("circuitId", circuitId);
        pTag.putIntArray("portNumbers", portNumbers);
        pTag.putIntArray("signalStrengths", signalStrengths);
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag pTag, HolderLookup.@NotNull Provider pRegistries) {
        super.loadAdditional(pTag, pRegistries);
        circuitId = pTag.getString("circuitId");
        portNumbers = pTag.getIntArray("portNumbers");
        signalStrengths = pTag.getIntArray("signalStrengths");
    }

    @Override
    public void handleUpdatePacket(ServerLevel serverLevel, UpdateHubPacket packet) {
        serverLevel.setBlockAndUpdate(packet.pos, serverLevel.getBlockState(packet.pos)
                .setValue(HubBlock.FRONT_PORT_STATE, packet.frontPortState)
                .setValue(HubBlock.BACK_PORT_STATE, packet.backPortState)
                .setValue(HubBlock.RIGHT_PORT_STATE, packet.rightPortState)
                .setValue(HubBlock.LEFT_PORT_STATE, packet.leftPortState)
                .setValue(HubBlock.UP_PORT_STATE, packet.upPortState)
                .setValue(HubBlock.DOWN_PORT_STATE, packet.downPortState)
        );

        this.circuitId = packet.circuitId;
        this.portNumbers = packet.portNumbers;

        serverLevel.updateNeighborsAt(getBlockPos(), ModBlocks.HUB_BLOCK.get());
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.Provider pRegistries) {
        CompoundTag pTag = new CompoundTag();
        saveAdditional(pTag, pRegistries);
        return pTag;
    }

    public void resetCircuitId() { circuitId = DEFAULT_CIRCUIT_ID; }
    public void resetPortNumbers() { Arrays.fill(portNumbers, DEFAULT_PORT_NUMBER); }
    public void resetSignalStrengths() { Arrays.fill(signalStrengths, DEFAULT_SIGNAL_STRENGTH); }
    public void reset() {

        resetCircuitId();
        resetPortNumbers();
        resetSignalStrengths();
    }

    public String getCircuitId() { return circuitId; }
    public void setCircuitId(String v) { circuitId = v; }

    public int[] getPortNumbers() { return portNumbers; }
    public void setPortNumbers(int[] v) { portNumbers = v; }

    public int[] getSignalStrengths() { return signalStrengths; }
    public void setSignalStrengths(int[] v) { signalStrengths = v; }

    public PortBlockEntity getPort(int side) {
        if (level == null) return null;
        Circuit circuit = CIRCUITS.get(circuitId);
        if (circuit == null) return null;
        int portNumber = portNumbers[side];
        return Optional.ofNullable(circuit.portBlocks.get(portNumber))
                .map(pos -> level.getBlockEntity(pos) instanceof PortBlockEntity be ? be : null)
                .orElse(null);
    }

    public Boolean getIsInput(int side) {
        return Optional.ofNullable(getPort(side))
                .map(PortBlockEntity::getIsInput)
                .orElse(null);
    }

    public boolean getIsOpen(int side) {
        return getPort(side) != null;
    }

    public int getPortNumberAt(int side) {
        return portNumbers[side];
    }

    public void setPortNumberAt(int side, int v) {
        portNumbers[side] = v;
    }

    public int getSideOf(int portNumber) {
        for (int i = 0; i < 6; i++) {
            if (portNumbers[i] == portNumber) return i;
        }
        return PortBlockEntity.DEFAULT_SIDE;
    }

    public static int portStateFrom(boolean isInput, boolean isOpen) {
        if (isInput) {
            return isOpen ? 1 : 0;
        }
        return isOpen ? 3 : 2;
    }

    public int portStateOf(int side) {
        if (side == PortBlockEntity.DEFAULT_SIDE) return DEFAULT_PORT_STATE;
        Boolean isInput = getIsInput(side);
        return isInput == null ? DEFAULT_PORT_STATE : portStateFrom(isInput, getIsOpen(side));
    }

    public int getSignalStrength(int portNumber) {
        for (int i = 0; i < 6; i++) {
            if (portNumbers[i] == portNumber)
                return signalStrengths[i];
        }
        return 0;
    }

    public int getSignalStrengthAt(int side) {
        return signalStrengths[side];
    }

    public void updateSignalStrengths(Level pLevel) {
        BlockPos pos = getBlockPos();
        BlockState state = pLevel.getBlockState(pos);
        for (int i = 0; i < 6; i++) {
            net.minecraft.core.Direction direction = Direction.fromSide(i).toAbsolute(state.getValue(HubBlock.FACING)).toMinecraftDirection();
            BlockPos relativePos = pos.relative(direction);
//            int signal = pLevel.getSignal(relativePos, direction.getOpposite());
            int directSignal = pLevel.getSignal(relativePos, direction.getOpposite());
            int indirectSignal = pLevel.getBestNeighborSignal(relativePos);
            signalStrengths[i] = Math.max(directSignal, indirectSignal);
        }
    }
}
