package com.bakery.main;

import com.bakery.presenters.OrderPresenter;
import com.bakery.services.OrderService;
import com.bakery.views.OrderViewPanel;

import javax.swing.*;

public class OrderUiSmokeTestApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {}

            JFrame frame = new JFrame("UI Smoke Test - Order Module (MVP Swing)");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1000, 820);
            frame.setLocationRelativeTo(null);

            // Khởi tạo kiến trúc MVP
            OrderViewPanel view = new OrderViewPanel();
            OrderService service = new OrderService();
            OrderPresenter presenter = new OrderPresenter(view, service);
            view.setPresenter(presenter);
            presenter.taiDuLieuBanDau();

            frame.add(view);
            frame.setVisible(true);
        });
    }
}
