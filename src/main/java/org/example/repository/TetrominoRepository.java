package org.example.repository;

import org.example.enums.Position;
import org.example.enums.Type;
import org.example.model.Tetromino;

import java.awt.Color;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TetrominoRepository {
    private static final Map<Type, Map<Position, int[][]>> ALL_TETROMINOES;
    static {
        int[][][][] coordinatesOfBlocks = {
                { // I
                        {{0, 0}, {0, -1}, {0, -2}, {0, -3}}, // UP
                        {{0, 0}, {1, 0}, {2, 0}, {3, 0}} // LEFT
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

    public Map<Type, Map<Position, int[][]>> getAll() {
        return ALL_TETROMINOES;
    }

    public int countPositions(Type type) {
        return ALL_TETROMINOES.get(type).size();
    }

    public Map<Position, List<Tetromino.Block>> get(Type type) {
        return switch(type) {
            case I -> create(ALL_TETROMINOES.get(type), Color.CYAN);
            case J -> create(ALL_TETROMINOES.get(type), Color.BLUE);
            case L -> create(ALL_TETROMINOES.get(type), Color.ORANGE);
            case O -> create(ALL_TETROMINOES.get(type), Color.YELLOW);
            case S -> create(ALL_TETROMINOES.get(type), Color.GREEN);
            case T -> create(ALL_TETROMINOES.get(type), Color.MAGENTA);
            case Z -> create(ALL_TETROMINOES.get(type), Color.RED);
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
