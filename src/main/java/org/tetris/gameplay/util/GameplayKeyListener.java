package org.tetris.gameplay.util;

import javafx.scene.input.KeyEvent;

import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

public class GameplayKeyListener {
    private static GameplayKeyListener instance;
    private final Queue<KeyEvent> keyEvents;

    private GameplayKeyListener() {
        this.keyEvents = new ArrayBlockingQueue<>(1);
    }

    public static GameplayKeyListener createInstance() {
        if (Objects.isNull(instance)) {
            instance = new GameplayKeyListener();
            return instance;
        } else {
            throw new IllegalStateException("GameplayKeyListener has already been created");
        }
    }

    public static GameplayKeyListener getInstance() {
        if (Objects.isNull(instance)) {
            throw new IllegalStateException("GameplayKeyListener has not been created");
        }
        return instance;
    }

    public static boolean isInstanceCreated() {
        return instance != null;
    }

    /**
     * Викликається, коли користувач натискає на клавішу
     * @param e the event to be processed
     */
    public void keyPressed(KeyEvent e) {
        keyEvents.add(e);
    }

    public Queue<KeyEvent> getKeyEvents() {
        return keyEvents;
    }
}