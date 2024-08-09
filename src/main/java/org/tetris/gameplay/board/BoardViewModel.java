package org.tetris.gameplay.board;

import javafx.scene.Scene;
import org.tetris.gameplay.tetromino.Tetromino;

import java.util.List;

public interface BoardViewModel {
    void start();
    void stop();
    BoardDTO getBoardData();
    void closeGame();
    void pause();
    void reset();
    void gameOver();
    void screenshot(Scene view);
    void increaseScore(int count);
    void increaseSpeed();
    void addTetrominoBlocksOnGrid(List<Tetromino.Block> blocks);
    void nextTetromino();

    /**
     * Видаляє заповнені лінії
     */
    void deleteCompleteLines();
}