package com.bakery.views.controllers;

import com.bakery.App;
import com.bakery.presenters.LoginPresenter;
import com.bakery.services.CaLamViecService;
import com.bakery.services.NhanVienService;
import com.bakery.views.interfaces.ILoginView;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class LoginController implements ILoginView {

    @FXML private TextField     tfTenDangNhap;
    @FXML private PasswordField pfMatKhau;
    @FXML private TextField     tfMatKhauHien;
    @FXML private Button        btnTogglePass;
    @FXML private Button        btnDangNhap;
    @FXML private Label         lblErrorTenDangNhap;
    @FXML private Label         lblErrorMatKhau;
    @FXML private Label         lblErrorChung;
    @FXML private VBox          paneLoading;

    private final LoginPresenter presenter = new LoginPresenter(
            this, new NhanVienService(), new CaLamViecService());

    private boolean dangHienMatKhau = false;

    @FXML
    public void initialize() {
        pfMatKhau.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!dangHienMatKhau) tfMatKhauHien.setText(newVal);
        });
        tfMatKhauHien.textProperty().addListener((obs, oldVal, newVal) -> {
            if (dangHienMatKhau) pfMatKhau.setText(newVal);
        });
    }

    @FXML
    private void onSubmitUsername() {
        if (dangHienMatKhau) tfMatKhauHien.requestFocus();
        else pfMatKhau.requestFocus();
    }

    @FXML
    private void onTogglePassword() {
        dangHienMatKhau = !dangHienMatKhau;
        if (dangHienMatKhau) {
            tfMatKhauHien.setText(pfMatKhau.getText());
            pfMatKhau.setVisible(false);
            pfMatKhau.setManaged(false);
            tfMatKhauHien.setVisible(true);
            tfMatKhauHien.setManaged(true);
            tfMatKhauHien.requestFocus();
            tfMatKhauHien.positionCaret(tfMatKhauHien.getText().length());
            btnTogglePass.setText("Ẩn");
        } else {
            pfMatKhau.setText(tfMatKhauHien.getText());
            tfMatKhauHien.setVisible(false);
            tfMatKhauHien.setManaged(false);
            pfMatKhau.setVisible(true);
            pfMatKhau.setManaged(true);
            pfMatKhau.requestFocus();
            pfMatKhau.positionCaret(pfMatKhau.getText().length());
            btnTogglePass.setText("Hiện");
        }
    }

    @FXML
    private void onDangNhap() {
        String tenDangNhap = tfTenDangNhap.getText().trim();
        String matKhau     = dangHienMatKhau ? tfMatKhauHien.getText() : pfMatKhau.getText();
        presenter.onDangNhapClicked(tenDangNhap, matKhau);
    }

    // ── ILoginView ────────────────────────────────────────────────────────────

    @Override
    public void clearErrors() {
        Platform.runLater(() -> {
            lblErrorTenDangNhap.setText(" ");
            lblErrorMatKhau.setText(" ");
            lblErrorChung.setText(" ");
        });
    }

    @Override
    public void showErrorTenDangNhap(String msg) {
        Platform.runLater(() -> lblErrorTenDangNhap.setText(msg));
    }

    @Override
    public void showErrorMatKhau(String msg) {
        Platform.runLater(() -> lblErrorMatKhau.setText(msg));
    }

    @Override
    public void showErrorChung(String msg) {
        Platform.runLater(() -> lblErrorChung.setText(msg));
    }

    @Override
    public void setLoading(boolean loading) {
        Platform.runLater(() -> {
            paneLoading.setVisible(loading);
            btnDangNhap.setDisable(loading);
        });
    }

    @Override
    public void navigateToMain() {
        Platform.runLater(() -> App.hienThiManHinh("/fxml/MainView.fxml", 1280, 800));
    }

    @Override
    public void navigateToMoCa() {
        Platform.runLater(() -> App.hienThiManHinh("/fxml/MoCaView.fxml", 560, 640));
    }
}
