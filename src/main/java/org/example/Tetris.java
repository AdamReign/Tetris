package org.example;

import org.example.controller.BoardController;
import org.example.service.TetrominoService;
import org.example.view.BoardView;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

public class Tetris {
    public static void main(String[] args) {
        int SCALE = 32;
        int WIDTH = 16;
        int HEIGHT = 22;
        BoardView boardView = new BoardView(WIDTH, HEIGHT, SCALE);





        TetrominoService tetrominoService = new TetrominoService(boardView);
        Queue<KeyEvent> keyEvents = new ArrayBlockingQueue<>(1);
        BoardController boardController = new BoardController(boardView, tetrominoService, keyEvents);




        JFrame frame = new JFrame("Тетріс");
        // Вказує об'єкт, який буде малювати на екрані
        frame.add(boardView);
        // Встановлює дію, яка відбудеться при закритті вікна
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Встановлює розмір вікна
        frame.setSize(((WIDTH+6)*SCALE)+14, (HEIGHT*SCALE)+37);
        // Забороняє змінювати розмір вікна за допомогою миші
        frame.setResizable(false);
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
