package com.bakery.views.controllers;

import com.bakery.main.App;
import com.bakery.services.nhansu.PhanQuyenService;
import com.bakery.utils.UserSession;
import com.bakery.views.controllers.banhang.ThuNganViewFXMLController;
import com.bakery.views.controllers.hethong.AppShellController;
import com.bakery.views.controllers.hethong.MainMenuViewFXMLController;
import com.bakery.views.controllers.hethong.MainViewFXMLController;
import com.bakery.views.controllers.hethong.ThoBepDashboardViewFXMLController;
import com.bakery.views.controllers.hethong.ThuKhoDashboardViewFXMLController;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.net.URL;
import java.util.logging.Logger;

public abstract class BaseController {
    private static final Logger LOGGER = Logger.getLogger(BaseController.class.getName());
    private final PhanQuyenService phanQuyenService = new PhanQuyenService();

    /** Timeline dùng cho auto-refresh — tự query DB mỗi N giây */
    private Timeline autoRefreshTimeline;

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
                App.APP_SHELL_VIEW,
                phanQuyenService.layTieuDeTrangChu(UserSession.getCurrentUser()),
                1280,
                720);
    }

    protected void transitionTo(Node sourceNode, String fxmlPath, String title, int width, int height) {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource(fxmlPath));
            Parent root = loader.load();

            Object controller = loader.getController();
            if (controller instanceof AppShellController) {
                ((AppShellController) controller).setNhanVienInfo(UserSession.getCurrentUser());
            } else if (controller instanceof MainMenuViewFXMLController) {
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
            stage.setResizable(true);
            stage.centerOnScreen();
        } catch (Exception e) {
            LOGGER.warning("[BaseController] Loi dieu huong toi '" + title + "': " + e.getMessage());
            hienThiThongBaoLoi("Loi dieu huong", "Khong the chuyen sang man hinh " + title + ": " + e.getMessage());
        }
    }

    // ── AUTO-REFRESH (Polling DB) ────────────────────────────────────────────

    /**
     * Bật auto-refresh: mỗi N giây tự gọi lại hàm tải dữ liệu từ DB.
     * Khi user A INSERT/UPDATE ở session khác, user B sẽ tự thấy dữ liệu mới.
     *
     * Tự động hủy khi màn hình bị thay thế (AppShell.loadView swap nội dung
     * contentArea → Node cũ mất Scene → listener tự stop Timeline).
     *
     * @param anchor       Node bất kỳ trên màn hình hiện tại (vd: TableView)
     * @param taiLaiDuLieu Runnable chứa logic SELECT + cập nhật TableView
     * @param chuKyGiay    Chu kỳ refresh (giây), khuyến nghị 10-15s
     */
    protected void batDauAutoRefresh(Node anchor, Runnable taiLaiDuLieu, int chuKyGiay) {
        huyAutoRefresh(); // Dọn timer cũ nếu có
        autoRefreshTimeline = new Timeline(new KeyFrame(
                Duration.seconds(chuKyGiay),
                e -> {
                    try {
                        taiLaiDuLieu.run();
                    } catch (Exception ex) {
                        LOGGER.warning("[AutoRefresh] Loi khi tai lai du lieu: " + ex.getMessage());
                    }
                }
        ));
        autoRefreshTimeline.setCycleCount(Animation.INDEFINITE);
        autoRefreshTimeline.play();

        // Tự hủy khi Node bị remove khỏi Scene (AppShell thay nội dung)
        anchor.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene == null) {
                huyAutoRefresh();
                LOGGER.info("[AutoRefresh] Scene removed — auto-stop " + this.getClass().getSimpleName());
            }
        });

        LOGGER.info("[AutoRefresh] Bat dau polling moi " + chuKyGiay + "s — " + this.getClass().getSimpleName());
    }

    /**
     * Tắt auto-refresh thủ công (backup — thường không cần gọi vì có auto-detect).
     */
    protected void huyAutoRefresh() {
        if (autoRefreshTimeline != null) {
            autoRefreshTimeline.stop();
            autoRefreshTimeline = null;
        }
    }
}
