package com.bakery.views.controllers.customer;

import com.bakery.model.dto.KhachHangDTO;
import com.bakery.presenters.CustomerDeletedPresenter;
import com.bakery.views.interfaces.CustomerDeletedView;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Controller cho màn hình Thùng rác.
 * Implement CustomerDeletedView interface - Presenter giao tiếp qua interface này.
 */
public class CustomerDeletedViewFXMLController extends AbstractCustomerController implements CustomerDeletedView {

    @FXML private TableView<KhachHangDTO> deletedTable;
    @FXML private TableColumn<KhachHangDTO, Integer> colMaKH;
    @FXML private TableColumn<KhachHangDTO, String> colTenKH;
    @FXML private TableColumn<KhachHangDTO, String> colSDT;
    @FXML private TableColumn<KhachHangDTO, LocalDateTime> colNgayXoa;
    @FXML private TableColumn<KhachHangDTO, String> colNguoiXoa;
    @FXML private TableColumn<KhachHangDTO, Void> colThaoTac;
    @FXML private Label lblPageInfo;
    @FXML private TextField searchField;

    private CustomerDeletedPresenter presenter;

    @FXML
    public void initialize() {
        presenter = new CustomerDeletedPresenter(this);
        setupColumns();
        deletedTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        if (searchField != null) {
            searchField.textProperty().addListener((obs, oldVal, newVal) -> presenter.searchDeletedCustomers(newVal));
        }

        presenter.refreshDeletedCustomers();
    }

    @Override
    public void displayDeletedCustomers(List<KhachHangDTO> customers) {
        deletedTable.setItems(FXCollections.observableArrayList(customers));
        deletedTable.refresh();
    }

    @Override
    public void updatePaginationInfo(String pageInfo) {
        lblPageInfo.setText(pageInfo);
    }

    @Override
    public void showErrorAlert(String title, String message) {
        hienThiLoi(title, message);
    }

    @Override
    public void showSuccessAlert(String title, String message) {
        hienThiThanhCong(title, message);
    }

    @Override
    public boolean confirmRestore(String customerName) {
        return new Alert(
                Alert.AlertType.CONFIRMATION,
                "Khôi phục \"" + customerName + "\"?",
                ButtonType.OK,
                ButtonType.CANCEL
        ).showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }

    @Override
    public void setBusy(boolean busy) {
        deletedTable.setDisable(busy);
        if (searchField != null) {
            searchField.setDisable(busy);
        }
    }

    @Override
    public void closeDialog() {
        dongForm();
    }

    private void setupColumns() {
        colMaKH.setCellValueFactory(cd -> new javafx.beans.property.SimpleIntegerProperty(cd.getValue().getMaKH()).asObject());
        colTenKH.setCellValueFactory(cd -> new javafx.beans.property.SimpleStringProperty(cd.getValue().getHoTen()));
        colSDT.setCellValueFactory(cd -> new javafx.beans.property.SimpleStringProperty(cd.getValue().getSdt()));
        colNgayXoa.setCellValueFactory(cd -> new javafx.beans.property.SimpleObjectProperty<>(cd.getValue().getThoiDiemXoa()));
        colNgayXoa.setCellFactory(col -> new TableCell<KhachHangDTO, LocalDateTime>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            }
        });
        colNguoiXoa.setCellValueFactory(cd -> new javafx.beans.property.SimpleStringProperty(
                cd.getValue().getTenNguoiXoa() != null ? cd.getValue().getTenNguoiXoa() : "Mã NV: " + cd.getValue().getMaNX()));
        colThaoTac.setCellFactory(col -> new TableCell<KhachHangDTO, Void>() {
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    return;
                }
                KhachHangDTO kh = getTableRow().getItem();
                if (kh == null) {
                    setGraphic(null);
                    return;
                }
                Button restore = new Button("🔄 Khôi phục");
                restore.setOnAction(e -> {
                    if (confirmRestore(kh.getHoTen())) {
                        presenter.restoreCustomer(kh.getMaKH());
                    }
                });
                setGraphic(restore);
            }
        });
    }
}
