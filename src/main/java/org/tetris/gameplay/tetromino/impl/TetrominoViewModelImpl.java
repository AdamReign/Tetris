package org.tetris.gameplay.tetromino.impl;

import org.tetris.gameplay.tetromino.Tetromino;
import org.tetris.gameplay.tetromino.TetrominoViewModel;
import org.tetris.gameplay.tetromino.TetrominoService;
import org.tetris.gameplay.tetromino.enums.Direction;

import java.util.Objects;

public class TetrominoViewModelImpl implements TetrominoViewModel {
    private static TetrominoViewModelImpl instance;
    private final TetrominoService tetrominoService;

    private TetrominoViewModelImpl(TetrominoService tetrominoService) {
        this.tetrominoService = tetrominoService;
    }

    public static TetrominoViewModelImpl createInstance(TetrominoService tetrominoService) {
        if (Objects.isNull(instance)) {
            instance = new TetrominoViewModelImpl(tetrominoService);
            return instance;
        } else {
            throw new IllegalStateException("TetrominoViewModelImpl has already been created");
        }
    }

    public static TetrominoViewModelImpl getInstance() {
        if (Objects.isNull(instance)) {
            throw new IllegalStateException("TetrominoViewModelImpl has not been created");
        }
        return instance;
    }

    public static boolean isInstanceCreated() {
        return instance != null;
    }

    /**
     * Змінює положення фігурки
     * @param tetromino - фігурка
     * @param grid - клітини поля
     */
    @Override
    public void rotate(Tetromino tetromino, Tetromino.Block[][] grid) {
        tetrominoService.rotate(tetromino, grid);
    }

    /**
     * Виконує падіння фігурки вниз до першої ж перешкоди,
     * повертає кількість клітинок, які вона пролетіла
     * @param tetromino - фігурка
     * @param grid - клітини поля
     */
    @Override
    public int fall(Tetromino tetromino, Tetromino.Block[][] grid) {
        return tetrominoService.fall(tetromino, grid);
    }

    /**
     * Рухає фігурку ліворуч
     * @param tetromino - фігурка
     * @param grid - клітини поля
     */
    @Override
    public void moveLeft(Tetromino tetromino, Tetromino.Block[][] grid) {
        tetrominoService.move(tetromino, Direction.LEFT, grid);
    }

    /**
     * Рухає фігурку праворуч
     * @param tetromino - фігурка
     * @param grid - клітини поля
     */
    @Override
    public void moveRight(Tetromino tetromino, Tetromino.Block[][] grid) {
        tetrominoService.move(tetromino, Direction.RIGHT, grid);
    }

    /**
     * Рухає фігурку вниз
     * @param tetromino - фігурка
     * @param grid - клітини поля
     */
    @Override
    public boolean moveDown(Tetromino tetromino, Tetromino.Block[][] grid) {
        return tetrominoService.move(tetromino, Direction.DOWN, grid);
    }

    /**
     * Створює Y координату для прицілу фігурки
     */
    @Override
    public int calculateAimYCoordinate() {
        return tetrominoService.calculateAimYCoordinate();
    }
}