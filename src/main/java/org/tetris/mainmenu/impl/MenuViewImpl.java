package org.tetris.mainmenu.impl;

import org.tetris.mainmenu.MenuView;

import java.util.Objects;

public class MenuViewImpl implements MenuView {
    private static MenuViewImpl instance;

    private MenuViewImpl() {

    }

    public static MenuViewImpl getInstance() {
        if (Objects.isNull(instance)) {
            instance = new MenuViewImpl();
        }
        return instance;
    }
}