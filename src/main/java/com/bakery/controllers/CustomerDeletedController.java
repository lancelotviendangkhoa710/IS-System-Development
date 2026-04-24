package com.bakery.controllers;

import com.bakery.models.dto.KhachHangDTO;
import com.bakery.services.CustomerService;
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

    // Các cột được định nghĩa trong FXML
    @FXML private TableColumn<KhachHangDTO, Integer> colMaKH;
    @FXML private TableColumn<KhachHangDTO, String> colTenKH;
    @FXML private TableColumn<KhachHangDTO, String> colSDT;
    @FXML private TableColumn<KhachHangDTO, LocalDateTime> colNgayXoa;
    @FXML private TableColumn<KhachHangDTO, Void> colThaoTac;

    private Stage stage;
    private MainController mainController;
    private final CustomerService customerService = new CustomerService();

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
                btnRestore.setStyle("-fx-background-color: transparent; -fx-text-fill: #7d562d; -fx-cursor: hand; -fx-font-size: 12px;");
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
        ObservableList<KhachHangDTO> data = FXCollections.observableArrayList(list);
        deletedTable.setItems(data);
        lblPageInfo.setText("Hiển thị " + data.size() + " khách hàng");
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
        } else if (mainController != null) {
            mainController.navigateTo("CustomerInfoView.fxml");
        }
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }
}