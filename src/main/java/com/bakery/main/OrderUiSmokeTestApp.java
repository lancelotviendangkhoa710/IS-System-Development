package com.bakery.main;

import com.formdev.flatlaf.FlatLightLaf;
import com.bakery.presenters.OrderPresenter;
import com.bakery.services.OrderService;
import com.bakery.views.OrderViewPanel;

import javax.swing.*;

public class OrderUiSmokeTestApp {
    public static void main(String[] args) {

        // Liệt kê các file trong thư mục resources (nếu có thể)
        try {
            java.io.File resDir = new java.io.File("src/main/resources");
            if (resDir.exists() && resDir.isDirectory()) {
                System.out.println("Files in src/main/resources:");
                for (String f : resDir.list()) System.out.println("  - " + f);
            } else {
                System.out.println("Directory 'src/main/resources' NOT FOUND at project root.");
            }
        } catch (Exception e) {
            System.out.println("Error listing local resources: " + e.getMessage());
        }
        System.out.println("====================");

        SwingUtilities.invokeLater(() -> {
            try {
                FlatLightLaf.setup();
                UIManager.put("Button.arc", 14);
                UIManager.put("Component.arc", 10);
                UIManager.put("TextComponent.arc", 10);
            } catch (Exception ignored) {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception ignoredFallback) {
                }
            }

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
