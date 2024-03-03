package org.example.service;

import org.example.model.Board;
import org.example.model.Tetromino;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class BoardService {
    private final Board BOARD;
    private final TetrominoService TETROMINO_SERVICE;

    public BoardService(Board board, TetrominoService tetrominoService) {
        BOARD = board;
        TETROMINO_SERVICE = tetrominoService;
    }

    public void initialize() {
        BOARD.setTetromino(createRandomTetromino());
        BOARD.setNextTetromino(createRandomTetromino());
        BOARD.setGameOver(false);
        BOARD.setPause(false);
    }

    public void reset() {
        // Очищує поле і повторно ініціалізує
        initialize();

        Tetromino.Block[][] emptyAllBlocks = new Tetromino.Block[BOARD.getHeight()][BOARD.getWidth()];

        BOARD.setAllBlocks(emptyAllBlocks);

        BOARD.setScore(0);
    }

    public void pause() {
        boolean isGameOver = BOARD.isGameOver();
        boolean isPause = BOARD.isPause();

        BOARD.setPause(!isGameOver && !isPause);
    }

    public void closeGame() {
        BOARD.setClose(true);
    }

    public void increaseScore(int count) {
        int currentScore = BOARD.getScore();
        if (count < 0) {
            throw new RuntimeException("Не можна збільшити бали на від'ємне число");
        }
        BOARD.setScore(currentScore + count);
    }

    public void increaseSpeed() {
        int currentSpeed = 1000 - ((BOARD.getScore() / 2500) * 100);
        BOARD.setSpeed(currentSpeed > 200 ? currentSpeed : 300);
    }

    /**
     * Перевіряє блоки в allBlocks на створення заповненої лінії.
     * Якщо утворилась лінія, то помічає всі блоки лінії як видаленими
     * та змінює їх колір на жовтий
     */
    public boolean markCompletedLines() {
        boolean areThereReadyLines = false;
        int offset = 2;
        int width = BOARD.getWidth() - offset;
        Tetromino.Block[][] allBlocks = BOARD.getAllBlocks();

        for (int i = 0; i < allBlocks.length; i++) {
                // Отримує блоки поточної лінії
                List<Tetromino.Block> currentLineBlocks = Arrays.stream(allBlocks[i])
                        .filter(Objects::nonNull)
                        .toList();

                // Перевірка чи лінія повна
                if (currentLineBlocks.size() >= width) {
                    areThereReadyLines = true;
                    // Помічає блоки заповненої лінії як видалені, та змінює їх колір на жовтий
                    currentLineBlocks.forEach(block -> {
                        block.delete();
                        allBlocks[block.getY()][block.getX()] = block.copyWithNewColor(Color.YELLOW);
                    });
                }
            }

        BOARD.setAllBlocks(allBlocks);
        return areThereReadyLines;
    }

    /**
     * Прибирає з allBlocks всі видалені блоки та рухає верхні блоки вниз
     */
    public void removeDeletedBlocksAndShiftDownward() {
        int offset = 2;
        int width = BOARD.getWidth() - offset;
        Tetromino.Block[][] allBlocks = BOARD.getAllBlocks();
        int score = BOARD.getScore();

        // Перевіряє кожну лінію на знаходження заповнених ліній
        for (int numberChekingLine = 0; numberChekingLine < allBlocks.length; numberChekingLine++) {
            Tetromino.Block[] currentLine = allBlocks[numberChekingLine];
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
                    Tetromino.Block[] someLineAboveChekingLine = allBlocks[numberSomeLineAboveChekingLine];
                    for (int x = 1; x <= maxCountBlocksInLine; x++) {
                        Tetromino.Block block = someLineAboveChekingLine[x];
                        if (Objects.nonNull(block)) {
                            someLineAboveChekingLine[x] = block.lower();
                        }
                    }

                    // Опускає чергову лінію вниз
                    allBlocks[numberSomeLineAboveChekingLine + 1] = Arrays.copyOf(someLineAboveChekingLine, someLineAboveChekingLine.length);
                    Arrays.fill(someLineAboveChekingLine, null);
                }

                // Збільшення балів
                score = score + (10 * (width - 2));

                // Зменшуємо номер лінії, щоб вона пройшла перевірку повторно
                --numberChekingLine;
            }
        }

        BOARD.setAllBlocks(allBlocks);
        BOARD.setScore(score);
    }

    public void nextTetromino(Tetromino nextTetromino) {
        BOARD.setTetromino(BOARD.getNextTetromino());
        BOARD.setNextTetromino(nextTetromino);
    }

    public void addBlocks(List<Tetromino.Block> blocks) {
        Tetromino.Block[][] allBlocks = BOARD.getAllBlocks();
        blocks.forEach(newBlock -> {
            // Координати, які потрібно перевірити
            int x = newBlock.getX();
            int y = newBlock.getY();

            // Перевірка координат на від'ємне число
            boolean isCoordinatesNotSmall = x >= 0 && y >= 0;
            // Перевірка координат на завелике число
            boolean isCoordinatesNotBig = x < allBlocks[0].length && y < allBlocks.length;

            // Перевірка чи координати блока знаходяться в межах розміру поля
            if (isCoordinatesNotSmall && isCoordinatesNotBig) {
                allBlocks[newBlock.getY()][newBlock.getX()] = newBlock.copyWithNewColor(Color.LIGHT_GRAY);
            }
        });

        BOARD.setAllBlocks(allBlocks);
    }

    private Tetromino createRandomTetromino() {
        int coordinatX = BOARD.getWidth() / 2;
        int coordinatY = 0;
        return TETROMINO_SERVICE.getRandom(coordinatX, coordinatY);
    }
}