package com.bakery.views.controllers.kho;

import com.bakery.model.dto.kho.DonViTinhDTO;
import com.bakery.model.dto.kho.NguyenLieuDTO;
import com.bakery.presenters.kho.NguyenLieuPresenter;
import com.bakery.views.interfaces.kho.INguyenLieuView;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller cho QuanLyNguyenLieu.
 * Hỗ trợ Mock Data cho Demo.
 */
public class NguyenLieuViewFXMLController extends BaseController implements INguyenLieuView {

    @FXML private TableView<NguyenLieuDTO>            tblNguyenLieu;
    @FXML private TableColumn<NguyenLieuDTO, Integer> colMaNL;
    @FXML private TableColumn<NguyenLieuDTO, String>  colTenNL;
    @FXML private TableColumn<NguyenLieuDTO, String>  colXuatXu;
    @FXML private TableColumn<NguyenLieuDTO, Double>  colMucTon;

    @FXML private TextField txtTimKiem;
    @FXML private TextField txtTenNL;
    @FXML private TextField txtXuatXu;
    @FXML private TextField txtMucTonAnToan;
    @FXML private ComboBox<DonViTinhDTO> cmbDonViTinh;

    private final ObservableList<NguyenLieuDTO> masterData = FXCollections.observableArrayList();
    private NguyenLieuPresenter presenter;

    @FXML
    public void initialize() {
        setupTable();
        presenter = new NguyenLieuPresenter(this, 1);
        tblNguyenLieu.getSelectionModel().selectedItemProperty()
                .addListener((obs, old, newVal) -> hienThiChiTiet(newVal));
        presenter.khoiTao();
    }

    private void setupTable() {
        colMaNL.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getMaNL()).asObject());
        colTenNL.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTenNL()));
        colXuatXu.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getXuatXu() != null ? c.getValue().getXuatXu() : "Việt Nam"));
        colMucTon.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getMucTonAnToan()).asObject());
        tblNguyenLieu.setItems(masterData);
    }

    @Override
    public void hienThiDanhSach(List<NguyenLieuDTO> ds) {
        if (ds == null || ds.isEmpty()) {
            ds = getMockIngredients();
        }
        masterData.setAll(ds);
    }

    private List<NguyenLieuDTO> getMockIngredients() {
        List<NguyenLieuDTO> mock = new ArrayList<>();
        mock.add(createMock(1, "Bột mì đa dụng", "Việt Nam", 10.0, 1));
        mock.add(createMock(2, "Đường cát trắng", "Biên Hòa", 5.0, 1));
        mock.add(createMock(3, "Bơ lạt Anchor", "New Zealand", 2.0, 1));
        mock.add(createMock(4, "Sữa tươi không đường", "Vinamilk", 12.0, 2));
        mock.add(createMock(5, "Trứng gà ta", "CP", 100.0, 3));
        mock.add(createMock(6, "Socola chip", "Bỉ", 1.5, 1));
        return mock;
    }

    private NguyenLieuDTO createMock(int id, String name, String origin, double stock, int unitId) {
        NguyenLieuDTO nl = new NguyenLieuDTO();
        nl.setMaNL(id); nl.setTenNL(name); nl.setXuatXu(origin); nl.setMucTonAnToan(stock);
        nl.setMaDVT(unitId);
        return nl;
    }

    @Override
    public void napDanhSachDonViTinh(List<DonViTinhDTO> dsDVT) {
        if (dsDVT == null || dsDVT.isEmpty()) {
            dsDVT = List.of(
                createMockDVT(1, "Kg"),
                createMockDVT(2, "Lít"),
                createMockDVT(3, "Quả")
            );
        }
        cmbDonViTinh.setItems(FXCollections.observableArrayList(dsDVT));
    }

    private DonViTinhDTO createMockDVT(int id, String name) {
        DonViTinhDTO dvt = new DonViTinhDTO();
        dvt.setMaDVT(id);
        dvt.setTenDVT(name);
        return dvt;
    }

    @Override
    public void hienThiChiTiet(NguyenLieuDTO nl) {
        if (nl == null) return;
        txtTenNL.setText(nl.getTenNL());
        txtXuatXu.setText(nl.getXuatXu());
        txtMucTonAnToan.setText(String.valueOf(nl.getMucTonAnToan()));
    }

    @Override public void hienThiLoi(String msg) { hienThiLoiLabel(msg); }
    @Override public void hienThiThanhCong(String msg) { hienThiThanhCongLabel(msg); }
    @Override public void xoaLoi() { if (lblThongBao != null) lblThongBao.setText(""); }
    @Override public void setLoading(boolean loading) { tblNguyenLieu.setDisable(loading); }
    @Override public void lamMoiForm() { txtTenNL.clear(); txtXuatXu.clear(); txtMucTonAnToan.clear(); cmbDonViTinh.setValue(null); }
    @Override public NguyenLieuDTO getSelectedNguyenLieu() { return tblNguyenLieu.getSelectionModel().getSelectedItem(); }
    @Override public String getTenNLInput() { return txtTenNL.getText(); }
    @Override public String getXuatXuInput() { return txtXuatXu.getText(); }
    @Override public DonViTinhDTO getDonViTinhSelected() { return cmbDonViTinh.getValue(); }
    @Override public String getTuKhoaTimKiemInput() { return txtTimKiem.getText(); }
    @Override public double getMucTonAnToanInput() { try { return Double.parseDouble(txtMucTonAnToan.getText()); } catch (Exception e) { return 0; } }

    @FXML private void onThemMoi() { lblThongBao.setText("Chế độ Demo: Đã thêm nguyên liệu mới."); }
    @FXML private void onLuuThayDoi() { lblThongBao.setText("Chế độ Demo: Đã cập nhật nguyên liệu."); }
    @FXML private void onXoa() { lblThongBao.setText("Chế độ Demo: Đã xóa nguyên liệu."); }
    @FXML private void onTimKiem() { presenter.timKiem(); }
    @FXML private void onQuayLai() { quayLaiMenuChinh(tblNguyenLieu); }
}
