package com.bakery.views.controllers;

import com.bakery.model.dto.DonViTinhDTO;
import com.bakery.model.dto.NguyenLieuDTO;
import com.bakery.presenters.NguyenLieuPresenter;
import com.bakery.views.interfaces.INguyenLieuView;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;

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

    @FXML private Button btnThemMoi;
    @FXML private Button btnLuuThayDoi;
    @FXML private Button btnXoa;

    private final ObservableList<NguyenLieuDTO> masterData = FXCollections.observableArrayList();
    private NguyenLieuPresenter presenter;

    @FXML
    public void initialize() {
        setupTable();
        presenter = new NguyenLieuPresenter(this, 1);
        tblNguyenLieu.getSelectionModel().selectedItemProperty()
                .addListener((obs, old, newVal) -> { if (presenter != null) presenter.onChonNguyenLieu(newVal); });
        lamMoiForm();
        presenter.khoiTao();
    }

    private void setupTable() {
        colMaNL.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getMaNL()).asObject());
        colTenNL.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTenNL()));
        colXuatXu.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getXuatXu() != null ? c.getValue().getXuatXu() : ""));
        colMucTon.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getMucTonAnToan()).asObject());
        tblNguyenLieu.setItems(masterData);
    }

    // ─── INguyenLieuView ─────────────────────────────────────

    @Override public void hienThiDanhSach(List<NguyenLieuDTO> ds) { masterData.setAll(ds); }

    @Override
    public void napDanhSachDonViTinh(List<DonViTinhDTO> dsDVT) {
        cmbDonViTinh.setItems(FXCollections.observableArrayList(dsDVT));
    }

    @Override
    public void hienThiChiTiet(NguyenLieuDTO nl) {
        if (nl == null) return;
        txtTenNL.setText(nl.getTenNL());
        txtXuatXu.setText(nl.getXuatXu() != null ? nl.getXuatXu() : "");
        txtMucTonAnToan.setText(String.valueOf(nl.getMucTonAnToan()));
        cmbDonViTinh.getItems().stream()
                .filter(dvt -> dvt.getMaDVT() == nl.getMaDVT())
                .findFirst().ifPresent(cmbDonViTinh::setValue);
        btnLuuThayDoi.setDisable(false);
        btnXoa.setDisable(false);
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
        if (lblThongBao != null) lblThongBao.setText("");
    }

    @Override
    public void setLoading(boolean loading) {
        tblNguyenLieu.setDisable(loading);
    }

    @Override
    public void lamMoiForm() {
        txtTenNL.clear(); txtXuatXu.clear(); txtMucTonAnToan.clear();
        cmbDonViTinh.setValue(null);
        tblNguyenLieu.getSelectionModel().clearSelection();
        btnLuuThayDoi.setDisable(true);
        btnXoa.setDisable(true);
        xoaLoi();
    }

    @Override public NguyenLieuDTO  getSelectedNguyenLieu()  { return tblNguyenLieu.getSelectionModel().getSelectedItem(); }
    @Override public String          getTenNLInput()           { return txtTenNL.getText().trim(); }
    @Override public String          getXuatXuInput()          { return txtXuatXu.getText().trim(); }
    @Override public DonViTinhDTO    getDonViTinhSelected()    { return cmbDonViTinh.getValue(); }
    @Override public String          getTuKhoaTimKiemInput()   { return txtTimKiem.getText().trim(); }

    @Override
    public double getMucTonAnToanInput() {
        try { return Double.parseDouble(txtMucTonAnToan.getText().trim()); }
        catch (NumberFormatException e) { return -1; }
    }

    // ─── FXML Actions ────────────────────────────────────────

    @FXML private void onThemMoi()    { presenter.themNguyenLieu(); }
    @FXML private void onLuuThayDoi() { presenter.suaNguyenLieu(); }
    @FXML private void onXoa()        { presenter.xoaNguyenLieu(); }
    @FXML private void onTimKiem()    { presenter.timKiem(); }

    @FXML
    private void onQuayLai() {
        quayLaiMenuChinh(tblNguyenLieu);
    }
}
