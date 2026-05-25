package com.bakery.views.controllers.kho;

import com.bakery.model.dao.kho.NguyenLieuDAO;
import com.bakery.model.dao.kho.PhieuXuatKhoDAO;
import com.bakery.model.dao.kho.SanPhamDAO;
import com.bakery.model.dto.kho.CTPhieuXuatDTO;
import com.bakery.model.dto.kho.NguyenLieuDTO;
import com.bakery.model.dto.kho.PhieuXuatKhoDTO;
import com.bakery.model.dto.kho.SanPhamDTO;
import com.bakery.services.kho.XuatKhoSanXuatService;
import com.bakery.services.nhansu.PhanQuyenService;
import com.bakery.utils.JasperReportUtils;
import com.bakery.utils.ReportPathUtils;
import com.bakery.utils.SessionContext;
import com.bakery.utils.UserSession;
import com.bakery.views.controllers.BaseController;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Controller Quản lý Xuất Kho.
 * Hỗ trợ 3 lý do xuất: Làm bánh / Nguyên liệu hỏng / Sản phẩm hỏng.
 * Mỗi lý do gọi đúng Procedure DB tương ứng, LYDOXUAT khớp constraint
 * CK_PX_LYDO.
 */
public class XuatKhoViewFXMLController extends BaseController {

    @FXML
    private Label lblTitle;
    @FXML
    private Button btnXoa;
    @FXML
    private Button btnInPhieu; // in phiếu xuất đang chọn bằng JasperReports
    @FXML
    private TableView<PhieuXuatKhoDTO> tblData;
    @FXML
    private TableColumn<PhieuXuatKhoDTO, String> colDate;
    @FXML
    private TableColumn<PhieuXuatKhoDTO, String> colUser;
    @FXML
    private TableColumn<PhieuXuatKhoDTO, String> colContent;
    @FXML
    private TableColumn<PhieuXuatKhoDTO, String> colStatus;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final java.text.NumberFormat FMT_TIEN = java.text.NumberFormat.getIntegerInstance();
    static {
        FMT_TIEN.setGroupingUsed(true);
    }

    // Hằng số lý do xuất — khớp chính xác constraint CK_PX_LYDO
    private static final String LYDO_LAM_BANH = "Lam banh";
    private static final String LYDO_NL_HONG = "Nguyen lieu hong";
    private static final String LYDO_SP_HONG = "San pham hong";
    private static final String LYDO_SAI_SOT = "Sai sot trong qua trinh lam banh";

    private final PhieuXuatKhoDAO xuatKhoDAO = new PhieuXuatKhoDAO();
    private final SanPhamDAO sanPhamDAO = new SanPhamDAO();
    private final NguyenLieuDAO nguyenLieuDAO = new NguyenLieuDAO();
    private final XuatKhoSanXuatService xuatSanXuatSvc = new XuatKhoSanXuatService();

    private final ObservableList<PhieuXuatKhoDTO> danhSach = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        lblTitle.setText("QUẢN LÝ XUẤT KHO");
        setupTable();
        taiDuLieu();
        capNhatQuyenXoa();
    }

    /** Chỉ Admin/Quản lý được thấy nút Xóa phiếu xuất. */
    private void capNhatQuyenXoa() {
        if (btnXoa == null)
            return;
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
        colDate.setCellValueFactory(c -> {
            String text = c.getValue().getNgayXuat() != null
                    ? c.getValue().getNgayXuat().format(FMT)
                    : "—";
            return new SimpleStringProperty(text);
        });
        colUser.setCellValueFactory(c -> new SimpleStringProperty(nvl(c.getValue().getTenNhanVien())));
        colContent.setCellValueFactory(c -> new SimpleStringProperty(nvl(c.getValue().getLyDoXuat())));
        colStatus.setCellValueFactory(c -> new SimpleStringProperty("Phiếu #" + c.getValue().getMaPX()));
        tblData.setItems(danhSach);
        tblData.setPlaceholder(new Label("Chưa có phiếu xuất nào."));
        // Double-click dòng → xem chi tiết phiếu xuất
        tblData.setRowFactory(tv -> {
            TableRow<PhieuXuatKhoDTO> row = new TableRow<>();
            row.setOnMouseClicked(evt -> {
                if (evt.getClickCount() == 2 && !row.isEmpty())
                    hienThiDialogChiTietPhieuXuat(row.getItem());
            });
            return row;
        });
    }

    private void taiDuLieu() {
        Thread t = new Thread(() -> {
            try {
                List<PhieuXuatKhoDTO> ds = xuatKhoDAO.layDanhSachPhieuXuat();
                javafx.application.Platform.runLater(() -> danhSach.setAll(ds));
            } catch (Exception e) {
                javafx.application.Platform
                        .runLater(() -> hienThiLoiLabel("Lỗi tải danh sách phiếu xuất: " + e.getMessage()));
            }
        }, "xuat-kho-tai-du-lieu");
        t.setDaemon(true);
        t.start();
    }

    @FXML
    private void onAction() {
        moDialogChonLyDo();
    }

    /**
     * Chức năng xóa phiếu xuất yêu cầu Procedure hủy chuyên biệt.
     * Hiện chỉ bật RBAC hiển thị nút theo vai trò và hướng dẫn thao tác an toàn.
     */
    @FXML
    private void onXoa() {
        PhieuXuatKhoDTO selected = tblData.getSelectionModel().getSelectedItem();
        if (selected == null)
            return;
        hienThiLoiLabel("Hệ thống chưa cấu hình Procedure hủy phiếu xuất kho. Vui lòng liên hệ Quản lý DB.");
    }

    // ── Bước 1: Dialog chọn lý do xuất ────────────────────────────────

    private void moDialogChonLyDo() {
        PhanQuyenService svc = new PhanQuyenService();
        com.bakery.model.dto.nhansu.NhanVienDTO user = UserSession.getCurrentUser();

        // Thu Kho chi duoc xuat vi "Nguyen lieu hong" — bo qua dialog chon, vao thang
        if (svc.laThuKho(user)) {
            moDialogNguyenLieuHong();
            return;
        }

        ButtonType btnTiepTuc = new ButtonType("Tiếp tục →", ButtonBar.ButtonData.OK_DONE);

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Tạo Phiếu Xuất Kho");
        dialog.setHeaderText("Chọn lý do xuất kho");
        dialog.getDialogPane().getButtonTypes().addAll(btnTiepTuc, ButtonType.CANCEL);
        injectDialogCss(dialog);

        RadioButton rdoLamBanh = new RadioButton("Xuất đơn bánh (1 loại bánh)");
        RadioButton rdoLamBanhGop = new RadioButton("Xuất mẻ bánh (nhiều loại bánh)");
        RadioButton rdoNLHong = new RadioButton("Xuất vì nguyên liệu hỏng");
        RadioButton rdoSPHong = new RadioButton("Xuất vì bánh bảo quản hỏng");
        RadioButton rdoSaiSot = new RadioButton("Xuất vì sai sót trong làm bánh");
        rdoLamBanh.getStyleClass().add("radio-button");
        rdoLamBanhGop.getStyleClass().add("radio-button");
        rdoNLHong.getStyleClass().add("radio-button");
        rdoSPHong.getStyleClass().add("radio-button");
        rdoSaiSot.getStyleClass().add("radio-button");

        ToggleGroup group = new ToggleGroup();
        rdoLamBanh.setToggleGroup(group);
        rdoLamBanhGop.setToggleGroup(group);
        rdoNLHong.setToggleGroup(group);
        rdoSPHong.setToggleGroup(group);
        rdoSaiSot.setToggleGroup(group);
        rdoLamBanhGop.setSelected(true); // new multi-product export as default

        VBox box = new VBox(14, rdoLamBanh, rdoLamBanhGop, rdoNLHong, rdoSPHong, rdoSaiSot);
        box.setPadding(new Insets(20));
        dialog.getDialogPane().setContent(box);
        dialog.getDialogPane().setPrefWidth(400);

        dialog.showAndWait().ifPresent(result -> {
            if (!result.getButtonData().isDefaultButton())
                return;
            if (rdoLamBanh.isSelected())
                moDialogLamBanh();
            else if (rdoLamBanhGop.isSelected())
                moDialogLamBanhGop();
            else if (rdoNLHong.isSelected())
                moDialogNguyenLieuHong();
            else if (rdoSPHong.isSelected())
                moDialogSanPhamHong();
            else if (rdoSaiSot.isSelected())
                moDialogSaiSotBanh();
        });
    }

    public static class ItemSanXuat {
        private final int maSP;
        private final String tenSP;
        private final double soLuong;
        private final double toiDaNL;

        public ItemSanXuat(int maSP, String tenSP, double soLuong, double toiDaNL) {
            this.maSP = maSP;
            this.tenSP = tenSP;
            this.soLuong = soLuong;
            this.toiDaNL = toiDaNL;
        }

        public int getMaSP() { return maSP; }
        public String getTenSP() { return tenSP; }
        public double getSoLuong() { return soLuong; }
        public double getToiDaNL() { return toiDaNL; }
    }

    // ── Lý do 1b: Xuất nhiều loại bánh gộp (Xuất mẻ bánh) ──────────────────
    private void moDialogLamBanhGop() {
        List<SanPhamDTO> dsSP = loadSanPham();
        if (dsSP == null)
            return;

        Dialog<ButtonType> dialog = buildDialog(
                "Xuất Kho — Làm Bánh (Mẻ Bánh)",
                "Xuất nguyên liệu gộp để làm nhiều loại bánh cùng lúc\n(Lý do: " + LYDO_LAM_BANH + ")");

        ObservableList<ItemSanXuat> dsChon = FXCollections.observableArrayList();

        // ── Danh sách sản phẩm đã thêm ───────────────────────────────────
        TableView<ItemSanXuat> tblSelected = new TableView<>();
        tblSelected.setPlaceholder(new Label("Chưa có sản phẩm nào trong danh sách."));
        tblSelected.setItems(dsChon);
        tblSelected.setPrefHeight(220);
        tblSelected.setPrefWidth(420);

        TableColumn<ItemSanXuat, String> colTen = new TableColumn<>("Sản phẩm");
        colTen.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTenSP()));

        TableColumn<ItemSanXuat, Double> colSL = new TableColumn<>("Số lượng");
        colSL.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getSoLuong()));

        TableColumn<ItemSanXuat, Double> colToiDaNL = new TableColumn<>("Tối đa (NL)");
        colToiDaNL.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getToiDaNL()));

        tblSelected.getColumns().addAll(List.of(colTen, colSL, colToiDaNL));
        tblSelected.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        // ── Khu vực chọn và thêm sản phẩm ─────────────────────────────────
        double[] khaDungHolder = { 0 };
        ComboBox<SanPhamDTO> cbSP = buildCbSanPham(dsSP);
        Label lblToiDa = new Label("Đang tính...");
        TextField txtSL = new TextField("1");
        Button btnAdd = new Button("➕ Thêm bánh");
        Button btnRemove = new Button("➖ Xóa bánh");

        cbSP.getStyleClass().add("combo-box");
        txtSL.getStyleClass().add("text-field");
        lblToiDa.getStyleClass().add("lbl-info");
        btnAdd.getStyleClass().add("btn-primary");
        btnRemove.getStyleClass().add("btn-secondary");

        cbSP.setOnAction(e -> {
            SanPhamDTO sp = cbSP.getValue();
            if (sp == null) {
                lblToiDa.setText("—");
                return;
            }
            lblToiDa.setText("Đang tính...");
            Thread t = new Thread(() -> {
                try {
                    double kd = xuatSanXuatSvc.tinhSoLuongKhaDung(sp.getMaSP());
                    khaDungHolder[0] = kd;
                    javafx.application.Platform.runLater(() -> {
                        lblToiDa.setText(kd <= 0
                                ? "⚠ Không đủ nguyên liệu"
                                : String.format("Tối đa: %.0f cái (theo NL)", kd));
                    });
                } catch (Exception ex) {
                    javafx.application.Platform.runLater(() -> lblToiDa.setText("Lỗi: " + ex.getMessage()));
                }
            }, "xuat-tinh-khadung-gop");
            t.setDaemon(true);
            t.start();
        });
        cbSP.fireEvent(new javafx.event.ActionEvent());

        // ── Xử lý nút Thêm/Xóa ────────────────────────────────────────────
        btnAdd.setOnAction(e -> {
            SanPhamDTO sp = cbSP.getValue();
            if (sp == null) return;
            double sl = parsePositive(txtSL.getText());
            if (sl <= 0) return;

            if (sl > khaDungHolder[0]) {
                hienThiLoiLabel("Số lượng vượt quá khả năng sản xuất (tối đa theo NL: " + (int)khaDungHolder[0] + " cái).");
                return;
            }

            // Kiểm tra trùng
            ItemSanXuat existed = dsChon.stream().filter(item -> item.getMaSP() == sp.getMaSP()).findFirst().orElse(null);
            if (existed != null) {
                double totalSL = existed.getSoLuong() + sl;
                if (totalSL > khaDungHolder[0]) {
                    hienThiLoiLabel("Tổng số lượng cho " + sp.getTenSP() + " vượt quá khả năng sản xuất (tối đa: " + (int)khaDungHolder[0] + " cái).");
                    return;
                }
                dsChon.remove(existed);
                dsChon.add(new ItemSanXuat(sp.getMaSP(), sp.getTenSP(), totalSL, khaDungHolder[0]));
            } else {
                dsChon.add(new ItemSanXuat(sp.getMaSP(), sp.getTenSP(), sl, khaDungHolder[0]));
            }
            txtSL.setText("1");
        });

        btnRemove.setOnAction(e -> {
            ItemSanXuat selected = tblSelected.getSelectionModel().getSelectedItem();
            if (selected != null) {
                dsChon.remove(selected);
            }
        });

        // ── Bố cục GridPane & Dialog content ──────────────────────────────
        GridPane inputGrid = new GridPane();
        inputGrid.setHgap(12);
        inputGrid.setVgap(10);
        inputGrid.add(new Label("Sản phẩm:"), 0, 0);
        inputGrid.add(cbSP, 1, 0);
        inputGrid.add(new Label("Tối đa NL:"), 0, 1);
        inputGrid.add(lblToiDa, 1, 1);
        inputGrid.add(new Label("Số lượng:"), 0, 2);
        inputGrid.add(txtSL, 1, 2);

        HBox btnBox = new HBox(12, btnAdd, btnRemove);
        btnBox.setAlignment(Pos.CENTER_LEFT);
        inputGrid.add(btnBox, 1, 3);

        VBox leftBox = new VBox(10, new Label("📋 Mẻ sản xuất gộp (Danh sách bánh):"), tblSelected);
        VBox rightBox = new VBox(10, new Label("Thêm bánh vào mẻ:"), inputGrid);

        HBox contentBox = new HBox(25, leftBox, rightBox);
        contentBox.setPadding(new Insets(20));
        dialog.getDialogPane().setContent(contentBox);
        dialog.getDialogPane().setPrefWidth(850);

        dialog.showAndWait().ifPresent(r -> {
            if (r != ButtonType.OK)
                return;
            if (dsChon.isEmpty()) {
                hienThiLoiLabel("Vui lòng thêm ít nhất 1 sản phẩm vào mẻ.");
                return;
            }

            // Xây dựng JSON array
            StringBuilder json = new StringBuilder("[");
            for (int i = 0; i < dsChon.size(); i++) {
                if (i > 0) json.append(",");
                ItemSanXuat item = dsChon.get(i);
                json.append("{")
                    .append("\"maSP\":").append(item.getMaSP()).append(",")
                    .append("\"soLuong\":").append(item.getSoLuong())
                    .append("}");
            }
            json.append("]");

            int maNV = SessionContext.getInstance().getMaNV();
            runAsync(() -> xuatSanXuatSvc.yeuCauMultiSanXuat(json.toString(), maNV),
                    "Đã xuất nguyên liệu làm mẻ bánh thành công (" + dsChon.size() + " loại bánh).",
                    "Lỗi xuất mẻ bánh", "xuat-multi-lam-banh");
        });
    }

    // ── Lý do 1: Xuất để làm bánh → PROC_XUATKHOSANXUAT ──────────────

    private void moDialogLamBanh() {
        List<SanPhamDTO> dsSP = loadSanPham();
        if (dsSP == null)
            return;

        Dialog<ButtonType> dialog = buildDialog(
                "Xuất Kho — Làm Bánh",
                "Xuất nguyên liệu theo công thức để sản xuất bánh\n(Lý do: " + LYDO_LAM_BANH + ")");

        // Dùng mảng để capture biến thay đổi trong lambda
        double[] khaDungHolder = { 0 };

        ComboBox<SanPhamDTO> cbSP = buildCbSanPham(dsSP);
        Label lblToiDa = new Label("Đang tính...");
        TextField txtSL = new TextField("1");
        Button btnOk = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);

        cbSP.getStyleClass().add("combo-box");
        txtSL.getStyleClass().add("text-field");
        lblToiDa.getStyleClass().add("lbl-info");

        // Mỗi khi đổi sản phẩm → gọi FUNC_SOLUONGKHADUNG trên thread phụ
        cbSP.setOnAction(e -> {
            SanPhamDTO sp = cbSP.getValue();
            if (sp == null) {
                lblToiDa.setText("—");
                return;
            }
            lblToiDa.setText("Đang tính...");
            if (btnOk != null)
                btnOk.setDisable(true);
            Thread t = new Thread(() -> {
                try {
                    double kd = xuatSanXuatSvc.tinhSoLuongKhaDung(sp.getMaSP());
                    khaDungHolder[0] = kd;
                    javafx.application.Platform.runLater(() -> {
                        lblToiDa.setText(kd <= 0
                                ? "⚠ Không đủ nguyên liệu"
                                : String.format("Tối đa: %.0f cái (theo NL)", kd));
                        // Disable OK nếu NL không đủ
                        if (btnOk != null)
                            btnOk.setDisable(kd <= 0);
                    });
                } catch (Exception ex) {
                    javafx.application.Platform.runLater(() -> lblToiDa.setText("Lỗi: " + ex.getMessage()));
                }
            }, "xuat-tinh-khadung");
            t.setDaemon(true);
            t.start();
        });
        // Kích hoạt tính ngay khi mở dialog (sản phẩm đã selectFirst)
        cbSP.fireEvent(new javafx.event.ActionEvent());

        GridPane grid = buildGrid(
                "Sản phẩm:", cbSP,
                "Tối đa (nguyên liệu):", lblToiDa,
                "Số lượng cần làm:", txtSL);
        dialog.getDialogPane().setContent(grid);

        dialog.showAndWait().ifPresent(r -> {
            if (r != ButtonType.OK)
                return;
            SanPhamDTO sp = cbSP.getValue();
            if (sp == null) {
                hienThiLoiLabel("Vui lòng chọn sản phẩm.");
                return;
            }
            double sl = parsePositive(txtSL.getText());
            if (sl < 0)
                return;
            
            if (sl > khaDungHolder[0]) {
                hienThiLoiLabel(String.format(
                        "Số lượng vượt quá khả năng sản xuất (tối đa %.0f cái).", khaDungHolder[0]));
                return;
            }
            int maNV = SessionContext.getInstance().getMaNV();
            runAsync(() -> xuatKhoDAO.xuatKhoSanXuat(sp.getMaSP(), sl, maNV),
                    "Đã xuất nguyên liệu làm " + (int) sl + " " + sp.getTenSP() + ".",
                    "Lỗi xuất làm bánh", "xuat-lam-banh");
        });
    }

    // ── Lý do 2: Xuất nguyên liệu hỏng → PROC_XUATNGUYENLIEUHO NG ────

    private void moDialogNguyenLieuHong() {
        List<NguyenLieuDTO> dsNL;
        try {
            dsNL = nguyenLieuDAO.layTatCaNguyenLieu();
        } catch (Exception e) {
            hienThiLoiLabel("Không thể tải danh sách nguyên liệu: " + e.getMessage());
            return;
        }
        if (dsNL.isEmpty()) {
            hienThiLoiLabel("Chưa có nguyên liệu nào trong kho.");
            return;
        }

        Dialog<ButtonType> dialog = buildDialog(
                "Xuất Kho — Nguyên Liệu Hỏng",
                "Xác nhận xuất hủy nguyên liệu không đạt chất lượng\n(Lý do: " + LYDO_NL_HONG + ")");

        ComboBox<NguyenLieuDTO> cbNL = new ComboBox<>(FXCollections.observableArrayList(dsNL));
        cbNL.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(NguyenLieuDTO item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null
                        : item.getTenNL() + " (Tồn: " + String.format("%.2f", item.getSoLuongTonTong()) + ")");
            }
        });
        cbNL.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(NguyenLieuDTO item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "— Chọn nguyên liệu —" : item.getTenNL());
            }
        });
        cbNL.getSelectionModel().selectFirst();
        cbNL.setMaxWidth(Double.MAX_VALUE);

        TextField txtSL = new TextField("1");
        dialog.getDialogPane().setContent(buildGrid("Nguyên liệu:", cbNL, "Số lượng hủy:", txtSL));

        dialog.showAndWait().ifPresent(r -> {
            if (r != ButtonType.OK)
                return;
            NguyenLieuDTO nl = cbNL.getValue();
            if (nl == null) {
                hienThiLoiLabel("Vui lòng chọn nguyên liệu.");
                return;
            }
            double sl = parsePositive(txtSL.getText());
            if (sl < 0)
                return;
            if (sl > nl.getSoLuongTonTong()) {
                hienThiLoiLabel(String.format(
                        "Số lượng vượt quá tồn kho hiện tại (tồn: %.2f).", nl.getSoLuongTonTong()));
                return;
            }
            int maNV = SessionContext.getInstance().getMaNV();
            runAsync(() -> xuatKhoDAO.xuatHuyNguyenLieu(nl.getMaNL(), sl, maNV),
                    "Đã xuất hủy " + String.format("%.2f", sl) + " " + nl.getTenNL() + " (hỏng).",
                    "Lỗi xuất hủy nguyên liệu", "xuat-huy-nguyen-lieu");
        });
    }

    // ── Lý do 3: Xuất bánh bảo quản hỏng → PROC_XUATHUYBANH ──────────

    private void moDialogSanPhamHong() {
        List<SanPhamDTO> dsSP = loadSanPham();
        if (dsSP == null)
            return;

        Dialog<ButtonType> dialog = buildDialog(
                "Xuất Kho — Bánh Bảo Quản Hỏng",
                "Xác nhận xuất hủy bánh không đạt tiêu chuẩn bảo quản\n(Lý do: " + LYDO_SP_HONG + ")");

        ComboBox<SanPhamDTO> cbSP = buildCbSanPham(dsSP);
        TextField txtSL = new TextField("1");
        dialog.getDialogPane().setContent(buildGrid("Sản phẩm:", cbSP, "Số lượng hủy:", txtSL));

        dialog.showAndWait().ifPresent(r -> {
            if (r != ButtonType.OK)
                return;
            SanPhamDTO sp = cbSP.getValue();
            if (sp == null) {
                hienThiLoiLabel("Vui lòng chọn sản phẩm.");
                return;
            }
            double sl = parsePositive(txtSL.getText());
            if (sl < 0)
                return;
            if (sl > sp.getSoLuongTon()) {
                hienThiLoiLabel(String.format(
                        "Số lượng vượt quá tồn kho hiện tại (tồn: %d cái).", (int) sp.getSoLuongTon()));
                return;
            }
            int maNV = SessionContext.getInstance().getMaNV();
            runAsync(() -> xuatKhoDAO.xuatHuyBanh(sp.getMaSP(), sl, maNV),
                    "Đã xuất hủy " + (int) sl + " " + sp.getTenSP() + " (hỏng bảo quản).",
                    "Lỗi xuất hủy bánh", "xuat-huy-banh");
        });
    }

    // ── Lý do 4: Xuất vì sai sót trong làm bánh → PROC_XUATSAISOTBANH ─

    private void moDialogSaiSotBanh() {
        List<SanPhamDTO> dsSP = loadSanPham();
        if (dsSP == null)
            return;

        Dialog<ButtonType> dialog = buildDialog(
                "Xuất Kho — Sai Sót Làm Bánh",
                "Xác nhận xuất hủy bánh bị lỗi trong quá trình sản xuất\n(Lý do: " + LYDO_SAI_SOT + ")");

        ComboBox<SanPhamDTO> cbSP = buildCbSanPham(dsSP);
        TextField txtSL = new TextField("1");
        dialog.getDialogPane().setContent(buildGrid("Sản phẩm:", cbSP, "Số lượng hủy:", txtSL));

        dialog.showAndWait().ifPresent(r -> {
            if (r != ButtonType.OK)
                return;
            SanPhamDTO sp = cbSP.getValue();
            if (sp == null) {
                hienThiLoiLabel("Vui lòng chọn sản phẩm.");
                return;
            }
            double sl = parsePositive(txtSL.getText());
            if (sl < 0)
                return;
            if (sl > sp.getSoLuongTon()) {
                hienThiLoiLabel(String.format(
                        "Số lượng vượt quá tồn kho hiện tại (tồn: %d cái).", (int) sp.getSoLuongTon()));
                return;
            }
            int maNV = SessionContext.getInstance().getMaNV();
            runAsync(() -> xuatKhoDAO.xuatSaiSotBanh(sp.getMaSP(), sl, maNV),
                    "Đã xuất hủy " + (int) sl + " " + sp.getTenSP() + " (sai sót SX).",
                    "Lỗi xuất hủy bánh sai sót", "xuat-sai-sot-banh");
        });
    }

    // ── Helpers ────────────────────────────────────────────────────────

    private List<SanPhamDTO> loadSanPham() {
        try {
            List<SanPhamDTO> ds = sanPhamDAO.layTatCaSanPhamQuanLy();
            if (ds.isEmpty()) {
                hienThiLoiLabel("Chưa có sản phẩm nào trong hệ thống.");
                return null;
            }
            return ds;
        } catch (Exception e) {
            hienThiLoiLabel("Không thể tải danh sách sản phẩm: " + e.getMessage());
            return null;
        }
    }

    private Dialog<ButtonType> buildDialog(String title, String header) {
        Dialog<ButtonType> d = new Dialog<>();
        d.setTitle(title);
        d.setHeaderText(header);
        d.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        d.getDialogPane().setPrefWidth(480);
        injectDialogCss(d);
        return d;
    }

    /**
     * Inject bakery.css vào dialog pane — áp dụng Amber Palette cho tất cả dialog.
     */
    private void injectDialogCss(Dialog<?> d) {
        java.net.URL cssUrl = getClass().getResource("/css/bakery.css");
        if (cssUrl != null) {
            d.getDialogPane().getStylesheets().add(cssUrl.toExternalForm());
        }
        d.getDialogPane().getStyleClass().add("dialog-pane-styled");
    }

    private ComboBox<SanPhamDTO> buildCbSanPham(List<SanPhamDTO> list) {
        ComboBox<SanPhamDTO> cb = new ComboBox<>(FXCollections.observableArrayList(list));
        cb.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(SanPhamDTO item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null
                        : item.getTenSP() + " (Tồn: " + (int) item.getSoLuongTon() + ")");
            }
        });
        cb.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(SanPhamDTO item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "— Chọn sản phẩm —" : item.getTenSP());
            }
        });
        cb.getSelectionModel().selectFirst();
        cb.setMaxWidth(Double.MAX_VALUE);
        return cb;
    }

    /** Tạo GridPane 2-cột từ cặp (label, control) xen kẽ. */
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

    /** Parse số dương; trả -1 và hiện lỗi nếu không hợp lệ. */
    private double parsePositive(String text) {
        try {
            double v = Double.parseDouble(text.trim());
            if (v <= 0)
                throw new NumberFormatException();
            return v;
        } catch (NumberFormatException e) {
            hienThiLoiLabel("Số lượng không hợp lệ (phải là số > 0).");
            return -1;
        }
    }

    /** Chạy tác vụ nặng trên thread phụ, update UI trên Platform thread. */
    private void runAsync(ThrowingRunnable task, String successMsg, String errPrefix, String threadName) {
        Thread t = new Thread(() -> {
            try {
                task.run();
                javafx.application.Platform.runLater(() -> {
                    hienThiThanhCongLabel(successMsg);
                    taiDuLieu();
                });
            } catch (Exception e) {
                String msg = e.getMessage() != null ? e.getMessage() : "Lỗi không xác định.";
                javafx.application.Platform.runLater(() -> {
                    // Deadlock / lock timeout → hiện Alert chi tiết thay vì label
                    if (msg.startsWith("⏱") || msg.startsWith("⚠ Deadlock")
                            || msg.contains("Deadlock") || msg.contains("LOCK_TIMEOUT")) {
                        hienThiAlertDeadlock(msg);
                    } else {
                        hienThiLoiLabel(errPrefix + ": " + msg);
                    }
                });
            }
        }, threadName);
        t.setDaemon(true);
        t.start();
    }

    /**
     * Hiển thị Alert rõ ràng khi xảy ra deadlock hoặc lock timeout (§4.4).
     *
     * <p>Message có thể từ 2 path:
     * <ul>
     *   <li>LOCK_TIMEOUT: cả 2 phiên đồng thời timeout → cả 2 ROLLBACK (BUG mode)</li>
     *   <li>DEADLOCK_DETECTED: Oracle chọn 1 nạn nhân → phiên này ROLLBACK</li>
     * </ul>
     * Cả 2 trường hợp đều kết luận: giao dịch không được lưu — cần thử lại.
     */
    private void hienThiAlertDeadlock(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("§4.4 Deadlock — Giao Dịch Bị Hủy");
        alert.setHeaderText("❌ Giao dịch không được thực hiện");

        // Dòng đầu = loại lỗi ngắn; phần sau = chi tiết + kết luận ROLLBACK
        String[] lines = msg.split("\n", 2);
        String dongChinh = lines[0].trim();
        String chiTiet   = lines.length > 1 ? lines[1].trim() : "";

        // Nếu message đã có dòng ROLLBACK (từ BaseDAO) thì không cần thêm
        boolean daCoRollback = chiTiet.contains("ROLLBACK");
        String footer = daCoRollback ? "" :
                "\n\u274c Giao dịch đã bị ROLLBACK hoàn toàn — không có dữ liệu nào được lưu.";

        alert.setContentText(dongChinh
                + (chiTiet.isEmpty() ? "" : "\n\n" + chiTiet)
                + footer);

        alert.getDialogPane().setMinWidth(460);
        alert.showAndWait();

        // Cập nhật label footer tóm tắt
        hienThiLoiLabel(dongChinh);
    }

    @FunctionalInterface
    private interface ThrowingRunnable {
        void run() throws Exception;
    }

    private static String nvl(String s) {
        return s != null ? s : "—";
    }

    // ── Xem chi tiết phiếu xuất ────────────────────────────────────────────

    /**
     * Hiển thị dialog chi tiết phiếu xuất — load NL + TP từ DB, hiển bảng đầy đủ.
     */
    private void hienThiDialogChiTietPhieuXuat(PhieuXuatKhoDTO phieu) {
        // ── Tải dữ liệu từ DB ────────────────────────────────────────────────
        java.util.List<CTPhieuXuatDTO> dsChiTiet = new java.util.ArrayList<>();
        try {
            dsChiTiet = new PhieuXuatKhoDAO().layChiTietPhieuXuat(phieu.getMaPX());
        } catch (Exception e) {
            hienThiLoiLabel("Không tải được chi tiết phiếu xuất: " + e.getMessage());
        }

        // ── Header label ────────────────────────────────────────────────
        String header = "Ðơn #" + phieu.getMaPX()
                + "  |  Ngày xuất: " + (phieu.getNgayXuat() != null ? phieu.getNgayXuat().format(FMT) : "—")
                + "  |  Lý do: " + nvl(phieu.getLyDoXuat())
                + "  |  Người xuất: " + nvl(phieu.getTenNhanVien());

        // ── TableView ────────────────────────────────────────────────
        TableView<CTPhieuXuatDTO> tbl = new TableView<>();

        TableColumn<CTPhieuXuatDTO, String> colLoai = new TableColumn<>("Loại");
        colLoai.setCellValueFactory(c -> new SimpleStringProperty(
                "NL".equals(c.getValue().getLoai()) ? "Nguyên liệu" : "Thành phẩm"));
        colLoai.setPrefWidth(110);

        TableColumn<CTPhieuXuatDTO, String> colTen = new TableColumn<>("Tên hàng");
        colTen.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTenHang()));
        colTen.setPrefWidth(200);

        TableColumn<CTPhieuXuatDTO, String> colDVT = new TableColumn<>("ĐVT");
        colDVT.setCellValueFactory(c -> new SimpleStringProperty(nvl(c.getValue().getDonViTinh())));
        colDVT.setPrefWidth(70);

        TableColumn<CTPhieuXuatDTO, Double> colSL = new TableColumn<>("Số lượng");
        colSL.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getSoLuong()));
        colSL.setPrefWidth(90);

        TableColumn<CTPhieuXuatDTO, String> colGia = new TableColumn<>("Đơn giá vốn");
        colGia.setCellValueFactory(c -> {
            if (c.getValue().getDonGiaVon() == null)
                return new SimpleStringProperty("—");
            return new SimpleStringProperty(FMT_TIEN.format(c.getValue().getDonGiaVon()) + " đ");
        });
        colGia.setPrefWidth(120);

        tbl.getColumns().addAll(java.util.List.of(colLoai, colTen, colDVT, colSL, colGia));
        tbl.setItems(FXCollections.observableArrayList(dsChiTiet));
        tbl.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        tbl.setMaxHeight(340);
        if (dsChiTiet.isEmpty())
            tbl.setPlaceholder(new Label("Phiếu xuất này chưa có dữ liệu chi tiết."));

        // ── Layout ────────────────────────────────────────────────
        Label lblHeader = new Label(header);
        lblHeader.getStyleClass().add("lbl-body-bold");
        lblHeader.setWrapText(true);

        Label lblTitle2 = new Label("📊 Chi tiết xuất kho");
        lblTitle2.getStyleClass().add("lbl-title-card");

        Button btnDong = new Button("❌ Đóng");
        btnDong.getStyleClass().add("btn-secondary");

        HBox footer = new HBox(btnDong);
        footer.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);

        VBox root = new VBox(10, lblHeader, new Separator(), lblTitle2, tbl, new Separator(), footer);
        root.setPadding(new Insets(16));
        root.getStyleClass().add("bg-app");

        Scene scene = new Scene(root, 640, 460);
        java.net.URL cssUrl = getClass().getResource("/css/bakery.css");
        if (cssUrl != null)
            scene.getStylesheets().add(cssUrl.toExternalForm());

        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Chi tiết phiếu xuất #" + phieu.getMaPX());
        stage.setScene(scene);
        btnDong.setOnAction(e -> stage.close());
        stage.showAndWait();
    }

    @FXML
    private void onInPhieuXuat() {
        PhieuXuatKhoDTO phieu = tblData.getSelectionModel().getSelectedItem();
        if (phieu == null) {
            hienThiLoiLabel("Vui lòng chọn phiếu xuất cần in.");
            return;
        }

        new Thread(() -> {
            try {
                String maPhieu = String.valueOf(phieu.getMaPX());
                String ngayXuat = phieu.getNgayXuat() != null
                        ? phieu.getNgayXuat().format(FMT)
                        : "—";
                String lyDo = nvl(phieu.getLyDoXuat());
                String nguoiXuat = nvl(phieu.getTenNhanVien());
                String ghiChu = "Lý do: " + lyDo;

                // Lấy chi tiết thực từ DB (NL + TP)
                PhieuXuatKhoDAO xuatDAO = new PhieuXuatKhoDAO();
                List<CTPhieuXuatDTO> chiTiet = xuatDAO.layChiTietPhieuXuat(
                        Integer.parseInt(maPhieu));

                java.util.List<String[]> rows = new java.util.ArrayList<>();
                for (CTPhieuXuatDTO ct : chiTiet) {
                    String loaiLabel = "NL".equals(ct.getLoai())
                            ? "Nguyên liệu" : "Thành phẩm";
                    String soLuongStr = String.valueOf(
                            ct.getSoLuong() % 1 == 0
                                    ? (long) ct.getSoLuong()
                                    : ct.getSoLuong());
                    String ghiChuRow = ct.getDonGiaVon() != null
                            ? "Đơn giá: " + String.format("%,.0f ₫", ct.getDonGiaVon())
                            : "";
                    // Format: {tenHang, loaiHang, soLuong, donVi, ghiChu}
                    // JasperReportUtils tự thêm STT nội bộ
                    rows.add(new String[]{
                            ct.getTenHang(),
                            loaiLabel,
                            soLuongStr,
                            ct.getDonViTinh() != null ? ct.getDonViTinh() : "—",
                            ghiChuRow
                    });
                }

                if (rows.isEmpty()) {
                    rows.add(new String[]{
                            "(Không có chi tiết hàng hóa)", "—", "—", "—", ""
                    });
                }

                File outputFile = ReportPathUtils.buildPdfPath("PhieuXuat", "PX-" + maPhieu);

                JasperReportUtils.xuatPhieuXuatKhoPDF(
                        outputFile, maPhieu, ngayXuat, lyDo, nguoiXuat, ghiChu, rows);

                javafx.application.Platform.runLater(() -> {
                    try {
                        if (java.awt.Desktop.isDesktopSupported())
                            java.awt.Desktop.getDesktop().open(outputFile);
                    } catch (Exception ignored) { }
                    hienThiThongTin("In phiếu xuất thành công",
                            "PDF đã lưu tại:\n" + outputFile.getAbsolutePath());
                });

            } catch (Exception e) {
                javafx.application.Platform.runLater(() -> hienThiThongBaoLoi("Lỗi in phiếu xuất", e.getMessage()));
            }
        }, "in-phieu-xuat-jasper").start();
    }
}
