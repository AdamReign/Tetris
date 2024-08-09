package org.tetris.common.manager;

import java.util.HashMap;
import java.util.Map;

public class AppContainer {
    private static final Map<Class<?>, Object> registry = new HashMap<>();

    public static <T> void register(Class<T> clazz, T object) {
        registry.put(clazz, object);
    }

    public static <T> T resolve(Class<T> clazz) {
        return clazz.cast(registry.get(clazz));
    }
}