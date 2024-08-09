package org.tetris.gameplay.gamefield.impl;

import org.tetris.gameplay.gamefield.GameField;
import org.tetris.gameplay.gamefield.GameFieldService;
import org.tetris.gameplay.tetromino.Tetromino;
import org.tetris.gameplay.tetromino.TetrominoService;
import org.tetris.gameplay.tetromino.util.ColorBlock;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class GameFieldServiceImpl implements GameFieldService {
    private static GameFieldServiceImpl instance;
    private static GameField gameField;
    private final TetrominoService tetrominoService;

    private GameFieldServiceImpl(GameField gameField,
                                 TetrominoService tetrominoService) {
        this.gameField = gameField;
        this.tetrominoService = tetrominoService;
    }

    public static GameFieldServiceImpl createInstance(GameField gameField,
                                                      TetrominoService tetrominoService) {
        if (Objects.isNull(instance)) {
            instance = new GameFieldServiceImpl(
                    gameField,
                    tetrominoService
            );
            return instance;
        } else {
            throw new IllegalStateException("GameFieldServiceImpl has already been created");
        }
    }

    public static GameFieldServiceImpl getInstance() {
        if (Objects.isNull(instance)) {
            throw new IllegalStateException("GameFieldServiceImpl has not been created");
        }
        return instance;
    }

    public static boolean isInstanceCreated() {
        return instance != null;
    }

    @Override
    public void initialize() {
        gameField.setTetromino(createRandomTetromino());
        gameField.setNextTetromino(createRandomTetromino());
    }

    @Override
    public void nextTetromino() {
        gameField.setTetromino(gameField.getNextTetromino());
        gameField.setNextTetromino(createRandomTetromino());
    }

    @Override
    public void addBlocks(List<Tetromino.Block> blocks) {
        Tetromino.Block[][] grid = gameField.getGrid();
        blocks.forEach(newBlock -> {
            // Координати, які потрібно перевірити
            int x = newBlock.getX();
            int y = newBlock.getY();

            // Перевірка координат на від'ємне число
            boolean isCoordinatesNotSmall = x >= 0 && y >= 0;
            // Перевірка координат на завелике число
            boolean isCoordinatesNotBig = x < grid[0].length && y < grid.length;

            // Перевірка чи координати блока знаходяться в межах розміру поля
            if (isCoordinatesNotSmall && isCoordinatesNotBig) {
                grid[newBlock.getY()][newBlock.getX()] = newBlock.copyWithNewColor(ColorBlock.LIGHTGRAY);
            }
        });

        gameField.setGrid(grid);
    }

    @Override
    public int deleteCompleteLines() {
        int score = 0;
        // Змінює колір блокам на полі, якщо вони склали лінію, позначає їх як видаленими
        boolean areThereReadyLines = markCompletedLines();
        if (areThereReadyLines) {
            try {
                TimeUnit.MILLISECONDS.sleep(20);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            // Видаляє блоки з поля, якщо вони позначені видаленими
            score = removeDeletedBlocksAndShiftDownward();
        }
        return score;
    }

    /**
     * Перевіряє блоки в grid на створення заповненої лінії.
     * Якщо утворилась лінія, то помічає всі блоки лінії як видаленими
     * та змінює їх колір на жовтий
     */
    private boolean markCompletedLines() {
        boolean areThereReadyLines = false;
        int offset = 2;
        int width = gameField.getWidth() - offset;

        Tetromino.Block[][] grid = gameField.getGrid();

        for (int i = 0; i < grid.length; i++) {
            // Отримує блоки поточної лінії
            List<Tetromino.Block> currentLineBlocks = Arrays.stream(grid[i])
                    .filter(Objects::nonNull)
                    .toList();

            // Перевірка чи лінія повна
            if (currentLineBlocks.size() >= width) {
                areThereReadyLines = true;
                // Помічає блоки заповненої лінії як видалені, та змінює їх колір на жовтий
                currentLineBlocks.forEach(block -> {
                    block.delete();
                    grid[block.getY()][block.getX()] = block.copyWithNewColor(ColorBlock.YELLOW);
                });
            }
        }

        // Оновлює масив блоків grid, якщо хоч одна лінія була заповнена
        if (areThereReadyLines) {
            gameField.setGrid(grid);
        }

        return areThereReadyLines;
    }

    /**
     * Прибирає з grid всі видалені блоки та рухає верхні блоки вниз
     */
    private int removeDeletedBlocksAndShiftDownward() {
        int offset = 2;
        int width = gameField.getWidth() - offset;
        Tetromino.Block[][] grid = gameField.getGrid();
        int score = 0;

        // Перевіряє кожну лінію на знаходження заповнених ліній
        for (int numberChekingLine = 0; numberChekingLine < grid.length; numberChekingLine++) {
            Tetromino.Block[] currentLine = grid[numberChekingLine];
            long countBlocksOfCurrentLine = Arrays.stream(currentLine)
                    .filter(Objects::nonNull)
                    .count();

            // Перевірка чи поточна лінія заповнена
            int maxCountBlocksInLine = width;
            if (countBlocksOfCurrentLine >= maxCountBlocksInLine) {

                // Робить перебір всіх ліній над видаленою лінією
                for (int numberSomeLineAboveChekingLine = numberChekingLine - 1;
                     numberSomeLineAboveChekingLine >= 0; numberSomeLineAboveChekingLine--) {

                    // Опускає кожен блок у всіх лініях над видаленою лінією
                    Tetromino.Block[] someLineAboveChekingLine = grid[numberSomeLineAboveChekingLine];
                    for (int x = 1; x <= maxCountBlocksInLine; x++) {
                        Tetromino.Block block = someLineAboveChekingLine[x];
                        if (Objects.nonNull(block)) {
                            someLineAboveChekingLine[x] = block.lower();
                        }
                    }

                    // Опускає чергову лінію вниз
                    grid[numberSomeLineAboveChekingLine + 1] = Arrays.copyOf(
                            someLineAboveChekingLine,
                            someLineAboveChekingLine.length
                    );
                    Arrays.fill(someLineAboveChekingLine, null);
                }

                // Збільшення балів
                score = score + (10 * (width - 2));

                // Зменшуємо номер лінії, щоб вона пройшла перевірку повторно
                --numberChekingLine;
            }
        }

        gameField.setGrid(grid);
        return score;
    }

    private Tetromino createRandomTetromino() {
        int coordinatX = gameField.getWidth() / 2;
        int coordinatY = 0;
        return tetrominoService.createRandom(coordinatX, coordinatY);
    }
}