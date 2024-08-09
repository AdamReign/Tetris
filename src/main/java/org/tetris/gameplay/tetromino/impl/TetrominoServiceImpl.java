package org.tetris.gameplay.tetromino.impl;

import org.tetris.gameplay.board.Board;
import org.tetris.gameplay.gamefield.GameField;
import org.tetris.gameplay.tetromino.Tetromino;
import org.tetris.gameplay.tetromino.TetrominoRepository;
import org.tetris.gameplay.tetromino.TetrominoService;
import org.tetris.gameplay.tetromino.enums.Direction;
import org.tetris.gameplay.tetromino.enums.Position;
import org.tetris.gameplay.tetromino.enums.Type;

import java.util.*;
import java.util.stream.Collectors;

public class TetrominoServiceImpl implements TetrominoService {
    private static TetrominoServiceImpl instance;
    private final TetrominoRepository tetrominoRepository;
    private final Board board;

    private TetrominoServiceImpl(Board board, TetrominoRepository tetrominoRepository) {
        this.board = board;
        this.tetrominoRepository = tetrominoRepository;
    }

    public static TetrominoServiceImpl createInstance(Board board, TetrominoRepository tetrominoRepository) {
        if (Objects.isNull(instance)) {
            instance = new TetrominoServiceImpl(board, tetrominoRepository);
            return instance;
        } else {
            throw new IllegalStateException("TetrominoServiceImpl has already been created");
        }
    }

    public static TetrominoServiceImpl getInstance() {
        if (Objects.isNull(instance)) {
            throw new IllegalStateException("TetrominoServiceImpl has not been created");
        }
        return instance;
    }

    public static boolean isInstanceCreated() {
        return instance != null;
    }

    @Override
    public Tetromino create(Type type, Position position, int x, int y) {
        Map<Position, List<Tetromino.Block>> allPositions = tetrominoRepository.get(type);

        Tetromino tetromino = new Tetromino(type, x, y, allPositions);
        tetromino.setPosition(position);
        tetromino.setBlocks(adjustCoordinatesOfBlocks(allPositions.get(position), x, y));

        return tetromino;
    }

    @Override
    public Tetromino createRandom(int x, int y) {
        Type[] types = Type.values();
        Type randomType = types[new Random().nextInt(types.length)];
        Position randomPosition = Position.values()[new Random().nextInt(
                tetrominoRepository.countPositions(randomType)
        )];
        Map<Position, List<Tetromino.Block>> allPositions = tetrominoRepository.get(randomType);

        Tetromino tetromino = new Tetromino(randomType, x, y, allPositions);
        tetromino.setPosition(randomPosition);
        tetromino.setBlocks(adjustCoordinatesOfBlocks(allPositions.get(randomPosition), x, y));

        return tetromino;
    }

    @Override
    public void rotate(Tetromino tetromino, Tetromino.Block[][] grid) {
        // Обчислення нового положення фігурки
        Position nextPosition = getNextPosition(tetromino);
        // Обчислення нового положення блоків фігурки
        // TODO Блоки створюються до перевірки чи вони є на полі, хоча правильно спочатку перевірити, а тільки потім створювати
        List<Tetromino.Block> blocksForNextPosition = createBlocksForNextPosition(tetromino, nextPosition);

        // Перевіряє чи заважають інші блоки на полі для зміни позиції
        boolean isBlocksExistOnBoard = checkingIfBlocksExistOnBoard(blocksForNextPosition, grid);
        if (!isBlocksExistOnBoard) {
            // Знаходження мінімальної координати по осі X серед блоків наступного положення для редагування X координати фігурки
            int newXCoordinateForTetromino = blocksForNextPosition.stream()
                    .mapToInt(Tetromino.Block::getX)
                    .min()
                    .orElse(0);

            // Присвоєння нових даних фігурці
            tetromino.setPosition(nextPosition);
            tetromino.setBlocks(blocksForNextPosition);
            tetromino.setX(newXCoordinateForTetromino);
        }
    }

    @Override
    public int fall(Tetromino tetromino, Tetromino.Block[][] grid) {
        int height = grid.length;

        // Перебірка всіх координат під фігуркою на перешкоди
        for (int i = 1; (tetromino.getY() + i) < height; i++) {
            // Перебирає всі блоки фігурки
            for (Tetromino.Block block : tetromino.getBlocks()) {
                // Координати блока фігурки, які потрібно перевірити
                int x = block.getX();
                int y = block.getY() + i;

                // Перевірка координат блока на від'ємне число
                boolean isCoordinatesNotSmall = x >= 0 && y >= 0;
                // Перевірка координат блока на завелике число
                boolean isCoordinatesNotBig = x < grid[0].length && y < grid.length;

                // Перевірка чи координати блока знаходяться в межах розміру поля
                if (isCoordinatesNotSmall && isCoordinatesNotBig) {
                    // Перевірка чи досягнула фігурка землі
                    boolean isBottom = y == height - 1;
                    // Перевірка чи зайняте місце, або чи було досягнуто землі
                    if (Objects.nonNull(grid[y][x]) || isBottom) {
                        // Створює для фігурки нові блоки з новими координатами
                        List<Tetromino.Block> newBlocksForTetromino = new ArrayList<>();
                        for (Tetromino.Block oldBlock : tetromino.getBlocks()) {
                            int newX = oldBlock.getX();
                            int newY = oldBlock.getY() + i - 1;
                            newBlocksForTetromino.add(new Tetromino.Block(newX, newY, oldBlock.getColor()));
                        }

                        // Редагує блоки фігурки на нові
                        tetromino.setBlocks(newBlocksForTetromino);
                        // Повертає кількість пройденого шляху
                        return i - 1;
                    }
                }
            }
        }

        return 0;
    }

    @Override
    public boolean move(Tetromino tetromino, Direction direction, Tetromino.Block[][] grid) {
        // Напрямок руху фігурки
        switch (direction) {
            case LEFT -> {
                return move(tetromino, -1, 0, grid);
            }
            case RIGHT -> {
                return move(tetromino, 1, 0, grid);
            }
            case DOWN -> {
                return move(tetromino, 0, 1, grid);
            }
            default -> {
                return false;
            }
        }
    }

    /**
     * Створює Y координату для прицілу фігурки
     */
    @Override
    public int calculateAimYCoordinate() {
        GameField gameField = board.getGameField();
        int height = gameField.getHeight();
        Tetromino tetromino = gameField.getTetromino();
        Tetromino.Block[][] grid = gameField.getGrid();

        int offset = 2;
        int minimumHeightForTetromino = grid.length - offset;
        for (int i = tetromino.getY() + 1; i < height - 1; i++) {
            for (Tetromino.Block block: tetromino.getBlocks()) {

                int y = block.getY() + (i - tetromino.getY());
                int x = block.getX();

                boolean isCoordinatesNotSmall = x >= 0 && y >= 0;
                boolean isCoordinatesNotBig = x < grid[0].length && y < grid.length;

                if (isCoordinatesNotSmall && isCoordinatesNotBig) {
                    if (Objects.nonNull(grid[y][x])) {
                        minimumHeightForTetromino = Math.min(i - 1, minimumHeightForTetromino);
                        break;
                    }
                }
            }
        }

        return minimumHeightForTetromino;
    }

    private List<Tetromino.Block> adjustCoordinatesOfBlocks(List<Tetromino.Block> blocks, int x, int y) {
        return blocks.stream()
                .map(block -> new Tetromino.Block(
                        block.getX() + x,
                        block.getY() + y,
                        block.getColor()
                ))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private Position getNextPosition(Tetromino tetromino) {
        // Поточне положення фігурки
        int position = tetromino.getPosition().ordinal();
        // Максимальна можлива позиція для даної фігурки
        int maxPosition = tetrominoRepository.countPositions(tetromino.getType()) - 1;
        // Масив всіх можливих положень для всіх фігурок
        Position[] positions = Position.values();
        return (position >= maxPosition) ? (Position.UP) : (positions[position + 1]);
    }

    private List<Tetromino.Block> createBlocksForNextPosition(Tetromino tetromino, Position nextPosition) {
        // Створюються блоки для наступної позиції тетроміно
        List<Tetromino.Block> blocksForNextPosition = tetromino.getAllPositions().get(nextPosition).stream()
                .map(block -> new Tetromino.Block(
                        block.getX() + tetromino.getX(),
                        block.getY() + tetromino.getY(),
                        block.getColor()
                ))
                .collect(Collectors.toList());

        // Виправлення положення блоків тетроміно у разі їх виходу за межі поля
        return adjustBlocksOfNextPositionIfTheyCrossBoundary(blocksForNextPosition);
    }

    private List<Tetromino.Block> adjustBlocksOfNextPositionIfTheyCrossBoundary(List<Tetromino.Block> blocks) {
        GameField gameField = board.getGameField();
        int offset = 2;
        int width = gameField.getWidth() - offset;

        // Знаходження максимальної координати по осі X серед блоків наступного положення
        int maxXCoordinateOfTetrominoBlock = blocks.stream()
                .mapToInt(Tetromino.Block::getX)
                .max()
                .orElse(0);
        // Визначення кількості кроків, на яку фігура виходить за межі поля
        int stepBack = Math.max(maxXCoordinateOfTetrominoBlock - width, 0);

        // Перевіряє чи фігурка виходить за межі поля
        if (stepBack > 0) {
            // Повертає відкоригований список блоків, якщо фігурка виходить за межі поля
            return blocks.stream()
                    .map(block -> new Tetromino.Block(
                            block.getX() - stepBack,
                            block.getY(),
                            block.getColor())
                    )
                    .collect(Collectors.toList());
        } else {
            // Повертає незмінений список блоків, якщо фігурка не виходить за межі поля
            return blocks;
        }
    }

    private boolean checkingIfBlocksExistOnBoard(List<Tetromino.Block> blocksForNextPosition, Tetromino.Block[][] grid) {
        for (Tetromino.Block block : blocksForNextPosition) {
            int x = block.getX();
            int y = block.getY();

            // Перевірка координат на від'ємне число
            boolean isCoordinatesNotSmall = x >= 0 && y >= 0;
            // Перевірка координат на завелике число
            boolean isCoordinatesNotBig = x < grid[0].length && y < grid.length;

            // Перевірка чи координати блока знаходяться в межах розміру поля
            if (isCoordinatesNotSmall && isCoordinatesNotBig) {
                if (Objects.nonNull(grid[y][x])) {
                    // Якщо блоки на полі заважають
                    return true;
                }
            }
        }

        // Якщо блоки на полі НЕ заважають
        return false;
    }

    private boolean move(Tetromino tetromino, int dx, int dy, Tetromino.Block[][] grid) {
        int height = grid.length;
        // Перевіряє чи заважають інші блоки на полі руху фігурки
        for (Tetromino.Block block : tetromino.getBlocks()) {
            // Координати блока фігурки, які потрібно перевірити
            int x = block.getX() + dx;
            int y = block.getY() + dy;

            // Перевірка координат блока на від'ємне число
            boolean isCoordinatesSmall = x <= 0;
            // Перевірка координат блока на завелике число
            boolean isCoordinatesBig = x >= grid[0].length - 1;

            // Перевірка чи координати блока знаходяться в межах розміру поля
            if (isCoordinatesSmall || isCoordinatesBig) {
                return false;
            }

            // Перевірка координат блока на від'ємне число
            boolean isCoordinatesNotSmall = y >= 0;
            // Перевірка координат блока на завелике число
            boolean isCoordinatesNotBig = x < grid[0].length - 1;

            // Перевірка чи координати блока знаходяться в межах розміру поля
            if (isCoordinatesNotSmall && isCoordinatesNotBig) {
                // Перевірка чи досягнула фігурка землі
                boolean isBottom = y >= height - 1;
                // Перевірка чи зайняте місце, або чи було досягнуто землі
                if (Objects.nonNull(grid[y][x]) || isBottom) {
                   return true;
                }
            }
        }

        // Створення нових блоків для фігурки
        List<Tetromino.Block> newBlocksForTetromino = new ArrayList<>();
        for (Tetromino.Block oldBlock : tetromino.getBlocks()) {
            Tetromino.Block newBlock = new Tetromino.Block(
                    oldBlock.getX() + dx,
                    oldBlock.getY() + dy,
                    oldBlock.getColor()
            );
            newBlocksForTetromino.add(newBlock);
        }

        // Присвоєння нових блоків та координат для фігурки
        if (dx != 0) {
            tetromino.setX(tetromino.getX() + dx);
        }

        if (dy != 0) {
            tetromino.setY(tetromino.getY() + dy);
        }

        tetromino.setBlocks(newBlocksForTetromino);

        return false;
    }
}