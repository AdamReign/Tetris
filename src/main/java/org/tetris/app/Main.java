package org.tetris.app;

import javafx.application.Application;
import javafx.stage.Stage;

import org.tetris.common.util.MessageSource;
import org.tetris.common.manager.AppContainer;
import org.tetris.common.config.ConfigInitializer;
import org.tetris.common.manager.ShutdownHandler;
import org.tetris.gameplay.board.BoardConfig;
import org.tetris.gameplay.board.BoardViewModel;

import java.util.Locale;

public class Main extends Application {
    @Override
    public void start(Stage stage) {
        // Create config
        BoardConfig boardConfig = BoardConfig.createInstance();

        // Configs initialization
        ConfigInitializer configInitializer = new ConfigInitializer();
        configInitializer.addConfig(boardConfig);
        configInitializer.initialize();

        // Scene create
        stage.setScene(boardConfig.getScene());

        // Запускає роботу поля гри
        BoardViewModel boardViewModel = AppContainer.resolve(BoardViewModel.class);
        boardViewModel.start();

        // Налаштовує вікно гри
        stageBuilder(stage);
    }

    private void stageBuilder(Stage stage) {
        String title = MessageSource.getMessage("title", Locale.of("ua"));
        stage.setTitle(title);
        stage.setResizable(false);
        // Установка поведінки при закритті вікна
        stage.setOnCloseRequest(event -> ShutdownHandler.exit());
        stage.show();
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}