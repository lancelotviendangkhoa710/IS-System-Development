package com.bakery.controllers;

import com.bakery.main.App;
import com.bakery.main.UserSession;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.util.Duration;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class HomeController {
    private static final DateTimeFormatter CLOCK_FMT = DateTimeFormatter.ofPattern("HH:mm:ss  dd/MM/yyyy");

    @FXML
    private BorderPane rootPane;
    @FXML
    private Label welcomeLabel;

    @FXML
    private Label subtitleLabel;
    @FXML
    private Label clockLabel;
    @FXML
    private Button themeToggleButton;

    private Timeline clockTimeline;

    @FXML
    private void initialize() {
        String name = UserSession.getEmployeeName();
        if (name == null || name.isBlank()) {
            name = "Nhan vien";
        }
        welcomeLabel.setText("Xin chao, " + name + "!");
        subtitleLabel.setText("Chon chuc nang ban muon thuc hien trong he thong.");
        applyDarkMode(UserSession.isDarkModeEnabled());
        startClock();
    }

    @FXML
    private void onOpenRevenueReport() {
        try {
            stopClock();
            App.showDashboard();
        } catch (Exception ignored) {
        }
    }

    @FXML
    private void onOpenChangePassword() {
        try {
            stopClock();
            App.showChangePassword();
        } catch (Exception ignored) {
        }
    }

    @FXML
    private void onLogout() {
        UserSession.clear();
        try {
            stopClock();
            App.showLogin();
        } catch (Exception ignored) {
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
}
