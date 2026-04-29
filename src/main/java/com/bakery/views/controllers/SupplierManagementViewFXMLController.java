package com.bakery.views.controllers;

import com.bakery.model.dto.NhaCungCapDTO;
import com.bakery.services.NhaCungCapService;
import com.bakery.utils.UserSession;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.net.URL;

import java.util.List;

public class SupplierManagementViewFXMLController {

    @FXML private TextField txtTimKiem;
    @FXML private TableView<NhaCungCapDTO> tvNhaCungCap;
    @FXML private TableColumn<NhaCungCapDTO, String> colMaNCC;
    @FXML private TableColumn<NhaCungCapDTO, String> colTenNCC;
    @FXML private TableColumn<NhaCungCapDTO, String> colSdt;
    @FXML private TableColumn<NhaCungCapDTO, String> colDiaChi;

    @FXML private TextField txtMaNCC;
    @FXML private TextField txtTenNCC;
    @FXML private TextField txtSdt;
    @FXML private TextArea txtDiaChi;

    @FXML private Button btnThem;
    @FXML private Button btnLuu;
    @FXML private Button btnXoa;
    @FXML private Button btnHuy;

    private final NhaCungCapService nhaCungCapService = new NhaCungCapService();
    private final ObservableList<NhaCungCapDTO> nccList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupTable();
        setupListeners();
        capNhatTrangThaiUI(true);
        loadData();
    }

    private void setupTable() {
        colMaNCC.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getMaNCC())));
        colTenNCC.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTenNCC()));
        colSdt.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSdt()));
        colDiaChi.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDiaChi()));

        tvNhaCungCap.setItems(nccList);
    }

    private void setupListeners() {
        tvNhaCungCap.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                hienThiChiTiet(newSelection);
            }
        });
        
        txtTimKiem.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.trim().isEmpty()) {
                onTaiLai();
            }
        });
    }

    private void hienThiChiTiet(NhaCungCapDTO ncc) {
        txtMaNCC.setText(String.valueOf(ncc.getMaNCC()));
        txtTenNCC.setText(ncc.getTenNCC());
        txtSdt.setText(ncc.getSdt());
        txtDiaChi.setText(ncc.getDiaChi());
        capNhatTrangThaiUI(false);
    }

    @FXML
    private void onTimKiem() {
        String tuKhoa = txtTimKiem.getText();
        Task<List<NhaCungCapDTO>> task = new Task<>() {
            @Override
            protected List<NhaCungCapDTO> call() throws Exception {
                return nhaCungCapService.timKiemNhaCungCap(tuKhoa);
            }
        };
        
        task.setOnSucceeded(e -> {
            nccList.setAll(task.getValue());
        });
        
        task.setOnFailed(e -> {
            showAlert(AlertType.ERROR, "Lỗi", "Không thể tìm kiếm: " + task.getException().getMessage());
        });

        new Thread(task).start();
    }

    @FXML
    private void onTaiLai() {
        txtTimKiem.clear();
        loadData();
    }

    private void loadData() {
        Task<List<NhaCungCapDTO>> task = new Task<>() {
            @Override
            protected List<NhaCungCapDTO> call() throws Exception {
                return nhaCungCapService.layDanhSachNhaCungCap();
            }
        };

        task.setOnSucceeded(e -> {
            nccList.setAll(task.getValue());
            onHuy(); // Clear form when reload
        });

        task.setOnFailed(e -> {
            showAlert(AlertType.ERROR, "Lỗi", "Không thể tải danh sách nhà cung cấp: " + task.getException().getMessage());
        });

        new Thread(task).start();
    }

    @FXML
    private void onThem() {
        try {
            NhaCungCapDTO dto = taoDTO();
            
            Task<Void> task = new Task<>() {
                @Override
                protected Void call() throws Exception {
                    nhaCungCapService.themNhaCungCap(dto);
                    return null;
                }
            };
            
            task.setOnSucceeded(e -> {
                showAlert(AlertType.INFORMATION, "Thành công", "Đã thêm nhà cung cấp mới.");
                loadData();
            });
            
            task.setOnFailed(e -> {
                showAlert(AlertType.ERROR, "Lỗi", task.getException().getMessage());
            });
            
            new Thread(task).start();
        } catch (Exception ex) {
            showAlert(AlertType.WARNING, "Cảnh báo", ex.getMessage());
        }
    }

    @FXML
    private void onLuu() {
        try {
            NhaCungCapDTO dto = taoDTO();
            if (txtMaNCC.getText() == null || txtMaNCC.getText().isEmpty()) {
                throw new Exception("Vui lòng chọn một nhà cung cấp để cập nhật.");
            }
            dto.setMaNCC(Integer.parseInt(txtMaNCC.getText()));
            
            Task<Void> task = new Task<>() {
                @Override
                protected Void call() throws Exception {
                    nhaCungCapService.suaNhaCungCap(dto);
                    return null;
                }
            };
            
            task.setOnSucceeded(e -> {
                showAlert(AlertType.INFORMATION, "Thành công", "Đã cập nhật thông tin nhà cung cấp.");
                loadData();
            });
            
            task.setOnFailed(e -> {
                showAlert(AlertType.ERROR, "Lỗi", task.getException().getMessage());
            });
            
            new Thread(task).start();
        } catch (Exception ex) {
            showAlert(AlertType.WARNING, "Cảnh báo", ex.getMessage());
        }
    }

    @FXML
    private void onXoa() {
        NhaCungCapDTO selected = tvNhaCungCap.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(AlertType.WARNING, "Cảnh báo", "Vui lòng chọn nhà cung cấp cần xóa.");
            return;
        }

        Alert confirmAlert = new Alert(AlertType.CONFIRMATION, "Bạn có chắc chắn muốn xóa đối tác này?", ButtonType.YES, ButtonType.NO);
        confirmAlert.setHeaderText("Xác nhận xóa");
        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                int maNV = UserSession.getCurrentUser() != null ? UserSession.getCurrentUser().getMaNV() : 1; // Default fallback for safety
                
                Task<Void> task = new Task<>() {
                    @Override
                    protected Void call() throws Exception {
                        nhaCungCapService.xoaNhaCungCap(selected.getMaNCC(), maNV);
                        return null;
                    }
                };
                
                task.setOnSucceeded(e -> {
                    showAlert(AlertType.INFORMATION, "Thành công", "Đã xóa nhà cung cấp.");
                    loadData();
                });
                
                task.setOnFailed(e -> {
                    showAlert(AlertType.ERROR, "Lỗi", task.getException().getMessage());
                });
                
                new Thread(task).start();
            }
        });
    }

    @FXML
    private void onHuy() {
        txtMaNCC.clear();
        txtTenNCC.clear();
        txtSdt.clear();
        txtDiaChi.clear();
        tvNhaCungCap.getSelectionModel().clearSelection();
        capNhatTrangThaiUI(true);
    }

    private NhaCungCapDTO taoDTO() throws Exception {
        if (txtTenNCC.getText() == null || txtTenNCC.getText().trim().isEmpty()) {
            throw new Exception("Tên nhà cung cấp không được để trống.");
        }
        
        NhaCungCapDTO dto = new NhaCungCapDTO();
        dto.setTenNCC(txtTenNCC.getText().trim());
        dto.setSdt(txtSdt.getText() != null ? txtSdt.getText().trim() : "");
        dto.setDiaChi(txtDiaChi.getText() != null ? txtDiaChi.getText().trim() : "");
        return dto;
    }

    private void capNhatTrangThaiUI(boolean isAddMode) {
        btnThem.setVisible(isAddMode);
        btnThem.setManaged(isAddMode);
        
        btnLuu.setVisible(!isAddMode);
        btnLuu.setManaged(!isAddMode);
        
        btnXoa.setDisable(isAddMode);
    }

    private void showAlert(AlertType type, String title, String content) {
        Platform.runLater(() -> {
            Alert alert = new Alert(type);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(content);
            alert.showAndWait();
        });
    }

    @FXML
    private void onVeMenu() {
        moScene("/fxml/MainMenuView.fxml", "H3K Bakery - Menu chức năng", 1366, 768);
    }

    private void moScene(String fxmlPath, String title, int width, int height) {
        try {
            URL fxmlUrl = getClass().getResource(fxmlPath);
            if (fxmlUrl == null) throw new RuntimeException("Không tìm thấy " + fxmlPath);
            
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Scene scene = new Scene(loader.load(), width, height);
            
            if ("/fxml/MainMenuView.fxml".equals(fxmlPath)) {
                MainMenuViewFXMLController controller = loader.getController();
                controller.khoiTaoThongTinDangNhap(UserSession.getCurrentUser());
            }
            
            URL cssUrl = getClass().getResource("/css/bakery.css");
            if (cssUrl != null) scene.getStylesheets().add(cssUrl.toExternalForm());
            
            Stage stage = (Stage) txtTenNCC.getScene().getWindow();
            stage.setTitle(title);
            stage.setScene(scene);
            stage.centerOnScreen();
        } catch (Exception ex) {
            showAlert(AlertType.ERROR, "Lỗi chuyển màn hình", ex.getMessage());
            ex.printStackTrace();
        }
    }
}

