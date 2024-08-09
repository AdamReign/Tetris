package org.tetris.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ConfigManager {
    private static final String CONFIG_DIR = "config/";
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final Map<String, Object> configCache = new HashMap<>();

    // Метод для загального завантаження конфігурації
    public static <T> T loadConfig(String fileName, Class<T> configClass) throws IOException {
        // Перевірка кешу конфігурацій
        if (configCache.containsKey(fileName)) {
            return configClass.cast(configCache.get(fileName));
        }

        // Завантаження конфігурації з файлу
        T config = mapper.readValue(new File(CONFIG_DIR + fileName), configClass);
        configCache.put(fileName, config);
        return config;
    }

    // Загальний метод для завантаження будь-якої конфігурації
    public static <T> T getConfig(Class<T> configClass) throws IOException {
        // Отримуємо шлях з ConfigPaths
        String path = ConfigPaths.getConfigPath(configClass);
        if (Objects.isNull(path)) {
            throw new IllegalArgumentException("Конфігурація для класу " + configClass.getName() + " не знайдена");
        }
        return loadConfig(path, configClass);
    }
}