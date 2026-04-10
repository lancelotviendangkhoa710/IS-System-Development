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

public class RegisterController {
    private static final DateTimeFormatter CLOCK_FMT = DateTimeFormatter.ofPattern("HH:mm:ss  dd/MM/yyyy");

    @FXML
    private BorderPane rootPane;
    @FXML
    private TextField nameField;
    @FXML
    private TextField phoneField;
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField confirmPasswordField;
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
    private void onRegister() {
        String fullName = safeTrim(nameField.getText());
        String phone = safeTrim(phoneField.getText());
        String username = safeTrim(usernameField.getText());
        String password = safeTrim(passwordField.getText());
        String confirmPassword = safeTrim(confirmPasswordField.getText());

        if (fullName.isEmpty() || phone.isEmpty() || username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showError("Vui long nhap day du thong tin.");
            return;
        }
        if (!phone.matches("\\d{9,15}")) {
            showError("So dien thoai phai tu 9 den 15 chu so.");
            return;
        }
        if (!password.equals(confirmPassword)) {
            showError("Mat khau nhap lai khong khop.");
            return;
        }
        if (password.length() < 3) {
            showError("Mat khau toi thieu 3 ky tu.");
            return;
        }

        try {
            NhanVienDTO created = nhanVienDAO.taoTaiKhoanNhanVien(fullName, phone, username, password);
            messageLabel.getStyleClass().setAll("form-message", "success");
            messageLabel.setText("Da tao tai khoan: " + created.getTenDangNhap() + ". Bam 'Quay lai dang nhap'.");
            clearForm();
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    @FXML
    private void onBackToLogin() {
        try {
            stopClock();
            App.showLogin();
        } catch (Exception e) {
            showError("Khong mo duoc man hinh dang nhap.");
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

    private void clearForm() {
        nameField.clear();
        phoneField.clear();
        usernameField.clear();
        passwordField.clear();
        confirmPasswordField.clear();
    }

    private void showError(String message) {
        messageLabel.getStyleClass().setAll("form-message", "danger");
        messageLabel.setText(message);
    }

    private static String safeTrim(String value) {
        return value == null ? "" : value.trim();
    }
}
