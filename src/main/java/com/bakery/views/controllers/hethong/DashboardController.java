package com.bakery.views.controllers.hethong;

import com.bakery.model.dto.nhansu.NhanVienDTO;
import com.bakery.services.baocao.ThongKeService;
import com.bakery.services.nhansu.PhanQuyenService;
import com.bakery.utils.UserSession;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

/**
 * Controller cho Dashboard (Trang chủ).
 * Mọi dữ liệu được lấy từ DB. Không có Mock Data.
 */
public class DashboardController {
    @FXML private Label lblBannerName;
    @FXML private Label lblThongBao;
    @FXML private FlowPane flowBestSellersMenu;
    @FXML private Button btnTaoDonHang;
    @FXML private Button btnQuanLyCa;
    @FXML private Button btnBanHangCard;
    @FXML private Button btnKhoCard;
    @FXML private Button btnNhanSuCard;
    @FXML private Button btnBaoCaoCard;
    @FXML private Button btnKdsCard;
    @FXML private Button btnAuditLogsCard;

    @FXML private VBox cardBanHang;
    @FXML private VBox cardKho;
    @FXML private VBox cardNhanSu;
    @FXML private VBox cardBaoCao;
    @FXML private VBox cardKds;
    @FXML private VBox cardAuditLogs;

    private final ThongKeService thongKeService = new ThongKeService();
    private final PhanQuyenService phanQuyenService = new PhanQuyenService();
    private Set<PhanQuyenService.TinhNangHeThong> tinhNangDuocCap =
            EnumSet.noneOf(PhanQuyenService.TinhNangHeThong.class);

    @FXML
    public void initialize() {
        NhanVienDTO currentUser = UserSession.getCurrentUser();
        if (currentUser != null) {
            lblBannerName.setText(currentUser.getHoTen() != null ? currentUser.getHoTen() : currentUser.getTenDangNhap());
        } else {
            lblBannerName.setText("Quản trị viên (Demo)");
        }
        tinhNangDuocCap = phanQuyenService.layTinhNangDuocCap(currentUser);
        apDungPhanQuyenManHinh();
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

                Label lblIcon = new Label(getIconForProduct(entry.getKey()));
                lblIcon.getStyleClass().add("best-seller-icon");

                Label lblName = new Label(entry.getKey());
                lblName.getStyleClass().add("best-seller-name");
                lblName.setWrapText(true);
                lblName.setAlignment(javafx.geometry.Pos.CENTER);

                Label lblQty = new Label(entry.getValue() + " đã bán");
                lblQty.getStyleClass().add("best-seller-qty");

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
        if (!yeuCauTruyCap(PhanQuyenService.TinhNangHeThong.BAN_HANG_POS, "đặt hàng POS")) return;
        AppShellController.getInstance().loadView("/fxml/banhang/DonHangView.fxml");
    }

    @FXML
    private void onMoKho() {
        if (!yeuCauTruyCap(PhanQuyenService.TinhNangHeThong.KHO_TONG_QUAN, "kho")) return;
        AppShellController.getInstance().loadView("/fxml/kho/KhoView.fxml");
    }

    @FXML
    private void onMoNhanSu() {
        if (!yeuCauTruyCap(PhanQuyenService.TinhNangHeThong.NHAN_SU, "nhân sự")) return;
        AppShellController.getInstance().loadView("/fxml/nhansu/QuanLyNhanVienView.fxml");
    }

    @FXML
    private void onMoBaoCao() {
        if (!yeuCauTruyCap(PhanQuyenService.TinhNangHeThong.BAO_CAO_KINH_DOANH, "báo cáo")) return;
        AppShellController.getInstance().loadView("/fxml/baocao/BaoCaoView.fxml");
    }

    @FXML
    private void onQuanLyCa() {
        if (!yeuCauTruyCap(PhanQuyenService.TinhNangHeThong.QUAN_LY_CA_LAM_VIEC, "quản lý ca")) return;
        try {
            boolean caoDangMo = com.bakery.utils.SessionContext.getInstance().isCaoDangMo();
            String fxmlPath = caoDangMo ? "/fxml/hethong/DoiSoatDongCaView.fxml" : "/fxml/hethong/MoCaView.fxml";
            String title    = caoDangMo ? "H3K Bakery - Dong ca" : "H3K Bakery - Mo ca";

            java.net.URL fxmlUrl = getClass().getResource(fxmlPath);
            if (fxmlUrl == null) { lblThongBao.setText("Khong tim thay FXML: " + fxmlPath); return; }

            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(fxmlUrl);
            javafx.scene.Scene scene = new javafx.scene.Scene(loader.load());
            java.net.URL cssUrl = getClass().getResource("/css/bakery.css");
            if (cssUrl != null) scene.getStylesheets().add(cssUrl.toExternalForm());

            javafx.stage.Stage dialog = new javafx.stage.Stage();
            dialog.setTitle(title);
            dialog.setScene(scene);
            dialog.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            if (lblThongBao.getScene() != null)
                dialog.initOwner(lblThongBao.getScene().getWindow());
            dialog.setResizable(false);
            dialog.showAndWait();
        } catch (Exception ex) {
            lblThongBao.setText("Loi mo quan ly ca: " + ex.getMessage());
        }
    }

    @FXML
    private void onMoLichSuHeThong() {
        if (!yeuCauTruyCap(PhanQuyenService.TinhNangHeThong.NHAT_KY_HE_THONG, "nhật ký hệ thống")) return;
        AppShellController.getInstance().loadView("/fxml/hethong/LichSuHeThongView.fxml");
    }

    @FXML
    private void onMoKds() {
        if (!yeuCauTruyCap(PhanQuyenService.TinhNangHeThong.KDS_MAN_HINH_BEP, "màn hình KDS")) return;
        AppShellController.getInstance().loadView("/fxml/hethong/ThoBepDashboardView.fxml");
    }

    @FXML
    private void onModuleChuaSanSang() {
        if (lblThongBao != null) {
            lblThongBao.setText("Chức năng đang phát triển.");
        }
    }

    private void apDungPhanQuyenManHinh() {
        capNhatTinhNang(null, coQuyen(PhanQuyenService.TinhNangHeThong.BAN_HANG_POS), btnTaoDonHang);
        capNhatTinhNang(cardBanHang, coQuyen(PhanQuyenService.TinhNangHeThong.BAN_HANG_POS), btnBanHangCard);
        capNhatTinhNang(cardKho, coQuyen(PhanQuyenService.TinhNangHeThong.KHO_TONG_QUAN), btnKhoCard);
        capNhatTinhNang(cardNhanSu, coQuyen(PhanQuyenService.TinhNangHeThong.NHAN_SU), btnNhanSuCard);
        capNhatTinhNang(cardBaoCao, coQuyen(PhanQuyenService.TinhNangHeThong.BAO_CAO_KINH_DOANH), btnBaoCaoCard);
        capNhatTinhNang(cardKds, coQuyen(PhanQuyenService.TinhNangHeThong.KDS_MAN_HINH_BEP), btnKdsCard);
        capNhatTinhNang(cardAuditLogs, coQuyen(PhanQuyenService.TinhNangHeThong.NHAT_KY_HE_THONG), btnAuditLogsCard);
        capNhatTinhNang(null, coQuyen(PhanQuyenService.TinhNangHeThong.QUAN_LY_CA_LAM_VIEC), btnQuanLyCa);
    }

    private void capNhatTinhNang(VBox card, boolean duocCapQuyen, Button... danhSachNut) {
        if (card != null) {
            card.setDisable(!duocCapQuyen);
            card.setOpacity(duocCapQuyen ? 1.0 : 0.45);
        }
        if (danhSachNut == null) {
            return;
        }
        for (Button nut : danhSachNut) {
            if (nut != null) {
                nut.setDisable(!duocCapQuyen);
                nut.setOpacity(duocCapQuyen ? 1.0 : 0.45);
            }
        }
    }

    private boolean coQuyen(PhanQuyenService.TinhNangHeThong tinhNang) {
        return tinhNangDuocCap != null && tinhNangDuocCap.contains(tinhNang);
    }

    private boolean yeuCauTruyCap(PhanQuyenService.TinhNangHeThong tinhNang, String tenTinhNang) {
        if (coQuyen(tinhNang)) {
            return true;
        }
        if (lblThongBao != null) {
            lblThongBao.setText("Bạn không có quyền truy cập " + tenTinhNang + ".");
        }
        return false;
    }
}
