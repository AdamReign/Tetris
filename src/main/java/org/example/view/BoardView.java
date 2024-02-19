package org.example.view;

import org.example.enums.Position;
import org.example.model.Tetromino;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class BoardView extends JPanel {
    private final int SCALE;
    private final int WIDTH;
    private final int HEIGHT;
    private Tetromino tetromino;
    private Tetromino nextTetromino;
    private List<Tetromino.Block> allBlocks = new ArrayList<>();
    private volatile boolean close = false;
    private volatile boolean gameOver;
    private volatile boolean pause;
    private volatile int score = 0;
    private volatile int speed = 1000;

    public BoardView(int width, int height, int scale) {
        WIDTH = width;
        HEIGHT = height;
        SCALE = scale;
    }

    @Override
    public void paint(Graphics g) {
        // Малює ігрове поле
        g.fillRect(0, 0, (WIDTH+6)*SCALE, HEIGHT*SCALE);

        // Створює Y координату для прицілу фігурки
        int mheight = HEIGHT-2;
        for (int i = tetromino.getY()+1; i < HEIGHT-1; i++) {
            for (Tetromino.Block block: tetromino.getBlocks()) {
                if (allBlocks.contains(new Tetromino.Block(block.getX(), block.getY()+(i- tetromino.getY()),null))) {
                    mheight = Math.min(i - 1, mheight);
                    break;
                }
            }
        }

        // Створює приціл для фігурки
        for (int i = 0; i < tetromino.getBlocks().size(); i++) {
            g.setColor(tetromino.getBlocks().get(i).getColor());
            g.fillRect(tetromino.getBlocks().get(i).getX()*SCALE+1, (tetromino.getBlocks().get(i).getY()+(mheight- tetromino.getY()))*SCALE+1, SCALE-1, SCALE-1);
            g.setColor(Color.BLACK);
            g.fillRect(tetromino.getBlocks().get(i).getX()*SCALE+2, (tetromino.getBlocks().get(i).getY()+(mheight- tetromino.getY()))*SCALE+2, SCALE-3, SCALE-3);
        }

        // Малює фігурку
        for (int i = 0; i < tetromino.getBlocks().size(); i++) {
            g.setColor(tetromino.getBlocks().get(i).getColor());
            g.fillRect(tetromino.getBlocks().get(i).getX()*SCALE+1, tetromino.getBlocks().get(i).getY()*SCALE+1, SCALE-1, SCALE-1);
        }

        // Видаляє з allBlocks всі блоки жовтого кольору і рухає верхні блоки вниз
        for (int i = 0; i < allBlocks.size(); i++) {
            if (allBlocks.get(i).getColor() == Color.YELLOW) {
                for (int j = 0; j < allBlocks.size(); j++) {
                    if (allBlocks.get(j).getX() == allBlocks.get(i).getX() && allBlocks.get(j).getY() < allBlocks.get(i).getY()) {
                        allBlocks.set(j, new Tetromino.Block(allBlocks.get(j).getX(), allBlocks.get(j).getY()+1, allBlocks.get(j).getColor()));
                    }
                }

                allBlocks.remove(i);
                score += 10;
            }
        }

        // Перевіряє блоки в allBlocks на створення лінії і помічує їх жовтим кольором
        for (Tetromino.Block block: allBlocks) {
            int lineSize = 1;
            for (Tetromino.Block block1: allBlocks) {
                if (block.getY() == block1.getY()) lineSize++;
            }

            if (lineSize == WIDTH) {
                for (int i = 0; i < allBlocks.size(); i++) {
                    if (allBlocks.get(i).getY() == block.getY()) {
                        allBlocks.set(i, new Tetromino.Block(allBlocks.get(i).getX(), allBlocks.get(i).getY(), Color.YELLOW));
                    }
                }
            }
        }

        // Малює блоки на полі
        for (Tetromino.Block block: allBlocks) {
            g.setColor(block.getColor());
            g.fillRect(block.getX()*SCALE+1, block.getY()*SCALE+1, SCALE-1, SCALE-1);
        }

        // Малює лівий кордон
        for (int i = 1; i < HEIGHT-1; i++) {
            g.setColor(Color.GRAY);
            g.fillRect(1, i*SCALE+1, SCALE-1, SCALE-1);
        }

        // Малює кордон_1 правого кордону
        for (int i = 1; i < HEIGHT-1; i++) {
            g.setColor(Color.GRAY);
            g.fillRect(WIDTH*SCALE+1, i*SCALE+1, SCALE-1, SCALE-1);
        }

        // Малює кордон_2 правого кордону
        for (int i = 1; i < HEIGHT-1; i++) {
            g.setColor(Color.GRAY);
            g.fillRect((WIDTH+5)*SCALE+1, i*SCALE+1, SCALE-1, SCALE-1);
        }

        // Малює кордон_1 верхнього кордону
        for (int i = 0; i < WIDTH+6; i++) {
            g.setColor(Color.GRAY);
            g.fillRect(i*SCALE+1, 1, SCALE-1, SCALE-1);
        }

        // Малює кордон_2 верхнього кордону
        for (int i = WIDTH+1; i < WIDTH+5; i++) {
            g.setColor(Color.GRAY);
            g.fillRect(i*SCALE+1, (5)*SCALE+1, SCALE-1, SCALE-1);
        }

        // Малює нижній кордон
        for (int i = 0; i < WIDTH+6; i++) {
            g.setColor(Color.GRAY);
            g.fillRect(i*SCALE+1, (HEIGHT-1)*SCALE+1, SCALE-1, SCALE-1);
        }

        // Пише "Пауза"
        if (!gameOver && pause) {
            Font sPause = new Font("Bitstream Charter", Font.BOLD, 40);
            Graphics2D g2 = (Graphics2D) g;
            g2.setFont(sPause);
            g2.setColor(Color.YELLOW);
            g2.drawString("Пауза", 7*SCALE, (HEIGHT*SCALE)/2);
        }

        // Пише "Кінець гри"
        for (Tetromino.Block block: allBlocks) {
            if (block.getY() == 1) {
                Font sGameOver = new Font("Bitstream Charter", Font.BOLD, 40);
                Graphics2D g2 = (Graphics2D) g;
                g2.setFont(sGameOver);
                g2.setColor(Color.YELLOW);
                g2.drawString("Кінець гри", 5*SCALE, (HEIGHT*SCALE)/2);
                gameOver = true;
                break;
            }
        }

        // Малює наступну фігурку
        for (Tetromino.Block block: nextTetromino.getAllPositions().get(Position.UP)) {
            g.setColor(block.getColor());
            int blockX = block.getX();
            int blockY = block.getY();
            int scale = SCALE - 1;
            switch (nextTetromino.getType()) {
                case I -> g.fillRect((blockX + WIDTH + 2) * SCALE + 17, (blockY + 4) * SCALE + 1, scale, scale);
                case J -> g.fillRect((blockX + WIDTH + 2) * SCALE + 1, (blockY + 3) * SCALE + 17, scale, scale);
                case L -> g.fillRect((blockX + WIDTH + 2) * SCALE + 1, (blockY + 3) * SCALE + 17, scale, scale);
                case O -> g.fillRect((blockX + WIDTH + 2) * SCALE + 1, (blockY + 3) * SCALE + 1, scale, scale);
                case S -> g.fillRect((blockX + WIDTH + 1) * SCALE + 17, (blockY + 3) * SCALE + 1, scale, scale);
                case T -> g.fillRect((blockX + WIDTH + 1) * SCALE + 17, (blockY + 3) * SCALE + 1, scale, scale);
                case Z -> g.fillRect((blockX + WIDTH + 1) * SCALE + 17, (blockY + 3) * SCALE + 1, scale, scale);
            }
        }

        // Відображає координати фігурки
        Font coordinates = new Font("Bitstream Charter", Font.BOLD, 20);
        Graphics2D g2 = (Graphics2D) g;
        g2.setFont(coordinates);
        g2.setColor(Color.YELLOW);
        g2.drawString("X: " + tetromino.getX() + " Y: " + (HEIGHT-1- tetromino.getY()), SCALE+5, 56);

        // Відображає очки
        Font sScore = new Font("Bitstream Charter", Font.BOLD, 35);
        Graphics2D g3 = (Graphics2D) g;
        g3.setFont(sScore);
        g3.setColor(Color.YELLOW);
        g3.drawString("ОЧКИ", (WIDTH+1)*SCALE+15, 7*SCALE+5);

        g.setColor(Color.GRAY);
        g.fillRect((WIDTH+1)*SCALE+10, 7*SCALE+13, 4*SCALE-18, SCALE+5);
        g.setColor(Color.YELLOW);
        g.fillRect((WIDTH+1)*SCALE+12, 7*SCALE+16, 4*SCALE-22, SCALE);

        Font iScore = new Font("Bitstream Charter", Font.BOLD, 30);
        Graphics2D g4 = (Graphics2D) g;
        g4.setFont(iScore);
        g4.setColor(Color.BLACK);
        g4.drawString("000000".substring((""+score).length())+score, (WIDTH+1)*SCALE+15, 8*SCALE+10);
    }

    public int getScale() {
        return SCALE;
    }

    public int getWidthBoard() {
        return WIDTH;
    }

    public int getHeightBoard() {
        return HEIGHT;
    }

    public int getSpeed() {
        return speed;
    }

    public boolean isPause() {
        return pause;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public boolean isClose() {
        return close;
    }

    public int getScore() {
        return score;
    }

    public Tetromino getTetromino() {
        return tetromino;
    }

    public Tetromino getNextTetromino() {
        return nextTetromino;
    }

    public List<Tetromino.Block> getAllBlocks() {
        return allBlocks;
    }






    public void setTetromino(Tetromino tetromino) {
        this.tetromino = tetromino;
    }

    public void setNextTetromino(Tetromino tetromino) {
        this.nextTetromino = tetromino;
    }

    public void setPause(boolean pause) {
        this.pause = pause;
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public void setScore(int score) {
        this.score = Math.max(score, this.score);
    }

    public void setClose(boolean close) {
        this.close = close;
    }

    public void setAllBlocks(List<Tetromino.Block> allBlocks) {
        this.allBlocks = allBlocks;
    }
}
