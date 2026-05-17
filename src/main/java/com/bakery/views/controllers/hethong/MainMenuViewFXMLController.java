package com.bakery.views.controllers.hethong;

import com.bakery.model.dto.nhansu.NhanVienDTO;
import com.bakery.services.SessionWatchdogService;
import com.bakery.services.nhansu.XacThucService;
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
import javafx.scene.control.Dialog;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.GridPane;
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
import java.util.logging.Logger;

public class MainMenuViewFXMLController {
    private static final Logger LOGGER = Logger.getLogger(MainMenuViewFXMLController.class.getName());

    // ─── Labels ──────────────────────────────────────────────────────────────
    @FXML
    private Label lblTenNguoiDung;
    @FXML
    private Label lblVaiTro;
    @FXML
    private Label lblThongBao;
    @FXML
    private Label lblBannerName;
    @FXML
    private Button btnAvatar;

    // ─── Sidebar layout ───────────────────────────────────────────────────────
    @FXML
    private VBox vboxSidebar;
    @FXML
    private HBox hboxLogo;
    @FXML
    private Label lblLogoText;
    @FXML
    private ScrollPane scrollNav;

    // ─── Sidebar buttons ──────────────────────────────────────────────────────
    @FXML
    private Button btnTongQuan;
    // BAN HANG
    @FXML
    private Button btnBanHang;
    @FXML
    private Button btnTheoDoiDon;
    @FXML
    private Button btnKhachHang;
    // SAN PHAM — 1 nut cha gom 4 module
    @FXML
    private Button btnSanPham;
    // KHO — 1 nut cha
    @FXML
    private Button btnKho;
    // BEP — 1 nut cha
    @FXML
    private Button btnBep;
    // NHAN SU — 1 nut cha
    @FXML
    private Button btnNhanSu;
    @FXML
    private Button btnBaoCao;
    @FXML
    private Button btnAuditLogs;
    @FXML
    private Button btnGiamSatCa;
    @FXML
    private Button btnCauHinhGioiHan;
    @FXML
    private Button btnKhoiPhucDuLieu;
    @FXML
    private Button btnQuanLyCa;
    @FXML
    private Button btnDoiVaiTro;

    // ─── Misc ─────────────────────────────────────────────────────────────────
    @FXML
    private FlowPane flowBestSellersMenu;
    @FXML
    private StackPane contentArea;

    public StackPane getContentArea() {
        return contentArea;
    }

    private boolean isSidebarCollapsed = false;
    private final java.util.Map<Button, String> buttonTextMap = new java.util.HashMap<>();
    private Button currentActiveBtn = null;

    private final PhanQuyenService authorizationService = new PhanQuyenService();
    private Set<PhanQuyenService.TinhNangHeThong> tinhNangDuocCap = EnumSet
            .noneOf(PhanQuyenService.TinhNangHeThong.class);

    private NhanVienDTO currentUser;
    private AppShellController appShellController;
    private SessionWatchdogService sessionWatchdog;

    public void setAppShellController(AppShellController appShellController) {
        this.appShellController = appShellController;
    }

    public void khoiTaoThongTinDangNhap(NhanVienDTO nhanVien) {
        this.currentUser = nhanVien != null ? nhanVien : UserSession.getCurrentUser();
        if (this.currentUser == null) {
            lblThongBao.setText("Khong tim thay thong tin dang nhap.");
            return;
        }

        UserSession.setCurrentUser(this.currentUser);
        boolean laAdmin = authorizationService.laAdmin(this.currentUser);
        tinhNangDuocCap = authorizationService.layTinhNangDuocCap(this.currentUser);

        lblTenNguoiDung.setText(
                this.currentUser.getHoTen() == null ? this.currentUser.getTenDangNhap() : this.currentUser.getHoTen());
        if (btnAvatar != null) {
            btnAvatar.setText(taoKyTuAvatar(lblTenNguoiDung.getText()));
        }
        if (lblBannerName != null) {
            lblBannerName.setText(lblTenNguoiDung.getText());
        }
        // Hien/an nut Doi Vai Tro (chi hien khi co > 1 vai tro)
        boolean coNhieuVaiTro = this.currentUser.getDanhSachMaVaiTro().size() > 1;
        if (btnDoiVaiTro != null) {
            btnDoiVaiTro.setVisible(coNhieuVaiTro);
            btnDoiVaiTro.setManaged(coNhieuVaiTro);
        }

        // Hien thi vai tro dang hoat dong (active role) hoac ten vai tro chinh
        String tenVaiTroHienThi = com.bakery.utils.SessionContext.getInstance().getActiveVaiTroTen();
        if (tenVaiTroHienThi == null || tenVaiTroHienThi.isBlank()) {
            tenVaiTroHienThi = this.currentUser.getTenVaiTro();
        }
        final String tenVTFinal = tenVaiTroHienThi;
        lblVaiTro.setText(laAdmin ? tenVTFinal + " - Full Access" : tenVTFinal + " - Role Access");

        // Tong quan
        capNhatTrangThaiNut(btnTongQuan, coQuyen(PhanQuyenService.TinhNangHeThong.TONG_QUAN));

        // Ban hang: POS → hien btnBanHang; chi THEO_DOI (Tho Bep) → hien btnTheoDoiDon rieng
        boolean coQuyenPos = coQuyen(PhanQuyenService.TinhNangHeThong.BAN_HANG_POS);
        boolean coQuyenTheoDoi = coQuyen(PhanQuyenService.TinhNangHeThong.THEO_DOI_DON_HANG);
        capNhatTrangThaiNut(btnBanHang, coQuyenPos);
        // Tho Bep: co THEO_DOI nhung khong co POS → hien nut rieng "Theo doi don hang"
        capNhatTrangThaiNut(btnTheoDoiDon, coQuyenTheoDoi && !coQuyenPos);
        capNhatTrangThaiNut(btnKhachHang, coQuyen(PhanQuyenService.TinhNangHeThong.KHACH_HANG));

        // San pham — 1 nut gom 4 module
        boolean coQuyenSP = coQuyen(PhanQuyenService.TinhNangHeThong.SAN_PHAM)
                || coQuyen(PhanQuyenService.TinhNangHeThong.DANH_MUC_SAN_PHAM)
                || coQuyen(PhanQuyenService.TinhNangHeThong.CONG_THUC_SAN_XUAT)
                || coQuyen(PhanQuyenService.TinhNangHeThong.THANH_PHAN_BANH);
        capNhatTrangThaiNut(btnSanPham, coQuyenSP);

        // Nut cha KHO — mo neu co it nhat 1 quyen kho
        boolean coQuyenKho = coQuyen(PhanQuyenService.TinhNangHeThong.KHO_TONG_QUAN)
                || coQuyen(PhanQuyenService.TinhNangHeThong.NHAP_KHO)
                || coQuyen(PhanQuyenService.TinhNangHeThong.KIEM_KE_KHO)
                || coQuyen(PhanQuyenService.TinhNangHeThong.NHA_CUNG_CAP)
                || coQuyen(PhanQuyenService.TinhNangHeThong.NGUYEN_LIEU);
        capNhatTrangThaiNut(btnKho, coQuyenKho);

        // Nut cha BEP — mo neu co it nhat 1 quyen bep
        boolean coQuyenBep = coQuyen(PhanQuyenService.TinhNangHeThong.XUAT_KHO)
                || coQuyen(PhanQuyenService.TinhNangHeThong.DON_HANG_BEP)
                || coQuyen(PhanQuyenService.TinhNangHeThong.KDS_MAN_HINH_BEP);
        capNhatTrangThaiNut(btnBep, coQuyenBep);

        // Nhan su — hien nut cha neu co it nhat 1 quyen nhan su
        boolean coQuyenNhanSu = coQuyen(PhanQuyenService.TinhNangHeThong.NHAN_SU)
                || coQuyen(PhanQuyenService.TinhNangHeThong.PHAN_QUYEN_TAI_KHOAN)
                || coQuyen(PhanQuyenService.TinhNangHeThong.PHAN_QUYEN_VAI_TRO);
        capNhatTrangThaiNut(btnNhanSu, coQuyenNhanSu);

        // Bao cao
        capNhatTrangThaiNut(btnBaoCao, coQuyen(PhanQuyenService.TinhNangHeThong.BAO_CAO_KINH_DOANH));
        capNhatTrangThaiNut(btnAuditLogs, coQuyen(PhanQuyenService.TinhNangHeThong.NHAT_KY_HE_THONG));
        // Giam sat tien mat dong ca — chi Quan ly
        capNhatTrangThaiNut(btnGiamSatCa, laAdmin || authorizationService.laQuanLy(currentUser));
        // Cấu hình giới hạn đơn — chỉ Quản lý (Service đã lọc, View double-check)
        boolean coQuyenCauHinh = coQuyen(PhanQuyenService.TinhNangHeThong.CAU_HINH_GIOI_HAN_DON)
                && (laAdmin || authorizationService.laQuanLy(currentUser));
        capNhatTrangThaiNut(btnCauHinhGioiHan, coQuyenCauHinh);
        // Khôi phục dữ liệu — chỉ Quản lý
        capNhatTrangThaiNut(btnKhoiPhucDuLieu, coQuyen(PhanQuyenService.TinhNangHeThong.KHOI_PHUC_DU_LIEU));
        // Nút Mở/Đóng Ca — chỉ Thu Ngân có quyền QUAN_LY_CA_LAM_VIEC
        capNhatTrangThaiNut(btnQuanLyCa, coQuyen(PhanQuyenService.TinhNangHeThong.QUAN_LY_CA_LAM_VIEC));

        // Load dashboard mac dinh
        if (contentArea != null && contentArea.getChildren().isEmpty()) {
            onMoDashboard();
        }

        khoiDongWatchdog();
    }

    /** Đặt nút nav đang active — xoá class cũ, thêm class mới. */
    private void setActiveNavButton(Button btn) {
        if (currentActiveBtn != null) {
            currentActiveBtn.getStyleClass().remove("dash-nav-active");
        }
        currentActiveBtn = btn;
        if (btn != null && !btn.getStyleClass().contains("dash-nav-active")) {
            btn.getStyleClass().add("dash-nav-active");
        }
    }

    // ─── Navigation handlers ──────────────────────────────────────────────────

    @FXML
    private void onMoDashboard() {
        if (!yeuCauTruyCap(PhanQuyenService.TinhNangHeThong.TONG_QUAN, "Tong quan"))
            return;
        setActiveNavButton(btnTongQuan);
        if (appShellController != null) {
            appShellController.loadView("/fxml/hethong/DashboardView.fxml");
        } else {
            loadView("/fxml/hethong/DashboardView.fxml");
        }
    }

    @FXML
    private void onMoBanHang() {
        boolean coQuyenPos = coQuyen(PhanQuyenService.TinhNangHeThong.BAN_HANG_POS);
        boolean coQuyenTheoDoi = coQuyen(PhanQuyenService.TinhNangHeThong.THEO_DOI_DON_HANG);
        if (!coQuyenPos && !coQuyenTheoDoi) {
            if (lblThongBao != null)
                lblThongBao.setText("Ban khong co quyen truy cap Ban hang.");
            return;
        }
        setActiveNavButton(btnBanHang);
        // Có quyền POS → load BanHangView (TabPane: POS + Theo dõi đơn)
        // Chỉ có THEO_DOI → load thẳng TheoDoiDonHangView
        loadView(coQuyenPos ? "/fxml/banhang/BanHangView.fxml" : "/fxml/banhang/TheoDoiDonHangView.fxml");
    }

    @FXML
    private void onMoTheoDoiDon() {
        if (!yeuCauTruyCap(PhanQuyenService.TinhNangHeThong.THEO_DOI_DON_HANG, "Theo doi don hang"))
            return;
        loadView("/fxml/banhang/TheoDoiDonHangView.fxml");
    }

    @FXML
    private void onMoKhachHang() {
        if (!yeuCauTruyCap(PhanQuyenService.TinhNangHeThong.KHACH_HANG, "Khach hang"))
            return;
        setActiveNavButton(btnKhachHang);
        loadView("/fxml/khachhang/KhachHangView.fxml");
    }

    @FXML
    private void onMoSanPham() {
        boolean coQuyen = coQuyen(PhanQuyenService.TinhNangHeThong.SAN_PHAM)
                || coQuyen(PhanQuyenService.TinhNangHeThong.DANH_MUC_SAN_PHAM)
                || coQuyen(PhanQuyenService.TinhNangHeThong.CONG_THUC_SAN_XUAT)
                || coQuyen(PhanQuyenService.TinhNangHeThong.THANH_PHAN_BANH);
        if (!coQuyen) {
            if (lblThongBao != null)
                lblThongBao.setText("Ban khong co quyen truy cap San pham.");
            return;
        }
        setActiveNavButton(btnSanPham);
        loadView("/fxml/kho/QuanLySanPhamView.fxml");
    }

    /** Mo man hinh KHO — tab mac dinh: Nha cung cap. */
    @FXML
    private void onMoKho() {
        setActiveNavButton(btnKho);
        moKhoTab("nhacungcap");
    }

    /** Mo man hinh BEP — tab mac dinh: Lap phieu xuat kho. */
    @FXML
    private void onMoBep() {
        setActiveNavButton(btnBep);
        moBepTab("xuatkho");
    }

    @FXML
    private void onMoNhanSu() {
        boolean coQuyen = coQuyen(PhanQuyenService.TinhNangHeThong.NHAN_SU)
                || coQuyen(PhanQuyenService.TinhNangHeThong.PHAN_QUYEN_TAI_KHOAN)
                || coQuyen(PhanQuyenService.TinhNangHeThong.PHAN_QUYEN_VAI_TRO);
        if (!coQuyen) {
            if (lblThongBao != null)
                lblThongBao.setText("Ban khong co quyen truy cap Nhan su.");
            return;
        }
        setActiveNavButton(btnNhanSu);
        loadView("/fxml/nhansu/NhanSuView.fxml");
    }

    @FXML
    private void onMoBaoCao() {
        if (!yeuCauTruyCap(PhanQuyenService.TinhNangHeThong.BAO_CAO_KINH_DOANH, "Bao cao kinh doanh"))
            return;
        setActiveNavButton(btnBaoCao);
        moBaoCaoTab("thongke");
    }

    @FXML
    private void onMoLichSuHeThong() {
        if (!yeuCauTruyCap(PhanQuyenService.TinhNangHeThong.NHAT_KY_HE_THONG, "Nhat ky he thong"))
            return;
        setActiveNavButton(btnAuditLogs);
        loadView("/fxml/hethong/LichSuHeThongView.fxml");
    }

    @FXML
    private void onMoKhoiPhucDuLieu() {
        if (!yeuCauTruyCap(PhanQuyenService.TinhNangHeThong.KHOI_PHUC_DU_LIEU, "Khoi phuc du lieu"))
            return;
        setActiveNavButton(btnKhoiPhucDuLieu);
        loadView("/fxml/hethong/KhoiPhucDuLieuView.fxml");
    }

    @FXML
    private void onMoCauHinhGioiHan() {
        if (!yeuCauTruyCap(PhanQuyenService.TinhNangHeThong.CAU_HINH_GIOI_HAN_DON, "Cau hinh gioi han nhan don"))
            return;
        setActiveNavButton(btnCauHinhGioiHan);
        moBepTab("cauhinhgioihan");
    }

    @FXML
    private void onMoGiamSatCa() {
        if (!authorizationService.laQuanLy(currentUser) && !authorizationService.laAdmin(currentUser)) {
            lblThongBao.setText("⚠️ Chuc nang nay chi danh cho Quan ly.");
            return;
        }
        setActiveNavButton(btnGiamSatCa);
        moBaoCaoTab("giamsatca");
    }

    // ─── Ca lam viec dialog ───────────────────────────────────────────────────

    @FXML
    private void onQuanLyCa() {
        if (!yeuCauTruyCap(PhanQuyenService.TinhNangHeThong.QUAN_LY_CA_LAM_VIEC, "quan ly ca lam viec"))
            return;
        try {
            boolean caoDangMo = com.bakery.utils.SessionContext.getInstance().isCaoDangMo();
            if (caoDangMo) {
                Stage owner = (Stage) lblTenNguoiDung.getScene().getWindow();
                DoiSoatDongCaViewFXMLController.hienThi(owner, false);
                return;
            }

            String fxmlPath = caoDangMo ? "/fxml/hethong/DoiSoatDongCaView.fxml" : "/fxml/hethong/MoCaView.fxml";
            String title = caoDangMo ? "H3K Bakery - Dong ca" : "H3K Bakery - Mo ca";

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
            lblThongBao.setText("Loi mo quan ly ca: " + ex.getMessage());
            LOGGER.warning("[MainMenu] Quan ly ca error: " + ex.getMessage());
        }
    }

    // ─── Sidebar toggle ───────────────────────────────────────────────────────

    @FXML
    private void onToggleSidebar() {
        if (buttonTextMap.isEmpty()) {
            Button[] navButtons = {
                    btnTongQuan, btnBanHang, btnTheoDoiDon, btnKhachHang,
                    btnSanPham,
                    btnKho, btnBep,
                    btnNhanSu,
                    btnBaoCao, btnAuditLogs, btnGiamSatCa, btnCauHinhGioiHan, btnKhoiPhucDuLieu,
                    btnQuanLyCa
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
    private void onModuleChuaSanSang() {
        lblThongBao.setText("Chuc nang dang phat trien.");
    }

    @FXML
    private void onMoThongTinCaNhan() {
        if (currentUser == null)
            return;

        TextField txtHoTen = new TextField(currentUser.getHoTen() == null ? "" : currentUser.getHoTen());
        TextField txtSdt = new TextField(currentUser.getSdt() == null ? "" : currentUser.getSdt());
        TextField txtEmail = new TextField(currentUser.getEmail() == null ? "" : currentUser.getEmail());
        PasswordField txtMatKhauMoi = new PasswordField();
        PasswordField txtXacNhanMatKhau = new PasswordField();

        txtHoTen.getStyleClass().add("text-field");
        txtSdt.getStyleClass().add("text-field");
        txtEmail.getStyleClass().add("text-field");
        txtMatKhauMoi.getStyleClass().add("text-field");
        txtXacNhanMatKhau.getStyleClass().add("text-field");

        txtEmail.setPromptText("example@email.com");
        txtMatKhauMoi.setPromptText("Nhập mật khẩu mới");
        txtXacNhanMatKhau.setPromptText("Xác nhận mật khẩu mới");

        GridPane form = new GridPane();
        form.setHgap(12);
        form.setVgap(10);
        form.setPadding(new Insets(8, 0, 0, 0));
        form.add(new Label("Họ tên:"), 0, 0);
        form.add(txtHoTen, 1, 0);
        form.add(new Label("Số điện thoại:"), 0, 1);
        form.add(txtSdt, 1, 1);
        form.add(new Label("Email:"), 0, 2);
        form.add(txtEmail, 1, 2);
        form.add(new Label("Mật khẩu mới:"), 0, 3);
        form.add(txtMatKhauMoi, 1, 3);
        form.add(new Label("Xác nhận mật khẩu:"), 0, 4);
        form.add(txtXacNhanMatKhau, 1, 4);

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Cập nhật thông tin cá nhân");
        dialog.setHeaderText("Cập nhật thông tin cá nhân.\nMật khẩu mới là tùy chọn — để trống nếu không muốn đổi.");
        dialog.getDialogPane().setContent(form);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.initOwner(lblTenNguoiDung.getScene().getWindow());

        dialog.showAndWait().ifPresent(bt -> {
            if (bt != ButtonType.OK)
                return;

            String emailNhap = txtEmail.getText() == null ? "" : txtEmail.getText().trim();

            javafx.concurrent.Task<Void> taskCapNhat = new javafx.concurrent.Task<>() {
                @Override
                protected Void call() throws Exception {
                    new XacThucService().capNhatThongTinCaNhan(
                            txtHoTen.getText(),
                            txtSdt.getText(),
                            txtMatKhauMoi.getText(),
                            txtXacNhanMatKhau.getText());
                    // Cập nhật email nếu có điền
                    if (!emailNhap.isBlank()) {
                        new XacThucService().capNhatEmail(emailNhap);
                    }
                    return null;
                }
            };

            taskCapNhat.setOnSucceeded(evt -> javafx.application.Platform.runLater(() -> {
                NhanVienDTO userMoi = UserSession.getCurrentUser();
                if (userMoi != null) {
                    currentUser = userMoi;
                    lblTenNguoiDung.setText(userMoi.getHoTen() == null ? userMoi.getTenDangNhap() : userMoi.getHoTen());
                    String tenVTHT = com.bakery.utils.SessionContext.getInstance().getActiveVaiTroTen();
                    if (tenVTHT == null || tenVTHT.isBlank()) tenVTHT = userMoi.getTenVaiTro();
                    boolean isAdm = authorizationService.laAdmin(userMoi);
                    lblVaiTro.setText(isAdm ? tenVTHT + " - Full Access" : tenVTHT + " - Role Access");
                    if (lblBannerName != null)
                        lblBannerName.setText(lblTenNguoiDung.getText());
                    if (btnAvatar != null)
                        btnAvatar.setText(taoKyTuAvatar(lblTenNguoiDung.getText()));
                }
                if (lblThongBao != null)
                    lblThongBao.setText("Đã cập nhật thông tin cá nhân thành công.");
            }));
            taskCapNhat.setOnFailed(
                    evt -> javafx.application.Platform.runLater(() -> hienThiLoiLabel(taskCapNhat.getException() != null
                            ? taskCapNhat.getException().getMessage()
                            : "Cập nhật thông tin thất bại.")));

            Thread t = new Thread(taskCapNhat, "cap-nhat-thong-tin-ca-nhan");
            t.setDaemon(true);
            t.start();
        });
    }


    // ─── Dang xuat ───────────────────────────────────────────────────────────

    private void hienThiLoiLabel(String thongBao) {
        if (lblThongBao != null) {
            lblThongBao.setText(thongBao == null || thongBao.isBlank()
                    ? "Co loi xay ra. Vui long thu lai."
                    : thongBao);
        }
    }

    @FXML
    private void onDangXuat() {
        // Thu ngan: neu ca dang mo thi mo thang dialog Dong ca (khong hien alert).
        // Check toan bo vai tro (khong loc theo active) vi ca la thuc the vat ly.
        boolean coVaiTroThuNgan = this.currentUser != null
                && this.currentUser.getDanhSachTenVaiTro() != null
                && this.currentUser.getDanhSachTenVaiTro().stream()
                .anyMatch(t -> t != null && t.toUpperCase().contains("THU NGAN"));
        if (coVaiTroThuNgan
                && com.bakery.utils.SessionContext.getInstance().isCaoDangMo()) {
            dungWatchdog();
            Stage owner = (Stage) lblTenNguoiDung.getScene().getWindow();
            DoiSoatDongCaViewFXMLController.hienThi(owner, true);
            if (lblThongBao != null) {
                lblThongBao.setText("Vui long dong ca de hoan tat dang xuat.");
            }
            return;
        }

        dungWatchdog();
        lblThongBao.setText("Dang dang xuat...");

        javafx.concurrent.Task<Void> taskDangXuat = new javafx.concurrent.Task<>() {
            @Override
            protected Void call() throws Exception {
                new com.bakery.services.nhansu.XacThucService().dangXuat();
                return null;
            }
        };

        taskDangXuat.setOnSucceeded(evt -> javafx.application.Platform.runLater(this::chuyenVeDangNhap));
        taskDangXuat.setOnFailed(evt -> javafx.application.Platform.runLater(() -> {
            UserSession.clear();
            com.bakery.utils.SessionContext.clear();
            chuyenVeDangNhap();
        }));

        Thread t = new Thread(taskDangXuat);
        t.setDaemon(true);
        t.start();
    }

    /** Dieu huong ve man dang nhap — chi goi tu FX thread. */
    private void chuyenVeDangNhap() {
        try {
            URL fxmlUrl = getClass().getResource("/fxml/hethong/DangNhapView.fxml");
            if (fxmlUrl == null)
                throw new RuntimeException("Khong tim thay /fxml/hethong/DangNhapView.fxml");

            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();

            Object controller = loader.getController();
            if (controller instanceof DangNhapViewFXMLController) {
                ((DangNhapViewFXMLController) controller).setLoginInfo("Ban da dang xuat thanh cong.");
            }

            Scene scene = new Scene(root);
            URL cssUrl = getClass().getResource("/css/bakery.css");
            if (cssUrl != null)
                scene.getStylesheets().add(cssUrl.toExternalForm());

            Stage stage = (Stage) lblTenNguoiDung.getScene().getWindow();
            stage.setTitle("H3K Bakery - Dang nhap");
            stage.setScene(scene);
            stage.setResizable(true);
            stage.centerOnScreen();
        } catch (Exception ex) {
            lblThongBao.setText("Loi dang xuat: " + ex.getMessage());
            LOGGER.severe("[MainMenu] Dang xuat error: " + ex.getMessage());
        }
    }

    // ─── Helpers load view ────────────────────────────────────────────────────

    /** Tai KhoView roi chuyen sang tab tuong ung. */
    private void moKhoTab(String tabKey) {
        try {
            javafx.fxml.FXMLLoader loader = FXMLLoaderUtil.getLoader("/fxml/kho/KhoView.fxml");
            Node view = loader.load();
            com.bakery.views.controllers.kho.KhoViewFXMLController ctrl = loader.getController();
            if (ctrl != null)
                ctrl.chuyenTab(tabKey);
            if (contentArea != null)
                contentArea.getChildren().setAll(view);
        } catch (Exception e) {
            LOGGER.warning("Khong the mo KhoView: " + e.getMessage());
        }
    }

    /** Tai BepView roi chuyen sang tab tuong ung. */
    private void moBepTab(String tabKey) {
        try {
            javafx.fxml.FXMLLoader loader = FXMLLoaderUtil.getLoader("/fxml/hethong/BepView.fxml");
            Node view = loader.load();
            com.bakery.views.controllers.bep.BepViewFXMLController ctrl = loader.getController();
            if (ctrl != null)
                ctrl.chuyenTab(tabKey);
            if (contentArea != null)
                contentArea.getChildren().setAll(view);
        } catch (Exception e) {
            LOGGER.warning("Khong the mo BepView: " + e.getMessage());
        }
    }

    /** Tai BaoCaoView roi chuyen sang tab tuong ung. */
    private void moBaoCaoTab(String tabKey) {
        try {
            javafx.fxml.FXMLLoader loader = FXMLLoaderUtil.getLoader("/fxml/baocao/BaoCaoView.fxml");
            Node view = loader.load();
            com.bakery.views.controllers.baocao.BaoCaoViewFXMLController ctrl = loader.getController();
            if (ctrl != null)
                ctrl.chuyenTab(tabKey);
            if (contentArea != null)
                contentArea.getChildren().setAll(view);
        } catch (Exception e) {
            LOGGER.warning("Khong the mo BaoCaoView: " + e.getMessage());
        }
    }

    private void loadView(String fxmlPath) {
        if (contentArea == null) {
            LOGGER.warning("contentArea is null, cannot load view: " + fxmlPath);
            return;
        }
        Node view = FXMLLoaderUtil.loadFXML(fxmlPath);
        if (view != null)
            contentArea.getChildren().setAll(view);
    }

    // ─── Quyen ───────────────────────────────────────────────────────────────

    private void capNhatTrangThaiNut(Button button, boolean duocCapQuyen) {
        if (button == null)
            return;
        button.setDisable(!duocCapQuyen);
        button.setVisible(duocCapQuyen);
        button.setManaged(duocCapQuyen);
        button.setOpacity(1.0);
    }

    private void anNutDieuHuong(Button button) {
        if (button == null)
            return;
        button.setDisable(true);
        button.setVisible(false);
        button.setManaged(false);
    }

    private String taoKyTuAvatar(String ten) {
        if (ten == null || ten.isBlank())
            return "👤";
        return String.valueOf(Character.toUpperCase(ten.trim().charAt(0)));
    }

    private boolean coQuyen(PhanQuyenService.TinhNangHeThong tinhNang) {
        return tinhNangDuocCap != null && tinhNangDuocCap.contains(tinhNang);
    }

    private boolean yeuCauTruyCap(PhanQuyenService.TinhNangHeThong tinhNang, String tenTinhNang) {
        if (coQuyen(tinhNang))
            return true;
        if (lblThongBao != null) {
            lblThongBao.setText("Ban khong co quyen truy cap " + tenTinhNang + ".");
        }
        return false;
    }

    /** Doi vai tro lam viec ngay trong app — chi hien khi user co > 1 vai tro. */
    @FXML
    private void onDoiVaiTro() {
        if (currentUser == null) return;

        // Khong cho doi vai tro khi ca dang mo (tranh mat trang thai ca)
        if (com.bakery.utils.SessionContext.getInstance().isCaoDangMo()) {
            if (lblThongBao != null)
                lblThongBao.setText("⚠️ Vui long dong ca truoc khi doi vai tro.");
            return;
        }

        java.util.List<Integer> maList  = currentUser.getDanhSachMaVaiTro();
        java.util.List<String>  tenList = currentUser.getDanhSachTenVaiTro();
        int activeId = com.bakery.utils.SessionContext.getInstance().getActiveVaiTroId();

        javafx.scene.control.Dialog<Integer> dialog = new javafx.scene.control.Dialog<>();
        dialog.setTitle("Doi vai tro lam viec");
        dialog.setHeaderText("🔄 Chon vai tro ban muon chuyen sang:");
        dialog.initOwner(lblTenNguoiDung.getScene().getWindow());

        javafx.scene.layout.VBox vbox = new javafx.scene.layout.VBox(10);
        vbox.setPadding(new javafx.geometry.Insets(20));
        vbox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        javafx.scene.control.ToggleGroup group = new javafx.scene.control.ToggleGroup();
        for (int i = 0; i < Math.min(maList.size(), tenList.size()); i++) {
            final int    maVT  = maList.get(i);
            final String tenVT = tenList.get(i);
            String icon = layIconVaiTro(tenVT);
            javafx.scene.control.RadioButton rb =
                    new javafx.scene.control.RadioButton(icon + " " + tenVT);
            rb.setUserData(maVT);
            rb.setToggleGroup(group);
            rb.setStyle("-fx-font-size: 14px; -fx-padding: 8 16;");
            if (maVT == activeId) rb.setSelected(true);
            else if (group.getSelectedToggle() == null) rb.setSelected(true);
            vbox.getChildren().add(rb);
        }

        dialog.getDialogPane().setContent(vbox);
        javafx.scene.control.ButtonType btnOk =
                new javafx.scene.control.ButtonType("Chuyen vai tro",
                        javafx.scene.control.ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnOk,
                javafx.scene.control.ButtonType.CANCEL);
        dialog.setResultConverter(bt -> {
            if (bt == btnOk && group.getSelectedToggle() != null)
                return (Integer) group.getSelectedToggle().getUserData();
            return null;
        });

        try {
            java.net.URL cssUrl = getClass().getResource("/css/bakery.css");
            if (cssUrl != null) dialog.getDialogPane().getStylesheets().add(cssUrl.toExternalForm());
        } catch (Exception ignored) {}

        dialog.showAndWait().ifPresent(maChon -> {
            if (maChon == activeId) return; // Khong thay doi
            String tenChon = "";
            for (int i = 0; i < Math.min(maList.size(), tenList.size()); i++) {
                if (maList.get(i).equals(maChon)) { tenChon = tenList.get(i); break; }
            }
            // Cap nhat active role va refresh toan bo sidebar/permissions
            com.bakery.utils.SessionContext.getInstance().datVaiTroHoatDong(maChon, tenChon);
            khoiTaoThongTinDangNhap(currentUser);
            if (lblThongBao != null)
                lblThongBao.setText("✅ Da chuyen sang vai tro: " + tenChon);
        });
    }

    /** Icon emoji cho tung loai vai tro (dung trong dialog). */
    private String layIconVaiTro(String tenVaiTro) {
        if (tenVaiTro == null) return "👤";
        String chuan = tenVaiTro.toUpperCase(java.util.Locale.ROOT);
        if (chuan.contains("QUAN LY") || chuan.contains("ADMIN")) return "👨\u200D💼";
        if (chuan.contains("THU NGAN")) return "💰";
        if (chuan.contains("BEP"))     return "🍳";
        if (chuan.contains("KHO"))     return "📦";
        return "👤";
    }

    // ─── Watchdog ─────────────────────────────────────────────────────────────

    private void khoiDongWatchdog() {
        sessionWatchdog = new SessionWatchdogService();
        sessionWatchdog.setOnSessionInvalid(this::xuLyPhienHetHan);
        sessionWatchdog.start();
        LOGGER.info("[Watchdog] Bat dau giam sat phien token.");
    }

    private void dungWatchdog() {
        if (sessionWatchdog != null && sessionWatchdog.isRunning()) {
            sessionWatchdog.cancel();
        }
    }

    /** Goi khi watchdog phat hien token khong con ton tai trong DB. */
    private void xuLyPhienHetHan() {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                javafx.scene.control.Alert.AlertType.WARNING);
        alert.setTitle("Het phien dang nhap");
        alert.setHeaderText(null);
        alert.setContentText(
                "Phan quyen cua ban da bi thu hoi hoac phien dang nhap da ket thuc.\nVui long dang nhap lai.");
        alert.showAndWait();
        try {
            java.net.URL fxmlUrl = getClass().getResource("/fxml/hethong/DangNhapView.fxml");
            if (fxmlUrl == null)
                return;
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(fxmlUrl);
            javafx.scene.Parent root = loader.load();
            javafx.scene.Scene scene = new javafx.scene.Scene(root);
            java.net.URL cssUrl = getClass().getResource("/css/bakery.css");
            if (cssUrl != null)
                scene.getStylesheets().add(cssUrl.toExternalForm());
            javafx.stage.Stage stage = (javafx.stage.Stage) lblTenNguoiDung.getScene().getWindow();
            stage.setTitle("H3K Bakery - Dang nhap");
            stage.setScene(scene);
            stage.setResizable(true);
            stage.centerOnScreen();
        } catch (Exception ex) {
            LOGGER.severe("[Watchdog] Loi chuyen ve trang dang nhap: " + ex.getMessage());
        }
    }
}
