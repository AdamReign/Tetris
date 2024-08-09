package org.tetris.gameplay.board;

import javafx.beans.property.*;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import org.tetris.gameplay.gamefield.GameField;
import org.tetris.gameplay.infopanel.InfoPanel;

import java.util.Objects;

public final class Board {
    private static Board instance;

    private final int WIDTH;
    private final int HEIGHT;
    private final int BLOCK_SIZE;
    private final Image IMAGE;
    private final Color COLOR;

    private GameField gameField;
    private InfoPanel infoPanel;

    private final BooleanProperty over;
    private final BooleanProperty pause;
    private final BooleanProperty open;

    private Board(int width, int height, int blockSize,
                  GameField gameField,
                  InfoPanel infoPanel) {
        WIDTH = width;
        HEIGHT = height;
        BLOCK_SIZE = blockSize;
        this.gameField = gameField;
        this.infoPanel = infoPanel;

        IMAGE = new Image("public/image/gameplay/background.png");
        COLOR = Color.BLACK;
        over = new SimpleBooleanProperty();
        pause = new SimpleBooleanProperty();
        open = new SimpleBooleanProperty();
    }

    public static Board createInstance(int width, int height, int blockSize,
                                       GameField gameField,
                                       InfoPanel infoPanel) {
        if (Objects.isNull(instance)) {
            instance = new Board(
                    width, height, blockSize,
                    gameField,
                    infoPanel
            );
            return instance;
        } else {
            throw new IllegalStateException("Board has already been created");
        }
    }

    public static Board getInstance() {
        if (Objects.isNull(instance)) {
            throw new IllegalStateException("Board has not been created");
        }
        return instance;
    }

    public static boolean isInstanceCreated() {
        return instance != null;
    }

    public int getWidth() {
        return WIDTH;
    }

    public int getHeight() {
        return HEIGHT;
    }

    public int getBlockSize() {
        return BLOCK_SIZE;
    }

    public Image getImage() {
        return IMAGE;
    }

    public Color getColor() {
        return COLOR;
    }

    public GameField getGameField() {
        return gameField;
    }

    public InfoPanel getInfoPanel() {
        return infoPanel;
    }

    public boolean isOver() {
        return over.get();
    }

    public boolean isPause() {
        return pause.get();
    }

    public boolean isOpen() {
        return open.get();
    }

    public void setGameField(GameField gameField) {
        this.gameField = gameField;
    }

    public void setInfoPanel(InfoPanel infoPanel) {
        this.infoPanel = infoPanel;
    }

    public synchronized void setOver(boolean over) {
        this.over.set(over);
    }

    public synchronized void setPause(boolean pause) {
        this.pause.set(pause);
    }

    public synchronized void setOpen(boolean open) {
        this.open.set(open);
    }

    public BooleanProperty overProperty() {
        return over;
    }

    public BooleanProperty pauseProperty() {
        return pause;
    }

    public BooleanProperty openProperty() {
        return open;
    }
}