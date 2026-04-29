package com.bakery.views;

import com.bakery.models.dto.VaiTroDTO;
import com.bakery.presenters.RegisterPresenter;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.util.List;

public class RegisterFrame implements RegisterPresenter.RegisterView {
    private static final String REGISTER_RESOURCE = "/images/register-screen.png";
    private static final int FRAME_WIDTH = 900;
    private static final int FRAME_HEIGHT = 507;
    private static final Color TEXT_COLOR = Color.web("#61391f");
    private static final Color DANGER_TEXT_COLOR = Color.web("#801818");

    private final TextField txtHoTen = createTextField(TEXT_COLOR);
    private final TextField txtTenDangNhap = createTextField(TEXT_COLOR);
    private final TextField txtSoDienThoai = createTextField(TEXT_COLOR);
    private final PasswordField txtMatKhau = createPasswordField();
    private final TextField txtMatKhauVisible = createTextField(TEXT_COLOR);
    private final ComboBox<RoleItem> cboVaiTro = createRoleComboBox();
    private final TextField txtMaXacNhanQuanLy = createTextField(DANGER_TEXT_COLOR);
    private final Button btnTogglePassword = createGhostButton("Hien");
    private final Button btnRegister = createGhostButton("");
    private final RegisterPresenter presenter = new RegisterPresenter(this);
    private final Stage stage = new Stage();

    private RegisterFrame(Window owner) {
        initStage(owner);
        presenter.loadRoles();
    }

    public static void open(Window owner) {
        new RegisterFrame(owner).stage.show();
    }

    private void initStage(Window owner) {
        stage.initModality(Modality.WINDOW_MODAL);
        if (owner != null) {
            stage.initOwner(owner);
        }
        stage.setTitle("Dang ky tai khoan");
        stage.setResizable(false);
        stage.setScene(new Scene(buildContent(), FRAME_WIDTH, FRAME_HEIGHT));
    }

    private StackPane buildContent() {
        ImageView background = new ImageView(new Image(getClass().getResourceAsStream(REGISTER_RESOURCE)));
        background.setFitWidth(FRAME_WIDTH);
        background.setFitHeight(FRAME_HEIGHT);
        background.setPreserveRatio(false);

        txtMatKhauVisible.textProperty().bindBidirectional(txtMatKhau.textProperty());
        txtMatKhauVisible.setVisible(false);
        txtMatKhauVisible.setManaged(false);

        Pane overlay = new Pane();
        overlay.setPrefSize(FRAME_WIDTH, FRAME_HEIGHT);

        addScaled(overlay, txtHoTen, 426, 193, 430, 34);
        addScaled(overlay, txtTenDangNhap, 426, 263, 430, 34);
        addScaled(overlay, txtSoDienThoai, 426, 332, 430, 34);
        addScaled(overlay, buildPasswordFieldContainer(), 426, 402, 430, 34);
        addScaled(overlay, cboVaiTro, 426, 471, 430, 34);
        addScaled(overlay, txtMaXacNhanQuanLy, 462, 541, 394, 34);
        addScaled(overlay, btnRegister, 341, 611, 259, 52);

        btnRegister.setOnAction(e -> presenter.handleRegister());
        btnTogglePassword.setOnAction(e -> togglePasswordVisibility());

        return new StackPane(background, overlay);
    }

    private HBox buildPasswordFieldContainer() {
        StackPane passwordStack = new StackPane(txtMatKhau, txtMatKhauVisible);
        HBox.setHgrow(passwordStack, Priority.ALWAYS);

        HBox container = new HBox();
        container.setSpacing(0);
        container.setPadding(Insets.EMPTY);
        container.getChildren().addAll(passwordStack, btnTogglePassword);
        return container;
    }

    private void addScaled(Pane pane, javafx.scene.Node node, int x, int y, int width, int height) {
        node.setLayoutX(scaleX(x));
        node.setLayoutY(scaleY(y));
        node.resizeRelocate(scaleX(x), scaleY(y), scaleX(width), scaleY(height));
        pane.getChildren().add(node);
    }

    private int scaleX(int value) {
        return Math.round(value * FRAME_WIDTH / 1365f);
    }

    private int scaleY(int value) {
        return Math.round(value * FRAME_HEIGHT / 768f);
    }

    private TextField createTextField(Color color) {
        TextField field = new TextField();
        field.setBackground(null);
        field.setPadding(new Insets(0, 30, 0, 12));
        field.setFont(Font.font("Segoe UI", 15));
        field.setStyle("-fx-background-color: transparent;");
        field.setFocusTraversable(true);
        field.setPromptText("");
        field.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        field.setStyle("""
                -fx-background-color: transparent;
                -fx-text-fill: %s;
                -fx-highlight-fill: #d9c1a5;
                -fx-highlight-text-fill: %s;
                -fx-display-caret: true;
                """.formatted(toHex(color), toHex(color)));
        return field;
    }

    private PasswordField createPasswordField() {
        PasswordField field = new PasswordField();
        field.setBackground(null);
        field.setPadding(new Insets(0, 30, 0, 12));
        field.setFont(Font.font("Segoe UI", 15));
        field.setStyle("""
                -fx-background-color: transparent;
                -fx-text-fill: #61391f;
                -fx-highlight-fill: #d9c1a5;
                -fx-highlight-text-fill: #61391f;
                -fx-display-caret: true;
                """);
        return field;
    }

    private ComboBox<RoleItem> createRoleComboBox() {
        ComboBox<RoleItem> comboBox = new ComboBox<>(FXCollections.observableArrayList());
        comboBox.setPadding(new Insets(0, 30, 0, 12));
        comboBox.setStyle("""
                -fx-background-color: transparent;
                -fx-font-size: 15px;
                -fx-text-fill: #61391f;
                -fx-mark-color: #61391f;
                -fx-border-color: transparent;
                """);
        comboBox.setButtonCell(new javafx.scene.control.ListCell<>() {
            @Override
            protected void updateItem(RoleItem item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.label());
                setTextFill(TEXT_COLOR);
                setFont(Font.font("Segoe UI", 15));
                setStyle("-fx-background-color: transparent;");
            }
        });
        comboBox.setCellFactory(list -> new javafx.scene.control.ListCell<>() {
            @Override
            protected void updateItem(RoleItem item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.label());
                setTextFill(TEXT_COLOR);
                setFont(Font.font("Segoe UI", 15));
                setStyle(empty ? "" : "-fx-background-color: white;");
            }
        });
        return comboBox;
    }

    private Button createGhostButton(String text) {
        Button button = new Button(text);
        button.setBackground(null);
        button.setBorder(null);
        button.setFont(Font.font("Segoe UI", 12));
        button.setTextFill(TEXT_COLOR);
        button.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
        return button;
    }

    private void togglePasswordVisibility() {
        boolean showing = txtMatKhauVisible.isVisible();
        txtMatKhauVisible.setVisible(!showing);
        txtMatKhauVisible.setManaged(!showing);
        txtMatKhau.setVisible(showing);
        txtMatKhau.setManaged(showing);
        btnTogglePassword.setText(showing ? "Hien" : "An");
    }

    @Override
    public String getHoTen() {
        return txtHoTen.getText().trim();
    }

    @Override
    public String getSoDienThoai() {
        return txtSoDienThoai.getText().trim();
    }

    @Override
    public String getTenDangNhap() {
        return txtTenDangNhap.getText().trim();
    }

    @Override
    public String getMatKhau() {
        return txtMatKhau.getText();
    }

    @Override
    public String getMaXacNhanQuanLy() {
        return txtMaXacNhanQuanLy.getText().trim();
    }

    @Override
    public Integer getMaVaiTro() {
        RoleItem item = cboVaiTro.getSelectionModel().getSelectedItem();
        return item == null ? null : item.id();
    }

    @Override
    public void setRegisterEnabled(boolean enabled) {
        btnRegister.setDisable(!enabled);
    }

    @Override
    public void clearForm() {
        txtHoTen.clear();
        txtTenDangNhap.clear();
        txtSoDienThoai.clear();
        txtMatKhau.clear();
        txtMaXacNhanQuanLy.clear();
        if (!cboVaiTro.getItems().isEmpty()) {
            cboVaiTro.getSelectionModel().selectFirst();
        }
        txtMatKhauVisible.setVisible(false);
        txtMatKhauVisible.setManaged(false);
        txtMatKhau.setVisible(true);
        txtMatKhau.setManaged(true);
        btnTogglePassword.setText("Hien");
        txtHoTen.requestFocus();
    }

    @Override
    public void showRoles(List<VaiTroDTO> roles) {
        cboVaiTro.getItems().setAll(
                roles.stream().map(role -> new RoleItem(role.getMaVaiTro(), role.getTenVaiTro())).toList()
        );
        if (!cboVaiTro.getItems().isEmpty()) {
            cboVaiTro.getSelectionModel().selectFirst();
        }
    }

    @Override
    public void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.initOwner(stage);
        alert.setTitle("Loi dang ky");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @Override
    public void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.initOwner(stage);
        alert.setTitle("Thanh cong");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private static String toHex(Color color) {
        return String.format("#%02x%02x%02x",
                Math.round(color.getRed() * 255),
                Math.round(color.getGreen() * 255),
                Math.round(color.getBlue() * 255));
    }

    private record RoleItem(int id, String label) {
        @Override
        public String toString() {
            return label;
        }
    }
}
