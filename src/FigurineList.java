import java.awt.Color;
import java.util.ArrayList;

public class FigurineList {
   //Фигурка - O
    public ArrayList<ArrayList<Block>> makeO() {
        ArrayList<ArrayList<Block>> allPositions = new ArrayList<ArrayList<Block>>();

        ArrayList<Block> positionUp = new ArrayList<Block>();
        positionUp.add(new Block(0, 0, Color.YELLOW));
        positionUp.add(new Block(1, 0, Color.YELLOW));
        positionUp.add(new Block(0, -1, Color.YELLOW));
        positionUp.add(new Block(1, -1, Color.YELLOW));
        allPositions.add(positionUp);

        return allPositions;
    }

    //Фигурка - S
    public ArrayList<ArrayList<Block>> makeS() {
        ArrayList<ArrayList<Block>> allPositions = new ArrayList<ArrayList<Block>>();

        ArrayList<Block> positionUp = new ArrayList<Block>();
        positionUp.add(new Block(0, 0, Color.GREEN));
        positionUp.add(new Block(1, 0, Color.GREEN));
        positionUp.add(new Block(1, -1, Color.GREEN));
        positionUp.add(new Block(2, -1, Color.GREEN));
        allPositions.add(positionUp);

        ArrayList<Block> positionLeft = new ArrayList<Block>();
        positionLeft.add(new Block(1, 0, Color.GREEN));
        positionLeft.add(new Block(1, -1, Color.GREEN));
        positionLeft.add(new Block(0, -1, Color.GREEN));
        positionLeft.add(new Block(0, -2, Color.GREEN));
        allPositions.add(positionLeft);

        return allPositions;
    }

    //Фигурка - Z
    public ArrayList<ArrayList<Block>> makeZ() {
        ArrayList<ArrayList<Block>> allPositions = new ArrayList<ArrayList<Block>>();

        ArrayList<Block> positionUp = new ArrayList<Block>();
        positionUp.add(new Block(1, 0, Color.RED));
        positionUp.add(new Block(2, 0, Color.RED));
        positionUp.add(new Block(0, -1, Color.RED));
        positionUp.add(new Block(1, -1, Color.RED));
        allPositions.add(positionUp);

        ArrayList<Block> positionLeft = new ArrayList<Block>();
        positionLeft.add(new Block(0, 0, Color.RED));
        positionLeft.add(new Block(0, -1, Color.RED));
        positionLeft.add(new Block(1, -1, Color.RED));
        positionLeft.add(new Block(1, -2, Color.RED));
        allPositions.add(positionLeft);

        return allPositions;
    }

    //Фигурка - I
    public ArrayList<ArrayList<Block>> makeI() {
        ArrayList<ArrayList<Block>> allPositions = new ArrayList<ArrayList<Block>>();

        ArrayList<Block> positionUp = new ArrayList<Block>();
        positionUp.add(new Block(0, 0, Color.CYAN));
        positionUp.add(new Block(0, -1, Color.CYAN));
        positionUp.add(new Block(0, -2, Color.CYAN));
        positionUp.add(new Block(0, -3, Color.CYAN));
        allPositions.add(positionUp);

        ArrayList<Block> positionLeft = new ArrayList<Block>();
        positionLeft.add(new Block(0, 0, Color.CYAN));
        positionLeft.add(new Block(1, 0, Color.CYAN));
        positionLeft.add(new Block(2, 0, Color.CYAN));
        positionLeft.add(new Block(3, 0, Color.CYAN));
        allPositions.add(positionLeft);

        return allPositions;
    }

    //Фигурка - L
    public ArrayList<ArrayList<Block>> makeL() {
        ArrayList<ArrayList<Block>> allPositions = new ArrayList<ArrayList<Block>>();

        ArrayList<Block> positionUp = new ArrayList<Block>();
        positionUp.add(new Block(0, 0, Color.ORANGE));
        positionUp.add(new Block(0, -1, Color.ORANGE));
        positionUp.add(new Block(0, -2, Color.ORANGE));
        positionUp.add(new Block(1, 0, Color.ORANGE));
        allPositions.add(positionUp);

        ArrayList<Block> positionLeft = new ArrayList<Block>();
        positionLeft.add(new Block(0, 0, Color.ORANGE));
        positionLeft.add(new Block(0, -1, Color.ORANGE));
        positionLeft.add(new Block(1, -1, Color.ORANGE));
        positionLeft.add(new Block(2, -1, Color.ORANGE));
        allPositions.add(positionLeft);

        ArrayList<Block> positionDown = new ArrayList<Block>();
        positionDown.add(new Block(1, 0, Color.ORANGE));
        positionDown.add(new Block(1, -1, Color.ORANGE));
        positionDown.add(new Block(1, -2, Color.ORANGE));
        positionDown.add(new Block(0, -2, Color.ORANGE));
        allPositions.add(positionDown);

        ArrayList<Block> positionRight = new ArrayList<Block>();
        positionRight.add(new Block(0, 0, Color.ORANGE));
        positionRight.add(new Block(1, 0, Color.ORANGE));
        positionRight.add(new Block(2, 0, Color.ORANGE));
        positionRight.add(new Block(2, -1, Color.ORANGE));
        allPositions.add(positionRight);

        return allPositions;
    }

    //Фигурка - J
    public ArrayList<ArrayList<Block>> makeJ() {
        ArrayList<ArrayList<Block>> allPositions = new ArrayList<ArrayList<Block>>();

        ArrayList<Block> positionUp = new ArrayList<Block>();
        positionUp.add(new Block(0, 0, Color.BLUE));
        positionUp.add(new Block(1, 0, Color.BLUE));
        positionUp.add(new Block(1, -1, Color.BLUE));
        positionUp.add(new Block(1, -2, Color.BLUE));
        allPositions.add(positionUp);

        ArrayList<Block> positionLeft = new ArrayList<Block>();
        positionLeft.add(new Block(0, 0, Color.BLUE));
        positionLeft.add(new Block(1, 0, Color.BLUE));
        positionLeft.add(new Block(2, 0, Color.BLUE));
        positionLeft.add(new Block(0, -1, Color.BLUE));
        allPositions.add(positionLeft);

        ArrayList<Block> positionDown = new ArrayList<Block>();
        positionDown.add(new Block(0, 0, Color.BLUE));
        positionDown.add(new Block(0, -1, Color.BLUE));
        positionDown.add(new Block(0, -2, Color.BLUE));
        positionDown.add(new Block(1, -2, Color.BLUE));
        allPositions.add(positionDown);

        ArrayList<Block> positionRight = new ArrayList<Block>();
        positionRight.add(new Block(2, 0, Color.BLUE));
        positionRight.add(new Block(0, -1, Color.BLUE));
        positionRight.add(new Block(1, -1, Color.BLUE));
        positionRight.add(new Block(2, -1, Color.BLUE));
        allPositions.add(positionRight);

        return allPositions;
    }

    //Фигурка - T
    public ArrayList<ArrayList<Block>> makeT() {
        ArrayList<ArrayList<Block>> allPositions = new ArrayList<ArrayList<Block>>();

        ArrayList<Block> positionUp = new ArrayList<Block>();
        positionUp.add(new Block(0, 0, Color.MAGENTA));
        positionUp.add(new Block(1, 0, Color.MAGENTA));
        positionUp.add(new Block(2, 0, Color.MAGENTA));
        positionUp.add(new Block(1, -1, Color.MAGENTA));
        allPositions.add(positionUp);

        ArrayList<Block> positionLeft = new ArrayList<Block>();
        positionLeft.add(new Block(0, 0, Color.MAGENTA));
        positionLeft.add(new Block(0, -1, Color.MAGENTA));
        positionLeft.add(new Block(0, -2, Color.MAGENTA));
        positionLeft.add(new Block(1, -1, Color.MAGENTA));
        allPositions.add(positionLeft);

        ArrayList<Block> positionDown = new ArrayList<Block>();
        positionDown.add(new Block(0, -1, Color.MAGENTA));
        positionDown.add(new Block(1, -1, Color.MAGENTA));
        positionDown.add(new Block(2, -1, Color.MAGENTA));
        positionDown.add(new Block(1, 0, Color.MAGENTA));
        allPositions.add(positionDown);

        ArrayList<Block> positionRight = new ArrayList<Block>();
        positionRight.add(new Block(1, 0, Color.MAGENTA));
        positionRight.add(new Block(1, -1, Color.MAGENTA));
        positionRight.add(new Block(1, -2, Color.MAGENTA));
        positionRight.add(new Block(0, -1, Color.MAGENTA));
        allPositions.add(positionRight);

        return allPositions;
    }
}
