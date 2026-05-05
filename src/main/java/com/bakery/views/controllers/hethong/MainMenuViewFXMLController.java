package com.bakery.views.controllers.hethong;

import com.bakery.model.dto.nhansu.NhanVienDTO;
import com.bakery.model.enums.SystemModule;
import com.bakery.services.baocao.ThongKeService;
import com.bakery.services.nhansu.PhanQuyenService;
import com.bakery.utils.UserSession;
import com.bakery.views.controllers.banhang.DonHangViewFXMLController;
import com.bakery.views.controllers.nhansu.DangNhapViewFXMLController;
import com.bakery.utils.FXMLLoaderUtil;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
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
    private Button btnNhanSuSidebar;
    @FXML
    private Button btnKhachHang;
    @FXML
    private Button btnBaoCao;
    @FXML
    private Button btnTheoDoiDon;
    @FXML
    private Button btnKds;
    @FXML
    private Button btnAuditLogs;
    @FXML
    private Button btnNhaCungCap;
    @FXML
    private Button btnDanhMuc;
    @FXML
    private Button btnSanPham;
    @FXML
    private Button btnNguyenLieu;
    @FXML
    private Button btnNhapKho;
    @FXML
    private Button btnXuatKho;
    @FXML
    private Button btnKiemKe;
    @FXML
    private Button btnMaTranPhanQuyen;
    @FXML
    private Button btnCongThuc;
    @FXML
    private FlowPane flowBestSellersMenu;
    @FXML
    private StackPane contentArea;

    public StackPane getContentArea() {
        return contentArea;
    }

    private boolean isSidebarCollapsed = false;
    private final java.util.Map<Button, String> buttonTextMap = new java.util.HashMap<>();

    private final PhanQuyenService authorizationService = new PhanQuyenService();
    private final ThongKeService thongKeService = new ThongKeService();
    private NhanVienDTO currentUser;
    private AppShellController appShellController;

    public void setAppShellController(AppShellController appShellController) {
        this.appShellController = appShellController;
    }

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
        capNhatTrangThaiNut(btnInventory, modulesDuocCap.contains(SystemModule.KHO));
        capNhatTrangThaiNut(btnNhanSuSidebar, modulesDuocCap.contains(SystemModule.NHAN_SU));
        capNhatTrangThaiNut(btnKhachHang, modulesDuocCap.contains(SystemModule.KHACH_HANG));
        capNhatTrangThaiNut(btnBaoCao, modulesDuocCap.contains(SystemModule.BAO_CAO));
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
        if (btnNhapKho != null)
            capNhatTrangThaiNut(btnNhapKho, modulesDuocCap.contains(SystemModule.KHO));
        if (btnXuatKho != null)
            capNhatTrangThaiNut(btnXuatKho, modulesDuocCap.contains(SystemModule.KHO));
        if (btnKiemKe != null)
            capNhatTrangThaiNut(btnKiemKe, modulesDuocCap.contains(SystemModule.KHO));
        if (btnMaTranPhanQuyen != null)
            capNhatTrangThaiNut(btnMaTranPhanQuyen, laAdmin);
        if (btnCongThuc != null)
            capNhatTrangThaiNut(btnCongThuc, modulesDuocCap.contains(SystemModule.KHO));

        // Load dashboard mặc định nếu chưa có gì
        if (contentArea != null && contentArea.getChildren().isEmpty()) {
            onMoDashboard();
        }
    }

    @FXML
    private void onMoDashboard() {
        if (appShellController != null) {
            appShellController.loadView("/fxml/DashboardView.fxml");
        } else {
            loadView("/fxml/DashboardView.fxml");
        }
    }

    @FXML
    private void onMoBanHang() {
        loadView("/fxml/DonHangView.fxml");
    }

    @FXML
    private void onMoKho() {
        loadView("/fxml/KhoView.fxml");
    }

    @FXML
    private void onMoNhanSu() {
        loadView("/fxml/QuanLyNhanVienView.fxml");
    }

    @FXML
    private void onMoSanPham() {
        loadView("/fxml/SanPhamView.fxml");
    }

    @FXML
    private void onMoNguyenLieu() {
        loadView("/fxml/NguyenLieuView.fxml");
    }

    @FXML
    private void onMoDanhMuc() {
        loadView("/fxml/DanhMucSPView.fxml");
    }

    @FXML
    private void onMoPhanQuyen() {
        loadView("/fxml/MaTranPhanQuyenView.fxml");
    }

    @FXML
    private void onMoCongThuc() {
        loadView("/fxml/ThanhPhanBanhView.fxml");
    }

    @FXML
    private void onMoKhachHang() {
        loadView("/fxml/KhachHangView.fxml");
    }

    @FXML
    private void onMoBaoCao() {
        loadView("/fxml/BaoCaoView.fxml");
    }

    @FXML
    private void onMoTheoDoiDon() {
        loadView("/fxml/TheoDoiDonHangView.fxml");
    }

    @FXML
    private void onMoNhaCungCap() {
        loadView("/fxml/QuanLyNhaCungCapView.fxml");
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

            URL cssUrl = getClass().getResource("/css/bakery.css");
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
                    btnTongQuan, btnBanHang, btnInventory, btnNhanSuSidebar, 
                    btnKhachHang, btnBaoCao,
                    btnTheoDoiDon, btnKds, btnAuditLogs, btnNhaCungCap,
                    btnDanhMuc, btnSanPham, btnNguyenLieu,
                    btnNhapKho, btnXuatKho, btnKiemKe, btnMaTranPhanQuyen, btnCongThuc
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
            if (controller instanceof DangNhapViewFXMLController) {
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


    private void loadView(String fxmlPath) {
        if (contentArea == null) {
            LOGGER.warning("contentArea is null, cannot load view: " + fxmlPath);
            return;
        }
        Node view = FXMLLoaderUtil.loadFXML(fxmlPath);
        if (view != null) {
            contentArea.getChildren().setAll(view);
        }
    }

    private void capNhatTrangThaiNut(Button button, boolean duocCapQuyen) {
        if (button == null) return;
        button.setDisable(!duocCapQuyen);
        button.setOpacity(duocCapQuyen ? 1.0 : 0.45);
    }

    private String xayDungNhanQuyen(NhanVienDTO nhanVien, boolean laAdmin) {
        if (nhanVien == null) return "Role Access";
        String tenVaiTro = nhanVien.getTenVaiTro();
        if (tenVaiTro != null && !tenVaiTro.isBlank()) {
            return laAdmin ? tenVaiTro + " - Full Access" : tenVaiTro + " - Role Access";
        }
        return laAdmin ? "Admin Access" : "Role Access";
    }
}
