package com.bakery.main;

import com.bakery.views.LoginFrame;
import com.bakery.views.StartScreen;
import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.UIManager;

public class App {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception e) {
            System.err.println("Không thể khởi tạo giao diện Flatlaf: " + e.getMessage());
        }

        StartScreen.open();
    }
}