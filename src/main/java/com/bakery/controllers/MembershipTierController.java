package com.bakery.controllers;

import com.bakery.models.dto.HangThanhVienDTO;
import com.bakery.services.CustomerTierService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.Optional;

public class MembershipTierController {

    @FXML private TableView<HangThanhVienDTO> tierTable;
    @FXML private TableColumn<HangThanhVienDTO, String> colTenHang;
    @FXML private TableColumn<HangThanhVienDTO, Integer> colDiemToiThieu;
    @FXML private TableColumn<HangThanhVienDTO, Double> colPhanTramGiamGia;
    @FXML private TableColumn<HangThanhVienDTO, Void> colThaoTac;

    private final CustomerTierService tierService = new CustomerTierService();
    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    public void initialize() {
        setupColumns();
        loadTiers();
        tierTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void setupColumns() {
        // Cột tên hạng: hiển thị tên và một thanh màu bên trái (tuỳ chọn)
        colTenHang.setCellValueFactory(new PropertyValueFactory<>("tenHang"));
        colTenHang.setCellFactory(col -> new TableCell<HangThanhVienDTO, String>() {
            private final Label nameLabel = new Label();
            private final HBox hbox = new HBox(8, new Label(" "), nameLabel); // khoảng trắng thay cho thanh màu

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    nameLabel.setText(item);
                    // Có thể tô màu theo tên hạng nếu muốn, tạm để mặc định
                    setGraphic(hbox);
                }
            }
        });

        colDiemToiThieu.setCellValueFactory(new PropertyValueFactory<>("diemToiThieu"));
        colPhanTramGiamGia.setCellValueFactory(new PropertyValueFactory<>("phanTramGiamGia"));

        // Cột thao tác: nút Sửa
        colThaoTac.setCellFactory(col -> new TableCell<HangThanhVienDTO, Void>() {
            private final Button btnEdit = new Button("Sửa");
            private final HBox pane = new HBox(5, btnEdit);

            {
                btnEdit.setOnAction(e -> {
                    HangThanhVienDTO tier = getTableView().getItems().get(getIndex());
                    if (tier != null) openEditTierDialog(tier);
                });

                btnEdit.setStyle("-fx-cursor: hand;");
                //căn giữa
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

    private void loadTiers() {
        try {
            ObservableList<HangThanhVienDTO> tiers = FXCollections.observableArrayList(
                    tierService.getAllTiers());
            tierTable.setItems(tiers);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể tải danh sách hạng.\n" + e.getMessage());
        }
    }

    @FXML
    private void openEditTierDialog(HangThanhVienDTO existingTier) {
        Dialog<HangThanhVienDTO> dialog = editTierFormDialog(existingTier);
        Optional<HangThanhVienDTO> result = dialog.showAndWait();
        result.ifPresent(tier -> {
            try {
                tier.setMaHang(existingTier.getMaHang()); // giữ nguyên mã hạng
                tierService.updateTier(tier);
                loadTiers();
            } catch (SQLException ex) {
                showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể cập nhật hạng.\n" + ex.getMessage());
            }
        });
    }

    /**
     * Tạo dialog sửa hạng với các trường: điểm tối thiểu, % giảm giá.
     */
    private Dialog<HangThanhVienDTO> editTierFormDialog(HangThanhVienDTO existing) {
        Dialog<HangThanhVienDTO> dialog = new Dialog<>();
        dialog.setTitle("Sửa hạng thành viên: " + existing.getTenHang());
        dialog.setResizable(false);

        // Nút Lưu
        ButtonType saveButtonType = new ButtonType("Lưu", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // Form sửa
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        // Ô Tên hạng (disabled, tối màu)
        TextField txtTenHang = new TextField(existing.getTenHang());
        txtTenHang.setEditable(false);
        txtTenHang.setStyle("-fx-background-color: #e8e8e8; -fx-text-fill: #78716c; -fx-border-color: #d4c4b7; -fx-border-radius: 8; -fx-padding: 8; -fx-font-size: 14px;");

        TextField txtDiemToiThieu = new TextField(String.valueOf(existing.getDiemToiThieu()));
        txtDiemToiThieu.setPromptText("0");

        TextField txtPhanTram = new TextField(String.valueOf(existing.getPhanTramGiamGia()));
        txtPhanTram.setPromptText("0.0");

        grid.add(new Label("Tên hạng:"), 0, 0);
        grid.add(txtTenHang, 1, 0);
        grid.add(new Label("Điểm tối thiểu:"), 0, 1);
        grid.add(txtDiemToiThieu, 1, 1);
        grid.add(new Label("% Giảm giá:"), 0, 2);
        grid.add(txtPhanTram, 1, 2);

        dialog.getDialogPane().setContent(grid);

        // Xử lý kết quả khi nhấn Lưu
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    HangThanhVienDTO dto = new HangThanhVienDTO();
                    dto.setMaHang(existing.getMaHang());           // giữ nguyên mã hạng
                    dto.setTenHang(existing.getTenHang());        // tên không đổi
                    dto.setDiemToiThieu(Integer.parseInt(txtDiemToiThieu.getText().trim()));
                    dto.setPhanTramGiamGia(Double.parseDouble(txtPhanTram.getText().trim()));
                    return dto;
                } catch (NumberFormatException e) {
                    showAlert(Alert.AlertType.ERROR, "Lỗi nhập liệu",
                            "Vui lòng nhập số hợp lệ cho điểm và phần trăm.");
                    return null;
                }
            }
            return null;
        });

        return dialog;
    }

    private int getCurrentEmployeeId() {
        // Tạm trả về 1, sau này kết nối SessionManager
        return 1;
    }

    @FXML
    private void closeDialog() {
        if (stage != null) {
            stage.close();
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type, message, ButtonType.OK);
        alert.setTitle(title);
        alert.showAndWait();
    }
}