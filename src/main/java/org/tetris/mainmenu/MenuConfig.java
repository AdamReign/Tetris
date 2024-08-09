package org.tetris.mainmenu;

import org.tetris.common.config.Config;

import java.util.Objects;

public class MenuConfig implements Config {
    private static MenuConfig instance;

    private MenuConfig() {

    }

    public static MenuConfig getInstance() {
        if (Objects.isNull(instance)) {
            instance = new MenuConfig();
        }
        return instance;
    }

    @Override
    public void initialize() {

    }
}