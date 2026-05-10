package com.bakery.views.controllers.nhansu;

import com.bakery.model.dto.hethong.CaLamViecDTO;
import com.bakery.model.dto.nhansu.NhanVienDTO;
import com.bakery.model.dto.nhansu.VaiTroDTO;
import com.bakery.services.hethong.CaLamViecService;
import com.bakery.services.nhansu.PhanQuyenService;
import com.bakery.services.nhansu.XacThucService;
import com.bakery.utils.UserSession;
import com.bakery.views.controllers.BaseController;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public class DangNhapViewFXMLController extends BaseController {
    private static final List<String> REGISTER_ROLE_ORDER = List.of("Quản lý", "Thu ngân", "Thợ bếp", "Thủ kho");

    @FXML private VBox panelStartScreen;
    @FXML private VBox panelLogin;
    @FXML private VBox panelRegister;

    @FXML private TextField txtTenDangNhap;
    @FXML private PasswordField txtMatKhau;
    @FXML private TextField txtMatKhauVisible;
    @FXML private Button btnDangNhap;
    @FXML private Button btnToggleMatKhau;

    @FXML private TextField txtRegisterHoTen;
    @FXML private TextField txtRegisterTenDangNhap;
    @FXML private TextField txtRegisterSoDienThoai;
    @FXML private PasswordField txtRegisterMatKhau;
    @FXML private TextField txtRegisterMatKhauVisible;
    @FXML private ComboBox<RoleOption> cboRegisterVaiTro;
    @FXML private TextField txtRegisterMaXacNhan;
    @FXML private Button btnTaoTaiKhoan;
    @FXML private Button btnToggleRegisterMatKhau;
    @FXML private Label lblRegisterThongBao;

    private final XacThucService xacThucService = new XacThucService();
    private final CaLamViecService caLamViecService = new CaLamViecService();
    private final PhanQuyenService phanQuyenService = new PhanQuyenService();

    @FXML
    private void initialize() {
        hienStartScreen();
        if (lblThongBao != null) {
            lblThongBao.setText("");
        }
        if (lblRegisterThongBao != null) {
            lblRegisterThongBao.setText("");
        }
        if (cboRegisterVaiTro != null) {
            cboRegisterVaiTro.setItems(FXCollections.observableArrayList());
            taiDanhSachVaiTroDangKy();
        }
        bindPasswordToggle(txtMatKhau, txtMatKhauVisible, btnToggleMatKhau);
        bindPasswordToggle(txtRegisterMatKhau, txtRegisterMatKhauVisible, btnToggleRegisterMatKhau);
    }

    public void setLoginInfo(String message) {
        if (message != null && !message.isBlank()) {
            hienDangNhap();
            hienThiLoiLabel(message);
        }
    }

    @FXML
    private void onMoDangNhap() {
        hienDangNhap();
    }

    @FXML
    private void onMoDangKy() {
        hienDangKy();
    }

    @FXML
    private void onQuayVeStart() {
        hienStartScreen();
    }

    @FXML
    private void onDangNhap() {
        String tenDangNhap = txtTenDangNhap.getText() == null ? "" : txtTenDangNhap.getText().trim();
        String matKhau = getFieldText(txtMatKhau, txtMatKhauVisible);

        if (tenDangNhap.isBlank() || matKhau.isBlank()) {
            hienDangNhap();
            hienThiLoiLabel("Vui lòng nhập đầy đủ tên đăng nhập và mật khẩu.");
            return;
        }

        setLoginFormDisabled(true);
        hienThiThanhCongLabel("Đang xác thực tài khoản...");

        Task<NhanVienDTO> task = new Task<>() {
            @Override
            protected NhanVienDTO call() throws Exception {
                return xacThucService.dangNhap(tenDangNhap, matKhau);
            }
        };

        task.setOnSucceeded(event -> {
            setLoginFormDisabled(false);
            try {
                NhanVienDTO nhanVien = task.getValue();
                UserSession.setCurrentUser(nhanVien);

                // Lần đăng nhập đầu tiên với mật khẩu seed → bắt buộc đổi mật khẩu
                if (nhanVien.isCanDoiMatKhau()) {
                    hienDialogDoiMatKhauBatBuoc(nhanVien, matKhau);
                    return;
                }

                if (phanQuyenService.laThuNgan(nhanVien)) {
                    CaLamViecDTO caHienTai = caLamViecService.layCaHienTai(nhanVien.getMaNV());
                    if (caHienTai != null) {
                        com.bakery.utils.SessionContext.getInstance().moCa(caHienTai.getMaCa());
                        quayLaiMenuChinh(txtTenDangNhap);
                        return;
                    }

                    transitionTo(txtTenDangNhap, "/fxml/MoCaView.fxml", "H3K Bakery - Mở ca làm việc", 1366, 768);
                    return;
                }

                quayLaiMenuChinh(txtTenDangNhap);
            } catch (Exception ex) {
                hienDangNhap();
                hienThiLoiLabel(resolveErrorMessage(ex));
            }
        });

        task.setOnFailed(event -> {
            setLoginFormDisabled(false);
            hienDangNhap();
            hienThiLoiLabel(resolveErrorMessage(task.getException()));
        });

        startBackgroundTask(task);
    }

    @FXML
    private void onTaoTaiKhoan() {
        RoleOption selectedRole = cboRegisterVaiTro.getSelectionModel().getSelectedItem();
        if (selectedRole == null) {
            hienDangKy();
            hienThiRegisterLoi("Vui lòng chọn vai trò.");
            return;
        }

        setRegisterFormDisabled(true);
        hienThiRegisterThanhCong("Đang tạo tài khoản...");

        Task<Integer> task = new Task<>() {
            @Override
            protected Integer call() throws Exception {
                return xacThucService.dangKy(
                        valueOf(txtRegisterHoTen),
                        valueOf(txtRegisterSoDienThoai),
                        valueOf(txtRegisterTenDangNhap),
                        getFieldText(txtRegisterMatKhau, txtRegisterMatKhauVisible),
                        valueOf(txtRegisterMaXacNhan),
                        selectedRole.id()
                );
            }
        };

        task.setOnSucceeded(event -> {
            setRegisterFormDisabled(false);
            clearRegisterForm();
            hienDangNhap();
            hienThiThanhCongLabel("Tạo tài khoản thành công, mã nhân viên mới: " + task.getValue());
            txtTenDangNhap.requestFocus();
        });

        task.setOnFailed(event -> {
            setRegisterFormDisabled(false);
            hienDangKy();
            hienThiRegisterLoi(resolveErrorMessage(task.getException()));
        });

        startBackgroundTask(task);
    }

    @FXML
    private void onToggleMatKhau() {
        togglePasswordField(txtMatKhau, txtMatKhauVisible, btnToggleMatKhau);
    }

    @FXML
    private void onToggleRegisterMatKhau() {
        togglePasswordField(txtRegisterMatKhau, txtRegisterMatKhauVisible, btnToggleRegisterMatKhau);
    }

    private void taiDanhSachVaiTroDangKy() {
        Task<List<VaiTroDTO>> task = new Task<>() {
            @Override
            protected List<VaiTroDTO> call() throws Exception {
                return xacThucService.layDanhSachVaiTroDangHoatDong();
            }
        };

        task.setOnSucceeded(event -> {
            List<RoleOption> roles = sapXepVaiTroDangKy(task.getValue());
            cboRegisterVaiTro.getItems().setAll(roles);
            if (!roles.isEmpty()) {
                cboRegisterVaiTro.getSelectionModel().selectFirst();
            }
        });

        task.setOnFailed(event -> hienThiRegisterLoi(resolveErrorMessage(task.getException())));
        startBackgroundTask(task);
    }

    private List<RoleOption> sapXepVaiTroDangKy(List<VaiTroDTO> roles) {
        Map<String, VaiTroDTO> roleMap = roles.stream()
                .collect(Collectors.toMap(
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

    /** Dialog bắt buộc đổi mật khẩu — không cho đóng cho đến khi đổi thành công */
    private void hienDialogDoiMatKhauBatBuoc(NhanVienDTO nhanVien, String matKhauCu) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Đổi mật khẩu bắt buộc");
        dialog.setHeaderText("⚠️ Đây là lần đăng nhập đầu tiên.\nVui lòng đặt mật khẩu mới trước khi tiếp tục.");
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setOnCloseRequest(e -> e.consume()); // Không cho đóng

        // Field mật khẩu mới với toggle hiện/ẩn
        PasswordField txtMoiMatKhau = new PasswordField();
        txtMoiMatKhau.setPromptText("Nhập mật khẩu mới");
        txtMoiMatKhau.getStyleClass().add("text-field");
        TextField txtMoiMatKhauVisible = new TextField();
        txtMoiMatKhauVisible.setPromptText("Nhập mật khẩu mới");
        txtMoiMatKhauVisible.getStyleClass().add("text-field");
        Button btnToggleMoi = new Button("Hiện");
        btnToggleMoi.getStyleClass().add("btn-secondary");

        // Field xác nhận mật khẩu với toggle hiện/ẩn
        PasswordField txtXacNhan = new PasswordField();
        txtXacNhan.setPromptText("Xác nhận mật khẩu mới");
        txtXacNhan.getStyleClass().add("text-field");
        TextField txtXacNhanVisible = new TextField();
        txtXacNhanVisible.setPromptText("Xác nhận mật khẩu mới");
        txtXacNhanVisible.getStyleClass().add("text-field");
        Button btnToggleXacNhan = new Button("Hiện");
        btnToggleXacNhan.getStyleClass().add("btn-secondary");

        Label lblLoi = new Label();
        lblLoi.getStyleClass().add("lbl-danger");
        lblLoi.setWrapText(true);

        // Bind toggle cho 2 field
        bindPasswordToggle(txtMoiMatKhau, txtMoiMatKhauVisible, btnToggleMoi);
        bindPasswordToggle(txtXacNhan, txtXacNhanVisible, btnToggleXacNhan);
        btnToggleMoi.setOnAction(e -> togglePasswordField(txtMoiMatKhau, txtMoiMatKhauVisible, btnToggleMoi));
        btnToggleXacNhan.setOnAction(e -> togglePasswordField(txtXacNhan, txtXacNhanVisible, btnToggleXacNhan));

        javafx.scene.layout.HBox rowMoi = new javafx.scene.layout.HBox(8, txtMoiMatKhau, txtMoiMatKhauVisible, btnToggleMoi);
        javafx.scene.layout.HBox.setHgrow(txtMoiMatKhau, javafx.scene.layout.Priority.ALWAYS);
        javafx.scene.layout.HBox.setHgrow(txtMoiMatKhauVisible, javafx.scene.layout.Priority.ALWAYS);

        javafx.scene.layout.HBox rowXacNhan = new javafx.scene.layout.HBox(8, txtXacNhan, txtXacNhanVisible, btnToggleXacNhan);
        javafx.scene.layout.HBox.setHgrow(txtXacNhan, javafx.scene.layout.Priority.ALWAYS);
        javafx.scene.layout.HBox.setHgrow(txtXacNhanVisible, javafx.scene.layout.Priority.ALWAYS);

        VBox content = new VBox(8,
                new Label("Mật khẩu mới:"), rowMoi,
                new Label("Xác nhận:"), rowXacNhan,
                lblLoi);
        content.setPadding(new Insets(20));
        content.setPrefWidth(380);
        dialog.getDialogPane().setContent(content);

        ButtonType btnXacNhanType = new ButtonType("Xác nhận đổi mật khẩu", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().add(btnXacNhanType);

        // Ngăn dialog đóng khi click nút — ta tự đóng sau khi Task xong
        Node btnXacNhanNode = dialog.getDialogPane().lookupButton(btnXacNhanType);
        btnXacNhanNode.addEventFilter(ActionEvent.ACTION, e -> {
            e.consume(); // Luôn consume — tự điều khiển việc đóng
            String mk1 = getFieldText(txtMoiMatKhau, txtMoiMatKhauVisible);
            String mk2 = getFieldText(txtXacNhan, txtXacNhanVisible);
            if (mk1.isBlank()) {
                lblLoi.setText("Mật khẩu mới không được để trống.");
                return;
            }
            if (!mk1.equals(mk2)) {
                lblLoi.setText("Mật khẩu xác nhận không khớp.");
                return;
            }

            // BCrypt hash nặng → chạy trên background Task, tránh UI freeze
            lblLoi.setText("Đang cập nhật mật khẩu...");
            btnXacNhanNode.setDisable(true);

            Task<Void> doiMatKhauTask = new Task<>() {
                @Override
                protected Void call() throws Exception {
                    xacThucService.doiMatKhau(matKhauCu, mk1, mk2, false);
                    return null;
                }
            };

            doiMatKhauTask.setOnSucceeded(ev -> {
                // Đóng dialog rồi điều hướng
                dialog.getDialogPane().getScene().getWindow().hide();
                try {
                    if (phanQuyenService.laThuNgan(nhanVien)) {
                        CaLamViecDTO caHienTai = caLamViecService.layCaHienTai(nhanVien.getMaNV());
                        if (caHienTai != null) {
                            com.bakery.utils.SessionContext.getInstance().moCa(caHienTai.getMaCa());
                            quayLaiMenuChinh(txtTenDangNhap);
                            return;
                        }
                        transitionTo(txtTenDangNhap, "/fxml/MoCaView.fxml", "H3K Bakery - Mở ca làm việc", 1366, 768);
                        return;
                    }
                    quayLaiMenuChinh(txtTenDangNhap);
                } catch (Exception ex) {
                    hienDangNhap();
                    hienThiLoiLabel(resolveErrorMessage(ex));
                }
            });

            doiMatKhauTask.setOnFailed(ev -> {
                lblLoi.setText(resolveErrorMessage(doiMatKhauTask.getException()));
                btnXacNhanNode.setDisable(false);
            });

            startBackgroundTask(doiMatKhauTask);
        });

        dialog.showAndWait();
    }

    private void hienStartScreen() {
        hienPanel(panelStartScreen);
        if (lblThongBao != null) {
            lblThongBao.setText("");
        }
        if (lblRegisterThongBao != null) {
            lblRegisterThongBao.setText("");
        }
    }

    private void hienDangNhap() {
        hienPanel(panelLogin);
    }

    private void hienDangKy() {
        hienPanel(panelRegister);
    }

    private void hienPanel(VBox panel) {
        panelStartScreen.setVisible(panel == panelStartScreen);
        panelStartScreen.setManaged(panel == panelStartScreen);
        panelLogin.setVisible(panel == panelLogin);
        panelLogin.setManaged(panel == panelLogin);
        panelRegister.setVisible(panel == panelRegister);
        panelRegister.setManaged(panel == panelRegister);
    }

    private void setLoginFormDisabled(boolean disabled) {
        txtTenDangNhap.setDisable(disabled);
        txtMatKhau.setDisable(disabled);
        txtMatKhauVisible.setDisable(disabled);
        btnDangNhap.setDisable(disabled);
        btnToggleMatKhau.setDisable(disabled);
    }

    private void setRegisterFormDisabled(boolean disabled) {
        txtRegisterHoTen.setDisable(disabled);
        txtRegisterTenDangNhap.setDisable(disabled);
        txtRegisterSoDienThoai.setDisable(disabled);
        txtRegisterMatKhau.setDisable(disabled);
        txtRegisterMatKhauVisible.setDisable(disabled);
        cboRegisterVaiTro.setDisable(disabled);
        txtRegisterMaXacNhan.setDisable(disabled);
        btnTaoTaiKhoan.setDisable(disabled);
        btnToggleRegisterMatKhau.setDisable(disabled);
    }

    private void clearRegisterForm() {
        txtRegisterHoTen.clear();
        txtRegisterTenDangNhap.clear();
        txtRegisterSoDienThoai.clear();
        txtRegisterMatKhau.clear();
        txtRegisterMatKhauVisible.clear();
        txtRegisterMaXacNhan.clear();
        if (!cboRegisterVaiTro.getItems().isEmpty()) {
            cboRegisterVaiTro.getSelectionModel().selectFirst();
        }
        if (lblRegisterThongBao != null) {
            lblRegisterThongBao.setText("");
        }
    }

    private void hienThiRegisterLoi(String msg) {
        if (lblRegisterThongBao != null) {
            lblRegisterThongBao.setText(msg);
            lblRegisterThongBao.getStyleClass().removeAll("lbl-success");
            if (!lblRegisterThongBao.getStyleClass().contains("lbl-danger")) {
                lblRegisterThongBao.getStyleClass().add("lbl-danger");
            }
        }
    }

    private void hienThiRegisterThanhCong(String msg) {
        if (lblRegisterThongBao != null) {
            lblRegisterThongBao.setText(msg);
            lblRegisterThongBao.getStyleClass().removeAll("lbl-danger");
            if (!lblRegisterThongBao.getStyleClass().contains("lbl-success")) {
                lblRegisterThongBao.getStyleClass().add("lbl-success");
            }
        }
    }

    private String valueOf(TextField field) {
        return field.getText() == null ? "" : field.getText().trim();
    }

    private void bindPasswordToggle(PasswordField hiddenField, TextField visibleField, Button toggleButton) {
        if (hiddenField == null || visibleField == null || toggleButton == null) {
            return;
        }
        visibleField.textProperty().bindBidirectional(hiddenField.textProperty());
        visibleField.setManaged(false);
        visibleField.setVisible(false);
        toggleButton.setText("Hiện");
    }

    private void togglePasswordField(PasswordField hiddenField, TextField visibleField, Button toggleButton) {
        boolean showing = visibleField.isVisible();
        visibleField.setVisible(!showing);
        visibleField.setManaged(!showing);
        hiddenField.setVisible(showing);
        hiddenField.setManaged(showing);
        toggleButton.setText(showing ? "Hiện" : "Ẩn");
        if (showing) {
            hiddenField.requestFocus();
            hiddenField.positionCaret(hiddenField.getText().length());
        } else {
            visibleField.requestFocus();
            visibleField.positionCaret(visibleField.getText().length());
        }
    }

    private String getFieldText(PasswordField hiddenField, TextField visibleField) {
        return visibleField != null && visibleField.isVisible()
                ? (visibleField.getText() == null ? "" : visibleField.getText())
                : (hiddenField.getText() == null ? "" : hiddenField.getText());
    }

    private String normalizeRoleName(String value) {
        if (value == null) {
            return "";
        }
        String normalized = Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .replace("đ", "d")
                .replace("Đ", "D");
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

    private record RoleOption(int id, String label) {
        @Override
        public String toString() {
            return label;
        }
    }
}
