package com.bakery.main;

import com.bakery.controllers.OrderController;
import com.bakery.views.OrderViewPanel;

import javax.swing.*;

public class OrderUiSmokeTestApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {}

            JFrame frame = new JFrame("UI Smoke Test - Order Module (MVC Swing)");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1000, 820);
            frame.setLocationRelativeTo(null);

            // BƯỚC QUAN TRỌNG: Khởi tạo kiến trúc MVC
            OrderViewPanel view = new OrderViewPanel();              // 1. Khởi tạo View
            OrderController controller = new OrderController(view);  // 2. Khởi tạo Controller, tiêm View vào
            view.setController(controller);                          // 3. Tiêm ngược Controller về View để bắt sự kiện
            controller.taiDuLieuBanDau();                            // 4. Controller bắt đầu gọi data đẩy lên View

            frame.add(view);
            frame.setVisible(true);
        });
    }
}