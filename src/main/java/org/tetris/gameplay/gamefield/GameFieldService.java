package org.tetris.gameplay.gamefield;

import org.tetris.gameplay.tetromino.Tetromino;

import java.util.List;

public interface GameFieldService {
    void initialize();
    void nextTetromino();
    void addBlocks(List<Tetromino.Block> blocks);
    int deleteCompleteLines();
}