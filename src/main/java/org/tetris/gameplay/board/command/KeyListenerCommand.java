package org.tetris.gameplay.board.command;

import javafx.scene.input.KeyEvent;
import org.tetris.common.util.concurrency.Command;
import org.tetris.gameplay.board.Board;
import org.tetris.gameplay.board.BoardDTO;
import org.tetris.gameplay.board.BoardEventHandler;
import org.tetris.gameplay.board.BoardViewModel;
import org.tetris.gameplay.tetromino.TetrominoEventHandler;

import java.util.Objects;
import java.util.Queue;

public class KeyListenerCommand implements Command {
    private final BoardEventHandler boardEventHandler;
    private final TetrominoEventHandler tetrominoEventHandler;
    private final Board board;
    private final BoardViewModel boardViewModel;
    private final Queue<KeyEvent> keyEvents;

    public KeyListenerCommand(BoardEventHandler boardEventHandler,
                              TetrominoEventHandler tetrominoEventHandler,
                              Board board,
                              BoardViewModel boardViewModel,
                              Queue<KeyEvent> keyEvents) {
        this.boardEventHandler = boardEventHandler;
        this.tetrominoEventHandler = tetrominoEventHandler;
        this.board = board;
        this.boardViewModel = boardViewModel;
        this.keyEvents = keyEvents;
    }

    @Override
    public void execute() {
        // Отримує дані поля
        BoardDTO boardData = boardViewModel.getBoardData();
        while (board.isOpen()) {
            if (!keyEvents.isEmpty()) {
                KeyEvent event = keyEvents.poll();
                // Обробляє події керування ігровим полем
                boardEventHandler.handle(Objects.requireNonNull(event));

                // Перевіряє чи кінець гри або, чи стоїть гра на паузі
                if (boardData.isOver() || boardData.isPause()) {
                    continue;
                }

                // Обробляє події керування фігуркою
                tetrominoEventHandler.handle(event, boardData);
            }
        }
    }
}