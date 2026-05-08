package com.bakery.views.controllers.hethong;

import com.bakery.model.dto.nhansu.NhanVienDTO;
import com.bakery.services.SessionWatchdogService;
import com.bakery.services.baocao.ThongKeService;
import com.bakery.services.nhansu.PhanQuyenService;
import com.bakery.utils.UserSession;
import com.bakery.views.controllers.nhansu.DangNhapViewFXMLController;
import com.bakery.utils.FXMLLoaderUtil;
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
import java.util.EnumSet;
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
    private Button btnPhanQuyenVaiTro;
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
    private Set<PhanQuyenService.TinhNangHeThong> tinhNangDuocCap =
            EnumSet.noneOf(PhanQuyenService.TinhNangHeThong.class);

    private NhanVienDTO currentUser;
    private AppShellController appShellController;
    private SessionWatchdogService sessionWatchdog;

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
        tinhNangDuocCap = authorizationService.layTinhNangDuocCap(this.currentUser);

        lblTenNguoiDung.setText(
                this.currentUser.getHoTen() == null ? this.currentUser.getTenDangNhap() : this.currentUser.getHoTen());
        if (lblBannerName != null) {
            lblBannerName.setText(lblTenNguoiDung.getText());
        }
        lblVaiTro.setText(xayDungNhanQuyen(this.currentUser, laAdmin));
        capNhatTrangThaiNut(btnTongQuan, coQuyen(PhanQuyenService.TinhNangHeThong.TONG_QUAN));
        capNhatTrangThaiNut(btnBanHang, coQuyen(PhanQuyenService.TinhNangHeThong.BAN_HANG_POS));
        capNhatTrangThaiNut(btnTheoDoiDon, coQuyen(PhanQuyenService.TinhNangHeThong.THEO_DOI_DON_HANG));
        capNhatTrangThaiNut(btnKds, coQuyen(PhanQuyenService.TinhNangHeThong.KDS_MAN_HINH_BEP));
        capNhatTrangThaiNut(btnKhachHang, coQuyen(PhanQuyenService.TinhNangHeThong.KHACH_HANG));

        capNhatTrangThaiNut(btnInventory, coQuyen(PhanQuyenService.TinhNangHeThong.KHO_TONG_QUAN));
        capNhatTrangThaiNut(btnNhapKho, coQuyen(PhanQuyenService.TinhNangHeThong.NHAP_KHO));
        capNhatTrangThaiNut(btnXuatKho, coQuyen(PhanQuyenService.TinhNangHeThong.XUAT_KHO));
        capNhatTrangThaiNut(btnKiemKe, coQuyen(PhanQuyenService.TinhNangHeThong.KIEM_KE_KHO));
        capNhatTrangThaiNut(btnNhaCungCap, coQuyen(PhanQuyenService.TinhNangHeThong.NHA_CUNG_CAP));

        capNhatTrangThaiNut(btnSanPham, coQuyen(PhanQuyenService.TinhNangHeThong.SAN_PHAM));
        capNhatTrangThaiNut(btnNguyenLieu, coQuyen(PhanQuyenService.TinhNangHeThong.NGUYEN_LIEU));
        capNhatTrangThaiNut(btnDanhMuc, coQuyen(PhanQuyenService.TinhNangHeThong.DANH_MUC_SAN_PHAM));
        capNhatTrangThaiNut(btnCongThuc, coQuyen(PhanQuyenService.TinhNangHeThong.CONG_THUC_SAN_XUAT));

        capNhatTrangThaiNut(btnNhanSuSidebar, coQuyen(PhanQuyenService.TinhNangHeThong.NHAN_SU));
        capNhatTrangThaiNut(btnMaTranPhanQuyen, coQuyen(PhanQuyenService.TinhNangHeThong.PHAN_QUYEN_TAI_KHOAN));
        capNhatTrangThaiNut(btnPhanQuyenVaiTro, coQuyen(PhanQuyenService.TinhNangHeThong.PHAN_QUYEN_VAI_TRO));

        capNhatTrangThaiNut(btnBaoCao, coQuyen(PhanQuyenService.TinhNangHeThong.BAO_CAO_KINH_DOANH));
        capNhatTrangThaiNut(btnAuditLogs, coQuyen(PhanQuyenService.TinhNangHeThong.NHAT_KY_HE_THONG));

        // Load dashboard mặc định nếu chưa có gì
        if (contentArea != null && contentArea.getChildren().isEmpty()) {
            onMoDashboard();
        }

        // Bắt đầu giám sát phiên đăng nhập qua DB token
        khoiDongWatchdog();
    }

    @FXML
    private void onMoDashboard() {
        if (!yeuCauTruyCap(PhanQuyenService.TinhNangHeThong.TONG_QUAN, "Tổng quan")) return;
        if (appShellController != null) {
            appShellController.loadView("/fxml/DashboardView.fxml");
        } else {
            loadView("/fxml/DashboardView.fxml");
        }
    }

    @FXML
    private void onMoBanHang() {
        if (!yeuCauTruyCap(PhanQuyenService.TinhNangHeThong.BAN_HANG_POS, "Đặt hàng POS")) return;
        loadView("/fxml/DonHangView.fxml");
    }

    @FXML
    private void onMoKho() {
        if (!yeuCauTruyCap(PhanQuyenService.TinhNangHeThong.KHO_TONG_QUAN, "Tồn kho")) return;
        moKhoTab("kiemke");
    }

    @FXML
    private void onMoSanPham() {
        if (!yeuCauTruyCap(PhanQuyenService.TinhNangHeThong.SAN_PHAM, "Quản lý sản phẩm")) return;
        moKhoTab("sanpham");
    }

    @FXML
    private void onMoNguyenLieu() {
        if (!yeuCauTruyCap(PhanQuyenService.TinhNangHeThong.NGUYEN_LIEU, "Quản lý nguyên liệu")) return;
        moKhoTab("nguyenlieu");
    }

    @FXML
    private void onMoDanhMuc() {
        if (!yeuCauTruyCap(PhanQuyenService.TinhNangHeThong.DANH_MUC_SAN_PHAM, "Quản lý danh mục sản phẩm")) return;
        moKhoTab("danhmuc");
    }

    @FXML
    private void onMoNhapKho() {
        if (!yeuCauTruyCap(PhanQuyenService.TinhNangHeThong.NHAP_KHO, "Nhập kho")) return;
        moKhoTab("nhapkho");
    }

    @FXML
    private void onMoXuatKho() {
        if (!yeuCauTruyCap(PhanQuyenService.TinhNangHeThong.XUAT_KHO, "Xuất kho")) return;
        moKhoTab("xuatkho");
    }

    @FXML
    private void onMoKiemKe() {
        if (!yeuCauTruyCap(PhanQuyenService.TinhNangHeThong.KIEM_KE_KHO, "Kiểm kê kho")) return;
        moKhoTab("kiemke");
    }

    @FXML
    private void onMoNhaCungCap() {
        if (!yeuCauTruyCap(PhanQuyenService.TinhNangHeThong.NHA_CUNG_CAP, "Nhà cung cấp")) return;
        moKhoTab("nhacungcap");
    }

    /**
     * Tải KhoView.fxml (chỉ 1 lần) rồi chuyển sang tab tương ứng.
     * Dùng FXMLLoaderUtil.getLoader() để lấy controller sau khi load.
     */
    private void moKhoTab(String tabKey) {
        try {
            javafx.fxml.FXMLLoader loader = com.bakery.utils.FXMLLoaderUtil.getLoader("/fxml/KhoView.fxml");
            javafx.scene.Node view = loader.load();
            com.bakery.views.controllers.kho.KhoViewFXMLController khoCtrl = loader.getController();
            if (khoCtrl != null) khoCtrl.chuyenTab(tabKey);
            if (contentArea != null) contentArea.getChildren().setAll(view);
        } catch (Exception e) {
            java.util.logging.Logger.getLogger(getClass().getName())
                    .warning("Khong the mo KhoView: " + e.getMessage());
        }
    }

    @FXML
    private void onMoNhanSu() {
        if (!yeuCauTruyCap(PhanQuyenService.TinhNangHeThong.NHAN_SU, "Quản lý nhân sự")) return;
        loadView("/fxml/QuanLyNhanVienView.fxml");
    }

    @FXML
    private void onMoPhanQuyen() {
        if (!yeuCauTruyCap(PhanQuyenService.TinhNangHeThong.PHAN_QUYEN_TAI_KHOAN, "Phân quyền tài khoản")) return;
        loadView("/fxml/MaTranPhanQuyenView.fxml");
    }

    @FXML
    private void onMoPhanQuyenVaiTro() {
        if (!yeuCauTruyCap(PhanQuyenService.TinhNangHeThong.PHAN_QUYEN_VAI_TRO, "Phân quyền vai trò")) return;
        loadView("/fxml/PhanQuyenVaiTroView.fxml");
    }

    @FXML
    private void onMoCongThuc() {
        if (!yeuCauTruyCap(PhanQuyenService.TinhNangHeThong.CONG_THUC_SAN_XUAT, "Công thức sản xuất")) return;
        loadView("/fxml/ThanhPhanBanhView.fxml");
    }

    @FXML
    private void onMoKhachHang() {
        if (!yeuCauTruyCap(PhanQuyenService.TinhNangHeThong.KHACH_HANG, "Khách hàng")) return;
        loadView("/fxml/KhachHangView.fxml");
    }

    @FXML
    private void onMoBaoCao() {
        if (!yeuCauTruyCap(PhanQuyenService.TinhNangHeThong.BAO_CAO_KINH_DOANH, "Báo cáo kinh doanh")) return;
        loadView("/fxml/BaoCaoView.fxml");
    }

    @FXML
    private void onMoLichSuHeThong() {
        if (!yeuCauTruyCap(PhanQuyenService.TinhNangHeThong.NHAT_KY_HE_THONG, "Nhật ký hệ thống")) return;
        loadView("/fxml/LichSuHeThongView.fxml");
    }

    @FXML
    private void onMoTheoDoiDon() {
        if (!yeuCauTruyCap(PhanQuyenService.TinhNangHeThong.THEO_DOI_DON_HANG, "Theo dõi đơn hàng")) return;
        loadView("/fxml/TheoDoiDonHangView.fxml");
    }

    @FXML
    private void onMoKds() {
        if (!yeuCauTruyCap(PhanQuyenService.TinhNangHeThong.KDS_MAN_HINH_BEP, "màn hình KDS")) return;
        loadView("/fxml/ThoBepDashboardView.fxml");
    }

    @FXML
    private void onQuanLyCa() {
        if (!yeuCauTruyCap(PhanQuyenService.TinhNangHeThong.QUAN_LY_CA_LAM_VIEC, "quản lý ca làm việc")) return;
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
        if (!yeuCauTruyCap(PhanQuyenService.TinhNangHeThong.NHAN_SU, "thêm nhân sự")) return;
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
        dungWatchdog();
        lblThongBao.setText("Đang đăng xuất...");

        // Revoke token trong DB trên background thread — KHÔNG block UI
        javafx.concurrent.Task<Void> taskDangXuat = new javafx.concurrent.Task<>() {
            @Override
            protected Void call() throws Exception {
                new com.bakery.services.nhansu.XacThucService().dangXuat();
                return null;
            }
        };

        taskDangXuat.setOnSucceeded(evt -> javafx.application.Platform.runLater(this::chuyenVeDangNhap));
        taskDangXuat.setOnFailed(evt -> javafx.application.Platform.runLater(() -> {
            // Dù DB lỗi vẫn xóa session local và về màn đăng nhập
            UserSession.clear();
            com.bakery.utils.SessionContext.clear();
            chuyenVeDangNhap();
        }));

        Thread t = new Thread(taskDangXuat);
        t.setDaemon(true);
        t.start();
    }

    /** Điều hướng về màn đăng nhập — chỉ gọi từ FX thread. */
    private void chuyenVeDangNhap() {
        try {
            URL fxmlUrl = getClass().getResource("/fxml/DangNhapView.fxml");
            if (fxmlUrl == null) {
                throw new RuntimeException("Không tìm thấy /fxml/DangNhapView.fxml");
            }

            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();

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

    private String xayDungNhanQuyen(NhanVienDTO nhanVien, boolean laAdmin) {
        if (nhanVien == null) return "Role Access";
        String tenVaiTro = nhanVien.getTenVaiTro();
        if (tenVaiTro != null && !tenVaiTro.isBlank()) {
            return laAdmin ? tenVaiTro + " - Full Access" : tenVaiTro + " - Role Access";
        }
        return laAdmin ? "Admin Access" : "Role Access";
    }

    // ─── Watchdog helpers ────────────────────────────────────────────────────

    private void khoiDongWatchdog() {
        sessionWatchdog = new SessionWatchdogService();
        sessionWatchdog.setOnSessionInvalid(this::xuLyPhienHetHan);
        sessionWatchdog.start();
        LOGGER.info("[Watchdog] Bắt đầu giám sát phiên token.");
    }

    private void dungWatchdog() {
        if (sessionWatchdog != null && sessionWatchdog.isRunning()) {
            sessionWatchdog.cancel();
        }
    }

    /**
     * Gọi khi watchdog phát hiện token không còn tồn tại trong DB.
     * Thông báo user và chuyển về màn hình đăng nhập.
     */
    private void xuLyPhienHetHan() {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                javafx.scene.control.Alert.AlertType.WARNING);
        alert.setTitle("Hết phiên đăng nhập");
        alert.setHeaderText(null);
        alert.setContentText("Phân quyền của bạn đã bị thu hồi hoặc phiên đăng nhập đã kết thúc.\nVui lòng đăng nhập lại.");
        alert.showAndWait();
        try {
            java.net.URL fxmlUrl = getClass().getResource("/fxml/DangNhapView.fxml");
            if (fxmlUrl == null) return;
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(fxmlUrl);
            javafx.scene.Parent root = loader.load();
            javafx.scene.Scene scene = new javafx.scene.Scene(root);
            java.net.URL cssUrl = getClass().getResource("/css/bakery.css");
            if (cssUrl != null) scene.getStylesheets().add(cssUrl.toExternalForm());
            javafx.stage.Stage stage = (javafx.stage.Stage) lblTenNguoiDung.getScene().getWindow();
            stage.setTitle("H3K Bakery - Đăng nhập");
            stage.setScene(scene);
            stage.setResizable(false);
            stage.centerOnScreen();
        } catch (Exception ex) {
            LOGGER.severe("[Watchdog] Lỗi chuyển về trang đăng nhập: " + ex.getMessage());
        }
    }
}
