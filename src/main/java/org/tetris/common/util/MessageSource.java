package org.tetris.common.util;

import java.io.FileReader;
import java.io.IOException;
import java.util.Locale;
import java.util.Properties;

public class MessageSource {
    public static String getMessage(String key, Locale language) {
        Properties properties = new Properties(){{
            try {
                String resourcesPath = "src/main/resources";
                String propertiesPath = String.format("%s/config/gameplay/lang/font_%s.properties", resourcesPath, language);
                load(new FileReader(propertiesPath));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }};
        return properties.get(key).toString();
    }
}