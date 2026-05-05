package com.bakery.views.controllers;

import com.bakery.main.App;
import com.bakery.services.nhansu.PhanQuyenService;
import com.bakery.utils.UserSession;
import com.bakery.views.controllers.banhang.ThuNganViewFXMLController;
import com.bakery.views.controllers.hethong.MainMenuViewFXMLController;
import com.bakery.views.controllers.hethong.MainViewFXMLController;
import com.bakery.views.controllers.hethong.ThoBepDashboardViewFXMLController;
import com.bakery.views.controllers.hethong.ThuKhoDashboardViewFXMLController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.net.URL;

public abstract class BaseController {
    private final PhanQuyenService phanQuyenService = new PhanQuyenService();

    @FXML
    protected Label lblThongBao;

    protected void hienThiThongBaoLoi(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    protected void hienThiCanhBao(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    protected void hienThiThongTin(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    protected void hienThiLoiLabel(String msg) {
        if (lblThongBao != null) {
            lblThongBao.setText(msg);
            lblThongBao.getStyleClass().removeAll("lbl-success");
            if (!lblThongBao.getStyleClass().contains("lbl-danger")) {
                lblThongBao.getStyleClass().add("lbl-danger");
            }
        }
    }

    protected void hienThiThanhCongLabel(String msg) {
        if (lblThongBao != null) {
            lblThongBao.setText(msg);
            lblThongBao.getStyleClass().removeAll("lbl-danger");
            if (!lblThongBao.getStyleClass().contains("lbl-success")) {
                lblThongBao.getStyleClass().add("lbl-success");
            }
        }
    }

    protected void quayLaiMenuChinh(Node sourceNode) {
        transitionTo(
                sourceNode,
                phanQuyenService.layManHinhTrangChu(UserSession.getCurrentUser()),
                phanQuyenService.layTieuDeTrangChu(UserSession.getCurrentUser()),
                1366,
                768);
    }

    protected void transitionTo(Node sourceNode, String fxmlPath, String title, int width, int height) {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource(fxmlPath));
            Parent root = loader.load();

            Object controller = loader.getController();
            if (controller instanceof MainMenuViewFXMLController) {
                ((MainMenuViewFXMLController) controller).khoiTaoThongTinDangNhap(UserSession.getCurrentUser());
            } else if (controller instanceof MainViewFXMLController) {
                // MainView (Shell) tu dong lay User tu Session trong initialize()
            } else if (controller instanceof ThuNganViewFXMLController) {
                ((ThuNganViewFXMLController) controller).khoiTaoDashboard(UserSession.getCurrentUser());
            } else if (controller instanceof ThoBepDashboardViewFXMLController) {
                ((ThoBepDashboardViewFXMLController) controller).khoiTaoDashboard(UserSession.getCurrentUser());
            } else if (controller instanceof ThuKhoDashboardViewFXMLController) {
                ((ThuKhoDashboardViewFXMLController) controller).khoiTaoDashboard(UserSession.getCurrentUser());
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
            hienThiThongBaoLoi("Loi dieu huong", "Khong the chuyen sang man hinh " + title + ": " + e.getMessage());
        }
    }
}
