package com.bakery.controllers;

import com.bakery.models.dto.HangThanhVienDTO;
import com.bakery.models.dto.KhachHangDTO;
import com.bakery.services.CustomerService;
import com.bakery.services.CustomerTierService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.SQLException;

public class CustomerUpdateController {
    @FXML private TextField txtMaKH;
    @FXML private TextField txtHoTen;
    @FXML private TextField txtSDT;
    @FXML private TextField txtDiemTichLuy;
    @FXML private TextArea txtDiaChi;
    @FXML private ComboBox<HangThanhVienDTO> cboHangThanhVien;

    private Stage stage;
    private boolean updated = false;
    private KhachHangDTO customer;

    private final CustomerService customerService = new CustomerService();
    private final CustomerTierService customerTierService = new CustomerTierService();

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
        // Tai danh sach hang thanh vien qua Service.
        try {
            cboHangThanhVien.setItems(FXCollections.observableArrayList(customerTierService.getAllTiers()));
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Loi", "Khong tai duoc danh sach hang thanh vien.\n" + e.getMessage());
        }
        cboHangThanhVien.setCellFactory(param -> new ListCell<HangThanhVienDTO>() {
            @Override
            protected void updateItem(HangThanhVienDTO item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getTenHang());
            }
        });
        cboHangThanhVien.setButtonCell(new ListCell<HangThanhVienDTO>() {
            @Override
            protected void updateItem(HangThanhVienDTO item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getTenHang());
            }
        });
    }

    private void populateForm() {
        if (customer == null) return;
        txtMaKH.setText("#" + customer.getMaKH());
        txtHoTen.setText(customer.getHoTen());
        txtSDT.setText(customer.getSdt());
        txtDiaChi.setText(customer.getDiaChi());
        txtDiemTichLuy.setText(String.valueOf(customer.getDiemTichLuy()));

        // Chọn hạng tương ứng
        for (HangThanhVienDTO hang : cboHangThanhVien.getItems()) {
            if (hang.getMaHang() == customer.getMaHang()) {
                cboHangThanhVien.setValue(hang);
                break;
            }
        }
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
        if (cboHangThanhVien.getValue() != null) {
            customer.setMaHang(cboHangThanhVien.getValue().getMaHang());
        }

        try {
            customerService.updateCustomer(customer);
            updated = true;
            stage.close();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Loi", e.getMessage());
        }
    }

    @FXML
    private void cancel() {
        stage.close();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type, message, ButtonType.OK);
        alert.setTitle(title);
        alert.showAndWait();
    }
}