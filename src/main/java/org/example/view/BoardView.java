package org.example.view;

import org.example.model.Board;
import org.example.model.Tetromino;

import javax.swing.*;
import java.util.Arrays;
import java.util.List;
import java.awt.*;
import java.util.Objects;

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
        drawBackground(g);

        drawDropZone(g);

        drawTetromino(g);

        drawBlocksOnBoard(g);

        drawBorders(g);

        drawCoordinates(g);

        drawNextTetromino(g);

        drawScore(g);

        displayPauseMessage(g);

        displayGameOverMessage(g);
    }

    /**
     * Відображає в інформаційній частині ігрового поля бали, які гравець набрав за гру
     */
    private void drawScore(Graphics g) {
        int scale = board.getScale();
        int width = board.getWidth();
        int offsetX = width * scale; // Зміщення по X для відображення даних про бали
        String score = String.format("%06d", board.getScore());

        // Налаштування шрифту та графіки для відображення балів
        Font scoreValueFont = new Font("Bitstream Charter", Font.BOLD, 30);
        g.setFont(scoreValueFont);
        g.setColor(Color.BLACK);
        g.drawString(score, offsetX + 15, 8 * scale + 10); // Відображення кількості балів
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
        String coordinatesText = String.format("X: %d Y: %d", tetromino.getX(), tetromino.getY());
        g.drawString(coordinatesText, textX, textY);
    }

    /**
     * Малює в інформаційному блоці яка буде наступна фігурка
     */
    private void drawNextTetromino(Graphics g) {
        int scale = board.getScale();
        int width = board.getWidth();
        Tetromino nextTetromino = board.getNextTetromino();

        int coordinatX = width * scale;
        int coordinatY = scale;
        ImageIcon imageForMenu = nextTetromino.getImageForMenu();
        g.drawImage(imageForMenu.getImage(), coordinatX, coordinatY, this);
    }

    /**
     * Виводить на екран "Кінець гри", коли гру закінчено
     */
    private void displayGameOverMessage(Graphics g) {
        int scale = board.getScale();
        int height = board.getHeight();
        Tetromino.Block[][] allBlocks = board.getAllBlocks();

        boolean isBlockTouchingCeiling = Arrays.stream(allBlocks[0]).anyMatch(Objects::nonNull);
        if (isBlockTouchingCeiling) {

            // TODO Прибрати частину метода в BoardService, а залишити лише малювання
            board.setGameOver(true);

            Font sGameOver = new Font("Bitstream Charter", Font.BOLD, 40);
            Graphics2D g2D = (Graphics2D) g;
            g2D.setFont(sGameOver);
            g2D.setColor(Color.YELLOW);
            g2D.drawString(
                    "Кінець гри",
                    5 * scale,
                    (height * scale) / 2
            );
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
                    (height * scale) / 2
            );
        }
    }

    /**
     * Малює кордони ігрового поля
     */
    private void drawBorders(Graphics g) {
        ImageIcon imageIcon = board.getImageIcon();
        g.drawImage(imageIcon.getImage(), 0, 0, this);
    }

    /**
     * Малює всі блоки allBlocks на ігровому полі
     */
    private void drawBlocksOnBoard(Graphics g) {
        int scale = board.getScale();
        Tetromino.Block[][] allBlocks = board.getAllBlocks();

        Arrays.stream(allBlocks).forEach(line -> {
            Arrays.stream(line)
                    .filter(Objects::nonNull)
                    .forEach(block -> {
                        int borderScale = scale - 1;

                        g.setColor(block.getColor());
                        g.fillRect(
                                (block.getX() * scale) + 1,
                                (block.getY() * scale) + 1,
                                borderScale,
                                borderScale
                        );
                    });
        });
    }

    /**
     * Малює ігрову фігурку на полі
     */
    private void drawTetromino(Graphics g) {
        int scale = board.getScale();
        Tetromino tetromino = board.getTetromino();

        tetromino.getBlocks().forEach(block -> {
            int coordinatX = (block.getX() * scale) + 1;
            int coordinatY = (block.getY() * scale) + 1;
            int distanceBetweenBlocks = scale - 1;
            g.setColor(block.getColor());
            g.fillRect(coordinatX, coordinatY, distanceBetweenBlocks, distanceBetweenBlocks);
        });
    }

    /**
     * Малює приціл для ігрової фігурки на полі
     */
    private void drawDropZone(Graphics g) {
        int scale = board.getScale();
        Color color = board.getColor();
        Tetromino tetromino = board.getTetromino();
        List<Tetromino.Block> blocks = tetromino.getBlocks();

        // Створює Y координату для прицілу фігурки
        int height = calculateAimYCoordinate();

        // Малює приціл
        blocks.forEach(block -> {
            int blockX = block.getX() * scale;
            int blockY = (block.getY() + (height - tetromino.getY())) * scale;
            // Малює кольорові блоки прицілу
            g.setColor(block.getColor());
            g.fillRect(
                    blockX + 1,
                    blockY + 1,
                    scale - 1, scale - 1
            );
            // Малює поверх кольорових блоків ще чорні блоки, але меншого розміру, щоб було видно лише краї кольорових блоків
            g.setColor(color);
            g.fillRect(
                    blockX + 2,
                    blockY + 2,
                    scale - 3, scale - 3
            );
        });
    }

    /**
     * Створює Y координату для прицілу фігурки
     */
    private int calculateAimYCoordinate() {
        int height = board.getHeight();
        Tetromino tetromino = board.getTetromino();
        Tetromino.Block[][] allBlocks = board.getAllBlocks();

        int offset = 2;
        int minimumHeightForTetromino = allBlocks.length - offset;
        for (int i = tetromino.getY() + 1; i < height - 1; i++) {
            for (Tetromino.Block block: tetromino.getBlocks()) {

                int y = block.getY() + (i - tetromino.getY());
                int x = block.getX();

                boolean isCoordinatesNotSmall = x >= 0 && y >= 0;
                boolean isCoordinatesNotBig = x < allBlocks[0].length && y < allBlocks.length;

                if (isCoordinatesNotSmall && isCoordinatesNotBig) {
                    if (Objects.nonNull(allBlocks[y][x])) {
                        minimumHeightForTetromino = Math.min(i - 1, minimumHeightForTetromino);
                        break;
                    }
                }
            }
        }

        return minimumHeightForTetromino;
    }

    /**
     * Малює фон ігрового поля
     */
    private void drawBackground(Graphics g) {
        int scale = board.getScale();
        int height = board.getHeight() * scale;
        int width = height;
        Color color = board.getColor();

        g.setColor(color);
        g.fillRect(0, 0, width, height);
    }
}