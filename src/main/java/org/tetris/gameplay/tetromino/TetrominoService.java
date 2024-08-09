package org.tetris.gameplay.tetromino;

import org.tetris.gameplay.tetromino.enums.Direction;
import org.tetris.gameplay.tetromino.enums.Position;
import org.tetris.gameplay.tetromino.enums.Type;

public interface TetrominoService {
    Tetromino create(Type type, Position position, int x, int y);
    Tetromino createRandom(int x, int y);
    void rotate(Tetromino tetromino, Tetromino.Block[][] grid);
    int fall(Tetromino tetromino, Tetromino.Block[][] grid);
    boolean move(Tetromino tetromino, Direction direction, Tetromino.Block[][] grid);

    /**
     * Створює Y координату для прицілу фігурки
     */
    int calculateAimYCoordinate();
}