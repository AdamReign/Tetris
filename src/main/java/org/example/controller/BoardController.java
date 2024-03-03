package org.example.controller;

import org.example.enums.Direction;
import org.example.model.Board;
import org.example.model.Tetromino;
import org.example.service.BoardService;
import org.example.service.TetrominoService;
import org.example.view.BoardView;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Queue;

public class BoardController {
    private final TetrominoService TETROMINO_SERVICE;
    private final Board BOARD;
    private final BoardView BOARD_VIEW;
    private final BoardService BOARD_SERVICE;
    private final Queue<KeyEvent> KEY_EVENTS;

    public BoardController(Board board,
                           BoardView boardView,
                           TetrominoService tetrominoService,
                           BoardService boardService,
                           Queue<KeyEvent> keyEvents) {
        this.BOARD = board;
        this.BOARD_VIEW = boardView;
        this.TETROMINO_SERVICE = tetrominoService;
        this.BOARD_SERVICE = boardService;
        this.KEY_EVENTS = keyEvents;

        boardService.initialize();
    }

    public void keyboard() {
        while (!BOARD.isClose()) {
            // Обробляє натискання клавіш
            keyboardListener();
        }
    }

    public void fall() {
        // Авторух фігурки вниз
        while (!BOARD.isClose()) {
            try {
                Thread.sleep(BOARD.getSpeed());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            if (!BOARD.isGameOver() && !BOARD.isPause()) {
                Tetromino tetromino = BOARD.getTetromino();
                Tetromino.Block[][] allBlocks = BOARD.getAllBlocks();
                // Опускає фігурку вниз, та перевіряє чи досягла фігурка перешкоди
                boolean isBottom = TETROMINO_SERVICE.move(tetromino, Direction.DOWN, allBlocks);
                // Перевіряє чи досягла фігурка перешкоди
                if (isBottom) {
                    // Додає блоки фігурки на поле
                    BOARD_SERVICE.addBlocks(tetromino.getBlocks());
                    // Змінює фігурку на наступну
                    BOARD_SERVICE.nextTetromino(TETROMINO_SERVICE.getRandom(BOARD.getWidth() / 2, 0));
                }
                // Перевіряє чи є на полі заповнені лінії
                deleteCompleteLines();

                // Збільшує швидкість гри в залежності від кількості набраних балів
                BOARD_SERVICE.increaseSpeed();
                repaint();
            }
        }
    }

    private void keyboardListener() {
        // "Спостерігач" містить події про натискання клавіш
        if (hasKeyEvents()) {
            // Код натиснутої клавіші
            int keyCode = getEventFromTop().getKeyCode();

            switch (keyCode) {
                // Якщо "ESCAPE" - закрити гру
                case KeyEvent.VK_ESCAPE -> {
                    BOARD_SERVICE.closeGame();
                }
                // Якщо "P" - поставити гру на паузу
                case KeyEvent.VK_P -> {
                    BOARD_SERVICE.pause();
                }
                // Якщо "ENTER" - створити нову фігурку
                case KeyEvent.VK_ENTER -> {
                    BOARD_SERVICE.reset();
                }
                // Якщо "T" - робить скріншот ігрового вікна
                case KeyEvent.VK_T -> {
                    try {
                        // Створення BufferedImage
                        BufferedImage image = new BufferedImage(BOARD_VIEW.getWidth(), BOARD_VIEW.getHeight(), BufferedImage.TYPE_INT_RGB);
                        Graphics2D g2D = image.createGraphics();
                        BOARD_VIEW.print(g2D);
                        g2D.dispose();

                        // Збереження зображення в файл
                        String projectDirectory = System.getProperty("user.dir");
                        String pathForScreenshots = String.format("%s/screenshot", projectDirectory);

                        LocalDateTime localDateTime = LocalDateTime.now();
                        String date = DateTimeFormatter.ofPattern("yyyy-MM-dd").format(localDateTime);
                        String time = DateTimeFormatter.ofPattern("HH-mm-ss").format(localDateTime);
                        String fileName = String.format("Screenshot_D%s_T%s_Tetris", date, time);
                        String fileFormat = "png";
                        String pathToFile = String.format("%s/%s.%s", pathForScreenshots, fileName, fileFormat);

                        // Перевірка наявності файлу та додавання нумерації
                        int fileNumber = 1;
                        String newFileName;
                        while (Files.exists(Paths.get(pathToFile))) {
                            newFileName = String.format("%s_(%d)", fileName, fileNumber++);
                            pathToFile = String.format("%s/%s.%s", pathForScreenshots, newFileName, fileFormat);
                        }

                        // Збереження зображення в файл
                        Path filePath = Paths.get(pathToFile);
                        Files.createDirectories(filePath.getParent());
                        ImageIO.write(image, fileFormat, filePath.toFile());
                        System.out.println("Зображення успішно збережено в " + filePath.toAbsolutePath());
                    } catch (IOException e) {
                        System.err.println("Помилка при збереженні зображення: " + e.getMessage());
                    }
                }
            }

            // Перевіряє чи кінець гри або, чи стоїть гра на паузі
            if (BOARD.isGameOver() || BOARD.isPause()) {
                repaint();
                return;
            } else {
                Tetromino tetromino = BOARD.getTetromino();
                Tetromino.Block[][] allBlocks = BOARD.getAllBlocks();
                switch (keyCode) {
                    // Якщо "стрілка вверх" - прокрутити фігурку
                    case KeyEvent.VK_UP, KeyEvent.VK_W -> {
                        TETROMINO_SERVICE.spin(tetromino, allBlocks);
                    }
                    // Якщо "стрілка вправо" - посунути фігурку праворуч
                    case KeyEvent.VK_LEFT, KeyEvent.VK_A -> {
                        TETROMINO_SERVICE.move(tetromino, Direction.LEFT, allBlocks);
                    }
                    // Якщо "стрілка вліво" - посунути фігурку ліворуч
                    case KeyEvent.VK_RIGHT, KeyEvent.VK_D -> {
                        TETROMINO_SERVICE.move(tetromino, Direction.RIGHT, allBlocks);
                    }
                    // Якщо "стрілка вниз" - посунути фігурку вниз
                    case KeyEvent.VK_DOWN, KeyEvent.VK_S -> {
                        // Опускає фігурку вниз, та перевіряє чи досягла фігурка перешкоди
                        boolean isBottom = TETROMINO_SERVICE.move(tetromino, Direction.DOWN, allBlocks);
                        // Додає один бал за одне натискання кнопки вниз
                        BOARD_SERVICE.increaseScore(1);
                        // Перевіряє чи досягла фігурка перешкоди
                        if (isBottom) {
                            // Додає блоки фігурки на поле
                            BOARD_SERVICE.addBlocks(tetromino.getBlocks());
                            // Змінює фігурку на наступну
                            BOARD_SERVICE.nextTetromino(TETROMINO_SERVICE.getRandom(BOARD.getWidth() / 2, 0));
                        }
                        // Перевіряє чи є на полі заповнені лінії
                        deleteCompleteLines();
                    }
                    // Якщо "SPACE" - моментально опустити фігурку в самий низ
                    case KeyEvent.VK_SPACE -> {
                        // Опускає фігурку вниз до першої перешкоди, та повертає кількість пройденого шляху
                        int score = TETROMINO_SERVICE.fall(tetromino, allBlocks);
                        // Збільшує бали на кількість пройденого шляху
                        BOARD_SERVICE.increaseScore(score);
                        // Додає блоки фігурки на поле
                        BOARD_SERVICE.addBlocks(tetromino.getBlocks());
                        // Змінює фігурку на наступну
                        BOARD_SERVICE.nextTetromino(TETROMINO_SERVICE.getRandom(BOARD.getWidth() / 2, 0));
                        // Перевіряє чи є на полі заповнені лінії
                        deleteCompleteLines();
                    }
                }

                repaint();
            }
        }
    }

    private KeyEvent getEventFromTop() {
        return KEY_EVENTS.poll();
    }

    private boolean hasKeyEvents() {
        return !KEY_EVENTS.isEmpty();
    }

    // Видаляє заповнені лінії
    private void deleteCompleteLines() {
        // Змінює колір блокам на полі, якщо вони склали лінію, позначає їх як видаленими
        if (BOARD_SERVICE.markCompletedLines()) {
            repaint();

            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            // Видаляє блоки з поля, якщо вони позначені видаленими
            BOARD_SERVICE.removeDeletedBlocksAndShiftDownward();
            repaint();
        }
    }

    private synchronized void repaint() {
        SwingUtilities.invokeLater(() -> BOARD_VIEW.paintImmediately(0, 0, BOARD_VIEW.getWidth(), BOARD_VIEW.getHeight()));
    }
}