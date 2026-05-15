package com.bakery.views.controllers.kho;

import com.bakery.model.dao.kho.NguyenLieuDAO;
import com.bakery.model.dao.kho.PhieuXuatKhoDAO;
import com.bakery.model.dao.kho.SanPhamDAO;
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
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.io.File;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Controller Quản lý Xuất Kho.
 * Hỗ trợ 3 lý do xuất: Làm bánh / Nguyên liệu hỏng / Sản phẩm hỏng.
 * Mỗi lý do gọi đúng Procedure DB tương ứng, LYDOXUAT khớp constraint CK_PX_LYDO.
 */
public class XuatKhoViewFXMLController extends BaseController {

    @FXML private Label lblTitle;
    @FXML private Button btnXoa;
    @FXML private Button btnInPhieu; // in phiếu xuất đang chọn bằng JasperReports
    @FXML private TableView<PhieuXuatKhoDTO> tblData;
    @FXML private TableColumn<PhieuXuatKhoDTO, String> colDate;
    @FXML private TableColumn<PhieuXuatKhoDTO, String> colUser;
    @FXML private TableColumn<PhieuXuatKhoDTO, String> colContent;
    @FXML private TableColumn<PhieuXuatKhoDTO, String> colStatus;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // Hằng số lý do xuất — khớp chính xác constraint CK_PX_LYDO
    private static final String LYDO_LAM_BANH = "Lam banh";
    private static final String LYDO_NL_HONG  = "Nguyen lieu hong";
    private static final String LYDO_SP_HONG  = "San pham hong";
    private static final String LYDO_SAI_SOT  = "Sai sot trong qua trinh lam banh";

    private final PhieuXuatKhoDAO     xuatKhoDAO       = new PhieuXuatKhoDAO();
    private final SanPhamDAO          sanPhamDAO        = new SanPhamDAO();
    private final NguyenLieuDAO       nguyenLieuDAO     = new NguyenLieuDAO();
    private final XuatKhoSanXuatService xuatSanXuatSvc  = new XuatKhoSanXuatService();

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
        colDate.setCellValueFactory(c -> {
            String text = c.getValue().getNgayXuat() != null
                    ? c.getValue().getNgayXuat().format(FMT) : "—";
            return new SimpleStringProperty(text);
        });
        colUser.setCellValueFactory(c ->
                new SimpleStringProperty(nvl(c.getValue().getTenNhanVien())));
        colContent.setCellValueFactory(c ->
                new SimpleStringProperty(nvl(c.getValue().getLyDoXuat())));
        colStatus.setCellValueFactory(c ->
                new SimpleStringProperty("Phiếu #" + c.getValue().getMaPX()));
        tblData.setItems(danhSach);
        tblData.setPlaceholder(new Label("Chưa có phiếu xuất nào."));
    }

    private void taiDuLieu() {
        Thread t = new Thread(() -> {
            try {
                List<PhieuXuatKhoDTO> ds = xuatKhoDAO.layDanhSachPhieuXuat();
                javafx.application.Platform.runLater(() -> danhSach.setAll(ds));
            } catch (Exception e) {
                javafx.application.Platform.runLater(() ->
                        hienThiLoiLabel("Lỗi tải danh sách phiếu xuất: " + e.getMessage()));
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
        if (selected == null) return;
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

        RadioButton rdoLamBanh = new RadioButton("Xuất để làm bánh");
        RadioButton rdoNLHong  = new RadioButton("Xuất vì nguyên liệu hỏng");
        RadioButton rdoSPHong  = new RadioButton("Xuất vì bánh bảo quản hỏng");
        RadioButton rdoSaiSot  = new RadioButton("Xuất vì sai sót trong làm bánh");
        rdoLamBanh.getStyleClass().add("radio-button");
        rdoNLHong.getStyleClass().add("radio-button");
        rdoSPHong.getStyleClass().add("radio-button");
        rdoSaiSot.getStyleClass().add("radio-button");

        ToggleGroup group = new ToggleGroup();
        rdoLamBanh.setToggleGroup(group);
        rdoNLHong.setToggleGroup(group);
        rdoSPHong.setToggleGroup(group);
        rdoSaiSot.setToggleGroup(group);
        rdoLamBanh.setSelected(true);

        VBox box = new VBox(14, rdoLamBanh, rdoNLHong, rdoSPHong, rdoSaiSot);
        box.setPadding(new Insets(20));
        dialog.getDialogPane().setContent(box);
        dialog.getDialogPane().setPrefWidth(400);

        dialog.showAndWait().ifPresent(result -> {
            if (!result.getButtonData().isDefaultButton()) return;
            if (rdoLamBanh.isSelected())        moDialogLamBanh();
            else if (rdoNLHong.isSelected())    moDialogNguyenLieuHong();
            else if (rdoSPHong.isSelected())    moDialogSanPhamHong();
            else if (rdoSaiSot.isSelected())    moDialogSaiSotBanh();
        });
    }

    // ── Lý do 1: Xuất để làm bánh → PROC_XUATKHOSANXUAT ──────────────

    private void moDialogLamBanh() {
        List<SanPhamDTO> dsSP = loadSanPham();
        if (dsSP == null) return;

        Dialog<ButtonType> dialog = buildDialog(
                "Xuất Kho — Làm Bánh",
                "Xuất nguyên liệu theo công thức để sản xuất bánh\n(Lý do: " + LYDO_LAM_BANH + ")");

        // Dùng mảng để capture biến thay đổi trong lambda
        double[] khaDungHolder = {0};
        // [0]=gioiHan, [1]=daDung; Integer.MAX_VALUE = chưa cấu hình → không giới hạn
        int[] gioiHanHolder = {Integer.MAX_VALUE, 0};

        ComboBox<SanPhamDTO> cbSP = buildCbSanPham(dsSP);
        Label lblToiDa   = new Label("Đang tính...");
        Label lblGioiHan = new Label("Đang tải...");
        TextField txtSL  = new TextField("1");
        Button btnOk = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);

        cbSP.getStyleClass().add("combo-box");
        txtSL.getStyleClass().add("text-field");
        lblToiDa.getStyleClass().add("lbl-info");
        lblGioiHan.getStyleClass().add("lbl-info");

        // Tải giới hạn ngày 1 lần khi mở dialog (không phụ thuộc vào sản phẩm)
        Thread tGioiHan = new Thread(() -> {
            try {
                int[] gh = xuatSanXuatSvc.layGioiHanVaDaLam(java.time.LocalDate.now());
                gioiHanHolder[0] = gh[0];
                gioiHanHolder[1] = gh[1];
                javafx.application.Platform.runLater(() -> {
                    if (gh[0] == Integer.MAX_VALUE) {
                        lblGioiHan.setText("Không giới hạn");
                    } else {
                        int conLai = Math.max(0, gh[0] - gh[1]);
                        lblGioiHan.setText(String.format("%d/%d cái (còn: %d)", gh[1], gh[0], conLai));
                        if (conLai <= 0 && btnOk != null) btnOk.setDisable(true);
                    }
                });
            } catch (Exception ex) {
                javafx.application.Platform.runLater(() -> lblGioiHan.setText("Lỗi: " + ex.getMessage()));
            }
        }, "xuat-load-gioihan");
        tGioiHan.setDaemon(true);
        tGioiHan.start();

        // Mỗi khi đổi sản phẩm → gọi FUNC_SOLUONGKHADUNG trên thread phụ
        cbSP.setOnAction(e -> {
            SanPhamDTO sp = cbSP.getValue();
            if (sp == null) { lblToiDa.setText("—"); return; }
            lblToiDa.setText("Đang tính...");
            if (btnOk != null) btnOk.setDisable(true);
            Thread t = new Thread(() -> {
                try {
                    double kd = xuatSanXuatSvc.tinhSoLuongKhaDung(sp.getMaSP());
                    khaDungHolder[0] = kd;
                    javafx.application.Platform.runLater(() -> {
                        lblToiDa.setText(kd <= 0
                                ? "⚠ Không đủ nguyên liệu"
                                : String.format("Tối đa: %.0f cái (theo NL)", kd));
                        // Disable OK nếu NL không đủ hoặc đã đạt giới hạn ngày
                        int conLai = gioiHanHolder[0] == Integer.MAX_VALUE
                                ? Integer.MAX_VALUE
                                : Math.max(0, gioiHanHolder[0] - gioiHanHolder[1]);
                        if (btnOk != null) btnOk.setDisable(kd <= 0 || conLai <= 0);
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
                "Giới hạn hôm nay:", lblGioiHan,
                "Số lượng cần làm:", txtSL);
        dialog.getDialogPane().setContent(grid);

        dialog.showAndWait().ifPresent(r -> {
            if (r != ButtonType.OK) return;
            SanPhamDTO sp = cbSP.getValue();
            if (sp == null) { hienThiLoiLabel("Vui lòng chọn sản phẩm."); return; }
            double sl = parsePositive(txtSL.getText());
            if (sl < 0) return;
            // Giới hạn tối đa = min(khả dụng NL, còn lại theo giới hạn ngày)
            double conLaiGioiHan = gioiHanHolder[0] == Integer.MAX_VALUE
                    ? Double.MAX_VALUE
                    : Math.max(0, gioiHanHolder[0] - gioiHanHolder[1]);
            double toiDa = Math.min(khaDungHolder[0], conLaiGioiHan);
            if (sl > toiDa) {
                if (conLaiGioiHan < khaDungHolder[0]) {
                    hienThiLoiLabel(String.format(
                            "Vượt giới hạn ngày! Hôm nay còn được làm tối đa %.0f cái.", conLaiGioiHan));
                } else {
                    hienThiLoiLabel(String.format(
                            "Số lượng vượt quá khả năng sản xuất (tối đa %.0f cái).", toiDa));
                }
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
        if (dsNL.isEmpty()) { hienThiLoiLabel("Chưa có nguyên liệu nào trong kho."); return; }

        Dialog<ButtonType> dialog = buildDialog(
                "Xuất Kho — Nguyên Liệu Hỏng",
                "Xác nhận xuất hủy nguyên liệu không đạt chất lượng\n(Lý do: " + LYDO_NL_HONG + ")");

        ComboBox<NguyenLieuDTO> cbNL = new ComboBox<>(FXCollections.observableArrayList(dsNL));
        cbNL.setCellFactory(lv -> new ListCell<>() {
            @Override protected void updateItem(NguyenLieuDTO item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null
                        : item.getTenNL() + " (Tồn: " + String.format("%.2f", item.getSoLuongTonTong()) + ")");
            }
        });
        cbNL.setButtonCell(new ListCell<>() {
            @Override protected void updateItem(NguyenLieuDTO item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "— Chọn nguyên liệu —" : item.getTenNL());
            }
        });
        cbNL.getSelectionModel().selectFirst();
        cbNL.setMaxWidth(Double.MAX_VALUE);

        TextField txtSL = new TextField("1");
        dialog.getDialogPane().setContent(buildGrid("Nguyên liệu:", cbNL, "Số lượng hủy:", txtSL));

        dialog.showAndWait().ifPresent(r -> {
            if (r != ButtonType.OK) return;
            NguyenLieuDTO nl = cbNL.getValue();
            if (nl == null) { hienThiLoiLabel("Vui lòng chọn nguyên liệu."); return; }
            double sl = parsePositive(txtSL.getText());
            if (sl < 0) return;
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
        if (dsSP == null) return;

        Dialog<ButtonType> dialog = buildDialog(
                "Xuất Kho — Bánh Bảo Quản Hỏng",
                "Xác nhận xuất hủy bánh không đạt tiêu chuẩn bảo quản\n(Lý do: " + LYDO_SP_HONG + ")");

        ComboBox<SanPhamDTO> cbSP = buildCbSanPham(dsSP);
        TextField txtSL = new TextField("1");
        dialog.getDialogPane().setContent(buildGrid("Sản phẩm:", cbSP, "Số lượng hủy:", txtSL));

        dialog.showAndWait().ifPresent(r -> {
            if (r != ButtonType.OK) return;
            SanPhamDTO sp = cbSP.getValue();
            if (sp == null) { hienThiLoiLabel("Vui lòng chọn sản phẩm."); return; }
            double sl = parsePositive(txtSL.getText());
            if (sl < 0) return;
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
        if (dsSP == null) return;

        Dialog<ButtonType> dialog = buildDialog(
                "Xuất Kho — Sai Sót Làm Bánh",
                "Xác nhận xuất hủy bánh bị lỗi trong quá trình sản xuất\n(Lý do: " + LYDO_SAI_SOT + ")");

        ComboBox<SanPhamDTO> cbSP = buildCbSanPham(dsSP);
        TextField txtSL = new TextField("1");
        dialog.getDialogPane().setContent(buildGrid("Sản phẩm:", cbSP, "Số lượng hủy:", txtSL));

        dialog.showAndWait().ifPresent(r -> {
            if (r != ButtonType.OK) return;
            SanPhamDTO sp = cbSP.getValue();
            if (sp == null) { hienThiLoiLabel("Vui lòng chọn sản phẩm."); return; }
            double sl = parsePositive(txtSL.getText());
            if (sl < 0) return;
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
            if (ds.isEmpty()) { hienThiLoiLabel("Chưa có sản phẩm nào trong hệ thống."); return null; }
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

    /** Inject bakery.css vào dialog pane — áp dụng Amber Palette cho tất cả dialog. */
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
            @Override protected void updateItem(SanPhamDTO item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null
                        : item.getTenSP() + " (Tồn: " + (int) item.getSoLuongTon() + ")");
            }
        });
        cb.setButtonCell(new ListCell<>() {
            @Override protected void updateItem(SanPhamDTO item, boolean empty) {
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
        g.setHgap(12); g.setVgap(10);
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
            if (v <= 0) throw new NumberFormatException();
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
                javafx.application.Platform.runLater(() ->
                        hienThiLoiLabel(errPrefix + ": " + e.getMessage()));
            }
        }, threadName);
        t.setDaemon(true);
        t.start();
    }

    @FunctionalInterface
    private interface ThrowingRunnable {
        void run() throws Exception;
    }

    private static String nvl(String s) { return s != null ? s : "—"; }

    // ── In phiếu xuất kho bằng JasperReports ──────────────────────────────────────

    /**
     * Handler cho nút "🖨 In phiếu" — xuất phiếu xuất kho đang chọn sang PDF.
     * Vì PhieuXuatKhoDTO không có CTPHIEUXUAT riêng, mỗi phiếu xuất chỉ có 1 dòng hàng hóa.
     * Row = {tenHang, loaiHang, soLuong, donVi, ghiChu}.
     */
    @FXML
    private void onInPhieuXuat() {
        PhieuXuatKhoDTO phieu = tblData.getSelectionModel().getSelectedItem();
        if (phieu == null) {
            hienThiLoiLabel("Vui lòng chọn phiếu xuất cần in.");
            return;
        }

        new Thread(() -> {
            try {
                String maPhieu   = String.valueOf(phieu.getMaPX());
                String ngayXuat  = phieu.getNgayXuat() != null
                        ? phieu.getNgayXuat().format(FMT) : "—";
                String lyDo      = nvl(phieu.getLyDoXuat());
                String nguoiXuat = nvl(phieu.getTenNhanVien());
                String ghiChu    = "Lý do: " + lyDo;

                // Phiếu xuất chỉ có 1 dòng tổng hợp
                java.util.List<String[]> rows = new java.util.ArrayList<>();
                rows.add(new String[]{
                    "(Xem chi tiết trong hệ thống)", // tenHang
                    lyDo,                               // loaiHang
                    "—",                               // soLuong
                    "—",                               // donVi
                    ""                                  // ghiChu
                });

                File outputFile = ReportPathUtils.buildPdfPath("PhieuXuat", "PX-" + maPhieu);

                JasperReportUtils.xuatPhieuXuatKhoPDF(
                        outputFile, maPhieu, ngayXuat, lyDo, nguoiXuat, ghiChu, rows);

                javafx.application.Platform.runLater(() ->
                        hienThiThongTin("In phiếu xuất thành công",
                                "PDF đã lưu tại:\n" + outputFile.getAbsolutePath()));

            } catch (Exception e) {
                javafx.application.Platform.runLater(() ->
                        hienThiThongBaoLoi("Lỗi in phiếu xuất", e.getMessage()));
            }
        }, "in-phieu-xuat-jasper").start();
    }
}
