package com.bakery.views.controllers;

import com.bakery.models.dto.KhachHangDTO;
import com.bakery.presenters.CustomerFormPresenter;
import com.bakery.views.interfaces.CustomerUpdateView;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Controller cho màn hình Cập nhật Khách hàng.
 * Implement CustomerUpdateView interface - Presenter giao tiếp qua interface này.
 */
public class CustomerUpdateController implements CustomerUpdateView {

    @FXML private TextField txtMaKH;
    @FXML private TextField txtHoTen;
    @FXML private TextField txtSDT;
    @FXML private TextField txtDiemTichLuy;
    @FXML private TextField txtHangThanhVien;
    @FXML private TextArea txtDiaChi;

    private Stage stage;
    private boolean updated = false;
    private KhachHangDTO customer;
    private CustomerFormPresenter presenter;

    public void setStage(Stage stage) { this.stage = stage; }
    public boolean isUpdated() { return updated; }
    public void loadCustomer(KhachHangDTO cust) { customer = cust; loadCustomerData(cust); }

    @FXML public void initialize() {
        presenter = new CustomerFormPresenter();
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
    @Override public void setFullNameError(String error) { txtHoTen.setStyle(error != null ? "-fx-border-color: red;" : ""); }
    @Override public void setPhoneError(String error) { txtSDT.setStyle(error != null ? "-fx-border-color: red;" : ""); }
    @Override public void setAddressError(String error) { txtDiaChi.setStyle(error != null ? "-fx-border-color: red;" : ""); }
    @Override public void showErrorAlert(String title, String message) { new Alert(Alert.AlertType.ERROR, message).showAndWait(); }
    @Override public void showSuccessAlert(String title, String message) { new Alert(Alert.AlertType.INFORMATION, message).showAndWait(); }
    @Override public void setBusy(boolean busy) { }
    @Override public void closeForm() { if (stage != null) stage.close(); }
}