package org.tetris.gameplay.board.command;

import org.tetris.common.util.concurrency.Command;
import org.tetris.gameplay.board.Board;
import org.tetris.gameplay.board.BoardViewModel;
import org.tetris.gameplay.gamefield.GameField;
import org.tetris.gameplay.tetromino.Tetromino;
import org.tetris.gameplay.tetromino.TetrominoViewModel;

import java.util.concurrent.TimeUnit;

public class TetrominoFallCommand implements Command {
    private final Board board;
    private final BoardViewModel boardViewModel;
    private final TetrominoViewModel tetrominoViewModel;

    public TetrominoFallCommand(Board board,
                                BoardViewModel boardViewModel,
                                TetrominoViewModel tetrominoViewModel) {
        this.board = board;
        this.boardViewModel = boardViewModel;
        this.tetrominoViewModel = tetrominoViewModel;
    }

    @Override
    public void execute() {
        // Авторух фігурки вниз
        while (board.isOpen()) {
            GameField gameField = board.getGameField();
            try {
                TimeUnit.MILLISECONDS.sleep(gameField.getFallingSpeed());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            if (!board.isOver() && !board.isPause()) {
                Tetromino tetromino = gameField.getTetromino();
                Tetromino.Block[][] grid = gameField.getGrid();
                // Опускає фігурку вниз, та перевіряє чи досягла фігурка перешкоди
                boolean isBottom = tetrominoViewModel.moveDown(tetromino, grid);
                // Перевіряє чи досягла фігурка перешкоди
                if (isBottom) {
                    // Додає блоки фігурки на поле
                    boardViewModel.addTetrominoBlocksOnGrid(tetromino.getBlocks());
                    // Змінює фігурку на наступну
                    boardViewModel.nextTetromino();
                }
                // Перевіряє чи є на полі заповнені лінії
                boardViewModel.deleteCompleteLines();

                // Збільшує швидкість гри в залежності від кількості набраних балів
                boardViewModel.increaseSpeed();
            }
        }
    }
}