package com.bakery.views.controllers.taichinh;

import com.bakery.model.dto.hethong.LoaiThuChiDTO;
import com.bakery.model.dto.hethong.PhieuThuChiDTO;
import com.bakery.services.hethong.SoQuyService;
import com.bakery.utils.CurrencyFormatter;
import com.bakery.utils.DialogHelper;
import com.bakery.utils.SessionContext;
import com.bakery.views.controllers.BaseController;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class SoQuyViewFXMLController extends BaseController {

    // ── Header ──────────────────────────────────────────────────────────
    @FXML
    private Button btnLapPhieu;

    // ── Tab Phiếu Thu Chi ────────────────────────────────────────────────
    @FXML
    private TableView<PhieuThuChiDTO> tblPhieu;
    @FXML
    private TableColumn<PhieuThuChiDTO, String> colMaPhieu;
    @FXML
    private TableColumn<PhieuThuChiDTO, String> colNgayTao;
    @FXML
    private TableColumn<PhieuThuChiDTO, String> colPhanLoai;
    @FXML
    private TableColumn<PhieuThuChiDTO, String> colHangMuc;
    @FXML
    private TableColumn<PhieuThuChiDTO, String> colSoTien;
    @FXML
    private TableColumn<PhieuThuChiDTO, String> colNguoiLap;
    @FXML
    private TableColumn<PhieuThuChiDTO, String> colGhiChu;
    @FXML
    private TableColumn<PhieuThuChiDTO, String> colTrangThai;
    @FXML
    private ComboBox<String> cmbLocPhanLoai;
    @FXML
    private ComboBox<String> cmbLocCa;
    @FXML
    private Label lblTongThu;
    @FXML
    private Label lblTongChi;
    @FXML
    private Label lblSoDu;

    // ── Tab Loại Thu Chi ─────────────────────────────────────────────────
    @FXML
    private TableView<LoaiThuChiDTO> tblLoai;
    @FXML
    private TableColumn<LoaiThuChiDTO, String> colLoaiMa;
    @FXML
    private TableColumn<LoaiThuChiDTO, String> colLoaiTen;
    @FXML
    private TableColumn<LoaiThuChiDTO, String> colLoaiPL;
    @FXML
    private TableColumn<LoaiThuChiDTO, String> colLoaiAction;

    // ── State ────────────────────────────────────────────────────────────
    private final SoQuyService soQuySvc = new SoQuyService();

    private final ObservableList<PhieuThuChiDTO> danhSachPhieu = FXCollections.observableArrayList();
    private final ObservableList<LoaiThuChiDTO> danhSachLoai = FXCollections.observableArrayList();

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final NumberFormat VND = NumberFormat.getInstance(new Locale("vi", "VN"));

    // ── Khởi tạo ────────────────────────────────────────────────────────

    @FXML
    public void initialize() {
        caiDatBangPhieu();
        caiDatBangLoai();
        caiDatBoCLoc();
        taiDuLieu();
        // Auto-refresh: mỗi 10s tự query DB — khi thu ngân xuất hóa đơn, phiếu thu tự hiện lên
        batDauAutoRefresh(tblPhieu, this::taiDuLieu, 10);
    }

    // ── Setup bảng phiếu ─────────────────────────────────────────────────

    private void caiDatBangPhieu() {
        colMaPhieu.setCellValueFactory(c -> new SimpleStringProperty("#" + c.getValue().getMaPhieuTC()));
        colNgayTao.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getNgayTao() != null ? c.getValue().getNgayTao().format(FMT) : "—"));
        colPhanLoai.setCellValueFactory(c -> new SimpleStringProperty(nvl(c.getValue().getPhanLoai())));
        colHangMuc.setCellValueFactory(c -> new SimpleStringProperty(nvl(c.getValue().getTenLoaiThuChi())));
        colSoTien.setCellValueFactory(c -> {
            BigDecimal st = c.getValue().getSoTien();
            return new SimpleStringProperty(st != null ? VND.format(st) + " ₫" : "—");
        });
        colNguoiLap.setCellValueFactory(c -> new SimpleStringProperty(nvl(c.getValue().getTenNhanVien())));
        colGhiChu.setCellValueFactory(c -> new SimpleStringProperty(nvl(c.getValue().getGhiChu())));
        colTrangThai.setCellValueFactory(
                c -> new SimpleStringProperty("cancelled".equals(c.getValue().getTrangThai()) ? "Đã hủy" : "Hợp lệ"));

        colTrangThai.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    getStyleClass().removeAll("badge-done", "badge-cancelled");
                    return;
                }
                setText(item);
                getStyleClass().removeAll("badge-done", "badge-cancelled");
                getStyleClass().add("Đã hủy".equals(item) ? "badge-cancelled" : "badge-done");
            }
        });

        tblPhieu.setItems(danhSachPhieu);
        tblPhieu.setPlaceholder(new Label("Chưa có phiếu thu chi nào."));

        // Click chọn hàng để có thể hủy
        tblPhieu.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2)
                onHuyPhieu();
        });
    }

    // ── Setup bảng loại thu chi ──────────────────────────────────────────

    private void caiDatBangLoai() {
        colLoaiMa.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getMaLoaiThuChi())));
        colLoaiTen.setCellValueFactory(c -> new SimpleStringProperty(nvl(c.getValue().getTenLoaiThuChi())));
        colLoaiPL.setCellValueFactory(c -> new SimpleStringProperty(nvl(c.getValue().getPhanLoai())));

        colLoaiAction.setCellFactory(tc -> new TableCell<>() {
            private final Button btnSua = new Button("Sửa");
            private final Button btnXoa = new Button("Xóa");
            private final HBox box = new HBox(8, btnSua, btnXoa);

            {
                btnSua.getStyleClass().add("btn-secondary");
                btnXoa.getStyleClass().add("btn-danger");
                btnSua.setOnAction(e -> moDialogSuaLoai(getTableView().getItems().get(getIndex())));
                btnXoa.setOnAction(e -> xacNhanXoaLoai(getTableView().getItems().get(getIndex())));
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });

        tblLoai.setItems(danhSachLoai);
        tblLoai.setPlaceholder(new Label("Chưa có hạng mục nào."));
    }

    // ── Setup bộ lọc ─────────────────────────────────────────────────────

    private void caiDatBoCLoc() {
        cmbLocPhanLoai.setItems(FXCollections.observableArrayList("Tất cả", "Thu", "Chi"));
        cmbLocPhanLoai.getSelectionModel().selectFirst();
        cmbLocPhanLoai.setOnAction(e -> apDungLocPhanLoai());

        cmbLocCa.setItems(FXCollections.observableArrayList("Ca hiện tại", "Tất cả"));
        cmbLocCa.getSelectionModel().selectFirst();
        cmbLocCa.setOnAction(e -> taiDuLieu());
    }

    // ── Tải dữ liệu ──────────────────────────────────────────────────────

    private void taiDuLieu() {
        Thread t = new Thread(() -> {
            try {
                boolean tatCaCa = "Tất cả".equals(cmbLocCa.getValue());
                List<PhieuThuChiDTO> ds = tatCaCa
                        ? soQuySvc.layTatCaGiaoDich()
                        : soQuySvc.layGiaoDich(SessionContext.getInstance().getMaCa());

                List<LoaiThuChiDTO> dsLoai = soQuySvc.layDanhSachLoai();

                javafx.application.Platform.runLater(() -> {
                    danhSachPhieu.setAll(ds);
                    danhSachLoai.setAll(dsLoai);
                    apDungLocPhanLoai();
                });
            } catch (Exception e) {
                javafx.application.Platform.runLater(() -> hienThiLoiLabel("Lỗi tải dữ liệu: " + e.getMessage()));
            }
        }, "so-quy-tai");
        t.setDaemon(true);
        t.start();
    }

    private void apDungLocPhanLoai() {
        String loc = cmbLocPhanLoai.getValue();
        ObservableList<PhieuThuChiDTO> hienThi;
        if (loc == null || "Tất cả".equals(loc)) {
            hienThi = danhSachPhieu;
        } else {
            hienThi = danhSachPhieu.filtered(dto -> loc.equals(dto.getPhanLoai()));
        }
        tblPhieu.setItems(hienThi);
        capNhatTongKet(hienThi);
    }

    private void capNhatTongKet(List<? extends PhieuThuChiDTO> ds) {
        BigDecimal tongThu = soQuySvc.tinhTongThu((List<PhieuThuChiDTO>) ds);
        BigDecimal tongChi = soQuySvc.tinhTongChi((List<PhieuThuChiDTO>) ds);
        BigDecimal soDu = tongThu.subtract(tongChi);

        lblTongThu.setText(VND.format(tongThu) + " ₫");
        lblTongChi.setText(VND.format(tongChi) + " ₫");
        lblSoDu.setText(VND.format(soDu) + " ₫");

        lblSoDu.getStyleClass().removeAll("lbl-danger", "lbl-success");
        lblSoDu.getStyleClass().add(soDu.compareTo(BigDecimal.ZERO) < 0 ? "lbl-danger" : "lbl-success");
    }

    // ── Handlers ─────────────────────────────────────────────────────────

    @FXML
    private void onQuayLai() {
        quayLaiMenuChinh(btnLapPhieu);
    }

    @FXML
    private void onLamMoi() {
        taiDuLieu();
    }

    /**
     * Mở dialog lập phiếu CHI thủ công.
     * Phiếu THU từ bán hàng được auto-tạo khi xuất hóa đơn.
     */
    @FXML
    private void onLapPhieu() {
        // Chỉ hiện loại "Chi" — Thu từ bán hàng là auto
        ObservableList<LoaiThuChiDTO> loaiChi = danhSachLoai.filtered(
                l -> "Chi".equals(l.getPhanLoai()));
        if (loaiChi.isEmpty()) {
            hienThiLoiLabel("Chưa có hạng mục chi. Vui lòng thêm hạng mục ở tab 'Hạng Mục'.");
            return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Lập Phiếu Chi");
        dialog.setHeaderText("Ghi nhận khoản chi thủ công\n(Thu từ bán hàng tự động ghi khi xuất hóa đơn)");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.getDialogPane().setPrefWidth(480);

        ComboBox<LoaiThuChiDTO> cmbLoai = new ComboBox<>(loaiChi);
        cmbLoai.setPromptText("— Chọn hạng mục chi —");
        cmbLoai.setMaxWidth(Double.MAX_VALUE);
        cmbLoai.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(LoaiThuChiDTO item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getTenLoaiThuChi());
            }
        });
        cmbLoai.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(LoaiThuChiDTO item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getTenLoaiThuChi());
            }
        });
        cmbLoai.getSelectionModel().selectFirst();

        TextField txtSoTien = new TextField();
        txtSoTien.setPromptText("Nhập số tiền (VNĐ)");
        txtSoTien.setPrefWidth(220);
        // Tự động chèn dấu chấm phân cách mỗi 3 chữ số khi user gõ
        CurrencyFormatter.apDungDinhDangNhapTien(txtSoTien);

        // Label đọc số tiền bằng chữ tiếng Việt (cập nhật real-time)
        Label lblDocChu = new Label("Nhập số tiền để xem bằng chữ...");
        lblDocChu.setStyle("-fx-font-style: italic; -fx-text-fill: #555; -fx-font-size: 11px;");
        lblDocChu.setWrapText(true);
        lblDocChu.setMaxWidth(280);
        txtSoTien.textProperty().addListener((obs, oldVal, newVal) -> {
            BigDecimal parsed = CurrencyFormatter.parse(newVal);
            if (parsed.compareTo(BigDecimal.ZERO) > 0) {
                lblDocChu.setText(CurrencyFormatter.docSoTien(parsed));
            } else {
                lblDocChu.setText("Nhập số tiền để xem bằng chữ...");
            }
        });

        TextField txtGhiChu = new TextField();
        txtGhiChu.setPromptText("Ghi chú (tùy chọn)");

        dialog.getDialogPane().setContent(buildGrid(
                "Hạng mục:", cmbLoai,
                "Số tiền:", txtSoTien,
                "Bằng chữ:", lblDocChu,
                "Ghi chú:", txtGhiChu));

        dialog.showAndWait().ifPresent(r -> {
            if (r != ButtonType.OK)
                return;
            LoaiThuChiDTO loai = cmbLoai.getValue();
            if (loai == null) {
                hienThiLoiLabel("Vui lòng chọn hạng mục.");
                return;
            }
            BigDecimal soTien = parseSoTien(txtSoTien.getText());
            if (soTien == null)
                return;

            int maNV = SessionContext.getInstance().getMaNV();
            int maCa = SessionContext.getInstance().getMaCa();
            String gh = txtGhiChu.getText().trim();

            runAsync(() -> soQuySvc.themGiaoDich(maCa, maNV, loai.getMaLoaiThuChi(), soTien, gh),
                    "Đã lập phiếu chi: " + loai.getTenLoaiThuChi(), "Lỗi lập phiếu");
        });
    }

    /** Double-click hàng → hỏi hủy. */
    private void onHuyPhieu() {
        PhieuThuChiDTO sel = tblPhieu.getSelectionModel().getSelectedItem();
        if (sel == null)
            return;
        if ("cancelled".equals(sel.getTrangThai())) {
            hienThiLoiLabel("Phiếu này đã bị hủy.");
            return;
        }

        Alert c = new Alert(Alert.AlertType.CONFIRMATION,
                "Hủy phiếu #" + sel.getMaPhieuTC() + "? Thao tác không thể hoàn tác.",
                ButtonType.OK, ButtonType.CANCEL);
        c.setTitle("Xác nhận hủy");
        DialogHelper.applyBakeryTheme(c);
        c.showAndWait().filter(r -> r == ButtonType.OK)
                .ifPresent(r -> runAsync(() -> soQuySvc.huyGiaoDich(sel.getMaPhieuTC(), "Hủy thủ công"),
                        "Đã hủy phiếu #" + sel.getMaPhieuTC(), "Lỗi hủy phiếu"));
    }

    // ── Hạng mục ─────────────────────────────────────────────────────────

    @FXML
    private void onThemLoai() {
        Dialog<ButtonType> d = buildDialogLoai("Thêm Hạng Mục", null);
        d.showAndWait().ifPresent(r -> {
            if (r != ButtonType.OK)
                return;
            TextField txtTen = (TextField) lookupId(d, "txtTen");
            ComboBox<?> cmbPL = (ComboBox<?>) lookupId(d, "cmbPL");
            runAsync(() -> soQuySvc.themLoai(txtTen.getText().trim(), (String) cmbPL.getValue()),
                    "Đã thêm hạng mục.", "Lỗi thêm hạng mục");
        });
    }

    private void moDialogSuaLoai(LoaiThuChiDTO dto) {
        Dialog<ButtonType> d = buildDialogLoai("Sửa Hạng Mục", dto);
        d.showAndWait().ifPresent(r -> {
            if (r != ButtonType.OK)
                return;
            TextField txtTen = (TextField) lookupId(d, "txtTen");
            ComboBox<?> cmbPL = (ComboBox<?>) lookupId(d, "cmbPL");
            runAsync(() -> soQuySvc.suaLoai(dto.getMaLoaiThuChi(), txtTen.getText().trim(), (String) cmbPL.getValue()),
                    "Đã cập nhật hạng mục.", "Lỗi cập nhật");
        });
    }

    private void xacNhanXoaLoai(LoaiThuChiDTO dto) {
        Alert c = new Alert(Alert.AlertType.CONFIRMATION,
                "Vô hiệu hóa \"" + dto.getTenLoaiThuChi() + "\"?",
                ButtonType.OK, ButtonType.CANCEL);
        DialogHelper.applyBakeryTheme(c);
        c.showAndWait().filter(r -> r == ButtonType.OK)
                .ifPresent(r -> runAsync(() -> soQuySvc.xoaLoai(dto.getMaLoaiThuChi()),
                        "Đã vô hiệu hóa hạng mục.", "Lỗi vô hiệu hóa"));
    }

    private Dialog<ButtonType> buildDialogLoai(String title, LoaiThuChiDTO existing) {
        Dialog<ButtonType> d = new Dialog<>();
        d.setTitle(title);
        d.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        d.getDialogPane().setPrefWidth(420);

        TextField txtTen = new TextField(existing != null ? existing.getTenLoaiThuChi() : "");
        txtTen.setId("txtTen");
        txtTen.setPromptText("Tên hạng mục...");

        ComboBox<String> cmbPL = new ComboBox<>(FXCollections.observableArrayList("Thu", "Chi"));
        cmbPL.setId("cmbPL");
        cmbPL.setMaxWidth(Double.MAX_VALUE);
        cmbPL.setValue(existing != null ? existing.getPhanLoai() : "Chi");

        d.getDialogPane().setContent(buildGrid("Tên hạng mục:", txtTen, "Phân loại:", cmbPL));
        DialogHelper.applyBakeryTheme(d);
        return d;
    }

    // ── Helpers ───────────────────────────────────────────────────────────

    private GridPane buildGrid(Object... pairs) {
        GridPane g = new GridPane();
        g.setHgap(12);
        g.setVgap(10);
        g.setPadding(new Insets(20));
        for (int i = 0; i < pairs.length; i += 2) {
            g.add(new Label((String) pairs[i]), 0, i / 2);
            g.add((Node) pairs[i + 1], 1, i / 2);
        }
        return g;
    }

    private Node lookupId(Dialog<?> d, String id) {
        return d.getDialogPane().getContent().lookup("#" + id);
    }

    private BigDecimal parseSoTien(String text) {
        BigDecimal v = CurrencyFormatter.parse(text);
        if (v.compareTo(BigDecimal.ZERO) <= 0) {
            hienThiLoiLabel("Số tiền không hợp lệ (phải là số nguyên dương).");
            return null;
        }
        return v;
    }

    private void runAsync(ThrowingRunnable task, String successMsg, String errPrefix) {
        Thread t = new Thread(() -> {
            try {
                task.run();
                javafx.application.Platform.runLater(() -> {
                    hienThiThanhCongLabel(successMsg);
                    taiDuLieu();
                });
            } catch (Exception e) {
                javafx.application.Platform.runLater(() -> hienThiLoiLabel(errPrefix + ": " + e.getMessage()));
            }
        }, "so-quy-async");
        t.setDaemon(true);
        t.start();
    }

    @FunctionalInterface
    private interface ThrowingRunnable {
        void run() throws Exception;
    }

    private static String nvl(String s) {
        return s != null ? s : "—";
    }
}
