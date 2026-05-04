package com.bakery.views.controllers.kho;

import com.bakery.model.dto.kho.CotBanhDTO;
import com.bakery.model.dto.kho.KieuTrangTriDTO;
import com.bakery.model.dto.kho.NhanBanhDTO;
import com.bakery.services.banhang.TuyChinhBanhService;
import com.bakery.utils.UserSession;
import com.bakery.views.controllers.BaseController;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import java.math.BigDecimal;
import java.util.Optional;

public class ThanhPhanBanhController extends BaseController {

    @FXML private TableView<CotBanhDTO> tblCotBanh;
    @FXML private TableColumn<CotBanhDTO, String> colTenCot;
    @FXML private TableColumn<CotBanhDTO, BigDecimal> colPhuPhiCot;

    @FXML private TableView<NhanBanhDTO> tblNhanBanh;
    @FXML private TableColumn<NhanBanhDTO, String> colTenNhan;
    @FXML private TableColumn<NhanBanhDTO, BigDecimal> colPhuPhiNhan;

    @FXML private TableView<KieuTrangTriDTO> tblKieuTrangTri;
    @FXML private TableColumn<KieuTrangTriDTO, String> colTenTrangTri;
    @FXML private TableColumn<KieuTrangTriDTO, BigDecimal> colPhuPhiTrangTri;

    @FXML private Label lblThongBao;

    private final TuyChinhBanhService service = new TuyChinhBanhService();

    @FXML
    public void initialize() {
        setupTables();
        taiDuLieu();
    }

    private void setupTables() {
        colTenCot.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getTenCot()));
        colPhuPhiCot.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getPhuPhi()));

        colTenNhan.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getTenNhan()));
        colPhuPhiNhan.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getPhuPhi()));

        colTenTrangTri.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getTenTrangTri()));
        colPhuPhiTrangTri.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getPhuPhi()));
    }

    private void taiDuLieu() {
        try {
            tblCotBanh.setItems(FXCollections.observableArrayList(service.layDanhSachCotBanh()));
            tblNhanBanh.setItems(FXCollections.observableArrayList(service.layDanhSachNhanBanh()));
            tblKieuTrangTri.setItems(FXCollections.observableArrayList(service.layDanhSachKieuTrangTri()));
        } catch (Exception e) {
            hienThiLoi("Lỗi tải dữ liệu: " + e.getMessage());
        }
    }

    // --- Cot Banh Actions ---
    @FXML private void onThemCot() {
        showInputDialog("Thêm Cốt Bánh").ifPresent(res -> {
            try {
                CotBanhDTO dto = new CotBanhDTO();
                dto.setTenCot(res.name());
                dto.setPhuPhi(res.price());
                if (service.themCotBanh(dto)) {
                    taiDuLieu();
                    hienThiThanhCong("Đã thêm cốt bánh.");
                }
            } catch (Exception e) { hienThiLoi(e.getMessage()); }
        });
    }

    @FXML private void onSuaCot() {
        CotBanhDTO selected = tblCotBanh.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        showEditDialog("Sửa Cốt Bánh", selected.getTenCot(), selected.getPhuPhi()).ifPresent(res -> {
            try {
                selected.setTenCot(res.name());
                selected.setPhuPhi(res.price());
                if (service.suaCotBanh(selected)) {
                    taiDuLieu();
                    hienThiThanhCong("Đã cập nhật cốt bánh.");
                }
            } catch (Exception e) { hienThiLoi(e.getMessage()); }
        });
    }

    @FXML private void onXoaCot() {
        CotBanhDTO selected = tblCotBanh.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        if (confirmDelete("Xóa cốt bánh: " + selected.getTenCot())) {
            try {
                int maNV = UserSession.getCurrentUser() != null ? UserSession.getCurrentUser().getMaNV() : 1;
                if (service.xoaCotBanh(selected.getMaCot(), maNV)) {
                    taiDuLieu();
                    hienThiThanhCong("Đã xóa cốt bánh.");
                }
            } catch (Exception e) { hienThiLoi(e.getMessage()); }
        }
    }

    // --- Nhan Banh Actions ---
    @FXML private void onThemNhan() {
        showInputDialog("Thêm Nhân Bánh").ifPresent(res -> {
            try {
                NhanBanhDTO dto = new NhanBanhDTO();
                dto.setTenNhan(res.name());
                dto.setPhuPhi(res.price());
                if (service.themNhanBanh(dto)) {
                    taiDuLieu();
                    hienThiThanhCong("Đã thêm nhân bánh.");
                }
            } catch (Exception e) { hienThiLoi(e.getMessage()); }
        });
    }

    @FXML private void onSuaNhan() {
        NhanBanhDTO selected = tblNhanBanh.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        showEditDialog("Sửa Nhân Bánh", selected.getTenNhan(), selected.getPhuPhi()).ifPresent(res -> {
            try {
                selected.setTenNhan(res.name());
                selected.setPhuPhi(res.price());
                if (service.suaNhanBanh(selected)) {
                    taiDuLieu();
                    hienThiThanhCong("Đã cập nhật nhân bánh.");
                }
            } catch (Exception e) { hienThiLoi(e.getMessage()); }
        });
    }

    @FXML private void onXoaNhan() {
        NhanBanhDTO selected = tblNhanBanh.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        if (confirmDelete("Xóa nhân bánh: " + selected.getTenNhan())) {
            try {
                int maNV = UserSession.getCurrentUser() != null ? UserSession.getCurrentUser().getMaNV() : 1;
                if (service.xoaNhanBanh(selected.getMaNhan(), maNV)) {
                    taiDuLieu();
                    hienThiThanhCong("Đã xóa nhân bánh.");
                }
            } catch (Exception e) { hienThiLoi(e.getMessage()); }
        }
    }

    // --- Kieu Trang Tri Actions ---
    @FXML private void onThemTrangTri() {
        showInputDialog("Thêm Kiểu Trang Trí").ifPresent(res -> {
            try {
                KieuTrangTriDTO dto = new KieuTrangTriDTO();
                dto.setTenTrangTri(res.name());
                dto.setPhuPhi(res.price());
                if (service.themKieuTrangTri(dto)) {
                    taiDuLieu();
                    hienThiThanhCong("Đã thêm kiểu trang trí.");
                }
            } catch (Exception e) { hienThiLoi(e.getMessage()); }
        });
    }

    @FXML private void onSuaTrangTri() {
        KieuTrangTriDTO selected = tblKieuTrangTri.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        showEditDialog("Sửa Kiểu Trang Trí", selected.getTenTrangTri(), selected.getPhuPhi()).ifPresent(res -> {
            try {
                selected.setTenTrangTri(res.name());
                selected.setPhuPhi(res.price());
                if (service.suaKieuTrangTri(selected)) {
                    taiDuLieu();
                    hienThiThanhCong("Đã cập nhật kiểu trang trí.");
                }
            } catch (Exception e) { hienThiLoi(e.getMessage()); }
        });
    }

    @FXML private void onXoaTrangTri() {
        KieuTrangTriDTO selected = tblKieuTrangTri.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        if (confirmDelete("Xóa kiểu trang trí: " + selected.getTenTrangTri())) {
            try {
                int maNV = UserSession.getCurrentUser() != null ? UserSession.getCurrentUser().getMaNV() : 1;
                if (service.xoaKieuTrangTri(selected.getMaTrangTri(), maNV)) {
                    taiDuLieu();
                    hienThiThanhCong("Đã xóa kiểu trang trí.");
                }
            } catch (Exception e) { hienThiLoi(e.getMessage()); }
        }
    }

    // --- Dialog Helpers ---
    private Optional<InputResult> showInputDialog(String title) {
        return showEditDialog(title, "", BigDecimal.ZERO);
    }

    private Optional<InputResult> showEditDialog(String title, String name, BigDecimal price) {
        Dialog<InputResult> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(20));
        TextField txtName = new TextField(name);
        txtName.setPromptText("Tên thành phần");
        TextField txtPrice = new TextField(price.toString());
        txtPrice.setPromptText("Phụ phí");

        vbox.getChildren().addAll(new Label("Tên:"), txtName, new Label("Phụ phí:"), txtPrice);
        dialog.getDialogPane().setContent(vbox);

        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                try {
                    return new InputResult(txtName.getText(), new BigDecimal(txtPrice.getText()));
                } catch (Exception e) { return null; }
            }
            return null;
        });

        return dialog.showAndWait();
    }

    private boolean confirmDelete(String msg) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, msg, ButtonType.YES, ButtonType.NO);
        return alert.showAndWait().orElse(ButtonType.NO) == ButtonType.YES;
    }

    private void hienThiLoi(String msg) {
        lblThongBao.setText(msg);
        lblThongBao.setStyle("-fx-text-fill: #dc2626;");
    }

    private void hienThiThanhCong(String msg) {
        lblThongBao.setText(msg);
        lblThongBao.setStyle("-fx-text-fill: #059669;");
    }

    private record InputResult(String name, BigDecimal price) {}
}
