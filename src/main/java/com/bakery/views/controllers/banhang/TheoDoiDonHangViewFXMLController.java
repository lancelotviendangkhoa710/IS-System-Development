package com.bakery.views.controllers.banhang;
import com.bakery.model.dto.khachhang.KhachHangDTO;

import com.bakery.model.dto.banhang.CTDonHangDTO;
import com.bakery.model.dto.kho.CotBanhDTO;
import com.bakery.model.dto.banhang.DonDatHangDTO;
import com.bakery.model.dto.banhang.HoaDonDTO;
import com.bakery.model.dto.kho.KichCoBanhDTO;
import com.bakery.model.dto.kho.KieuTrangTriDTO;
import com.bakery.model.dto.kho.NhanBanhDTO;
import com.bakery.model.dto.kho.SanPhamDTO;
import com.bakery.presenters.banhang.DonHangPresenter;
import com.bakery.services.banhang.DonHangService;
import com.bakery.utils.UserSession;
import com.bakery.views.controllers.hethong.MainMenuViewFXMLController;
import com.bakery.views.interfaces.banhang.IDonHangView;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.text.Normalizer;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Controller cho màn hình Theo dõi đơn hàng (Order Tracking).
 * Implement IOrderView để nhận thông báo từ DonHangPresenter.
 */
public class TheoDoiDonHangViewFXMLController implements IDonHangView, Initializable {

    @FXML
    private TextField txtTimMaDon;
    @FXML
    private DatePicker dpNgayTheoDoi;
    @FXML
    private ComboBox<String> cbGioTu;
    @FXML
    private ComboBox<String> cbGioDen;
    @FXML
    private ComboBox<String> cbLocTrangThaiTheoDoi;
    @FXML
    private Label lblThongBaoTab2;
    @FXML
    private VBox panelChuaDon;

    private static final NumberFormat FMT_TIEN = NumberFormat.getNumberInstance(Locale.of("vi", "VN"));
    private static final DateTimeFormatter FMT_NGAY_GIO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    static {
        FMT_TIEN.setMaximumFractionDigits(0);
    }

    private DonHangPresenter presenter;
    private final TaoDonHangViewFXMLController dialogFactory = new TaoDonHangViewFXMLController();
    private final List<String> danhSachTrangThai = new ArrayList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        khoiTaoComboTheoDoi();

        presenter = new DonHangPresenter(this, new DonHangService());
        presenter.setDialogFactory(dialogFactory);

        // Chờ scene được set để gán owner window cho dialogFactory
        panelChuaDon.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                dialogFactory.setOwnerWindow(newScene.getWindow());
            }
        });

        presenter.taiDuLieuBanDau();
    }

    @FXML
    private void onBackToMenu() {
        try {
            com.bakery.services.nhansu.PhanQuyenService phanQuyenService = new com.bakery.services.nhansu.PhanQuyenService();
            String fxmlPath = phanQuyenService.layManHinhTrangChu(UserSession.getCurrentUser());
            URL fxmlUrl = getClass().getResource(fxmlPath);
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Scene scene = new Scene(loader.load(), 1280, 720);

            Object controller = loader.getController();
            if (controller instanceof MainMenuViewFXMLController) {
                ((MainMenuViewFXMLController) controller).khoiTaoThongTinDangNhap(UserSession.getCurrentUser());
            } else if (controller instanceof ThuNganViewFXMLController) {
                ((ThuNganViewFXMLController) controller).khoiTaoDashboard(UserSession.getCurrentUser());
            } else if (controller instanceof com.bakery.views.controllers.hethong.ThoBepDashboardViewFXMLController) {
                ((com.bakery.views.controllers.hethong.ThoBepDashboardViewFXMLController) controller).khoiTaoDashboard(UserSession.getCurrentUser());
            }

            URL cssUrl = getClass().getResource("/css/bakery.css");
            if (cssUrl != null)
                scene.getStylesheets().add(cssUrl.toExternalForm());

            Stage stage = (Stage) panelChuaDon.getScene().getWindow();
            stage.setTitle(phanQuyenService.layTieuDeTrangChu(UserSession.getCurrentUser()));
            stage.setScene(scene);
            stage.centerOnScreen();
        } catch (Exception ex) {
            System.err.println("[TheoDoiDon] Lỗi quay lại Menu: " + ex.getMessage());
        }
    }

    @FXML
    private void onTimKiemDon() {
        if (presenter == null)
            return;
        String maDon = txtTimMaDon.getText() == null ? "" : txtTimMaDon.getText().trim();
        LocalDate ngay = dpNgayTheoDoi.getValue() != null ? dpNgayTheoDoi.getValue() : LocalDate.now();
        LocalTime gioTu = parseGioTheoDoi(cbGioTu.getValue());
        LocalTime gioDen = parseGioTheoDoi(cbGioDen.getValue());
        presenter.timKiemDonTheoDoi(maDon, ngay, gioTu, gioDen, layTrangThaiFilterTuUI());
    }

    @FXML
    private void onLocTrangThaiTheoDoi() {
        onTimKiemDon();
    }

    private void khoiTaoComboTheoDoi() {
        dpNgayTheoDoi.setValue(LocalDate.now());
        cbGioTu.getItems().clear();
        cbGioDen.getItems().clear();
        cbGioTu.getItems().add("Tất cả");
        cbGioDen.getItems().add("Tất cả");
        for (int h = 0; h < 24; h++) {
            cbGioTu.getItems().add(String.format("%02d:00", h));
            cbGioDen.getItems().add(String.format("%02d:00", h));
            if (h < 23) {
                cbGioTu.getItems().add(String.format("%02d:30", h));
                cbGioDen.getItems().add(String.format("%02d:30", h));
            }
        }
        cbGioTu.setValue("Tất cả");
        cbGioDen.setValue("Tất cả");

        cbLocTrangThaiTheoDoi.getItems().clear();
        cbLocTrangThaiTheoDoi.getItems().add("Tất cả");
        cbLocTrangThaiTheoDoi.getItems().add("Chưa hoàn thành");
        cbLocTrangThaiTheoDoi.getItems().add("Hoàn thành");
        cbLocTrangThaiTheoDoi.setValue("Chưa hoàn thành");
    }

    private String layTrangThaiFilterTuUI() {
        String filter = cbLocTrangThaiTheoDoi == null ? null : cbLocTrangThaiTheoDoi.getValue();
        if (filter == null || filter.isBlank() || "Tất cả".equalsIgnoreCase(filter)) {
            return "ALL";
        }
        if ("Hoàn thành".equalsIgnoreCase(filter)) {
            return "COMPLETED";
        }
        return "NOT_COMPLETED";
    }

    private LocalTime parseGioTheoDoi(String value) {
        if (value == null || value.isBlank() || "Tất cả".equalsIgnoreCase(value)) {
            return null;
        }
        return LocalTime.parse(value);
    }

    private String dinhDangTien(double amount) {
        return FMT_TIEN.format(amount) + " đ";
    }

    private String dinhDangTien(java.math.BigDecimal amount) {
        if (amount == null) return FMT_TIEN.format(0) + " đ";
        return FMT_TIEN.format(amount.doubleValue()) + " đ";
    }

    private Node taoCardTheoDoi(DonDatHangDTO don) {
        VBox card = new VBox(10);
        card.getStyleClass().add("order-card");

        HBox header = new HBox(8);
        Label lblMaDon = new Label("#" + don.getMaDon());
        lblMaDon.getStyleClass().add("lbl-body-bold");

        Label badge = new Label(don.getTenTrangThai() == null ? "N/A" : don.getTenTrangThai());
        badge.getStyleClass().addAll("badge", mapStyleTrangThai(don.getTenTrangThai()));

        Region pushRight = new Region();
        HBox.setHgrow(pushRight, Priority.ALWAYS);
        header.getChildren().addAll(lblMaDon, pushRight, badge);

        Label lblKhach = new Label("Khách: " + (don.getMaKH() == null ? "Khách lẻ" : "KH #" + don.getMaKH()));
        lblKhach.getStyleClass().add("lbl-small");
        Label lblNgayNhan = new Label("Nhận lúc: " + (don.getNgayGioNhanBanh() == null
                ? "N/A"
                : don.getNgayGioNhanBanh().format(FMT_NGAY_GIO)));
        lblNgayNhan.getStyleClass().add("lbl-small");
        Label lblTongTien = new Label("Tổng: " + dinhDangTien(don.getTongTienHDBan()));
        lblTongTien.getStyleClass().add("lbl-primary");

        HBox action = new HBox(8);
        action.setAlignment(Pos.CENTER_LEFT);
        ComboBox<String> cbTrangThai = new ComboBox<>();
        cbTrangThai.setItems(FXCollections.observableArrayList(danhSachTrangThai));
        cbTrangThai.setValue(don.getTenTrangThai());
        cbTrangThai.setPrefWidth(180);

        Button btnCapNhat = new Button("Cập nhật");
        btnCapNhat.getStyleClass().add("btn-primary");
        btnCapNhat.setOnAction(event -> {
            if (presenter == null || cbTrangThai.getValue() == null)
                return;
            presenter.capNhatTrangThai(
                    String.valueOf(don.getMaDon()),
                    cbTrangThai.getValue(),
                    don.getTenTrangThai());
        });

        Button btnChiTiet = new Button("Chi tiết");
        btnChiTiet.getStyleClass().add("btn-secondary");
        btnChiTiet.setOnAction(event -> showOrderDetails(don));

        Button btnHuyDon = new Button("Hủy đơn");
        btnHuyDon.getStyleClass().add("btn-danger");
        // Chỉ cho phép hủy nếu chưa hoàn thành và chưa hủy
        String tt = don.getTenTrangThai() == null ? "" : don.getTenTrangThai();
        if (tt.contains("Hoàn thành") || tt.contains("Hủy")) {
            btnHuyDon.setDisable(true);
        }
        btnHuyDon.setOnAction(event -> {
            if (presenter != null) {
                presenter.huyDonHang(String.valueOf(don.getMaDon()));
            }
        });

        action.getChildren().addAll(cbTrangThai, btnCapNhat, btnChiTiet, btnHuyDon);
        card.getChildren().addAll(header, lblKhach, lblNgayNhan, lblTongTien, action);
        return card;
    }

    private String mapStyleTrangThai(String trangThai) {
        if (trangThai == null)
            return "badge-new";
        String normalized = Normalizer.normalize(trangThai, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .replace("đ", "d")
                .replace("Đ", "D")
                .toUpperCase(Locale.ROOT);
        if (normalized.contains("HOAN THANH"))
            return "badge-done";
        if (normalized.contains("HUY"))
            return "badge-cancelled";
        if (normalized.contains("DANG SAN XUAT"))
            return "badge-processing";
        if (normalized.contains("CHO GIAO"))
            return "badge-shipping";
        if (normalized.contains("CHO KHACH LAY"))
            return "badge-pickup";
        if (normalized.contains("DA COC"))
            return "badge-deposited";
        return "badge-new";
    }

    // --- IOrderView implementation (Theo doi don methods) ---
    @Override
    public void taiDanhSachTrangThai(List<String> list) {
        danhSachTrangThai.clear();
        danhSachTrangThai.addAll(list);
    }

    @Override
    public void hienThiDanhSachDonTheoDoi(List<DonDatHangDTO> dsDonTheoDoi) {
        panelChuaDon.getChildren().clear();
        for (DonDatHangDTO don : dsDonTheoDoi) {
            panelChuaDon.getChildren().add(taoCardTheoDoi(don));
        }
    }

    @Override
    public void showOrderDetails(DonDatHangDTO order) {
        String noiDung = "Mã đơn: #" + order.getMaDon() + "\n"
                + "Trạng thái: " + order.getTenTrangThai() + "\n"
                + "Khách hàng: " + (order.getMaKH() == null ? "Khách lẻ" : "KH #" + order.getMaKH()) + "\n"
                + "Thời gian nhận: " + (order.getNgayGioNhanBanh() == null
                        ? "N/A"
                        : order.getNgayGioNhanBanh().format(FMT_NGAY_GIO))
                + "\n"
                + "Tổng tiền: " + dinhDangTien(order.getTongTienHDBan()) + "\n"
                + "Đã cọc: " + dinhDangTien(order.getTienDaCoc());
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("Chi tiết đơn hàng");
        alert.setContentText(noiDung);
        alert.showAndWait();
    }

    @Override
    public void hienThiLoiTraCuu(String msg) {
        lblThongBaoTab2.getStyleClass().setAll("lbl-danger");
        lblThongBaoTab2.setText(msg);
    }

    @Override
    public void hienThiThongBaoTraCuu(String msg) {
        lblThongBaoTab2.getStyleClass().setAll("lbl-success");
        lblThongBaoTab2.setText(msg);
    }

    @Override
    public void hienThiKetQuaTraCuu(String kh, String tt, double tongTien) {
        lblThongBaoTab2.getStyleClass().setAll("lbl-info");
        lblThongBaoTab2.setText("KQ: " + kh + " | " + tt + " | " + dinhDangTien(tongTien));
    }

    // --- IOrderView implementation (No-ops for POS methods) ---
    @Override
    public void lamMoiBangGioHang(List<CTDonHangDTO> items, List<SanPhamDTO> originData) {
    }

    @Override
    public void lamMoiBaoCaoTien(double tongHang, double giamGia, double tongThanhToan, double minCoc, double conLai,
            double tienThua, boolean isThieuTienThua) {
    }

    @Override
    public void batTatNutThanhToan(boolean state) {
    }

    @Override
    public void hienThiThongTinKhach(String text, boolean isVip) {
    }

    @Override
    public void capNhatKhachHangHienTai(KhachHangDTO kh) {
    }

    @Override
    public void hienThiDanhSachSanPham(List<SanPhamDTO> ds, Map<Integer, String> dict) {
    }

    @Override
    public void hienThiDuLieuTuyChinh(List<SanPhamDTO> spTuyChinh, List<KichCoBanhDTO> kichCo, List<CotBanhDTO> cotBanh,
            List<NhanBanhDTO> nhanBanh, List<KieuTrangTriDTO> trangTri) {
    }

    @Override
    public void hienThiLoi(String msg) {
    }

    @Override
    public void hienThiThanhCong(String msg) {
    }

    @Override
    public void lamMoiForm() {
    }

    @Override
    public void inPhieuHoaDon(String tieuDe, HoaDonDTO hd, DonDatHangDTO don,
            List<CTDonHangDTO> cart, List<SanPhamDTO> data, double pGiam,
            double khachDua, double tienThua, boolean laDonCoc) {
        // No-op for Tracking view as of now, or implement if needed
    }


    @Override
    public void inHoaDonHoanThanh(DonDatHangDTO don, HoaDonDTO hd, List<CTDonHangDTO> dsItems,
            double khachDua, double tienThua, boolean laDonCoc) {
    }

    @Override
    public LocalDateTime getNgayGioNhanBanh() {
        return LocalDateTime.now();
    }

    @Override
    public double getTongThanhToanHienTai() {
        return 0;
    }
}
