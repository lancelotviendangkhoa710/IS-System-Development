package com.bakery.controllers;

import com.bakery.dao.NhanVienDAO;
import com.bakery.main.App;
import com.bakery.main.UserSession;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.BorderPane;
import javafx.util.Duration;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ChangePasswordController {
    private static final DateTimeFormatter CLOCK_FMT = DateTimeFormatter.ofPattern("HH:mm:ss  dd/MM/yyyy");

    @FXML
    private BorderPane rootPane;
    @FXML
    private Label userInfoLabel;
    @FXML
    private PasswordField currentPasswordField;
    @FXML
    private PasswordField newPasswordField;
    @FXML
    private PasswordField confirmNewPasswordField;
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
        String name = UserSession.getEmployeeName();
        if (name == null || name.isBlank()) {
            name = "Unknown";
        }
        userInfoLabel.setText("Tai khoan: " + name);
        messageLabel.setText("");
        applyDarkMode(UserSession.isDarkModeEnabled());
        startClock();
    }

    @FXML
    private void onSaveNewPassword() {
        Integer maNV = UserSession.getEmployeeId();
        if (maNV == null) {
            showError("Phien dang nhap khong hop le. Vui long dang nhap lai.");
            return;
        }

        String currentPassword = safeTrim(currentPasswordField.getText());
        String newPassword = safeTrim(newPasswordField.getText());
        String confirmPassword = safeTrim(confirmNewPasswordField.getText());

        if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            showError("Vui long nhap day du thong tin.");
            return;
        }
        if (!newPassword.equals(confirmPassword)) {
            showError("Mat khau moi va nhap lai khong khop.");
            return;
        }
        if (newPassword.length() < 3) {
            showError("Mat khau moi toi thieu 3 ky tu.");
            return;
        }
        if (newPassword.equals(currentPassword)) {
            showError("Mat khau moi phai khac mat khau hien tai.");
            return;
        }

        try {
            boolean updated = nhanVienDAO.doiMatKhau(maNV, currentPassword, newPassword);
            if (updated) {
                messageLabel.getStyleClass().setAll("form-message", "success");
                messageLabel.setText("Doi mat khau thanh cong.");
                currentPasswordField.clear();
                newPasswordField.clear();
                confirmNewPasswordField.clear();
            } else {
                showError("Khong doi duoc mat khau.");
            }
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    @FXML
    private void onBackDashboard() {
        try {
            stopClock();
            App.showHome();
        } catch (Exception e) {
            showError("Khong mo duoc man hinh bao cao.");
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
