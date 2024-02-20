package org.example.service;

import org.example.enums.Direction;
import org.example.enums.Position;
import org.example.enums.Type;
import org.example.model.Board;
import org.example.model.Tetromino;
import org.example.repository.TetrominoRepository;
import org.example.view.BoardView;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

public class TetrominoService {
    private final TetrominoRepository TETROMINO_REPOSITORY;
    private final Board  BOARD;
    private final BoardView BOARD_VIEW;

    public TetrominoService(Board board, BoardView boardView) {
        BOARD = board;
        BOARD_VIEW = boardView;
        TETROMINO_REPOSITORY = new TetrominoRepository();
    }

//    public ArrayList<ArrayList<ArrayList<Tetromino.Block>>> getAll() {
//        return Stream.of(Type.values())
//                .map(TETROMINO_REPOSITORY::get)
//                .collect(Collectors.toCollection(ArrayList::new));
//    }

    public Tetromino getRandom(int x, int y) {
        Type[] types = Type.values();
        Type randomType = types[new Random().nextInt(types.length)];
        Position randomPosition = Position.values()[new Random().nextInt(
                TETROMINO_REPOSITORY.countPositions(randomType)
        )];
        Map<Position, List<Tetromino.Block>> allPositions = TETROMINO_REPOSITORY.get(randomType);

        Tetromino tetromino = new Tetromino(randomType, x, y, allPositions);
        tetromino.setPosition(randomPosition);
        tetromino.setBlocks(allPositions.get(randomPosition));

        update(tetromino);

        return tetromino;
    }

    public void update(Tetromino tetromino) {
        List<Tetromino.Block> blocks = tetromino.getBlocks().stream()
                .map(block -> new Tetromino.Block(
                        block.getX() + tetromino.getX(),
                        block.getY() + tetromino.getY(),
                        block.getColor()
                ))
                .collect(Collectors.toCollection(ArrayList::new));
        tetromino.setBlocks(blocks);
    }

    public void spin(Tetromino tetromino) {
        // Поточна позиція фігури
        int position = tetromino.getPosition().ordinal();
        // Максимальна кількість можливих позицій для даної фігури
        int maxPositions = TETROMINO_REPOSITORY.countPositions(tetromino.getType()) - 1;
        // Масив можливих позицій
        Position[] positions = Position.values();

        // Встановлення нової позиції для фігури
        tetromino.setPosition(position == maxPositions ? Position.UP : positions[position + 1]);

        // Обчислення нової позиції блоків фігури
        List<Tetromino.Block> newPosition = calculateNewPosition(tetromino);

        // Знаходження максимальної координати по осі X серед блоків нової позиції
        int maxX = newPosition.stream().mapToInt(Tetromino.Block::getX).max().orElse(0);
        // Визначення кількості кроків, на яку фігура вийшла за межі поля
        int stepBack = Math.max(maxX - (BOARD.getWidth() - 1), 0);

        // Виправлення позиції фігури в разі виходу за межі поля
        List<Tetromino.Block> newPositionAdjusted = adjustPosition(newPosition, stepBack);

        // Обробка випадку, коли фігура вийшла за межі поля по осі X
        if (maxX > BOARD.getWidth() - 1) {
            handleMaxXPosition(tetromino, maxPositions, positions, newPositionAdjusted, stepBack);
        }
        // Обробка випадку, коли фігура заважає іншій фігурі
        else if (newPosition.stream().anyMatch(BOARD.getAllBlocks()::contains)) {
            handleBlockedPosition(tetromino, positions, position);
        }
        // Встановлення блоків з новою позиціє для фігури
        else {
            tetromino.setBlocks(newPosition);
        }
    }

    private List<Tetromino.Block> calculateNewPosition(Tetromino tetromino) {
        return tetromino.getAllPositions().get(tetromino.getPosition())
                .stream()
                .map(block -> new Tetromino.Block(
                        block.getX() + tetromino.getX(),
                        block.getY() + tetromino.getY(),
                        block.getColor())
                )
                .collect(Collectors.toList());
    }

    private List<Tetromino.Block> adjustPosition(List<Tetromino.Block> newPosition, int stepBack) {
        return newPosition.stream()
                .map(block -> new Tetromino.Block(
                        block.getX() - stepBack,
                        block.getY(),
                        block.getColor())
                )
                .collect(Collectors.toList());
    }

    private void handleMaxXPosition(Tetromino tetromino, int maxPositions, Position[] positions,
            List<Tetromino.Block> newPositionAdjusted, int stepBack) {

        if (newPositionAdjusted.stream().anyMatch(BOARD.getAllBlocks()::contains)) {
            tetromino.setPosition(tetromino.getPosition() == Position.UP ?
                    positions[maxPositions - 1] : positions[tetromino.getPosition().ordinal() - 1]
            );
        } else {
            tetromino.setBlocks(newPositionAdjusted);
            tetromino.setX(tetromino.getX() - stepBack);
        }
    }

    private void handleBlockedPosition(Tetromino tetromino, Position[] positions, int position) {
        if (position > 0) {
            tetromino.setPosition(positions[position - 1]);
        }
    }

    public void fall(Tetromino tetromino) {
        // Створює Y координату для падіння фігурки
        int mheight = BOARD.getHeight()-2;
        for (int i = tetromino.getY()+1; i < BOARD.getHeight()-1; i++) {
            for (Tetromino.Block block: tetromino.getBlocks()) {
                if (BOARD.getAllBlocks().contains(new Tetromino.Block(
                        block.getX(),
                        block.getY()+(i-tetromino.getY()),
                        null)
                )) {
                    mheight = Math.min(i - 1, mheight);
                    break;
                }
            }
        }

        // Переміщує фігурку вниз, поки вона не вріжиться в щось
        for (int i = tetromino.getY(); i < mheight+1; i++) {
            move(tetromino, Direction.DOWN);
            BOARD.setScore(BOARD.getScore()+1);
        }
    }

    public void move(Tetromino tetromino, Direction direction) {
        tetromino.setDirection(direction);

        // Направлення руху фігурки
        switch (direction) {
            case LEFT -> move(tetromino, -1, 0);
            case RIGHT -> move(tetromino, 1, 0);
            case DOWN -> move(tetromino, 0, 1);
        }
    }

    private void move(Tetromino tetromino, int dx, int dy) {
        // Створення нових координат для блоків фігурки
        List<Tetromino.Block> newBlocks = new ArrayList<>();
        for (Tetromino.Block block: tetromino.getBlocks()) {
            newBlocks.add(new Tetromino.Block(block.getX() + dx, block.getY() + dy, block.getColor()));
        }

        // Перевірка фігурки на вихід за межі поля
        for (Tetromino.Block block: newBlocks) {
            if (block.getX() < 1 || block.getX() > BOARD.getWidth()-1) return;
        }

        // Збереження блоків фігурки на полі, якщо під нею є блоки чи підлога
        if (tetromino.getDirection() == Direction.DOWN) {
            for (Tetromino.Block block: newBlocks) {
                if (BOARD.getAllBlocks().contains(block) || block.getY() > BOARD.getHeight()-2) {
                    for (Tetromino.Block block1: tetromino.getBlocks()) {
                        BOARD.getAllBlocks().add(new Tetromino.Block(block1.getX(), block1.getY(), Color.LIGHT_GRAY));
                    }

                    BOARD.setTetromino(BOARD.getNextTetromino());



                    // TODO
                    BOARD.setNextTetromino(getRandom(BOARD.getWidth()/2, 0));
                    return;
                }
            }
            // Перевірка на зайняте місце іншим блоком
        } else {
            for (Tetromino.Block block: newBlocks) {
                if (BOARD.getAllBlocks().contains(block)) return;
            }
        }

        // Нові координати фігурки
        tetromino.setX(tetromino.getX() + dx);
        tetromino.setY(tetromino.getY() + dy);

        // Фігурка з новими координатами
        tetromino.setBlocks(newBlocks);
    }
}
