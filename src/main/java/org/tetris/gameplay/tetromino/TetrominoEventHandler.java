package org.tetris.gameplay.tetromino;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.tetris.gameplay.board.BoardDTO;
import org.tetris.gameplay.board.BoardViewModel;
import org.tetris.gameplay.gamefield.GameField;

import java.util.Objects;

public class TetrominoEventHandler {
    private static TetrominoEventHandler instance;
    private final BoardViewModel boardViewModel;
    private final TetrominoViewModel tetrominoViewModel;

    private TetrominoEventHandler(BoardViewModel boardViewModel, TetrominoViewModel tetrominoViewModel) {
        this.boardViewModel = boardViewModel;
        this.tetrominoViewModel = tetrominoViewModel;
    }

    public static TetrominoEventHandler createInstance(BoardViewModel boardViewModel,
                                                       TetrominoViewModel tetrominoViewModel) {
        if (Objects.isNull(instance)) {
            instance = new TetrominoEventHandler(boardViewModel, tetrominoViewModel);
            return instance;
        } else {
            throw new IllegalStateException("TetrominoEventsHandler has already been created");
        }
    }

    public static TetrominoEventHandler getInstance() {
        if (Objects.isNull(instance)) {
            throw new IllegalStateException("TetrominoEventsHandler has not been created");
        }
        return instance;
    }

    public static boolean isInstanceCreated() {
        return instance != null;
    }

    /**
     * Обробляє події керування фігуркою
     *
     * @param e - код натиснутої клавіші клавіатури
     */
    public void handle(KeyEvent e, BoardDTO boardData) {
        GameField gameField = boardData.getGameField();
        Tetromino tetromino = gameField.getTetromino();
        Tetromino.Block[][] grid = gameField.getGrid();
        // Код натиснутої клавіші
        KeyCode keyCode = e.getCode();
        switch (keyCode) {
            // Якщо "стрілка вверх" - прокрутити фігурку
            case UP, W -> {
                tetrominoViewModel.rotate(tetromino, grid);
            }
            // Якщо "стрілка вправо" - посунути фігурку праворуч
            case LEFT, A -> {
                tetrominoViewModel.moveLeft(tetromino, grid);
            }
            // Якщо "стрілка вліво" - посунути фігурку ліворуч
            case RIGHT, D -> {
                tetrominoViewModel.moveRight(tetromino, grid);
            }
            // Якщо "стрілка вниз" - посунути фігурку вниз
            case DOWN, S -> {
                // Опускає фігурку вниз, та перевіряє чи досягла фігурка перешкоди
                boolean isBottom = tetrominoViewModel.moveDown(tetromino, grid);
                // Додає один бал за одне натискання кнопки вниз
                boardViewModel.increaseScore(1);
                // Перевіряє чи досягла фігурка перешкоди
                if (isBottom) {
                    // Додає блоки фігурки на поле
                    boardViewModel.addTetrominoBlocksOnGrid(tetromino.getBlocks());
                    // Змінює фігурку на наступну
                    boardViewModel.nextTetromino();
                }
                // Перевіряє чи є на полі заповнені лінії
                boardViewModel.deleteCompleteLines();
            }
            // Якщо "SPACE" - моментально опустити фігурку в самий низ
            case SPACE -> {
                // Опускає фігурку вниз до першої перешкоди, та повертає кількість пройденого шляху
                int score = tetrominoViewModel.fall(tetromino, grid);
                // Збільшує бали на кількість пройденого шляху
                boardViewModel.increaseScore(score);
                // Додає блоки фігурки на поле
                boardViewModel.addTetrominoBlocksOnGrid(tetromino.getBlocks());
                // Змінює фігурку на наступну
                boardViewModel.nextTetromino();
                // Перевіряє чи є на полі заповнені лінії
                boardViewModel.deleteCompleteLines();
            }
        }
    }
}