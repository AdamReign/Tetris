package org.tetris.gameplay.tetromino;

public interface TetrominoViewModel {
    /**
     * Змінює положення фігурки
     * @param tetromino - фігурка
     * @param grid - клітини поля
     */
    void rotate(Tetromino tetromino, Tetromino.Block[][] grid);

    /**
     * Виконує падіння фігурки вниз до першої ж перешкоди,
     * повертає кількість клітинок, які вона пролетіла
     * @param tetromino - фігурка
     * @param grid - клітини поля
     */
    int fall(Tetromino tetromino, Tetromino.Block[][] grid);

    /**
     * Рухає фігурку ліворуч
     * @param tetromino - фігурка
     * @param grid - клітини поля
     */
    void moveLeft(Tetromino tetromino, Tetromino.Block[][] grid);

    /**
     * Рухає фігурку праворуч
     * @param tetromino - фігурка
     * @param grid - клітини поля
     */
    void moveRight(Tetromino tetromino, Tetromino.Block[][] grid);

    /**
     * Рухає фігурку вниз
     * @param tetromino - фігурка
     * @param grid - клітини поля
     */
    boolean moveDown(Tetromino tetromino, Tetromino.Block[][] grid);

    /**
     * Створює Y координату для прицілу фігурки
     */
    int calculateAimYCoordinate();
}