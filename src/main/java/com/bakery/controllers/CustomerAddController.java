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
import java.time.LocalDate;

public class CustomerAddController {
    @FXML private TextField txtHoTen;
    @FXML private TextField txtSDT;
    @FXML private TextArea txtDiaChi;

    private Stage stage;
    private boolean saved = false;

    private final CustomerService customerService = new CustomerService();

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public boolean isSaved() {
        return saved;
    }

    @FXML
    public void initialize() {
        // Khong co du lieu khoi tao dac biet cho form Add.
    }

    @FXML
    private void save() {
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

        KhachHangDTO kh = new KhachHangDTO();
        kh.setHoTen(hoTen);
        kh.setSdt(sdt);
        kh.setDiaChi(diaChi);
        kh.setNgayDangKy(LocalDate.now());
        kh.setDiemTichLuy(0);

        try {
            customerService.createCustomer(kh);
            saved = true;
            closeStageSafely();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", e.getMessage());
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