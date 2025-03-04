package net.lakazatong.compactcircuitsmod.circuits;

import net.lakazatong.compactcircuitsmod.block.entity.HubBlockEntity;
import net.lakazatong.compactcircuitsmod.block.entity.PortBlockEntity;
import net.lakazatong.compactcircuitsmod.block.entity.UpdatableBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.Level;

import java.util.*;

import static net.lakazatong.compactcircuitsmod.CompactCircuitsMod.CIRCUITS;

public class Circuit {
    public static final String DEFAULT_CIRCUIT_ID = "";

    public final String CIRCUIT_ID;

    public Map<Integer, BlockPos> hubBlocks;
    public Map<Integer, BlockPos> portBlocks;

    public Circuit(String circuitId) {
        CIRCUIT_ID = circuitId;

        this.hubBlocks = new HashMap<>();
        this.portBlocks = new HashMap<>();

        CIRCUITS.putIfAbsent(circuitId, this);
    }

    public Circuit(String circuitId, CompoundTag tag) {
        this(circuitId);
        load(tag);
    }

    public boolean isEmpty() {
        return hubBlocks.isEmpty() && portBlocks.isEmpty();
    }

    public static void removeIfEmpty(String circuitId) {
        Circuit circuit = CIRCUITS.get(circuitId);
        if (circuit != null && circuit.isEmpty()) CIRCUITS.remove(circuitId);
    }

    private BlockPos _addPort(PortBlockEntity be) {
        int portNumber = be.getPortNumber();

        be.setCircuitId(CIRCUIT_ID);
        if (portNumber != PortBlockEntity.DEFAULT_PORT_NUMBER) portBlocks.put(portNumber, be.getBlockPos());

        return hubBlocks.get(portNumber);
    }

    public void addPort(PortBlockEntity be) {
        Level level = be.getLevel();
        if (level == null) return;
        BlockPos pos = _addPort(be);
        if (pos != null && level.getBlockEntity(pos) instanceof HubBlockEntity hubBlockEntity) {
            be.update();
            hubBlockEntity.update();
        }
    }

    private BlockPos _removePort(PortBlockEntity be) {
        int portNumber = be.getPortNumber();

        be.resetCircuitId();
        portBlocks.remove(portNumber);

        return hubBlocks.get(portNumber);
    }

    public void removePort(PortBlockEntity be) {
        Level level = be.getLevel();
        if (level == null) return;
        BlockPos pos = _removePort(be);
        if (pos != null && level.getBlockEntity(pos) instanceof HubBlockEntity hubBlockEntity) {
            be.update();
            hubBlockEntity.update();
        }
    }

    public void updatePort(PortBlockEntity be, int newPortNumber) {
        Level level = be.getLevel();
        if (level == null) return;

        BlockPos oldHubBlockPos = _removePort(be);
        be.setPortNumber(newPortNumber);
        BlockPos hubBlockPos = _addPort(be);

        if (oldHubBlockPos != null && level.getBlockEntity(oldHubBlockPos) instanceof HubBlockEntity hubBlockEntity)
            hubBlockEntity.update();
        if (hubBlockPos != null && level.getBlockEntity(hubBlockPos) instanceof HubBlockEntity hubBlockEntity)
            hubBlockEntity.update();
        be.update();
    }

    public void addHub(HubBlockEntity be) {
        Level level = be.getLevel();
        if (level == null) return;
        int[] portNumbers = be.getPortNumbers();
        be.setCircuitId(CIRCUIT_ID);
        BlockPos pos = be.getBlockPos();

        for (int portNumber : portNumbers) {
            if (portNumber == PortBlockEntity.DEFAULT_PORT_NUMBER) continue;

            hubBlocks.put(portNumber, pos);

            BlockPos portBlockPos = portBlocks.get(portNumber);
            if (portBlockPos != null && level.getBlockEntity(portBlockPos) instanceof PortBlockEntity portBlockEntity)
                portBlockEntity.update();
        }

        be.update();
    }

    public void removeHub(HubBlockEntity be) {
        Level level = be.getLevel();
        if (level == null) return;
        int[] portNumbers = be.getPortNumbers();
        be.resetCircuitId();

        for (int portNumber : portNumbers) {
            if (portNumber == PortBlockEntity.DEFAULT_PORT_NUMBER) continue;

            hubBlocks.remove(portNumber);

            BlockPos portBlockPos = portBlocks.get(portNumber);
            if (portBlockPos != null && level.getBlockEntity(portBlockPos) instanceof PortBlockEntity portBlockEntity)
                portBlockEntity.update();
        }

        be.update();
    }

    public void updateHub(HubBlockEntity be, int[] newPortNumbers) {
        Level level = be.getLevel();
        int[] oldPortNumbers = be.getPortNumbers();
        if (level == null) return;

        BlockPos pos = be.getBlockPos();

        be.setPortNumbers(newPortNumbers);

        for (int i = 0; i < 6; i++) {
            int oldPortNumber = oldPortNumbers[i];
            if (oldPortNumber == PortBlockEntity.DEFAULT_PORT_NUMBER) continue;

            hubBlocks.remove(oldPortNumber);

            BlockPos portBlockPos = portBlocks.get(oldPortNumber);
            if (portBlockPos != null && level.getBlockEntity(portBlockPos) instanceof PortBlockEntity portBlockEntity)
                portBlockEntity.update();
        }

        for (int i = 0; i < 6; i++) {
            int newPortNumber = newPortNumbers[i];
            if (newPortNumber == PortBlockEntity.DEFAULT_PORT_NUMBER) continue;

            BlockPos oldHubBlockPos = hubBlocks.put(newPortNumber, pos);

            if (oldHubBlockPos != null && level.getBlockEntity(oldHubBlockPos) instanceof HubBlockEntity oldHubBlockEntity) {
                oldHubBlockEntity.setPortNumberAt(i, PortBlockEntity.DEFAULT_PORT_NUMBER);
                oldHubBlockEntity.update();
            }

            BlockPos portBlockPos = portBlocks.get(newPortNumber);
            if (portBlockPos != null && level.getBlockEntity(portBlockPos) instanceof PortBlockEntity portBlockEntity)
                portBlockEntity.update();
        }

        be.update();
    }

    // both migrate functions expect the given be to have its old attributes but the circuit id

    public static void migratePort(PortBlockEntity be,
        String oldCircuitId, String newCircuitId,
        int oldPortNumber, int newPortNumber) {

        if (newCircuitId.equals(oldCircuitId)) {
            if (newCircuitId.isEmpty()) {
                be.setPortNumber(newPortNumber);
                be.update();
            } else if (newPortNumber != oldPortNumber) {
                CIRCUITS.get(newCircuitId).updatePort(be, newPortNumber);
            }
        } else {
            Circuit oldCircuit = CIRCUITS.get(oldCircuitId);
            if (oldCircuit != null)
                oldCircuit.removePort(be);
            be.setPortNumber(newPortNumber);
            if (newCircuitId.isEmpty()) {
                be.update();
            } else {
                Circuit newCircuit = CIRCUITS.getOrDefault(newCircuitId, new Circuit(newCircuitId));
                newCircuit.addPort(be);
            }
        }
    }

    public static void migrateHub(HubBlockEntity be,
       String oldCircuitId, String newCircuitId,
       int[] oldPortNumbers, int[] newPortNumbers) {

        if (newCircuitId.equals(oldCircuitId)) {
            if (newCircuitId.isEmpty()) {
                be.setPortNumbers(newPortNumbers);
                be.update();
            } else if (!Arrays.equals(newPortNumbers, oldPortNumbers)) {
                CIRCUITS.get(newCircuitId).updateHub(be, newPortNumbers);
            }
        } else {
            Circuit oldCircuit = CIRCUITS.get(oldCircuitId);
            if (oldCircuit != null)
                oldCircuit.removeHub(be);
            be.setPortNumbers(newPortNumbers);
            if (newCircuitId.isEmpty()) {
                be.update();
            } else {
                Circuit newCircuit = CIRCUITS.getOrDefault(newCircuitId, new Circuit(newCircuitId));
                newCircuit.addHub(be);
            }
        }
    }

    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();

        ListTag savedPortBlocks = new ListTag();
        portBlocks.forEach((portNumber, pos) -> {
            CompoundTag pair = new CompoundTag();
            pair.putInt("key", portNumber);
            CompoundTag octoTag = new CompoundTag();
            pair.putLong("value", pos.asLong());
            savedPortBlocks.add(pair);
        });
        tag.put("portBlocks", savedPortBlocks);

        ListTag savedHubBlocks = new ListTag();
        hubBlocks.forEach((portNumber, pos) -> {
            CompoundTag pair = new CompoundTag();
            pair.putInt("key", portNumber);
            CompoundTag octoTag = new CompoundTag();
            pair.putLong("value", pos.asLong());
            savedHubBlocks.add(pair);
        });
        tag.put("hubBlocks", savedHubBlocks);

        return tag;
    }

    public void load(CompoundTag tag) {
        for (Tag tt : tag.getList("portBlocks", Tag.TAG_COMPOUND)) {
            CompoundTag t = (CompoundTag) tt;
            portBlocks.put(t.getInt("key"), BlockPos.of(t.getLong("value")));
        }
        for (Tag tt : tag.getList("hubBlocks", Tag.TAG_COMPOUND)) {
            CompoundTag t = (CompoundTag) tt;
            hubBlocks.put(t.getInt("key"), BlockPos.of(t.getLong("value")));
        }
    }

    public void clear(Level level) {
        Set<PortBlockEntity> portBlockEntities = new HashSet<>();
        for (Map.Entry<Integer, BlockPos> entry : portBlocks.entrySet()) {
            if (level.getBlockEntity(entry.getValue()) instanceof PortBlockEntity be) {
                portBlockEntities.add(be);
            }
        }
        Set<HubBlockEntity> hubBlockEntities = new HashSet<>();
        for (Map.Entry<Integer, BlockPos> entry : portBlocks.entrySet()) {
            if (level.getBlockEntity(entry.getValue()) instanceof HubBlockEntity be) {
                hubBlockEntities.add(be);
            }
        }
        portBlocks.clear();
        hubBlocks.clear();
        CIRCUITS.remove(CIRCUIT_ID);
        portBlockEntities.forEach(UpdatableBlockEntity::update);
        hubBlockEntities.forEach(UpdatableBlockEntity::update);
    }
}
