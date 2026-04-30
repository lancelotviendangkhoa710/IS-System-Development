package com.bakery.views.controllers.khachhang;

import com.bakery.model.dto.khachhang.HangThanhVienDTO;
import com.bakery.presenters.khachhang.CustomerTierPresenter;
import com.bakery.views.interfaces.khachhang.MembershipTierView;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.math.BigDecimal;
import java.util.List;

/**
 * Controller cho màn hình Quản lý Hạng thành viên.
 * Implement MembershipTierView interface - Presenter giao tiếp qua interface này.
 */
public class MembershipTierController implements MembershipTierView {

    @FXML private TableView<HangThanhVienDTO> tierTable;
    @FXML private TableColumn<HangThanhVienDTO, String> colTenHang;
    @FXML private TableColumn<HangThanhVienDTO, Integer> colDiemToiThieu;
    @FXML private TableColumn<HangThanhVienDTO, Double> colPhanTramGiamGia;
    @FXML private TableColumn<HangThanhVienDTO, Void> colThaoTac;

    private Stage stage;
    private CustomerTierPresenter presenter;

    public void setStage(Stage stage) { this.stage = stage; }

    @FXML public void initialize() {
        presenter = new CustomerTierPresenter(this);
        setupColumns();
        tierTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        presenter.loadTiers();
    }

    @Override public String getTierName() { return null; }
    @Override public int getMinimumPoints() { return 0; }
    @Override public double getDiscountPercentage() { return 0; }
    @Override public void setTierNameError(String error) { }
    @Override public void setMinPointsError(String error) { }
    @Override public void setDiscountError(String error) { }
    @Override public void clearForm() { }
    @Override public boolean confirmUpdate(String tierName) { return new Alert(Alert.AlertType.CONFIRMATION, "Cập nhật hạng \"" + tierName + "\"?", ButtonType.OK, ButtonType.CANCEL).showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK; }
    @Override public void displayTiers(List<HangThanhVienDTO> tiers) {
        tierTable.setItems(FXCollections.observableArrayList(tiers));
        tierTable.refresh();
    }
    @Override public void showErrorAlert(String title, String message) { new Alert(Alert.AlertType.ERROR, message).showAndWait(); }
    @Override public void showSuccessAlert(String title, String message) { new Alert(Alert.AlertType.INFORMATION, message).showAndWait(); }
    @Override public void setBusy(boolean busy) { tierTable.setDisable(busy); }

    private void setupColumns() {
        colTenHang.setCellValueFactory(cd -> new javafx.beans.property.SimpleStringProperty(cd.getValue().getTenHang()));
        colDiemToiThieu.setCellValueFactory(cd -> new javafx.beans.property.SimpleIntegerProperty(cd.getValue().getDiemToiThieu()).asObject());
        colPhanTramGiamGia.setCellValueFactory(cd -> new javafx.beans.property.SimpleDoubleProperty(cd.getValue().getPhanTramGiamGia().doubleValue()).asObject());
        colThaoTac.setCellFactory(col -> new TableCell<HangThanhVienDTO, Void>() {
            @Override protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) { setGraphic(null); } else {
                    HangThanhVienDTO tier = getTableRow().getItem();
                    if (tier != null) {
                        Button edit = new Button("Sửa");
                        edit.setOnAction(e -> openEditTierDialog(tier));
                        setGraphic(edit);
                    }
                }
            }
        });
    }

    private void openEditTierDialog(HangThanhVienDTO tier) {
        Dialog<HangThanhVienDTO> dialog = new Dialog<>();
        dialog.setTitle("Sửa hạng: " + tier.getTenHang());
        dialog.setResizable(false);
        ButtonType save = new ButtonType("Lưu", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(save, ButtonType.CANCEL);
        
        javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20));
        
        TextField txtName = new TextField(tier.getTenHang());
        txtName.setEditable(false);
        TextField txtPoints = new TextField(String.valueOf(tier.getDiemToiThieu()));
        TextField txtDiscount = new TextField(String.valueOf(tier.getPhanTramGiamGia()));
        
        grid.add(new Label("Tên hạng:"), 0, 0);
        grid.add(txtName, 1, 0);
        grid.add(new Label("Điểm tối thiểu:"), 0, 1);
        grid.add(txtPoints, 1, 1);
        grid.add(new Label("% Giảm giá:"), 0, 2);
        grid.add(txtDiscount, 1, 2);
        
        dialog.getDialogPane().setContent(grid);
        dialog.setResultConverter(btn -> btn == save ? tier : null);
        
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
