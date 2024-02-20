package org.example.model;

import org.example.enums.Direction;
import org.example.enums.Position;
import org.example.enums.Type;

import java.awt.Color;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Tetromino {
    private final Type TYPE;
    private volatile int x;
    private volatile int y;
    private final Map<Position, List<Tetromino.Block>> ALL_POSITIONS;
    private volatile Position position;
    private volatile List<Tetromino.Block> blocks;

    private volatile Direction direction;

    public Tetromino(Type type, int x, int y, Map<Position, List<Tetromino.Block>> allPositions) {
        this.TYPE = type;
        this.x = x;
        this.y = y;
        this.ALL_POSITIONS = allPositions;

        this.position = Position.UP;
    }

    public Type getType() {
        return TYPE;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Map<Position, List<Tetromino.Block>> getAllPositions() {
        return ALL_POSITIONS;
    }

    public Position getPosition() {
        return position;
    }

    public List<Tetromino.Block> getBlocks() {
        return blocks;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setBlocks(List<Tetromino.Block> blocks) {
        this.blocks = blocks;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public static class Block {
        private final int X;
        private final int Y;
        private final Color COLOR;
        private volatile boolean deleted;

        public Block(int x, int y, Color color) {
            this.X = x;
            this.Y = y;
            this.COLOR = color;
            deleted = false;
        }

        public Block lower() {
            if (isDeleted()) {
                throw new RuntimeException("Спроба опустити видалений блок");
            }
            return new Block(this.X, this.Y + 1, this.COLOR);
        }

        public Block copyWithColor(Color newColor) {
            Block block = new Block(this.X, this.Y, newColor);
            if (isDeleted()) {
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
            int result = 17;
            result = 31 * result + X;
            result = 31 * result + Y;
            return result;
        }
    }
}
