package org.tetris.gameplay.board;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import org.tetris.common.manager.AppContainer;
import org.tetris.common.config.Config;
import org.tetris.common.util.concurrency.Command;
import org.tetris.gameplay.board.command.KeyListenerCommand;
import org.tetris.gameplay.board.command.TetrominoFallCommand;
import org.tetris.gameplay.board.impl.BoardViewModelImpl;
import org.tetris.gameplay.board.impl.BoardServiceImpl;
import org.tetris.gameplay.board.impl.BoardViewImpl;
import org.tetris.gameplay.gamefield.GameField;
import org.tetris.gameplay.gamefield.GameFieldService;
import org.tetris.gameplay.gamefield.impl.GameFieldServiceImpl;
import org.tetris.gameplay.infopanel.InfoPanel;
import org.tetris.gameplay.infopanel.InfoPanelService;
import org.tetris.gameplay.infopanel.impl.InfoPanelServiceImpl;
import org.tetris.gameplay.tetromino.TetrominoEventHandler;
import org.tetris.gameplay.tetromino.TetrominoViewModel;
import org.tetris.gameplay.tetromino.TetrominoRepository;
import org.tetris.gameplay.tetromino.TetrominoService;
import org.tetris.gameplay.tetromino.impl.TetrominoViewModelImpl;
import org.tetris.gameplay.tetromino.impl.TetrominoRepositoryImpl;
import org.tetris.gameplay.tetromino.impl.TetrominoServiceImpl;
import org.tetris.gameplay.util.GameplayKeyListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BoardConfig implements Config {
    private static BoardConfig instance;

    private final int WIDTH_GRID = 17;
    private final int HEIGHT_GRID = 22;
    private final int WIDTH_INFO_PANEL = 5;
    private final int HEIGHT_BOARD = 22;
    private final int BLOCK_SIZE = 32;

    private GameField gameField;
    private InfoPanel infoPanel;
    private Board model;

    private TetrominoRepository tetrominoRepository;

    private TetrominoService tetrominoService;
    private GameFieldService gameFieldService;
    private InfoPanelService infoPanelService;
    private BoardService service;

    private BoardViewModel viewModel;
    private TetrominoViewModel tetrominoViewModel;

    private BoardView view;

    private BoardEventHandler eventsHandler;
    private TetrominoEventHandler tetrominoEventHandler;
    private GameplayKeyListener keyListener;

    private List<Command> commands;

    private Scene scene;

    private BoardConfig() {
    }

    public static BoardConfig createInstance() {
        if (Objects.isNull(instance)) {
            instance = new BoardConfig();
            return instance;
        } else {
            throw new IllegalStateException("BoardConfig has already been created");
        }
    }

    public static BoardConfig getInstance() {
        if (Objects.isNull(instance)) {
            throw new IllegalStateException("BoardConfig has not been created");
        }
        return instance;
    }

    public static boolean isInstanceCreated() {
        return instance != null;
    }

    @Override
    public void initialize() {
        // Models
        initModel();
        AppContainer.register(GameField.class, gameField);
        AppContainer.register(InfoPanel.class, infoPanel);
        AppContainer.register(Board.class, model);

        // Repositories
        initRepository();
        AppContainer.register(TetrominoRepository.class, tetrominoRepository);

        // Services
        initService();
        AppContainer.register(TetrominoService.class, tetrominoService);
        AppContainer.register(InfoPanelService.class, infoPanelService);
        AppContainer.register(BoardService.class, service);

        // ViewModels
        initViewModel();
        AppContainer.register(BoardViewModel.class, viewModel);
        AppContainer.register(TetrominoViewModel.class, tetrominoViewModel);

        // Views
        initView();
        AppContainer.register(BoardView.class, view);

        // KeyListener
        initKeyListener();
        AppContainer.register(BoardEventHandler.class, eventsHandler);
        AppContainer.register(TetrominoEventHandler.class, tetrominoEventHandler);
        AppContainer.register(GameplayKeyListener.class, keyListener);

        // Commands
        initCommands();

        //Scene
        initScene();
    }

    private void initModel() {
        gameField = GameField.createInstance(
                WIDTH_GRID,
                HEIGHT_GRID
        );

        int widthInfoPanel = WIDTH_INFO_PANEL * BLOCK_SIZE;
        int heightInfoPanel = HEIGHT_BOARD * BLOCK_SIZE;
        infoPanel = InfoPanel.createInstance(
                widthInfoPanel,
                heightInfoPanel
        );

        int widthBoard = (WIDTH_GRID + WIDTH_INFO_PANEL) * BLOCK_SIZE;
        int heightBoard = HEIGHT_BOARD * BLOCK_SIZE;
        model = Board.createInstance(
                widthBoard,
                heightBoard,
                BLOCK_SIZE,
                gameField,
                infoPanel
        );
    }

    private void initRepository() {
        tetrominoRepository = TetrominoRepositoryImpl.createInstance();
    }

    private void initService() {
        tetrominoService = TetrominoServiceImpl.createInstance(
                model,
                tetrominoRepository
        );
        gameFieldService = GameFieldServiceImpl.createInstance(
                gameField,
                tetrominoService
        );
        infoPanelService = InfoPanelServiceImpl.createInstance(infoPanel);
        commands = new ArrayList<>();
        service = BoardServiceImpl.createInstance(
                model,
                gameFieldService,
                infoPanelService,
                commands
        );
    }

    private void initViewModel() {
        viewModel = BoardViewModelImpl.createInstance(service);
        tetrominoViewModel = TetrominoViewModelImpl.createInstance(tetrominoService);
    }

    private void initView() {
        view = BoardViewImpl.createInstance(viewModel, tetrominoViewModel);
    }

    private void initKeyListener() {
        eventsHandler = BoardEventHandler.createInstance(
                viewModel,
                view
        );
        tetrominoEventHandler = TetrominoEventHandler.createInstance(
                viewModel,
                tetrominoViewModel
        );

        keyListener = GameplayKeyListener.createInstance();
    }

    private void initCommands() {
        commands.add(new TetrominoFallCommand(
                model,
                viewModel,
                tetrominoViewModel
        ));
        commands.add(new KeyListenerCommand(
                eventsHandler,
                tetrominoEventHandler,
                model,
                viewModel,
                keyListener.getKeyEvents()
        ));
    }

    private void initScene() {
        int widthBoard = (WIDTH_GRID + WIDTH_INFO_PANEL) * BLOCK_SIZE;
        int heightBoard = HEIGHT_BOARD * BLOCK_SIZE;
        scene = new Scene(
                (Group) view,
                widthBoard,
                heightBoard
        );
        scene.setFill(model.getColor());
        // Змінює дані моделі
        scene.addEventHandler(KeyEvent.KEY_PRESSED, keyListener::keyPressed);
    }

    public Scene getScene() {
        return scene;
    }
}