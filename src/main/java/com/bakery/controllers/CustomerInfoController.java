package com.bakery.controllers;

import com.bakery.models.dto.KhachHangDTO;
import com.bakery.services.CustomerService;
import com.bakery.utils.SessionManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class CustomerInfoController {

    // Các thành phần giao diện
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

    private final CustomerService customerService = new CustomerService();
    private ObservableList<KhachHangDTO> fullCustomerList;  // dữ liệu hiện tại (có thể đã lọc)
    private List<KhachHangDTO> originalData;               // dữ liệu gốc từ DB (chưa lọc)
    private boolean suppressSearchFilter = false;

    // Phân trang
    private static final int ROWS_PER_PAGE = 10;
    private int currentPage = 1;
    private int totalPages = 1;

    // Lớp lưu dữ liệu tạm khi xóa bất đồng bộ
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

    @FXML
    public void initialize() {
        setupTableColumns();
        customerTable.setFixedCellSize(36.0);
        refreshCustomerData();
        customerTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Lọc theo từ khóa tìm kiếm mỗi khi người dùng gõ
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!suppressSearchFilter) {
                filterCustomerList(newVal);
            }
        });
    }

    // ==================== TẢI DỮ LIỆU & PHÂN TRANG ====================

    @FXML
    private void refreshCustomerData() {
        suppressSearchFilter = true;
        try {
            if (searchField.getText() != null && !searchField.getText().isEmpty()) {
                searchField.clear();
            }
        } finally {
            suppressSearchFilter = false;
        }

        loadTableData();
        updateStats();

        // Reset về dữ liệu gốc sau khi đã làm mới danh sách
        fullCustomerList = FXCollections.observableArrayList(originalData);
        currentPage = 1;
        applyPagination();
    }

    private void loadTableData() {
        try {
            originalData = customerService.getActiveCustomers();
            fullCustomerList = FXCollections.observableArrayList(originalData);
            applyPagination();
        } catch (SQLException e) {
            fullCustomerList = FXCollections.observableArrayList();
            originalData = List.of();
            customerTable.setItems(fullCustomerList);
            customerTable.refresh();
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không tải được danh sách khách hàng.\n" + e.getMessage());
        }
    }

    private void filterCustomerList(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            // Không có từ khóa -> trở về dữ liệu gốc
            fullCustomerList = FXCollections.observableArrayList(originalData);
        } else {
            try {
                List<KhachHangDTO> filtered = customerService.searchCustomers(keyword.trim());
                fullCustomerList = FXCollections.observableArrayList(filtered);
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể tìm kiếm.\n" + e.getMessage());
                return;
            }
        }
        currentPage = 1;
        applyPagination();
    }

    private void applyPagination() {
        if (fullCustomerList == null || fullCustomerList.isEmpty()) {
            totalPages = 1;
            currentPage = 1;
            customerTable.setItems(FXCollections.observableArrayList());
            lblPageInfo.setText("Hiển thị 0-0 của 0");
            updatePaginationControls();
            return;
        }

        totalPages = (int) Math.ceil((double) fullCustomerList.size() / ROWS_PER_PAGE);
        if (currentPage > totalPages) currentPage = totalPages;
        if (currentPage < 1) currentPage = 1;

        int fromIndex = (currentPage - 1) * ROWS_PER_PAGE;
        int toIndex = Math.min(fromIndex + ROWS_PER_PAGE, fullCustomerList.size());

        List<KhachHangDTO> pageData = fullCustomerList.subList(fromIndex, toIndex);
        customerTable.setItems(FXCollections.observableArrayList(pageData));
        customerTable.refresh();

        lblPageInfo.setText(String.format("Hiển thị %d-%d của %d",
                fromIndex + 1, toIndex, fullCustomerList.size()));
        updatePaginationControls();
    }

    private void updatePaginationControls() {
        paginationBox.getChildren().clear();

        // Nút Previous
        Button prevBtn = new Button("◀");
        prevBtn.getStyleClass().add("pagination-button");
        prevBtn.setDisable(currentPage <= 1);
        prevBtn.setOnAction(e -> goToPage(currentPage - 1));
        paginationBox.getChildren().add(prevBtn);

        // Hiển thị tối đa 5 nút số trang xung quanh trang hiện tại
        int maxButtons = 5;
        int start = Math.max(1, currentPage - maxButtons / 2);
        int end = Math.min(totalPages, start + maxButtons - 1);
        if (end - start < maxButtons - 1) {
            start = Math.max(1, end - maxButtons + 1);
        }

        for (int i = start; i <= end; i++) {
            Button pageBtn = new Button(String.valueOf(i));
            if (i == currentPage) {
                pageBtn.getStyleClass().add("pagination-button-active");
            } else {
                pageBtn.getStyleClass().add("pagination-button");
            }
            int page = i;
            pageBtn.setOnAction(e -> goToPage(page));
            paginationBox.getChildren().add(pageBtn);
        }

        // Nút Next
        Button nextBtn = new Button("▶");
        nextBtn.getStyleClass().add("pagination-button");
        nextBtn.setDisable(currentPage >= totalPages);
        nextBtn.setOnAction(e -> goToPage(currentPage + 1));
        paginationBox.getChildren().add(nextBtn);
    }

    private void goToPage(int page) {
        if (page < 1 || page > totalPages) return;
        currentPage = page;
        applyPagination();
    }

    // ==================== CỘT BẢNG ====================

    private void setupTableColumns() {
        // Các cột Mã KH, Tên, SĐT, Địa chỉ, Ngày đăng ký đã có cellValueFactory trong FXML -> KHÔNG can thiệp.

        // Cột ĐIỂM: thêm cell factory hiển thị ProgressBar + label
        colPoints.setCellFactory(col -> new TableCell<KhachHangDTO, Integer>() {
            private final ProgressBar bar = new ProgressBar(0);
            private final Label label = new Label();
            private final HBox hbox = new HBox(5, label, bar);

            {
                bar.setPrefWidth(80);
                bar.setMaxHeight(8);
                label.setStyle("-fx-font-size: 12px;");
                hbox.setAlignment(javafx.geometry.Pos.CENTER);
            }

            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(null);
                    label.setText(String.valueOf(item));
                    double progress = item / 1000.0;
                    if (progress > 1.0) progress = 1.0;
                    bar.setProgress(progress);
                    setGraphic(hbox);
                }
            }
        });

        // Cột HẠNG
        // Cột Hạng: dùng Label graphic để hiển thị badge đẹp hơn
        colTier.setCellValueFactory(cellData -> {
            KhachHangDTO customer = cellData.getValue();
            String tierName = customer != null ? customer.getTenHang() : null;
            return new SimpleStringProperty(tierName == null || tierName.trim().isEmpty() ? "-" : tierName.trim());
        });

        colTier.setCellFactory(col -> new TableCell<KhachHangDTO, String>() {
            private final Label badge = new Label();

            {
                // Style cơ bản cho badge
                badge.setStyle("-fx-background-radius: 10; -fx-padding: 2 8; -fx-font-size: 12px; -fx-font-weight: bold;");
                setAlignment(javafx.geometry.Pos.CENTER);
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    return;
                }

                badge.setText(item);
                // Áp dụng màu sắc dựa trên tên hạng
                switch (item.toLowerCase()) {
                    case "vàng":
                        badge.setStyle("-fx-background-color: #d4a373; -fx-text-fill: white; -fx-background-radius: 10; -fx-padding: 2 8; -fx-font-size: 12px; -fx-font-weight: bold;");
                        break;
                    case "bạc":
                        badge.setStyle("-fx-background-color: #c0c0c0; -fx-text-fill: black; -fx-background-radius: 10; -fx-padding: 2 8; -fx-font-size: 12px; -fx-font-weight: bold;");
                        break;
                    case "kim cương":
                        badge.setStyle("-fx-background-color: #7f8c8d; -fx-text-fill: white; -fx-background-radius: 10; -fx-padding: 2 8; -fx-font-size: 12px; -fx-font-weight: bold;");
                        break;
                    case "đồng":
                    case "không":
                        badge.setStyle("-fx-background-color: #e0e0e0; -fx-text-fill: black; -fx-background-radius: 10; -fx-padding: 2 8; -fx-font-size: 12px; -fx-font-weight: bold;");
                        break;
                    default:
                        badge.setStyle("-fx-background-color: #e0e0e0; -fx-text-fill: black; -fx-background-radius: 10; -fx-padding: 2 8; -fx-font-size: 12px; -fx-font-weight: bold;");
                }
                setGraphic(badge);
            }
        });

        // Cột THAO TÁC: thêm nút Sửa/Xóa
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
                btnEdit.setStyle("-fx-cursor: hand;");
                btnDelete.setStyle("-fx-cursor: hand;");
                pane.setAlignment(javafx.geometry.Pos.CENTER);
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

    // ==================== THỐNG KÊ ====================

    private void updateStats() {
        int total = customerService.countActiveCustomers();
        lblTotalCustomers.setText(String.valueOf(total));
        int newThisMonth = customerService.countNewCustomersInMonth(
                LocalDate.now().getYear(), LocalDate.now().getMonthValue());
        lblNewCustomers.setText(String.valueOf(newThisMonth));
    }

    // ==================== XUẤT EXCEL ====================
    @FXML
    private void exportCustomersToExcel() {
        List<KhachHangDTO> dataToExport =
                (fullCustomerList == null) ? List.of() : new ArrayList<>(fullCustomerList);

        if (dataToExport.isEmpty()) {
            showAlert(Alert.AlertType.INFORMATION, "Thông báo", "Không có dữ liệu để xuất.");
            return;
        }

        FileChooser chooser = new FileChooser();
        chooser.setTitle("Lưu file Excel");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Excel Workbook (*.xlsx)", "*.xlsx"));
        chooser.setInitialFileName("danh_sach_khach_hang_" + LocalDate.now() + ".xlsx");

        File file = chooser.showSaveDialog(customerTable.getScene().getWindow());
        if (file == null) return;

        DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        try (Workbook workbook = new XSSFWorkbook();
            FileOutputStream out = new FileOutputStream(file)) {

            Sheet sheet = workbook.createSheet("KhachHang");

            String[] headers = {"Mã KH", "Họ tên", "SĐT", "Địa chỉ", "Ngày đăng ký", "Điểm", "Hạng"};
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }

            int rowIdx = 1;
            for (KhachHangDTO kh : dataToExport) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(kh.getMaKH());
                row.createCell(1).setCellValue(kh.getHoTen() == null ? "" : kh.getHoTen());
                row.createCell(2).setCellValue(kh.getSdt() == null ? "" : kh.getSdt());
                row.createCell(3).setCellValue(kh.getDiaChi() == null ? "" : kh.getDiaChi());
                row.createCell(4).setCellValue(
                        kh.getNgayDangKy() == null ? "" : kh.getNgayDangKy().format(dateFmt));
                row.createCell(5).setCellValue(kh.getDiemTichLuy());
                row.createCell(6).setCellValue(kh.getTenHang() == null ? "-" : kh.getTenHang());
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            showAlert(Alert.AlertType.INFORMATION, "Thành công", "Xuất Excel thành công.");
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể xuất Excel.\n" + e.getMessage());
        }
    }

    // ==================== DIALOG & XÓA ====================

    @FXML
    private void goToDeletedView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bakery/views/CustomerDeletedView.fxml"));
            Parent root = loader.load();
            CustomerDeletedController controller = loader.getController();

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Thùng rác");
            stage.setScene(new Scene(root));
            controller.setStage(stage);
            stage.showAndWait();

            // Sau khi đóng popup, refresh lại toàn bộ dữ liệu
            refreshCustomerData();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể mở thùng rác.\n" + e.getMessage());
        }
    }

    @FXML
    private void openTierManagementDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bakery/views/MembershipTierView.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Quản lý hạng thành viên");
            stage.setScene(new Scene(root));

            Object controllerObj = loader.getController();
            if (controllerObj instanceof MembershipTierController) {
                ((MembershipTierController) controllerObj).setStage(stage);
            }

            stage.showAndWait();

            // Sau khi đóng popup, nếu cần refresh danh sách khách hàng (ví dụ hiển thị tên hạng thay đổi) thì có thể gọi refreshCustomerData()
        } catch (Exception e) {
            Throwable root = e;
            while (root.getCause() != null) {
                root = root.getCause();
            }
            String detail = root.getMessage() != null ? root.getMessage() : e.toString();
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể mở quản lý hạng.\n" + detail);
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
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể mở form thêm khách hàng.\n" + e.getMessage());
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
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể mở form sửa khách hàng.\n" + e.getMessage());
        }
    }

    private void softDeleteCustomer(KhachHangDTO customer) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                "Bạn có chắc muốn xóa khách hàng này?", ButtonType.YES, ButtonType.NO);
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

    private void executeDeleteCustomerAsync(int customerId, int employeeId, String keyword) {
        setBusyState(true);

        Task<CustomerRefreshData> deleteTask = new Task<>() {
            @Override
            protected CustomerRefreshData call() throws Exception {
                customerService.softDeleteCustomer(customerId, employeeId);

                List<KhachHangDTO> activeCustomers = customerService.getActiveCustomers();
                String normalizedKeyword = (keyword == null ? "" : keyword).trim();
                boolean isFiltered = !normalizedKeyword.isEmpty();

                List<KhachHangDTO> tableCustomers = isFiltered
                        ? customerService.searchCustomers(normalizedKeyword)
                        : activeCustomers;

                int total = customerService.countActiveCustomers();
                int newThisMonth = customerService.countNewCustomersInMonth(
                        LocalDate.now().getYear(), LocalDate.now().getMonthValue());

                return new CustomerRefreshData(activeCustomers, tableCustomers, total, newThisMonth, isFiltered);
            }
        };

        deleteTask.setOnSucceeded(event -> {
            CustomerRefreshData data = deleteTask.getValue();
            originalData = data.activeCustomers;

            if (data.filtered) {
                fullCustomerList = FXCollections.observableArrayList(data.tableCustomers);
            } else {
                fullCustomerList = FXCollections.observableArrayList(data.activeCustomers);
            }

            lblTotalCustomers.setText(String.valueOf(data.totalCustomers));
            lblNewCustomers.setText(String.valueOf(data.newCustomersInMonth));

            currentPage = 1;
            applyPagination();
            setBusyState(false);
        });

        deleteTask.setOnFailed(event -> {
            setBusyState(false);
            Throwable ex = deleteTask.getException();

            if (isCustomerMissingError(ex)) {
                refreshCustomerData();
                showAlert(Alert.AlertType.INFORMATION, "Thông báo",
                        "Khách hàng không còn tồn tại. Hệ thống đã tự đồng bộ.");
                return;
            }

            String errorMessage = ex != null ? ex.getMessage() : "Lỗi không xác định";
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Xóa khách hàng thất bại.\n" + errorMessage);
        });

        Thread worker = new Thread(deleteTask, "crm-delete-customer-worker");
        worker.setDaemon(true);
        worker.start();
    }

    private void setBusyState(boolean busy) {
        customerTable.setDisable(busy);
        searchField.setDisable(busy);
        if (btnRefresh != null) {
            btnRefresh.setDisable(busy);
        }
    }

    private boolean isCustomerMissingError(Throwable ex) {
        Throwable current = ex;
        while (current != null) {
            String msg = current.getMessage();
            if (msg != null) {
                String normalized = msg.toLowerCase();
                if (normalized.contains("khach hang khong ton tai") ||
                        normalized.contains("khong tim thay khach hang")) {
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