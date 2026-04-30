package com.bakery.views.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.net.URL;

public class KhoViewFXMLController {
    @FXML private Label lblThongBao;

    @FXML
    private void onVeMenu() {
        moScene("/fxml/MainMenuView.fxml", "H3K Bakery - Menu chuc nang", 1366, 768);
    }

    @FXML
    private void onMoPOS() {
        moScene("/fxml/DonHangView.fxml", "H3K Bakery - POS", 1280, 720);
    }

    @FXML
    private void onActionTam() {
        lblThongBao.setText("Da ghi nhan thao tac kho (demo giao dien).");
    }

    private void moScene(String fxmlPath, String title, int width, int height) {
        try {
            URL fxmlUrl = getClass().getResource(fxmlPath);
            if (fxmlUrl == null) {
                throw new RuntimeException("Khong tim thay " + fxmlPath);
            }
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Scene scene = new Scene(loader.load(), width, height);
            if ("/fxml/MainMenuView.fxml".equals(fxmlPath)) {
                MainMenuViewFXMLController controller = loader.getController();
                controller.khoiTaoThongTinDangNhap(com.bakery.utils.UserSession.getCurrentUser());
            }
            URL cssUrl = getClass().getResource("/css/bakery.css");
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
            }
            Stage stage = (Stage) lblThongBao.getScene().getWindow();
            stage.setTitle(title);
            stage.setScene(scene);
            stage.centerOnScreen();
        } catch (Exception ex) {
            lblThongBao.setText("Khong the mo man hinh: " + ex.getMessage());
        }
    }
}
