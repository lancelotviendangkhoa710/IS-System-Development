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

public class DashboardController {
    @FXML
    private Label lblBannerName;
    @FXML
    private Label lblThongBao;
    @FXML
    private FlowPane flowBestSellersMenu;
    @FXML
    private Button btnQuanLyCa;

    private final ThongKeService thongKeService = new ThongKeService();

    @FXML
    public void initialize() {
        NhanVienDTO currentUser = UserSession.getCurrentUser();
        if (currentUser != null) {
            lblBannerName.setText(currentUser.getHoTen() != null ? currentUser.getHoTen() : currentUser.getTenDangNhap());
        }
        loadBestSellers();
    }

    private void loadBestSellers() {
        if (flowBestSellersMenu == null) return;
        flowBestSellersMenu.getChildren().clear();

        try {
            Map<String, Integer> top5 = thongKeService.getTop5BanChay();
            for (Map.Entry<String, Integer> entry : top5.entrySet()) {
                VBox card = new VBox(8);
                card.getStyleClass().add("best-seller-card");
                card.setPrefWidth(220);
                card.setMinWidth(200);

                Label lblIcon = new Label("🍰");
                lblIcon.getStyleClass().add("best-seller-icon");

                Label lblName = new Label(entry.getKey());
                lblName.getStyleClass().add("best-seller-name");

                Label lblQty = new Label(entry.getValue() + " đã bán");
                lblQty.getStyleClass().add("best-seller-qty");

                card.getChildren().addAll(lblIcon, lblName, lblQty);
                flowBestSellersMenu.getChildren().add(card);
            }
        } catch (Exception e) {
            System.err.println("Lỗi tải best sellers: " + e.getMessage());
        }
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
        // Gọi logic quản lý ca từ MainMenuView hoặc tạo một Event Bus
        // Ở đây tạm thời để trống hoặc gọi qua AppShell
    }

    @FXML
    private void onModuleChuaSanSang() {
        if (lblThongBao != null) {
            lblThongBao.setText("Chức năng đang phát triển.");
        }
    }
}
