package com.bakery.controllers;

import com.bakery.models.dto.KhachHangDTO;
import com.bakery.services.CustomerService;
import javafx.event.ActionEvent;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class CustomerDeletedController {
    @FXML private TableView<KhachHangDTO> deletedTable;
    @FXML private Label lblPageInfo;
    @FXML private Label lblPageEllipsis;
    @FXML private Button btnPrevPage;
    @FXML private Button btnPage1;
    @FXML private Button btnPage2;
    @FXML private Button btnPage3;
    @FXML private Button btnNextPage;

    // Các cột được định nghĩa trong FXML
    @FXML private TableColumn<KhachHangDTO, Integer> colMaKH;
    @FXML private TableColumn<KhachHangDTO, String> colTenKH;
    @FXML private TableColumn<KhachHangDTO, String> colSDT;
    @FXML private TableColumn<KhachHangDTO, LocalDateTime> colNgayXoa;
    @FXML private TableColumn<KhachHangDTO, Void> colThaoTac;

    private Stage stage;
    private final CustomerService customerService = new CustomerService();
    private final ObservableList<KhachHangDTO> allDeletedCustomers = FXCollections.observableArrayList();
    private static final int PAGE_SIZE = 8;
    private int currentPage = 1;
    private int totalPages = 1;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    public void initialize() {
        setupColumns();
        loadDeletedCustomers();
        deletedTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void setupColumns() {
        // Mã KH
        colMaKH.setCellValueFactory(new PropertyValueFactory<>("maKH"));

        // Tên khách hàng
        colTenKH.setCellValueFactory(new PropertyValueFactory<>("hoTen"));

        // SĐT
        colSDT.setCellValueFactory(new PropertyValueFactory<>("sdt"));

        // Ngày xoá
        colNgayXoa.setCellValueFactory(new PropertyValueFactory<>("thoiDiemXoa"));
        colNgayXoa.setCellFactory(col -> new TableCell<KhachHangDTO, LocalDateTime>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
                }
            }
        });

        // Thao tác (nút Khôi phục)
        colThaoTac.setCellFactory(col -> new TableCell<KhachHangDTO, Void>() {
            private final Button btnRestore = new Button("🔄 Khôi phục");
            {
                btnRestore.setOnAction(e -> {
                    KhachHangDTO kh = getTableView().getItems().get(getIndex());
                    restoreCustomer(kh);
                });
                btnRestore.setStyle("-fx-text-fill: #7d562d; -fx-cursor: hand; -fx-font-size: 12px;");
                setAlignment(javafx.geometry.Pos.CENTER);

            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btnRestore);
                }
            }
        });
    }

    private void loadDeletedCustomers() {
        List<KhachHangDTO> list = customerService.getDeletedCustomers();
        allDeletedCustomers.setAll(list);
        currentPage = 1;
        applyPagination();
    }

    private void applyPagination() {
        if (allDeletedCustomers.isEmpty()) {
            totalPages = 1;
            currentPage = 1;
            deletedTable.setItems(FXCollections.observableArrayList());
            lblPageInfo.setText("Hiển thị 0-0 của 0 khách hàng");
            updatePaginationControls();
            return;
        }

        totalPages = (int) Math.ceil((double) allDeletedCustomers.size() / PAGE_SIZE);
        if (currentPage < 1) currentPage = 1;
        if (currentPage > totalPages) currentPage = totalPages;

        int fromIndex = (currentPage - 1) * PAGE_SIZE;
        int toIndex = Math.min(fromIndex + PAGE_SIZE, allDeletedCustomers.size());

        deletedTable.setItems(FXCollections.observableArrayList(allDeletedCustomers.subList(fromIndex, toIndex)));
        lblPageInfo.setText("Hiển thị " + (fromIndex + 1) + "-" + toIndex + " của " + allDeletedCustomers.size() + " khách hàng");
        updatePaginationControls();
    }

    private void updatePaginationControls() {
        if (btnPrevPage == null || btnNextPage == null) {
            return;
        }

        boolean hasData = !allDeletedCustomers.isEmpty();
        btnPrevPage.setDisable(!hasData || currentPage <= 1);
        btnNextPage.setDisable(!hasData || currentPage >= totalPages);

        Button[] pageButtons = {btnPage1, btnPage2, btnPage3};
        int start = Math.max(1, currentPage - 1);
        if (start + 2 > totalPages) {
            start = Math.max(1, totalPages - 2);
        }

        for (int i = 0; i < pageButtons.length; i++) {
            Button button = pageButtons[i];
            int pageNumber = start + i;
            boolean visible = hasData && pageNumber <= totalPages;

            button.setVisible(visible);
            button.setManaged(visible);

            if (visible) {
                button.setText(String.valueOf(pageNumber));
                button.getStyleClass().remove("pagination-button-active");
                if (pageNumber == currentPage) {
                    button.getStyleClass().add("pagination-button-active");
                }
                button.setDisable(pageNumber == currentPage);
            }
        }

        boolean showEllipsis = hasData && totalPages > 3 && (start + 2) < totalPages;
        if (lblPageEllipsis != null) {
            lblPageEllipsis.setVisible(showEllipsis);
            lblPageEllipsis.setManaged(showEllipsis);
        }
    }

    @FXML
    private void goToPreviousPage() {
        if (currentPage > 1) {
            currentPage--;
            applyPagination();
        }
    }

    @FXML
    private void goToNextPage() {
        if (currentPage < totalPages) {
            currentPage++;
            applyPagination();
        }
    }

    @FXML
    private void goToSelectedPage(ActionEvent event) {
        if (!(event.getSource() instanceof Button)) {
            return;
        }
        Button clickedButton = (Button) event.getSource();
        try {
            int selectedPage = Integer.parseInt(clickedButton.getText());
            if (selectedPage >= 1 && selectedPage <= totalPages && selectedPage != currentPage) {
                currentPage = selectedPage;
                applyPagination();
            }
        } catch (NumberFormatException ignored) {
            // Ignore invalid page labels.
        }
    }

    private void restoreCustomer(KhachHangDTO customer) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                "Khôi phục khách hàng \"" + customer.getHoTen() + "\"?",
                ButtonType.YES, ButtonType.NO);
        alert.setTitle("Xác nhận khôi phục");
        alert.showAndWait().ifPresent(type -> {
            if (type == ButtonType.YES) {
                try {
                    customerService.restoreCustomer(customer.getMaKH());
                    loadDeletedCustomers();
                } catch (SQLException e) {
                    new Alert(Alert.AlertType.ERROR, "Khoi phuc that bai.\n" + e.getMessage(), ButtonType.OK).show();
                }
            }
        });
    }

    @FXML
    private void closeDialog() {
        if (stage != null) {
            stage.close();
        }
    }

}