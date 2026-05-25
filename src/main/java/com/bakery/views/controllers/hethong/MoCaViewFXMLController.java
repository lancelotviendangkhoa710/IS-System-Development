package com.bakery.views.controllers.hethong;


import com.bakery.presenters.hethong.MoCaPresenter;
import com.bakery.services.hethong.CaLamViecService;
import com.bakery.utils.CurrencyFormatter;
import com.bakery.views.interfaces.hethong.IMoCaView;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;

import java.util.List;

public class MoCaViewFXMLController extends BaseController implements IMoCaView {

    @FXML private Label           lblHoTen;
    @FXML private ComboBox<String> cbMayPOS;
    @FXML private TextField       tfTienDauCa;
    @FXML private StackPane       paneLoading;
    @FXML private Button          btnBatDau;

    private final MoCaPresenter presenter = new MoCaPresenter(this, new CaLamViecService());

    @FXML
    public void initialize() {
        presenter.onInitialize();
        // Tự động định dạng dấu chấm phân cách mỗi 3 số khi nhập tiền đầu ca
        CurrencyFormatter.apDungDinhDangNhapTien(tfTienDauCa);
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
        hienThiLoiLabel(msg);
    }

    @Override
    public void xoaLoi() {
        if (lblThongBao != null) lblThongBao.setText("");
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
            try {
                javafx.stage.Stage stage = (javafx.stage.Stage) btnBatDau.getScene().getWindow();
                if (stage.getOwner() != null) {
                    // Nếu là cửa sổ dialog (mở từ MainMenu), đóng dialog
                    stage.close();
                } else {
                    // Nếu là cửa sổ chính (mở sau khi đăng nhập), chuyển hướng về menu chính
                    quayLaiMenuChinh(btnBatDau);
                }
            } catch (Exception e) {
                hienThiLoi("Lỗi chuyển màn hình: " + e.getMessage());
            }
        });
    }

    @Override
    public void navigateToLogin() {
        Platform.runLater(() -> {
            try {
                javafx.stage.Stage stage = (javafx.stage.Stage) btnBatDau.getScene().getWindow();
                if (stage.getOwner() != null) {
                    // Đóng cửa sổ dialog
                    stage.close();
                    // Chuyển hướng cửa sổ chính (owner) sang màn hình Đăng nhập
                    javafx.stage.Stage ownerStage = (javafx.stage.Stage) stage.getOwner();
                    transitionTo(ownerStage.getScene().getRoot(), "/fxml/hethong/DangNhapView.fxml", "H3K Bakery - Đăng nhập", 1366, 768);
                } else {
                    transitionTo(btnBatDau, "/fxml/hethong/DangNhapView.fxml", "H3K Bakery - Đăng nhập", 1366, 768);
                }
            } catch (Exception e) {
                hienThiLoi("Lỗi chuyển màn hình: " + e.getMessage());
            }
        });
    }
}
