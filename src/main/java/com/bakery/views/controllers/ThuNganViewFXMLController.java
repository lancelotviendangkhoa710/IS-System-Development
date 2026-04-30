package com.bakery.views.controllers;

import com.bakery.main.App;
import com.bakery.services.XacThucService;
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

/**
 * Controller cho màn hình giao diện Thu ngân (Cashier).
 * Quản lý các chức năng: Tổng quan, Bán hàng POS, Đơn đặt bánh, Khách hàng, Lịch sử hóa đơn.
 */
public class ThuNganViewFXMLController {

    private final XacThucService xacThucService = new XacThucService();

    // --- FXML Controls ---
    @FXML
    private Label lblCashierName;      // Tên thu ngân đang đăng nhập
    @FXML
    private Label lblShiftStatus;     // Trạng thái ca làm việc (Mã ca)
    @FXML
    private Label lblHeaderTitle;     // Tiêu đề phân hệ hiện tại
    @FXML
    private Label lblClock;           // Đồng hồ hiển thị thời gian thực

    @FXML
    private Button btnMenuOverview;    // Nút menu Tổng quan
    @FXML
    private Button btnMenuPOS;         // Nút menu Bán hàng POS
    @FXML
    private Button btnMenuCustomOrders; // Nút menu Đơn đặt bánh
    @FXML
    private Button btnMenuCustomers;    // Nút menu Khách hàng
    @FXML
    private Button btnMenuHistory;      // Nút menu Lịch sử hóa đơn

    @FXML
    private VBox paneOverview;         // Panel hiển thị các chỉ số tổng quan
    @FXML
    private VBox panePlaceholder;      // Panel tạm thời cho các chức năng đang phát triển
    @FXML
    private Label lblPlaceholderMessage; // Thông báo trên panel tạm thời

    @FXML
    private Label lblTotalRevenue;      // Hiển thị Tổng doanh thu trong ca
    @FXML
    private Label lblTotalInvoices;     // Hiển thị Tổng số hóa đơn
    @FXML
    private Label lblTotalCustomOrders; // Hiển thị Tổng đơn đặt bánh

    private Timeline clockTimeline;    // Timeline để cập nhật đồng hồ mỗi giây

    /**
     * Khởi tạo các thành phần giao diện khi View được load.
     */
    @FXML
    private void initialize() {
        startClock();
        loadSessionInfo();
        handleNavOverview(); // Mặc định hiển thị tab Tổng quan
    }

    /**
     * Bắt đầu chạy đồng hồ cập nhật mỗi giây.
     */
    private void startClock() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        clockTimeline = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            lblClock.setText(LocalDateTime.now().format(formatter));
        }), new KeyFrame(Duration.seconds(1)));
        clockTimeline.setCycleCount(Animation.INDEFINITE);
        clockTimeline.play();
    }

    /**
     * Hiển thị thông tin nhân viên và trạng thái ca từ SessionContext.
     */
    private void loadSessionInfo() {
        SessionContext.AuthSession session = xacThucService.layPhienHienTai();
        if (session != null) {
            lblCashierName.setText(session.getHoTen());

            lblShiftStatus.setText("Ca làm việc: Đang mở (CA_AUTO)");
        }
    }

    // --- ĐIỀU HƯỚNG MENU (NAVIGATION) ---

    /**
     * Xóa trạng thái 'active' (CSS class) của tất cả các nút menu.
     */
    private void resetMenuStyles() {
        List<Button> buttons = Arrays.asList(btnMenuOverview, btnMenuPOS, btnMenuCustomOrders, btnMenuCustomers,
                btnMenuHistory);
        for (Button btn : buttons) {
            btn.getStyleClass().remove("active");
        }
    }

    /**
     * Chuyển hướng sang màn hình Tổng quan giao dịch.
     */
    @FXML
    private void handleNavOverview() {
        resetMenuStyles();
        btnMenuOverview.getStyleClass().add("active");
        lblHeaderTitle.setText("Tổng quan Giao dịch");

        paneOverview.setVisible(true);
        paneOverview.setManaged(true);
        panePlaceholder.setVisible(false);
        panePlaceholder.setManaged(false);

        // Load các chỉ số (Mock data cho MVP, sẽ kết nối DAO sau)
        lblTotalRevenue.setText("0 đ");
        lblTotalInvoices.setText("0");
        lblTotalCustomOrders.setText("0");
    }

    /**
     * Chuyển hướng sang module Bán hàng POS.
     */
    @FXML
    private void handleNavPOS() {
        navigateToModule(btnMenuPOS, "Bán hàng POS", "Chức năng Bán hàng POS đang được xây dựng.");
    }

    /**
     * Chuyển hướng sang module Quản lý Đơn đặt bánh.
     */
    @FXML
    private void handleNavCustomOrders() {
        navigateToModule(btnMenuCustomOrders, "Đơn đặt bánh", "Danh sách đơn khách theo yêu cầu riêng.");
    }

    /**
     * Chuyển hướng sang module Quản lý Khách hàng.
     */
    @FXML
    private void handleNavCustomers() {
        navigateToModule(btnMenuCustomers, "Quản lý Khách hàng",
                "Chức năng Hàng thành viên và Khách hàng.");
    }

    /**
     * Chuyển hướng sang module Lịch sử hóa đơn.
     */
    @FXML
    private void handleNavHistory() {
        navigateToModule(btnMenuHistory, "Lịch sử hóa đơn",
                "Dữ liệu biên lai các giao dịch đã thực hiện trong ca.");
    }

    /**
     * Phương thức dùng chung để chuyển sang các module đang phát triển (dùng placeholder).
     */
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

    // --- CÁC HÀNH ĐỘNG HỆ THỐNG (ACTIONS) ---

    /**
     * Xử lý đối soát và đóng ca làm việc (UC17).
     */
    @FXML
    private void handleShiftReconciliation() {
        showAlert(Alert.AlertType.INFORMATION, "Đối soát",
                "Chức năng Đối soát đóng ca sẽ tổng hợp tiền mặt theo Use Case UC17.");
    }

    /**
     * Chuyển đến màn hình đổi mật khẩu cá nhân.
     */
    @FXML
    private void handleChangePassword() {
        showAlert(Alert.AlertType.INFORMATION, "Đổi mật khẩu",
                "Truy cập giao diện đổi mật khẩu cá nhân.");
    }

    /**
     * Đăng xuất khỏi hệ thống và quay về màn hình Đăng nhập.
     */
    @FXML
    private void handleLogout() {
        if (clockTimeline != null)
            clockTimeline.stop();
        xacThucService.dangXuat();
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource(App.LOGIN_VIEW));
            Parent root = loader.load();
            DangNhapViewFXMLController controller = loader.getController();
            controller.setLoginInfo("Đã đăng xuất tài khoản Thu ngân.");

            Stage stage = (Stage) lblCashierName.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
        } catch (Exception ex) {
            showAlert(Alert.AlertType.ERROR, "Lỗi đăng xuất", ex.getMessage());
        }
    }

    /**
     * Hiển thị thông báo (Alert) cho người dùng.
     */
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
