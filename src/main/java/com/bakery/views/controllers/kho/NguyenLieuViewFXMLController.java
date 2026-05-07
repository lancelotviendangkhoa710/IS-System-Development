package com.bakery.views.controllers.kho;

import com.bakery.model.dto.kho.DonViTinhDTO;
import com.bakery.model.dto.kho.NguyenLieuDTO;
import com.bakery.presenters.kho.NguyenLieuPresenter;
import com.bakery.views.controllers.BaseController;
import com.bakery.views.interfaces.kho.INguyenLieuView;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.util.List;



/**
 * Controller cho QuanLyNguyenLieu.
 * Mọi dữ liệu được lấy từ DB. Không có Mock Data.
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
            masterData.clear();
            hienThiLoi("Không có dữ liệu nguyên liệu.");
            return;
        }
        masterData.setAll(ds);
    }

    @Override
    public void napDanhSachDonViTinh(List<DonViTinhDTO> dsDVT) {
        if (dsDVT == null || dsDVT.isEmpty()) {
            return;
        }
        cmbDonViTinh.setItems(FXCollections.observableArrayList(dsDVT));
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

    @FXML private void onThemMoi()     { if (presenter != null) presenter.themNguyenLieu(); }
    @FXML private void onLuuThayDoi() { if (presenter != null) presenter.suaNguyenLieu(); }
    @FXML private void onXoa()         { if (presenter != null) presenter.xoaNguyenLieu(); }
    @FXML private void onTimKiem()     { if (presenter != null) presenter.timKiem(); }
    @FXML private void onLamMoi()      { if (presenter != null) presenter.taiDanhSach(); }
    @FXML private void onQuayLai()     { quayLaiMenuChinh(tblNguyenLieu); }
}
