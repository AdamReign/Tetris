package org.tetris.common.config;

import java.util.ArrayList;
import java.util.List;

public class ConfigInitializer {
    private final List<Config> configs = new ArrayList<>();

    public void addConfig(Config config) {
        configs.add(config);
    }

    public void initialize() {
        for (Config config : configs) {
            config.initialize();
        }
    }
}