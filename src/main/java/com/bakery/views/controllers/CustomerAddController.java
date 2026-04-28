package com.bakery.views.controllers;

import com.bakery.presenters.CustomerFormPresenter;
import com.bakery.views.interfaces.CustomerAddView;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Controller cho màn hình Thêm Khách hàng.
 * Implement CustomerAddView interface - Presenter giao tiếp qua interface này.
 */
public class CustomerAddController implements CustomerAddView {

    @FXML private TextField txtHoTen;
    @FXML private TextField txtSDT;
    @FXML private TextArea txtDiaChi;

    private Stage stage;
    private boolean saved = false;
    private CustomerFormPresenter presenter;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public boolean isSaved() {
        return saved;
    }

    @FXML public void initialize() {
        presenter = new CustomerFormPresenter();
    }

    @FXML private void onSaveClicked() {
        presenter.handleAddCustomer(this);
        saved = true; // Flag được set nếu thành công
    }

    @FXML private void onCancelClicked() {
        closeForm();
    }

    @Override public String getFullName() { return txtHoTen.getText().trim(); }
    @Override public String getPhoneNumber() { return txtSDT.getText().trim(); }
    @Override public String getAddress() { return txtDiaChi.getText().trim(); }
    @Override public void clearForm() { txtHoTen.clear(); txtSDT.clear(); txtDiaChi.clear(); }
    @Override public void setFullNameError(String error) { txtHoTen.setStyle(error != null ? "-fx-border-color: red;" : ""); }
    @Override public void setPhoneError(String error) { txtSDT.setStyle(error != null ? "-fx-border-color: red;" : ""); }
    @Override public void setAddressError(String error) { txtDiaChi.setStyle(error != null ? "-fx-border-color: red;" : ""); }
    @Override public void showErrorAlert(String title, String message) { new Alert(Alert.AlertType.ERROR, message).showAndWait(); }
    @Override public void showSuccessAlert(String title, String message) { new Alert(Alert.AlertType.INFORMATION, message).showAndWait(); }
    @Override public void setBusy(boolean busy) { }
    @Override public void closeForm() { if (stage != null) stage.close(); }
}