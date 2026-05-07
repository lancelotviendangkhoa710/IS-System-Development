package com.bakery.views.controllers.hethong;

import com.bakery.model.dto.nhansu.NhanVienDTO;
import com.bakery.services.baocao.ThongKeService;
import com.bakery.utils.UserSession;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import java.util.Map;

/**
 * Controller cho Dashboard (Trang chủ).
 * Mọi dữ liệu được lấy từ DB. Không có Mock Data.
 */
public class DashboardController {
    @FXML private Label lblBannerName;
    @FXML private Label lblThongBao;
    @FXML private FlowPane flowBestSellersMenu;
    @FXML private Button btnQuanLyCa;

    private final ThongKeService thongKeService = new ThongKeService();

    @FXML
    public void initialize() {
        NhanVienDTO currentUser = UserSession.getCurrentUser();
        if (currentUser != null) {
            lblBannerName.setText(currentUser.getHoTen() != null ? currentUser.getHoTen() : currentUser.getTenDangNhap());
        } else {
            lblBannerName.setText("Quản trị viên (Demo)");
        }
        loadBestSellers();
    }

    private void loadBestSellers() {
        if (flowBestSellersMenu == null) return;
        flowBestSellersMenu.getChildren().clear();

        try {
            Map<String, Integer> top5 = thongKeService.getTop5BanChay();

            if (top5 == null || top5.isEmpty()) {
                if (lblThongBao != null) {
                    lblThongBao.setText("Chưa có dữ liệu bán hàng để hiển thị.");
                }
                return;
            }

            for (Map.Entry<String, Integer> entry : top5.entrySet()) {
                VBox card = new VBox(10);
                card.getStyleClass().add("best-seller-card");
                card.setPrefWidth(200);
                card.setMinWidth(180);
                card.setAlignment(javafx.geometry.Pos.CENTER);
                card.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-padding: 20; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 5);");

                Label lblIcon = new Label(getIconForProduct(entry.getKey()));
                lblIcon.setStyle("-fx-font-size: 32px;");

                Label lblName = new Label(entry.getKey());
                lblName.setStyle("-fx-font-weight: bold; -fx-text-fill: #1F2937; -fx-font-size: 14px;");
                lblName.setWrapText(true);
                lblName.setAlignment(javafx.geometry.Pos.CENTER);

                Label lblQty = new Label(entry.getValue() + " đã bán");
                lblQty.setStyle("-fx-text-fill: #92400E; -fx-font-weight: bold; -fx-font-size: 12px;");

                card.getChildren().addAll(lblIcon, lblName, lblQty);
                flowBestSellersMenu.getChildren().add(card);
            }
        } catch (Exception e) {
            System.err.println("Lỗi tải dashboard: " + e.getMessage());
        }
    }

    private String getIconForProduct(String name) {
        if (name.contains("Bánh Kem")) return "🎂";
        if (name.contains("Croissant")) return "🥐";
        if (name.contains("Bánh Mì")) return "🍞";
        if (name.contains("Tiramisu")) return "🍰";
        if (name.contains("Su Kem")) return "🧁";
        return "🥐";
    }

    @FXML
    private void onMoBanHang() {
        AppShellController.getInstance().loadView("/fxml/DonHangView.fxml");
    }

    @FXML
    private void onMoKho() {
        AppShellController.getInstance().loadView("/fxml/KhoView.fxml");
    }

    @FXML
    private void onMoNhanSu() {
        AppShellController.getInstance().loadView("/fxml/QuanLyNhanVienView.fxml");
    }

    @FXML
    private void onMoBaoCao() {
        AppShellController.getInstance().loadView("/fxml/BaoCaoView.fxml");
    }

    @FXML
    private void onQuanLyCa() {
        AppShellController.getInstance().loadView("/fxml/DoiSoatDongCaView.fxml");
    }

    @FXML
    private void onMoLichSuHeThong() {
        AppShellController.getInstance().loadView("/fxml/LichSuHeThongView.fxml");
    }

    @FXML
    private void onModuleChuaSanSang() {
        if (lblThongBao != null) {
            lblThongBao.setText("Chức năng đang phát triển.");
        }
    }
}
