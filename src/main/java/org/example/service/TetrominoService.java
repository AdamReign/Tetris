package org.example.service;

import org.example.enums.Direction;
import org.example.enums.Position;
import org.example.enums.Type;
import org.example.model.Board;
import org.example.model.Tetromino;
import org.example.repository.TetrominoRepository;

import javax.swing.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class TetrominoService {
    private final TetrominoRepository TETROMINO_REPOSITORY;
    private final Board  BOARD;

    public TetrominoService(Board board) {
        BOARD = board;
        TETROMINO_REPOSITORY = new TetrominoRepository();
    }

    public Tetromino get(Type type, Position position, int x, int y) {
        ImageIcon imageIconForMenu = TETROMINO_REPOSITORY.getImageForMenu(type);
        Map<Position, List<Tetromino.Block>> allPositions = TETROMINO_REPOSITORY.get(type);

        Tetromino tetromino = new Tetromino(type, x, y, imageIconForMenu, allPositions);
        tetromino.setPosition(position);
        tetromino.setBlocks(allPositions.get(position));

        adjustCoordinatesOfBlocks(tetromino);

        return tetromino;
    }

    public Tetromino getRandom(int x, int y) {
        Type[] types = Type.values();
        Type randomType = types[new Random().nextInt(types.length)];
        Position randomPosition = Position.values()[new Random().nextInt(
                TETROMINO_REPOSITORY.countPositions(randomType)
        )];
        ImageIcon imageIconForMenu = TETROMINO_REPOSITORY.getImageForMenu(randomType);
        Map<Position, List<Tetromino.Block>> allPositions = TETROMINO_REPOSITORY.get(randomType);

        Tetromino tetromino = new Tetromino(randomType, x, y, imageIconForMenu, allPositions);
        tetromino.setPosition(randomPosition);
        tetromino.setBlocks(allPositions.get(randomPosition));

        adjustCoordinatesOfBlocks(tetromino);

        return tetromino;
    }

    public void spin(Tetromino tetromino, Tetromino.Block[][] allBlocks) {
        // Обчислення нового положення фігурки
        Position nextPosition = getNextPosition(tetromino);
        // Обчислення нового положення блоків фігурки
        // TODO Блоки створюються до перевірки чи вони є на полі, хоча правильно спочатку перевірити, а тільки потім створювати
        List<Tetromino.Block> blocksForNextPosition = createBlocksForNextPosition(tetromino, nextPosition);

        // Перевіряє чи заважають інші блоки на полі для зміни позиції
        boolean isBlocksExistOnBoard = checkingIfBlocksExistOnBoard(blocksForNextPosition, allBlocks);
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

    public int fall(Tetromino tetromino, Tetromino.Block[][] allBlocks) {
        int height = allBlocks.length;

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
                boolean isCoordinatesNotBig = x < allBlocks[0].length && y < allBlocks.length;

                // Перевірка чи координати блока знаходяться в межах розміру поля
                if (isCoordinatesNotSmall && isCoordinatesNotBig) {
                    // Перевірка чи досягнула фігурка землі
                    boolean isBottom = y == height - 1;
                    // Перевірка чи зайняте місце, або чи було досягнуто землі
                    if (Objects.nonNull(allBlocks[y][x]) || isBottom) {
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

    public boolean move(Tetromino tetromino, Direction direction, Tetromino.Block[][] allBlocks) {
        // Напрямок руху фігурки
        switch (direction) {
            case LEFT -> {
                return move(tetromino, -1, 0, allBlocks);
            }
            case RIGHT -> {
                return move(tetromino, 1, 0, allBlocks);
            }
            case DOWN -> {
                return move(tetromino, 0, 1, allBlocks);
            }
            default -> {
                return false;
            }
        }
    }

    private void adjustCoordinatesOfBlocks(Tetromino tetromino) {
        List<Tetromino.Block> blocks = tetromino.getBlocks().stream()
                .map(block -> new Tetromino.Block(
                        block.getX() + tetromino.getX(),
                        block.getY() + tetromino.getY(),
                        block.getColor()
                ))
                .collect(Collectors.toCollection(ArrayList::new));
        tetromino.setBlocks(blocks);
    }

    private Position getNextPosition(Tetromino tetromino) {
        // Поточне положення фігурки
        int position = tetromino.getPosition().ordinal();
        // Максимальна можлива позиція для даної фігурки
        int maxPosition = TETROMINO_REPOSITORY.countPositions(tetromino.getType()) - 1;
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
        int offset = 2;
        int width = BOARD.getWidth() - offset;

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

    private boolean checkingIfBlocksExistOnBoard(List<Tetromino.Block> blocksForNextPosition, Tetromino.Block[][] allBlocks) {
        for (Tetromino.Block block : blocksForNextPosition) {
            int x = block.getX();
            int y = block.getY();

            // Перевірка координат на від'ємне число
            boolean isCoordinatesNotSmall = x >= 0 && y >= 0;
            // Перевірка координат на завелике число
            boolean isCoordinatesNotBig = x < allBlocks[0].length && y < allBlocks.length;

            // Перевірка чи координати блока знаходяться в межах розміру поля
            if (isCoordinatesNotSmall && isCoordinatesNotBig) {
                if (Objects.nonNull(allBlocks[y][x])) {
                    // Якщо блоки на полі заважають
                    return true;
                }
            }
        }

        // Якщо блоки на полі НЕ заважають
        return false;
    }

    private boolean move(Tetromino tetromino, int dx, int dy, Tetromino.Block[][] allBlocks) {
        int height = allBlocks.length;
        // Перевіряє чи заважають інші блоки на полі руху фігурки
        for (Tetromino.Block block : tetromino.getBlocks()) {
            // Координати блока фігурки, які потрібно перевірити
            int x = block.getX() + dx;
            int y = block.getY() + dy;

            // Перевірка координат блока на від'ємне число
            boolean isCoordinatesSmall = x <= 0;
            // Перевірка координат блока на завелике число
            boolean isCoordinatesBig = x >= allBlocks[0].length - 1;

            // Перевірка чи координати блока знаходяться в межах розміру поля
            if (isCoordinatesSmall || isCoordinatesBig) {
                return false;
            }

            // Перевірка координат блока на від'ємне число
            boolean isCoordinatesNotSmall = y >= 0;
            // Перевірка координат блока на завелике число
            boolean isCoordinatesNotBig = x < allBlocks[0].length - 1;

            // Перевірка чи координати блока знаходяться в межах розміру поля
            if (isCoordinatesNotSmall && isCoordinatesNotBig) {
                // Перевірка чи досягнула фігурка землі
                boolean isBottom = y >= height - 1;
                // Перевірка чи зайняте місце, або чи було досягнуто землі
                if (Objects.nonNull(allBlocks[y][x]) || isBottom) {
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
        tetromino.setBlocks(newBlocksForTetromino);
        tetromino.setX(tetromino.getX() + dx);
        tetromino.setY(tetromino.getY() + dy);

        return false;
    }
}