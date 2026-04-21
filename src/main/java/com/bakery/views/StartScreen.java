package com.bakery.views;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.io.IOException;
import java.net.URL;

public class StartScreen extends JFrame {
    private static final String BACKGROUND_RESOURCE = "/start-screen.png";

    public StartScreen() {
        initComponents();
    }

    private void initComponents() {
        setTitle("Amitie Bakery");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1365, 768);
        setLocationRelativeTo(null);
        setResizable(false);

        BackgroundPanel backgroundPanel = new BackgroundPanel();
        backgroundPanel.setLayout(null);

        RoundedButton btnLogin = new RoundedButton("Login");
        btnLogin.setBounds(800, 385, 270, 70);
        btnLogin.addActionListener(e -> LoginFrame.open());

        RoundedButton btnRegister = new RoundedButton("Register");
        btnRegister.setBounds(800, 515, 270, 70);
        btnRegister.addActionListener(e -> RegisterFrame.open());

        backgroundPanel.add(btnLogin);
        backgroundPanel.add(btnRegister);

        setContentPane(backgroundPanel);
    }

    private static class BackgroundPanel extends JPanel {
        private Image backgroundImage;

        BackgroundPanel() {
            try {
                URL imageUrl = getClass().getResource(BACKGROUND_RESOURCE);
                if (imageUrl == null) {
                    System.out.println("Không tìm thấy ảnh nền " + BACKGROUND_RESOURCE);
                    return;
                }
                backgroundImage = ImageIO.read(imageUrl);
            } catch (IOException e) {
                System.out.println("Không tải được ảnh nền: " + e.getMessage());
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(1365, 768);
        }
    }

    public static void open() {
        SwingUtilities.invokeLater(() -> new StartScreen().setVisible(true));
    }
}
