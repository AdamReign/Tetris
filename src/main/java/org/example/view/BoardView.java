package org.example.view;

import org.example.enums.Position;
import org.example.model.Board;
import org.example.model.Tetromino;

import javax.swing.*;
import java.util.List;
import java.awt.*;

/**
 * Відображає ігрове поле
 */
public class BoardView extends JPanel {
    /**
     * Модель ігрового поля
     */
    private final Board board;

    public BoardView(Board board) {
        this.board = board;
    }

    /**
     * Відображає на екрані будь-які задані елементи
     * @param g містить в собі дані для відображення
     */
    @Override
    public void paint(Graphics g) {
//        int scale = board.getScale();
//        int width = board.getWidth();
//        int height = board.getHeight();
//        Tetromino tetromino = board.getTetromino();
//        Tetromino nextTetromino = board.getNextTetromino();
//        List<Tetromino.Block> allBlocks = board.getAllBlocks();
//        boolean gameOver = board.isGameOver();
//        boolean pause = board.isPause();
//        int score = board.getScore();

        drawBackground(g);

        drawDropZone(g);

        drawTetromino(g);

        markCompletedLines();

        removeDeletedBlocksAndShiftDownward();

        drawBlocksOnBoard(g);

        drawBorders(g);

        displayPauseMessage(g);

        displayGameOverMessage(g);

        drawNextTetromino(g);

        drawCoordinates(g);

        drawScore(g);
    }

    /**
     * Відображає в інформаційній частині ігрового поля бали, які гравець набрав за гру
     */
    private void drawScore(Graphics g) {
        int scale = board.getScale();
        int width = board.getWidth();
        int offsetX = (width + 1) * scale; // Зміщення по X для відображення даних про бали
        String score = String.format("%06d", board.getScore());

        // Налаштування шрифту та графіки для надпису "БАЛИ"
        Font scoreFont = new Font("Bitstream Charter", Font.BOLD, 35);
        g.setFont(scoreFont);
        g.setColor(Color.YELLOW);
        int scoreTextX = offsetX + 15; // Координата X для надпису "БАЛИ"
        int scoreTextY = 7 * scale + 5; // Координата Y для надпису "БАЛИ"
        g.drawString("БАЛИ", scoreTextX, scoreTextY);

        // Малювання фону для відображення балів
        g.setColor(Color.GRAY);
        int backgroundX = offsetX + 10; // Координата X для фону
        int backgroundY = 7 * scale + 13; // Координата Y для фону
        int backgroundWidth = 4 * scale - 18; // Ширина фону
        int backgroundHeight = scale + 5; // Висота фону
        g.fillRect(backgroundX, backgroundY, backgroundWidth, backgroundHeight);

        g.setColor(Color.YELLOW);
        int scoreX = offsetX + 12; // Координата X для відображення балів
        int scoreY = 7 * scale + 16; // Координата Y для відображення балів
        int scoreWidth = 4 * scale - 22; // Ширина блоку з балами
        int scoreHeight = scale; // Висота блоку з балами
        g.fillRect(scoreX, scoreY, scoreWidth, scoreHeight);

        // Налаштування шрифту та графіки для відображення балів
        Font scoreValueFont = new Font("Bitstream Charter", Font.BOLD, 30);
        g.setFont(scoreValueFont);
        g.setColor(Color.BLACK);
        g.drawString(score, scoreTextX, 8 * scale + 10); // Відображення кількості балів
    }

    /**
     * Відображає координати фігурки
     */
    private void drawCoordinates(Graphics g) {
        int scale = board.getScale();
        int height = board.getHeight();
        Tetromino tetromino = board.getTetromino();

        // Налаштування шрифту та графіки для відображення координат фігурки
        Font coordinatesFont = new Font("Bitstream Charter", Font.BOLD, 20);
        g.setFont(coordinatesFont);
        g.setColor(Color.YELLOW);

        // Координати для відображення тексту з координатами фігурки
        int textX = scale + 5; // Координата X для тексту з координатами
        int textY = 56; // Координата Y для тексту з координатами

        // Відображення тексту з координатами фігурки
        String coordinatesText = String.format("X: %d Y: %d", tetromino.getX(), (height - 1 - tetromino.getY()));
        g.drawString(coordinatesText, textX, textY);
    }

    /**
     * Малює в інформаційному блоці яка буде наступна фігурка
     */
    private void drawNextTetromino(Graphics g) {
        int scale = board.getScale();
        int width = board.getWidth();
        Tetromino nextTetromino = board.getNextTetromino();

        nextTetromino.getAllPositions().get(Position.UP).forEach(block ->  {
            g.setColor(block.getColor());
            int blockX = block.getX();
            int blockY = block.getY();
            int blockScale = scale - 1;

            int testX = (blockX + width + 2) * scale;
            int testY = (blockY + 3) * scale;
            switch (nextTetromino.getType()) {
                case I -> g.fillRect(
                        testX + 17,
                        (blockY + 4) * scale + 1,
                        blockScale,blockScale);
                case J, L -> g.fillRect(
                        testX + 1,
                        testY + 17,
                        blockScale, blockScale);
                case O -> g.fillRect(
                        testX + 1,
                        testY + 1,
                        blockScale, blockScale);
                case S, T, Z -> g.fillRect(
                        (blockX + width + 1) * scale + 17,
                        testY + 1,
                        blockScale, blockScale);
            }
        });
    }

    /**
     * Виводить на екран "Кінець гри", коли гру закінчено
     */
    private void displayGameOverMessage(Graphics g) {
        int scale = board.getScale();
        int height = board.getHeight();
        List<Tetromino.Block> allBlocks = board.getAllBlocks();

        for (Tetromino.Block block: allBlocks) {
            boolean isBlockTouchingCeiling = block.getY() == 1;
            if (isBlockTouchingCeiling) {
                board.setGameOver(true);
                Font sGameOver = new Font("Bitstream Charter", Font.BOLD, 40);
                Graphics2D g2D = (Graphics2D) g;
                g2D.setFont(sGameOver);
                g2D.setColor(Color.YELLOW);
                g2D.drawString(
                        "Кінець гри",
                        5 * scale,
                        (height * scale) / 2);
                break;
            }
        }
    }

    /**
     * Виводить на екран текст "Пауза", коли гра ставиться на паузу
     */
    private void displayPauseMessage(Graphics g) {
        int scale = board.getScale();
        int height = board.getHeight();
        boolean gameOver = board.isGameOver();
        boolean pause = board.isPause();

        if (!gameOver && pause) {
            Font sPause = new Font("Bitstream Charter", Font.BOLD, 40);
            Graphics2D g2D = (Graphics2D) g;
            g2D.setFont(sPause);
            g2D.setColor(Color.YELLOW);
            g2D.drawString(
                    "Пауза",
                    7 * scale,
                    (height * scale) / 2);
        }
    }

    /**
     * Малює кордони ігрового поля
     */
    private void drawBorders(Graphics g) {
        int scale = board.getScale();
        int width = board.getWidth();
        int height = board.getHeight();

        for (int i = 1; i < height - 1; i++) {
            int borderScale = scale - 1;
            int borderY = i * scale + 1;

            g.setColor(Color.GRAY);
            // Малює лівий кордон
            g.fillRect(
                    1,
                    borderY,
                    borderScale, borderScale);
            // Малює кордон_1 правого кордону
            g.fillRect(
                    width * scale + 1,
                    borderY,
                    borderScale, borderScale);
            // Малює кордон_2 правого кордону
            g.fillRect(
                    (width + 5) * scale + 1,
                    borderY,
                    borderScale, borderScale);
        }

        // Малює кордон_1 верхнього кордону
        for (int i = 0; i < width + 6; i++) {
            int borderScale = scale - 1;

            g.setColor(Color.GRAY);
            g.fillRect(
                    i * scale + 1,
                    1,
                    borderScale, borderScale);
        }

        // Малює кордон_2 верхнього кордону
        for (int i = width + 1; i < width + 5; i++) {
            int borderScale = scale - 1;

            g.setColor(Color.GRAY);
            g.fillRect(
                    i * scale + 1,
                    (5) * scale + 1,
                    borderScale, borderScale);
        }

        // Малює нижній кордон
        for (int i = 0; i < width + 6; i++) {
            int borderScale = scale - 1;

            g.setColor(Color.GRAY);
            g.fillRect(
                    i * scale + 1,
                    (height - 1) * scale + 1,
                    borderScale, borderScale);
        }
    }

    /**
     * Малює всі блоки allBlocks на ігровому полі
     */
    private void drawBlocksOnBoard(Graphics g) {
        int scale = board.getScale();
        List<Tetromino.Block> allBlocks = board.getAllBlocks();

        allBlocks.forEach(block -> {
            int borderScale = scale - 1;

            g.setColor(block.getColor());
            g.fillRect(
                    block.getX() * scale + 1,
                    block.getY() * scale + 1,
                    borderScale, borderScale);
        });
    }

    /**
     * Перевіряє блоки в allBlocks на створення заповненої лінії.
     * Якщо утворилась лінія, то помічає всі блоки лінії як видаленими
     * та змінює їх колір на жовтий
     */
    private void markCompletedLines() {
        int width = board.getWidth();
        List<Tetromino.Block> allBlocks = board.getAllBlocks();

        allBlocks.forEach(checkingBlock ->  {
            int lineSize = 1;
            for (Tetromino.Block block: allBlocks) {
                if (checkingBlock.getY() == block.getY()) {
                    lineSize++;
                }
            }

            if (lineSize >= width) {
                for (int i = 0; i < allBlocks.size(); i++) {
                    if (allBlocks.get(i).getY() == checkingBlock.getY()) {
                        allBlocks.get(i).delete();
                        allBlocks.set(i, allBlocks.get(i).copyWithColor(Color.YELLOW));
                    }
                }
            }
        });
    }

    /**
     * Прибирає з allBlocks всі видалені блоки та рухає верхні блоки вниз
     */
    private void removeDeletedBlocksAndShiftDownward() {
        List<Tetromino.Block> allBlocks = board.getAllBlocks();
        int score = board.getScore();

        for (int i = 0; i < allBlocks.size(); i++) {
            Tetromino.Block blockForDeletionCheck = allBlocks.get(i);
            if (blockForDeletionCheck.isDeleted()) {
                // Видаляється блок та збільшуються бали
                Tetromino.Block deletedBlock = allBlocks.remove(i);
                score = score + 10;

                // Опускає на одну позицію вниз всі блоки над видаленим
                for (int j = 0; j < allBlocks.size(); j++) {
                    Tetromino.Block checkingBlock = allBlocks.get(j);
                    boolean isBlockAboveDeleted = deletedBlock.getX() == checkingBlock.getX() && deletedBlock.getY() > checkingBlock.getY();
                    if (isBlockAboveDeleted) {
                        Tetromino.Block blockAboveDeleted = checkingBlock;
                        allBlocks.set(j, blockAboveDeleted.lower());
                    }
                }
            }
        }

        board.setScore(score);
    }

    /**
     * Малює ігрову фігурку на полі
     */
    private void drawTetromino(Graphics g) {
        int scale = board.getScale();
        Tetromino tetromino = board.getTetromino();

        for (int i = 0; i < tetromino.getBlocks().size(); i++) {
            g.setColor(tetromino.getBlocks().get(i).getColor());
            g.fillRect(
                    tetromino.getBlocks().get(i).getX() * scale + 1,
                    tetromino.getBlocks().get(i).getY() * scale + 1,
                    scale - 1, scale - 1
            );
        }
    }

    /**
     * Малює приціл для ігрової фігурки на полі
     */
    private void drawDropZone(Graphics g) {
        int scale = board.getScale();
        Tetromino tetromino = board.getTetromino();

        // Створює Y координату для прицілу фігурки
        int mheight = calculateAimYCoordinate();

        // Малює приціл
        for (int i = 0; i < tetromino.getBlocks().size(); i++) {
            g.setColor(tetromino.getBlocks().get(i).getColor());
            g.fillRect(
                    tetromino.getBlocks().get(i).getX() * scale + 1,
                    (tetromino.getBlocks().get(i).getY()+(mheight - tetromino.getY())) * scale + 1,
                    scale - 1, scale - 1
            );
            g.setColor(Color.BLACK);
            g.fillRect(
                    tetromino.getBlocks().get(i).getX() * scale + 2,
                    (tetromino.getBlocks().get(i).getY()+(mheight - tetromino.getY())) * scale + 2,
                    scale - 3, scale - 3
            );
        }
    }

    /**
     * Створює Y координату для прицілу фігурки
     */
    private int calculateAimYCoordinate() {
        int height = board.getHeight();
        Tetromino tetromino = board.getTetromino();
        List<Tetromino.Block> allBlocks = board.getAllBlocks();

        int mheight = height - 2;
        for (int i = tetromino.getY() + 1; i < height - 1; i++) {
            for (Tetromino.Block block: tetromino.getBlocks()) {
                if (allBlocks.contains(new Tetromino.Block(
                        block.getX(),
                        block.getY() + (i - tetromino.getY()),
                        null
                ))) {
                    mheight = Math.min(i - 1, mheight);
                    break;
                }
            }
        }
        return mheight;
    }

    /**
     * Малює фон ігрового поля
     */
    private void drawBackground(Graphics g) {
        int scale = board.getScale();
        int width = (board.getWidth() + 6) * scale;
        int height = board.getHeight() * scale;

        g.setColor(Color.BLACK);
        g.fillRect(0, 0, width, height);
    }
}
