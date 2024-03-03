package org.example.service;

import org.example.enums.Position;
import org.example.enums.Type;
import org.example.model.Board;
import org.example.model.Tetromino;
import org.example.repository.TetrominoRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Objects;

public class TetrominoServiceTest {
    private Board board;
    private TetrominoRepository tetrominoRepository;
    private TetrominoService tetrominoService;
    private Tetromino.Block[][] allBlocks;

    @BeforeEach
    public void initializeFields() {
        int scale = 32;
        int width = 17;
        int height = 22;
        board = new Board(width, height, scale, null, null);
        tetrominoRepository = new TetrominoRepository();
        tetrominoService = new TetrominoService(board);
        allBlocks = new Tetromino.Block[height][width];
    }

    @AfterEach
    public void clearFields() {
        board = null;
        tetrominoRepository = null;
        tetrominoService = null;
        allBlocks = null;
    }

    /**
     * Тест 1.1: робота spin() біля кордону.
     * Умови: фігурка знаходиться біля кордону, для неї є місце для маневру.
     * Очікується: spin() має змінити позицію фігурки на наступну.
     */
    @Test
    public void spin_NearBorder_ChangeOfPositionToNextOne() {
        // Вмикає логування
        final boolean logs = false;
        // Початкові координати для фігурки
        int x = board.getWidth() - 2;
        int y = 4;

        // Перевірка кожного типу фігурки
        for (Type type : Type.values()) {
            // Максимальна можлива позиція поточного типу фігурки
            int maxPositionOfType = tetrominoRepository.countPositions(type) - 1;

            // Перевірка кожного положення фігурки
            for (Position position : Position.values()) {
                // Якщо поточний тип фігурки не підтримує поточне положення, то переходить до наступного типу фігурки
                if (position.ordinal() > maxPositionOfType) {
                    break;
                }

                // Створення фігурки
                Tetromino tetromino = tetrominoService.get(type, position, x, y);

                // Перевіряє чи фігурка має валідну X координату та зменшує її у разі помилки
                for (int newX = x - 1; !isLocationValid(tetromino, allBlocks); newX--) {
                    // Якщо координата X менша за 0, то кидає виключення, інакше створює фігурку
                    if (newX < 0) {
                        throw new RuntimeException("Невалідні координати для фігурки");
                    } else {
                        tetromino = tetrominoService.get(type, position, newX, y);
                    }
                }

                // Визначається очікуване наступне положення фігурки
                Position nextPosition;
                if (position.ordinal() >= maxPositionOfType) {
                    nextPosition = Position.UP;
                } else {
                    nextPosition = Position.values()[position.ordinal() + 1];
                }

                // Виклик метода, що тестується
                tetrominoService.spin(tetromino, allBlocks);

                // Логування результатів
                if (logs) {
                    System.out.printf("%s: %s -> %s%n", type, position, tetromino.getPosition());
                }

                // Зрівняння положень до та після виклику метода, що тестується
                Assertions.assertEquals(nextPosition, tetromino.getPosition());
            }
        }
    }

    /**
     * Тест 1.2: робота spin() біля кордону.
     * Умови: фігурка знаходиться біля кордону, для неї є місце для маневру.
     * Очікується: фігурка має відступати місце від кордону, якщо він їй заважає.
     */
    @Test
    public void spin_NearBorder_RetreatPlaceFromBorder() {
        // Вмикає логування
        final boolean logs = false;
        // Максимальний індекс масиву ігрового поля
        int width = board.getWidth() - 2;
        // Початкові координати для фігурки
        int x = width;
        int y = 4;

        // Перевірка кожного типу фігурки
        for (Type type : Type.values()) {
            // Максимальна можлива позиція поточного типу фігурки
            int maxPositionOfType = tetrominoRepository.countPositions(type) - 1;

            // Перевірка кожного положення фігурки
            for (Position position : Position.values()) {
                // Якщо поточний тип фігурки не підтримує поточне положення, то переходить до наступного типу фігурки
                if (position.ordinal() > maxPositionOfType) {
                    break;
                }

                // Створення фігурки
                Tetromino tetromino = tetrominoService.get(type, position, x, y);

                // Перевіряє чи фігурка має валідну X координату та зменшує її у разі помилки
                for (int newX = x - 1; !isLocationValid(tetromino, allBlocks); newX--) {
                    // Якщо координата X менша за 0, то кидає виключення, інакше створює фігурку
                    if (newX < 0) {
                        throw new RuntimeException("Невалідні координати для фігурки");
                    } else {
                        tetromino = tetrominoService.get(type, position, newX, y);
                    }
                }

                // Виклик метода, що тестується
                tetrominoService.spin(tetromino, allBlocks);

                // Знаходження максимальної координати по осі X серед блоків наступного положення
                int maxXCoordinateOfTetrominoBlock = tetromino.getBlocks().stream()
                        .mapToInt(Tetromino.Block::getX)
                        .max()
                        .orElse(0);
                // Визначення кількості кроків, на яку фігура виходить за межі поля
                int stepBack = Math.max(maxXCoordinateOfTetrominoBlock - width, 0);

                // Логування результатів
                if (logs) {
                    System.out.printf("%s: %s - вихід на %d%n", type, position, stepBack);
                }

                // Перевірка на скільки фігурка вилазить за лінію поля після виклику метода, що тестується
                Assertions.assertEquals(stepBack, 0);
            }
        }
    }

    /**
     * Тест 1.3: робота spin() між блоками та кордоном поля.
     * Умови: фігурка знаходиться між блоками та кордоном поля, місце для маневру ВІДСУТНЄ.
     * Очікується: spin() НЕ має вилазити на інші блоки.
     */
    @Test
    public void spin_BetweenBlocksAndBorder_DoNotReact() {
        // Вмикає логування
        final boolean logs = false;
        // Встановлення висоти блоків на полі
        int startY = 1;
        int endY = board.getHeight() - 2;
        // Встановлення ширини блоків на полі
        int startX = 1;
        int endX = board.getWidth() - 2;

        // Початкові координати для фігурки
        int x = 7;
        int y = 11;

        // Перевірка кожного типу фігурки
        for (Type type : Type.values()) {
            // Максимальна можлива позиція поточного типу фігурки
            int maxPositionOfType = tetrominoRepository.countPositions(type) - 1;

            // Перевірка кожного положення фігурки
            for (Position position : Position.values()) {
                // Якщо поточний тип фігурки не підтримує поточне положення, то переходить до наступного типу фігурки
                if (position.ordinal() > maxPositionOfType) {
                    break;
                }

                // Створення фігурки
                Tetromino tetromino = tetrominoService.get(type, position, x, y);

                // Генерація блоків на полі
                fillBlocks(startY, endY, startX, endX, allBlocks);

                // Зачищує місце на полі для фігурки
                tetromino.getBlocks().forEach(block -> {
                    if (Objects.nonNull(allBlocks[block.getY()][block.getX()])) {
                        allBlocks[block.getY()][block.getX()] = null;
                    }
                });

                // Виклик метода, що тестується
                tetrominoService.spin(tetromino, allBlocks);

                updateBoard(tetromino, allBlocks);

                // Логування результатів
                if (logs) {
                    System.out.printf("%s: %s -> %s%n", type, position, tetromino.getPosition());
                }

                // Зрівняння положень до та після виклику метода, що тестується
                Assertions.assertEquals(position, tetromino.getPosition());
            }
        }
    }


























    private boolean isLocationValid(Tetromino tetromino, Tetromino.Block[][] allBlocks) {
        for (Tetromino.Block block : tetromino.getBlocks()) {
            boolean isCoordinatesSmall = block.getX() > 0 && block.getY() > 0;
            boolean isCoordinatesBig = block.getX() < allBlocks[block.getX()].length-1 && block.getY() < allBlocks.length-1;
            if (isCoordinatesSmall && isCoordinatesBig) {
                if (Objects.nonNull(allBlocks[block.getY()][block.getX()])) {
                    return false;
                }
            } else {
                return false;
            }
        }

        return true;
    }

    private void fillBlocks(int startY, int endY, int startX, int endX, Tetromino.Block[][] allBlocks) {
        for (int y = startY; y <= endY; y++) {
            for (int x = startX; x <= endX; x++) {
                if (y != 0 && x != 0 && y != allBlocks.length-1 && x != allBlocks[x].length-1) {
                    allBlocks[y][x] = new Tetromino.Block(x, y, null);
                }
            }
        }
    }

    private void updateBoard(Tetromino tetromino, Tetromino.Block[][] allBlocks) {
        tetromino.getBlocks().forEach(block -> {
            allBlocks[block.getY()][block.getX()] = block;
        });

        drawBoard(tetromino, allBlocks);

        tetromino.getBlocks().forEach(block -> {
            allBlocks[block.getY()][block.getX()] = null;
        });
    }

    private void drawBoard(Tetromino tetromino, Tetromino.Block[][] allBlocks) {
        System.out.print("     ");
        for (int i = 0; i < allBlocks[0].length; i++) {
            System.out.print(String.format("%02d", i) + "  ");
        }
        System.out.println();
        for (int y = 0; y < allBlocks.length; y++) {
            System.out.print(String.format("%02d", y) + ": ");
            for (int x = 0; x < allBlocks[y].length; x++) {
                Tetromino.Block block = allBlocks[y][x];


                if (y == 0 || x == 0 || y == allBlocks.length-1 || x == allBlocks[x].length-1) {
                    System.out.print("[=] ");
                } else {
                    if (tetromino.getBlocks().contains(block)) {
                        System.out.print("[O] ");
                    } else if (Objects.nonNull(block)) {
                        System.out.print("[X] ");
                    } else {
                        System.out.print("[ ] ");
                    }
                }

//                if (tetromino.getBlocks().contains(block)) {
//                    System.out.print("[O] ");
//                } else if (Objects.nonNull(block)) {
//                    System.out.print("[X] ");
//                } else {
//                    System.out.print("[ ] ");
//                }
            }
            System.out.println();
        }
        System.out.println();
    }
}