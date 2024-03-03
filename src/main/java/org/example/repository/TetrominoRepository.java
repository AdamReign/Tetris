package org.example.repository;

import org.example.Tetris;
import org.example.enums.Position;
import org.example.enums.Type;
import org.example.model.Tetromino;

import javax.swing.*;
import java.awt.Color;
import java.util.*;
import java.util.stream.Collectors;

public class TetrominoRepository {
    private static final Map<Type, Map<Position, int[][]>> ALL_TETROMINOES;
    private static final Map<Type, String> ALL_IMAGES_FOR_MENU;
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

        String path = "/icon_for_menu/%s.png";
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

    public ImageIcon getImageForMenu(Type type) {
        String path = ALL_IMAGES_FOR_MENU.get(type);
        return new ImageIcon(Objects.requireNonNull(Tetris.class.getResource(path)));
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
