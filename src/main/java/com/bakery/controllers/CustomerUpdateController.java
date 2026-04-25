package com.bakery.controllers;

import com.bakery.models.dto.KhachHangDTO;
import com.bakery.services.CustomerService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.SQLException;

public class CustomerUpdateController {
    @FXML private TextField txtMaKH;
    @FXML private TextField txtHoTen;
    @FXML private TextField txtSDT;
    @FXML private TextField txtDiemTichLuy;
    @FXML private TextField txtHangThanhVien;
    @FXML private TextArea txtDiaChi;

    private Stage stage;
    private boolean updated = false;
    private KhachHangDTO customer;

    private final CustomerService customerService = new CustomerService();

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public boolean isUpdated() {
        return updated;
    }

    public void setCustomer(KhachHangDTO customer) {
        this.customer = customer;
        populateForm();
    }

    @FXML
    public void initialize() {
        // Form update hien tai su dung text field readonly cho hang thanh vien.
    }

    private void populateForm() {
        if (customer == null) return;
        txtMaKH.setText("#" + customer.getMaKH());
        txtHoTen.setText(customer.getHoTen());
        txtSDT.setText(customer.getSdt());
        txtDiaChi.setText(customer.getDiaChi());
        txtDiemTichLuy.setText(String.valueOf(customer.getDiemTichLuy()));
        String tierName = customer.getTenHang();
        txtHangThanhVien.setText(tierName == null || tierName.trim().isEmpty() ? "-" : tierName.trim());
    }

    @FXML
    private void update() {
        String hoTen = txtHoTen.getText().trim();
        String sdt = txtSDT.getText().trim();
        String diaChi = txtDiaChi.getText().trim();

        if (hoTen.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Họ tên không được để trống.");
            return;
        }
        if (!sdt.matches("\\d{10}")) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Số điện thoại phải đúng 10 chữ số.");
            return;
        }

        customer.setHoTen(hoTen);
        customer.setSdt(sdt);
        customer.setDiaChi(diaChi);

        try {
            customerService.updateCustomer(customer);
            updated = true;
            closeStageSafely();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Loi", e.getMessage());
        }
    }

    @FXML
    private void cancel() {
        closeStageSafely();
    }

    private void closeStageSafely() {
        if (stage != null) {
            stage.close();
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type, message, ButtonType.OK);
        alert.setTitle(title);
        alert.showAndWait();
    }
}