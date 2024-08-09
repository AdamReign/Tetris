package org.tetris.gameplay.gamefield;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.tetris.gameplay.tetromino.Tetromino;

import java.util.Arrays;
import java.util.Objects;

public class GameField {
    private static GameField instance;

    private final int WIDTH;
    private final int HEIGHT;

    private final ObjectProperty<Tetromino.Block[][]> grid;
    private final ObjectProperty<Tetromino> tetromino;
    private final ObjectProperty<Tetromino> nextTetromino;
    private final IntegerProperty fallingSpeed;

    private GameField(int width, int height) {
        WIDTH = width;
        HEIGHT = height;

        grid = new SimpleObjectProperty<>(new Tetromino.Block[height][width]);
        tetromino = new SimpleObjectProperty<>();
        nextTetromino = new SimpleObjectProperty<>();
        fallingSpeed = new SimpleIntegerProperty(1000);
    }

    public static GameField createInstance(int width, int height) {
        if (Objects.isNull(instance)) {
            instance = new GameField(width, height);
            return instance;
        } else {
            throw new IllegalStateException("GameField has already been created");
        }
    }

    public static GameField getInstance() {
        if (Objects.isNull(instance)) {
            throw new IllegalStateException("GameField has not been created");
        }
        return instance;
    }

    public static boolean isInstanceCreated() {
        return instance != null;
    }

    public int getWidth() {
        return WIDTH;
    }

    public int getHeight() {
        return HEIGHT;
    }

    public Tetromino.Block[][] getGrid() {
        Tetromino.Block[][] grid = new Tetromino.Block[HEIGHT][WIDTH];
        for (int y = 0; y < grid.length; y++) {
            grid[y] = Arrays.copyOf(this.grid.get()[y], this.grid.get()[y].length);
        }
        return grid;
    }

    public Tetromino getTetromino() {
        return tetromino.get();
    }

    public Tetromino getNextTetromino() {
        return nextTetromino.get();
    }

    public int getFallingSpeed() {
        return fallingSpeed.get();
    }

    public synchronized void setGrid(Tetromino.Block[][] grid) {
        Tetromino.Block[][] newGrid = new Tetromino.Block[HEIGHT][WIDTH];
        for (int y = 0; y < newGrid.length; y++) {
            newGrid[y] = Arrays.copyOf(grid[y], grid[y].length);
        }
        this.grid.set(newGrid);
    }

    public void setTetromino(Tetromino tetromino) {
        this.tetromino.set(tetromino);
    }

    public void setNextTetromino(Tetromino nextTetromino) {
        this.nextTetromino.set(nextTetromino);
    }

    public void setFallingSpeed(int fallingSpeed) {
        this.fallingSpeed.set(fallingSpeed);
    }

    public ObjectProperty<Tetromino.Block[][]> gridProperty() {
        return grid;
    }

    public ObjectProperty<Tetromino> tetrominoProperty() {
        return tetromino;
    }

    public ObjectProperty<Tetromino> nextTetrominoProperty() {
        return nextTetromino;
    }

    public IntegerProperty fallingSpeedProperty() {
        return fallingSpeed;
    }
}