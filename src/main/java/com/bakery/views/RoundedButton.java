package com.bakery.views;

import javax.swing.JButton;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

public class RoundedButton extends JButton {
    private static final Color NORMAL_COLOR = new Color(116, 66, 51);
    private static final Color HOVER_COLOR = new Color(143, 86, 67);
    private static final Color BORDER_COLOR = new Color(63, 35, 25);
    private static final Color TEXT_COLOR = new Color(254, 249, 225);

    private boolean hovered;

    public RoundedButton(String text) {
        super(text);
        setFont(new Font("Segoe UI", Font.BOLD, 24));
        setForeground(TEXT_COLOR);
        setFocusPainted(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setOpaque(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setRolloverEnabled(false);

        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                hovered = true;
                repaint();
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                hovered = false;
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(hovered ? HOVER_COLOR : NORMAL_COLOR);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 36, 36);

        if (hovered) {
            g2.setColor(new Color(255, 255, 255, 28));
            g2.fillRoundRect(8, 8, getWidth() - 16, getHeight() / 2, 28, 28);
        }

        g2.dispose();
        super.paintComponent(g);
    }

    @Override
    protected void paintBorder(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(BORDER_COLOR);
        g2.setStroke(new BasicStroke(4f));
        g2.drawRoundRect(2, 2, getWidth() - 5, getHeight() - 5, 36, 36);
        g2.dispose();
    }
}
