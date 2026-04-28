package com.bakery.views.controllers;

import com.bakery.model.dto.NhanVienDTO;
import com.bakery.services.ThongKeService;
import com.bakery.services.AuthorizationService;
import com.bakery.services.AuthorizationService.ModuleKey;
import com.bakery.utils.UserSession;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Map;
import java.util.Set;

public class MainMenuViewFXMLController {
    @FXML private Label lblTenNguoiDung;
    @FXML private Label lblVaiTro;
    @FXML private Label lblThongBao;
    @FXML private Label lblBannerName;

    @FXML private Button btnPos;
    @FXML private Button btnInventory;
    @FXML private Button btnStaff;
    @FXML private Button btnReports;
    @FXML private Button btnReportsCard;
    @FXML private Button btnTheoDoiDon;
    @FXML private Button btnKds;
    @FXML private Button btnAuditLogs;
    @FXML private Button btnSupplier;
    @FXML private Button btnDanhMuc;
    @FXML private Button btnSanPham;
    @FXML private Button btnNguyenLieu;
    @FXML private HBox hboxBestSellersMenu;

    private final AuthorizationService authorizationService = new AuthorizationService();
    private final ThongKeService thongKeService = new ThongKeService();
    private NhanVienDTO currentUser;

    public void khoiTaoThongTinDangNhap(NhanVienDTO nhanVien) {
        this.currentUser = nhanVien != null ? nhanVien : UserSession.getCurrentUser();
        if (this.currentUser == null) {
            lblThongBao.setText("Khong tim thay thong tin dang nhap.");
            return;
        }

        UserSession.setCurrentUser(this.currentUser);
        boolean laAdmin = authorizationService.laAdmin(this.currentUser);
        Set<ModuleKey> modulesDuocCap = authorizationService.layModulesDuocCap(this.currentUser);

        lblTenNguoiDung.setText(this.currentUser.getHoTen() == null ? this.currentUser.getTenDangNhap() : this.currentUser.getHoTen());
        if (lblBannerName != null) {
            lblBannerName.setText(lblTenNguoiDung.getText());
        }
        lblVaiTro.setText(xayDungNhanQuyen(this.currentUser, laAdmin));
        capNhatTrangThaiNut(btnPos, modulesDuocCap.contains(ModuleKey.POS));
        capNhatTrangThaiNut(btnInventory, modulesDuocCap.contains(ModuleKey.INVENTORY));
        capNhatTrangThaiNut(btnStaff, modulesDuocCap.contains(ModuleKey.STAFF));
        capNhatTrangThaiNut(btnReports, modulesDuocCap.contains(ModuleKey.REPORTS));
        if (btnReportsCard != null) capNhatTrangThaiNut(btnReportsCard, modulesDuocCap.contains(ModuleKey.REPORTS));
        if (btnTheoDoiDon != null) capNhatTrangThaiNut(btnTheoDoiDon, modulesDuocCap.contains(ModuleKey.POS));
        capNhatTrangThaiNut(btnKds, modulesDuocCap.contains(ModuleKey.KDS));
        capNhatTrangThaiNut(btnAuditLogs, modulesDuocCap.contains(ModuleKey.AUDIT_LOGS));
        if (btnSupplier != null) capNhatTrangThaiNut(btnSupplier, modulesDuocCap.contains(ModuleKey.INVENTORY));
        if (btnDanhMuc != null) capNhatTrangThaiNut(btnDanhMuc, modulesDuocCap.contains(ModuleKey.INVENTORY));
        if (btnSanPham != null) capNhatTrangThaiNut(btnSanPham, modulesDuocCap.contains(ModuleKey.INVENTORY));
        if (btnNguyenLieu != null) capNhatTrangThaiNut(btnNguyenLieu, modulesDuocCap.contains(ModuleKey.INVENTORY));

        loadBestSellers();
    }

    private void loadBestSellers() {
        if (hboxBestSellersMenu == null) return;
        hboxBestSellersMenu.getChildren().clear();
        
        Map<String, Integer> top5 = thongKeService.getTop5BanChay();
        for (Map.Entry<String, Integer> entry : top5.entrySet()) {
            VBox card = new VBox(8);
            card.getStyleClass().add("best-seller-card");
            card.setPrefWidth(200);

            Label lblIcon = new Label("🧁");
            lblIcon.getStyleClass().add("best-seller-icon");

            Label lblName = new Label(entry.getKey());
            lblName.getStyleClass().add("best-seller-name");

            Label lblQty = new Label(entry.getValue() + " đã bán");
            lblQty.getStyleClass().add("best-seller-qty");

            card.getChildren().addAll(lblIcon, lblName, lblQty);
            hboxBestSellersMenu.getChildren().add(card);
        }
    }

    @FXML
    private void onMoPOS() {
        moScene(btnPos, "/fxml/OrderView.fxml", "H3K Bakery - POS", 1280, 720, "Khong the mo POS: ");
    }

    @FXML
    private void onMoInventory() {
        moScene(btnInventory, "/fxml/InventoryView.fxml", "H3K Bakery - Inventory", 1366, 768, "Khong the mo Inventory: ");
    }

    @FXML
    private void onMoReports() {
        moScene(btnReports, "/fxml/ReportsView.fxml", "H3K Bakery - Thống kê Kinh doanh", 1366, 768, "Khong the mo Thống kê: ");
    }

    @FXML
    private void onMoSupplier() {
        moScene(btnSupplier, "/fxml/SupplierManagementView.fxml", "H3K Bakery - Quản lý Nhà Cung Cấp", 1280, 720, "Không thể mở Nhà Cung Cấp: ");
    }

    @FXML
    private void onMoDanhMuc() {
        moScene(btnDanhMuc, "/fxml/DanhMucSPView.fxml", "H3K Bakery - Quản lý Danh mục", 1280, 720, "Không thể mở Danh mục: ");
    }

    @FXML
    private void onMoSanPham() {
        moScene(btnSanPham, "/fxml/SanPhamView.fxml", "H3K Bakery - Quản lý Sản phẩm", 1280, 720, "Không thể mở Sản phẩm: ");
    }

    @FXML
    private void onMoNguyenLieu() {
        moScene(btnNguyenLieu, "/fxml/NguyenLieuView.fxml", "H3K Bakery - Quản lý Nguyên liệu", 1280, 720, "Không thể mở Nguyên liệu: ");
    }

    @FXML
    private void onMoTheoDoiDon() {
        try {
            URL fxmlUrl = getClass().getResource("/fxml/OrderTrackingView.fxml");
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();
            
            Scene scene = new Scene(root, 1366, 768);
            URL cssUrl = getClass().getResource("/css/bakery.css");
            if (cssUrl != null) scene.getStylesheets().add(cssUrl.toExternalForm());
            
            Button source = btnTheoDoiDon != null ? btnTheoDoiDon : btnPos;
            Stage stage = (Stage) source.getScene().getWindow();
            stage.setTitle("H3K Bakery - Theo Dõi Đơn");
            stage.setScene(scene);
            stage.centerOnScreen();
        } catch (Exception ex) {
            lblThongBao.setText("Không thể mở Theo dõi đơn: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    @FXML
    private void onThemNhanVien() {
        try {
            URL fxmlUrl = getClass().getResource("/fxml/ThemNhanVienDialog.fxml");
            if (fxmlUrl == null) throw new RuntimeException("Không tìm thấy ThemNhanVienDialog.fxml");
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Scene scene = new Scene(loader.load());
            URL cssUrl = getClass().getResource("/css/bakery.css");
            if (cssUrl != null) scene.getStylesheets().add(cssUrl.toExternalForm());
            Stage dialog = new Stage();
            dialog.setTitle("H3K Bakery - Thêm Nhân Viên");
            dialog.setScene(scene);
            dialog.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            dialog.initOwner(lblTenNguoiDung.getScene().getWindow());
            dialog.setResizable(false);
            dialog.showAndWait();
        } catch (Exception ex) {
            lblThongBao.setText("Lỗi mở dialog thêm nhân viên: " + ex.getMessage());
        }
    }
    @FXML
    private void onModuleChuaSanSang() {
        lblThongBao.setText("Chuc nang dang phat trien.");
    }

    private void moScene(Button source, String fxmlPath, String title, int width, int height, String errorPrefix) {
        try {
            URL fxmlUrl = getClass().getResource(fxmlPath);
            if (fxmlUrl == null) {
                throw new RuntimeException("Khong tim thay " + fxmlPath);
            }
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Scene scene = new Scene(loader.load(), width, height);
            if ("/fxml/OrderView.fxml".equals(fxmlPath)) {
                OrderViewFXMLController controller = loader.getController();
                controller.apDungThongTinDangNhap(UserSession.getCurrentUser());
            }
            URL cssUrl = getClass().getResource("/css/bakery.css");
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
            }
            Stage stage = (Stage) source.getScene().getWindow();
            stage.setTitle(title);
            stage.setScene(scene);
            stage.centerOnScreen();
        } catch (Exception ex) {
            lblThongBao.setText(errorPrefix + ex.getMessage());
        }
    }

    private void capNhatTrangThaiNut(Button button, boolean duocCapQuyen) {
        button.setDisable(!duocCapQuyen);
        button.setOpacity(duocCapQuyen ? 1.0 : 0.45);
        if (button.getParent() != null) {
            button.getParent().setDisable(!duocCapQuyen);
        }
    }

    private String xayDungNhanQuyen(NhanVienDTO nhanVien, boolean laAdmin) {
        String tenVaiTro = nhanVien.getTenVaiTro();
        if (tenVaiTro != null && !tenVaiTro.isBlank()) {
            return laAdmin ? tenVaiTro + " - Full Access" : tenVaiTro + " - Role Access";
        }
        return laAdmin ? "Admin Access" : "Role Access";
    }
}
