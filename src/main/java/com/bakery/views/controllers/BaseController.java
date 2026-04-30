package com.bakery.views.controllers;

import com.bakery.main.App;
import com.bakery.utils.UserSession;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.net.URL;

/**
 * Lớp cơ sở cho các Controller trong hệ thống.
 * Chứa các phương thức dùng chung cho thông báo, điều hướng và quản lý giao diện.
 */
public abstract class BaseController {

    @FXML
    protected Label lblThongBao;

    /**
     * Hiển thị thông báo lỗi qua Popup Alert.
     */
    protected void hienThiThongBaoLoi(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Hiển thị thông báo cảnh báo qua Popup Alert.
     */
    protected void hienThiCanhBao(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Hiển thị thông báo thành công/thông tin qua Popup Alert.
     */
    protected void hienThiThongTin(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Hiển thị thông báo lỗi lên thanh trạng thái (Label).
     */
    protected void hienThiLoiLabel(String msg) {
        if (lblThongBao != null) {
            lblThongBao.setText(msg);
            lblThongBao.getStyleClass().removeAll("lbl-success");
            lblThongBao.getStyleClass().add("lbl-danger");
        }
    }

    /**
     * Hiển thị thông báo thành công lên thanh trạng thái (Label).
     */
    protected void hienThiThanhCongLabel(String msg) {
        if (lblThongBao != null) {
            lblThongBao.setText(msg);
            lblThongBao.getStyleClass().removeAll("lbl-danger");
            lblThongBao.getStyleClass().add("lbl-success");
        }
    }

    /**
     * Quay lại màn hình Menu chính và khởi tạo lại phiên đăng nhập.
     * 
     * @param sourceNode Một node bất kỳ trên Scene hiện tại để lấy Stage
     */
    protected void quayLaiMenuChinh(Node sourceNode) {
        transitionTo(sourceNode, App.MAIN_MENU_VIEW, "H3K Bakery - Hệ thống Quản trị", 1366, 768);
    }

    /**
     * Chuyển màn hình linh hoạt giữa các module.
     * 
     * @param sourceNode Một node bất kỳ trên Scene hiện tại để lấy Stage
     * @param fxmlPath Đường dẫn tới file FXML
     * @param title Tiêu đề cửa sổ
     * @param width Chiều rộng
     * @param height Chiều cao
     */
    protected void transitionTo(Node sourceNode, String fxmlPath, String title, int width, int height) {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource(fxmlPath));
            Parent root = loader.load();
            
            // Khởi tạo thông tin đăng nhập nếu chuyển về Menu
            Object controller = loader.getController();
            if (controller instanceof MainMenuViewFXMLController) {
                ((MainMenuViewFXMLController) controller).khoiTaoThongTinDangNhap(UserSession.getCurrentUser());
            }

            Scene scene = new Scene(root, width, height);
            URL cssUrl = App.class.getResource("/css/bakery.css");
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
            }
            
            Stage stage = (Stage) sourceNode.getScene().getWindow();
            stage.setTitle(title);
            stage.setScene(scene);
            stage.centerOnScreen();
        } catch (Exception e) {
            hienThiThongBaoLoi("Lỗi điều hướng", "Không thể chuyển sang màn hình " + title + ": " + e.getMessage());
            System.err.println("[BaseController] Navigation Error: " + e.getMessage());
        }
    }
}
