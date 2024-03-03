package org.example.model;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public final class Board {
    private final int SCALE;
    private final int WIDTH;
    private final int HEIGHT;
    private final ImageIcon IMAGE_ICON;
    private final Color COLOR;
    private volatile Tetromino tetromino;
    private volatile Tetromino nextTetromino;
    private volatile Tetromino.Block[][] allBlocks;
    private volatile boolean close;
    private volatile boolean gameOver;
    private volatile boolean pause;
    private volatile int score;
    private volatile int speed;

    public Board(int width, int height, int scale, ImageIcon imageIcon, Color color) {
        IMAGE_ICON = imageIcon;
        WIDTH = width;
        HEIGHT = height;
        SCALE = scale;
        COLOR = color;

        allBlocks = new Tetromino.Block[height][width];

        score = 0;
        speed = 1000;
        close = false;
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

    public ImageIcon getImageIcon() {
        return IMAGE_ICON;
    }

    public Color getColor() {
        return COLOR;
    }

    public Tetromino getTetromino() {
        return tetromino;
    }


    public Tetromino getNextTetromino() {
        return nextTetromino;
    }


    public Tetromino.Block[][] getAllBlocks() {
        Tetromino.Block[][] allBlocks = new Tetromino.Block[HEIGHT][WIDTH];

        for (int y = 0; y < allBlocks.length; y++) {
            allBlocks[y] = Arrays.copyOf(this.allBlocks[y], this.allBlocks[y].length);
        }

        return allBlocks;
    }

    public boolean isClose() {
        return close;
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

    public int getScore() {
        return score;
    }

    public int getSpeed() {
        return speed;
    }








    public void setTetromino(Tetromino tetromino) {
        this.tetromino = tetromino;
    }

    public void setNextTetromino(Tetromino nextTetromino) {
        this.nextTetromino = nextTetromino;
    }

    public synchronized void setAllBlocks(Tetromino.Block[][] allBlocks) {
        Tetromino.Block[][] newAllBlocks = new Tetromino.Block[HEIGHT][WIDTH];

        for (int y = 0; y < newAllBlocks.length; y++) {
            newAllBlocks[y] = Arrays.copyOf(allBlocks[y], allBlocks[y].length);
        };

        this.allBlocks = newAllBlocks;
    }


    public void setClose(boolean close) {
        this.close = close;
    }

    public void setPause(boolean pause) {
        this.pause = pause;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }
}