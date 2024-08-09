package org.tetris.gameplay.board;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.util.Objects;

public class BoardEventHandler {
    private static BoardEventHandler instance;
    private final BoardViewModel boardViewModel;
    private final BoardView boardView;

    private BoardEventHandler(BoardViewModel boardViewModel, BoardView boardView) {
        this.boardViewModel = boardViewModel;
        this.boardView = boardView;
    }

    public static BoardEventHandler createInstance(BoardViewModel boardViewModel, BoardView boardView) {
        if (Objects.isNull(instance)) {
            instance = new BoardEventHandler(boardViewModel, boardView);
            return instance;
        } else {
            throw new IllegalStateException("BoardEventsHandler has already been created");
        }
    }

    public static BoardEventHandler getInstance() {
        if (Objects.isNull(instance)) {
            throw new IllegalStateException("BoardEventsHandler has not been created");
        }
        return instance;
    }

    public static boolean isInstanceCreated() {
        return instance != null;
    }

    /**
     * Обробляє події керування ігровим полем
     *
     * @param e - код натиснутої клавіші клавіатури
     */
    public void handle(KeyEvent e) {
        // Код натиснутої клавіші
        KeyCode keyCode = e.getCode();
        switch (keyCode) {
            // Якщо "ESCAPE" - закрити гру
            case ESCAPE -> {
                boardViewModel.closeGame();
            }
            // Якщо "P" - поставити гру на паузу
            case P -> {
                boardViewModel.pause();
            }
            // Якщо "ENTER" - створити нову фігурку
            case ENTER -> {
                boardViewModel.reset();
            }
            // Якщо "T" - робить скріншот ігрового вікна
            case T -> {
                Scene scene = ((Group) boardView).getScene();
                boardViewModel.screenshot(scene);
            }
        }
    }
}