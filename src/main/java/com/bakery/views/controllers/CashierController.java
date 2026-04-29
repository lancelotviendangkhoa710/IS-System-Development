package com.bakery.views.controllers;

import com.bakery.main.App;
import com.bakery.services.AuthService;
import com.bakery.utils.SessionContext;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

public class CashierController {
    
    private final AuthService authService = new AuthService();

    @FXML private Label lblCashierName;
    @FXML private Label lblShiftStatus;
    @FXML private Label lblHeaderTitle;
    @FXML private Label lblClock;

    @FXML private Button btnMenuOverview;
    @FXML private Button btnMenuPOS;
    @FXML private Button btnMenuCustomOrders;
    @FXML private Button btnMenuCustomers;
    @FXML private Button btnMenuHistory;

    @FXML private VBox paneOverview;
    @FXML private VBox panePlaceholder;
    @FXML private Label lblPlaceholderMessage;

    @FXML private Label lblTotalRevenue;
    @FXML private Label lblTotalInvoices;
    @FXML private Label lblTotalCustomOrders;

    private Timeline clockTimeline;

    @FXML
    private void initialize() {
        startClock();
        loadSessionInfo();
        handleNavOverview(); // Default view
    }

    private void startClock() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        clockTimeline = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            lblClock.setText(LocalDateTime.now().format(formatter));
        }), new KeyFrame(Duration.seconds(1)));
        clockTimeline.setCycleCount(Animation.INDEFINITE);
        clockTimeline.play();
    }

    private void loadSessionInfo() {
        SessionContext.AuthSession session = authService.getCurrentSession();
        if (session != null) {
            lblCashierName.setText(session.getHoTen());
            // TODO: Call ShiftService to get active shift info. Simulated for MVP.
            lblShiftStatus.setText("Ca làm việc: Đang mở (CA_AUTO)");
        }
    }

    // --- NAVIGATION --- //

    private void resetMenuStyles() {
        List<Button> buttons = Arrays.asList(btnMenuOverview, btnMenuPOS, btnMenuCustomOrders, btnMenuCustomers, btnMenuHistory);
        for (Button btn : buttons) {
            btn.getStyleClass().remove("active");
        }
    }

    @FXML
    private void handleNavOverview() {
        resetMenuStyles();
        btnMenuOverview.getStyleClass().add("active");
        lblHeaderTitle.setText("Tổng quan Giao dịch");
        
        paneOverview.setVisible(true);
        paneOverview.setManaged(true);
        panePlaceholder.setVisible(false);
        panePlaceholder.setManaged(false);

        // Load metrics (Mock data for MVP, to be connected to DAO)
        lblTotalRevenue.setText("0 đ");
        lblTotalInvoices.setText("0");
        lblTotalCustomOrders.setText("0");
    }

    @FXML
    private void handleNavPOS() {
        navigateToModule(btnMenuPOS, "Bán hàng POS", "Chức năng Bán hàng POS đang được xây dựng.");
    }

    @FXML
    private void handleNavCustomOrders() {
        navigateToModule(btnMenuCustomOrders, "Đơn đặt bánh", "Danh sách Đơn khách theo yêu cầu riêng.");
    }

    @FXML
    private void handleNavCustomers() {
        navigateToModule(btnMenuCustomers, "Quản lý Khách hàng", "Chức năng Hàng thành viên và Khách hàng.");
    }

    @FXML
    private void handleNavHistory() {
        navigateToModule(btnMenuHistory, "Lịch sử hóa đơn", "Dữ liệu biên lai các giao dịch đã thực hiện trong ca.");
    }

    private void navigateToModule(Button activeBtn, String title, String placeholderMsg) {
        resetMenuStyles();
        activeBtn.getStyleClass().add("active");
        lblHeaderTitle.setText(title);

        paneOverview.setVisible(false);
        paneOverview.setManaged(false);
        panePlaceholder.setVisible(true);
        panePlaceholder.setManaged(true);
        
        lblPlaceholderMessage.setText(placeholderMsg);
    }

    // --- ACTIONS --- //

    @FXML
    private void handleShiftReconciliation() {
        showAlert(Alert.AlertType.INFORMATION, "Đối soát", "Chức năng Đối soát đóng ca sẽ tổng hợp tiền mặt theo Use Case UC17.");
    }

    @FXML
    private void handleChangePassword() {
        // Will route to Password screen or show a small dialog
        showAlert(Alert.AlertType.INFORMATION, "Đổi mật khẩu", "Truy cập giao diện đổi mật khẩu cá nhân.");
    }

    @FXML
    private void handleLogout() {
        if (clockTimeline != null) clockTimeline.stop();
        authService.logout();
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource(App.LOGIN_VIEW));
            Parent root = loader.load();
            MainController controller = loader.getController();
            controller.setLoginInfo("Đã đăng xuất tài khoản Thu ngân.");
            
            Stage stage = (Stage) lblCashierName.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
        } catch (Exception ex) {
            showAlert(Alert.AlertType.ERROR, "Lỗi đăng xuất", ex.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Platform.runLater(() -> {
            Alert alert = new Alert(type);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(content);
            alert.showAndWait();
        });
    }
}
