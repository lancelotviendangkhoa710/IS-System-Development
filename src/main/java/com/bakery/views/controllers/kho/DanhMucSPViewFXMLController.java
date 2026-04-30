package com.bakery.views.controllers.kho;

import com.bakery.model.dto.kho.DanhMucSPDTO;
import com.bakery.model.dto.khachhang.KhachHangDTO;
import com.bakery.presenters.kho.DanhMucSPPresenter;
import com.bakery.presenters.khachhang.FormKhachHangPresenter;
import com.bakery.views.interfaces.khachhang.CapNhatKhachHangView;
import com.bakery.views.interfaces.kho.IDanhMucSPView;
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

    /**
     * Controller cho màn hình Cập nhật Khách hàng.
     * Implement CapNhatKhachHangView interface - Presenter giao tiếp qua interface này.
     */
    public static class CustomerUpdateViewFXMLController extends BaoCaoViewFXMLController.AbstractCustomerController implements CapNhatKhachHangView {

        @FXML private TextField txtMaKH;
        @FXML private TextField txtHoTen;
        @FXML private TextField txtSDT;
        @FXML private TextField txtDiemTichLuy;
        @FXML private TextField txtHangThanhVien;
        @FXML private TextArea txtDiaChi;

        private boolean updated = false;
        private KhachHangDTO customer;
        private FormKhachHangPresenter presenter;

        public boolean isUpdated() { return updated; }
        public void loadCustomer(KhachHangDTO cust) { customer = cust; loadCustomerData(cust); }

        @FXML public void initialize() {
            presenter = new FormKhachHangPresenter();
        }

        @FXML private void onSaveClicked() {
            if (customer != null) {
                presenter.handleUpdateCustomer(this, customer.getMaKH());
                updated = true;
            }
        }

        @FXML private void onCancelClicked() { closeForm(); }

        @Override public String getFullName() { return txtHoTen.getText().trim(); }
        @Override public String getPhoneNumber() { return txtSDT.getText().trim(); }
        @Override public String getAddress() { return txtDiaChi.getText().trim(); }
        @Override public void loadCustomerData(KhachHangDTO cust) {
            if (cust != null) {
                txtMaKH.setText("#" + cust.getMaKH());
                txtHoTen.setText(cust.getHoTen());
                txtSDT.setText(cust.getSdt());
                txtDiaChi.setText(cust.getDiaChi());
                txtDiemTichLuy.setText(String.valueOf(cust.getDiemTichLuy()));
                String tierName = cust.getTenHang();
                txtHangThanhVien.setText(tierName == null || tierName.isEmpty() ? "-" : tierName);
            }
        }
        @Override public void clearForm() { txtHoTen.clear(); txtSDT.clear(); txtDiaChi.clear(); }
        @Override public void setFullNameError(String error) { capNhatLoiTruongNhap(txtHoTen, error); }
        @Override public void setPhoneError(String error) { capNhatLoiTruongNhap(txtSDT, error); }
        @Override public void setAddressError(String error) { capNhatLoiTruongNhap(txtDiaChi, error); }
        @Override public void showErrorAlert(String title, String message) { hienThiLoi(title, message); }
        @Override public void showSuccessAlert(String title, String message) { hienThiThanhCong(title, message); }
        @Override public void setBusy(boolean busy) { }
        @Override public void closeForm() { dongForm(); }
    }
}
