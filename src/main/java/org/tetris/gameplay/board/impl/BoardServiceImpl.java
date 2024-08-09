package org.tetris.gameplay.board.impl;

import org.tetris.common.manager.ShutdownHandler;
import org.tetris.common.util.concurrency.Command;
import org.tetris.common.util.concurrency.CommandExecutor;
import org.tetris.gameplay.board.Board;
import org.tetris.gameplay.board.BoardDTO;
import org.tetris.gameplay.board.BoardMapper;
import org.tetris.gameplay.board.BoardService;
import org.tetris.gameplay.gamefield.GameField;
import org.tetris.gameplay.gamefield.GameFieldService;
import org.tetris.gameplay.infopanel.InfoPanel;
import org.tetris.gameplay.infopanel.InfoPanelService;
import org.tetris.gameplay.tetromino.Tetromino;

import java.util.List;
import java.util.Objects;

public class BoardServiceImpl implements BoardService {
    private static BoardServiceImpl instance;
    private final Board board;
    private final GameFieldService gameFieldService;
    private final InfoPanelService infoPanelService;
    private final List<Command> commands;
    private CommandExecutor commandExecutor;

    private BoardServiceImpl(Board board,
                             GameFieldService gameFieldService,
                             InfoPanelService infoPanelService,
                             List<Command> commands) {
        this.board = board;
        this.gameFieldService = gameFieldService;
        this.infoPanelService = infoPanelService;
        this.commands = commands;
    }

    public static BoardServiceImpl createInstance(Board board,
                                                  GameFieldService gameFieldService,
                                                  InfoPanelService infoPanelService,
                                                  List<Command> commands) {
        if (Objects.isNull(instance)) {
            instance = new BoardServiceImpl(
                    board,
                    gameFieldService,
                    infoPanelService,
                    commands
            );
            return instance;
        } else {
            throw new IllegalStateException("BoardServiceImpl has already been created");
        }
    }

    public static BoardServiceImpl getInstance() {
        if (Objects.isNull(instance)) {
            throw new IllegalStateException("BoardServiceImpl has not been created");
        }
        return instance;
    }

    public static boolean isInstanceCreated() {
        return instance != null;
    }

    @Override
    public void start() {
        // Thread
        if (Objects.isNull(commandExecutor)) {
            commandExecutor = new CommandExecutor(commands.size());
        } else {
            commandExecutor.shutdownNow();
            commandExecutor = new CommandExecutor(commands.size());
        }
        commandExecutor.execute(commands);
        commandExecutor.shutdown();
    }

    @Override
    public void stop() {
        if (Objects.nonNull(commandExecutor)) {
            commandExecutor.shutdownNow();
        }
    }

    @Override
    public boolean isRunning() {
        if (Objects.nonNull(commandExecutor)) {
            return !commandExecutor.isRunning();
        } else {
            return false;
        }
    }

    @Override
    public void initialize() {
        gameFieldService.initialize();
        Tetromino nextTetromino = board.getGameField().getNextTetromino();
        infoPanelService.setImageNextTetromino(nextTetromino.getType());

        board.setOver(false);
        board.setPause(false);
        board.setOpen(true);
    }

    @Override
    public void reset() {
        // Очищує поле і повторно ініціалізує
        initialize();

        InfoPanel infoPanel = board.getInfoPanel();
        GameField gameField = board.getGameField();

        Tetromino.Block[][] emptyGrid = new Tetromino.Block[gameField.getHeight()][gameField.getWidth()];
        gameField.setGrid(emptyGrid);

        infoPanel.setScore(0);
    }

    @Override
    public void pause() {
        boolean isNotOver = !board.isOver();
        boolean isPause = board.isPause();

        board.setPause(isNotOver && !isPause);
    }

    @Override
    public void closeGame() {
        board.setOpen(false);
        ShutdownHandler.exit();
    }

    @Override
    public void gameOver() {
        board.setOver(true);
    }

    @Override
    public void increaseScore(int count) {
        if (count < 0) {
            throw new RuntimeException("Не можна збільшити бали на від'ємне число");
        }

        InfoPanel infoPanel = board.getInfoPanel();
        int currentScore = infoPanel.getScore();
        infoPanel.setScore(currentScore + count);
    }

    @Override
    public void increaseFallingSpeed() {
        InfoPanel infoPanel = board.getInfoPanel();
        GameField gameField = board.getGameField();

        int currentFallingSpeed = 1000 - ((infoPanel.getScore() / 2500) * 100);
        gameField.setFallingSpeed(currentFallingSpeed > 200 ? currentFallingSpeed : 300);
    }

    @Override
    public void nextTetromino() {
        gameFieldService.nextTetromino();
        GameField gameField = board.getGameField();
        Tetromino nextTetromino = gameField.getNextTetromino();
        infoPanelService.setImageNextTetromino(nextTetromino.getType());
    }

    @Override
    public void addBlocks(List<Tetromino.Block> blocks) {
        gameFieldService.addBlocks(blocks);
    }

    @Override
    public void deleteCompleteLines() {
        int oldScore = board.getInfoPanel().getScore();
        int newScore = oldScore + gameFieldService.deleteCompleteLines();
        board.getInfoPanel().setScore(newScore);
    }

    @Override
    public BoardDTO getBoardData() {
        return BoardMapper.toDTO(board);
    }
}