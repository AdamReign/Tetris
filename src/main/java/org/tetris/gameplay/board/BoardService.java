package org.tetris.gameplay.board;

import org.tetris.gameplay.tetromino.Tetromino;

import java.util.List;

public interface BoardService {
    void start();
    void stop();
    boolean isRunning();
    void initialize();
    void reset();
    void pause();
    void closeGame();
    void gameOver();
    void increaseScore(int count);
    void increaseFallingSpeed();
    void nextTetromino();
    void addBlocks(List<Tetromino.Block> blocks);
    void deleteCompleteLines();
    BoardDTO getBoardData();
}