package com.bakery.views.controllers.hethong;

import com.bakery.model.dto.nhansu.NhanVienDTO;
import com.bakery.model.enums.SystemModule;
import com.bakery.services.baocao.ThongKeService;
import com.bakery.services.nhansu.PhanQuyenService;
import com.bakery.utils.UserSession;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.FlowPane;
import javafx.scene.control.ScrollPane;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller cho giao diện chính (Main Menu) của ứng dụng.
 * Quản lý việc hiển thị thông tin người dùng, kiểm tra phân quyền và điều hướng
 * đến các phân hệ khác.
 */
public class MainMenuViewFXMLController {
    private static final Logger LOGGER = Logger.getLogger(MainMenuViewFXMLController.class.getName());
    @FXML
    private Label lblTenNguoiDung;
    @FXML
    private Label lblVaiTro;
    @FXML
    private Label lblThongBao;
    @FXML
    private Label lblBannerName;

    @FXML
    private VBox vboxSidebar;
    @FXML
    private HBox hboxLogo;
    @FXML
    private Label lblLogoText;
    @FXML
    private ScrollPane scrollNav;
    @FXML
    private Button btnTongQuan;
    @FXML
    private Button btnInventory;
    @FXML
    private Button btnBanHang;
    @FXML
    private Button btnKho;
    @FXML
    private Button btnNhanSu;
    @FXML
    private Button btnNhanSuSidebar;
    @FXML
    private Button btnBaoCao;
    @FXML
    private Button btnBaoCaoCard;
    @FXML
    private Button btnTheoDoiDon;
    @FXML
    private Button btnKds;
    @FXML
    private Button btnAuditLogs;
    @FXML
    private Button btnNhaCungCap;
    @FXML
    private Button btnSoQuy;
    @FXML
    private Button btnDanhMuc;
    @FXML
    private Button btnSanPham;
    @FXML
    private Button btnNguyenLieu;
    @FXML
    private FlowPane flowBestSellersMenu;

    private boolean isSidebarCollapsed = false;
    private final java.util.Map<Button, String> buttonTextMap = new java.util.HashMap<>();

    private final PhanQuyenService authorizationService = new PhanQuyenService();
    private final ThongKeService thongKeService = new ThongKeService();
    private NhanVienDTO currentUser;

    public void khoiTaoThongTinDangNhap(NhanVienDTO nhanVien) {
        this.currentUser = nhanVien != null ? nhanVien : UserSession.getCurrentUser();
        if (this.currentUser == null) {
            lblThongBao.setText("Không tìm thấy thông tin đăng nhập.");
            return;
        }

        UserSession.setCurrentUser(this.currentUser);
        boolean laAdmin = authorizationService.laAdmin(this.currentUser);
        Set<SystemModule> modulesDuocCap = authorizationService.layModulesDuocCap(this.currentUser);

        lblTenNguoiDung.setText(
                this.currentUser.getHoTen() == null ? this.currentUser.getTenDangNhap() : this.currentUser.getHoTen());
        if (lblBannerName != null) {
            lblBannerName.setText(lblTenNguoiDung.getText());
        }
        lblVaiTro.setText(xayDungNhanQuyen(this.currentUser, laAdmin));
        capNhatTrangThaiNut(btnBanHang, modulesDuocCap.contains(SystemModule.BAN_HANG));
        capNhatTrangThaiNut(btnKho, modulesDuocCap.contains(SystemModule.KHO));
        capNhatTrangThaiNut(btnNhanSu, modulesDuocCap.contains(SystemModule.NHAN_SU));
        capNhatTrangThaiNut(btnNhanSuSidebar, modulesDuocCap.contains(SystemModule.NHAN_SU));
        capNhatTrangThaiNut(btnBaoCao, modulesDuocCap.contains(SystemModule.BAO_CAO));
        if (btnBaoCaoCard != null)
            capNhatTrangThaiNut(btnBaoCaoCard, modulesDuocCap.contains(SystemModule.BAO_CAO));
        if (btnTheoDoiDon != null)
            capNhatTrangThaiNut(btnTheoDoiDon, modulesDuocCap.contains(SystemModule.BAN_HANG));
        capNhatTrangThaiNut(btnKds, modulesDuocCap.contains(SystemModule.NHA_BEP));
        capNhatTrangThaiNut(btnAuditLogs, modulesDuocCap.contains(SystemModule.NHAT_KY));
        if (btnNhaCungCap != null)
            capNhatTrangThaiNut(btnNhaCungCap, modulesDuocCap.contains(SystemModule.KHO));
        if (btnDanhMuc != null)
            capNhatTrangThaiNut(btnDanhMuc, modulesDuocCap.contains(SystemModule.KHO));
        if (btnSanPham != null)
            capNhatTrangThaiNut(btnSanPham, modulesDuocCap.contains(SystemModule.KHO));
        if (btnNguyenLieu != null)
            capNhatTrangThaiNut(btnNguyenLieu, modulesDuocCap.contains(SystemModule.KHO));

        loadBestSellers();
    }

    private void loadBestSellers() {
        if (flowBestSellersMenu == null)
            return;
        flowBestSellersMenu.getChildren().clear();

        try {
            Map<String, Integer> top5 = thongKeService.getTop5BanChay();
            for (Map.Entry<String, Integer> entry : top5.entrySet()) {
                VBox card = new VBox(8);
                card.getStyleClass().add("best-seller-card");
                card.setPrefWidth(220); // Fixed width for better grid look in FlowPane
                card.setMinWidth(200);

                Label lblIcon = new Label("🍰"); // Biểu tượng bánh
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
        moScene(btnBanHang, "/fxml/DonHangView.fxml", "H3K Bakery - Bán hàng", 1280, 720, "Không thể mở POS: ");
    }

    @FXML
    private void onMoKho() {
        moScene(btnKho, "/fxml/KhoView.fxml", "H3K Bakery - Kho hàng", 1280, 720,
                "Không thể mở Kho hàng: ");
    }

    @FXML
    private void onMoNhanSu() {
        moScene(btnNhanSu != null ? btnNhanSu : btnNhanSuSidebar, "/fxml/QuanLyNhanVienView.fxml",
                "H3K Bakery - Quản lý nhân sự", 1280, 720,
                "Không thể mở Quản lý nhân sự: ");
    }

    @FXML
    private void onMoBaoCao() {
        moScene(btnBaoCao, "/fxml/BaoCaoView.fxml", "H3K Bakery - Thống kê Kinh doanh", 1280, 720,
                "Không thể mở Thống kê: ");
    }

    @FXML
    private void onMoNhaCungCap() {
        moScene(btnNhaCungCap, "/fxml/QuanLyNhaCungCapView.fxml", "H3K Bakery - Quản lý Nhà Cung Cấp", 1280, 720,
                "Không thể mở Nhà Cung Cấp: ");
    }

    @FXML
    private void onMoDanhMuc() {
        moScene(btnDanhMuc, "/fxml/DanhMucSPView.fxml", "H3K Bakery - Quản lý Danh mục", 1280, 720,
                "Không thể mở Danh mục: ");
    }

    @FXML
    private void onMoSanPham() {
        moScene(btnSanPham, "/fxml/SanPhamView.fxml", "H3K Bakery - Quản lý Sản phẩm", 1280, 720,
                "Không thể mở Sản phẩm: ");
    }

    @FXML
    private void onMoNguyenLieu() {
        moScene(btnNguyenLieu, "/fxml/NguyenLieuView.fxml", "H3K Bakery - Quản lý Nguyên liệu", 1280, 720,
                "Không thể mở Nguyên liệu: ");
    }

    @FXML
    private void onMoSoQuy() {
        moScene(btnSoQuy, "/fxml/SoQuyView.fxml", "H3K Bakery - Sổ quỹ thu chi", 1280, 720,
                "Không thể mở Sổ quỹ: ");
    }

    @FXML
    private void onQuanLyCa() {
        try {
            boolean caoDangMo = com.bakery.utils.SessionContext.getInstance().isCaoDangMo();
            String fxmlPath = caoDangMo ? "/fxml/DoiSoatDongCaView.fxml" : "/fxml/MoCaView.fxml";
            String title = caoDangMo ? "H3K Bakery - Đóng ca" : "H3K Bakery - Mở ca";

            URL fxmlUrl = getClass().getResource(fxmlPath);
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Scene scene = new Scene(loader.load());

            URL cssUrl = getClass().getResource("/css/amber.css");
            if (cssUrl != null)
                scene.getStylesheets().add(cssUrl.toExternalForm());

            Stage dialog = new Stage();
            dialog.setTitle(title);
            dialog.setScene(scene);
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initOwner(lblTenNguoiDung.getScene().getWindow());
            dialog.setResizable(false);
            dialog.showAndWait();
        } catch (Exception ex) {
            lblThongBao.setText("Lỗi mở quản lý ca: " + ex.getMessage());
            System.err.println("[MainMenu] Quản lý ca error: " + ex.getMessage());
        }
    }

    @FXML
    private void onMoTheoDoiDon() {
        try {
            URL fxmlUrl = getClass().getResource("/fxml/TheoDoiDonHangView.fxml");
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();

            Scene scene = new Scene(root, 1366, 768);
            URL cssUrl = getClass().getResource("/css/bakery.css");
            if (cssUrl != null)
                scene.getStylesheets().add(cssUrl.toExternalForm());

            Button source = btnTheoDoiDon != null ? btnTheoDoiDon : btnBanHang;
            Stage stage = (Stage) source.getScene().getWindow();
            stage.setTitle("H3K Bakery - Theo Dõi Đơn");
            stage.setScene(scene);
            stage.centerOnScreen();
        } catch (Exception ex) {
            lblThongBao.setText("Không thể mở Theo dõi đơn: " + ex.getMessage());
            System.err.println("[MainMenu] Theo dõi đơn error: " + ex.getMessage());
        }
    }

    @FXML
    private void onThemNhanVien() {
        try {
            URL fxmlUrl = getClass().getResource("/fxml/ThemNhanVienDialog.fxml");
            if (fxmlUrl == null)
                throw new RuntimeException("Không tìm thấy ThemNhanVienDialog.fxml");
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Scene scene = new Scene(loader.load());
            URL cssUrl = getClass().getResource("/css/bakery.css");
            if (cssUrl != null)
                scene.getStylesheets().add(cssUrl.toExternalForm());
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
    private void onToggleSidebar() {
        if (buttonTextMap.isEmpty()) {
            // Store original text
            Button[] navButtons = {
                    btnTongQuan, btnBanHang, btnInventory, btnNhanSuSidebar, btnBaoCao,
                    btnTheoDoiDon, btnKds, btnAuditLogs, btnNhaCungCap, btnSoQuy,
                    btnDanhMuc, btnSanPham, btnNguyenLieu
            };
            for (Button btn : navButtons) {
                if (btn != null)
                    buttonTextMap.put(btn, btn.getText());
            }
        }

        isSidebarCollapsed = !isSidebarCollapsed;
        double newWidth = isSidebarCollapsed ? 60 : 240;

        vboxSidebar.setPrefWidth(newWidth);
        lblLogoText.setVisible(!isSidebarCollapsed);
        lblLogoText.setManaged(!isSidebarCollapsed);
        scrollNav.setVisible(!isSidebarCollapsed);
        scrollNav.setManaged(!isSidebarCollapsed);

        if (isSidebarCollapsed) {
            hboxLogo.setPadding(new Insets(20, 0, 20, 0));
            hboxLogo.setAlignment(Pos.CENTER);
        } else {
            hboxLogo.setPadding(new Insets(32));
            hboxLogo.setAlignment(Pos.CENTER_LEFT);
            for (java.util.Map.Entry<Button, String> entry : buttonTextMap.entrySet()) {
                Button btn = entry.getKey();
                btn.setText(entry.getValue());
                btn.setTooltip(null);
                btn.setAlignment(Pos.BASELINE_LEFT);
            }
        }
    }

    @FXML
    private void onToggleFullScreen() {
        try {
            Stage stage = (Stage) vboxSidebar.getScene().getWindow();
            if (stage != null) {
                stage.setFullScreen(!stage.isFullScreen());
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Khong the chuyen trang thai full screen", e);
            lblThongBao.setText("Khong the chuyen full screen.");
        }
    }

    @FXML
    private void onModuleChuaSanSang() {
        lblThongBao.setText("Chức năng đang phát triển.");
    }

    @FXML
    private void onDangXuat() {
        try {
            // Clear session
            UserSession.setCurrentUser(null);
            com.bakery.utils.SessionContext.clear();

            // Load login view
            URL fxmlUrl = getClass().getResource("/fxml/DangNhapView.fxml");
            if (fxmlUrl == null) {
                throw new RuntimeException("Không tìm thấy /fxml/DangNhapView.fxml");
            }

            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();

            // Optional: Pass a logout message to MainViewFXMLController
            Object controller = loader.getController();
            if (controller instanceof MainViewFXMLController) {
                ((MainViewFXMLController) controller).setLoginInfo("Bạn đã đăng xuất thành công.");
            } else if (controller instanceof DangNhapViewFXMLController) {
                ((DangNhapViewFXMLController) controller).setLoginInfo("Bạn đã đăng xuất thành công.");
            }

            Scene scene = new Scene(root);
            URL cssUrl = getClass().getResource("/css/bakery.css");
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
            }

            Stage stage = (Stage) lblTenNguoiDung.getScene().getWindow();
            stage.setTitle("H3K Bakery - Đăng nhập");
            stage.setScene(scene);
            stage.setResizable(false);
            stage.centerOnScreen();
        } catch (Exception ex) {
            lblThongBao.setText("Lỗi đăng xuất: " + ex.getMessage());
            System.err.println("[MainMenu] Đăng xuất error: " + ex.getMessage());
        }
    }

    private void moScene(Button source, String fxmlPath, String title, int width, int height, String errorPrefix) {
        try {
            URL fxmlUrl = getClass().getResource(fxmlPath);
            if (fxmlUrl == null) {
                throw new RuntimeException("Không tìm thấy " + fxmlPath);
            }
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Scene scene = new Scene(loader.load(), width, height);
            if ("/fxml/DonHangView.fxml".equals(fxmlPath)) {
                DonHangViewFXMLController controller = loader.getController();
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
