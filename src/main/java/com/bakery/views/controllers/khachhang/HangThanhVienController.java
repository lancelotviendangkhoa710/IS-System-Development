package com.bakery.views.controllers.khachhang;

import com.bakery.model.dto.khachhang.HangThanhVienDTO;
import com.bakery.presenters.khachhang.HangThanhVienPresenter;
import com.bakery.utils.DialogHelper;
import com.bakery.views.interfaces.khachhang.HangThanhVienView;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.math.BigDecimal;
import java.util.List;

/**
 * Controller cho màn hình Quản lý Hạng thành viên.
 * Implement HangThanhVienView interface - Presenter giao tiếp qua interface này.
 */
public class HangThanhVienController implements HangThanhVienView {

    @FXML private TableView<HangThanhVienDTO> tierTable;
    @FXML private TableColumn<HangThanhVienDTO, String> colTenHang;
    @FXML private TableColumn<HangThanhVienDTO, Integer> colDiemToiThieu;
    @FXML private TableColumn<HangThanhVienDTO, Double> colPhanTramGiamGia;

    @FXML private Button btnSua;

    private HangThanhVienPresenter presenter;

    @FXML public void initialize() {
        presenter = new HangThanhVienPresenter(this);
        setupColumns();
        tierTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        // Selection Listener để bật/tắt nút Sửa hạng
        tierTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (btnSua != null) {
                btnSua.setDisable(newVal == null);
            }
        });
        
        presenter.loadTiers();
    }

    @Override public String getTierName() { return null; }
    @Override public int getMinimumPoints() { return 0; }
    @Override public double getDiscountPercentage() { return 0; }
    @Override public void setTierNameError(String error) { }
    @Override public void setMinPointsError(String error) { }
    @Override public void setDiscountError(String error) { }
    @Override public void clearForm() { }

    @Override public boolean confirmUpdate(String tierName) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                "Cập nhật hạng \"" + tierName + "\"?", ButtonType.OK, ButtonType.CANCEL);
        DialogHelper.applyBakeryTheme(alert);
        return alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }

    @Override public void displayTiers(List<HangThanhVienDTO> tiers) {
        tierTable.setItems(FXCollections.observableArrayList(tiers));
        tierTable.refresh();
    }

    @Override public void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message);
        DialogHelper.applyBakeryTheme(alert);
        alert.showAndWait();
    }

    @Override public void showSuccessAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message);
        DialogHelper.applyBakeryTheme(alert);
        alert.showAndWait();
    }

    @Override public void setBusy(boolean busy) { tierTable.setDisable(busy); }

    private void setupColumns() {
        colTenHang.setCellValueFactory(cd -> new javafx.beans.property.SimpleStringProperty(cd.getValue().getTenHang()));
        colDiemToiThieu.setCellValueFactory(cd -> new javafx.beans.property.SimpleIntegerProperty(cd.getValue().getDiemToiThieu()).asObject());
        colPhanTramGiamGia.setCellValueFactory(cd -> new javafx.beans.property.SimpleDoubleProperty(cd.getValue().getPhanTramGiamGia().doubleValue()).asObject());
    }

    @FXML
    private void onSuaAction() {
        HangThanhVienDTO selected = tierTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            openEditTierDialog(selected);
        }
    }

    private void openEditTierDialog(HangThanhVienDTO tier) {
        Dialog<HangThanhVienDTO> dialog = new Dialog<>();
        dialog.setTitle("Sửa hạng: " + tier.getTenHang());
        dialog.setResizable(false);
        dialog.getDialogPane().getStyleClass().add("bg-app");

        ButtonType save = new ButtonType("Lưu", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(save, ButtonType.CANCEL);

        Button btnSave = (Button) dialog.getDialogPane().lookupButton(save);
        if (btnSave != null) btnSave.getStyleClass().add("btn-primary");
        Button btnCancel = (Button) dialog.getDialogPane().lookupButton(ButtonType.CANCEL);
        if (btnCancel != null) btnCancel.getStyleClass().add("btn-secondary");

        javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20));

        TextField txtName = new TextField(tier.getTenHang());
        txtName.setEditable(false);
        txtName.getStyleClass().add("text-field");

        TextField txtPoints = new TextField(String.valueOf(tier.getDiemToiThieu()));
        txtPoints.getStyleClass().add("text-field");

        TextField txtDiscount = new TextField(String.valueOf(tier.getPhanTramGiamGia()));
        txtDiscount.getStyleClass().add("text-field");

        Label lblTenHang = new Label("Tên hạng:");
        lblTenHang.getStyleClass().add("lbl-body-bold");
        Label lblDiem = new Label("Điểm tối thiểu:");
        lblDiem.getStyleClass().add("lbl-body-bold");
        Label lblGiamGia = new Label("% Giảm giá:");
        lblGiamGia.getStyleClass().add("lbl-body-bold");

        grid.add(lblTenHang, 0, 0);
        grid.add(txtName, 1, 0);
        grid.add(lblDiem, 0, 1);
        grid.add(txtPoints, 1, 1);
        grid.add(lblGiamGia, 0, 2);
        grid.add(txtDiscount, 1, 2);

        dialog.getDialogPane().setContent(grid);
        dialog.setResultConverter(btn -> btn == save ? tier : null);
        
        java.net.URL cssUrl = getClass().getResource("/css/bakery.css");
        if (cssUrl != null) dialog.getDialogPane().getStylesheets().add(cssUrl.toExternalForm());
        DialogHelper.applyBakeryTheme(dialog);

        dialog.showAndWait().ifPresent(t -> {
            try {
                t.setDiemToiThieu(Integer.parseInt(txtPoints.getText().trim()));
                t.setPhanTramGiamGia(BigDecimal.valueOf(Double.parseDouble(txtDiscount.getText().trim())));
                presenter.updateTier(t);
            } catch (NumberFormatException ex) {
                showErrorAlert("Lỗi", "Vui lòng nhập số hợp lệ");
            }
        });
    }
}
