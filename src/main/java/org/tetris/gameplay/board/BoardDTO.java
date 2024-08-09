package org.tetris.gameplay.board;

import javafx.beans.property.*;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import org.tetris.gameplay.gamefield.GameField;
import org.tetris.gameplay.infopanel.InfoPanel;

public class BoardDTO {
    private final int width;
    private final int height;
    private final int blockSize;
    private final Image image;
    private final Color color;

    private final GameField gameField;
    private final InfoPanel infoPanel;

    private final BooleanProperty over;
    private final BooleanProperty pause;
    private final BooleanProperty open;

    public BoardDTO(Board board) {
        width = board.getWidth();
        height = board.getHeight();
        blockSize = board.getBlockSize();
        image = board.getImage();
        color = board.getColor();
        gameField = board.getGameField();
        infoPanel = board.getInfoPanel();

        over = new SimpleBooleanProperty();
        pause = new SimpleBooleanProperty();
        open = new SimpleBooleanProperty();

        over.bind(board.overProperty());
        pause.bind(board.pauseProperty());
        open.bind(board.openProperty());
    }

    public int getBlockSize() {
        return blockSize;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Image getImage() {
        return image;
    }

    public Color getColor() {
        return color;
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

    public BooleanProperty overProperty() {
        return over;
    }

    public BooleanProperty pauseProperty() {
        return pause;
    }

    public BooleanProperty openProperty() {
        return open;
    }

//    @Override
//    public String toString() {
//        return "BoardDto{" +
//                "color=" + (color != null ? color.toString() : "null") +
//                ", tetromino=" + (tetromino != null ? tetromino.toString() : "null") +
//                ", nextTetromino=" + (nextTetromino != null ? nextTetromino.toString() : "null") +
//                ", status=" + status +
//                ", gameOver=" + gameOver +
//                ", pause=" + pause +
//                ", score=" + score +
//                ", speed=" + speed +
//                '}';
//    }
}