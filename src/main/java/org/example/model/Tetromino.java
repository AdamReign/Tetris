package org.example.model;

import org.example.enums.Position;
import org.example.enums.Type;

import javax.swing.*;
import java.awt.Color;
import java.util.*;
import java.util.stream.Collectors;

public final class Tetromino {
    private final Type TYPE;
    private final ImageIcon IMAGE_ICON_FOR_MENU;
    private volatile int x;
    private volatile int y;
    private final Map<Position, List<Tetromino.Block>> ALL_POSITIONS;
    private volatile Position position;
    private volatile List<Tetromino.Block> blocks;

    public Tetromino(Type type, int x, int y, ImageIcon imageIconForMenu, Map<Position, List<Tetromino.Block>> allPositions) {
        this.TYPE = type;
        this.x = x;
        this.y = y;
        IMAGE_ICON_FOR_MENU = imageIconForMenu;
        this.ALL_POSITIONS = allPositions;

        this.position = Position.UP;
    }

    public Type getType() {
        return TYPE;
    }

    public ImageIcon getImageForMenu() {
        return new ImageIcon(IMAGE_ICON_FOR_MENU.getImage());
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Map<Position, List<Tetromino.Block>> getAllPositions() {
        return ALL_POSITIONS.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> new ArrayList<>(entry.getValue())));
    }

    public Position getPosition() {
        return position;
    }

    public List<Tetromino.Block> getBlocks() {
        return new ArrayList<>(blocks);
    }

    public synchronized void setBlocks(List<Tetromino.Block> blocks) {
        this.blocks = new ArrayList<>(blocks);
    }

    public synchronized void setX(int x) {
        this.x = x;
    }

    public synchronized void setY(int y) {
        this.y = y;
    }

    public synchronized void setPosition(Position position) {
        this.position = position;
    }

    public static final class Block {
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
//            int result = 17;
//            result = 31 * result + X;
//            result = 31 * result + Y;
//            return result;
            return Objects.hash(X, Y);
        }
    }
}