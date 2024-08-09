package org.tetris.gameplay.board.impl;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import org.tetris.common.util.MessageSource;
import org.tetris.common.util.concurrency.Command;
import org.tetris.gameplay.board.BoardDTO;
import org.tetris.gameplay.board.BoardView;
import org.tetris.gameplay.board.BoardViewModel;
import org.tetris.gameplay.gamefield.GameField;
import org.tetris.gameplay.infopanel.InfoPanel;
import org.tetris.gameplay.tetromino.Tetromino;
import org.tetris.gameplay.tetromino.TetrominoViewModel;
import org.tetris.gameplay.tetromino.enums.Position;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * Відображає ігрове поле
 */
public class BoardViewImpl extends Group implements BoardView {
    private static BoardViewImpl instance;

    private final BoardViewModel boardViewModel;
    private final TetrominoViewModel tetrominoViewModel;

    private final Canvas canvas;
    private final ChangeListener<Position> positionListener;
    private final ChangeListener<List<Tetromino.Block>> blocksListener;

    private Tetromino currentTetromino;
    private BoardDTO boardData;

    private BoardViewImpl(BoardViewModel boardViewModel,
                          TetrominoViewModel tetrominoViewModel) {
        this.boardViewModel = boardViewModel;
        this.tetrominoViewModel = tetrominoViewModel;

        boardData = boardViewModel.getBoardData();
        GameField gameField = boardData.getGameField();
        currentTetromino = gameField.getTetromino();

        int height = boardData.getHeight();
        int width = boardData.getWidth();

        canvas = new Canvas(width, height);
        this.getChildren().add(canvas);

        Command command = this::paint;
        positionListener = (observable, oldValue, newValue) -> command.execute();
        blocksListener = (observable, oldValue, newValue) -> command.execute();
        addListeners(command);

        paint();
    }

    private void addListeners(Command command) {
        InfoPanel infoPanel = boardData.getInfoPanel();
        GameField gameField = boardData.getGameField();

        infoPanel.scoreProperty().addListener((observable, oldValue, newValue) -> command.execute());
        gameField.tetrominoProperty().addListener((observable, oldValue, newValue) -> command.execute());
        gameField.nextTetrominoProperty().addListener((observable, oldValue, newValue) -> command.execute());
        gameField.fallingSpeedProperty().addListener((observable, oldValue, newValue) -> command.execute());
        boardData.openProperty().addListener((observable, oldValue, newValue) -> command.execute());
        boardData.overProperty().addListener((observable, oldValue, newValue) -> command.execute());
        boardData.pauseProperty().addListener((observable, oldValue, newValue) -> command.execute());

        gameField.getTetromino().positionProperty().addListener(positionListener);
        gameField.getTetromino().blocksProperty().addListener(blocksListener);
    }

    public static BoardViewImpl createInstance(BoardViewModel boardViewModel,
                                               TetrominoViewModel tetrominoViewModel) {
        if (Objects.isNull(instance)) {
            instance = new BoardViewImpl(boardViewModel, tetrominoViewModel);
            return instance;
        } else {
            throw new IllegalStateException("BoardViewImpl has already been created");
        }
    }

    public static BoardViewImpl getInstance() {
        if (Objects.isNull(instance)) {
            throw new IllegalStateException("BoardViewImpl has not been created");
        }
        return instance;
    }

    public static boolean isInstanceCreated() {
        return instance != null;
    }

    /**
     * Відображає на екрані будь-які задані елементи
     */
    @Override
    public void paint() {
        Platform.runLater(() -> {
            propertyChange();

            drawBackground();

            drawDropZone();

            drawTetromino();

            drawBlocksOnBoard();

            drawBorders();

            drawCoordinates();

            drawNextTetromino();

            drawScore();

            displayPauseMessage();

            displayGameOverMessage();
        });
    }

    /**
     * Оновлює слухачі та відв'язує їх від старих об'єктів.
     */
    private void propertyChange() {
        Tetromino oldTetromino = currentTetromino;
        GameField gameField = boardData.getGameField();
        Tetromino currentTetromino = gameField.getTetromino();

        if (!Objects.equals(currentTetromino, oldTetromino)) {
            oldTetromino.positionProperty().removeListener(positionListener);
            oldTetromino.blocksProperty().removeListener(blocksListener);

            currentTetromino.positionProperty().addListener(positionListener);
            currentTetromino.blocksProperty().addListener(blocksListener);

            this.currentTetromino = currentTetromino;
        }
    }

    /**
     * Малює фон ігрового поля
     */
    private void drawBackground() {
        GraphicsContext context = canvas.getGraphicsContext2D();

        int height = boardData.getHeight();
        int width = boardData.getWidth();
        Color color = boardData.getColor();

        context.setFill(color);
        context.fillRect(0, 0, width, height);
    }

    /**
     * Малює приціл для ігрової фігурки на полі
     */
    private void drawDropZone() {
        GraphicsContext context = canvas.getGraphicsContext2D();

        int blockSize = boardData.getBlockSize();
        Color color = boardData.getColor();
        GameField gameField = boardData.getGameField();
        Tetromino tetromino = gameField.getTetromino();
        List<Tetromino.Block> blocks = tetromino.getBlocks();

        // Створює Y координату для прицілу фігурки
        int height = tetrominoViewModel.calculateAimYCoordinate();

        // Малює приціл
        blocks.forEach(block -> {
            int blockX = block.getX() * blockSize;
            int blockY = (block.getY() + (height - tetromino.getY())) * blockSize;
            // Малює кольорові блоки прицілу
            int outerBlockSize = blockSize - 1;
            int outerBlockX = blockX + 1;
            int outerBlockY = blockY + 1;
            context.setFill(block.getColor());
            context.fillRect(
                    outerBlockX,
                    outerBlockY,
                    outerBlockSize,
                    outerBlockSize
            );
            // Малює поверх кольорових блоків ще чорні блоки,
            // але меншого розміру, щоб було видно лише краї кольорових блоків
            int innerBlockSize = blockSize - 3;
            int innerBlockX = blockX + 2;
            int innerBlockY = blockY + 2;
            context.setFill(color);
            context.fillRect(
                    innerBlockX,
                    innerBlockY,
                    innerBlockSize,
                    innerBlockSize
            );
        });
    }

    /**
     * Малює ігрову фігурку на полі
     */
    private void drawTetromino() {
        GraphicsContext context = canvas.getGraphicsContext2D();

        int blockSize = boardData.getBlockSize();
        GameField gameField = boardData.getGameField();
        Tetromino tetromino = gameField.getTetromino();

        int spaceSizeBetweenBlocks = blockSize - 1;
        tetromino.getBlocks().forEach(block -> {
            int coordinateX = (block.getX() * blockSize) + 1;
            int coordinateY = (block.getY() * blockSize) + 1;
            Color color = block.getColor();
            context.setFill(color);
            context.fillRect(
                    coordinateX,
                    coordinateY,
                    spaceSizeBetweenBlocks,
                    spaceSizeBetweenBlocks
            );
        });
    }

    /**
     * Малює всі блоки grid на ігровому полі
     */
    private void drawBlocksOnBoard() {
        GraphicsContext context = canvas.getGraphicsContext2D();

        int blockSize = boardData.getBlockSize();
        GameField gameField = boardData.getGameField();
        Tetromino.Block[][] grid = gameField.getGrid();

        int spaceSizeBetweenBlocks = blockSize - 1;
        Arrays.stream(grid).forEach(line -> Arrays.stream(line)
                .filter(Objects::nonNull)
                .forEach(block -> {
                    int coordinateX = (block.getX() * blockSize) + 1;
                    int coordinateY = (block.getY() * blockSize) + 1;
                    Color color = block.getColor();
                    context.setFill(color);
                    context.fillRect(
                            coordinateX,
                            coordinateY,
                            spaceSizeBetweenBlocks,
                            spaceSizeBetweenBlocks
                    );
                }));
    }

    /**
     * Малює кордони ігрового поля
     */
    private void drawBorders() {
        GraphicsContext context = canvas.getGraphicsContext2D();

        Image image = boardData.getImage();
        context.drawImage(image, 0, 0);
    }

    /**
     * Відображає координати фігурки
     */
    private void drawCoordinates() {
        GraphicsContext context = canvas.getGraphicsContext2D();

        int messageCoordinateX = 1;
        int messageCoordinateY = 1;
        int blockSize = boardData.getBlockSize();
        GameField gameField = boardData.getGameField();
        Tetromino tetromino = gameField.getTetromino();

        // Налаштування шрифту та графіки для відображення координат фігурки
        String style = "Bitstream Charter";
        FontWeight weight = FontWeight.BOLD;
        int size = 20;
        Font font = Font.font(style, weight, size);
        context.setFont(font);
        context.setFill(Color.YELLOW);

        // Координати для відображення тексту з координатами фігурки
        int textX = (messageCoordinateX * blockSize) + 5; // Координата X для тексту з координатами
        int textY = (messageCoordinateY * blockSize) + 25; // Координата Y для тексту з координатами

        // Відображення тексту з координатами фігурки
        String coordinatesText = String.format(
                "X: %d Y: %d",
                tetromino.getX(),
                tetromino.getY()
        );
        context.fillText(
                coordinatesText,
                textX,
                textY
        );
    }

    /**
     * Малює в інформаційному блоці яка буде наступна фігурка
     */
    private void drawNextTetromino() {
        GraphicsContext context = canvas.getGraphicsContext2D();
        InfoPanel infoPanel = boardData.getInfoPanel();
        int blockSize = boardData.getBlockSize();
        int widthBoard = boardData.getWidth();
        int widthInfoPanel = infoPanel.getWidth();

        int coordinateX = widthBoard - widthInfoPanel;
        int coordinateY = blockSize;
        Image imageTetromino = infoPanel.getImageNextTetromino();
        context.drawImage(
                imageTetromino,
                coordinateX,
                coordinateY
        );
    }

    /**
     * Відображає в інформаційній частині ігрового поля бали, які гравець набрав за гру
     */
    private void drawScore() {
        GraphicsContext context = canvas.getGraphicsContext2D();
        InfoPanel infoPanel = boardData.getInfoPanel();

        int messageCoordinateY = 8;
        int blockSize = boardData.getBlockSize();
        int widthBoard = boardData.getWidth();
        int widthInfoPanel = infoPanel.getWidth();
        String score = String.format("%06d", infoPanel.getScore());

        // Налаштування шрифту та графіки для відображення балів
        String style = "Bitstream Charter";
        FontWeight weight = FontWeight.BOLD;
        int size = 29;
        Font font = Font.font(style, weight, size);
        context.setFont(font);
        context.setFill(Color.BLACK);
        // Відображення кількості балів
        int coordinateX = (widthBoard - widthInfoPanel) + 15;
        int coordinateY = (messageCoordinateY * blockSize) + 10;
        context.fillText(
                score,
                coordinateX,
                coordinateY
        );
    }

    /**
     * Виводить на екран текст "Пауза", коли гра ставиться на паузу
     */
    private void displayPauseMessage() {
        GraphicsContext context = canvas.getGraphicsContext2D();

        int messageCoordinateX = 7;
        int blockSize = boardData.getBlockSize();
        int height = boardData.getHeight();
        boolean isNotOver = !boardData.isOver();
        boolean isPause = boardData.isPause();

        if (isNotOver && isPause) {
            int outerRectangleCoordinateX = 217;
            int outerRectangleCoordinateY = 310;
            int outerRectangleWidth = 130;
            int outerRectangleHeight = 60;
            Color outerRectangleColor = Color.YELLOW;
            context.setFill(outerRectangleColor);
            context.fillRect(
                    outerRectangleCoordinateX,
                    outerRectangleCoordinateY,
                    outerRectangleWidth,
                    outerRectangleHeight
            );

            int innerRectangleCoordinateX = outerRectangleCoordinateX + 2;
            int innerRectangleCoordinateY = outerRectangleCoordinateY + 2;
            int innerRectangleWidth = outerRectangleWidth - 4;
            int innerRectangleHeight = outerRectangleHeight - 4;
            Color innerRectangleColor = Color.BLACK;
            context.setFill(innerRectangleColor);
            context.fillRect(
                    innerRectangleCoordinateX,
                    innerRectangleCoordinateY,
                    innerRectangleWidth,
                    innerRectangleHeight
            );

            String style = "Bitstream Charter";
            FontWeight weight = FontWeight.BOLD;
            int size = 40;
            Font font = Font.font(style, weight, size);
            context.setFont(font);

            context.setFill(Color.YELLOW);

            String message = MessageSource.getMessage("pause", Locale.of("ua"));
            int coordinateX = messageCoordinateX * blockSize;
            int coordinateY = height / 2;
            context.fillText(
                    message,
                    coordinateX,
                    coordinateY
            );
        }
    }

    /**
     * Виводить на екран "Кінець гри", коли гру закінчено
     */
    private void displayGameOverMessage() {
        GraphicsContext context = canvas.getGraphicsContext2D();

        int messageCoordinateX = 5;
        int blockSize = boardData.getBlockSize();
        int height = boardData.getHeight();
        GameField gameField = boardData.getGameField();
        Tetromino.Block[][] grid = gameField.getGrid();

        // TODO Можливо ця команда має бути в треді TetrominoFallCommand
        boolean isBlockTouchingCeiling = Arrays.stream(grid[1]).anyMatch(Objects::nonNull);

        if (isBlockTouchingCeiling) {

            // TODO Прибрати цю команду в BoardService, а залишити лише малювання
            boardViewModel.gameOver();

            int outerRectangleCoordinateX = 150;
            int outerRectangleCoordinateY = 310;
            int outerRectangleWidth = 230;
            int outerRectangleHeight = 60;
            Color outerRectangleColor = Color.YELLOW;
            context.setFill(outerRectangleColor);
            context.fillRect(
                    outerRectangleCoordinateX,
                    outerRectangleCoordinateY,
                    outerRectangleWidth,
                    outerRectangleHeight
            );

            int innerRectangleCoordinateX = outerRectangleCoordinateX + 2;
            int innerRectangleCoordinateY = outerRectangleCoordinateY + 2;
            int innerRectangleWidth = outerRectangleWidth - 4;
            int innerRectangleHeight = outerRectangleHeight - 4;
            Color innerRectangleColor = Color.BLACK;
            context.setFill(innerRectangleColor);
            context.fillRect(
                    innerRectangleCoordinateX,
                    innerRectangleCoordinateY,
                    innerRectangleWidth,
                    innerRectangleHeight
            );

            String style = "Bitstream Charter";
            FontWeight weight = FontWeight.BOLD;
            int size = 40;
            Font font = Font.font(style, weight, size);
            context.setFont(font);

            context.setFill(Color.YELLOW);

            String message = MessageSource.getMessage("gameOver", Locale.of("ua"));
            int coordinateX = messageCoordinateX * blockSize;
            int coordinateY = height / 2;
            context.fillText(
                    message,
                    coordinateX,
                    coordinateY
            );
        }
    }
}