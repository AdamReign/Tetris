import java.awt.*;
import java.util.ArrayList;

public class Figurine {
    private int x;
    private int y;
    private int position;
    private int indexFigurine;

    private ArrayList<Block> blocks;
    private FigurineDirection direction;
    private ArrayList<ArrayList<Block>> allPositions;

    public ArrayList<Block> getBlocks() {
        return blocks;
    }

    public ArrayList<ArrayList<Block>> getAllPositions() {
        return allPositions;
    }

    public FigurineDirection getDirection() {
        return direction;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getPosition() {
        return position;
    }

    public int getIndexFigurine() {
        return indexFigurine;
    }

    public void spin() {
        //Переключение между позициями
        if (++position == allPositions.size()) position = 0;

        //Новая позиция для фигурки
        ArrayList<Block> newPosition1 = new ArrayList<Block>();
        for (Block block: allPositions.get(position)) {
            newPosition1.add(new Block(block.getX()+x, block.getY()+y, block.getColor()));
        }

        //Проверка на выход фигурки за края поля
        ArrayList<Block> newPosition2 = new ArrayList<Block>();
        int maX = 0;
        for (int i = 0; i < newPosition1.size(); i++) {

            //Поиск блока с самой большей X координатой
            if (newPosition1.get(i).getX() > maX) { maX = newPosition1.get(i).getX(); }
            if (i != newPosition1.size()-1) continue;

            //Проверка на выход за край поля
            if (maX > Window.WIDTH-1) {
                //Погрешность, которую нужно отступить от края поля
                int stepBack = maX - (Window.WIDTH-1);
                for (Block block1: newPosition1) { newPosition2.add(new Block(block1.getX()-stepBack, block1.getY(), block1.getColor())); }

                //Проверка на занятое место поля каким то блоком
                for (int j = 0; j < newPosition2.size(); j++) {
                    if (Window.getAllBlocks().contains(newPosition2.get(j))) {
                        --position;
                        return;
                    } else if (j == newPosition2.size()-1) {
                        blocks = newPosition2;
                        this.x = this.x - stepBack;
                        return;
                    }
                }
            //Проверка на занятое место поля каким то блоком, если фигурка не у края поля
            } else {
                for (Block block: newPosition1) {
                    if (Window.getAllBlocks().contains(block)) {
                        --position;
                        return;
                    }
                }
            }
        }

        blocks = newPosition1;
    }

    public void fall() {
        //Создает Y координату для падения фигурки
        int mheight = Window.HEIGHT-2;
        for (int i = y+1; i < Window.HEIGHT-1; i++) {
            for (Block block: blocks) {
                if (Window.getAllBlocks().contains(new Block(block.getX(), block.getY()+(i-y),null))) {
                    mheight = i-1 < mheight ? i-1 : mheight;
                    break;
                }
            }
        }

        //Перемещает фигурку вниз, пока она не врежется в что-то
        for (int i = y; i < mheight+1; i++) {
            move(FigurineDirection.DOWN);
            Window.setScore(Window.getScore()+1);
        }
    }

    public void move(FigurineDirection direction) {
        this.direction = direction;

        //Направление движения фигурки
        if (direction == FigurineDirection.LEFT) move(-1, 0);
        else if (direction == FigurineDirection.RIGHT) move(1, 0);
        else if (direction == FigurineDirection.DOWN) move(0, 1);
    }

    private void move(int dx, int dy) {
        //Создание новых координат для блоков фигурки
        ArrayList<Block> newBlocks = new ArrayList<Block>();
        for (Block block: blocks) { newBlocks.add(new Block(block.getX() + dx, block.getY() + dy, block.getColor())); }

        //Проверка на выход фигурки за края поля
        for (Block block: newBlocks) { if (block.getX() < 1 || block.getX() > Window.WIDTH-1) return; }

        //Сохранение фигурки на поле если под ней есть блок или пол
        if (direction == FigurineDirection.DOWN) {
            for (Block block: newBlocks) {
                if (Window.getAllBlocks().contains(block) || block.getY() > Window.HEIGHT-2) {
                    for (Block block1: blocks) { Window.getAllBlocks().add(new Block(block1.getX(), block1.getY(), Color.LIGHT_GRAY)); }

                    Window.setFigurine(Window.getNextFigurine());
                    Window.setNextFigurine(new Figurine(Window.WIDTH/2, 0));
                    return;
                }
            }
        //Проверка на занятое место каким то другим блоком
        } else {
            for (Block block: newBlocks) { if (Window.getAllBlocks().contains(block)) return; }
        }

        //Новые координаты фигурки
        this.x += dx;
        this.y += dy;

        //Фигурка с новыми координатами
        blocks = newBlocks;
    }

    //Спаунер случайной фигурки в случайной позиции
    public Figurine(int x, int y) {
        //Координаты созданной фигурки
        this.x = x;
        this.y = y;

        //Список всех фигурок
        ArrayList<ArrayList<ArrayList<Block>>> allFigurines = new ArrayList<ArrayList<ArrayList<Block>>>();

        //Добавление всех фигурок в список allFigurines
        FigurineList figurineList = new FigurineList();

        allFigurines.add(figurineList.makeO()); // 0
        allFigurines.add(figurineList.makeS()); // 1
        allFigurines.add(figurineList.makeZ()); // 2
        allFigurines.add(figurineList.makeI()); // 3
        allFigurines.add(figurineList.makeL()); // 4
        allFigurines.add(figurineList.makeJ()); // 5
        allFigurines.add(figurineList.makeT()); // 6

        //Создание списка всех позиций рандомной фигурки
        indexFigurine = (int) (Math.random() * allFigurines.size());
        allPositions = allFigurines.get(indexFigurine);

        //Создание рандомной позиции для фигурки
        position = (int) (Math.random() * allPositions.size());

        //Создание фигурки
        ArrayList<Block> newBlocks = new ArrayList<Block>();
        for (Block block: allPositions.get(position)) {
            newBlocks.add(new Block(block.getX()+x, block.getY()+y, block.getColor()));
        }

        blocks = newBlocks;
    }
}
