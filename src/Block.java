import java.awt.Color;
import java.util.Objects;

public class Block {
    private int x;
    private int y;
    private Color color;

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Color getColor() {
        return color;
    }

    public Block(int x, int y, Color color) {
        this.x = x;
        this.y = y;
        this.color = color;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Block)) return false;
        Block block = (Block) o;
        return x == block.x && y == block.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
