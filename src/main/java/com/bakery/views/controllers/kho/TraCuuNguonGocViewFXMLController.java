package com.bakery.views.controllers.kho;

import com.bakery.model.dto.kho.MeSanXuatDTO;
import com.bakery.model.dto.kho.TraCuuNguonGocDTO;
import com.bakery.presenters.kho.TraCuuNguonGocPresenter;
import com.bakery.views.interfaces.kho.ITraCuuNguonGocView;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Controller màn hình Truy xuất nguồn gốc nguyên liệu.
 * Chỉ bắt sự kiện và cập nhật UI — không chứa logic nghiệp vụ.
 */
public class TraCuuNguonGocViewFXMLController implements ITraCuuNguonGocView {

    private static final DateTimeFormatter FMT_DT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter FMT_DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // ── Bộ lọc ────────────────────────────────────────────────────────────────
    @FXML private TextField txtTimKiem;
    @FXML private DatePicker dpTuNgay;
    @FXML private DatePicker dpDenNgay;
    @FXML private Button btnTimKiem;
    @FXML private Button btnXoaLoc;

    // ── Bảng mẻ sản xuất (trái) ───────────────────────────────────────────────
    @FXML private TableView<MeSanXuatDTO> tblMe;
    @FXML private TableColumn<MeSanXuatDTO, Integer> colMaMe;
    @FXML private TableColumn<MeSanXuatDTO, String>  colTenSP;
    @FXML private TableColumn<MeSanXuatDTO, String>  colSoLuong;
    @FXML private TableColumn<MeSanXuatDTO, String>  colNgaySX;
    @FXML private TableColumn<MeSanXuatDTO, String>  colNhanVien;

    // ── Bảng chi tiết lô NL (phải) ────────────────────────────────────────────
    @FXML private TableView<TraCuuNguonGocDTO> tblChiTiet;
    @FXML private TableColumn<TraCuuNguonGocDTO, String>  colTenNL;
    @FXML private TableColumn<TraCuuNguonGocDTO, String>  colSoLuongDung;
    @FXML private TableColumn<TraCuuNguonGocDTO, Integer> colMaLo;
    @FXML private TableColumn<TraCuuNguonGocDTO, String>  colMaVach;
    @FXML private TableColumn<TraCuuNguonGocDTO, String>  colNSX;
    @FXML private TableColumn<TraCuuNguonGocDTO, String>  colHSD;
    @FXML private TableColumn<TraCuuNguonGocDTO, String>  colNCC;
    @FXML private TableColumn<TraCuuNguonGocDTO, String>  colSDT;

    // ── Footer ─────────────────────────────────────────────────────────────────
    @FXML private Label lblThongBao;

    private TraCuuNguonGocPresenter presenter;

    @FXML
    public void initialize() {
        presenter = new TraCuuNguonGocPresenter(this);
        cauHinhBangMe();
        cauHinhBangChiTiet();

        // Chọn hàng mẻ → nạp chi tiết NL
        tblMe.getSelectionModel().selectedItemProperty().addListener(
            (obs, cu, selected) -> presenter.onChonMe(selected)
        );

        presenter.khoiTao();
    }

    // ── Cài đặt bảng ──────────────────────────────────────────────────────────

    private void cauHinhBangMe() {
        colMaMe.setCellValueFactory(new PropertyValueFactory<>("maMe"));
        colTenSP.setCellValueFactory(new PropertyValueFactory<>("tenSP"));
        colSoLuong.setCellValueFactory(c ->
            new SimpleStringProperty(String.valueOf((int) c.getValue().getSoLuongSanXuat()) + " cái"));
        colNgaySX.setCellValueFactory(c ->
            new SimpleStringProperty(c.getValue().getNgaySanXuat() != null
                ? c.getValue().getNgaySanXuat().format(FMT_DT) : "—"));
        colNhanVien.setCellValueFactory(new PropertyValueFactory<>("tenNhanVien"));
    }

    private void cauHinhBangChiTiet() {
        colTenNL.setCellValueFactory(new PropertyValueFactory<>("tenNguyenLieu"));
        colSoLuongDung.setCellValueFactory(c ->
            new SimpleStringProperty(String.format("%.3f", c.getValue().getSoLuongDaDung())));
        colMaLo.setCellValueFactory(new PropertyValueFactory<>("maLo"));
        colMaVach.setCellValueFactory(c ->
            new SimpleStringProperty(c.getValue().getMaVachLo() != null ? c.getValue().getMaVachLo() : "—"));
        colNSX.setCellValueFactory(c ->
            new SimpleStringProperty(c.getValue().getNsxNguyenLieu() != null
                ? c.getValue().getNsxNguyenLieu().format(FMT_DATE) : "—"));
        colHSD.setCellValueFactory(c ->
            new SimpleStringProperty(c.getValue().getHanSuDung() != null
                ? c.getValue().getHanSuDung().format(FMT_DATE) : "—"));
        colNCC.setCellValueFactory(new PropertyValueFactory<>("tenNCC"));
        colSDT.setCellValueFactory(new PropertyValueFactory<>("sdtNCC"));
    }

    // ── Sự kiện nút ────────────────────────────────────────────────────────────

    @FXML
    private void onTimKiem() {
        LocalDate tuNgay = dpTuNgay.getValue();
        LocalDate denNgay = dpDenNgay.getValue();
        presenter.onTimKiem(tuNgay, denNgay);
    }

    @FXML
    private void onXoaLoc() {
        txtTimKiem.clear();
        dpTuNgay.setValue(null);
        dpDenNgay.setValue(null);
        presenter.khoiTao();
    }

    // ── ITraCuuNguonGocView ─────────────────────────────────────────────────────

    @Override
    public void hienThiDanhSachMe(List<MeSanXuatDTO> danhSach) {
        tblMe.getItems().setAll(danhSach);
        tblChiTiet.getItems().clear();
    }

    @Override
    public void hienThiChiTietNguonGoc(List<TraCuuNguonGocDTO> chiTiet) {
        tblChiTiet.getItems().setAll(chiTiet);
    }

    @Override
    public void hienThiThongBao(String thongBao) {
        lblThongBao.setText(thongBao);
        lblThongBao.getStyleClass().removeAll("lbl-danger");
        if (!lblThongBao.getStyleClass().contains("lbl-success")) {
            lblThongBao.getStyleClass().add("lbl-success");
        }
    }

    // ── IBaseView ──────────────────────────────────────────────────────────────

    @Override
    public void hienThiLoi(String msg) {
        lblThongBao.setText("⚠ " + msg);
        lblThongBao.getStyleClass().removeAll("lbl-success");
        if (!lblThongBao.getStyleClass().contains("lbl-danger")) {
            lblThongBao.getStyleClass().add("lbl-danger");
        }
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
        tblMe.setDisable(loading);
    }

    @Override
    public String getTuKhoaInput() {
        return txtTimKiem.getText();
    }
}
