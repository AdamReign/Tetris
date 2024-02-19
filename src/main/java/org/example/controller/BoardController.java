package org.example.controller;

import org.example.enums.Direction;
import org.example.service.TetrominoService;
import org.example.view.BoardView;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Queue;

public class BoardController {
    private TetrominoService tetrominoService;
    private BoardView boardView;
    private Queue<KeyEvent> keyEvents;

    public BoardController(BoardView boardView, TetrominoService tetrominoService, Queue<KeyEvent> keyEvents) {
        this.boardView = boardView;
        this.tetrominoService = tetrominoService;
        this.keyEvents = keyEvents;


        boardView.setTetromino(tetrominoService.getRandom(boardView.getWidthBoard()/2, 0));
        boardView.setNextTetromino(tetrominoService.getRandom(boardView.getWidthBoard()/2, 0));
        boardView.setGameOver(false);
        boardView.setPause(false);
    }

    public void keyboard() {
        while (!boardView.isClose()) {
            // Обробляє натискання клавіш
            keyboardListener();
            boardView.repaint();
        }
    }

    public void fall() {
        // Авторух фігурки вниз
        while (!boardView.isClose()) {
            if (!boardView.isGameOver() && !boardView.isPause()) {
                tetrominoService.move(boardView.getTetromino(), Direction.DOWN);
                // Збільшує швидкість гри в залежності від кількості набраних очків
                boardView.setSpeed(1000-((boardView.getScore()/2500)*100) > 200 ? 1000-((boardView.getScore()/2500)*100) : 300);
                boardView.repaint();

                try {
                    Thread.sleep(boardView.getSpeed());
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
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
            case KeyEvent.VK_ESCAPE -> boardView.setClose(true);
            // Якщо "P" - поставити гру на паузу
            case KeyEvent.VK_P -> boardView.setPause(!boardView.isGameOver() && !boardView.isPause());
            // Якщо "ENTER" - створити нову фігурку
            case KeyEvent.VK_ENTER -> {
                boardView.setTetromino(tetrominoService.getRandom(boardView.getWidthBoard()/2, 0));
                boardView.setNextTetromino(tetrominoService.getRandom(boardView.getWidthBoard()/2, 0));
                boardView.setAllBlocks(new ArrayList<>());
                boardView.setScore(0);
                boardView.setGameOver(false);
                boardView.setPause(false);
            }
        }

        // Перевіряє чи кінець гри або чи стоїть гра на паузі
        if (boardView.isGameOver() || boardView.isPause()) {
            return;
        }

        switch (keyCode) {
            // Якщо "стрілка вверх" - прокрутити фігурку
            case KeyEvent.VK_UP, KeyEvent.VK_W -> tetrominoService.spin(boardView.getTetromino());
            // Якщо "стрілка вправо" - посунути фігурку праворуч
            case KeyEvent.VK_LEFT, KeyEvent.VK_A -> tetrominoService.move(boardView.getTetromino(), Direction.LEFT);
            // Якщо "стрілка вліво" - посунути фігурку ліворуч
            case KeyEvent.VK_RIGHT, KeyEvent.VK_D -> tetrominoService.move(boardView.getTetromino(), Direction.RIGHT);
            // Якщо "стрілка вниз" - посунути фігурку вниз
            case KeyEvent.VK_DOWN, KeyEvent.VK_S -> {
                tetrominoService.move(boardView.getTetromino(), Direction.DOWN);
                boardView.setScore(boardView.getScore() + 1);
            }
            // Якщо "SPACE" - моментально опустити фігурку в самий низ
            case KeyEvent.VK_SPACE -> tetrominoService.fall(boardView.getTetromino());
        }
    }

    private KeyEvent getEventFromTop() {
        return keyEvents.poll();
    }

    private boolean hasKeyEvents() {
        return !keyEvents.isEmpty();
    }
}
