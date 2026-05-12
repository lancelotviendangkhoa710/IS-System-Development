package com.bakery.views.controllers.hethong;

import com.bakery.model.dto.hethong.HoatDongNhanVienDTO;
import com.bakery.presenters.hethong.LichSuHeThongPresenter;
import com.bakery.utils.SessionContext;
import com.bakery.views.controllers.BaseController;
import com.bakery.views.interfaces.hethong.LichSuHeThongView;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Controller màn hình Lịch sử hệ thống.
 * CHỈ QUẢN LÝ được xem — kiểm tra vai trò ngay khi initialize().
 */
public class LichSuHeThongViewFXMLController extends BaseController implements LichSuHeThongView {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    // Mapping tên hiển thị cho cột NHOM
    private static final java.util.Map<String, String> NHOM_LABEL = java.util.Map.of(
            "DON_HANG", "Đơn hàng",
            "KHACH_HANG", "Khách hàng",
            "KHO", "Kho",
            "SAN_PHAM", "Sản phẩm");

    @FXML
    private TableView<HoatDongNhanVienDTO> tblAuditLog;
    @FXML
    private TableColumn<HoatDongNhanVienDTO, String> colThoiGian;
    @FXML
    private TableColumn<HoatDongNhanVienDTO, String> colNguoiDung;
    @FXML
    private TableColumn<HoatDongNhanVienDTO, String> colHanhDong;
    @FXML
    private TableColumn<HoatDongNhanVienDTO, String> colChiTiet;
    @FXML
    private TableColumn<HoatDongNhanVienDTO, String> colTrangThai;

    @FXML
    private TextField txtTimKiem;
    @FXML
    private ComboBox<String> cbBoLoc;

    private final LichSuHeThongPresenter presenter = new LichSuHeThongPresenter(this);

    @FXML
    public void initialize() {
        // Kiểm tra quyền — chỉ Quản lý được xem
        if (!kiemTraQuyenQuanLy())
            return;

        khoiTaoBoLoc();
        khoiTaoBang();
        presenter.taiDuLieu();
    }

    // ── LichSuHeThongView interface ──────────────────────────────────────

    @Override
    public void hienThiDanhSachHoatDong(List<HoatDongNhanVienDTO> danhSach) {
        if (danhSach == null || danhSach.isEmpty()) {
            tblAuditLog.setItems(FXCollections.observableArrayList());
            tblAuditLog.setPlaceholder(new Label("Không có dữ liệu hoạt động."));
            return;
        }
        tblAuditLog.setItems(FXCollections.observableArrayList(danhSach));
    }

    @Override
    public void batTatTrangThaiDangTai(boolean dangTai) {
        tblAuditLog.setDisable(dangTai);
        if (dangTai)
            tblAuditLog.setPlaceholder(new Label("⏳ Đang tải..."));
    }

    @Override
    public void hienThiLoi(String msg) {
        hienThiLoiLabel(msg);
    }

    @Override
    public void hienThiThanhCong(String msg) {
        hienThiThanhCongLabel(msg);
    }

    @Override
    public void xoaLoi() {
        hienThiLoiLabel("");
    }

    @Override
    public void setLoading(boolean loading) {
        batTatTrangThaiDangTai(loading);
    }

    // ── FXML handlers ────────────────────────────────────────────────────

    @FXML
    private void onTaiLai() {
        txtTimKiem.clear();
        cbBoLoc.getSelectionModel().selectFirst();
        locDuLieu();
    }

    @FXML
    private void onTimKiem() {
        locDuLieu();
    }

    // ── private helpers ──────────────────────────────────────────────────

    private void khoiTaoBang() {
        colThoiGian.setCellValueFactory(c -> {
            String val = c.getValue().getThoiGian() != null
                    ? c.getValue().getThoiGian().format(FMT)
                    : "—";
            return new SimpleStringProperty(val);
        });
        colNguoiDung.setCellValueFactory(c -> {
            HoatDongNhanVienDTO d = c.getValue();
            String ten = d.getTenNhanVien() != null ? d.getTenNhanVien() : "NV#" + d.getMaNV();
            String vt = d.getVaiTro() != null ? " (" + d.getVaiTro() + ")" : "";
            return new SimpleStringProperty(ten + vt);
        });
        colHanhDong.setCellValueFactory(
                c -> new SimpleStringProperty(NHOM_LABEL.getOrDefault(c.getValue().getNhom(), c.getValue().getNhom())));
        colChiTiet.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getHanhDong()));
        colTrangThai.setCellValueFactory(c -> {
            String entityStr = c.getValue().getEntityId() != null
                    ? "ID: " + c.getValue().getEntityId()
                    : "—";
            return new SimpleStringProperty(entityStr);
        });
    }

    private void khoiTaoBoLoc() {
        cbBoLoc.setItems(FXCollections.observableArrayList(
                "Tất cả", "Đơn hàng", "Khách hàng", "Kho", "Sản phẩm"));
        cbBoLoc.getSelectionModel().selectFirst();
        cbBoLoc.valueProperty().addListener((obs, o, n) -> locDuLieu());
    }

    private void locDuLieu() {
        String tuKhoa = txtTimKiem.getText();
        String nhom = mapNhom(cbBoLoc.getValue());
        presenter.loc(nhom, tuKhoa, null, null);
    }

    private String mapNhom(String tenHienThi) {
        return switch (tenHienThi == null ? "" : tenHienThi) {
            case "Đơn hàng" -> "DON_HANG";
            case "Khách hàng" -> "KHACH_HANG";
            case "Kho" -> "KHO";
            case "Sản phẩm" -> "SAN_PHAM";
            default -> null; // "Tất cả" → không lọc
        };
    }

    /**
     * Kiểm tra người dùng hiện tại có vai trò Quản lý không.
     * Nếu không, hiển thị thông báo từ chối và trả false.
     */
    private boolean kiemTraQuyenQuanLy() {
        SessionContext.AuthSession session = SessionContext.getCurrentSession();
        if (session == null) {
            hienThiLoiLabel("Phiên làm việc hết hạn, vui lòng đăng nhập lại.");
            return false;
        }
        String tenVaiTro = session.getTenVaiTro();
        // Cho phép nếu vai trò chứa "quan ly" hoặc "quản lý" (case insensitive, không
        // dấu)
        if (tenVaiTro != null &&
                (tenVaiTro.toLowerCase().contains("quan ly") ||
                        tenVaiTro.toLowerCase().contains("quản lý") ||
                        tenVaiTro.toLowerCase().contains("admin"))) {
            return true;
        }
        tblAuditLog.setPlaceholder(new Label("🔒 Chỉ Quản lý mới có quyền xem lịch sử hệ thống."));
        tblAuditLog.setDisable(true);
        if (txtTimKiem != null)
            txtTimKiem.setDisable(true);
        if (cbBoLoc != null)
            cbBoLoc.setDisable(true);
        hienThiLoiLabel("Bạn không có quyền truy cập chức năng này.");
        return false;
    }
}
