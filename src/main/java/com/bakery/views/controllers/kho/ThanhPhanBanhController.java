package com.bakery.views.controllers.kho;

import com.bakery.model.dto.kho.CotBanhDTO;
import com.bakery.model.dto.kho.KieuTrangTriDTO;
import com.bakery.model.dto.kho.NhanBanhDTO;
import com.bakery.services.banhang.TuyChinhBanhService;
import com.bakery.utils.DialogHelper;
import com.bakery.utils.SessionContext;
import com.bakery.views.controllers.BaseController;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Controller cho ThanhPhanBanhView (Quản lý công thức/thành phần bánh).
 * Mọi dữ liệu được lấy từ DB. Không có Mock Data.
 */
public class ThanhPhanBanhController extends BaseController {

    @FXML private TableView<CotBanhDTO> tblCotBanh;
    @FXML private TableColumn<CotBanhDTO, String> colTenCot;
    @FXML private TableColumn<CotBanhDTO, BigDecimal> colPhuPhiCot;
    @FXML private TableColumn<CotBanhDTO, String> colGiaVonCot;

    @FXML private TableView<NhanBanhDTO> tblNhanBanh;
    @FXML private TableColumn<NhanBanhDTO, String> colTenNhan;
    @FXML private TableColumn<NhanBanhDTO, BigDecimal> colPhuPhiNhan;
    @FXML private TableColumn<NhanBanhDTO, String> colGiaVonNhan;

    @FXML private TableView<KieuTrangTriDTO> tblKieuTrangTri;
    @FXML private TableColumn<KieuTrangTriDTO, String> colTenTrangTri;
    @FXML private TableColumn<KieuTrangTriDTO, BigDecimal> colPhuPhiTrangTri;
    @FXML private TableColumn<KieuTrangTriDTO, String> colGiaVonTrangTri;

    @FXML private Label lblTongGiaVon;
    @FXML private Label lblThongBao;

    // Bảng công thức nguyên liệu — hiện chưa có DAO tương ứng, để trống chờ impl
    @FXML private TableView<?> tblCongThuc;

    private final TuyChinhBanhService service = new TuyChinhBanhService();

    private final ObservableList<CotBanhDTO>      dsCotBanh   = FXCollections.observableArrayList();
    private final ObservableList<NhanBanhDTO>     dsNhanBanh  = FXCollections.observableArrayList();
    private final ObservableList<KieuTrangTriDTO> dsTrangTri  = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupTables();
        taiDuLieu();
    }

    // ── Setup bảng ─────────────────────────────────────────────────────

    private void setupTables() {
        colTenCot.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getTenCot()));
        colPhuPhiCot.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getPhuPhi()));
        colGiaVonCot.setCellValueFactory(cell -> new SimpleStringProperty(
                String.format("%,.0f đ", cell.getValue().getPhuPhi().doubleValue() * 0.4)));

        colTenNhan.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getTenNhan()));
        colPhuPhiNhan.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getPhuPhi()));
        colGiaVonNhan.setCellValueFactory(cell -> new SimpleStringProperty(
                String.format("%,.0f đ", cell.getValue().getPhuPhi().doubleValue() * 0.45)));

        colTenTrangTri.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getTenTrangTri()));
        colPhuPhiTrangTri.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getPhuPhi()));
        colGiaVonTrangTri.setCellValueFactory(cell -> new SimpleStringProperty(
                String.format("%,.0f đ", cell.getValue().getPhuPhi().doubleValue() * 0.3)));

        tblCotBanh.setItems(dsCotBanh);
        tblNhanBanh.setItems(dsNhanBanh);
        tblKieuTrangTri.setItems(dsTrangTri);

        tblCotBanh.setPlaceholder(new Label("Chưa có cốt bánh."));
        tblNhanBanh.setPlaceholder(new Label("Chưa có nhân bánh."));
        tblKieuTrangTri.setPlaceholder(new Label("Chưa có kiểu trang trí."));

        tblCotBanh.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> tinhTongGiaVon());
        tblNhanBanh.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> tinhTongGiaVon());
        tblKieuTrangTri.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> tinhTongGiaVon());
    }

    private void tinhTongGiaVon() {
        double cost = 0;
        if (tblCotBanh.getSelectionModel().getSelectedItem() != null)
            cost += tblCotBanh.getSelectionModel().getSelectedItem().getPhuPhi().doubleValue() * 0.4;
        if (tblNhanBanh.getSelectionModel().getSelectedItem() != null)
            cost += tblNhanBanh.getSelectionModel().getSelectedItem().getPhuPhi().doubleValue() * 0.45;
        if (tblKieuTrangTri.getSelectionModel().getSelectedItem() != null)
            cost += tblKieuTrangTri.getSelectionModel().getSelectedItem().getPhuPhi().doubleValue() * 0.3;
        if (lblTongGiaVon != null) lblTongGiaVon.setText(String.format("%,.0f đ", cost));
    }

    // ── Tải dữ liệu (background) ────────────────────────────────────────

    private void taiDuLieu() {
        Thread t = new Thread(() -> {
            StringBuilder log = new StringBuilder();
            List<CotBanhDTO>      cots  = List.of();
            List<NhanBanhDTO>     nhans = List.of();
            List<KieuTrangTriDTO> tts   = List.of();

            try { cots  = service.layDanhSachCotBanh(); }
            catch (Exception e) { log.append("Lỗi tải cốt bánh: ").append(e.getMessage()).append(". "); }

            try { nhans = service.layDanhSachNhanBanh(); }
            catch (Exception e) { log.append("Lỗi tải nhân bánh: ").append(e.getMessage()).append(". "); }

            try { tts   = service.layDanhSachKieuTrangTri(); }
            catch (Exception e) { log.append("Lỗi tải trang trí: ").append(e.getMessage()).append(". "); }

            final List<CotBanhDTO>      fCots  = cots;
            final List<NhanBanhDTO>     fNhans = nhans;
            final List<KieuTrangTriDTO> fTts   = tts;
            final String logStr = log.toString();

            Platform.runLater(() -> {
                dsCotBanh.setAll(fCots);
                dsNhanBanh.setAll(fNhans);
                dsTrangTri.setAll(fTts);
                if (lblThongBao != null)
                    lblThongBao.setText(logStr.isBlank() ? "Đã tải dữ liệu từ cơ sở dữ liệu." : logStr.trim());
            });
        }, "thanh-phan-banh-tai");
        t.setDaemon(true);
        t.start();
    }

    // ── CỐT BÁNH — handlers ─────────────────────────────────────────────

    @FXML
    private void onThemCot() {
        moDialogThanhPhan("Thêm Cốt Bánh", "Tên cốt bánh:", "Phụ phí (đ):", null, null,
            (ten, phuPhi) -> {
                CotBanhDTO dto = new CotBanhDTO();
                dto.setTenCot(ten);
                dto.setPhuPhi(phuPhi);
                chayNenVaLamMoi(() -> service.themCotBanh(dto), "✅ Đã thêm cốt bánh \"" + ten + "\".");
            });
    }

    @FXML
    private void onSuaCot() {
        CotBanhDTO sel = tblCotBanh.getSelectionModel().getSelectedItem();
        if (sel == null) { hienThiLoiLabel("Vui lòng chọn cốt bánh cần sửa."); return; }
        moDialogThanhPhan("Sửa Cốt Bánh", "Tên cốt bánh:", "Phụ phí (đ):", sel.getTenCot(), sel.getPhuPhi(),
            (ten, phuPhi) -> {
                sel.setTenCot(ten);
                sel.setPhuPhi(phuPhi);
                chayNenVaLamMoi(() -> service.suaCotBanh(sel), "✅ Đã cập nhật cốt bánh \"" + ten + "\".");
            });
    }

    @FXML
    private void onXoaCot() {
        CotBanhDTO sel = tblCotBanh.getSelectionModel().getSelectedItem();
        if (sel == null) { hienThiLoiLabel("Vui lòng chọn cốt bánh cần xóa."); return; }
        if (xacNhanXoa("cốt bánh", sel.getTenCot())) {
            int maNV = SessionContext.getInstance().getMaNV();
            chayNenVaLamMoi(() -> service.xoaCotBanh(sel.getMaCot(), maNV),
                    "✅ Đã xóa cốt bánh \"" + sel.getTenCot() + "\".");
        }
    }

    // ── NHÂN BÁNH — handlers ────────────────────────────────────────────

    @FXML
    private void onThemNhan() {
        moDialogThanhPhan("Thêm Nhân Bánh", "Tên nhân bánh:", "Phụ phí (đ):", null, null,
            (ten, phuPhi) -> {
                NhanBanhDTO dto = new NhanBanhDTO();
                dto.setTenNhan(ten);
                dto.setPhuPhi(phuPhi);
                chayNenVaLamMoi(() -> service.themNhanBanh(dto), "✅ Đã thêm nhân bánh \"" + ten + "\".");
            });
    }

    @FXML
    private void onSuaNhan() {
        NhanBanhDTO sel = tblNhanBanh.getSelectionModel().getSelectedItem();
        if (sel == null) { hienThiLoiLabel("Vui lòng chọn nhân bánh cần sửa."); return; }
        moDialogThanhPhan("Sửa Nhân Bánh", "Tên nhân bánh:", "Phụ phí (đ):", sel.getTenNhan(), sel.getPhuPhi(),
            (ten, phuPhi) -> {
                sel.setTenNhan(ten);
                sel.setPhuPhi(phuPhi);
                chayNenVaLamMoi(() -> service.suaNhanBanh(sel), "✅ Đã cập nhật nhân bánh \"" + ten + "\".");
            });
    }

    @FXML
    private void onXoaNhan() {
        NhanBanhDTO sel = tblNhanBanh.getSelectionModel().getSelectedItem();
        if (sel == null) { hienThiLoiLabel("Vui lòng chọn nhân bánh cần xóa."); return; }
        if (xacNhanXoa("nhân bánh", sel.getTenNhan())) {
            int maNV = SessionContext.getInstance().getMaNV();
            chayNenVaLamMoi(() -> service.xoaNhanBanh(sel.getMaNhan(), maNV),
                    "✅ Đã xóa nhân bánh \"" + sel.getTenNhan() + "\".");
        }
    }

    // ── KIỂU TRANG TRÍ — handlers ───────────────────────────────────────

    @FXML
    private void onThemTrangTri() {
        moDialogThanhPhan("Thêm Kiểu Trang Trí", "Tên trang trí:", "Phụ phí (đ):", null, null,
            (ten, phuPhi) -> {
                KieuTrangTriDTO dto = new KieuTrangTriDTO();
                dto.setTenTrangTri(ten);
                dto.setPhuPhi(phuPhi);
                chayNenVaLamMoi(() -> service.themKieuTrangTri(dto), "✅ Đã thêm kiểu trang trí \"" + ten + "\".");
            });
    }

    @FXML
    private void onSuaTrangTri() {
        KieuTrangTriDTO sel = tblKieuTrangTri.getSelectionModel().getSelectedItem();
        if (sel == null) { hienThiLoiLabel("Vui lòng chọn kiểu trang trí cần sửa."); return; }
        moDialogThanhPhan("Sửa Kiểu Trang Trí", "Tên trang trí:", "Phụ phí (đ):", sel.getTenTrangTri(), sel.getPhuPhi(),
            (ten, phuPhi) -> {
                sel.setTenTrangTri(ten);
                sel.setPhuPhi(phuPhi);
                chayNenVaLamMoi(() -> service.suaKieuTrangTri(sel), "✅ Đã cập nhật kiểu trang trí \"" + ten + "\".");
            });
    }

    @FXML
    private void onXoaTrangTri() {
        KieuTrangTriDTO sel = tblKieuTrangTri.getSelectionModel().getSelectedItem();
        if (sel == null) { hienThiLoiLabel("Vui lòng chọn kiểu trang trí cần xóa."); return; }
        if (xacNhanXoa("kiểu trang trí", sel.getTenTrangTri())) {
            int maNV = SessionContext.getInstance().getMaNV();
            chayNenVaLamMoi(() -> service.xoaKieuTrangTri(sel.getMaTrangTri(), maNV),
                    "✅ Đã xóa kiểu trang trí \"" + sel.getTenTrangTri() + "\".");
        }
    }

    @FXML private void onQuayLai() { quayLaiMenuChinh(tblCotBanh); }

    // ── Helpers ─────────────────────────────────────────────────────────

    /**
     * Dialog tái sử dụng cho Thêm/Sửa một thành phần bánh (tên + phụ phí).
     * Callback chỉ được gọi khi validation đã qua.
     */
    @FunctionalInterface
    private interface ThanhPhanCallback {
        void execute(String ten, BigDecimal phuPhi);
    }

    private void moDialogThanhPhan(String tieuDe, String lblTen, String lblPhi,
                                   String tenCu, BigDecimal phiCu,
                                   ThanhPhanCallback callback) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(tieuDe);
        dialog.setHeaderText(tieuDe);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        TextField txtTen = new TextField(tenCu != null ? tenCu : "");
        txtTen.setPromptText("Nhập tên...");
        txtTen.setPrefWidth(260);

        TextField txtPhi = new TextField(phiCu != null ? phiCu.toPlainString() : "");
        txtPhi.setPromptText("Ví dụ: 15000");
        txtPhi.setPrefWidth(260);

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        grid.add(new Label(lblTen), 0, 0);
        grid.add(txtTen, 1, 0);
        grid.add(new Label(lblPhi), 0, 1);
        grid.add(txtPhi, 1, 1);

        dialog.getDialogPane().setContent(grid);
        DialogHelper.applyBakeryTheme(dialog);

        // Disable OK khi tên rỗng
        Button okBtn = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        okBtn.setDisable(true);
        txtTen.textProperty().addListener((obs, o, n) -> okBtn.setDisable(n.trim().isEmpty()));
        if (tenCu != null && !tenCu.isBlank()) okBtn.setDisable(false);

        Platform.runLater(txtTen::requestFocus);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isEmpty() || result.get() != ButtonType.OK) return;

        String ten = txtTen.getText().trim();
        if (ten.isEmpty()) { hienThiLoiLabel("Tên không được để trống."); return; }

        BigDecimal phuPhi;
        try {
            phuPhi = new BigDecimal(txtPhi.getText().trim().replace(",", "").replace(".", ""));
            if (phuPhi.compareTo(BigDecimal.ZERO) < 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            hienThiLoiLabel("Phụ phí không hợp lệ. Vui lòng nhập số nguyên dương.");
            return;
        }

        callback.execute(ten, phuPhi);
    }

    /** Hiển thị dialog xác nhận xóa mềm, trả true nếu user bấm OK. */
    private boolean xacNhanXoa(String loai, String ten) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Xóa " + loai + " \"" + ten + "\"?\nDữ liệu sẽ bị ẩn khỏi hệ thống.",
                ButtonType.OK, ButtonType.CANCEL);
        confirm.setTitle("Xác nhận xóa");
        DialogHelper.applyBakeryTheme(confirm);
        return confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }

    /** Chạy tác vụ DB trên background thread, reload bảng khi xong. */
    private void chayNenVaLamMoi(ThrowingRunnable task, String successMsg) {
        Thread t = new Thread(() -> {
            try {
                task.run();
                Platform.runLater(() -> {
                    hienThiThanhCongLabel(successMsg);
                    taiDuLieu();
                });
            } catch (Exception e) {
                Platform.runLater(() -> hienThiLoiLabel("Lỗi: " + e.getMessage()));
            }
        }, "thanh-phan-banh-async");
        t.setDaemon(true);
        t.start();
    }

    @FunctionalInterface
    private interface ThrowingRunnable {
        void run() throws Exception;
    }
}
