package org.example.controller;

import org.example.enums.Direction;
import org.example.model.Board;
import org.example.service.TetrominoService;
import org.example.view.BoardView;

import java.awt.event.KeyEvent;
import java.util.Queue;
import java.util.concurrent.CopyOnWriteArrayList;

public class BoardController {
    private final TetrominoService tetrominoService;

    private final Board board;
    private final BoardView boardView;
    private final Queue<KeyEvent> keyEvents;

    public BoardController(Board board, BoardView boardView, TetrominoService tetrominoService, Queue<KeyEvent> keyEvents) {
        this.board = board;
        this.boardView = boardView;
        this.tetrominoService = tetrominoService;
        this.keyEvents = keyEvents;


        board.setTetromino(tetrominoService.getRandom(board.getWidth()/2, 0));
        board.setNextTetromino(tetrominoService.getRandom(board.getWidth()/2, 0));
        board.setGameOver(false);
        board.setPause(false);
    }

    public void keyboard() {
        while (!board.isClose()) {
            // Обробляє натискання клавіш
            keyboardListener();
            repaint();
        }
    }

    public void fall() {
        // Авторух фігурки вниз
        while (!board.isClose()) {
            if (!board.isGameOver() && !board.isPause()) {
                tetrominoService.move(board.getTetromino(), Direction.DOWN);
                // Збільшує швидкість гри в залежності від кількості набраних балів
                board.setSpeed(1000-((board.getScore()/2500)*100) > 200 ? 1000-((board.getScore()/2500)*100) : 300);

                repaint();

                try {
                    Thread.sleep(board.getSpeed());
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public synchronized void repaint() {
        boardView.repaint();
    }

    private void keyboardListener() {
        // "Спостерігач" містить події про натискання клавіш
        if (!hasKeyEvents()) {
            return;
        }

        // Код натиснутої клавіші
        int keyCode = getEventFromTop().getKeyCode();

        switch (keyCode) {
            // Якщо "ESCAPE" - закрити гру
            case KeyEvent.VK_ESCAPE -> board.setClose(true);
            // Якщо "P" - поставити гру на паузу
            case KeyEvent.VK_P -> board.setPause(!board.isGameOver() && !board.isPause());
            // Якщо "ENTER" - створити нову фігурку
            case KeyEvent.VK_ENTER -> {
                board.setTetromino(tetrominoService.getRandom(board.getWidth()/2, 0));
                board.setNextTetromino(tetrominoService.getRandom(board.getWidth()/2, 0));
                board.setAllBlocks(new CopyOnWriteArrayList<>());
                board.setScore(0);
                board.setGameOver(false);
                board.setPause(false);
            }
        }

        // Перевіряє чи кінець гри або, чи стоїть гра на паузі
        if (board.isGameOver() || board.isPause()) {
            return;
        }

        switch (keyCode) {
            // Якщо "стрілка вверх" - прокрутити фігурку
            case KeyEvent.VK_UP, KeyEvent.VK_W -> tetrominoService.spin(board.getTetromino());
            // Якщо "стрілка вправо" - посунути фігурку праворуч
            case KeyEvent.VK_LEFT, KeyEvent.VK_A -> tetrominoService.move(board.getTetromino(), Direction.LEFT);
            // Якщо "стрілка вліво" - посунути фігурку ліворуч
            case KeyEvent.VK_RIGHT, KeyEvent.VK_D -> tetrominoService.move(board.getTetromino(), Direction.RIGHT);
            // Якщо "стрілка вниз" - посунути фігурку вниз
            case KeyEvent.VK_DOWN, KeyEvent.VK_S -> {
                tetrominoService.move(board.getTetromino(), Direction.DOWN);
                board.setScore(board.getScore() + 1);
            }
            // Якщо "SPACE" - моментально опустити фігурку в самий низ
            case KeyEvent.VK_SPACE -> tetrominoService.fall(board.getTetromino());
        }
    }

    private KeyEvent getEventFromTop() {
        return keyEvents.poll();
    }

    private boolean hasKeyEvents() {
        return !keyEvents.isEmpty();
    }
}
