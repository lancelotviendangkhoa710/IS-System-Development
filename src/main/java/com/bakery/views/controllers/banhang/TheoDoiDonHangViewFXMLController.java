package com.bakery.views.controllers.banhang;

import com.bakery.model.dto.khachhang.KhachHangDTO;
import com.bakery.model.dto.banhang.CTDonHangDTO;
import com.bakery.model.dto.banhang.CTDonTuyChinhDTO;
import com.bakery.model.dto.kho.CotBanhDTO;
import com.bakery.model.dto.banhang.DonDatHangDTO;
import com.bakery.model.dto.banhang.HoaDonDTO;
import com.bakery.model.dto.kho.KichCoBanhDTO;
import com.bakery.model.dto.kho.KieuTrangTriDTO;
import com.bakery.model.dto.kho.NhanBanhDTO;
import com.bakery.model.dto.kho.SanPhamDTO;
import com.bakery.presenters.banhang.DonHangPresenter;
import com.bakery.services.banhang.DonHangService;
import com.bakery.utils.DialogHelper;
import com.bakery.views.interfaces.banhang.IDonHangView;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;
import java.text.Normalizer;
import java.util.logging.Logger;
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
 * Đã refactor sang kiến trúc AppShell: Loại bỏ điều hướng dư thừa.
 */
public class TheoDoiDonHangViewFXMLController implements IDonHangView, Initializable {

    @FXML
    private TextField txtTimMaDon;
    @FXML
    private TextField txtTimKhachHang;
    @FXML
    private DatePicker dpNgayTheoDoi;
    @FXML
    private ComboBox<String> cbGioTu;
    @FXML
    private ComboBox<String> cbGioDen;
    @FXML
    private ComboBox<String> cbLocTrangThaiTheoDoi;
    @FXML
    private Label lblThongBao;
    @FXML
    private VBox panelChuaDon;

    private static final NumberFormat FMT_TIEN = NumberFormat.getNumberInstance(Locale.of("vi", "VN"));
    private static final DateTimeFormatter FMT_NGAY_GIO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    static {
        FMT_TIEN.setMaximumFractionDigits(0);
    }

    private static final List<String> TRANG_THAI_THEO_THU_TU = List.of(
            "Mới đặt", "Đã cọc", "Đang sản xuất", "Chờ giao", "Chờ khách lấy", "Hoàn thành");

    private static final List<String> TRANG_THAI_KET_THUC = List.of("Hoàn thành", "Hủy", "Hoàn hàng");
    private static final Logger LOGGER = Logger.getLogger(TheoDoiDonHangViewFXMLController.class.getName());
    private Timeline autoRefreshTimeline;

    private DonHangPresenter presenter;
    private final TaoDonHangViewFXMLController dialogFactory = new TaoDonHangViewFXMLController();
    private final List<String> danhSachTrangThai = new ArrayList<>();

    /**
     * true khi view được nhúng trong tab Bếp — tự động tải đơn tùy chỉnh chưa hoàn
     * thành.
     */
    private boolean bepMode = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        khoiTaoComboTheoDoi();

        presenter = new DonHangPresenter(this, new DonHangService());
        presenter.setDialogFactory(dialogFactory);

        panelChuaDon.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                dialogFactory.setOwnerWindow(newScene.getWindow());
                if (bepMode) {
                    // bepMode: tải đơn bếp khi scene sẵn sàng — KHÔNG bật auto-refresh
                    javafx.application.Platform.runLater(this::taiDonBep);
                } else {
                    javafx.application.Platform.runLater(() -> presenter.taiDonLanDau());
                    batDauAutoRefresh();
                }
            } else {
                dungAutoRefresh();
            }
        });

        presenter.taiDuLieuBanDau();
    }

    // ── AUTO-REFRESH ────────────────────────────────────────────────────────

    /**
     * Bật auto-refresh 10s.
     * - bepMode: gọi lại {@link #taiDonBep()} để lấy đơn chưa hoàn thành.
     * - Chế độ thường: gọi lại {@link #onTimKiemDon()} để refresh kết quả tìm kiếm.
     * Phòng chống memory leak: tự stop khi scene bị remove.
     */
    private void batDauAutoRefresh() {
        dungAutoRefresh(); // dọn timer cũ nếu có
        autoRefreshTimeline = new Timeline(new KeyFrame(
                javafx.util.Duration.seconds(10),
                evt -> {
                    try {
                        if (bepMode)
                            taiDonBep();
                        else
                            // Dùng last search để refresh đúng kết quả hiện tại
                            presenter.refreshLastSearch();
                    } catch (Exception ex) {
                        LOGGER.warning("[AutoRefresh] Loi refresh don hang: " + ex.getMessage());
                    }
                }));
        autoRefreshTimeline.setCycleCount(Animation.INDEFINITE);
        autoRefreshTimeline.play();
        LOGGER.info("[AutoRefresh] Bat dau polling 10s — TheoDoiDonHangViewFXMLController (bepMode=" + bepMode + ")");
    }

    /** Dừng timer an toàn. */
    private void dungAutoRefresh() {
        if (autoRefreshTimeline != null) {
            autoRefreshTimeline.stop();
            autoRefreshTimeline = null;
        }
    }

    /** Gọi bởi BepViewFXMLController khi tab Đơn hàng bếp được chọn. */
    public void batDauAutoRefreshPublic() {
        batDauAutoRefresh();
    }

    /** Gọi bởi BepViewFXMLController khi rời khỏi tab Đơn hàng bếp. */
    public void dungAutoRefreshPublic() {
        dungAutoRefresh();
    }

    /**
     * Kích hoạt chế độ Bếp: tự động tải đơn tùy chỉnh chưa hoàn thành.
     * Gọi bởi BepViewFXMLController sau khi fx:include sẵn sàng.
     */
    public void setBepMode(boolean bepMode) {
        this.bepMode = bepMode;
        if (bepMode && panelChuaDon != null) {
            javafx.application.Platform.runLater(this::taiDonBep);
        }
    }

    /** Tải danh sách đơn bánh tùy chỉnh chưa hoàn thành — chế độ Bếp. */
    private void taiDonBep() {
        if (presenter != null) {
            presenter.taiDonBepTuyChinhChuaHoanThanh();
        }
    }

    @FXML
    private void onTimKiemDon() {
        if (presenter == null)
            return;
        String maDonRaw = txtTimMaDon.getText() == null ? "" : txtTimMaDon.getText().trim();
        // Validate mã đơn: nếu nhập thì phải là số nguyên dương
        if (!maDonRaw.isEmpty()) {
            try {
                int ma = Integer.parseInt(maDonRaw);
                if (ma <= 0)
                    throw new NumberFormatException();
            } catch (NumberFormatException e) {
                lblThongBao.getStyleClass().setAll("lbl-danger");
                lblThongBao.setText("Mã đơn không hợp lệ — phải là số nguyên dương.");
                return;
            }
        }
        lblThongBao.getStyleClass().setAll("lbl-small-bold");
        lblThongBao.setText("");
        String tenKH = txtTimKhachHang.getText() == null ? "" : txtTimKhachHang.getText().trim();
        LocalDate ngay = dpNgayTheoDoi.getValue();
        LocalTime gioTu = parseGioTheoDoi(cbGioTu.getValue());
        LocalTime gioDen = parseGioTheoDoi(cbGioDen.getValue());
        presenter.timKiemDonTheoDoi(maDonRaw.isEmpty() ? null : maDonRaw, tenKH, ngay, gioTu, gioDen,
                layTrangThaiFilterTuUI());
    }

    @FXML
    private void onLocTrangThaiTheoDoi() {
        onTimKiemDon();
    }

    private void khoiTaoComboTheoDoi() {
        // DatePicker để TRỐNG — nghĩa là không lọc theo ngày (hiển thị tất cả)
        // User chủ động chọn ngày nếu muốn thu hẹp kết quả
        dpNgayTheoDoi.setValue(null);
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
        if (amount == null)
            return FMT_TIEN.format(0) + " đ";
        return FMT_TIEN.format(amount.doubleValue()) + " đ";
    }

    /**
     * Tạo card hiển thị một đơn hàng trong danh sách theo dõi.
     *
     * Logic chuyển trạng thái:
     * - Thứ tự cố định: Mới đặt → Đã cọc → Đang sản xuất → Chờ giao → Chờ khách lấy
     * → Hoàn thành
     * - Chỉ được tiến (index tăng), không lùi, cho phép nhảy cóc
     * - Nút gần nhất (bước kế tiếp) style btn-primary, các nút nhảy cóc style
     * btn-outline
     * - Đơn đã kết thúc (Hoàn thành / Hủy / Hoàn hàng) không hiển thị nút chuyển
     */
    private Node taoCardTheoDoi(DonDatHangDTO don) {
        VBox card = new VBox(10);
        card.getStyleClass().add("order-card");

        // ── Header: mã đơn + badge trạng thái ──
        HBox header = new HBox(8);
        Label lblMaDon = new Label("#" + don.getMaDon());
        lblMaDon.getStyleClass().add("lbl-body-bold");
        Label badge = new Label(don.getTenTrangThai() == null ? "N/A" : don.getTenTrangThai());
        badge.getStyleClass().addAll("badge", mapStyleTrangThai(don.getTenTrangThai()));
        Region pushRight = new Region();
        HBox.setHgrow(pushRight, Priority.ALWAYS);
        header.getChildren().addAll(lblMaDon, pushRight, badge);

        // ── Info ──
        Label lblKhach = new Label("Khách: " + (don.getMaKH() == null ? "Khách lẻ" : "KH #" + don.getMaKH()));
        lblKhach.getStyleClass().add("lbl-small");
        Label lblNgayNhan = new Label("Nhận lúc: "
                + (don.getNgayGioNhanBanh() == null ? "N/A" : don.getNgayGioNhanBanh().format(FMT_NGAY_GIO)));
        lblNgayNhan.getStyleClass().add("lbl-small");
        Label lblTongTien = new Label("Tổng: " + dinhDangTien(don.getTongTienHDBan()));
        lblTongTien.getStyleClass().add("lbl-primary");

        // ── Actions ──
        HBox action = new HBox(8);
        action.setAlignment(Pos.CENTER_LEFT);
        action.getStyleClass().add("card-actions");

        String ttHienTai = don.getTenTrangThai() == null ? "" : don.getTenTrangThai();
        boolean daKetThuc = TRANG_THAI_KET_THUC.contains(ttHienTai);

        if (!daKetThuc) {
            int viTriHT = TRANG_THAI_THEO_THU_TU.indexOf(ttHienTai);
            // Thêm nút cho mỗi trạng thái cao hơn hiện tại (nhảy cóc được)
            if (viTriHT >= 0) {
                for (int i = viTriHT + 1; i < TRANG_THAI_THEO_THU_TU.size(); i++) {
                    final String ttMuc = TRANG_THAI_THEO_THU_TU.get(i);
                    Button btn = new Button("→ " + ttMuc);
                    // Bước kế tiếp: primary. Nhảy cóc: outline
                    btn.getStyleClass().add(i == viTriHT + 1 ? "btn-primary" : "btn-outline");
                    btn.setOnAction(e -> {
                        if (presenter != null)
                            presenter.capNhatTrangThai(
                                    String.valueOf(don.getMaDon()), ttMuc, ttHienTai);
                    });
                    action.getChildren().add(btn);
                }
            }

            // Hủy đơn — chỉ hiển thị khi chưa kết thúc
            // Nếu đơn đang sản xuất → KHÔNG được hủy (đã cọc nguyên liệu đang chạy)
            if ("Đang sản xuất".equalsIgnoreCase(ttHienTai)) {
                Label lblKhongHuy = new Label("⚠ Đơn đang SX — không thể hủy");
                lblKhongHuy.getStyleClass().add("lbl-small-bold");
                lblKhongHuy.setStyle("-fx-text-fill: #854F0B;");
                action.getChildren().add(lblKhongHuy);
            } else {
                Button btnHuyDon = new Button("Hủy đơn");
                btnHuyDon.getStyleClass().add("btn-danger");
                btnHuyDon.setOnAction(event -> {
                    if (presenter != null)
                        presenter.huyDonHang(String.valueOf(don.getMaDon()));
                });
                action.getChildren().add(btnHuyDon);
            }
        }

        // Chi tiết — luôn hiển thị
        Button btnChiTiet = new Button("Chi tiết");
        btnChiTiet.getStyleClass().add("btn-secondary");
        btnChiTiet.setOnAction(event -> showOrderDetails(don));
        action.getChildren().add(btnChiTiet);

        card.getChildren().addAll(header, lblKhach, lblNgayNhan, lblTongTien, action);
        return card;
    }

    private String mapStyleTrangThai(String trangThai) {
        if (trangThai == null)
            return "badge-new";
        String normalized = Normalizer.normalize(trangThai, Normalizer.Form.NFD).replaceAll("\\p{M}", "")
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
        // Tiêu đề header
        String header = "Chi tiết đơn #" + order.getMaDon()
                + "  —  " + order.getTenTrangThai()
                + "  |  Khách: " + (order.getMaKH() == null ? "Khách lẻ" : "KH #" + order.getMaKH())
                + "  |  Nhận: "
                + (order.getNgayGioNhanBanh() == null ? "N/A" : order.getNgayGioNhanBanh().format(FMT_NGAY_GIO))
                + "  |  Tổng: " + dinhDangTien(order.getTongTienHDBan())
                + "  —  Đã cọc: " + dinhDangTien(order.getTienDaCoc());

        // Lấy chi tiết từ presenter
        List<CTDonHangDTO> banSan = new ArrayList<>();
        List<CTDonTuyChinhDTO> tuyChinh = new ArrayList<>();
        if (presenter != null) {
            try {
                banSan = presenter.layChiTietBanSan(order.getMaDon());
            } catch (Exception ignored) {}
            try {
                tuyChinh = presenter.layChiTietTuyChinh(order.getMaDon());
            } catch (Exception ignored) {}
        }

        // ─── Bảng bánh bán sẵn ─────────────────────────────────────
        TableView<CTDonHangDTO> tblBanSan = new TableView<>();
        TableColumn<CTDonHangDTO, String> colTenSP = new TableColumn<>("Sản phẩm");
        colTenSP.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty("SP #" + c.getValue().getMaSP()));
        colTenSP.setPrefWidth(180);
        TableColumn<CTDonHangDTO, Integer> colSL = new TableColumn<>("Số lượng");
        colSL.setCellValueFactory(c -> new javafx.beans.property.SimpleObjectProperty<>(c.getValue().getSoLuong()));
        colSL.setPrefWidth(80);
        TableColumn<CTDonHangDTO, String> colGia = new TableColumn<>("Đơn giá");
        colGia.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(dinhDangTien(c.getValue().getDonGia())));
        colGia.setPrefWidth(110);
        TableColumn<CTDonHangDTO, String> colTT = new TableColumn<>("Thành tiền");
        colTT.setCellValueFactory(c -> {
            double tt = (c.getValue().getDonGia() != null ? c.getValue().getDonGia().doubleValue() : 0) * c.getValue().getSoLuong();
            return new javafx.beans.property.SimpleStringProperty(dinhDangTien(tt));
        });
        colTT.setPrefWidth(120);
        tblBanSan.getColumns().addAll(colTenSP, colSL, colGia, colTT);
        tblBanSan.setItems(FXCollections.observableArrayList(banSan));
        tblBanSan.setMaxHeight(150);
        tblBanSan.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        if (banSan.isEmpty()) tblBanSan.setPlaceholder(new Label("Không có sản phẩm bán sẵn"));

        // ─── Bảng bánh tùy chỉnh ──────────────────────────────────────
        TableView<CTDonTuyChinhDTO> tblTuyChinh = new TableView<>();
        TableColumn<CTDonTuyChinhDTO, String> colTcTen = new TableColumn<>("Bánh tùy chỉnh (SP#)");
        colTcTen.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty("SP #" + c.getValue().getMaSP()));
        colTcTen.setPrefWidth(160);
        TableColumn<CTDonTuyChinhDTO, Integer> colTcSL = new TableColumn<>("SL");
        colTcSL.setCellValueFactory(c -> new javafx.beans.property.SimpleObjectProperty<>(c.getValue().getSoLuong()));
        colTcSL.setPrefWidth(50);
        TableColumn<CTDonTuyChinhDTO, String> colTcGia = new TableColumn<>("Đơn giá");
        colTcGia.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(dinhDangTien(c.getValue().getDonGia())));
        colTcGia.setPrefWidth(110);
        TableColumn<CTDonTuyChinhDTO, String> colTcLoiChuc = new TableColumn<>("Lời chúc");
        colTcLoiChuc.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
                nvl(c.getValue().getLoiChucTrenBanh())));
        colTcLoiChuc.setPrefWidth(140);
        TableColumn<CTDonTuyChinhDTO, String> colTcGhiChu = new TableColumn<>("Ghi chú");
        colTcGhiChu.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
                nvl(c.getValue().getGhiChuThoBanh())));
        colTcGhiChu.setPrefWidth(150);
        tblTuyChinh.getColumns().addAll(colTcTen, colTcSL, colTcGia, colTcLoiChuc, colTcGhiChu);
        tblTuyChinh.setItems(FXCollections.observableArrayList(tuyChinh));
        tblTuyChinh.setMaxHeight(150);
        tblTuyChinh.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        if (tuyChinh.isEmpty()) tblTuyChinh.setPlaceholder(new Label("Không có bánh tùy chỉnh"));

        // ─── Layout dialog ────────────────────────────────────────────
        VBox root = new VBox(10);
        root.setPadding(new Insets(16));
        root.getStyleClass().add("bg-app");

        Label lblHeader = new Label(header);
        lblHeader.getStyleClass().add("lbl-body-bold");
        lblHeader.setWrapText(true);

        Label lblBanSan = new Label("🛒 Bánh bán sẵn");
        lblBanSan.getStyleClass().add("lbl-title-card");
        Label lblTuyChinh = new Label("✨ Bánh tùy chỉnh");
        lblTuyChinh.getStyleClass().add("lbl-title-card");

        Button btnDong = new Button("❌ Đóng");
        btnDong.getStyleClass().add("btn-secondary");

        HBox footer = new HBox(btnDong);
        footer.setAlignment(Pos.CENTER_RIGHT);

        root.getChildren().addAll(lblHeader, new Separator(),
                lblBanSan, tblBanSan,
                lblTuyChinh, tblTuyChinh,
                new Separator(), footer);

        Scene scene = new Scene(root, 680, 520);
        URL cssUrl = getClass().getResource("/css/bakery.css");
        if (cssUrl != null) scene.getStylesheets().add(cssUrl.toExternalForm());

        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Chi tiết đơn #" + order.getMaDon());
        stage.setScene(scene);
        btnDong.setOnAction(e -> stage.close());
        stage.showAndWait();
    }

    private static String nvl(String s) { return s != null && !s.isBlank() ? s : "—"; }

    @Override
    public void hienThiLoiTraCuu(String msg) {
        lblThongBao.getStyleClass().setAll("lbl-danger");
        lblThongBao.setText(msg);
    }

    @Override
    public void hienThiThongBaoTraCuu(String msg) {
        lblThongBao.getStyleClass().setAll("lbl-success");
        lblThongBao.setText(msg);
    }

    @Override
    public void hienThiKetQuaTraCuu(String kh, String tt, double tongTien) {
        lblThongBao.getStyleClass().setAll("lbl-info");
        lblThongBao.setText("KQ: " + kh + " | " + tt + " | " + dinhDangTien(tongTien));
    }

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
    public void inPhieuHoaDon(String tieuDe, HoaDonDTO hd, DonDatHangDTO don, List<CTDonHangDTO> cart,
            List<SanPhamDTO> data, double pGiam, double khachDua, double tienThua, boolean laDonCoc) {
    }

    @Override
    public void inHoaDonHoanThanh(DonDatHangDTO don, HoaDonDTO hd, List<CTDonHangDTO> dsItems, double khachDua,
            double tienThua, boolean laDonCoc) {
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
