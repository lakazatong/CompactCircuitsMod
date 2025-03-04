package net.lakazatong.compactcircuitsmod.utils;

import net.lakazatong.compactcircuitsmod.CompactCircuitsMod;

import java.util.ServiceLoader;

// credits to https://github.com/North-West-Wind/ShortCircuit

public class Services {
    public static final IRegistryHelper REGISTRY = load(IRegistryHelper.class);

    public static <T> T load(Class<T> clazz) {
        final T loadedService = ServiceLoader.load(clazz)
                .findFirst()
                .orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
        CompactCircuitsMod.LOGGER.debug("Loaded {} for service {}", loadedService, clazz);
        return loadedService;
    }
}