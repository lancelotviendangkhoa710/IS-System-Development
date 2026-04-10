package com.bakery.controllers;

import com.bakery.main.App;
import com.bakery.main.UserSession;
import com.bakery.reports.ReportException;
import com.bakery.reports.ReportService;
import com.bakery.reports.RevenueReportResult;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import javafx.util.Duration;

public class MainController {
    private static final DateTimeFormatter CLOCK_FMT = DateTimeFormatter.ofPattern("HH:mm:ss  dd/MM/yyyy");

    @FXML
    private DatePicker fromDatePicker;

    @FXML
    private DatePicker toDatePicker;

    @FXML
    private TextField outputPathField;

    @FXML
    private Button exportButton;

    @FXML
    private Label statusLabel;

    @FXML
    private Label invoiceCountValueLabel;

    @FXML
    private Label totalRevenueValueLabel;

    @FXML
    private Label currentUserLabel;

    @FXML
    private BorderPane rootPane;

    @FXML
    private Label clockLabel;

    @FXML
    private Button themeToggleButton;

    private final ReportService reportService = new ReportService();
    private Timeline clockTimeline;

    @FXML
    private void initialize() {
        LocalDate today = LocalDate.now();
        fromDatePicker.setValue(today.minusDays(6));
        toDatePicker.setValue(today);
        outputPathField.setText(defaultOutputName());
        statusLabel.setText("Ready to export");
        invoiceCountValueLabel.setText("-");
        totalRevenueValueLabel.setText("-");
        String displayName = UserSession.getEmployeeName();
        if (displayName == null || displayName.isBlank()) {
            displayName = "Unknown";
        }
        currentUserLabel.setText("Nhan vien: " + displayName);

        applyDarkMode(UserSession.isDarkModeEnabled());
        startClock();
    }

    @FXML
    private void onChooseOutputFile() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Save revenue report");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF files", "*.pdf"));
        chooser.setInitialFileName(defaultOutputName());

        Window window = outputPathField.getScene().getWindow();
        File file = chooser.showSaveDialog(window);
        if (file != null) {
            String path = file.getAbsolutePath();
            if (!path.toLowerCase().endsWith(".pdf")) {
                path += ".pdf";
            }
            outputPathField.setText(path);
        }
    }

    @FXML
    private void onExportRevenuePdf() {
        LocalDate fromDate = fromDatePicker.getValue();
        LocalDate toDate = toDatePicker.getValue();
        String outputPath = outputPathField.getText();

        if (fromDate == null || toDate == null) {
            showAlert(Alert.AlertType.WARNING, "Missing data", "Please choose both dates.");
            return;
        }
        if (fromDate.isAfter(toDate)) {
            showAlert(Alert.AlertType.WARNING, "Invalid range", "From date must be before or equal to to date.");
            return;
        }
        if (outputPath == null || outputPath.isBlank()) {
            showAlert(Alert.AlertType.WARNING, "Missing output", "Please choose the PDF output path.");
            return;
        }

        Path pdfPath = Path.of(outputPath.trim());
        exportButton.setDisable(true);

        Task<RevenueReportResult> task = new Task<>() {
            @Override
            protected RevenueReportResult call() {
                return reportService.exportRevenueReportPdf(fromDate, toDate, pdfPath);
            }
        };

        task.setOnSucceeded(event -> {
            exportButton.setDisable(false);
            RevenueReportResult result = task.getValue();
            invoiceCountValueLabel.setText(String.valueOf(result.invoiceCount()));
            totalRevenueValueLabel.setText(formatMoney(result.totalRevenue()));
            Path generated = result.outputPdf();
            statusLabel.setText("Export completed");
            showAlert(Alert.AlertType.INFORMATION, "Success", "Report saved to:\n" + generated.toAbsolutePath());
        });
        task.setOnFailed(event -> {
            exportButton.setDisable(false);
            Throwable error = task.getException();
            String message = error instanceof ReportException ? error.getMessage() : safeMessage(error);
            statusLabel.setText("Export failed");
            showAlert(Alert.AlertType.ERROR, "Export failed", message);
        });

        Thread worker = new Thread(task, "revenue-report-export");
        worker.setDaemon(true);
        worker.start();
    }

    private static String defaultOutputName() {
        LocalDate today = LocalDate.now();
        return "BaoCaoDoanhThu_" + today.minusDays(6) + "_" + today + ".pdf";
    }

    @FXML
    private void onLogout() {
        stopClock();
        UserSession.clear();
        try {
            App.showLogin();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Navigation error", safeMessage(e));
        }
    }

    @FXML
    private void onGoChangePassword() {
        stopClock();
        try {
            App.showChangePassword();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Navigation error", safeMessage(e));
        }
    }

    @FXML
    private void onGoHome() {
        stopClock();
        try {
            App.showHome();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Navigation error", safeMessage(e));
        }
    }

    @FXML
    private void onToggleDarkMode() {
        boolean newValue = !UserSession.isDarkModeEnabled();
        UserSession.setDarkModeEnabled(newValue);
        applyDarkMode(newValue);
    }

    private void applyDarkMode(boolean enabled) {
        if (rootPane == null) {
            return;
        }
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
        if (clockLabel != null) {
            clockLabel.setText(CLOCK_FMT.format(LocalDateTime.now()));
        }
    }

    private static String formatMoney(double value) {
        return String.format(Locale.forLanguageTag("vi-VN"), "%,.0f VND", value);
    }

    private static String safeMessage(Throwable t) {
        if (t == null) {
            return "Unknown error";
        }
        String msg = t.getMessage();
        return (msg == null || msg.isBlank()) ? t.getClass().getSimpleName() : msg;
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
