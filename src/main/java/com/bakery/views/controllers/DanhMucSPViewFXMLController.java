package com.bakery.views.controllers;

import com.bakery.model.dto.DanhMucSPDTO;
import com.bakery.presenters.DanhMucSPPresenter;
import com.bakery.views.interfaces.IDanhMucSPView;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.util.List;

public class DanhMucSPViewFXMLController extends BaseController implements IDanhMucSPView {

    @FXML private TableView<DanhMucSPDTO> tblDanhMuc;
    @FXML private TableColumn<DanhMucSPDTO, Integer> colMaDM;
    @FXML private TableColumn<DanhMucSPDTO, String> colTenDM;

    @FXML private TextField txtTenDanhMuc;
    @FXML private TextField txtTimKiem;
    
    @FXML private Button btnThemMoi;
    @FXML private Button btnLuuThayDoi;
    @FXML private Button btnXoa;

    private final ObservableList<DanhMucSPDTO> masterData = FXCollections.observableArrayList();
    private DanhMucSPPresenter presenter;

    @FXML
    public void initialize() {
        setupTable();
        
        // Khởi tạo Presenter, tạm dùng mã NV = 1 cho đến khi tích hợp SessionManager
        presenter = new DanhMucSPPresenter(this, 1);
        
        setupSelectionListener();
        
        // Mặc định ban đầu
        lamMoiForm();
        presenter.taiDanhSach();
    }

    private void setupTable() {
        colMaDM.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getMaDM()).asObject());
        colTenDM.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTenDM()));
        
        tblDanhMuc.setItems(masterData);
    }

    private void setupSelectionListener() {
        tblDanhMuc.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (presenter != null) {
                presenter.onChonDanhMuc(newSelection);
            }
        });
    }

    // ─── Thực thi Interface IDanhMucSPView ──────────────────────────────

    @Override
    public void hienThiDanhSach(List<DanhMucSPDTO> ds) {
        masterData.setAll(ds);
    }

    @Override
    public void hienThiChiTiet(DanhMucSPDTO dm) {
        if (dm != null) {
            txtTenDanhMuc.setText(dm.getTenDM());
            btnLuuThayDoi.setDisable(false);
            btnXoa.setDisable(false);
        }
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
    public void lamMoiForm() {
        txtTenDanhMuc.clear();
        tblDanhMuc.getSelectionModel().clearSelection();
        btnLuuThayDoi.setDisable(true);
        btnXoa.setDisable(true);
        lblThongBao.setText("");
    }

    @Override
    public DanhMucSPDTO getSelectedCategory() {
        return tblDanhMuc.getSelectionModel().getSelectedItem();
    }

    @Override
    public String getTenDanhMucInput() {
        return txtTenDanhMuc.getText().trim();
    }

    @Override
    public String getTuKhoaTimKiemInput() {
        return txtTimKiem.getText().trim();
    }

    // ─── FXML Actions ───────────────────────────────────────────────────

    @FXML
    private void onThemMoi() {
        presenter.themDanhMuc();
    }

    @FXML
    private void onLuuThayDoi() {
        presenter.suaDanhMuc();
    }

    @FXML
    private void onXoa() {
        presenter.xoaDanhMuc();
    }

    @FXML
    private void onTimKiem() {
        presenter.timKiem();
    }

    @FXML
    private void onQuayLai() {
        quayLaiMenuChinh(tblDanhMuc);
    }
}
