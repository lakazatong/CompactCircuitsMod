package net.lakazatong.compactcircuitsmod.circuits;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CircuitsSavedData extends SavedData implements Map<String, Circuit> {
    private final Map<String, Circuit> CIRCUITS;

    public CircuitsSavedData() {
        CIRCUITS = new HashMap<>();
    }

    public static CircuitsSavedData load(CompoundTag tag, HolderLookup.Provider lookupProvider) {
        CircuitsSavedData data = new CircuitsSavedData();
        for (String circuitId : tag.getAllKeys()) {
            if (tag.get(circuitId) instanceof CompoundTag circuitTag)
                data.put(circuitId, new Circuit(circuitId, circuitTag));
        }
        return data;
    }

    @Override
    public @NotNull CompoundTag save(CompoundTag pTag, HolderLookup.@NotNull Provider pRegistries) {
        for (Circuit circuit : values()) {
            if (!circuit.isEmpty()) pTag.put(circuit.CIRCUIT_ID, circuit.save());
        }
        return pTag;
    }

    @Override
    public int size() {
        setDirty();
        return CIRCUITS.size();
    }

    @Override
    public boolean isEmpty() {
        setDirty();
        return CIRCUITS.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        setDirty();
        return CIRCUITS.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        setDirty();
        return CIRCUITS.containsValue(value);
    }

    @Override
    public Circuit get(Object key) {
        setDirty();
        return CIRCUITS.get(key);
    }

    @Override
    public @Nullable Circuit put(String key, Circuit value) {
        setDirty();
        return CIRCUITS.put(key, value);
    }

    @Override
    public Circuit remove(Object key) {
        setDirty();
        return CIRCUITS.remove(key);
    }

    @Override
    public void putAll(@NotNull Map<? extends String, ? extends Circuit> m) {
        setDirty();
        CIRCUITS.putAll(m);
    }

    @Override
    public void clear() {
        setDirty();
        CIRCUITS.clear();;
    }

    @Override
    public @NotNull Set<String> keySet() {
        setDirty();
        return CIRCUITS.keySet();
    }

    @Override
    public @NotNull Collection<Circuit> values() {
        setDirty();
        return CIRCUITS.values();
    }

    @Override
    public @NotNull Set<Entry<String, Circuit>> entrySet() {
        setDirty();
        return CIRCUITS.entrySet();
    }
}
