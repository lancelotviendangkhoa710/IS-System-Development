package com.bakery.controllers;

import com.bakery.dao.NhanVienDAO;
import com.bakery.dto.NhanVienDTO;
import com.bakery.main.App;
import com.bakery.main.UserSession;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.util.Duration;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LoginController {
    private static final DateTimeFormatter CLOCK_FMT = DateTimeFormatter.ofPattern("HH:mm:ss  dd/MM/yyyy");

    @FXML
    private BorderPane rootPane;
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label messageLabel;
    @FXML
    private Label clockLabel;
    @FXML
    private Button themeToggleButton;

    private final NhanVienDAO nhanVienDAO = new NhanVienDAO();
    private Timeline clockTimeline;

    @FXML
    private void initialize() {
        messageLabel.setText("");
        applyDarkMode(UserSession.isDarkModeEnabled());
        startClock();
    }

    @FXML
    private void onLogin() {
        String username = safeTrim(usernameField.getText());
        String password = safeTrim(passwordField.getText());

        if (username.isEmpty() || password.isEmpty()) {
            showError("Vui long nhap ten dang nhap va mat khau.");
            return;
        }

        try {
            NhanVienDTO nhanVien = nhanVienDAO.dangNhap(username, password);
            UserSession.login(nhanVien.getMaNV(), nhanVien.getHoTen(), nhanVien.getTenDangNhap());
            stopClock();
            App.showHome();
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    @FXML
    private void onGoRegister() {
        try {
            stopClock();
            App.showRegister();
        } catch (Exception e) {
            showError("Khong mo duoc man hinh dang ky.");
        }
    }

    @FXML
    private void onToggleDarkMode() {
        boolean newValue = !UserSession.isDarkModeEnabled();
        UserSession.setDarkModeEnabled(newValue);
        applyDarkMode(newValue);
    }

    private void applyDarkMode(boolean enabled) {
        if (enabled) {
            if (!rootPane.getStyleClass().contains("dark-mode")) {
                rootPane.getStyleClass().add("dark-mode");
            }
            themeToggleButton.setText("Light mode");
        } else {
            rootPane.getStyleClass().remove("dark-mode");
            themeToggleButton.setText("Dark mode");
        }
    }

    private void startClock() {
        updateClock();
        clockTimeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> updateClock()));
        clockTimeline.setCycleCount(Timeline.INDEFINITE);
        clockTimeline.play();
    }

    private void stopClock() {
        if (clockTimeline != null) {
            clockTimeline.stop();
        }
    }

    private void updateClock() {
        clockLabel.setText(CLOCK_FMT.format(LocalDateTime.now()));
    }

    private void showError(String message) {
        messageLabel.getStyleClass().setAll("form-message", "danger");
        messageLabel.setText(message);
    }

    private static String safeTrim(String value) {
        return value == null ? "" : value.trim();
    }
}
