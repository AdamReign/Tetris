package org.tetris.gameplay.tetromino;

import javafx.scene.image.Image;
import org.tetris.gameplay.tetromino.enums.Position;
import org.tetris.gameplay.tetromino.enums.Type;

import java.util.List;
import java.util.Map;

public interface TetrominoRepository {
    int countPositions(Type type);
    Map<Position, List<Tetromino.Block>> get(Type type);
}