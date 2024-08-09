package org.tetris.gameplay.board.impl;

import javafx.scene.Scene;

import org.tetris.common.manager.ScreenshotManager;
import org.tetris.gameplay.board.BoardDTO;
import org.tetris.gameplay.board.BoardService;
import org.tetris.gameplay.board.BoardViewModel;
import org.tetris.gameplay.tetromino.Tetromino;

import java.util.List;
import java.util.Objects;

public class BoardViewModelImpl implements BoardViewModel {
    private static BoardViewModelImpl instance;
    private final BoardService boardService;

    private BoardViewModelImpl(BoardService boardService) {
        this.boardService = boardService;

        this.boardService.initialize();
    }

    public static BoardViewModelImpl createInstance(BoardService boardService) {
        if (Objects.isNull(instance)) {
            instance = new BoardViewModelImpl(boardService);
            return instance;
        } else {
            throw new IllegalStateException("BoardViewModelImpl has already been created");
        }
    }

    public static BoardViewModelImpl getInstance() {
        if (Objects.isNull(instance)) {
            throw new IllegalStateException("BoardViewModelImpl has not been created");
        }
        return instance;
    }

    public static boolean isInstanceCreated() {
        return instance != null;
    }

    @Override
    public void start() {
        boardService.start();
    }

    @Override
    public void stop() {
        boardService.stop();
    }

    @Override
    public BoardDTO getBoardData() {
        return boardService.getBoardData();
    }

    @Override
    public void closeGame() {
        boardService.closeGame();
    }

    @Override
    public void pause() {
        boardService.pause();
    }

    @Override
    public void reset() {
        boardService.reset();
    }

    @Override
    public void gameOver() {
        boardService.gameOver();
    }

    @Override
    public void screenshot(Scene view) {
        ScreenshotManager.screenshot(view);
    }

    @Override
    public void increaseScore(int count) {
        boardService.increaseScore(count);
    }

    @Override
    public void increaseSpeed() {
        boardService.increaseFallingSpeed();
    }

    @Override
    public void addTetrominoBlocksOnGrid(List<Tetromino.Block> blocks) {
        boardService.addBlocks(blocks);
    }

    @Override
    public void nextTetromino() {
        boardService.nextTetromino();
    }

    /**
     * Видаляє заповнені лінії
     */
    @Override
    public void deleteCompleteLines() {
        boardService.deleteCompleteLines();
    }
}