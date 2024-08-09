package org.tetris.gameplay.tetromino;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import org.tetris.gameplay.tetromino.enums.Position;
import org.tetris.gameplay.tetromino.enums.Type;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public final class Tetromino {
    private final Type TYPE;
    private final Map<Position, List<Block>> ALL_POSITIONS;

    private final IntegerProperty x;
    private final IntegerProperty y;
    private final ObjectProperty<Position> position;
    private final ObjectProperty<List<Block>> blocks;

    public Tetromino(Tetromino tetromino) {
        this.TYPE = tetromino.getType();
        this.x = new SimpleIntegerProperty(tetromino.getX());
        this.y = new SimpleIntegerProperty(tetromino.getY());
        this.ALL_POSITIONS = tetromino.getAllPositions();
        this.position = new SimpleObjectProperty<>(tetromino.getPosition());
        this.blocks = new SimpleObjectProperty<>(tetromino.getBlocks());
    }

    public Tetromino(Type type, int x, int y,
                     Map<Position, List<Block>> allPositions) {
        this.TYPE = type;
        this.x = new SimpleIntegerProperty(x);
        this.y = new SimpleIntegerProperty(y);
        this.ALL_POSITIONS = allPositions;

        this.position = new SimpleObjectProperty<>(Position.UP);
        this.blocks = new SimpleObjectProperty<>();
    }

    public Type getType() {
        return TYPE;
    }

    public int getX() {
        return x.get();
    }

    public int getY() {
        return y.get();
    }

    public Map<Position, List<Block>> getAllPositions() {
        return ALL_POSITIONS.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> new ArrayList<>(entry.getValue())));
    }

    public Position getPosition() {
        return position.get();
    }

    public List<Block> getBlocks() {
        return new ArrayList<>(blocks.get());
    }

    public synchronized void setBlocks(List<Block> blocks) {
        this.blocks.set(new ArrayList<>(blocks));
    }

    public synchronized void setX(int x) {
        this.x.set(x);
    }

    public synchronized void setY(int y) {
        this.y.set(y);
    }

    public synchronized void setPosition(Position position) {
        this.position.set(position);
    }

    public IntegerProperty xProperty() {
        return x;
    }

    public IntegerProperty yProperty() {
        return y;
    }

    public ObjectProperty<Position> positionProperty() {
        return position;
    }

    public ObjectProperty<List<Block>> blocksProperty() {
        return blocks;
    }

    public static final class Block {
        private final int X;
        private final int Y;
        private final Color COLOR;
        private volatile boolean deleted;

        public Block(Block block) {
            this.X = block.getX();
            this.Y = block.getY();
            this.COLOR = block.getColor();
            this.deleted = block.isDeleted();
        }

        public Block(int x, int y, Color color) {
            this.X = x;
            this.Y = y;
            this.COLOR = color;
            deleted = false;
        }

        public Block lower() {
            return new Block(this.X, this.Y + 1, this.COLOR);
        }

        public Block copyWithNewColor(Color newColor) {
            Block block = new Block(this.X, this.Y, newColor);
            if (this.isDeleted()) {
                block.delete();
            }
            return block;
        }

        public void delete() {
            deleted = true;
        }

        public int getX() {
            return X;
        }

        public int getY() {
            return Y;
        }

        public Color getColor() {
            return COLOR;
        }

        public boolean isDeleted() {
            return deleted;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || o.getClass() != Block.class || o.hashCode() != this.hashCode()) {
                return false;
            }
            if (this == o) {
                return true;
            }
            Block block = (Block) o;
            return (X == block.X) && (Y == block.Y);
        }

        @Override
        public int hashCode() {
            return Objects.hash(X, Y);
        }
    }
}