package main;

import enemies.Rat;
import javafx.application.Application;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;

public class Main extends Application {

    public static void main(String[] args) {
        Application.launch();
    }

    @Override
    public void start(Stage stage) {
        System.out.println("lets gooooo");

//        JFXThread jfxAudioPlayer = new JFXThread();
//        jfxAudioPlayer.letsGo();

        JFrame window = new JFrame();

        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(true);
        window.setTitle("Pépito Returned Home");

        try {
            GamePanel gamePanel = new GamePanel(this, window);
            window.add(gamePanel);

            window.setBackground(Color.BLACK);

            Rat THE_TRUE_RAT;

            window.addComponentListener(new ComponentAdapter() {
                public void componentResized(ComponentEvent componentEvent) {
                gamePanel.width = (short) (componentEvent.getComponent().getWidth() - 16);
                gamePanel.height = (short) (componentEvent.getComponent().getHeight() - 39);

                gamePanel.onResizeEvent();
                }
            });

            window.addWindowFocusListener(new WindowAdapter() {
                public void windowGainedFocus(WindowEvent e) {
                    gamePanel.isFocused = true;
                    gamePanel.repaint();
                    gamePanel.everySecond10th.remove("unfocusedRender");
                }

                public void windowLostFocus(WindowEvent e) {
                    gamePanel.isFocused = false;
                    gamePanel.repaint();
                    gamePanel.everySecond10th.put("unfocusedRender", () -> gamePanel.repaint(0, 0, gamePanel.getWidth(), gamePanel.getHeight()));
                }
            });
            
            window.pack();

            window.setLocationRelativeTo(null);
            window.setVisible(true);

            try {
                window.setIconImage(ImageIO.read(Objects.requireNonNull(Main.class.getResourceAsStream("/utils/icon.png"))));
            } catch (IOException exception) {
                exception.printStackTrace();
            }

            gamePanel.startThread();

            int boba = (int) (Math.random() * 347);
            if (boba == 1) {
                JFrame bobaWindow = new JFrame();

                bobaWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                bobaWindow.setResizable(false);
                bobaWindow.setTitle("boba");

                bobaWindow.setBackground(Color.BLACK);

                URL url = getClass().getResource("/boba.gif");

                Icon icon = new ImageIcon(url);
                JLabel label = new JLabel(icon);
                bobaWindow.getContentPane().add(label);
                bobaWindow.pack();

                bobaWindow.setVisible(true);
                window.setLocationRelativeTo(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


//    @Override
//    public void stop() throws Exception {
//        System.out.println("Bye bye <3");
//    }
}
