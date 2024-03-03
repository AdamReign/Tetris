package org.example;

import org.example.controller.BoardController;
import org.example.model.Board;
import org.example.service.BoardService;
import org.example.service.TetrominoService;
import org.example.view.BoardView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

public class Tetris {
    public static void main(String[] args) {
        int scale = 32;
        int width = 17;
        int height = 22;
        ImageIcon imageIcon =  new ImageIcon(Objects.requireNonNull(Tetris.class.getResource("/background.png")));
        Color color = Color.BLACK;
        Board board = new Board(width, height, scale, imageIcon, color);
        BoardView boardView = new BoardView(board);


        TetrominoService tetrominoService = new TetrominoService(board);
        BoardService boardService = new BoardService(board, tetrominoService);
        Queue<KeyEvent> keyEvents = new ArrayBlockingQueue<>(1);
        BoardController boardController = new BoardController(board, boardView, tetrominoService, boardService, keyEvents);




        JFrame frame = new JFrame("Тетріс");
        // Вказує об'єкт, який буде малювати на екрані
        frame.add(boardView);
        // Встановлює дію, яка відбудеться при закритті вікна
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Встановлює розмір вікна
        int heightWindow = (height * scale);
        int widthWindow = heightWindow;
        frame.setSize(widthWindow + 14, heightWindow + 37);
        // Забороняє змінювати розмір вікна за допомогою миші
//        frame.setResizable(false);
        // Встановлює позицію вікна по центру екрана
        frame.setLocationRelativeTo(null);
        // Робить вікно видимим, виводить його на екран
        frame.setVisible(true);
        // Додає читач натискань клавіш клавіатури
        frame.addKeyListener(new KeyListener() {
            // Викликається, коли користувач натискає і відпускає клавішу
            public void keyTyped(KeyEvent e) {

            }
            // Викликається, коли користувач відпускає натиснуту клавішу
            public void keyReleased(KeyEvent e) {

            }
            // Викликається, коли користувач натискає на клавішу
            public void keyPressed(KeyEvent e) {
                keyEvents.add(e);
            }
        });






        try {
            Thread thread1 = new Thread(boardController::keyboard);
            thread1.start();

            Thread thread2 = new Thread(boardController::fall);
            thread2.start();

            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }













        frame.setVisible(false);
        frame.dispose();
    }
}
