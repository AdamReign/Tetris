package org.tetris.gameplay.infopanel;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.image.Image;

import java.util.Objects;

public class InfoPanel {
    private static InfoPanel instance;

    private final int WIDTH;
    private final int HEIGHT;

    private ObjectProperty<Image> imageNextTetromino;
    private final IntegerProperty score;

    private InfoPanel(int width, int height) {
        this.WIDTH = width;
        this.HEIGHT = height;

        imageNextTetromino  = new SimpleObjectProperty<>();
        score = new SimpleIntegerProperty(0);
    }

    public static InfoPanel createInstance(int width, int height) {
        if (Objects.isNull(instance)) {
            instance = new InfoPanel(width, height);
            return instance;
        } else {
            throw new IllegalStateException("InfoPanel has already been created");
        }
    }

    public static InfoPanel getInstance() {
        if (Objects.isNull(instance)) {
            throw new IllegalStateException("InfoPanel has not been created");
        }
        return instance;
    }

    public static boolean isInstanceCreated() {
        return instance != null;
    }

    public int getWidth() {
        return WIDTH;
    }

    public int getHeight() {
        return HEIGHT;
    }

    public Image getImageNextTetromino() {
        return new Image(imageNextTetromino.get().getUrl());
    }

    public int getScore() {
        return score.get();
    }

    public synchronized void setImageNextTetromino(Image image) {
        this.imageNextTetromino.set(new Image(image.getUrl()));
    }

    public synchronized void setScore(int score) {
        this.score.set(score);
    }

    public ObjectProperty<Image> imageProperty() {
        return imageNextTetromino;
    }

    public IntegerProperty scoreProperty() {
        return score;
    }
}