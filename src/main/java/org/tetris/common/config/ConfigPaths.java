package org.tetris.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ConfigPaths {
    private static final Map<Class<?>, String> configPaths = new HashMap<>();

    static {
        try {
            // Створення ObjectMapper для зчитування JSON
            ObjectMapper objectMapper = new ObjectMapper();
            // Зчитування шляхів з файлу paths.json у тимчасову мапу
            Map<String, String> pathsFromFile = objectMapper.readValue(new File("configs/paths.json"), Map.class);

            // Перетворення ключів із строк на класи
            for (Map.Entry<String, String> entry : pathsFromFile.entrySet()) {
                try {
                    // Завантажуємо клас за його назвою
                    Class<?> configClass = Class.forName(entry.getKey());
                    configPaths.put(configClass, entry.getValue());
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                    System.err.println("Не знайдено клас для конфігурації: " + entry.getKey());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Не вдалося завантажити шляхи до конфігураційних файлів");
        }
    }

    // Метод для отримання шляху за класом конфігурації
    public static String getConfigPath(Class<?> configClass) {
        return configPaths.get(configClass);
    }
}