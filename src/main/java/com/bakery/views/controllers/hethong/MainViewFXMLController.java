package com.bakery.views.controllers.hethong;

import com.bakery.main.App;
import com.bakery.model.dto.nhansu.ChucNangDTO;
import com.bakery.model.dto.nhansu.NhanVienDTO;
import com.bakery.model.dto.nhansu.VaiTroDTO;
import com.bakery.services.nhansu.XacThucService;
import com.bakery.utils.SessionContext;
import com.bakery.utils.UserSession;
import com.bakery.views.controllers.nhansu.DangNhapViewFXMLController;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Controller chính xử lý đăng nhập, đăng ký và điều hướng ban đầu.
 */
public class MainViewFXMLController {
    private final XacThucService xacThucService = new XacThucService();
    private static final List<String> REGISTER_ROLE_ORDER = List.of("Thu ngân", "Quản lý", "Thợ bếp");

    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private Button btnLogin;
    @FXML private Label lblLoginMessage;
    
    @FXML private TextField txtRegisterHoTen;
    @FXML private TextField txtRegisterSoDienThoai;
    @FXML private TextField txtRegisterUsername;
    @FXML private PasswordField txtRegisterPassword;
    @FXML private ComboBox<RoleOption> cboRegisterRole;
    @FXML private TextField txtRegisterManagerCode;
    @FXML private Button btnRegister;
    @FXML private Label lblRegisterMessage;

    @FXML private Label lblWelcome;
    @FXML private Label lblRole;
    @FXML private Label lblCurrentView;
    @FXML private Label lblDashboardMessage;
    @FXML private VBox menuContainer;
    
    @FXML private VBox overviewPane;
    @FXML private VBox changePasswordPane;
    @FXML private Label lblPermissionCount;
    @FXML private Label lblPermissionHint;
    
    @FXML private PasswordField txtCurrentPassword;
    @FXML private PasswordField txtNewPassword;
    @FXML private PasswordField txtConfirmPassword;
    @FXML private Button btnChangePassword;

    private String pendingLoginMessage;

    @FXML
    private void initialize() {
        if (txtUsername != null) {
            initializeLoginView();
        }
        if (lblWelcome != null) {
            initializeDashboardView();
        }
    }

    public void setLoginInfo(String message) {
        pendingLoginMessage = message;
        if (lblLoginMessage != null) {
            lblLoginMessage.setText(message);
        }
    }

    @FXML
    private void handleLogin() {
        setLoginFormDisabled(true);
        lblLoginMessage.setText("Đang xác thực tài khoản...");

        Task<NhanVienDTO> task = new Task<>() {
            @Override
            protected NhanVienDTO call() throws Exception {
                return xacThucService.dangNhap(getUsername(), getPassword());
            }
        };

        task.setOnSucceeded(event -> {
            setLoginFormDisabled(false);
            try {
                // Lấy session đã tạo và chuyển sang MainMenuView
                SessionContext.AuthSession session = xacThucService.layPhienHienTai();
                NhanVienDTO nhanVien = task.getValue();

                // Đồng bộ tenVaiTro từ session sang DTO để MainMenuView hiển thị đúng
                if (session != null) {
                    nhanVien.setTenVaiTro(session.getTenVaiTro());
                }

                // Set UserSession cho toàn bộ ứng dụng sử dụng
                UserSession.setCurrentUser(nhanVien);

                // Chuyển sang giao diện MainMenuView
                chuyenSangMainMenu(nhanVien);
            } catch (Exception ex) {
                lblLoginMessage.setText(ex.getMessage());
            }
        });

        task.setOnFailed(event -> {
            setLoginFormDisabled(false);
            lblLoginMessage.setText(resolveErrorMessage(task.getException()));
        });

        startBackgroundTask(task);
    }

    @FXML
    private void handleShowOverview() {
        if (overviewPane == null || changePasswordPane == null) {
            return;
        }
        overviewPane.setVisible(true);
        overviewPane.setManaged(true);
        changePasswordPane.setVisible(false);
        changePasswordPane.setManaged(false);
        lblCurrentView.setText("Tổng quan quyền truy cập");
        lblDashboardMessage.setText("Chọn một mục menu bên trái để xem quyền được cấp.");
    }

    @FXML
    private void handleShowChangePassword() {
        if (overviewPane == null || changePasswordPane == null) {
            return;
        }
        overviewPane.setVisible(false);
        overviewPane.setManaged(false);
        changePasswordPane.setVisible(true);
        changePasswordPane.setManaged(true);
        lblCurrentView.setText("Đổi mật khẩu");
        lblDashboardMessage.setText("Sau khi đổi mật khẩu thành công, hệ thống sẽ bắt buộc đăng xuất.");
        clearChangePasswordForm();
    }

    @FXML
    private void handleChangePassword() {
        setChangePasswordDisabled(true);
        lblDashboardMessage.setText("Đang cập nhật mật khẩu...");

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                xacThucService.doiMatKhau(
                        txtCurrentPassword.getText(),
                        txtNewPassword.getText(),
                        txtConfirmPassword.getText()
                );
                return null;
            }
        };

        task.setOnSucceeded(event -> {
            setChangePasswordDisabled(false);
            try {
                switchScene(App.LOGIN_VIEW, "Đổi mật khẩu thành công. Vui lòng đăng nhập lại.");
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Lỗi điều hướng", ex.getMessage());
            }
        });

        task.setOnFailed(event -> {
            setChangePasswordDisabled(false);
            lblDashboardMessage.setText(resolveErrorMessage(task.getException()));
        });

        startBackgroundTask(task);
    }

    @FXML
    private void handleLogout() {
        xacThucService.dangXuat();
        try {
            switchScene(App.LOGIN_VIEW, "Đã đăng xuất thành công.");
        } catch (Exception ex) {
            showAlert(Alert.AlertType.ERROR, "Lỗi đăng xuất", ex.getMessage());
        }
    }

    private void initializeLoginView() {
        if (cboRegisterRole != null) {
            cboRegisterRole.setItems(FXCollections.observableArrayList());
            loadRegisterRoles();
        }
        if (pendingLoginMessage != null && !pendingLoginMessage.isBlank()) {
            lblLoginMessage.setText(pendingLoginMessage);
        } else {
            lblLoginMessage.setText("Nhập tài khoản để bắt đầu phiên làm việc.");
        }
        if (lblRegisterMessage != null) {
            lblRegisterMessage.setText("Đăng ký nhân viên mới với 3 vai trò: Thu ngân, Quản lý, Thợ bếp.");
        }
    }

    @FXML
    private void handleRegister() {
        setRegisterFormDisabled(true);
        lblRegisterMessage.setText("Đang tạo tài khoản nhân viên...");

        Task<Integer> task = new Task<>() {
            @Override
            protected Integer call() throws Exception {
                RoleOption selectedRole = cboRegisterRole.getSelectionModel().getSelectedItem();
                return xacThucService.dangKy(
                        valueOf(txtRegisterHoTen),
                        valueOf(txtRegisterSoDienThoai),
                        valueOf(txtRegisterUsername),
                        txtRegisterPassword.getText(),
                        valueOf(txtRegisterManagerCode),
                        selectedRole == null ? null : selectedRole.id()
                );
            }
        };

        task.setOnSucceeded(event -> {
            setRegisterFormDisabled(false);
            int maNhanVien = task.getValue();
            lblRegisterMessage.setText("Đăng ký thành công. Mã nhân viên mới: " + maNhanVien);
            clearRegisterForm();
            lblLoginMessage.setText("Tài khoản mới đã được tạo. Có thể đăng nhập ngay.");
            txtUsername.requestFocus();
        });

        task.setOnFailed(event -> {
            setRegisterFormDisabled(false);
            lblRegisterMessage.setText(resolveErrorMessage(task.getException()));
        });

        startBackgroundTask(task);
    }

    private void initializeDashboardView() {
        SessionContext.AuthSession session = xacThucService.layPhienHienTai();
        if (session == null) {
            Platform.runLater(() -> {
                try {
                    switchScene(App.LOGIN_VIEW, "Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại.");
                } catch (Exception ex) {
                    showAlert(Alert.AlertType.ERROR, "Lỗi phiên đăng nhập", ex.getMessage());
                }
            });
            return;
        }

        lblWelcome.setText(session.getHoTen());
        lblRole.setText(session.getTenVaiTro());
        lblPermissionCount.setText(String.valueOf(session.getQuyen().size()));
        lblPermissionHint.setText("Menu được render từ quyền trong CSDL cho role hiện tại.");
        renderPermissionMenu();
        handleShowOverview();
    }

    private void renderPermissionMenu() {
        menuContainer.getChildren().clear();
        try {
            List<ChucNangDTO> danhSachChucNang = xacThucService.layQuyenPhienHienTai();
            for (ChucNangDTO chucNang : danhSachChucNang) {
                Button button = new Button(chucNang.getTenChucNang());
                button.setMaxWidth(Double.MAX_VALUE);
                button.setWrapText(true);
                button.setOnAction(event -> {
                    handleShowOverview();
                    lblCurrentView.setText(chucNang.getTenChucNang());
                    if (chucNang.getMoTa() == null || chucNang.getMoTa().isBlank()) {
                        lblDashboardMessage.setText("Role hiện tại được truy cập chức năng \"" + chucNang.getTenChucNang() + "\".");
                    } else {
                        lblDashboardMessage.setText(chucNang.getMoTa());
                    }
                });
                VBox.setVgrow(button, Priority.NEVER);
                menuContainer.getChildren().add(button);
            }
        } catch (Exception ex) {
            lblDashboardMessage.setText(resolveErrorMessage(ex));
        }
    }

    private String getUsername() {
        return txtUsername.getText() == null ? "" : txtUsername.getText();
    }

    private String getPassword() {
        return txtPassword.getText() == null ? "" : txtPassword.getText();
    }

    private void setLoginFormDisabled(boolean disabled) {
        txtUsername.setDisable(disabled);
        txtPassword.setDisable(disabled);
        btnLogin.setDisable(disabled);
    }

    private void setChangePasswordDisabled(boolean disabled) {
        txtCurrentPassword.setDisable(disabled);
        txtNewPassword.setDisable(disabled);
        txtConfirmPassword.setDisable(disabled);
        btnChangePassword.setDisable(disabled);
    }

    private void setRegisterFormDisabled(boolean disabled) {
        txtRegisterHoTen.setDisable(disabled);
        txtRegisterSoDienThoai.setDisable(disabled);
        txtRegisterUsername.setDisable(disabled);
        txtRegisterPassword.setDisable(disabled);
        txtRegisterManagerCode.setDisable(disabled);
        cboRegisterRole.setDisable(disabled);
        btnRegister.setDisable(disabled);
    }

    private void clearChangePasswordForm() {
        txtCurrentPassword.clear();
        txtNewPassword.clear();
        txtConfirmPassword.clear();
    }

    private void clearRegisterForm() {
        txtRegisterHoTen.clear();
        txtRegisterSoDienThoai.clear();
        txtRegisterUsername.clear();
        txtRegisterPassword.clear();
        txtRegisterManagerCode.clear();
        if (!cboRegisterRole.getItems().isEmpty()) {
            cboRegisterRole.getSelectionModel().selectFirst();
        }
    }

    private void loadRegisterRoles() {
        Task<List<VaiTroDTO>> task = new Task<>() {
            @Override
            protected List<VaiTroDTO> call() throws Exception {
                return xacThucService.layDanhSachVaiTroDangHoatDong();
            }
        };

        task.setOnSucceeded(event -> {
            List<RoleOption> roles = sapXepVaiTroDangKy(task.getValue());
            cboRegisterRole.getItems().setAll(roles);
            if (!roles.isEmpty()) {
                cboRegisterRole.getSelectionModel().selectFirst();
                lblRegisterMessage.setText("Đã tải danh sách vai trò đăng ký.");
            } else {
                lblRegisterMessage.setText("Không tìm thấy vai trò hợp lệ cho đăng ký.");
            }
        });

        task.setOnFailed(event -> lblRegisterMessage.setText(resolveErrorMessage(task.getException())));
        startBackgroundTask(task);
    }

    private String valueOf(TextField field) {
        return field.getText() == null ? "" : field.getText().trim();
    }

    private List<RoleOption> sapXepVaiTroDangKy(List<VaiTroDTO> roles) {
        Map<String, VaiTroDTO> roleMap = roles.stream()
                .collect(java.util.stream.Collectors.toMap(
                        role -> normalizeRoleName(role.getTenVaiTro()),
                        role -> role,
                        (existing, ignored) -> existing
                ));

        List<RoleOption> orderedRoles = new ArrayList<>();
        for (String roleName : REGISTER_ROLE_ORDER) {
            VaiTroDTO role = roleMap.get(normalizeRoleName(roleName));
            if (role != null) {
                orderedRoles.add(new RoleOption(role.getMaVaiTro(), roleName));
            }
        }
        return orderedRoles;
    }

    private String normalizeRoleName(String value) {
        if (value == null) {
            return "";
        }
        String normalized = Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .replace("\u0111", "d")
                .replace("\u0110", "D");
        return normalized.trim().replaceAll("\\s+", " ").toUpperCase(Locale.ROOT);
    }

    private String resolveErrorMessage(Throwable error) {
        Throwable current = error;
        while (current != null && current.getCause() != null) {
            current = current.getCause();
        }
        if (current == null || current.getMessage() == null || current.getMessage().isBlank()) {
            return "Đã xảy ra lỗi không xác định.";
        }
        return current.getMessage();
    }

    private void startBackgroundTask(Task<?> task) {
        Thread worker = new Thread(task);
        worker.setDaemon(true);
        worker.start();
    }

    private void chuyenSangMainMenu(NhanVienDTO nhanVien) throws Exception {
        FXMLLoader loader = new FXMLLoader(App.class.getResource(App.MAIN_MENU_VIEW));
        Parent root = loader.load();

        MainMenuViewFXMLController menuController = loader.getController();
        menuController.khoiTaoThongTinDangNhap(nhanVien);

        Scene scene = new Scene(root, 1366, 768);
        java.net.URL cssUrl = App.class.getResource("/css/bakery.css");
        if (cssUrl != null) {
            scene.getStylesheets().add(cssUrl.toExternalForm());
        }

        Stage stage = currentStage();
        stage.setTitle("H3K Bakery - Main Menu");
        stage.setResizable(true);
        stage.setMinWidth(1280);
        stage.setMinHeight(720);
        stage.setScene(scene);
        stage.centerOnScreen();
    }

    private void switchScene(String viewPath, String loginMessage) throws Exception {
        FXMLLoader loader = new FXMLLoader(App.class.getResource(viewPath));
        Parent root = loader.load();
        
        Object controller = loader.getController();
        if (loginMessage != null) {
            if (controller instanceof MainViewFXMLController) {
                ((MainViewFXMLController) controller).setLoginInfo(loginMessage);
            } else if (controller instanceof DangNhapViewFXMLController) {
                ((DangNhapViewFXMLController) controller).setLoginInfo(loginMessage);
            }
        }

        Scene scene = new Scene(root);
        java.net.URL cssUrl = App.class.getResource("/css/bakery.css");
        if (cssUrl != null) {
            scene.getStylesheets().add(cssUrl.toExternalForm());
        }

        Stage stage = currentStage();
        stage.setScene(scene);
        stage.sizeToScene();
        stage.centerOnScreen();
    }

    private Stage currentStage() {
        Node node = txtUsername != null ? txtUsername : lblWelcome;
        return (Stage) node.getScene().getWindow();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private record RoleOption(int id, String label) {
        @Override
        public String toString() {
            return label;
        }
    }
}
