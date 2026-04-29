package com.bakery.views.controllers;


import com.bakery.presenters.MoCaPresenter;
import com.bakery.services.CaLamViecService;
import com.bakery.views.interfaces.IMoCaView;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;

import java.util.List;

public class MoCaController implements IMoCaView {

    @FXML private Label           lblHoTen;
    @FXML private ComboBox<String> cbMayPOS;
    @FXML private TextField       tfTienDauCa;
    @FXML private Label           lblError;
    @FXML private StackPane       paneLoading;
    @FXML private Button          btnBatDau;

    private final MoCaPresenter presenter = new MoCaPresenter(this, new CaLamViecService());

    @FXML
    public void initialize() {
        presenter.onInitialize();

        // Chỉ cho nhập số
        tfTienDauCa.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("[0-9]*"))
                tfTienDauCa.setText(newVal.replaceAll("[^0-9]", ""));
        });
    }

    @FXML
    private void onBatDauLamViec() {
        presenter.onBatDauLamViecClicked(cbMayPOS.getValue(), tfTienDauCa.getText().trim());
    }

    @FXML
    private void onDangXuat() {
        presenter.onDangXuatClicked();
    }

    // ── IMoCaView ─────────────────────────────────────────────────────────────

    @Override
    public void setHoTen(String hoTen) {
        Platform.runLater(() -> lblHoTen.setText(hoTen));
    }

    @Override
    public void setPosOptions(List<String> options) {
        Platform.runLater(() -> {
            cbMayPOS.setItems(FXCollections.observableArrayList(options));
            cbMayPOS.getSelectionModel().selectFirst();
        });
    }

    @Override
    public void hienThiLoi(String msg) {
        Platform.runLater(() -> lblError.setText(msg));
    }

    @Override
    public void xoaLoi() {
        Platform.runLater(() -> lblError.setText(" "));
    }

    @Override
    public void setLoading(boolean loading) {
        Platform.runLater(() -> {
            paneLoading.setVisible(loading);
            paneLoading.setManaged(loading);
            btnBatDau.setDisable(loading);
        });
    }

    @Override
    public void navigateToMain() {
        Platform.runLater(() -> {
            if (btnBatDau.getScene() != null && btnBatDau.getScene().getWindow() != null) {
                ((javafx.stage.Stage) btnBatDau.getScene().getWindow()).close();
            }
        });
    }

    @Override
    public void navigateToLogin() {
        // Not used inside modal
    }
}
