package com.bakery.views.controllers.kho;

import com.bakery.model.dao.kho.NhaCungCapDAO;
import com.bakery.model.dao.kho.NguyenLieuDAO;
import com.bakery.model.dao.kho.PhieuNhapKhoDAO;
import com.bakery.model.dto.kho.CTPhieuNhapDTO;
import com.bakery.model.dto.kho.NhaCungCapDTO;
import com.bakery.model.dto.kho.NguyenLieuDTO;
import com.bakery.model.dto.kho.PhieuNhapKhoDTO;
import com.bakery.utils.SessionContext;
import com.bakery.views.controllers.BaseController;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
    @FXML private TableView<PhieuNhapKhoDTO> tblData;
    @FXML private TableColumn<PhieuNhapKhoDTO, String> colDate;
    @FXML private TableColumn<PhieuNhapKhoDTO, String> colUser;
    @FXML private TableColumn<PhieuNhapKhoDTO, String> colContent;
    @FXML private TableColumn<PhieuNhapKhoDTO, String> colStatus;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final NumberFormat FMT_TIEN = NumberFormat.getNumberInstance(Locale.of("vi", "VN"));
    static { FMT_TIEN.setMaximumFractionDigits(0); }

    private final PhieuNhapKhoDAO nhapKhoDAO = new PhieuNhapKhoDAO();
    private final NhaCungCapDAO nhaCungCapDAO = new NhaCungCapDAO();
    private final NguyenLieuDAO nguyenLieuDAO = new NguyenLieuDAO();
    private final ObservableList<PhieuNhapKhoDTO> danhSach = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        lblTitle.setText("QUẢN LÝ NHẬP KHO");
        setupTable();
        taiDuLieu();
    }

    private void setupTable() {
        // Reuse FXML columns (colDate/colUser/colContent/colStatus) — map to PhieuNhapKhoDTO
        colDate.setCellValueFactory(c -> {
            PhieuNhapKhoDTO dto = c.getValue();
            String text = dto.getNgayNhap() != null ? dto.getNgayNhap().format(FMT) : "—";
            return new SimpleStringProperty(text);
        });
        colUser.setCellValueFactory(c ->
                new SimpleStringProperty(nvl(c.getValue().getTenNhanVien())));
        colContent.setCellValueFactory(c ->
                new SimpleStringProperty("NCC: " + nvl(c.getValue().getTenNhaCungCap()) +
                        " | Tổng: " + (c.getValue().getTongTienNhap() != null
                        ? FMT_TIEN.format(c.getValue().getTongTienNhap()) + " đ" : "—")));
        colStatus.setCellValueFactory(c ->
                new SimpleStringProperty("Phiếu #" + c.getValue().getMaPN()));
        tblData.setItems(danhSach);
        tblData.setPlaceholder(new Label("Chưa có phiếu nhập nào."));
    }

    private void taiDuLieu() {
        Thread t = new Thread(() -> {
            try {
                List<PhieuNhapKhoDTO> ds = nhapKhoDAO.layDanhSachPhieuNhap();
                javafx.application.Platform.runLater(() -> {
                    danhSach.setAll(ds);
                    if (ds.isEmpty()) hienThiLoiLabel("Chưa có phiếu nhập nào trong hệ thống.");
                });
            } catch (Exception e) {
                javafx.application.Platform.runLater(() ->
                        hienThiLoiLabel("Lỗi tải danh sách phiếu nhập: " + e.getMessage()));
            }
        }, "nhap-kho-tai-du-lieu");
        t.setDaemon(true);
        t.start();
    }

    @FXML
    private void onAction() {
        moDialogTaoPhieu();
    }

    @FXML
    private void onBack() {
        quayLaiMenuChinh(lblTitle);
    }

    // ── Dialog tạo phiếu nhập ─────────────────────────────────────────

    private void moDialogTaoPhieu() {
        // Tải dữ liệu cần thiết trước
        List<NhaCungCapDTO> dsNCC;
        List<NguyenLieuDTO> dsNL;
        try {
            dsNCC = nhaCungCapDAO.layDanhSachNhaCungCap();
            dsNL  = nguyenLieuDAO.layTatCaNguyenLieu();
        } catch (Exception e) {
            hienThiLoiLabel("Không thể tải dữ liệu: " + e.getMessage());
            return;
        }
        if (dsNCC.isEmpty()) {
            hienThiLoiLabel("Chưa có nhà cung cấp. Hãy thêm nhà cung cấp trước.");
            return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Tạo Phiếu Nhập Kho");
        dialog.setHeaderText("Chọn nhà cung cấp và nhập thông tin lô hàng");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(12); grid.setVgap(10);
        grid.setPadding(new Insets(20));

        // NCC combo
        ComboBox<NhaCungCapDTO> cbNCC = new ComboBox<>(FXCollections.observableArrayList(dsNCC));
        cbNCC.setCellFactory(lv -> new ListCell<>() {
            @Override protected void updateItem(NhaCungCapDTO item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getTenNCC() + " (" + item.getSdt() + ")");
            }
        });
        cbNCC.setButtonCell(new ListCell<>() {
            @Override protected void updateItem(NhaCungCapDTO item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "— Chọn NCC —" : item.getTenNCC());
            }
        });
        cbNCC.setMaxWidth(Double.MAX_VALUE);

        // Bảng chi tiết lô
        ObservableList<CTPhieuNhapDTO> chiTiet = FXCollections.observableArrayList();
        TableView<CTPhieuNhapDTO> tblChiTiet = buildBangChiTiet(chiTiet, dsNL);

        // Nút thêm dòng
        Button btnThemDong = new Button("+ Thêm nguyên liệu");
        btnThemDong.setOnAction(e -> themDongChiTiet(chiTiet, dsNL));

        grid.add(new Label("Nhà cung cấp:"), 0, 0);
        grid.add(cbNCC, 1, 0);
        grid.add(new Label("Chi tiết lô hàng:"), 0, 1);
        grid.add(tblChiTiet, 0, 2, 2, 1);
        grid.add(btnThemDong, 1, 3);

        tblChiTiet.setPrefHeight(200);
        dialog.getDialogPane().setContent(new VBox(10, grid));
        dialog.getDialogPane().setPrefWidth(700);

        // Thêm dòng đầu tiên mặc định
        themDongChiTiet(chiTiet, dsNL);

        dialog.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                xuLyLuuPhieuNhap(cbNCC.getValue(), chiTiet);
            }
        });
    }

    @SuppressWarnings("unchecked")
    private TableView<CTPhieuNhapDTO> buildBangChiTiet(
            ObservableList<CTPhieuNhapDTO> data, List<NguyenLieuDTO> dsNL) {

        TableView<CTPhieuNhapDTO> tbl = new TableView<>(data);

        TableColumn<CTPhieuNhapDTO, String> colNL = new TableColumn<>("Nguyên liệu");
        colNL.setPrefWidth(200);
        colNL.setCellValueFactory(c -> new SimpleStringProperty(nvl(c.getValue().getTenNL())));
        colNL.setCellFactory(col -> new TableCell<>() {
            private final ComboBox<NguyenLieuDTO> combo = new ComboBox<>(FXCollections.observableArrayList(dsNL));
            {
                combo.setCellFactory(lv -> new ListCell<>() {
                    @Override protected void updateItem(NguyenLieuDTO item, boolean empty) {
                        super.updateItem(item, empty);
                        setText(empty || item == null ? null : item.getTenNL());
                    }
                });
                combo.setButtonCell(new ListCell<>() {
                    @Override protected void updateItem(NguyenLieuDTO item, boolean empty) {
                        super.updateItem(item, empty);
                        setText(empty || item == null ? "— Chọn NL —" : item.getTenNL());
                    }
                });
                combo.setMaxWidth(Double.MAX_VALUE);
                combo.valueProperty().addListener((obs, old, nv) -> {
                    CTPhieuNhapDTO dto = getTableView().getItems().get(getIndex());
                    if (nv != null) { dto.setMaNL(nv.getMaNL()); dto.setTenNL(nv.getTenNL()); }
                });
            }
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) { setGraphic(null); return; }
                CTPhieuNhapDTO dto = getTableView().getItems().get(getIndex());
                dsNL.stream().filter(nl -> nl.getMaNL() == dto.getMaNL()).findFirst()
                        .ifPresent(combo::setValue);
                setGraphic(combo);
            }
        });

        TableColumn<CTPhieuNhapDTO, String> colSL = editableNumberColumn("Số lượng", 90);
        colSL.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getSoLuong())));
        colSL.setOnEditCommit(e -> {
            try { e.getRowValue().setSoLuong(Double.parseDouble(e.getNewValue())); } catch (NumberFormatException ignored) {}
        });

        TableColumn<CTPhieuNhapDTO, String> colDG = editableNumberColumn("Đơn giá (đ)", 110);
        colDG.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getDonGia() != null ? c.getValue().getDonGia().toPlainString() : "0"));
        colDG.setOnEditCommit(e -> {
            try { e.getRowValue().setDonGia(new java.math.BigDecimal(e.getNewValue())); } catch (NumberFormatException ignored) {}
        });

        TableColumn<CTPhieuNhapDTO, String> colHSD = editableTextColumn("Hạn dùng (yyyy-MM-dd)", 150);
        colHSD.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getHanSuDung() != null ? c.getValue().getHanSuDung().toString() : ""));
        colHSD.setOnEditCommit(e -> {
            try { e.getRowValue().setHanSuDung(LocalDate.parse(e.getNewValue())); } catch (Exception ignored) {}
        });

        tbl.getColumns().addAll(colNL, colSL, colDG, colHSD);
        tbl.setEditable(true);
        tbl.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        return tbl;
    }

    private void themDongChiTiet(ObservableList<CTPhieuNhapDTO> chiTiet, List<NguyenLieuDTO> dsNL) {
        CTPhieuNhapDTO dong = new CTPhieuNhapDTO();
        if (!dsNL.isEmpty()) { dong.setMaNL(dsNL.get(0).getMaNL()); dong.setTenNL(dsNL.get(0).getTenNL()); }
        dong.setSoLuong(1);
        dong.setDonGia(java.math.BigDecimal.ZERO);
        chiTiet.add(dong);
    }

    private void xuLyLuuPhieuNhap(NhaCungCapDTO ncc, List<CTPhieuNhapDTO> chiTiet) {
        if (ncc == null) { hienThiLoiLabel("Vui lòng chọn nhà cung cấp."); return; }
        if (chiTiet.isEmpty()) { hienThiLoiLabel("Vui lòng thêm ít nhất một nguyên liệu."); return; }

        int maNV = SessionContext.getInstance().getMaNV();

        // Build JSON array cho procedure
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < chiTiet.size(); i++) {
            CTPhieuNhapDTO ct = chiTiet.get(i);
            if (i > 0) json.append(",");
            json.append("{")
                .append("\"maNL\":").append(ct.getMaNL()).append(",")
                .append("\"tenNL\":\"").append(escapeJson(ct.getTenNL())).append("\",")
                .append("\"xuatXu\":\"\",")
                .append("\"maDVT\":1,")
                .append("\"soLuong\":").append(ct.getSoLuong()).append(",")
                .append("\"donGia\":").append(ct.getDonGia() != null ? ct.getDonGia().toPlainString() : "0").append(",")
                .append("\"ngaySanXuat\":\"").append(ct.getNgaySanXuat() != null ? ct.getNgaySanXuat().toString() : "").append("\",")
                .append("\"hanSuDung\":\"").append(ct.getHanSuDung() != null ? ct.getHanSuDung().toString() : "").append("\"")
                .append("}");
        }
        json.append("]");

        final String jsonStr = json.toString();
        Thread t = new Thread(() -> {
            try {
                int maPN = nhapKhoDAO.taoPhieuNhap(maNV, ncc.getMaNCC(), jsonStr);
                javafx.application.Platform.runLater(() -> {
                    hienThiThanhCongLabel("Đã tạo phiếu nhập #" + maPN + " thành công!");
                    taiDuLieu();
                });
            } catch (Exception e) {
                javafx.application.Platform.runLater(() ->
                        hienThiLoiLabel("Lỗi tạo phiếu nhập: " + e.getMessage()));
            }
        }, "nhap-kho-luu");
        t.setDaemon(true);
        t.start();
    }

    // ── Helpers ────────────────────────────────────────────────────────

    private static TableColumn<CTPhieuNhapDTO, String> editableNumberColumn(String title, double width) {
        TableColumn<CTPhieuNhapDTO, String> col = new TableColumn<>(title);
        col.setPrefWidth(width);
        col.setCellFactory(javafx.scene.control.cell.TextFieldTableCell.forTableColumn());
        return col;
    }

    private static TableColumn<CTPhieuNhapDTO, String> editableTextColumn(String title, double width) {
        TableColumn<CTPhieuNhapDTO, String> col = new TableColumn<>(title);
        col.setPrefWidth(width);
        col.setCellFactory(javafx.scene.control.cell.TextFieldTableCell.forTableColumn());
        return col;
    }

    private static String nvl(String s) { return s != null ? s : "—"; }
    private static String escapeJson(String s) { return s == null ? "" : s.replace("\"", "\\\""); }
}
