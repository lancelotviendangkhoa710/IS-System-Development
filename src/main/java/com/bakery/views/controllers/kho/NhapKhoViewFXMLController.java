package com.bakery.views.controllers.kho;

import com.bakery.model.dao.kho.NhaCungCapDAO;
import com.bakery.model.dao.kho.NguyenLieuDAO;
import com.bakery.model.dao.kho.PhieuNhapKhoDAO;
import com.bakery.model.dto.kho.CTPhieuNhapDTO;
import com.bakery.model.dto.kho.NhaCungCapDTO;
import com.bakery.model.dto.kho.NguyenLieuDTO;
import com.bakery.model.dto.kho.PhieuNhapKhoDTO;
import com.bakery.services.kho.NhapKhoService;
import com.bakery.services.nhansu.PhanQuyenService;
import com.bakery.utils.DialogHelper;
import com.bakery.utils.JasperReportUtils;
import com.bakery.utils.ReportPathUtils;
import com.bakery.utils.SessionContext;
import com.bakery.utils.UserSession;
import com.bakery.views.controllers.BaseController;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.ScrollPane;
import javafx.stage.FileChooser;

import java.io.File;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Controller Quản lý Nhập Kho.
 * Hiển thị lịch sử phiếu nhập và cung cấp dialog tạo phiếu mới.
 * Gọi PROC_TAOPHIEUNHAPKHO qua PhieuNhapKhoDAO.
 */
public class NhapKhoViewFXMLController extends BaseController {

    // ── Header row (từ FXML cũ — dùng lại fx:id) ──────────────────────
    @FXML private Label lblTitle;
    @FXML private Button btnXoa;      // chỉ visible với Admin/Quản lý
    @FXML private Button btnInPhieu;  // in phiếu nhập đang chọn bằng JasperReports
    @FXML private Button btnLapBaoCao; // lập báo cáo thống kê phiếu nhập theo tháng/năm
    @FXML private TableView<PhieuNhapKhoDTO> tblData;
    @FXML
    private TableColumn<PhieuNhapKhoDTO, String> colDate;
    @FXML
    private TableColumn<PhieuNhapKhoDTO, String> colUser;
    @FXML
    private TableColumn<PhieuNhapKhoDTO, String> colContent;
    @FXML
    private TableColumn<PhieuNhapKhoDTO, String> colStatus;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final NumberFormat FMT_TIEN = NumberFormat.getNumberInstance(Locale.of("vi", "VN"));
    static {
        FMT_TIEN.setMaximumFractionDigits(0);
    }

    private final PhieuNhapKhoDAO nhapKhoDAO = new PhieuNhapKhoDAO();
    private final NhaCungCapDAO nhaCungCapDAO = new NhaCungCapDAO();
    private final NguyenLieuDAO nguyenLieuDAO = new NguyenLieuDAO();
    private final NhapKhoService nhapKhoService = new NhapKhoService();
    private final ObservableList<PhieuNhapKhoDTO> danhSach = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        lblTitle.setText("QUẢN LÝ NHẬP KHO");
        setupTable();
        taiDuLieu();
        // Task 3: Hiện nút Xóa chỉ khi Admin hoặc Quản lý
        capNhatQuyenXoa();
    }

    /** Kiểm tra vai trò, hiện/ẩn btnXoa theo RBAC. */
    private void capNhatQuyenXoa() {
        if (btnXoa == null) return;
        PhanQuyenService svc = new PhanQuyenService();
        com.bakery.model.dto.nhansu.NhanVienDTO user = UserSession.getCurrentUser();
        boolean coQuyen = svc.laAdmin(user) || svc.laQuanLy(user);
        btnXoa.setVisible(coQuyen);
        btnXoa.setManaged(coQuyen);
        if (coQuyen) {
            btnXoa.setDisable(true);
            tblData.getSelectionModel().selectedItemProperty().addListener(
                    (obs, old, nv) -> btnXoa.setDisable(nv == null));
        }
        // Nút In phiếu: enable khi có dòng được chọn
        if (btnInPhieu != null) {
            tblData.getSelectionModel().selectedItemProperty().addListener(
                    (obs, old, nv) -> btnInPhieu.setDisable(nv == null));
        }
    }

    private void setupTable() {
        // Reuse FXML columns (colDate/colUser/colContent/colStatus) — map to
        // PhieuNhapKhoDTO
        colDate.setCellValueFactory(c -> {
            PhieuNhapKhoDTO dto = c.getValue();
            String text = dto.getNgayNhap() != null ? dto.getNgayNhap().format(FMT) : "—";
            return new SimpleStringProperty(text);
        });
        colUser.setCellValueFactory(c -> new SimpleStringProperty(nvl(c.getValue().getTenNhanVien())));
        colContent.setCellValueFactory(c -> new SimpleStringProperty("NCC: " + nvl(c.getValue().getTenNhaCungCap()) +
                " | Tổng: " + (c.getValue().getTongTienNhap() != null
                        ? FMT_TIEN.format(c.getValue().getTongTienNhap()) + " đ"
                        : "—")));
        colStatus.setCellValueFactory(c -> new SimpleStringProperty("Phiếu #" + c.getValue().getMaPN()));
        tblData.setItems(danhSach);
        tblData.setPlaceholder(new Label("Chưa có phiếu nhập nào."));
        // Double-click dòng → xem chi tiết lô hàng
        tblData.setRowFactory(tv -> {
            TableRow<PhieuNhapKhoDTO> row = new TableRow<>();
            row.setOnMouseClicked(evt -> {
                if (evt.getClickCount() == 2 && !row.isEmpty())
                    onXemChiTietPhieuNhap(row.getItem());
            });
            return row;
        });
    }

    private void taiDuLieu() {
        Thread t = new Thread(() -> {
            try {
                List<PhieuNhapKhoDTO> ds = nhapKhoDAO.layDanhSachPhieuNhap();
                javafx.application.Platform.runLater(() -> {
                    danhSach.setAll(ds);
                    if (ds.isEmpty())
                        hienThiLoiLabel("Chưa có phiếu nhập nào trong hệ thống.");
                });
            } catch (Exception e) {
                javafx.application.Platform
                        .runLater(() -> hienThiLoiLabel("Lỗi tải danh sách phiếu nhập: " + e.getMessage()));
            }
        }, "nhap-kho-tai-du-lieu");
        t.setDaemon(true);
        t.start();
    }

    @FXML
    private void onAction() {
        moDialogTaoPhieu();
    }

    /** Task 3: Hủy phiếu nhập đang chọn — chỉ Admin/Quản lý. */
    @FXML
    private void onXoa() {
        PhieuNhapKhoDTO selected = tblData.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Xác nhận hủy");
        confirm.setHeaderText("Hủy phiếu nhập #" + selected.getMaPN() + "?");
        confirm.setContentText("Hệ thống sẽ hoàn kho theo quy tắc DB. Bạn có chắc chắn?");
        confirm.initOwner(lblTitle.getScene().getWindow());
        DialogHelper.applyBakeryTheme(confirm);
        confirm.showAndWait().ifPresent(bt -> {
            if (bt == ButtonType.OK) {
                Thread t = new Thread(() -> {
                    try {
                        nhapKhoDAO.huyPhieuNhap(selected.getMaPN());
                        javafx.application.Platform.runLater(() -> {
                            hienThiThanhCongLabel("✅ Đã hủy phiếu nhập #" + selected.getMaPN());
                            taiDuLieu();
                        });
                    } catch (Exception e) {
                        javafx.application.Platform.runLater(() ->
                                hienThiLoiLabel("Lỗi hủy phiếu nhập: " + e.getMessage()));
                    }
                }, "nhap-kho-xoa");
                t.setDaemon(true);
                t.start();
            }
        });
    }

    @FXML
    private void onNhapTuFile() {
        // Bước 1: chọn NCC
        List<NhaCungCapDTO> dsNCC;
        try {
            dsNCC = nhaCungCapDAO.layDanhSachNhaCungCap();
        } catch (Exception e) {
            hienThiLoiLabel("Không thể tải danh sách nhà cung cấp: " + e.getMessage());
            return;
        }
        if (dsNCC.isEmpty()) {
            hienThiLoiLabel("Chưa có nhà cung cấp. Hãy thêm nhà cung cấp trước.");
            return;
        }

        // Dialog chọn NCC
        ComboBox<NhaCungCapDTO> cbNCC = new ComboBox<>(FXCollections.observableArrayList(dsNCC));
        cbNCC.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(NhaCungCapDTO item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getTenNCC() + " (" + item.getSdt() + ")");
            }
        });
        cbNCC.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(NhaCungCapDTO item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "— Chọn NCC —" : item.getTenNCC());
            }
        });
        cbNCC.setMaxWidth(Double.MAX_VALUE);

        Dialog<NhaCungCapDTO> dialogNCC = new Dialog<>();
        dialogNCC.setTitle("Nhập Kho Từ File");
        dialogNCC.setHeaderText("Bước 1: Chọn nhà cung cấp cho lô hàng");
        dialogNCC.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        VBox body = new VBox(8, new Label("Nhà cung cấp:"), cbNCC);
        body.setPadding(new Insets(16));
        dialogNCC.getDialogPane().setContent(body);
        dialogNCC.setResultConverter(bt -> bt == ButtonType.OK ? cbNCC.getValue() : null);

        DialogHelper.applyBakeryTheme(dialogNCC);
        NhaCungCapDTO nccChon = dialogNCC.showAndWait().orElse(null);
        if (nccChon == null)
            return;

        // Bước 2: chọn file
        FileChooser fc = new FileChooser();
        fc.setTitle("Chọn file nhập kho");
        fc.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("File JSON / CSV", "*.json", "*.csv"),
                new FileChooser.ExtensionFilter("Tất cả", "*.*"));
        File file = fc.showOpenDialog(lblTitle.getScene().getWindow());
        if (file == null)
            return;

        // Bước 3: parse + validate + preview
        xuLyNhapTuFile(file, nccChon);
    }

    private void xuLyNhapTuFile(File file, NhaCungCapDTO ncc) {
        Thread t = new Thread(() -> {
            try {
                // Parse file
                List<CTPhieuNhapDTO> dsDong;
                String tenFile = file.getName().toLowerCase();
                if (tenFile.endsWith(".json")) {
                    dsDong = nhapKhoService.docFileJson(file);
                } else if (tenFile.endsWith(".csv")) {
                    dsDong = nhapKhoService.docFileCsv(file);
                } else {
                    throw new Exception("Định dạng file không hỗ trợ. Chỉ chấp nhận .json hoặc .csv");
                }

                // Validate
                List<String> danhSachLoi = nhapKhoService.validate(dsDong);

                javafx.application.Platform.runLater(() -> hienThiDialogPreview(dsDong, danhSachLoi, ncc));

            } catch (Exception e) {
                javafx.application.Platform.runLater(() -> hienThiLoiLabel("Lỗi đọc file: " + e.getMessage()));
            }
        }, "nhap-kho-doc-file");
        t.setDaemon(true);
        t.start();
    }

    /**
     * Preview kết quả đọc file: bảng dữ liệu + danh sách lỗi, confirm trước khi
     * lưu.
     */
    private void hienThiDialogPreview(List<CTPhieuNhapDTO> dsDong,
            List<String> danhSachLoi, NhaCungCapDTO ncc) {
        Dialog<ButtonType> preview = new Dialog<>();
        preview.setTitle("Preview — Nhập Kho Từ File");
        preview.setHeaderText("NCC: " + ncc.getTenNCC() + " | " + dsDong.size() + " dòng");

        VBox root = new VBox(12);
        root.setPadding(new Insets(16));

        // Bảng preview
        TableView<CTPhieuNhapDTO> tblPreview = new TableView<>(
                FXCollections.observableArrayList(dsDong));
        tblPreview.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tblPreview.setPrefHeight(200);

        TableColumn<CTPhieuNhapDTO, String> colTen = new TableColumn<>("Tên NL");
        colTen.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTenNL()));

        TableColumn<CTPhieuNhapDTO, String> colSL = new TableColumn<>("Số lượng");
        colSL.setCellValueFactory(c -> {
            double sl = c.getValue().getSoLuong();
            String txt = (sl == (long) sl) ? String.valueOf((long) sl) : String.valueOf(sl);
            return new SimpleStringProperty(txt);
        });
        colSL.setPrefWidth(90);

        TableColumn<CTPhieuNhapDTO, String> colDG = new TableColumn<>("Đơn giá");
        colDG.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getDonGia() != null
                        ? FMT_TIEN.format(c.getValue().getDonGia()) + " đ" : "0 đ"));
        colDG.setPrefWidth(110);

        TableColumn<CTPhieuNhapDTO, String> colHSD = new TableColumn<>("Hạn sử dụng");
        colHSD.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getHanSuDung() != null
                        ? c.getValue().getHanSuDung().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "—"));
        colHSD.setPrefWidth(120);

        tblPreview.getColumns().addAll(colTen, colSL, colDG, colHSD);
        root.getChildren().add(tblPreview);

        // Khu vực lỗi
        if (!danhSachLoi.isEmpty()) {
            Label lblLoi = new Label("⚠️ Phát hiện " + danhSachLoi.size() + " lỗi — không thể lưu:");
            lblLoi.getStyleClass().addAll("lbl-body-bold", "text-danger");
            TextArea taLoi = new TextArea(String.join("\n", danhSachLoi));
            taLoi.setEditable(false);
            taLoi.setPrefHeight(100);
            root.getChildren().addAll(lblLoi, taLoi);
        }

        preview.getDialogPane().setContent(root);
        preview.getDialogPane().setPrefWidth(700);

        // Nút lưu chỉ hiện khi không có lỗi
        if (danhSachLoi.isEmpty()) {
            preview.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
            ((Button) preview.getDialogPane().lookupButton(ButtonType.OK)).setText("✅ Xác nhận Lưu");
        } else {
            preview.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        }

        preview.showAndWait().ifPresent(bt -> {
            if (bt == ButtonType.OK && danhSachLoi.isEmpty()) {
                thucHienLuuTuFile(dsDong, ncc);
            }
        });
    }

    private void thucHienLuuTuFile(List<CTPhieuNhapDTO> dsDong, NhaCungCapDTO ncc) {
        int maNV = SessionContext.getInstance().getMaNV();
        int maCa = SessionContext.getInstance().getMaCa(); // 0 nếu không có ca (thủ kho) — proc dùng NULLIF
        String json = nhapKhoService.buildJsonPayload(dsDong);

        Thread t = new Thread(() -> {
            try {
                int maPN = nhapKhoDAO.taoPhieuNhap(maNV, ncc.getMaNCC(), json, maCa);
                javafx.application.Platform.runLater(() -> {
                    hienThiThanhCongLabel("✅ Đã nhập kho từ file — Phiếu #" + maPN
                            + " (" + dsDong.size() + " lô hàng)");
                    taiDuLieu();
                });
            } catch (Exception e) {
                javafx.application.Platform.runLater(() -> hienThiLoiLabel("Lỗi lưu phiếu nhập: " + e.getMessage()));
            }
        }, "nhap-kho-luu-tu-file");
        t.setDaemon(true);
        t.start();
    }

    // ── Dialog tạo phiếu nhập ─────────────────────────────────────────

    private void moDialogTaoPhieu() {
        // Tải dữ liệu cần thiết trước
        List<NhaCungCapDTO> dsNCC;
        List<NguyenLieuDTO> dsNL;
        try {
            dsNCC = nhaCungCapDAO.layDanhSachNhaCungCap();
            dsNL = nguyenLieuDAO.layTatCaNguyenLieu();
        } catch (Exception e) {
            hienThiLoiLabel("Không thể tải dữ liệu: " + e.getMessage());
            return;
        }
        if (dsNCC.isEmpty()) {
            hienThiLoiLabel("Chưa có nhà cung cấp. Hãy thêm nhà cung cấp trước.");
            return;
        }
        if (dsNL.isEmpty()) {
            hienThiLoiLabel("Chưa có nguyên liệu nào trong hệ thống. Hãy thêm nguyên liệu trước khi tạo phiếu nhập.");
            return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Tạo Phiếu Nhập Kho");
        dialog.setHeaderText("Chọn nhà cung cấp và nhập thông tin lô hàng");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.getDialogPane().getStyleClass().add("bg-app");

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        // NCC combo
        ComboBox<NhaCungCapDTO> cbNCC = new ComboBox<>(FXCollections.observableArrayList(dsNCC));
        cbNCC.getStyleClass().add("combo-box");
        cbNCC.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(NhaCungCapDTO item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getTenNCC() + " (" + item.getSdt() + ")");
            }
        });
        cbNCC.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(NhaCungCapDTO item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "— Chọn NCC —" : item.getTenNCC());
            }
        });
        cbNCC.setMaxWidth(Double.MAX_VALUE);

        // Chi tiết lô hàng — dùng VBox+HBox rows thay TableView (tránh Dialog focus conflict)
        ObservableList<CTPhieuNhapDTO> chiTiet = FXCollections.observableArrayList();
        VBox rowsContainer = new VBox(6);

        // Header row
        HBox headerRow = new HBox(8,
                lblWith("Nguyên liệu", 175), lblWith("Số lượng", 85),
                lblWith("ĐVT", 55), lblWith("Đơn giá (đ)", 105), lblWith("Hạn dùng (yyyy-MM-dd)", 130));
        headerRow.getStyleClass().add("lbl-body-bold");
        rowsContainer.getChildren().add(headerRow);

        ScrollPane scrollChiTiet = new ScrollPane(rowsContainer);
        scrollChiTiet.setFitToWidth(true);
        scrollChiTiet.setPrefHeight(240);
        scrollChiTiet.setStyle("-fx-background-color: transparent; -fx-background: transparent;");

        // Nút thêm dòng
        Button btnThemDong = new Button("+ Thêm dòng nguyên liệu");
        btnThemDong.getStyleClass().add("btn-secondary");
        btnThemDong.setOnAction(e -> {
            CTPhieuNhapDTO dto = taoDongMacDinh(dsNL);
            chiTiet.add(dto);
            rowsContainer.getChildren().add(buildDongNhapLieu(dto, chiTiet, rowsContainer, dsNL));
        });

        Label lblNhaCungCap = new Label("Nhà cung cấp:");
        lblNhaCungCap.getStyleClass().add("lbl-body-bold");
        Label lblChiTietLo = new Label("Chi tiết lô hàng:");
        lblChiTietLo.getStyleClass().add("lbl-body-bold");

        grid.add(lblNhaCungCap, 0, 0);
        grid.add(cbNCC, 1, 0);
        grid.add(lblChiTietLo, 0, 1);
        grid.add(scrollChiTiet, 0, 2, 2, 1);
        grid.add(btnThemDong, 1, 3);

        dialog.getDialogPane().setContent(new VBox(10, grid));
        dialog.getDialogPane().setPrefWidth(680);

        // Áp dụng Amber theme
        java.net.URL cssUrlDialog = getClass().getResource("/css/bakery.css");
        if (cssUrlDialog != null) dialog.getDialogPane().getStylesheets().add(cssUrlDialog.toExternalForm());
        DialogHelper.applyBakeryTheme(dialog);

        Button btnOk = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        if (btnOk != null) btnOk.getStyleClass().add("btn-primary");
        Button btnCancel = (Button) dialog.getDialogPane().lookupButton(ButtonType.CANCEL);
        if (btnCancel != null) btnCancel.getStyleClass().add("btn-secondary");

        // Thêm dòng đầu tiên mặc định
        CTPhieuNhapDTO initDto = taoDongMacDinh(dsNL);
        chiTiet.add(initDto);
        rowsContainer.getChildren().add(buildDongNhapLieu(initDto, chiTiet, rowsContainer, dsNL));

        dialog.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                xuLyLuuPhieuNhap(cbNCC.getValue(), chiTiet);
            }
        });
    }

    // ── Builder cho 1 dòng nhập liệu (VBox+HBox pattern — không dùng TableView) ─────────────────

    /**
     * Tạo HBox 1 dòng nhập liệu: ComboBox NL + TextField SL + Label DVT + TextField DG + TextField HSD + nút xóa.
     * Binding 2 chiều trực tiếp vào {@code dto} — không qua TableView cell, tránh hoàn toàn Dialog focus conflict.
     */
    private HBox buildDongNhapLieu(CTPhieuNhapDTO dto,
                                    ObservableList<CTPhieuNhapDTO> chiTiet,
                                    VBox rowsContainer,
                                    List<NguyenLieuDTO> dsNL) {
        // ComboBox nguyên liệu
        ComboBox<NguyenLieuDTO> cbNL = new ComboBox<>(FXCollections.observableArrayList(dsNL));
        cbNL.setPrefWidth(175);
        cbNL.getStyleClass().add("combo-box");
        cbNL.setCellFactory(lv -> new ListCell<>() {
            @Override protected void updateItem(NguyenLieuDTO item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getTenNL());
            }
        });
        cbNL.setButtonCell(new ListCell<>() {
            @Override protected void updateItem(NguyenLieuDTO item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "— Chọn NL —" : item.getTenNL());
            }
        });
        dsNL.stream().filter(nl -> nl.getMaNL() == dto.getMaNL()).findFirst().ifPresent(cbNL::setValue);

        // Label đơn vị tính — tự cập nhật khi chọn NL
        Label lblDVT = new Label(dto.getTenDVT() != null ? dto.getTenDVT() : "");
        lblDVT.setPrefWidth(55);
        lblDVT.getStyleClass().add("lbl-body");

        cbNL.valueProperty().addListener((obs, old, nv) -> {
            if (nv != null) {
                dto.setMaNL(nv.getMaNL());
                dto.setTenNL(nv.getTenNL());
                dto.setTenDVT(nv.getTenDVT() != null ? nv.getTenDVT() : "");
                lblDVT.setText(dto.getTenDVT());
            }
        });

        // TextField số lượng
        TextField txtSL = new TextField(
                dto.getSoLuong() == (long) dto.getSoLuong()
                        ? String.valueOf((long) dto.getSoLuong()) : String.valueOf(dto.getSoLuong()));
        txtSL.setPrefWidth(85);
        txtSL.setPromptText("Số lượng");
        txtSL.textProperty().addListener((obs, old, nv) -> {
            try { dto.setSoLuong(Double.parseDouble(nv)); } catch (NumberFormatException ignored) {}
        });

        // TextField đơn giá
        TextField txtDG = new TextField(
                dto.getDonGia() != null ? dto.getDonGia().stripTrailingZeros().toPlainString() : "0");
        txtDG.setPrefWidth(105);
        txtDG.setPromptText("Đơn giá");
        txtDG.textProperty().addListener((obs, old, nv) -> {
            try { dto.setDonGia(new java.math.BigDecimal(nv)); } catch (NumberFormatException ignored) {}
        });

        // TextField hạn sử dụng
        TextField txtHSD = new TextField(
                dto.getHanSuDung() != null ? dto.getHanSuDung().toString() : "");
        txtHSD.setPrefWidth(130);
        txtHSD.setPromptText("yyyy-MM-dd");
        txtHSD.textProperty().addListener((obs, old, nv) -> {
            try { dto.setHanSuDung(LocalDate.parse(nv)); } catch (Exception ignored) {}
        });

        // Nút xóa dòng
        Button btnXoaDong = new Button("✕");
        btnXoaDong.getStyleClass().add("btn-danger");
        HBox row = new HBox(8, cbNL, txtSL, lblDVT, txtDG, txtHSD, btnXoaDong);
        row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        btnXoaDong.setOnAction(e -> {
            chiTiet.remove(dto);
            rowsContainer.getChildren().remove(row);
        });
        return row;
    }

    /** Tạo CTPhieuNhapDTO mặc định lấy NL đầu tiên trong danh sách. */
    private CTPhieuNhapDTO taoDongMacDinh(List<NguyenLieuDTO> dsNL) {
        CTPhieuNhapDTO dong = new CTPhieuNhapDTO();
        NguyenLieuDTO nlDau = dsNL.get(0);
        dong.setMaNL(nlDau.getMaNL());
        dong.setTenNL(nlDau.getTenNL());
        dong.setTenDVT(nlDau.getTenDVT() != null ? nlDau.getTenDVT() : "");
        dong.setSoLuong(1);
        dong.setDonGia(java.math.BigDecimal.ZERO);
        return dong;
    }

    /** Tạo Label header với chiều rộng cố định. */
    private static Label lblWith(String text, double width) {
        Label lbl = new Label(text);
        lbl.setPrefWidth(width);
        lbl.getStyleClass().add("lbl-body-bold");
        return lbl;
    }


    private void xuLyLuuPhieuNhap(NhaCungCapDTO ncc, List<CTPhieuNhapDTO> chiTiet) {
        if (ncc == null) {
            hienThiLoiLabel("Vui lòng chọn nhà cung cấp.");
            return;
        }
        if (chiTiet.isEmpty()) {
            hienThiLoiLabel("Vui lòng thêm ít nhất một nguyên liệu.");
            return;
        }

        int maNV = SessionContext.getInstance().getMaNV();
        int maCa = SessionContext.getInstance().getMaCa(); // 0 nếu không có ca (thủ kho) — proc dùng NULLIF

        // Build JSON array cho procedure
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < chiTiet.size(); i++) {
            CTPhieuNhapDTO ct = chiTiet.get(i);
            if (i > 0)
                json.append(",");
            json.append("{")
                    .append("\"maNL\":").append(ct.getMaNL()).append(",")
                    .append("\"tenNL\":\"").append(escapeJson(ct.getTenNL())).append("\",")
                    .append("\"xuatXu\":\"\",")
                    .append("\"maDVT\":1,")
                    .append("\"soLuong\":").append(ct.getSoLuong()).append(",")
                    .append("\"donGia\":").append(ct.getDonGia() != null ? ct.getDonGia().toPlainString() : "0")
                    .append(",")
                    .append("\"ngaySanXuat\":\"")
                    .append(ct.getNgaySanXuat() != null ? ct.getNgaySanXuat().toString() : "").append("\",")
                    .append("\"hanSuDung\":\"").append(ct.getHanSuDung() != null ? ct.getHanSuDung().toString() : "")
                    .append("\"")
                    .append("}");
        }
        json.append("]");

        final String jsonStr = json.toString();
        Thread t = new Thread(() -> {
            try {
                int maPN = nhapKhoDAO.taoPhieuNhap(maNV, ncc.getMaNCC(), jsonStr, maCa);
                javafx.application.Platform.runLater(() -> {
                    hienThiThanhCongLabel("Đã tạo phiếu nhập #" + maPN + " thành công!");
                    taiDuLieu();
                });
            } catch (Exception e) {
                javafx.application.Platform.runLater(() -> hienThiLoiLabel("Lỗi tạo phiếu nhập: " + e.getMessage()));
            }
        }, "nhap-kho-luu");
        t.setDaemon(true);
        t.start();
    }

    // ── Helpers ────────────────────────────────────────────────────────


    private static String nvl(String s) {
        return s != null ? s : "—";
    }

    private static String escapeJson(String s) {
        return s == null ? "" : s.replace("\"", "\\\"");
    }

    // ── Xem chi tiết phiếu nhập ────────────────────────────────────────────

    /** Tải chi tiết phiếu nhập trên background thread rồi hiển thị dialog. */
    private void onXemChiTietPhieuNhap(PhieuNhapKhoDTO phieu) {
        new Thread(() -> {
            try {
                List<CTPhieuNhapDTO> chiTiet = nhapKhoDAO.layChiTietPhieuNhap(phieu.getMaPN());
                javafx.application.Platform.runLater(() ->
                        hienThiDialogChiTietPhieuNhap(phieu, chiTiet));
            } catch (Exception e) {
                javafx.application.Platform.runLater(() ->
                        hienThiLoiLabel("Lỗi tải chi tiết phiếu #" + phieu.getMaPN() + ": " + e.getMessage()));
            }
        }, "xem-chi-tiet-phieu-nhap").start();
    }

    /** Dialog xem chi tiết lô hàng của một phiếu nhập. */
    @SuppressWarnings("unchecked")
    private void hienThiDialogChiTietPhieuNhap(PhieuNhapKhoDTO phieu, List<CTPhieuNhapDTO> chiTiet) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Chi tiết phiếu nhập #" + phieu.getMaPN());
        dialog.setHeaderText(
                "NCC: " + nvl(phieu.getTenNhaCungCap()) +
                " | Người nhập: " + nvl(phieu.getTenNhanVien()) +
                " | Ngày: " + (phieu.getNgayNhap() != null ? phieu.getNgayNhap().format(FMT) : "—"));
        dialog.getDialogPane().getStyleClass().add("bg-app");

        TableView<CTPhieuNhapDTO> tbl = new TableView<>(FXCollections.observableArrayList(chiTiet));
        tbl.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tbl.setPrefHeight(300);
        tbl.setPlaceholder(new Label("Phiếu này chưa có chi tiết lô hàng."));
        tbl.getStyleClass().add("table-view");

        TableColumn<CTPhieuNhapDTO, String> cTen = new TableColumn<>("Nguyên liệu");
        cTen.setCellValueFactory(c -> new SimpleStringProperty(nvl(c.getValue().getTenNL())));

        TableColumn<CTPhieuNhapDTO, String> cSL = new TableColumn<>("Số lượng");
        cSL.setCellValueFactory(c -> {
            double sl = c.getValue().getSoLuong();
            return new SimpleStringProperty((sl == (long) sl) ? String.valueOf((long) sl) : String.valueOf(sl));
        });
        cSL.setPrefWidth(90);

        TableColumn<CTPhieuNhapDTO, String> cDG = new TableColumn<>("Đơn giá");
        cDG.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getDonGia() != null
                        ? FMT_TIEN.format(c.getValue().getDonGia()) + " đ" : "—"));
        cDG.setPrefWidth(120);

        TableColumn<CTPhieuNhapDTO, String> cTT = new TableColumn<>("Thành tiền");
        cTT.setCellValueFactory(c -> {
            double sl = c.getValue().getSoLuong();
            double dg = c.getValue().getDonGia() != null ? c.getValue().getDonGia().doubleValue() : 0.0;
            return new SimpleStringProperty(FMT_TIEN.format(sl * dg) + " đ");
        });
        cTT.setPrefWidth(130);

        TableColumn<CTPhieuNhapDTO, String> cHSD = new TableColumn<>("Hạn sử dụng");
        cHSD.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getHanSuDung() != null
                        ? c.getValue().getHanSuDung().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "—"));
        cHSD.setPrefWidth(110);

        tbl.getColumns().addAll(cTen, cSL, cDG, cTT, cHSD);

        Label lblTong = new Label("Tổng tiền phiếu: " +
                (phieu.getTongTienNhap() != null ? FMT_TIEN.format(phieu.getTongTienNhap()) + " đ" : "—"));
        lblTong.getStyleClass().addAll("lbl-body-bold");

        javafx.scene.layout.VBox content = new javafx.scene.layout.VBox(12, tbl, lblTong);
        content.setPadding(new javafx.geometry.Insets(16));
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().setPrefWidth(750);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        java.net.URL cssUrl = getClass().getResource("/css/bakery.css");
        if (cssUrl != null) dialog.getDialogPane().getStylesheets().add(cssUrl.toExternalForm());
        
        Button btnClose = (Button) dialog.getDialogPane().lookupButton(ButtonType.CLOSE);
        if (btnClose != null) btnClose.getStyleClass().add("btn-secondary");

        dialog.showAndWait();
    }

    // ── In phiếu nhập kho bằng JasperReports ────────────────────────────────

    /**
     * Handler cho nút "In phiếu" — xuất phiếu nhập kho đang chọn sang PDF.
     * Chạy DB query + compile/fill Jasper trên background thread, mở dialog kết quả trên FX thread.
     */
    @FXML
    private void onInPhieuNhap() {
        PhieuNhapKhoDTO phieu = tblData.getSelectionModel().getSelectedItem();
        if (phieu == null) {
            hienThiLoiLabel("Vui lòng chọn phiếu nhập cần in.");
            return;
        }

        new Thread(() -> {
            try {
                // Lấy chi tiết lô hàng từ DB
                List<CTPhieuNhapDTO> chiTiet = nhapKhoDAO.layChiTietPhieuNhap(phieu.getMaPN());

                // Map DTO → String[] row cho Jasper
                // row = {tenNL, soLuong, donGia, thanhTien, hanSuDung, ghiChu}
                List<String[]> rows = new ArrayList<>();
                for (CTPhieuNhapDTO ct : chiTiet) {
                    double sl   = ct.getSoLuong();
                    double dg   = ct.getDonGia() != null ? ct.getDonGia().doubleValue() : 0.0;
                    double tt   = sl * dg;
                    String hanSD = ct.getHanSuDung() != null
                            ? ct.getHanSuDung().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "—";
                    rows.add(new String[]{
                        nvl(ct.getTenNL()),
                        FMT_TIEN.format(sl),
                        FMT_TIEN.format(dg) + " đ",
                        FMT_TIEN.format(tt) + " đ",
                        hanSD,
                        "" // ghi chú (không có trong DTO hiện tại)
                    });
                }

                String maPhieu   = String.valueOf(phieu.getMaPN());
                String ngayNhap  = phieu.getNgayNhap() != null
                        ? phieu.getNgayNhap().format(FMT) : "—";
                String ncc       = nvl(phieu.getTenNhaCungCap());
                String nguoiNhap = nvl(phieu.getTenNhanVien());
                String tongTien  = phieu.getTongTienNhap() != null
                        ? FMT_TIEN.format(phieu.getTongTienNhap()) + " đ" : "0 đ";

                File outputFile = ReportPathUtils.buildPdfPath("PhieuNhap", "PN-" + maPhieu);

                JasperReportUtils.xuatPhieuNhapKhoPDF(
                        outputFile, maPhieu, ngayNhap, ncc, nguoiNhap, tongTien, rows);

                javafx.application.Platform.runLater(() -> {
                        try {
                            if (java.awt.Desktop.isDesktopSupported())
                                java.awt.Desktop.getDesktop().open(outputFile);
                        } catch (Exception ignored) { }
                        hienThiThongTin("In phiếu thành công",
                                "PDF đã lưu tại:\n" + outputFile.getAbsolutePath());
                });

            } catch (Exception e) {
                javafx.application.Platform.runLater(() ->
                        hienThiThongBaoLoi("Lỗi in phiếu nhập", e.getMessage()));
            }
        }, "in-phieu-nhap-jasper").start();
    }

    // ── Lập báo cáo thống kê phiếu nhập kho ─────────────────────────────────

    /**
     * Lập báo cáo phiếu nhập kho — lọc theo tháng/năm, xuất PDF JasperReports.
     * (Nút chuyển từ màn hình Nguyên liệu sang đây)
     */
    @FXML
    private void onLapBaoCao() {
        // Đếm nhanh tổng số phiếu trong hệ thống
        int tongPhieu;
        try {
            tongPhieu = nhapKhoDAO.layDanhSachPhieuNhap().size();
        } catch (Exception e) {
            hienThiLoiLabel("Không thể truy vấn danh sách phiếu nhập: " + e.getMessage());
            return;
        }

        // ── Dialog chọn tháng/năm ────────────────────────────────────────────
        LocalDate now = LocalDate.now();

        ComboBox<Integer> cboThang = new ComboBox<>();
        for (int i = 1; i <= 12; i++)
            cboThang.getItems().add(i);
        cboThang.setValue(now.getMonthValue());

        ComboBox<Integer> cboNam = new ComboBox<>();
        for (int y = now.getYear(); y >= now.getYear() - 4; y--)
            cboNam.getItems().add(y);
        cboNam.setValue(now.getYear());

        javafx.scene.layout.HBox hboxLoc = new javafx.scene.layout.HBox(8,
                new Label("Tháng:"), cboThang,
                new Label("Năm:"), cboNam);
        hboxLoc.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        VBox content = new VBox(12,
                new Label("Hệ thống hiện có " + tongPhieu + " phiếu nhập trong kho."),
                new Label("Lọc báo cáo theo:"),
                hboxLoc);
        content.setPadding(new Insets(4, 0, 0, 0));

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Lập Báo Cáo Phiếu Nhập Kho");
        dialog.setHeaderText("📋  Lập báo cáo phiếu nhập kho");
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        DialogHelper.applyBakeryTheme(dialog);

        java.util.Optional<ButtonType> result = dialog.showAndWait();
        if (result.isEmpty() || result.get() != ButtonType.OK)
            return;

        final int thang = cboThang.getValue();
        final int nam = cboNam.getValue();

        // ── Background: truy vấn + lọc tháng + xuất PDF ─────────────────────
        if (btnLapBaoCao != null)
            btnLapBaoCao.setDisable(true);
        hienThiThanhCongLabel("⏳  Đang lập báo cáo tháng " + thang + "/" + nam + "...");

        final String nguoiLap = UserSession.getCurrentUser() != null
                ? UserSession.getCurrentUser().getHoTen()
                : "Hệ thống";
        final String ngayLap = now.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        new Thread(() -> {
            try {
                com.bakery.model.dto.kho.KetQuaKiemKeDTO ketQua = nhapKhoDAO.lapBaoCaoPhieuNhap();

                FMT_TIEN.setMaximumFractionDigits(0);
                DateTimeFormatter fmtDt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

                // Lọc theo tháng/năm đã chọn
                List<PhieuNhapKhoDTO> dsLoc = ketQua.getDanhSachPhieu().stream()
                        .filter(p -> p.getNgayNhap() != null
                                && p.getNgayNhap().getMonthValue() == thang
                                && p.getNgayNhap().getYear() == nam)
                        .collect(java.util.stream.Collectors.toList());

                // Tổng tiền cả kỳ
                java.math.BigDecimal tongTien = dsLoc.stream()
                        .filter(p -> p.getTongTienNhap() != null)
                        .map(PhieuNhapKhoDTO::getTongTienNhap)
                        .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);

                List<Integer> maPhieuList = dsLoc.stream()
                        .map(PhieuNhapKhoDTO::getMaPN)
                        .collect(java.util.stream.Collectors.toList());
                java.util.Map<Integer, List<String[]>> ctMap =
                        nhapKhoDAO.layChiTietNhieuPhieuNhap(maPhieuList);

                // Sort: theo NCC rồi mã phiếu tăng dần
                dsLoc.sort(java.util.Comparator
                        .comparing((PhieuNhapKhoDTO p) ->
                                p.getTenNhaCungCap() != null ? p.getTenNhaCungCap() : "")
                        .thenComparingInt(PhieuNhapKhoDTO::getMaPN));

                // Build flat rows cho Jasper
                List<String[]> jasperRows = new ArrayList<>();
                for (PhieuNhapKhoDTO dto : dsLoc) {
                    String tongTienPhieu = dto.getTongTienNhap() != null
                            ? FMT_TIEN.format(dto.getTongTienNhap()) + " ₫" : "0 ₫";
                    String ngayNhapStr = dto.getNgayNhap() != null
                            ? dto.getNgayNhap().format(fmtDt) : "—";
                    String ncc = nvl(dto.getTenNhaCungCap());
                    String nguoiNhap = nvl(dto.getTenNhanVien());
                    String maPhieuStr = String.valueOf(dto.getMaPN());

                    List<String[]> ctLines = ctMap.getOrDefault(dto.getMaPN(), List.of());
                    if (ctLines.isEmpty()) {
                        jasperRows.add(new String[] {
                                maPhieuStr, ngayNhapStr, ncc, nguoiNhap, tongTienPhieu,
                                "(Không có chi tiết)", "", "", "0 ₫", "0 ₫"
                        });
                    } else {
                        for (String[] ct : ctLines) {
                            // ct = {TENNL, soLuong, TENDVT, donGia_raw, thanhTien_raw}
                            String donGiaFmt = FMT_TIEN.format(Long.parseLong(ct[3])) + " ₫";
                            String thanhTienFmt = FMT_TIEN.format(Long.parseLong(ct[4])) + " ₫";
                            jasperRows.add(new String[] {
                                    maPhieuStr, ngayNhapStr, ncc, nguoiNhap, tongTienPhieu,
                                    ct[0], ct[1], ct[2], donGiaFmt, thanhTienFmt
                            });
                        }
                    }
                }

                File outputFile = ReportPathUtils.buildPdfPath("BaoCaoPhieuNhap", thang + "-" + nam);
                JasperReportUtils.xuatBaoCaoKiemKePhieuNhapPDF(
                        outputFile,
                        String.valueOf(dsLoc.size()),
                        FMT_TIEN.format(tongTien) + " ₫",
                        nguoiLap, ngayLap, jasperRows);

                final String tenFile = outputFile.getName();
                final String folder  = outputFile.getParent();

                javafx.application.Platform.runLater(() -> {
                    if (btnLapBaoCao != null) btnLapBaoCao.setDisable(false);
                    hienThiThanhCongLabel("✅ Đã lưu báo cáo: " + tenFile);
                    hienThiThongTin("Lập báo cáo thành công",
                            "Báo cáo phiếu nhập tháng " + thang + "/" + nam
                                    + " (" + dsLoc.size() + " phiếu) đã được lưu vào:\n" + folder);
                    try {
                        if (java.awt.Desktop.isDesktopSupported())
                            java.awt.Desktop.getDesktop().open(outputFile);
                    } catch (Exception ignored) { }
                });

            } catch (Exception e) {
                javafx.application.Platform.runLater(() -> {
                    if (btnLapBaoCao != null) btnLapBaoCao.setDisable(false);
                    hienThiLoiLabel("Lỗi lập báo cáo: " + e.getMessage());
                    hienThiThongBaoLoi("Lỗi Lập Báo Cáo", e.getMessage());
                });
            }
        }, "thread-lap-bao-cao-phieunhap").start();
    }
}
