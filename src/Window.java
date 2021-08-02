import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

public class Window extends JPanel implements Runnable {

    private Queue<KeyEvent> keyEvents = new ArrayBlockingQueue<KeyEvent>(100);

    private JFrame frame;
    private static Window game;
    private static Thread gameT;
    private static Figurine figurine;
    private static Figurine nextFigurine;
    private static ArrayList<Block> allBlocks = new ArrayList<Block>();
    private static boolean close = false;
    private static boolean gameOver;
    private boolean pause;

    private static int score = 0;
    private int speed = 1000;

    public static final int SCALE = 32;
    public static final int WIDTH = 16;
    public static final int HEIGHT = 22;

    @Override
    public void paint(Graphics g) {
        //Рисует игровое поле
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, (WIDTH+6)*SCALE, HEIGHT*SCALE);

        //Создает Y координату для прицела фигурки
        int mheight = HEIGHT-2;
        for (int i = figurine.getY()+1; i < HEIGHT-1; i++) {
            for (Block block: figurine.getBlocks()) {
                if (allBlocks.contains(new Block(block.getX(), block.getY()+(i-figurine.getY()),null))) {
                    mheight = i-1 < mheight ? i-1 : mheight;
                    break;
                }
            }
        }

        //Рисует прицел для фигурки
        for (int i = 0; i < figurine.getBlocks().size(); i++) {
            g.setColor(figurine.getBlocks().get(i).getColor());
            g.fillRect(figurine.getBlocks().get(i).getX()*SCALE+1, (figurine.getBlocks().get(i).getY()+(mheight-figurine.getY()))*SCALE+1, SCALE-1, SCALE-1);
            g.setColor(Color.BLACK);
            g.fillRect(figurine.getBlocks().get(i).getX()*SCALE+2, (figurine.getBlocks().get(i).getY()+(mheight-figurine.getY()))*SCALE+2, SCALE-3, SCALE-3);
        }

        //Рисует фигурку
        for (int i = 0; i < figurine.getBlocks().size(); i++) {
            g.setColor(figurine.getBlocks().get(i).getColor());
            g.fillRect(figurine.getBlocks().get(i).getX()*SCALE+1, figurine.getBlocks().get(i).getY()*SCALE+1, SCALE-1, SCALE-1);
        }

        //Удаляет из allBlocks все блоки желтого цвета и двигает верхние блоки вниз
        for (int i = 0; i < allBlocks.size(); i++) {
            if (allBlocks.get(i).getColor() == Color.YELLOW) {
                for (int j = 0; j < allBlocks.size(); j++) {
                    if (allBlocks.get(j).getX() == allBlocks.get(i).getX() && allBlocks.get(j).getY() < allBlocks.get(i).getY()) {
                        allBlocks.set(j, new Block(allBlocks.get(j).getX(), allBlocks.get(j).getY()+1, allBlocks.get(j).getColor()));
                    }
                }

                allBlocks.remove(i);
                score += 10;
            }
        }

        //Проверяет блоки в allBlocks на создание линии и помечает их желтым цветом
        for (Block block: allBlocks) {
            int lineSize = 1;
            for (Block block1: allBlocks) { if (block.getY() == block1.getY()) lineSize++; }

            if (lineSize == WIDTH) { for (int i = 0; i < allBlocks.size(); i++) {
                    if (allBlocks.get(i).getY() == block.getY()) {
                        allBlocks.set(i, new Block(allBlocks.get(i).getX(), allBlocks.get(i).getY(), Color.YELLOW));
                    }
                }
            }
        }

        //Рисует блоки на поле
        for (Block block: allBlocks) {
            g.setColor(block.getColor());
            g.fillRect(block.getX()*SCALE+1, block.getY()*SCALE+1, SCALE-1, SCALE-1);
        }

        //Рисует границу поля слева
        for (int i = 1; i < HEIGHT-1; i++) {
            g.setColor(Color.GRAY);
            g.fillRect(1, i*SCALE+1, SCALE-1, SCALE-1);
        }

        //Рисует границу_1 поля справа
        for (int i = 1; i < HEIGHT-1; i++) {
            g.setColor(Color.GRAY);
            g.fillRect(WIDTH*SCALE+1, i*SCALE+1, SCALE-1, SCALE-1);
        }

        //Рисует границу_2 поля справа
        for (int i = 1; i < HEIGHT-1; i++) {
            g.setColor(Color.GRAY);
            g.fillRect((WIDTH+5)*SCALE+1, i*SCALE+1, SCALE-1, SCALE-1);
        }

        //Рисует границу_1 поля вверху
        for (int i = 0; i < WIDTH+6; i++) {
            g.setColor(Color.GRAY);
            g.fillRect(i*SCALE+1, 1, SCALE-1, SCALE-1);
        }

        //Рисует границу_2 поля вверху
        for (int i = WIDTH+1; i < WIDTH+5; i++) {
            g.setColor(Color.GRAY);
            g.fillRect(i*SCALE+1, (5)*SCALE+1, SCALE-1, SCALE-1);
        }

        //Рисует границу поля внизу
        for (int i = 0; i < WIDTH+6; i++) {
            g.setColor(Color.GRAY);
            g.fillRect(i*SCALE+1, (HEIGHT-1)*SCALE+1, SCALE-1, SCALE-1);
        }

        //Пишет слово "Пауза"
        if (!gameOver && pause) {
            Font sPause = new Font("Bitstream Charter", Font.BOLD, 40);
            Graphics2D g2 = (Graphics2D) g;
            g2.setFont(sPause);
            g2.setColor(Color.YELLOW);
            g2.drawString("Пауза", 7*SCALE, (HEIGHT*SCALE)/2);
        }

        //Пишет фразу "Конец игры"
        for (Block block: allBlocks) {
            if (block.getY() == 1) {
                Font sGameOver = new Font("Bitstream Charter", Font.BOLD, 40);
                Graphics2D g2 = (Graphics2D) g;
                g2.setFont(sGameOver);
                g2.setColor(Color.YELLOW);
                g2.drawString("Конец игры", 5*SCALE, (HEIGHT*SCALE)/2);
                gameOver = true;
                break;
            }
        }

        //Рисует следующую фигурку
        for (Block block: nextFigurine.getAllPositions().get(0)) {
            g.setColor(block.getColor());
            switch (nextFigurine.getIndexFigurine()) {
                case 0: // makeO()
                    g.fillRect((block.getX() + WIDTH + 2) * SCALE + 1, (block.getY() + 3) * SCALE + 1, SCALE - 1, SCALE - 1);
                    break;
                case 1: // makeS()
                    g.fillRect((block.getX() + WIDTH + 1) * SCALE + 17, (block.getY() + 3) * SCALE + 1, SCALE - 1, SCALE - 1);
                    break;
                case 2: // makeZ()
                    g.fillRect((block.getX() + WIDTH + 1) * SCALE + 17, (block.getY() + 3) * SCALE + 1, SCALE - 1, SCALE - 1);
                    break;
                case 3: // makeI()
                    g.fillRect((block.getX() + WIDTH + 2) * SCALE + 17, (block.getY() + 4) * SCALE + 1, SCALE - 1, SCALE - 1);
                    break;
                case 4: // makeL()
                    g.fillRect((block.getX() + WIDTH + 2) * SCALE + 1, (block.getY() + 3) * SCALE + 17, SCALE - 1, SCALE - 1);
                    break;
                case 5: // makeJ()
                    g.fillRect((block.getX() + WIDTH + 2) * SCALE + 1, (block.getY() + 3) * SCALE + 17, SCALE - 1, SCALE - 1);
                    break;
                case 6: // makeT()
                    g.fillRect((block.getX() + WIDTH + 1) * SCALE + 17, (block.getY() + 3) * SCALE + 1, SCALE - 1, SCALE - 1);
                    break;
            }
        }

        //Отображение координат фигурки
        Font coordinates = new Font("Bitstream Charter", Font.BOLD, 20);
        Graphics2D g2 = (Graphics2D) g;
        g2.setFont(coordinates);
        g2.setColor(Color.YELLOW);
        g2.drawString("X: " + figurine.getX() + " Y: " + (HEIGHT-1-figurine.getY()), SCALE+5, 56);

        //Отображение очков
        Font sScore = new Font("Bitstream Charter", Font.BOLD, 35);
        Graphics2D g3 = (Graphics2D) g;
        g3.setFont(sScore);
        g3.setColor(Color.YELLOW);
        g3.drawString("ОЧКИ", (WIDTH+1)*SCALE+15, 7*SCALE+5);

        g.setColor(Color.GRAY);
        g.fillRect((WIDTH+1)*SCALE+10, 7*SCALE+13, 4*SCALE-18, SCALE+5);
        g.setColor(Color.YELLOW);
        g.fillRect((WIDTH+1)*SCALE+12, 7*SCALE+16, 4*SCALE-22, SCALE);

        Font iScore = new Font("Bitstream Charter", Font.BOLD, 30);
        Graphics2D g4 = (Graphics2D) g;
        g4.setFont(iScore);
        g4.setColor(Color.BLACK);
        g4.drawString("000000".substring((""+score).length())+score, (WIDTH+1)*SCALE+15, 8*SCALE+10);
    }

    public boolean hasKeyEvents() {
        return !keyEvents.isEmpty();
    }

    public KeyEvent getEventFromTop() {
        Queue<KeyEvent> newKeyEvents = keyEvents;
        keyEvents = new ArrayBlockingQueue<KeyEvent>(100);
        return newKeyEvents.poll();
    }

    public void keyboardListener() {
        //"Наблюдатель" содержит события о нажатии клавиш
        if (hasKeyEvents()) {
            int keyCode = getEventFromTop().getKeyCode();

            //Если "ESCAPE" - закрыть игру
            if (keyCode == KeyEvent.VK_ESCAPE) { close = true; }

            //Если "P" - поставить игру на паузу
            if (keyCode == KeyEvent.VK_P) {
                if (!gameOver && !pause) pause = true;
                else pause = false;
            }

            //Если "ENTER" - создать новою фигурку
            if (keyCode == KeyEvent.VK_ENTER) {
                figurine = new Figurine(WIDTH/2, 0);
                nextFigurine = new Figurine(WIDTH/2, 0);;
                allBlocks = new ArrayList<Block>();
                score = 0;
                gameOver = false;
                pause = false;
            }

            //Проверяет конец ли игры или стоит игра на паузе
            if (gameOver || pause) return;

            //Если "стрелка вверх" - прокрутить фигурку
            if (keyCode == KeyEvent.VK_UP || keyCode == KeyEvent.VK_W) {
                figurine.spin();
            }
            //Если "стрелка вправо" - сдвинуть фигурку вправо
            else if (keyCode == KeyEvent.VK_LEFT || keyCode == KeyEvent.VK_A) {
                figurine.move(FigurineDirection.LEFT);
            }
            //Если "стрелка влево" - сдвинуть фигурку влево
            else if (keyCode == KeyEvent.VK_RIGHT || keyCode == KeyEvent.VK_D) {
                figurine.move(FigurineDirection.RIGHT);
            }
            //Если "стрелка влево" - сдвинуть фигурку влево
            else if (keyCode == KeyEvent.VK_DOWN || keyCode == KeyEvent.VK_S) {
                figurine.move(FigurineDirection.DOWN);
                score++;
            }
            //Если "SPACE" - мгновенно опустить фигурку в самый низ
            else if (keyCode == KeyEvent.VK_SPACE) {
                figurine.fall();
            }
        }
    }

    public Window() {
        game = this;
    }

    /**
     * Основной цикл программы.
     * Тут происходят все важные действия
     */
    @Override
    public void run() {
        frame = new JFrame("Тетрис");
        frame.add(game);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(((WIDTH+6)*SCALE)+14, (HEIGHT*SCALE)+37);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        frame.addKeyListener(new KeyListener() {
            public void keyTyped(KeyEvent e) {
                //Ничего не делает
            }
            public void keyReleased(KeyEvent e) {
                //Ничего не делает
            }
            public void keyPressed(KeyEvent e) {
                keyEvents.add(e);
            }
        });

        while (!close) {
            //Обрабатывает нажатие клавиш
            keyboardListener();

            //Увеличивает скорость игры в зависимости от количества набранных очков
            speed = 1000-((score/2500)*100) > 200 ? 1000-((score/2500)*100) : 300;
            repaint();

            try { Thread.sleep(43); }
            catch (InterruptedException e) { e.printStackTrace(); }
        }

        frame.setVisible(false);
        frame.dispose();
    }



    public static void setFigurine(Figurine figurine) {
        Window.figurine = figurine;
    }

    public static Figurine getNextFigurine() {
        return nextFigurine;
    }

    public static void setNextFigurine(Figurine nextFigurine) {
        Window.nextFigurine = nextFigurine;
    }

    public static ArrayList<Block> getAllBlocks() {
        return allBlocks;
    }

    public static int getScore() {
        return score;
    }

    public static void setScore(int score) {
        Window.score = score > Window.score ? score : Window.score;
    }

    public static void main(String[] args) {
        figurine = new Figurine(WIDTH/2, 0);
        nextFigurine = new Figurine(WIDTH/2, 0);
        game = new Window();
        gameOver = false;
        game.pause = false;
        gameT = new Thread(game);
        gameT.start();

        //Автодвижение фигурки вниз
        while (!close) {
            if (!gameOver && !game.pause) {
                figurine.move(FigurineDirection.DOWN);
                game.repaint();

                try { Thread.sleep(game.speed); }
                catch (InterruptedException e) { e.printStackTrace(); }
            } else System.out.print(""); //Магия!!! Не убирать, иначе не работает автодвижение фигурки после паузы!
        }
    }
}





























