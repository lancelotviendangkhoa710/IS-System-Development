package com.bakery.views.controllers.hethong;

import com.bakery.model.dto.hethong.KhoiPhucDuLieuDTO;
import com.bakery.presenters.hethong.KhoiPhucDuLieuPresenter;
import com.bakery.services.hethong.KhoiPhucDuLieuService;
import com.bakery.utils.SessionContext;
import com.bakery.utils.DialogHelper;
import com.bakery.views.controllers.BaseController;
import com.bakery.views.interfaces.hethong.IKhoiPhucDuLieuView;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Controller màn hình Khôi phục dữ liệu (UC60).
 * Chỉ Quản lý mới có quyền truy cập.
 */
public class KhoiPhucDuLieuViewFXMLController extends BaseController implements IKhoiPhucDuLieuView {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final int NGUONG_NGAY = KhoiPhucDuLieuService.NGUONG_NGAY_XOA;

    // ─── FXML bindings ────────────────────────────────────────────────────────
    @FXML private TableView<KhoiPhucDuLieuDTO>              tblDanhSach;
    @FXML private TableColumn<KhoiPhucDuLieuDTO, String>    colLoai;
    @FXML private TableColumn<KhoiPhucDuLieuDTO, String>    colTen;
    @FXML private TableColumn<KhoiPhucDuLieuDTO, String>    colMa;
    @FXML private TableColumn<KhoiPhucDuLieuDTO, String>    colThoiDiemXoa;
    @FXML private TableColumn<KhoiPhucDuLieuDTO, String>    colNguoiXoa;
    @FXML private TableColumn<KhoiPhucDuLieuDTO, String>    colConLai;

    @FXML private ComboBox<String>  cmbLoai;
    @FXML private TextField         txtTimKiem;
    @FXML private Label             lblSoBanGhi;
    @FXML private Button            btnKhoiPhuc;
    @FXML private Button            btnXoaVinhVien;

    // ─── State ────────────────────────────────────────────────────────────────
    private final KhoiPhucDuLieuPresenter presenter = new KhoiPhucDuLieuPresenter(this);
    private final ObservableList<KhoiPhucDuLieuDTO> masterData  = FXCollections.observableArrayList();
    private FilteredList<KhoiPhucDuLieuDTO> filteredData;

    // ─── Initialize ───────────────────────────────────────────────────────────

    @FXML
    public void initialize() {
        if (!kiemTraQuyenQuanLy()) return;

        khoiTaoBang();
        khoiTaoBoLoc();
        // Tự động purge ngay khi mở màn hình — xóa vĩnh viễn bản ghi > 120 ngày
        presenter.tuDongXoaQuaHan(loaiBoLocHienTai());
        // Auto-refresh mỗi 60s: purge ngầm rồi reload danh sách
        batDauAutoRefresh(tblDanhSach, () -> presenter.tuDongXoaQuaHan(loaiBoLocHienTai()), 60);
    }

    // ─── IKhoiPhucDuLieuView ─────────────────────────────────────────────────

    @Override
    public void hienThiDanhSach(List<KhoiPhucDuLieuDTO> danhSach) {
        masterData.setAll(danhSach != null ? danhSach : List.of());
        apDungBoLoc();
        capNhatSoBanGhi();
    }

    @Override
    public void setLoading(boolean loading) {
        tblDanhSach.setDisable(loading);
        if (btnKhoiPhuc  != null) btnKhoiPhuc.setDisable(loading);
        if (btnXoaVinhVien != null) btnXoaVinhVien.setDisable(loading);
        if (loading) tblDanhSach.setPlaceholder(new Label("⏳ Đang tải..."));
    }

    @Override
    public void hienThiLoi(String msg) { hienThiLoiLabel(msg); }

    @Override
    public void hienThiThanhCong(String msg) { hienThiThanhCongLabel(msg); }

    @Override
    public void xoaLoi() { hienThiLoiLabel(""); }

    // ─── FXML handlers ────────────────────────────────────────────────────────

    @FXML
    private void onTaiLai() {
        txtTimKiem.clear();
        cmbLoai.getSelectionModel().selectFirst();
        presenter.taiDuLieu();
    }

    @FXML
    private void onLocDuLieu() {
        presenter.loc(loaiBoLocHienTai());
    }

    @FXML
    private void onTimKiem() {
        apDungBoLoc();
        capNhatSoBanGhi();
    }

    @FXML
    private void onKhoiPhuc() {
        KhoiPhucDuLieuDTO chon = tblDanhSach.getSelectionModel().getSelectedItem();
        if (chon == null) {
            hienThiLoi("Vui lòng chọn bản ghi cần khôi phục.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Khôi phục \"" + chon.getTenDoiTuong() + "\" (" + chon.getLoaiDoiTuong() + ")?\n"
                + "Bản ghi sẽ hoạt động trở lại trong hệ thống.",
                ButtonType.YES, ButtonType.NO);
        confirm.setTitle("Xác nhận khôi phục");
        confirm.setHeaderText(null);
        DialogHelper.applyBakeryTheme(confirm);
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.YES) {
                presenter.khoiPhuc(chon, loaiBoLocHienTai());
            }
        });
    }

    @FXML
    private void onXoaVinhVien() {
        Alert confirm = new Alert(Alert.AlertType.WARNING,
                "Thao tác này sẽ XÓA VĨNH VIỄN tất cả bản ghi đã xóa mềm hơn "
                + NGUONG_NGAY + " ngày.\nHành động KHÔNG THỂ hoàn tác!\n\nBạn có chắc chắn?",
                ButtonType.YES, ButtonType.NO);
        confirm.setTitle("⚠ Xóa vĩnh viễn dữ liệu quá hạn");
        confirm.setHeaderText("Cảnh báo: Xóa vĩnh viễn");
        DialogHelper.applyBakeryTheme(confirm);
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.YES) {
                presenter.xoaVinhVienQuaHan(loaiBoLocHienTai());
            }
        });
    }

    // ─── Private helpers ──────────────────────────────────────────────────────

    private void khoiTaoBang() {
        colLoai.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getLoaiDoiTuong()));

        colTen.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTenDoiTuong()));

        colMa.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getMaDoiTuong()));

        colThoiDiemXoa.setCellValueFactory(c -> {
            LocalDateTime thoiDiem = c.getValue().getThoiDiemXoa();
            return new SimpleStringProperty(thoiDiem != null ? thoiDiem.format(FMT) : "—");
        });

        colNguoiXoa.setCellValueFactory(c -> {
            String ten = c.getValue().getTenNhanVienXoa();
            return new SimpleStringProperty(ten != null ? ten : "—");
        });

        // Cột "Ngày còn lại" trước khi bị purge tự động (NGUONG_NGAY - soNgayDaXoa)
        colConLai.setCellValueFactory(c -> {
            LocalDateTime thoiDiem = c.getValue().getThoiDiemXoa();
            if (thoiDiem == null) return new SimpleStringProperty("—");
            long soNgayCuDa = ChronoUnit.DAYS.between(thoiDiem, LocalDateTime.now());
            long conLai = NGUONG_NGAY - soNgayCuDa;
            if (conLai <= 0) return new SimpleStringProperty("⚠ Quá hạn");
            return new SimpleStringProperty(conLai + " ngày");
        });

        // Tô màu hàng quá hạn
        tblDanhSach.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(KhoiPhucDuLieuDTO item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setStyle("");
                    return;
                }
                LocalDateTime thoiDiem = item.getThoiDiemXoa();
                if (thoiDiem != null) {
                    long soNgay = ChronoUnit.DAYS.between(thoiDiem, LocalDateTime.now());
                    if (soNgay >= NGUONG_NGAY) {
                        setStyle("-fx-background-color: #FFF3E0;"); // cam nhạt = sắp bị purge
                    } else if (soNgay >= NGUONG_NGAY - 14) {
                        setStyle("-fx-background-color: #FFF9C4;"); // vàng nhạt = cảnh báo
                    } else {
                        setStyle("");
                    }
                } else {
                    setStyle("");
                }
            }
        });

        filteredData = new FilteredList<>(masterData, p -> true);
        tblDanhSach.setItems(filteredData);
    }

    private void khoiTaoBoLoc() {
        cmbLoai.setItems(FXCollections.observableArrayList(
                presenter.layDanhSachLoai()
        ));
        cmbLoai.getSelectionModel().selectFirst();
    }

    /** Áp dụng filter theo ô tìm kiếm (client-side, không query DB). */
    private void apDungBoLoc() {
        String keyword = txtTimKiem.getText() == null ? "" : txtTimKiem.getText().trim().toLowerCase();
        filteredData.setPredicate(dto -> {
            if (keyword.isBlank()) return true;
            String ten = dto.getTenDoiTuong() == null ? "" : dto.getTenDoiTuong().toLowerCase();
            String loai = dto.getLoaiDoiTuong() == null ? "" : dto.getLoaiDoiTuong().toLowerCase();
            return ten.contains(keyword) || loai.contains(keyword);
        });
    }

    private void capNhatSoBanGhi() {
        int hienThi = filteredData != null ? filteredData.size() : 0;
        int tong    = masterData.size();
        if (lblSoBanGhi != null) {
            lblSoBanGhi.setText("Hiển thị " + hienThi + " / " + tong + " bản ghi đã xóa");
        }
    }

    private String loaiBoLocHienTai() {
        return cmbLoai.getSelectionModel().getSelectedItem();
    }


    /** Kiểm tra quyền Quản lý. */
    private boolean kiemTraQuyenQuanLy() {
        SessionContext.AuthSession session = SessionContext.getCurrentSession();
        if (session == null) {
            hienThiLoiLabel("Phiên làm việc hết hạn, vui lòng đăng nhập lại.");
            return false;
        }
        String tenVaiTro = session.getTenVaiTro();
        if (tenVaiTro != null && (
                tenVaiTro.toLowerCase().contains("quan ly")
                || tenVaiTro.toLowerCase().contains("quản lý")
                || tenVaiTro.toLowerCase().contains("admin"))) {
            return true;
        }
        if (tblDanhSach != null) {
            tblDanhSach.setPlaceholder(new Label("🔒 Chỉ Quản lý mới có quyền sử dụng chức năng này."));
            tblDanhSach.setDisable(true);
        }
        hienThiLoiLabel("Bạn không có quyền truy cập chức năng Khôi phục dữ liệu.");
        return false;
    }
}
