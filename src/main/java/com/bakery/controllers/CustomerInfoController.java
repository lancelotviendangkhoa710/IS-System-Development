package com.bakery.controllers;

import com.bakery.models.dto.KhachHangDTO;
import com.bakery.services.CustomerService;
import com.bakery.utils.SessionManager;
import javafx.concurrent.Task;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class CustomerInfoController {
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

    private final CustomerService customerService = new CustomerService();
    private ObservableList<KhachHangDTO> customerList;
    private MainController mainController;

    private static class CustomerRefreshData {
        private final List<KhachHangDTO> activeCustomers;
        private final List<KhachHangDTO> tableCustomers;
        private final int totalCustomers;
        private final int newCustomersInMonth;
        private final boolean filtered;

        private CustomerRefreshData(List<KhachHangDTO> activeCustomers,
                                    List<KhachHangDTO> tableCustomers,
                                    int totalCustomers,
                                    int newCustomersInMonth,
                                    boolean filtered) {
            this.activeCustomers = activeCustomers;
            this.tableCustomers = tableCustomers;
            this.totalCustomers = totalCustomers;
            this.newCustomersInMonth = newCustomersInMonth;
            this.filtered = filtered;
        }
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @FXML
    public void initialize() {
        // Tai du lieu va tao cot khi mo man CRM.
        setupTableColumns();
        refreshCustomerData();
        customerTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Lọc theo từ khóa tìm kiếm
        searchField.textProperty().addListener((obs, oldVal, newVal) -> filterCustomerList(newVal));
    }

    // Dong bo du lieu CRM tu DB khi nguoi dung bam Refresh hoac khi quay lai man hinh.
    @FXML
    private void refreshCustomerData() {
        loadTableData();
        updateStats();

        String keyword = searchField != null ? searchField.getText() : "";
        if (keyword != null && !keyword.trim().isEmpty()) {
            filterCustomerList(keyword);
        }
    }

    private void loadTableData() {
        try {
            List<KhachHangDTO> list = customerService.getActiveCustomers();
            customerList = FXCollections.observableArrayList(list);
            customerTable.setItems(customerList);
        } catch (SQLException e) {
            customerList = FXCollections.observableArrayList();
            customerTable.setItems(customerList);
            showAlert(Alert.AlertType.ERROR, "Loi", "Khong tai duoc danh sach khach hang.\n" + e.getMessage());
        }
    }

    private void setupTableColumns() {
        // Cac cot co body trong FXML; tai day chi gan logic hien thi dac biet.
        colPoints.setCellFactory(col -> new TableCell<KhachHangDTO, Integer>() {
            private final ProgressBar bar = new ProgressBar(0);
            private final Label label = new Label();
            private final HBox hbox = new HBox(5, label, bar);

            {
                bar.setPrefWidth(80);
                bar.setMaxHeight(8);
                label.setStyle("-fx-font-size: 12px;");
                hbox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
            }

            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    label.setText(String.valueOf(item));
                    double progress = item / 1000.0;
                    if (progress > 1.0) progress = 1.0;
                    bar.setProgress(progress);
                    setGraphic(hbox);
                }
            }
        });

        colTier.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getTenHang()));
        colTier.setCellFactory(col -> new TableCell<KhachHangDTO, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    switch (item.toLowerCase()) {
                        case "vàng":
                            setStyle("-fx-background-color: #d4a373; -fx-text-fill: white; -fx-background-radius: 10; -fx-padding: 2 8;");
                            break;
                        case "bạc":
                            setStyle("-fx-background-color: #c0c0c0; -fx-text-fill: black; -fx-background-radius: 10; -fx-padding: 2 8;");
                            break;
                        default:
                            setStyle("-fx-background-color: #e0e0e0; -fx-text-fill: black; -fx-background-radius: 10; -fx-padding: 2 8;");
                    }
                }
            }
        });

        colActions.setCellFactory(col -> new TableCell<KhachHangDTO, Void>() {
            private final Button btnEdit = new Button("Sửa");
            private final Button btnDelete = new Button("Xóa");
            private final HBox pane = new HBox(5, btnEdit, btnDelete);

            {
                btnEdit.setOnAction(e -> {
                    KhachHangDTO kh = getTableRow().getItem();
                    if (kh != null) openUpdateDialog(kh);
                });
                btnDelete.setOnAction(e -> {
                    KhachHangDTO kh = getTableRow().getItem();
                    if (kh != null) softDeleteCustomer(kh);
                });
                btnEdit.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
                btnDelete.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(pane);
                }
            }
        });
    }

    private void filterCustomerList(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            customerTable.setItems(customerList);
            lblPageInfo.setText("Showing 1 to " + customerList.size() + " of " + customerList.size() + " entries");
        } else {
            try {
                List<KhachHangDTO> filtered = customerService.searchCustomers(keyword.trim());
                ObservableList<KhachHangDTO> data = FXCollections.observableArrayList(filtered);
                customerTable.setItems(data);
                lblPageInfo.setText("Showing 1 to " + data.size() + " of " + data.size() + " entries (filtered)");
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Loi", "Khong tim kiem duoc khach hang.\n" + e.getMessage());
            }
        }
    }

    private void updateStats() {
        int total = customerService.countActiveCustomers();
        lblTotalCustomers.setText(String.valueOf(total));
        int newThisMonth = customerService.countNewCustomersInMonth(LocalDate.now().getYear(), LocalDate.now().getMonthValue());
        lblNewCustomers.setText(String.valueOf(newThisMonth));
    }

    // Trong CustomerInfoController, sửa phương thức goToDeletedView:
    @FXML
    private void goToDeletedView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bakery/views/CustomerDeletedView.fxml"));
            Parent root = loader.load();
            CustomerDeletedController controller = loader.getController();

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL); // chặn tương tác với cửa sổ chính
            stage.setTitle("Thùng rác");
            stage.setScene(new Scene(root));
            controller.setStage(stage);
            stage.showAndWait();

            // Sau khi popup dong, dong bo lai du lieu CRM.
            refreshCustomerData();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể mở thùng rác.\n" + e.getMessage());
        }
    }

    @FXML
    private void openAddCustomerDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bakery/views/CustomerAddView.fxml"));
            Parent root = loader.load();
            CustomerAddController controller = loader.getController();
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Thêm khách hàng mới");
            stage.setScene(new Scene(root));
            controller.setStage(stage);
            stage.showAndWait();
            if (controller.isSaved()) {
                refreshCustomerData();
            }
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Lỗi mở dialog", "Không thể mở form thêm khách hàng. Kiểm tra file CustomerAddView.fxml.\n" + e.getMessage());
        }
    }

    private void openUpdateDialog(KhachHangDTO customer) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bakery/views/CustomerUpdateView.fxml"));
            Parent root = loader.load();
            CustomerUpdateController controller = loader.getController();
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Sửa thông tin khách hàng");
            stage.setScene(new Scene(root));
            controller.setStage(stage);
            controller.setCustomer(customer);
            stage.showAndWait();
            if (controller.isUpdated()) {
                refreshCustomerData();
            }
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Lỗi mở dialog", "Không thể mở form sửa khách hàng. Kiểm tra file CustomerUpdateView.fxml.\n" + e.getMessage());
        }
    }

    private void softDeleteCustomer(KhachHangDTO customer) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Bạn có chắc muốn xóa khách hàng này?", ButtonType.YES, ButtonType.NO);
        alert.setTitle("Xác nhận xóa");
        alert.showAndWait().ifPresent(type -> {
            if (type == ButtonType.YES) {
                int manv = SessionManager.getCurrentEmployeeId();
                if (manv <= 0) {
                    showAlert(Alert.AlertType.ERROR, "Lỗi", "Không xác định được nhân viên đăng nhập.");
                    return;
                }
                executeDeleteCustomerAsync(customer.getMaKH(), manv, searchField.getText());
            }
        });
    }

    // Xu ly xoa mem va reload du lieu bang bat dong bo.
    private void executeDeleteCustomerAsync(int customerId, int employeeId, String keyword) {
        setBusyState(true);

        Task<CustomerRefreshData> deleteTask = new Task<CustomerRefreshData>() {
            @Override
            protected CustomerRefreshData call() throws Exception {
                customerService.softDeleteCustomer(customerId, employeeId);

                List<KhachHangDTO> activeCustomers = customerService.getActiveCustomers();
                String normalizedKeyword = keyword == null ? "" : keyword.trim();
                boolean isFiltered = !normalizedKeyword.isEmpty();

                List<KhachHangDTO> tableCustomers = isFiltered
                        ? customerService.searchCustomers(normalizedKeyword)
                        : activeCustomers;

                int total = customerService.countActiveCustomers();
                int newThisMonth = customerService.countNewCustomersInMonth(LocalDate.now().getYear(), LocalDate.now().getMonthValue());

                return new CustomerRefreshData(activeCustomers, tableCustomers, total, newThisMonth, isFiltered);
            }
        };

        deleteTask.setOnSucceeded(event -> {
            CustomerRefreshData data = deleteTask.getValue();
            customerList = FXCollections.observableArrayList(data.activeCustomers);

            ObservableList<KhachHangDTO> tableData = FXCollections.observableArrayList(data.tableCustomers);
            customerTable.setItems(tableData);

            if (data.filtered) {
                lblPageInfo.setText("Showing 1 to " + tableData.size() + " of " + tableData.size() + " entries (filtered)");
            } else {
                lblPageInfo.setText("Showing 1 to " + tableData.size() + " of " + tableData.size() + " entries");
            }

            lblTotalCustomers.setText(String.valueOf(data.totalCustomers));
            lblNewCustomers.setText(String.valueOf(data.newCustomersInMonth));
            setBusyState(false);
        });

        deleteTask.setOnFailed(event -> {
            setBusyState(false);
            Throwable ex = deleteTask.getException();

            // Neu du lieu da bi xoa ben ngoai app, tu dong dong bo lai man hinh CRM.
            if (isCustomerMissingError(ex)) {
                refreshCustomerData();
                showAlert(Alert.AlertType.INFORMATION, "Thong bao",
                        "Khach hang nay da khong con ton tai trong CSDL.\nHe thong da tu dong dong bo lai du lieu.");
                return;
            }

            String errorMessage = ex != null ? ex.getMessage() : "Loi khong xac dinh";
            showAlert(Alert.AlertType.ERROR, "Loi", "Xoa khach hang that bai.\n" + errorMessage);
        });

        Thread worker = new Thread(deleteTask, "crm-delete-customer-worker");
        worker.setDaemon(true);
        worker.start();
    }

    // Bat/tat trang thai ban de nguoi dung khong bam lap lai khi dang xu ly DB.
    private void setBusyState(boolean busy) {
        customerTable.setDisable(busy);
        searchField.setDisable(busy);
        if (btnRefresh != null) {
            btnRefresh.setDisable(busy);
        }
    }

    // Nhan dien loi ban ghi da bi xoa ben ngoai he thong de xu ly UX mem.
    private boolean isCustomerMissingError(Throwable ex) {
        Throwable current = ex;
        while (current != null) {
            String msg = current.getMessage();
            if (msg != null) {
                String normalized = msg.toLowerCase();
                if (normalized.contains("khach hang khong ton tai") || normalized.contains("khong tim thay khach hang")) {
                    return true;
                }
            }
            current = current.getCause();
        }
        return false;
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type, message, ButtonType.OK);
        alert.setTitle(title);
        alert.showAndWait();
    }
}