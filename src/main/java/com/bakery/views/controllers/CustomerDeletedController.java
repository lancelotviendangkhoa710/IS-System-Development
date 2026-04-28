package com.bakery.views.controllers;

import com.bakery.models.dto.KhachHangDTO;
import com.bakery.presenters.CustomerDeletedPresenter;
import com.bakery.views.interfaces.CustomerDeletedView;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Controller cho màn hình Thùng rác.
 * Implement CustomerDeletedView interface - Presenter giao tiếp qua interface này.
 */
public class CustomerDeletedController implements CustomerDeletedView {

    @FXML private TableView<KhachHangDTO> deletedTable;
    @FXML private TableColumn<KhachHangDTO, Integer> colMaKH;
    @FXML private TableColumn<KhachHangDTO, String> colTenKH;
    @FXML private TableColumn<KhachHangDTO, String> colSDT;
    @FXML private TableColumn<KhachHangDTO, LocalDateTime> colNgayXoa;
    @FXML private TableColumn<KhachHangDTO, String> colNguoiXoa;
    @FXML private TableColumn<KhachHangDTO, Void> colThaoTac;
    @FXML private Label lblPageInfo;
    @FXML private HBox paginationBox;

    private Stage stage;
    private CustomerDeletedPresenter presenter;

    public void setStage(Stage stage) { this.stage = stage; }

    @FXML public void initialize() {
        presenter = new CustomerDeletedPresenter(this);
        setupColumns();
        deletedTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        presenter.refreshDeletedCustomers();
    }

    @FXML private void onPreviousPageClicked() { presenter.goToPreviousPage(); }
    @FXML private void onNextPageClicked() { presenter.goToNextPage(); }

    @Override public void displayDeletedCustomers(List<KhachHangDTO> customers) {
        deletedTable.setItems(FXCollections.observableArrayList(customers));
        deletedTable.refresh();
    }

    @Override public void updatePaginationInfo(String pageInfo) { lblPageInfo.setText(pageInfo); }

    @Override public void updatePaginationControls(int currentPage, int totalPages) {
        paginationBox.getChildren().clear();
        Button prev = new Button("◀");
        prev.getStyleClass().add("pagination-button");
        prev.setDisable(currentPage <= 1);
        prev.setOnAction(e -> onPreviousPageClicked());
        paginationBox.getChildren().add(prev);
        
        for (int i = Math.max(1, currentPage - 1); i <= Math.min(totalPages, currentPage + 1); i++) {
            Button b = new Button(String.valueOf(i));
            b.getStyleClass().add(i == currentPage ? "pagination-button-active" : "pagination-button");
            int page = i;
            b.setOnAction(e -> presenter.goToPage(page));
            paginationBox.getChildren().add(b);
        }
        
        Button next = new Button("▶");
        next.getStyleClass().add("pagination-button");
        next.setDisable(currentPage >= totalPages);
        next.setOnAction(e -> onNextPageClicked());
        paginationBox.getChildren().add(next);
    }

    @Override public void showErrorAlert(String title, String message) { new Alert(Alert.AlertType.ERROR, message).showAndWait(); }
    @Override public void showSuccessAlert(String title, String message) { new Alert(Alert.AlertType.INFORMATION, message).showAndWait(); }
    @Override public boolean confirmRestore(String customerName) { return new Alert(Alert.AlertType.CONFIRMATION, "Khôi phục \"" + customerName + "\"?", ButtonType.OK, ButtonType.CANCEL).showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK; }
    @Override public void setBusy(boolean busy) { deletedTable.setDisable(busy); paginationBox.setDisable(busy); }
    @Override public void closeDialog() { if (stage != null) stage.close(); }

    private void setupColumns() {
        colMaKH.setCellValueFactory(cd -> new javafx.beans.property.SimpleIntegerProperty(cd.getValue().getMaKH()).asObject());
        colTenKH.setCellValueFactory(cd -> new javafx.beans.property.SimpleStringProperty(cd.getValue().getHoTen()));
        colSDT.setCellValueFactory(cd -> new javafx.beans.property.SimpleStringProperty(cd.getValue().getSdt()));
        colNgayXoa.setCellValueFactory(cd -> new javafx.beans.property.SimpleObjectProperty<>(cd.getValue().getThoiDiemXoa()));
        colNgayXoa.setCellFactory(col -> new TableCell<KhachHangDTO, LocalDateTime>() {
            @Override protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            }
        });
        colNguoiXoa.setCellValueFactory(cd -> new javafx.beans.property.SimpleStringProperty(cd.getValue().getTenNguoiXoa() != null ? cd.getValue().getTenNguoiXoa() : "Mã NV: " + cd.getValue().getMaNX()));
        colThaoTac.setCellFactory(col -> new TableCell<KhachHangDTO, Void>() {
            @Override protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) { setGraphic(null); } else {
                    KhachHangDTO kh = getTableRow().getItem();
                    if (kh != null) {
                        Button restore = new Button("🔄 Khôi phục");
                        restore.setOnAction(e -> { if (confirmRestore(kh.getHoTen())) presenter.restoreCustomer(kh.getMaKH()); });
                        setGraphic(restore);
                    }
                }
            }
        });
    }
}