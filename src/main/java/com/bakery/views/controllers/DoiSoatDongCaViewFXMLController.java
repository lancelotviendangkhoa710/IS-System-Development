package com.bakery.views.controllers;

// import com.bakery.App;
import com.bakery.presenters.DoiSoatDongCaPresenter;
import com.bakery.services.DoiSoatService;
import com.bakery.utils.CurrencyFormatter;
import com.bakery.views.interfaces.IDoiSoatDongCaView;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;

public class DoiSoatDongCaViewFXMLController implements IDoiSoatDongCaView {

    @FXML
    private StackPane paneLoading;
    @FXML
    private VBox paneInfo;
    @FXML
    private Label lblMaCa;
    @FXML
    private Label lblMayPOS;
    @FXML
    private Label lblTienDauCa;
    @FXML
    private Label lblDoanhThu;

    @FXML
    private HBox sectionNhapTien;
    @FXML
    private TextField tfNhap;

    @FXML
    private VBox sectionSauKiemTra;
    @FXML
    private TextField tfHienThi;
    @FXML
    private VBox boxCanhBao;
    @FXML
    private VBox boxThanhCong;
    @FXML
    private VBox vboxLyDo;
    @FXML
    private TextArea taLyDo;

    @FXML
    private VBox sectionKetQua;
    @FXML
    private TextField tfKetQua;
    @FXML
    private VBox vboxLyDoReadOnly;
    @FXML
    private Label lblLyDoReadOnly;
    @FXML
    private Label lblKQThucTe;
    @FXML
    private Label lblKQHeThonh;
    @FXML
    private Label lblKQChenhLech;

    // ── FXML — nút bấm
    @FXML
    private HBox hboxBtnInput;
    @FXML
    private HBox hboxBtnKhoaSo;
    @FXML
    private Button btnKhoaSo;
    @FXML
    private Button btnHoanTat;

    private final DoiSoatDongCaPresenter presenter = new DoiSoatDongCaPresenter(this, new DoiSoatService());

    public static void hienThi() {
        try {
            URL fxml = DoiSoatDongCaViewFXMLController.class.getResource("/fxml/DoiSoatDongCaView.fxml");
            if (fxml == null)
                return;

            Parent root = FXMLLoader.load(fxml);
            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            // dialog.initOwner(App.getPrimaryStage());
            dialog.setTitle("Đối soát đóng ca");
            dialog.setResizable(false);

            Scene scene = new Scene(root);
            URL css = DoiSoatDongCaViewFXMLController.class.getResource("/css/amber.css");
            if (css != null)
                scene.getStylesheets().add(css.toExternalForm());

            dialog.setScene(scene);
            dialog.showAndWait();
        } catch (IOException e) {
            System.err.println("[DoiSoatDongCaViewFXMLController] Lỗi: " + e.getMessage());
        }
    }

    // ── Khởi tạo
    // ─────────────────────────────────────────────────────────────

    @FXML
    public void initialize() {
        presenter.onInitialize();
    }

    // ── Sự kiện (forward to Presenter)
    // ───────────────────────────────────────

    @FXML
    private void onKiemTra() {
        presenter.onKiemTraClicked(tfNhap.getText().trim());
    }

    @FXML
    private void onSuaLai() {
        presenter.onSuaLaiClicked();
    }

    @FXML
    private void onKhoaSo() {
        presenter.onKhoaSoClicked(taLyDo.getText().trim());
    }

    @FXML
    private void onHuyBo() {
        presenter.onHuyBoClicked();
    }

    @FXML
    private void onHoanTat() {
        presenter.onHoanTatClicked();
    }

    // ── IDoiSoatDongCaView
    // ────────────────────────────────────────────────────

    @Override
    public void setLoading(boolean loading) {
        Platform.runLater(() -> {
            setVisible(paneLoading, loading);
            setVisible(paneInfo, !loading);
        });
    }

    @Override
    public void hienThiThongTinCa(String maCa, String mayPOS, String tienDauCa, String doanhThu) {
        Platform.runLater(() -> {
            lblMaCa.setText(maCa);
            lblMayPOS.setText(mayPOS);
            lblTienDauCa.setText(tienDauCa);
            lblDoanhThu.setText(doanhThu);
        });
    }

    @Override
    public void hienThiLoiTaiCa(String maCa, String causeMsg) {
        Platform.runLater(() -> {
            lblMaCa.setText(maCa);
            lblMayPOS.setText("LỖI");
            lblTienDauCa.setStyle("-fx-text-fill: -color-danger; -fx-font-size: 11px; -fx-font-family: 'Segoe UI';");
            lblTienDauCa.setText(causeMsg.length() > 80 ? causeMsg.substring(0, 80) + "..." : causeMsg);
            lblDoanhThu.setText("");
        });
    }

    @Override
    public void hienThiLoiNhapTrong() {
        Platform.runLater(() -> tfNhap.setStyle("-fx-prompt-text-fill: -color-danger;"));
    }

    @Override
    public void hienThiLoiNhapSaiDinhDang() {
        Platform.runLater(() -> {
            tfNhap.clear();
            tfNhap.setPromptText("Số không hợp lệ — thử lại");
        });
    }

    @Override
    public void chuyenSangSauKiemTra(boolean khop, String tienHienThi) {
        Platform.runLater(() -> {
            tfHienThi.setText(tienHienThi);
            setVisible(boxCanhBao, !khop);
            setVisible(boxThanhCong, khop);
            setVisible(vboxLyDo, !khop);

            if (!khop) {
                taLyDo.clear();
                taLyDo.textProperty().addListener((obs, oldV, newV) -> presenter.onLyDoChanged(newV));
            }

            setVisible(sectionNhapTien, false);
            setVisible(sectionSauKiemTra, true);
            setVisible(hboxBtnInput, false);
            setVisible(hboxBtnKhoaSo, true);
        });
    }

    @Override
    public void setNutKhoaSoEnabled(boolean enabled) {
        Platform.runLater(() -> btnKhoaSo.setDisable(!enabled));
    }

    @Override
    public void chuyenVeNhapTien() {
        Platform.runLater(() -> {
            setVisible(sectionSauKiemTra, false);
            setVisible(sectionNhapTien, true);
            setVisible(hboxBtnKhoaSo, false);
            setVisible(hboxBtnInput, true);
            tfNhap.clear();
            tfNhap.requestFocus();
        });
    }

    @Override
    public void setNutKhoaSoDangXuLy(boolean dangXuLy) {
        Platform.runLater(() -> btnKhoaSo.setDisable(dangXuLy));
    }

    @Override
    public void hienThiKetQua(BigDecimal tienThucTe, BigDecimal tienHeThonh,
            BigDecimal chenhLech, String lyDo) {
        Platform.runLater(() -> {
            tfKetQua.setText(tfHienThi.getText());
            lblKQThucTe.setText(CurrencyFormatter.format(tienThucTe));
            lblKQHeThonh.setText(CurrencyFormatter.format(tienHeThonh));
            lblKQChenhLech.setText(CurrencyFormatter.format(chenhLech));

            boolean coChenhLech = chenhLech.compareTo(BigDecimal.ZERO) != 0;
            lblKQChenhLech.setStyle(coChenhLech
                    ? "-fx-text-fill: -color-danger; -fx-font-weight: bold; -fx-font-size: 15px; -fx-font-family: 'Segoe UI';"
                    : "-fx-text-fill: -color-success; -fx-font-weight: bold; -fx-font-size: 15px; -fx-font-family: 'Segoe UI';");

            if (coChenhLech && lyDo != null) {
                lblLyDoReadOnly.setText(lyDo);
                setVisible(vboxLyDoReadOnly, true);
            }

            setVisible(sectionSauKiemTra, false);
            setVisible(sectionKetQua, true);
            setVisible(hboxBtnKhoaSo, false);
            setVisible(btnHoanTat, true);
        });
    }

    @Override
    public void hienThiLoiKhoaSo(String msg) {
        Platform.runLater(() -> System.err.println("Khóa sổ lỗi: " + msg));
    }

    @Override
    public void dongDialog() {
        Platform.runLater(() -> getStage().close());
    }

    @Override
    public void navigateToLogin() {
        Platform.runLater(() -> dongDialog());
    }

    @Override
    public void resizeDialog() {
        Platform.runLater(() -> getStage().sizeToScene());
    }

    // ── Tiện ích UI
    // ───────────────────────────────────────────────────────────

    private static void setVisible(javafx.scene.Node node, boolean visible) {
        node.setVisible(visible);
        node.setManaged(visible);
    }

    private Stage getStage() {
        return (Stage) paneLoading.getScene().getWindow();
    }
}
