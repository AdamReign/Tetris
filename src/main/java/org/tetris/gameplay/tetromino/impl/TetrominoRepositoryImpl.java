package org.tetris.gameplay.tetromino.impl;

import javafx.scene.paint.Color;
import org.tetris.gameplay.tetromino.Tetromino;
import org.tetris.gameplay.tetromino.TetrominoRepository;
import org.tetris.gameplay.tetromino.enums.Position;
import org.tetris.gameplay.tetromino.enums.Type;
import org.tetris.gameplay.tetromino.util.ColorBlock;

import java.util.*;
import java.util.stream.Collectors;

public class TetrominoRepositoryImpl implements TetrominoRepository {
    private static TetrominoRepositoryImpl instance;
    private static final Map<Type, Map<Position, int[][]>> ALL_TETROMINOES;
    static {
        int[][][][] coordinatesOfBlocks = {
                { // I
                        {{0, 0}, {0, -1}, {0, -2}, {0, -3}}, // UP
                        {{0, 0}, {1, 0}, {2, 0}, {3, 0}} // LEFT
//                        {{0, 0}, {1, 0}, {2, 0}, {3, 0}, {4, 0}, {5, 0}, {6, 0}, {7, 0}, {8, 0}, {9, 0}, {10, 0}, {11, 0}, {12, 0}, {13, 0}} // LEFT
                },
                { // J
                        {{0, 0}, {1, 0}, {1, -1}, {1, -2}}, // UP
                        {{0, 0}, {1, 0}, {2, 0}, {0, -1}}, // LEFT
                        {{0, 0}, {0, -1}, {0, -2}, {1, -2}}, // DOWN
                        {{2, 0}, {0, -1}, {1, -1}, {2, -1}} // RIGHT
                },
                { // L
                        {{0, 0}, {0, -1}, {0, -2}, {1, 0}}, // UP
                        {{0, 0}, {0, -1}, {1, -1}, {2, -1}}, // LEFT
                        {{1, 0}, {1, -1}, {1, -2}, {0, -2}}, // DOWN
                        {{0, 0}, {1, 0}, {2, 0}, {2, -1}} // RIGHT
                },
                { // O
                        {{0, 0}, {1, 0}, {0, -1}, {1, -1}} // UP
                },
                { // S
                        {{0, 0}, {1, 0}, {1, -1}, {2, -1}}, // UP
                        {{1, 0}, {1, -1}, {0, -1}, {0, -2}} // LEFT
                },
                { // T
                        {{0, 0}, {1, 0}, {2, 0}, {1, -1}}, // UP
                        {{0, 0}, {0, -1}, {0, -2}, {1, -1}}, // LEFT
                        {{0, -1}, {1, -1}, {2, -1}, {1, 0}}, // DOWN
                        {{1, 0}, {1, -1}, {1, -2}, {0, -1}} // RIGHT
                },
                { // Z
                        {{1, 0}, {2, 0}, {0, -1}, {1, -1}}, // UP
                        {{0, 0}, {0, -1}, {1, -1}, {1, -2}} // LEFT
                }





//                { // I
//                        {{3, 3}, {3, 2}, {3, 1}, {3, 0}}, // UP
//                        {{3, 3}, {4, 3}, {5, 3}, {6, 3}} // LEFT
//                },
//                { // J
//                        {{3, 3}, {4, 3}, {4, 2}, {4, 1}}, // UP
//                        {{3, 3}, {4, 3}, {5, 3}, {3, 2}}, // LEFT
//                        {{3, 3}, {3, 2}, {3, 1}, {4, 1}}, // DOWN
//                        {{5, 3}, {3, 2}, {4, 2}, {5, 2}} // RIGHT
//                },
//                { // L
//                        {{3, 3}, {3, 2}, {3, 1}, {4, 3}}, // UP
//                        {{3, 3}, {3, 2}, {4, 2}, {5, 2}}, // LEFT
//                        {{4, 3}, {4, 2}, {4, 1}, {3, 1}}, // DOWN
//                        {{3, 3}, {4, 3}, {5, 3}, {5, 2}} // RIGHT
//                },
//                { // O
//                        {{3, 3}, {4, 3}, {3, 2}, {4, 2}} // UP
//                },
//                { // S
//                        {{3, 3}, {4, 3}, {4, 2}, {5, 2}}, // UP
//                        {{4, 3}, {4, 2}, {3, 2}, {3, 1}} // LEFT
//                },
//                { // T
//                        {{3, 3}, {4, 3}, {5, 3}, {4, 2}}, // UP
//                        {{3, 3}, {3, 2}, {3, 1}, {4, 2}}, // LEFT
//                        {{3, 2}, {4, 2}, {5, 2}, {4, 3}}, // DOWN
//                        {{4, 3}, {4, 2}, {4, 1}, {3, 2}} // RIGHT
//                },
//                { // Z
//                        {{4, 3}, {5, 3}, {3, 2}, {4, 2}}, // UP
//                        {{3, 3}, {3, 2}, {4, 2}, {4, 1}} // LEFT
//                }
        };
        ALL_TETROMINOES = Arrays.stream(Type.values(), 0, coordinatesOfBlocks.length)
                .collect(Collectors.toMap(
                        type -> type,
                        type -> Arrays.stream(Position.values(), 0, coordinatesOfBlocks[type.ordinal()].length)
                                .collect(Collectors.toMap(
                                        pos -> pos,
                                        pos -> coordinatesOfBlocks[type.ordinal()][pos.ordinal()]
                                ))
                ));
    }

    private TetrominoRepositoryImpl() {

    }

    public static TetrominoRepositoryImpl createInstance() {
        if (Objects.isNull(instance)) {
            instance = new TetrominoRepositoryImpl();
            return instance;
        } else {
            throw new IllegalStateException("TetrominoRepositoryImpl has already been created");
        }
    }

    public static TetrominoRepositoryImpl getInstance() {
        if (Objects.isNull(instance)) {
            throw new IllegalStateException("TetrominoRepositoryImpl has not been created");
        }
        return instance;
    }

    public static boolean isInstanceCreated() {
        return instance != null;
    }

    @Override
    public int countPositions(Type type) {
        return ALL_TETROMINOES.get(type).size();
    }

    @Override
    public Map<Position, List<Tetromino.Block>> get(Type type) {
        return switch(type) {
            case I -> create(ALL_TETROMINOES.get(type), ColorBlock.CYAN);
            case J -> create(ALL_TETROMINOES.get(type), ColorBlock.BLUE);
            case L -> create(ALL_TETROMINOES.get(type), ColorBlock.ORANGE);
            case O -> create(ALL_TETROMINOES.get(type), ColorBlock.YELLOW);
            case S -> create(ALL_TETROMINOES.get(type), ColorBlock.GREEN);
            case T -> create(ALL_TETROMINOES.get(type), ColorBlock.MAGENTA);
            case Z -> create(ALL_TETROMINOES.get(type), ColorBlock.RED);
        };
    }

    private Map<Position, List<Tetromino.Block>> create(Map<Position, int[][]> positions, Color color) {
        return positions.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> Arrays.stream(entry.getValue())
                                .map(array -> new Tetromino.Block(array[0], array[1], color))
                                .collect(Collectors.toCollection(ArrayList::new))
                ));
    }
}
