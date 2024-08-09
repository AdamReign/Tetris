package org.tetris.gameplay.infopanel.impl;

import javafx.scene.image.Image;
import org.tetris.gameplay.infopanel.InfoPanel;
import org.tetris.gameplay.infopanel.InfoPanelService;
import org.tetris.gameplay.tetromino.enums.Type;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class InfoPanelServiceImpl implements InfoPanelService {
    private static InfoPanelServiceImpl instance;
    private volatile InfoPanel infoPanel;
    private static final Map<Type, String> ALL_IMAGES_FOR_MENU;

    static {
        String path = "/public/image/gameplay/info_panel/%s.png";
        String[] paths = {
                String.format(path, "I"),
                String.format(path, "J"),
                String.format(path, "L"),
                String.format(path, "O"),
                String.format(path, "S"),
                String.format(path, "T"),
                String.format(path, "Z")
        };
        ALL_IMAGES_FOR_MENU = Arrays.stream(Type.values())
                .collect(Collectors.toMap(
                        key -> key,
                        value -> paths[value.ordinal()]
                ));
    }

    private InfoPanelServiceImpl(InfoPanel infoPanel) {
        this.infoPanel = infoPanel;
    }

    public static InfoPanelServiceImpl createInstance(InfoPanel infoPanel) {
        if (Objects.isNull(instance)) {
            instance = new InfoPanelServiceImpl(infoPanel);
            return instance;
        } else {
            throw new IllegalStateException("InfoPanelServiceImpl has already been created");
        }
    }

    public static InfoPanelServiceImpl getInstance() {
        if (Objects.isNull(instance)) {
            throw new IllegalStateException("InfoPanelServiceImpl has not been created");
        }
        return instance;
    }

    public static boolean isInstanceCreated() {
        return instance != null;
    }

    @Override
    public void setImageNextTetromino(Type type) {
        Image imageNextTetromino = getImageForPanel(type);
        infoPanel.setImageNextTetromino(imageNextTetromino);
    }

    private Image getImageForPanel(Type type) {
        String path = ALL_IMAGES_FOR_MENU.get(type);
        return new Image(path);
    }
}