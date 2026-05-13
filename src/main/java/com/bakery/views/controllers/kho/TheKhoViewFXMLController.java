package com.bakery.views.controllers.kho;

import com.bakery.model.dto.kho.NguyenLieuDTO;
import com.bakery.model.dto.kho.TheKhoBienDongDTO;
import com.bakery.presenters.kho.TheKhoPresenter;
import com.bakery.views.interfaces.kho.ITheKhoView;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

/**
 * Controller Tra cứu thẻ kho nguyên liệu (UC44).
 * Chỉ bắt sự kiện và cập nhật UI — không chứa logic nghiệp vụ.
 */
public class TheKhoViewFXMLController implements ITheKhoView {

    private static final DateTimeFormatter FMT_DT  = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final NumberFormat      FMT_NUM =
            NumberFormat.getNumberInstance(Locale.of("vi", "VN"));
    static { FMT_NUM.setMaximumFractionDigits(3); }

    // ── Bộ lọc ────────────────────────────────────────────────────────────────
    @FXML private ComboBox<NguyenLieuDTO> cmbNguyenLieu;
    @FXML private DatePicker dpTuNgay;
    @FXML private DatePicker dpDenNgay;
    @FXML private Button btnTimKiem;
    @FXML private Button btnXoaLoc;

    // ── KPI tổng hợp kỳ ───────────────────────────────────────────────────────
    @FXML private Label lblTonDauKy;
    @FXML private Label lblNhapKy;
    @FXML private Label lblXuatKy;
    @FXML private Label lblTonCuoiKy;

    // ── Bảng lịch sử biến động ────────────────────────────────────────────────
    @FXML private TableView<TheKhoBienDongDTO>             tblBienDong;
    @FXML private TableColumn<TheKhoBienDongDTO, String>   colNgay;
    @FXML private TableColumn<TheKhoBienDongDTO, String>   colLoai;
    @FXML private TableColumn<TheKhoBienDongDTO, Integer>  colMaLo;
    @FXML private TableColumn<TheKhoBienDongDTO, String>   colSoLuong;
    @FXML private TableColumn<TheKhoBienDongDTO, String>   colConLai;

    // ── Footer ────────────────────────────────────────────────────────────────
    @FXML private Label lblThongBao;

    private TheKhoPresenter presenter;

    @FXML
    public void initialize() {
        presenter = new TheKhoPresenter(this);
        cauHinhComboBox();
        cauHinhBang();
        presenter.khoiTao();
    }

    // ── Cài đặt ComboBox ──────────────────────────────────────────────────────

    private void cauHinhComboBox() {
        cmbNguyenLieu.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(NguyenLieuDTO item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null
                        : item.getTenNL() + " (Tồn: " + FMT_NUM.format(item.getSoLuongTonTong()) + ")");
            }
        });
        cmbNguyenLieu.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(NguyenLieuDTO item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "— Chọn nguyên liệu —" : item.getTenNL());
            }
        });
    }

    // ── Cài đặt bảng ─────────────────────────────────────────────────────────

    private void cauHinhBang() {
        colNgay.setCellValueFactory(c ->
            new SimpleStringProperty(c.getValue().getNgayGiaoDich() != null
                ? c.getValue().getNgayGiaoDich().format(FMT_DT) : "—"));

        colLoai.setCellValueFactory(c ->
            new SimpleStringProperty(c.getValue().getLoaiGiaoDich() != null
                ? c.getValue().getLoaiGiaoDich() : "—"));

        colMaLo.setCellValueFactory(new PropertyValueFactory<>("maLo"));

        colSoLuong.setCellValueFactory(c -> {
            double sl = c.getValue().getSoLuong();
            String prefix = sl >= 0 ? "+" : "";
            return new SimpleStringProperty(prefix + FMT_NUM.format(sl));
        });

        colConLai.setCellValueFactory(c ->
            new SimpleStringProperty(c.getValue().getSoLuong() >= 0
                ? FMT_NUM.format(c.getValue().getSoLuongConLai()) : "—"));

        // Highlight màu: nhập = xanh (row-done), xuất = đỏ (row-danger)
        tblBienDong.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(TheKhoBienDongDTO item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    getStyleClass().removeAll("row-done", "row-danger");
                } else if (item.getSoLuong() >= 0) {
                    getStyleClass().removeAll("row-danger");
                    if (!getStyleClass().contains("row-done")) getStyleClass().add("row-done");
                } else {
                    getStyleClass().removeAll("row-done");
                    if (!getStyleClass().contains("row-danger")) getStyleClass().add("row-danger");
                }
            }
        });

        tblBienDong.setPlaceholder(new Label("Chọn nguyên liệu và nhấn 'Xem thẻ kho'."));
    }

    // ── Sự kiện nút ──────────────────────────────────────────────────────────

    @FXML
    private void onTimKiem() {
        presenter.onTimKiem();
    }

    @FXML
    private void onXoaLoc() {
        dpTuNgay.setValue(null);
        dpDenNgay.setValue(null);
        presenter.onXoaLoc();
    }

    // ── ITheKhoView ──────────────────────────────────────────────────────────

    @Override
    public void hienThiBienDong(List<TheKhoBienDongDTO> bienDong) {
        tblBienDong.setItems(FXCollections.observableArrayList(bienDong));
    }

    @Override
    public void hienThiTongHop(double tonDauKy, double nhapKy, double xuatKy, double tonCuoiKy) {
        lblTonDauKy.setText(FMT_NUM.format(tonDauKy));
        lblNhapKy.setText("+" + FMT_NUM.format(nhapKy));
        lblXuatKy.setText("-" + FMT_NUM.format(xuatKy));
        // Tô màu tồn cuối kỳ theo ngưỡng
        lblTonCuoiKy.setText(FMT_NUM.format(tonCuoiKy));
        lblTonCuoiKy.getStyleClass().removeAll("lbl-danger", "lbl-success");
        lblTonCuoiKy.getStyleClass().add(tonCuoiKy <= 0 ? "lbl-danger" : "lbl-success");
    }

    @Override
    public void napDanhSachNguyenLieu(List<NguyenLieuDTO> danhSach) {
        cmbNguyenLieu.setItems(FXCollections.observableArrayList(danhSach));
        if (!danhSach.isEmpty()) cmbNguyenLieu.getSelectionModel().selectFirst();
    }

    @Override
    public NguyenLieuDTO getNguyenLieuDangChon() {
        return cmbNguyenLieu.getValue();
    }

    @Override
    public LocalDate getTuNgay() {
        return dpTuNgay.getValue();
    }

    @Override
    public LocalDate getDenNgay() {
        return dpDenNgay.getValue();
    }

    // ── IBaseView ─────────────────────────────────────────────────────────────

    @Override
    public void hienThiLoi(String msg) {
        lblThongBao.setText("⚠ " + msg);
        lblThongBao.getStyleClass().removeAll("lbl-success");
        if (!lblThongBao.getStyleClass().contains("lbl-danger"))
            lblThongBao.getStyleClass().add("lbl-danger");
    }

    @Override
    public void hienThiThanhCong(String msg) {
        lblThongBao.setText(msg);
        lblThongBao.getStyleClass().removeAll("lbl-danger");
        if (!lblThongBao.getStyleClass().contains("lbl-success"))
            lblThongBao.getStyleClass().add("lbl-success");
    }

    @Override
    public void xoaLoi() {
        lblThongBao.setText("");
        lblThongBao.getStyleClass().removeAll("lbl-success", "lbl-danger");
    }

    @Override
    public void setLoading(boolean loading) {
        btnTimKiem.setDisable(loading);
        btnXoaLoc.setDisable(loading);
        cmbNguyenLieu.setDisable(loading);
        tblBienDong.setDisable(loading);
    }
}
