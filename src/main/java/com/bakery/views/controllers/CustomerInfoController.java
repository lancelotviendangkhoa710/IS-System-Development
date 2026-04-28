package com.bakery.views.controllers;

import com.bakery.models.dto.KhachHangDTO;
import com.bakery.presenters.CustomerInfoPresenter;
import com.bakery.views.interfaces.CustomerInfoView;
import com.bakery.views.interfaces.ViewFactory;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javafx.application.Platform;
import com.bakery.services.CustomerTierService;
import com.bakery.models.dto.HangThanhVienDTO;
/**
 * Controller cho màn hình Danh sách Khách hàng.
 * Implement CustomerInfoView interface - Presenter giao tiếp qua interface này.
 * Controller chỉ làm: bind FXML, bắt sự kiện, delegate sang Presenter.
 */
public class CustomerInfoController implements CustomerInfoView {
    
    @FXML private TableView<KhachHangDTO> customerTable;
    @FXML private TableColumn<KhachHangDTO, Integer> colId;
    @FXML private TableColumn<KhachHangDTO, String> colName;
    @FXML private TableColumn<KhachHangDTO, String> colPhone;
    @FXML private TableColumn<KhachHangDTO, String> colAddress;
    @FXML private TableColumn<KhachHangDTO, LocalDate> colRegDate;
    @FXML private TableColumn<KhachHangDTO, Integer> colPoints;
    @FXML private TableColumn<KhachHangDTO, String> colTier;
    @FXML private TableColumn<KhachHangDTO, Void> colActions;
    @FXML private Label lblTotalCustomers;
    @FXML private Label lblNewCustomers;
    @FXML private Label lblPageInfo;
    @FXML private TextField searchField;
    @FXML private Button btnRefresh;
    @FXML private HBox paginationBox;

    private CustomerInfoPresenter presenter;
    private ViewFactory viewFactory;
    private List<HangThanhVienDTO> activeTiers = new ArrayList<>();

    @FXML public void initialize() {
        setupTableColumns();
        customerTable.setFixedCellSize(36.0);
        customerTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        
        Platform.runLater(() -> {
            viewFactory = new DefaultViewFactory(customerTable.getScene() != null ? customerTable.getScene().getWindow() : null);
            presenter = new CustomerInfoPresenter(CustomerInfoController.this, viewFactory);
            
            searchField.textProperty().addListener((obs, oldVal, newVal) -> {
                presenter.searchCustomers(newVal == null ? "" : newVal);
            });
            
            presenter.refreshCustomers();
            refreshTiers();
        });
    }

    private void refreshTiers() {
        try {
            CustomerTierService tierService = new CustomerTierService();
            activeTiers = tierService.getAllTiers();
            if (activeTiers != null) {
                activeTiers.sort(Comparator.comparingInt(HangThanhVienDTO::getDiemToiThieu));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML private void onRefreshClicked() { 
        presenter.refreshCustomers(); 
        refreshTiers();
    }
    @FXML private void onAddCustomerClicked() { presenter.openAddCustomerDialog(); }
    @FXML private void onDeletedViewClicked() { presenter.openDeletedCustomersDialog(); }
    @FXML private void onExportExcelClicked() { File f = chooseExcelFileToSave(); if (f != null) presenter.exportCustomersToExcel(f); }
    @FXML private void onTierManagementClicked() { viewFactory.openMembershipTierDialog(null); }
    @FXML private void onPreviousPageClicked() { presenter.goToPage(getCurrentPage() - 1); }
    @FXML private void onNextPageClicked() { presenter.goToPage(getCurrentPage() + 1); }

    @Override public void displayCustomers(List<KhachHangDTO> customers) { customerTable.setItems(FXCollections.observableArrayList(customers)); customerTable.refresh(); }
    @Override public void updatePaginationInfo(String pageInfo) { lblPageInfo.setText(pageInfo); }
    @Override public void updateTotalCustomersCount(int count) { lblTotalCustomers.setText(String.valueOf(count)); }
    @Override public void updateNewCustomersThisMonth(int count) { lblNewCustomers.setText(String.valueOf(count)); }
    @Override public String getSearchKeyword() { return searchField.getText(); }
    @Override public void clearSearchField() { searchField.clear(); }
    @Override public void setBusy(boolean busy) { customerTable.setDisable(busy); searchField.setDisable(busy); btnRefresh.setDisable(busy); paginationBox.setDisable(busy); }
    @Override public void showErrorAlert(String title, String message) { new Alert(Alert.AlertType.ERROR, message, ButtonType.OK).showAndWait(); }
    @Override public void showSuccessAlert(String title, String message) { new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK).showAndWait(); }
    @Override public void showInfoAlert(String title, String message) { new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK).showAndWait(); }
    @Override public void openAddCustomerDialog(Runnable onAddedCallback) { viewFactory.openAddCustomerDialog(onAddedCallback); }
    @Override public void openUpdateCustomerDialog(KhachHangDTO customer, Runnable onUpdatedCallback) { viewFactory.openUpdateCustomerDialog(customer, onUpdatedCallback); }
    @Override public void openDeletedCustomersDialog(Runnable onClosedCallback) { viewFactory.openDeletedCustomersDialog(onClosedCallback); }
    @Override public boolean confirmDelete(String customerName) { return new Alert(Alert.AlertType.CONFIRMATION, "Xoá khách hàng \"" + customerName + "\"?", ButtonType.OK, ButtonType.CANCEL).showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK; }
    @Override public File chooseExcelFileToSave() { FileChooser fc = new FileChooser(); fc.setTitle("Lưu Excel"); fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel (*.xlsx)", "*.xlsx")); fc.setInitialFileName("khach_hang_" + LocalDate.now() + ".xlsx"); return fc.showSaveDialog(customerTable.getScene().getWindow()); }
    @Override public void updatePaginationControls(int currentPage, int totalPages) { paginationBox.getChildren().clear(); Button prev = new Button("◀"); prev.getStyleClass().add("pagination-button"); prev.setDisable(currentPage <= 1); prev.setOnAction(e -> onPreviousPageClicked()); paginationBox.getChildren().add(prev); for (int i = Math.max(1, currentPage - 2); i <= Math.min(totalPages, currentPage + 2); i++) { Button b = new Button(String.valueOf(i)); b.getStyleClass().add(i == currentPage ? "pagination-button-active" : "pagination-button"); int p = i; b.setOnAction(e -> presenter.goToPage(p)); paginationBox.getChildren().add(b); } Button next = new Button("▶"); next.getStyleClass().add("pagination-button"); next.setDisable(currentPage >= totalPages); next.setOnAction(e -> onNextPageClicked()); paginationBox.getChildren().add(next); }

    private void setupTableColumns() {
        colId.setCellValueFactory(cd -> new javafx.beans.property.SimpleIntegerProperty(cd.getValue().getMaKH()).asObject());
        colName.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getHoTen()));
        colPhone.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getSdt()));
        colAddress.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getDiaChi()));
        colRegDate.setCellValueFactory(cd -> new javafx.beans.property.SimpleObjectProperty<>(cd.getValue().getNgayDangKy()));
        
        colPoints.setCellFactory(col -> new TableCell<KhachHangDTO, Integer>() {
            @Override protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setGraphic(null); } else {
                    int nextTierPoints = -1;
                    if (activeTiers != null) {
                        for (HangThanhVienDTO tier : activeTiers) {
                            if (tier.getDiemToiThieu() > item) {
                                nextTierPoints = tier.getDiemToiThieu();
                                break;
                            }
                        }
                    }
                    
                    double progress = 1.0;
                    if (nextTierPoints > 0) {
                        progress = (double) item / nextTierPoints;
                    }

                    ProgressBar bar = new ProgressBar(Math.min(progress, 1.0));
                    Label lbl = new Label(String.valueOf(item));
                    HBox hb = new HBox(5, lbl, bar);
                    bar.setPrefWidth(80);
                    hb.setAlignment(javafx.geometry.Pos.CENTER);
                    setGraphic(hb);
                }
            }
        });
        
        colTier.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getTenHang() == null ? "-" : cd.getValue().getTenHang()));
        colTier.setCellFactory(col -> new TableCell<KhachHangDTO, String>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) { setGraphic(null); } else {
                    Label badge = new Label(item);
                    badge.setStyle(getTierStyle(item.toLowerCase()));
                    setGraphic(badge);
                }
            }
        });
        
        colActions.setCellFactory(col -> new TableCell<KhachHangDTO, Void>() {
            @Override protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) { setGraphic(null); } else {
                    KhachHangDTO kh = getTableRow().getItem();
                    if (kh != null) {
                        Button edit = new Button("Sửa");
                        Button del = new Button("Xóa");
                        edit.setOnAction(e -> presenter.openUpdateCustomerDialog(kh));
                        del.setOnAction(e -> { if (confirmDelete(kh.getHoTen())) presenter.deleteCustomerAndReload(kh.getMaKH(), com.bakery.utils.SessionManager.getCurrentEmployeeId()); });
                        HBox hb = new HBox(5, edit, del);
                        hb.setAlignment(javafx.geometry.Pos.CENTER);
                        setGraphic(hb);
                    }
                }
            }
        });
    }

    private String getTierStyle(String tier) {
        return switch (tier) {
            case "vàng" -> "-fx-background-color: #d4a373; -fx-text-fill: white;";
            case "bạc" -> "-fx-background-color: #c0c0c0; -fx-text-fill: black;";
            case "kim cương" -> "-fx-background-color: #7f8c8d; -fx-text-fill: white;";
            default -> "-fx-background-color: #e0e0e0; -fx-text-fill: black;";
        } + " -fx-background-radius: 10; -fx-padding: 2 8; -fx-font-size: 12px; -fx-font-weight: bold;";
    }

    private int getCurrentPage() {
        for (int i = 1; i < paginationBox.getChildren().size() - 1; i++) {
            Button btn = (Button) paginationBox.getChildren().get(i);
            if (btn.getStyleClass().contains("pagination-button-active")) {
                return Integer.parseInt(btn.getText());
            }
        }
        return 1;
    }
}