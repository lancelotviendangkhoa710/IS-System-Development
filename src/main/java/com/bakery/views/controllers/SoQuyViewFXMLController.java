package com.bakery.views.controllers;

import com.bakery.model.dto.LoaiThuChiDTO;
import com.bakery.model.dto.PhieuThuChiDTO;
import com.bakery.presenters.SoQuyPresenter;
import com.bakery.services.SoQuyService;
import com.bakery.utils.CurrencyFormatter;
import com.bakery.utils.DialogHelper;
import com.bakery.views.interfaces.ISoQuyView;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class SoQuyViewFXMLController implements ISoQuyView {

    // ── FXML — Notification ──────────────────────────────────────────────────
    @FXML private Label          lblThongBao;

    // ── FXML — Custom tab bar ────────────────────────────────────────────────
    @FXML private Button         btnTabTongQuan;
    @FXML private Button         btnTabLichSu;
    @FXML private Button         btnTabCauHinh;
    @FXML private Button         btnThemGiaoDich;
    @FXML private Button         btnThemDanhMucMoi;

    // ── FXML — Content panes ─────────────────────────────────────────────────
    @FXML private ScrollPane     paneTongQuan;
    @FXML private VBox           paneLichSu;
    @FXML private VBox           paneCauHinh;

    // ── FXML — Tổng quan ────────────────────────────────────────────────────
    @FXML private Label          lblTongThu;
    @FXML private Label          lblTongChi;
    @FXML private Label          lblSoDu;
    @FXML private ProgressBar    barThu;
    @FXML private ProgressBar    barChi;
    @FXML private Label          lblTyLeThu;
    @FXML private Label          lblTyLeChi;

    // ── FXML — Lịch sử ──────────────────────────────────────────────────────
    @FXML private StackPane      paneLoadingLichSu;
    @FXML private TextField      tfTimKiem;
    @FXML private ComboBox<String> cbBoLoc;
    @FXML private TableView<PhieuThuChiDTO> tableGiaoDich;
    @FXML private Label          lblFooterGiaoDich;
    @FXML private Button         btnTruoc;
    @FXML private Button         btnTrangNum;
    @FXML private Button         btnSau;

    // ── FXML — Cấu hình ─────────────────────────────────────────────────────
    @FXML private StackPane      paneLoadingCauHinh;
    @FXML private TableView<LoaiThuChiDTO> tableLoai;
    @FXML private Label          lblFooterLoai;
    @FXML private Button         btnTruocLoai;
    @FXML private Button         btnTrangLoaiNum;
    @FXML private Button         btnSauLoai;

    private final SoQuyPresenter presenter =
            new SoQuyPresenter(this, new SoQuyService());

    private static final DateTimeFormatter FMT_THOIGIAN =
            DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy");

    private static final String STYLE_TAB_ACTIVE =
            "-fx-background-color: transparent;" +
            "-fx-border-color: transparent transparent #92400e transparent;" +
            "-fx-border-width: 0 0 3 0;" +
            "-fx-text-fill: #92400e;" +
            "-fx-font-weight: bold;" +
            "-fx-font-size: 13px;" +
            "-fx-padding: 12 16;" +
            "-fx-cursor: hand;";

    private static final String STYLE_TAB_INACTIVE =
            "-fx-background-color: transparent;" +
            "-fx-border-color: transparent;" +
            "-fx-text-fill: #6B7280;" +
            "-fx-font-size: 13px;" +
            "-fx-padding: 12 16;" +
            "-fx-font-weight: normal;" +
            "-fx-cursor: hand;";

    // ── Khởi tạo ─────────────────────────────────────────────────────────────

    @FXML
    public void initialize() {
        setupTableGiaoDich();
        setupTableLoai();
        setupBoLoc();
        activateTab("TONG_QUAN");
        presenter.onInitialize();
    }

    // ── Custom tab switching ──────────────────────────────────────────────────

    @FXML private void onTabTongQuan() { activateTab("TONG_QUAN"); }
    @FXML private void onTabLichSu()   { activateTab("LICH_SU"); }
    @FXML private void onTabCauHinh()  {
        activateTab("CAU_HINH");
        presenter.onTaiDanhSachLoai();
    }

    @FXML
    private void onQuayLai() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(com.bakery.main.App.class.getResource(com.bakery.main.App.MAIN_MENU_VIEW));
            javafx.scene.Parent root = loader.load();

            MainMenuViewFXMLController controller = loader.getController();
            com.bakery.model.dto.NhanVienDTO user = com.bakery.utils.UserSession.getCurrentUser();
            if (user != null) {
                controller.khoiTaoThongTinDangNhap(user);
            }

            javafx.scene.Scene scene = new javafx.scene.Scene(root, 1366, 768);
            java.net.URL cssUrl = com.bakery.main.App.class.getResource("/css/bakery.css");
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
            }

            javafx.stage.Stage stage = (javafx.stage.Stage) lblThongBao.getScene().getWindow();
            stage.setTitle("H3K Bakery - Hệ thống Quản trị");
            stage.setScene(scene);
            stage.setResizable(true);
            stage.centerOnScreen();
        } catch (Exception e) {
            System.err.println("[SoQuy] Lỗi quay lại menu: " + e.getMessage());
            hienThiLoi("Lỗi quay lại menu: " + e.getMessage());
        }
    }

    private void activateTab(String tab) {
        paneTongQuan.setVisible(false); paneTongQuan.setManaged(false);
        paneLichSu.setVisible(false);   paneLichSu.setManaged(false);
        paneCauHinh.setVisible(false);  paneCauHinh.setManaged(false);

        btnThemGiaoDich.setVisible(false);   btnThemGiaoDich.setManaged(false);
        btnThemDanhMucMoi.setVisible(false); btnThemDanhMucMoi.setManaged(false);

        btnTabTongQuan.setStyle(STYLE_TAB_INACTIVE);
        btnTabLichSu.setStyle(STYLE_TAB_INACTIVE);
        btnTabCauHinh.setStyle(STYLE_TAB_INACTIVE);

        switch (tab) {
            case "TONG_QUAN" -> {
                paneTongQuan.setVisible(true); paneTongQuan.setManaged(true);
                btnTabTongQuan.setStyle(STYLE_TAB_ACTIVE);
            }
            case "LICH_SU" -> {
                paneLichSu.setVisible(true); paneLichSu.setManaged(true);
                btnTabLichSu.setStyle(STYLE_TAB_ACTIVE);
                btnThemGiaoDich.setVisible(true); btnThemGiaoDich.setManaged(true);
            }
            case "CAU_HINH" -> {
                paneCauHinh.setVisible(true); paneCauHinh.setManaged(true);
                btnTabCauHinh.setStyle(STYLE_TAB_ACTIVE);
                btnThemDanhMucMoi.setVisible(true); btnThemDanhMucMoi.setManaged(true);
            }
        }
    }

    // ── Filter / search setup ─────────────────────────────────────────────────

    private void setupBoLoc() {
        cbBoLoc.setItems(FXCollections.observableArrayList("Tất cả", "Thu", "Chi"));
        cbBoLoc.setValue("Tất cả");
        cbBoLoc.valueProperty().addListener((obs, o, n) ->
                presenter.onBoLocThayDoi(mapBoLoc(n), tfTimKiem.getText()));
        tfTimKiem.textProperty().addListener((obs, o, n) ->
                presenter.onBoLocThayDoi(mapBoLoc(cbBoLoc.getValue()), n));
    }

    private String mapBoLoc(String value) {
        if ("Thu".equals(value)) return "Thu";
        if ("Chi".equals(value)) return "Chi";
        return "ALL";
    }

    // ── Setup TableView: Giao dịch ────────────────────────────────────────────

    @SuppressWarnings("unchecked")
    private void setupTableGiaoDich() {
        TableColumn<PhieuThuChiDTO, String> colMa = new TableColumn<>("Mã Phiếu");
        colMa.setCellValueFactory(c -> new SimpleStringProperty(
                String.format("PTC%02d", c.getValue().getMaPhieuTC())));
        colMa.setPrefWidth(90);

        TableColumn<PhieuThuChiDTO, String> colThoiGian = new TableColumn<>("Thời Gian");
        colThoiGian.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getNgayTao() != null
                        ? c.getValue().getNgayTao().format(FMT_THOIGIAN) : ""));
        colThoiGian.setPrefWidth(130);

        TableColumn<PhieuThuChiDTO, String> colLoai = new TableColumn<>("Loại");
        colLoai.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPhanLoai()));
        colLoai.setPrefWidth(70);
        colLoai.setCellFactory(col -> badgeCellThuChi());

        TableColumn<PhieuThuChiDTO, String> colDanhMuc = new TableColumn<>("Danh Mục");
        colDanhMuc.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getTenLoaiThuChi() != null ? c.getValue().getTenLoaiThuChi() : ""));
        colDanhMuc.setPrefWidth(160);

        TableColumn<PhieuThuChiDTO, String> colSoTien = new TableColumn<>("Số Tiền");
        colSoTien.setPrefWidth(130);
        colSoTien.setCellValueFactory(c -> {
            PhieuThuChiDTO p = c.getValue();
            String sign = "Thu".equals(p.getPhanLoai()) ? "+" : "-";
            return new SimpleStringProperty(sign + CurrencyFormatter.format(p.getSoTien()));
        });
        colSoTien.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); return; }
                setText(item);
                boolean isThu = item.startsWith("+");
                setStyle("-fx-alignment: CENTER-RIGHT; -fx-font-weight: bold;" +
                        " -fx-font-family: 'Segoe UI';" +
                        (isThu ? " -fx-text-fill: #16A34A;" : " -fx-text-fill: #DC2626;"));
            }
        });

        TableColumn<PhieuThuChiDTO, String> colMoTa = new TableColumn<>("Mô Tả");
        colMoTa.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getGhiChu() != null ? c.getValue().getGhiChu() : ""));
        colMoTa.setPrefWidth(180);

        TableColumn<PhieuThuChiDTO, String> colNguoiLap = new TableColumn<>("Người Lập");
        colNguoiLap.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getTenNhanVien() != null ? c.getValue().getTenNhanVien() : ""));
        colNguoiLap.setPrefWidth(130);

        TableColumn<PhieuThuChiDTO, Void> colThaoTac = new TableColumn<>("Thao Tác");
        colThaoTac.setPrefWidth(90);
        colThaoTac.setCellFactory(col -> new TableCell<>() {
            private final Button btnHuy = new Button("Huỷ");
            {
                btnHuy.setStyle(
                        "-fx-background-color: #FEE2E2; -fx-text-fill: #DC2626;" +
                        " -fx-background-radius: 6px; -fx-font-size: 12px;" +
                        " -fx-padding: 4 10; -fx-cursor: hand;");
            }
            @Override protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty);
                if (empty) { setGraphic(null); return; }
                PhieuThuChiDTO item = getTableView().getItems().get(getIndex());
                if ("cancelled".equals(item.getTrangThai())) {
                    Label lblHuy = new Label("Đã huỷ");
                    lblHuy.setStyle(
                            "-fx-background-color: #F3F4F6; -fx-text-fill: #9CA3AF;" +
                            " -fx-background-radius: 999px; -fx-padding: 2 10;" +
                            " -fx-font-size: 12px; -fx-font-family: 'Segoe UI';");
                    setGraphic(lblHuy);
                } else {
                    btnHuy.setOnAction(e -> presenter.onHuyGiaoDich(item.getMaPhieuTC()));
                    setGraphic(btnHuy);
                }
            }
        });

        tableGiaoDich.getColumns().addAll(
                colMa, colThoiGian, colLoai, colDanhMuc,
                colSoTien, colMoTa, colNguoiLap, colThaoTac);
        tableGiaoDich.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        tableGiaoDich.setPlaceholder(new Label("Không có giao dịch nào."));
    }

    // ── Setup TableView: Loại thu chi ─────────────────────────────────────────

    @SuppressWarnings("unchecked")
    private void setupTableLoai() {
        TableColumn<LoaiThuChiDTO, String> colID = new TableColumn<>("ID");
        colID.setCellValueFactory(c -> new SimpleStringProperty(
                String.format("LTC%02d", c.getValue().getMaLoaiThuChi())));
        colID.setPrefWidth(70);

        TableColumn<LoaiThuChiDTO, String> colTen = new TableColumn<>("Tên Danh Mục");
        colTen.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTenLoaiThuChi()));
        colTen.setPrefWidth(220);

        TableColumn<LoaiThuChiDTO, String> colPL = new TableColumn<>("Phân Loại");
        colPL.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPhanLoai()));
        colPL.setPrefWidth(90);
        colPL.setCellFactory(col -> badgeCellThuChi());

        TableColumn<LoaiThuChiDTO, String> colTrangThai = new TableColumn<>("Trạng Thái");
        colTrangThai.setPrefWidth(110);
        colTrangThai.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getThoiDiemXoa() == null ? "active" : "locked"));
        colTrangThai.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setGraphic(null); return; }
                boolean locked = "locked".equals(item);
                Label badge = new Label(locked ? "Đã khóa" : "Đang dùng");
                badge.setStyle(locked
                        ? "-fx-background-color: #F3F4F6; -fx-text-fill: #6B7280;" +
                          " -fx-background-radius: 999px; -fx-padding: 2 10;" +
                          " -fx-font-size: 12px; -fx-font-family: 'Segoe UI';"
                        : "-fx-background-color: #DCFCE7; -fx-text-fill: #16A34A;" +
                          " -fx-background-radius: 999px; -fx-padding: 2 10;" +
                          " -fx-font-size: 12px; -fx-font-family: 'Segoe UI';");
                setGraphic(badge);
                setText(null);
            }
        });

        TableColumn<LoaiThuChiDTO, Void> colThaoTac = new TableColumn<>("Thao Tác");
        colThaoTac.setPrefWidth(170);
        colThaoTac.setCellFactory(col -> new TableCell<>() {
            private final Button btnSua    = new Button("Sửa");
            private final Button btnToggle = new Button();
            private final HBox   hbox      = new HBox(8, btnSua, btnToggle);
            {
                btnSua.setStyle(
                        "-fx-background-color: #FEF3C7; -fx-text-fill: #92400E;" +
                        " -fx-background-radius: 6px; -fx-font-size: 12px;" +
                        " -fx-padding: 4 10; -fx-cursor: hand;");
                hbox.setAlignment(Pos.CENTER_LEFT);
            }
            @Override protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty);
                if (empty) { setGraphic(null); return; }
                LoaiThuChiDTO item = getTableView().getItems().get(getIndex());
                boolean locked = item.getThoiDiemXoa() != null;
                btnSua.setOnAction(e -> hienThiDialogThemSuaLoai(item));
                if (locked) {
                    btnToggle.setText("Mở khoá");
                    btnToggle.setStyle(
                            "-fx-background-color: #DCFCE7; -fx-text-fill: #16A34A;" +
                            " -fx-background-radius: 6px; -fx-font-size: 12px;" +
                            " -fx-padding: 4 10; -fx-cursor: hand;");
                    btnToggle.setOnAction(e -> presenter.onMoKhoaLoai(item.getMaLoaiThuChi()));
                } else {
                    btnToggle.setText("Khoá");
                    btnToggle.setStyle(
                            "-fx-background-color: #FEE2E2; -fx-text-fill: #DC2626;" +
                            " -fx-background-radius: 6px; -fx-font-size: 12px;" +
                            " -fx-padding: 4 10; -fx-cursor: hand;");
                    btnToggle.setOnAction(e -> presenter.onKhoaLoai(item.getMaLoaiThuChi()));
                }
                setGraphic(hbox);
            }
        });

        tableLoai.getColumns().addAll(colID, colTen, colPL, colTrangThai, colThaoTac);
        tableLoai.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        tableLoai.setPlaceholder(new Label("Không có danh mục nào."));
    }

    private <T> TableCell<T, String> badgeCellThuChi() {
        return new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setGraphic(null); return; }
                Label badge = new Label(item);
                badge.setStyle("Thu".equals(item)
                        ? "-fx-background-color: #DCFCE7; -fx-text-fill: #16A34A;" +
                          " -fx-background-radius: 999px; -fx-padding: 2 10;" +
                          " -fx-font-size: 12px; -fx-font-family: 'Segoe UI';"
                        : "-fx-background-color: #FEE2E2; -fx-text-fill: #DC2626;" +
                          " -fx-background-radius: 999px; -fx-padding: 2 10;" +
                          " -fx-font-size: 12px; -fx-font-family: 'Segoe UI';");
                setGraphic(badge);
                setText(null);
            }
        };
    }

    // ── FXML events ───────────────────────────────────────────────────────────

    @FXML private void onYeuCauThemGiaoDich() { presenter.onYeuCauThemGiaoDich(); }
    @FXML private void onTrangTruoc()         { presenter.onTrangTruoc(); }
    @FXML private void onTrangSau()           { presenter.onTrangSau(); }
    @FXML private void onTrangLoaiTruoc()     { presenter.onTrangLoaiTruoc(); }
    @FXML private void onTrangLoaiSau()       { presenter.onTrangLoaiSau(); }
    @FXML private void onMoDialogThemLoai()   { hienThiDialogThemSuaLoai(null); }

    // ── ISoQuyView — Tổng quan ─────────────────────────────────────────────

    @Override
    public void hienThiTongQuan(BigDecimal tongThu, BigDecimal tongChi, BigDecimal soDu) {
        Platform.runLater(() -> {
            lblTongThu.setText(CurrencyFormatter.format(tongThu));
            lblTongChi.setText(CurrencyFormatter.format(tongChi));
            lblSoDu.setText(CurrencyFormatter.format(soDu));
            boolean am = soDu.compareTo(BigDecimal.ZERO) < 0;
            lblSoDu.setStyle(
                    "-fx-font-size: 24px; -fx-font-weight: bold; -fx-font-family: 'Segoe UI';" +
                    (am ? " -fx-text-fill: #DC2626;" : " -fx-text-fill: #16A34A;"));
        });
    }

    @Override
    public void hienThiTyTrong(double tyLeThu, double tyLeChi) {
        Platform.runLater(() -> {
            barThu.setProgress(tyLeThu);
            barChi.setProgress(tyLeChi);
            lblTyLeThu.setText(String.format("%.0f%%", tyLeThu * 100));
            lblTyLeChi.setText(String.format("%.0f%%", tyLeChi * 100));
        });
    }

    // ── ISoQuyView — Lịch sử ──────────────────────────────────────────────

    @Override
    public void setLoadingLichSu(boolean loading) {
        Platform.runLater(() -> {
            paneLoadingLichSu.setVisible(loading);
            paneLoadingLichSu.setManaged(loading);
        });
    }

    @Override
    public void setLoaiOptions(List<LoaiThuChiDTO> options) {
        // no-op: list is passed directly to the dialog at call time
    }

    @Override
    public void hienThiDanhSachGiaoDich(List<PhieuThuChiDTO> ds) {
        Platform.runLater(() -> {
            tableGiaoDich.setItems(FXCollections.observableArrayList(ds));
            tableGiaoDich.refresh();
        });
    }

    @Override
    public void hienThiFooterGiaoDich(int tu, int den, int tong) {
        Platform.runLater(() -> {
            lblFooterGiaoDich.setText(
                    "Hiển thị " + tu + " - " + den + " trong tổng số " + tong + " dữ liệu");
            int trang = (tu <= 1) ? 1 : (tu - 1) / 20 + 1;
            btnTrangNum.setText(String.valueOf(trang));
        });
    }

    @Override
    public void setNutPhanTrangEnabled(boolean truoc, boolean sau) {
        Platform.runLater(() -> {
            btnTruoc.setDisable(!truoc);
            btnSau.setDisable(!sau);
        });
    }

    @Override
    public void hienThiDialogThemGiaoDich(List<LoaiThuChiDTO> cacLoai) {
        Platform.runLater(() -> buildDialogLapPhieu(cacLoai));
    }

    // ── ISoQuyView — Cấu hình ─────────────────────────────────────────────

    @Override
    public void setTabCauHinhVisible(boolean visible) {
        Platform.runLater(() -> {
            btnTabCauHinh.setVisible(visible);
            btnTabCauHinh.setManaged(visible);
        });
    }

    @Override
    public void setLoadingCauHinh(boolean loading) {
        Platform.runLater(() -> {
            paneLoadingCauHinh.setVisible(loading);
            paneLoadingCauHinh.setManaged(loading);
        });
    }

    @Override
    public void hienThiDanhSachLoai(List<LoaiThuChiDTO> ds) {
        Platform.runLater(() ->
                tableLoai.setItems(FXCollections.observableArrayList(ds)));
    }

    @Override
    public void hienThiFooterLoai(int tu, int den, int tong) {
        Platform.runLater(() -> {
            lblFooterLoai.setText(
                    "Hiển thị " + tu + " - " + den + " trong tổng số " + tong + " dữ liệu");
            int trang = (tu <= 1) ? 1 : (tu - 1) / 20 + 1;
            btnTrangLoaiNum.setText(String.valueOf(trang));
        });
    }

    @Override
    public void setNutPhanTrangLoaiEnabled(boolean truoc, boolean sau) {
        Platform.runLater(() -> {
            btnTruocLoai.setDisable(!truoc);
            btnSauLoai.setDisable(!sau);
        });
    }

    // ── ISoQuyView — Dialog xác nhận ──────────────────────────────────────

    @Override
    public Optional<String> hienThiDialogXacNhanHuy(String tenPhieu) {
        return DialogHelper.showReasonConfirmDialog(
                "Hủy Phiếu",
                "Bạn có chắc chắn muốn hủy phiếu " + tenPhieu + "?\n"
                + "Hành động này không thể hoàn tác và sẽ loại phiếu ra khỏi sổ quỹ.",
                "Lý do hủy phiếu:");
    }

    // ── ISoQuyView — Thông báo ─────────────────────────────────────────────

    @Override
    public void hienThiThongBao(String msg) {
        Platform.runLater(() -> {
            lblThongBao.setText(msg);
            lblThongBao.setStyle(
                    "-fx-background-color: #DCFCE7; -fx-text-fill: #16A34A;" +
                    " -fx-padding: 8 16; -fx-font-weight: bold; -fx-font-family: 'Segoe UI';");
            lblThongBao.setVisible(true);
            lblThongBao.setManaged(true);
        });
    }

    @Override
    public void hienThiLoi(String msg) {
        Platform.runLater(() -> {
            lblThongBao.setText(msg);
            lblThongBao.setStyle(
                    "-fx-background-color: #FEE2E2; -fx-text-fill: #DC2626;" +
                    " -fx-padding: 8 16; -fx-font-weight: bold; -fx-font-family: 'Segoe UI';");
            lblThongBao.setVisible(true);
            lblThongBao.setManaged(true);
        });
    }

    // ── Dialog: Lập Phiếu Mới ────────────────────────────────────────────────

    private void buildDialogLapPhieu(List<LoaiThuChiDTO> cacLoai) {
        Stage stage = new Stage(StageStyle.DECORATED);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Lập Phiếu Mới");
        stage.setResizable(false);

        Label lblTitle = new Label("Lập Phiếu Mới");
        lblTitle.setStyle(
                "-fx-font-size: 16px; -fx-font-weight: bold; -fx-font-family: 'Segoe UI';" +
                " -fx-text-fill: #92400E;");

        // Thu / Chi toggle
        ToggleGroup tgPhanLoai = new ToggleGroup();
        ToggleButton btnThu = new ToggleButton("Thu");
        ToggleButton btnChi = new ToggleButton("Chi");
        btnThu.setToggleGroup(tgPhanLoai);
        btnChi.setToggleGroup(tgPhanLoai);
        btnThu.setSelected(true);

        String styleThuOn   = "-fx-background-color: white; -fx-text-fill: #16A34A;" +
                              " -fx-border-color: #16A34A; -fx-border-width: 2;" +
                              " -fx-border-radius: 6px; -fx-background-radius: 6px;" +
                              " -fx-font-weight: bold; -fx-padding: 6 20; -fx-cursor: hand;";
        String styleChiOn   = "-fx-background-color: white; -fx-text-fill: #DC2626;" +
                              " -fx-border-color: #DC2626; -fx-border-width: 2;" +
                              " -fx-border-radius: 6px; -fx-background-radius: 6px;" +
                              " -fx-font-weight: bold; -fx-padding: 6 20; -fx-cursor: hand;";
        String styleToggleOff = "-fx-background-color: white; -fx-text-fill: #6B7280;" +
                                " -fx-border-color: #D1D5DB; -fx-border-width: 1;" +
                                " -fx-border-radius: 6px; -fx-background-radius: 6px;" +
                                " -fx-font-weight: normal; -fx-padding: 6 20; -fx-cursor: hand;";

        btnThu.setStyle(styleThuOn);
        btnChi.setStyle(styleToggleOff);

        ComboBox<LoaiThuChiDTO> cbLoai = new ComboBox<>();
        cbLoai.setMaxWidth(Double.MAX_VALUE);
        cbLoai.setPromptText("Chọn danh mục...");
        cbLoai.setConverter(new javafx.util.StringConverter<>() {
            @Override public String toString(LoaiThuChiDTO l)   { return l == null ? "" : l.getTenLoaiThuChi(); }
            @Override public LoaiThuChiDTO fromString(String s) { return null; }
        });

        Runnable refreshLoai = () -> {
            String loaiChon = btnThu.isSelected() ? "Thu" : "Chi";
            cbLoai.setItems(FXCollections.observableArrayList(
                    cacLoai.stream().filter(l -> loaiChon.equals(l.getPhanLoai())).toList()));
            cbLoai.setValue(null);
        };
        refreshLoai.run();

        tgPhanLoai.selectedToggleProperty().addListener((obs, o, n) -> {
            if (n == null) { tgPhanLoai.selectToggle(o); return; }
            boolean thuOn = n == btnThu;
            btnThu.setStyle(thuOn ? styleThuOn : styleToggleOff);
            btnChi.setStyle(thuOn ? styleToggleOff : styleChiOn);
            refreshLoai.run();
        });

        TextField tfSoTien = new TextField();
        tfSoTien.setPromptText("VD: 500000");
        tfSoTien.setMaxWidth(Double.MAX_VALUE);

        TextArea taMoTa = new TextArea();
        taMoTa.setPromptText("Mô tả (tuỳ chọn)");
        taMoTa.setPrefRowCount(3);
        taMoTa.setWrapText(true);

        Button btnXacNhan = new Button("Xác Nhận Lập");
        btnXacNhan.setMaxWidth(Double.MAX_VALUE);
        btnXacNhan.setStyle(
                "-fx-background-color: #92400E; -fx-text-fill: white;" +
                " -fx-font-weight: bold; -fx-font-size: 14px; -fx-background-radius: 8px;" +
                " -fx-padding: 10 0; -fx-cursor: hand;");

        Button btnHuy = new Button("Huỷ");
        btnHuy.setMaxWidth(Double.MAX_VALUE);
        btnHuy.setStyle(
                "-fx-background-color: #F3F4F6; -fx-text-fill: #374151;" +
                " -fx-font-size: 14px; -fx-background-radius: 8px;" +
                " -fx-padding: 10 0; -fx-cursor: hand;");

        btnHuy.setOnAction(e -> stage.close());
        btnXacNhan.setOnAction(e -> {
            LoaiThuChiDTO loai = cbLoai.getValue();
            if (loai == null) { hienThiLoi("Vui lòng chọn danh mục."); return; }
            try {
                String raw = tfSoTien.getText().trim()
                        .replace(",", "").replace(".", "").replace(" ", "");
                BigDecimal soTien = new BigDecimal(raw);
                if (soTien.compareTo(BigDecimal.ZERO) <= 0) throw new NumberFormatException();
                presenter.onXacNhanThemGiaoDich(
                        loai.getMaLoaiThuChi(), soTien, taMoTa.getText().trim());
                stage.close();
            } catch (NumberFormatException ex) {
                hienThiLoi("Số tiền không hợp lệ. Nhập số nguyên dương.");
            }
        });

        HBox hboxToggle = new HBox(8, btnThu, btnChi);
        HBox hboxBtns   = new HBox(8, btnHuy, btnXacNhan);
        HBox.setHgrow(btnHuy, Priority.ALWAYS);
        HBox.setHgrow(btnXacNhan, Priority.ALWAYS);

        Label lblPhanLoai  = smallLabel("Loại giao dịch:");
        Label lblDanhMuc   = smallLabel("Danh mục:");
        Label lblSoTienLbl = smallLabel("Số tiền (đ):");
        Label lblMoTaLbl   = smallLabel("Mô tả:");

        VBox content = new VBox(12,
                lblTitle,
                lblPhanLoai, hboxToggle,
                lblDanhMuc, cbLoai,
                lblSoTienLbl, tfSoTien,
                lblMoTaLbl, taMoTa,
                hboxBtns);
        content.setPadding(new Insets(24));
        content.setPrefWidth(420);

        stage.setScene(new Scene(content));
        stage.showAndWait();
    }

    // ── Dialog: Thêm / Sửa Danh Mục ─────────────────────────────────────────

    private void hienThiDialogThemSuaLoai(LoaiThuChiDTO item) {
        boolean isSua = item != null;
        Stage stage = new Stage(StageStyle.DECORATED);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle(isSua ? "Sửa Danh Mục" : "Thêm Danh Mục Mới");
        stage.setResizable(false);

        Label lblTitle = new Label("📁 " + (isSua ? "Sửa Danh Mục" : "Thêm Danh Mục Mới"));
        lblTitle.setStyle(
                "-fx-font-size: 16px; -fx-font-weight: bold; -fx-font-family: 'Segoe UI';" +
                " -fx-text-fill: #92400E;");

        TextField tfTen = new TextField(isSua ? item.getTenLoaiThuChi() : "");
        tfTen.setPromptText("Tên danh mục...");
        tfTen.setMaxWidth(Double.MAX_VALUE);

        ToggleGroup tgPL = new ToggleGroup();
        ToggleButton btnThu = new ToggleButton("Khoản THU");
        ToggleButton btnChi = new ToggleButton("Khoản CHI");
        btnThu.setToggleGroup(tgPL);
        btnChi.setToggleGroup(tgPL);

        if (isSua && "Chi".equals(item.getPhanLoai())) btnChi.setSelected(true);
        else btnThu.setSelected(true);

        String styleThuOn = "-fx-background-color: white; -fx-text-fill: #16A34A;" +
                            " -fx-border-color: #16A34A; -fx-border-width: 2;" +
                            " -fx-border-radius: 6px; -fx-background-radius: 6px;" +
                            " -fx-font-weight: bold; -fx-padding: 6 16; -fx-cursor: hand;";
        String styleChiOn = "-fx-background-color: white; -fx-text-fill: #DC2626;" +
                            " -fx-border-color: #DC2626; -fx-border-width: 2;" +
                            " -fx-border-radius: 6px; -fx-background-radius: 6px;" +
                            " -fx-font-weight: bold; -fx-padding: 6 16; -fx-cursor: hand;";
        String styleOff   = "-fx-background-color: white; -fx-text-fill: #6B7280;" +
                            " -fx-border-color: #D1D5DB; -fx-border-width: 1;" +
                            " -fx-border-radius: 6px; -fx-background-radius: 6px;" +
                            " -fx-font-weight: normal; -fx-padding: 6 16; -fx-cursor: hand;";

        Runnable applyStyle = () -> {
            boolean thuSel = btnThu.isSelected();
            btnThu.setStyle(thuSel ? styleThuOn : styleOff);
            btnChi.setStyle(thuSel ? styleOff : styleChiOn);
        };
        applyStyle.run();
        tgPL.selectedToggleProperty().addListener((obs, o, n) -> {
            if (n == null) { tgPL.selectToggle(o); return; }
            applyStyle.run();
        });

        Button btnLuu = new Button(isSua ? "Lưu Lại" : "Thêm Mới");
        btnLuu.setMaxWidth(Double.MAX_VALUE);
        btnLuu.setStyle(
                "-fx-background-color: #92400E; -fx-text-fill: white;" +
                " -fx-font-weight: bold; -fx-font-size: 14px; -fx-background-radius: 8px;" +
                " -fx-padding: 10 0; -fx-cursor: hand;");

        Button btnHuy = new Button("Huỷ");
        btnHuy.setMaxWidth(Double.MAX_VALUE);
        btnHuy.setStyle(
                "-fx-background-color: #F3F4F6; -fx-text-fill: #374151;" +
                " -fx-font-size: 14px; -fx-background-radius: 8px;" +
                " -fx-padding: 10 0; -fx-cursor: hand;");

        btnHuy.setOnAction(e -> stage.close());
        btnLuu.setOnAction(e -> {
            String ten = tfTen.getText().trim();
            String pl  = btnThu.isSelected() ? "Thu" : "Chi";
            if (isSua) presenter.onSuaLoai(item.getMaLoaiThuChi(), ten, pl);
            else        presenter.onThemLoai(ten, pl);
            stage.close();
        });

        HBox hboxToggle = new HBox(8, btnThu, btnChi);
        HBox hboxBtns   = new HBox(8, btnHuy, btnLuu);
        HBox.setHgrow(btnHuy, Priority.ALWAYS);
        HBox.setHgrow(btnLuu, Priority.ALWAYS);

        VBox content = new VBox(12,
                lblTitle,
                smallLabel("Tên danh mục:"), tfTen,
                smallLabel("Phân loại:"), hboxToggle,
                hboxBtns);
        content.setPadding(new Insets(24));
        content.setPrefWidth(380);

        stage.setScene(new Scene(content));
        stage.showAndWait();
    }

    // ── Utility ───────────────────────────────────────────────────────────────

    private static Label smallLabel(String text) {
        Label lbl = new Label(text);
        lbl.setStyle("-fx-font-size: 12px; -fx-text-fill: #374151; -fx-font-family: 'Segoe UI';");
        return lbl;
    }
}
