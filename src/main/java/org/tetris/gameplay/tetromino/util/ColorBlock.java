package org.tetris.gameplay.tetromino.util;

import javafx.scene.paint.Color;

public class ColorBlock {
    public static final Color LIGHTGRAY = Color.rgb(192, 192, 192);
    public static final Color CYAN = Color.rgb(0, 255, 255);
    public static final Color BLUE = Color.rgb(0, 0, 255);
    public static final Color ORANGE = Color.rgb(255, 200, 0);
    public static final Color YELLOW = Color.rgb(255, 255, 0);
    public static final Color GREEN = Color.rgb(0, 255, 0);
    public static final Color MAGENTA = Color.rgb(255, 0, 255);
    public static final Color RED = Color.rgb(255, 0, 0);

    private ColorBlock() {

    }

    public static Color rgb(int red, int green, int blue) {
        return Color.rgb(red, green, blue);
    }
}