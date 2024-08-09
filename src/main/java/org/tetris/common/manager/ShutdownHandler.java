package org.tetris.common.manager;

import javafx.application.Platform;

public class ShutdownHandler {
    private ShutdownHandler() {

    }

    public static void exit() {
        // Завершення JavaFX Runtime
        Platform.exit();
        // Завершення JVM
        System.exit(0);
    }
}