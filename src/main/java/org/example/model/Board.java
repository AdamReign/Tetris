package org.example.model;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Board {
    private final int SCALE;
    private final int WIDTH;
    private final int HEIGHT;
    private volatile Tetromino tetromino;
    private volatile Tetromino nextTetromino;
    private volatile List<Tetromino.Block> allBlocks = new CopyOnWriteArrayList<>();
    private volatile boolean close = false;
    private volatile boolean gameOver;
    private volatile boolean pause;
    private volatile int score = 0;
    private volatile int speed = 1000;

    public Board(int width, int height, int scale) {
        WIDTH = width;
        HEIGHT = height;
        SCALE = scale;
    }

    public int getScale() {
        return SCALE;
    }

    public int getWidth() {
        return WIDTH;
    }

    public int getHeight() {
        return HEIGHT;
    }

    public Tetromino getTetromino() {
        return tetromino;
    }

    public void setTetromino(Tetromino tetromino) {
        this.tetromino = tetromino;
    }

    public Tetromino getNextTetromino() {
        return nextTetromino;
    }

    public void setNextTetromino(Tetromino nextTetromino) {
        this.nextTetromino = nextTetromino;
    }

    public List<Tetromino.Block> getAllBlocks() {
        return allBlocks;
    }

    public void setAllBlocks(List<Tetromino.Block> allBlocks) {
        this.allBlocks = allBlocks;
    }

    public boolean isClose() {
        return close;
    }

    public void setClose(boolean close) {
        this.close = close;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

    public boolean isPause() {
        return pause;
    }

    public void setPause(boolean pause) {
        this.pause = pause;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }
}