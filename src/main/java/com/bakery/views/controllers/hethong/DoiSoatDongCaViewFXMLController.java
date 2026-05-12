package com.bakery.views.controllers.hethong;

// import com.bakery.App;
// import com.bakery.model.dto.khachhang.KhachHangDTO;
import com.bakery.presenters.hethong.DoiSoatDongCaPresenter;
import com.bakery.services.hethong.DoiSoatService;
import com.bakery.utils.CurrencyFormatter;
import com.bakery.views.interfaces.hethong.IDoiSoatDongCaView;
import javafx.application.Platform;
// import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;

public class DoiSoatDongCaViewFXMLController extends BaseController implements IDoiSoatDongCaView {

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
            URL fxml = DoiSoatDongCaViewFXMLController.class.getResource("/fxml/hethong/DoiSoatDongCaView.fxml");
            if (fxml == null)
                return;

            Parent root = FXMLLoader.load(fxml);
            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            // dialog.initOwner(App.getPrimaryStage());
            dialog.setTitle("Đối soát đóng ca");
            dialog.setResizable(false);

            Scene scene = new Scene(root);
            URL css = DoiSoatDongCaViewFXMLController.class.getResource("/css/bakery.css");
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
            lblTienDauCa.getStyleClass().add("lbl-danger");
            lblTienDauCa.setText(causeMsg.length() > 80 ? causeMsg.substring(0, 80) + "..." : causeMsg);
            lblDoanhThu.setText("");
        });
    }

    @Override
    public void hienThiLoiNhapTrong() {
        Platform.runLater(() -> {
            tfNhap.setPromptText("Số tiền không được để trống!");
            hienThiLoiLabel("Vui lòng nhập số tiền mặt thực tế trong két.");
        });
    }

    @Override
    public void hienThiLoiNhapSaiDinhDang() {
        Platform.runLater(() -> {
            tfNhap.clear();
            tfNhap.setPromptText("Số không hợp lệ — thử lại");
            hienThiLoiLabel("Định dạng số tiền không đúng. Vui lòng kiểm tra lại.");
        });
    }

    @Override
    public void chuyenSangSauKiemTra(BigDecimal chenhLech, String tienHienThi) {
        Platform.runLater(() -> {
            tfHienThi.setText(tienHienThi);
            // Âm: thiếu tiền → cảnh báo + yêu cầu lý do; dương/0 → thành công ngay
            boolean amTien = chenhLech.compareTo(BigDecimal.ZERO) < 0;
            setVisible(boxCanhBao, amTien);
            setVisible(boxThanhCong, !amTien);
            setVisible(vboxLyDo, amTien);

            if (amTien) {
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
            lblKQChenhLech.getStyleClass().removeAll("lbl-danger", "lbl-success");
            lblKQChenhLech.getStyleClass().add(coChenhLech ? "lbl-danger" : "lbl-success");

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
        hienThiLoi("LỖI KHÓA SỔ: " + msg);
    }

    @Override
    public void hienThiLoi(String msg) {
        Platform.runLater(() -> hienThiLoiLabel(msg));
    }

    @Override
    public void xoaLoi() {
        Platform.runLater(() -> {
            if (lblThongBao != null) lblThongBao.setText("");
        });
    }

    @Override
    public void dongDialog() {
        Platform.runLater(() -> getStage().close());
    }

    @Override
    public void navigateToLogin() {
        Platform.runLater(() -> {
            // Lấy owner window (MainMenuView) trước khi đóng dialog
            javafx.stage.Window owner = getStage().getOwner();
            getStage().close();

            // Chuyển owner về màn hình đăng nhập
            if (owner instanceof Stage ownerStage) {
                try {
                    java.net.URL fxmlUrl = getClass().getResource("/fxml/hethong/DangNhapView.fxml");
                    if (fxmlUrl == null) return;
                    javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(fxmlUrl);
                    javafx.scene.Parent root = loader.load();
                    javafx.scene.Scene scene = new javafx.scene.Scene(root);
                    URL css = getClass().getResource("/css/bakery.css");
                    if (css != null) scene.getStylesheets().add(css.toExternalForm());
                    ownerStage.setTitle("H3K Bakery - Đăng nhập");
                    ownerStage.setScene(scene);
                    ownerStage.setResizable(false);
                    ownerStage.centerOnScreen();
                } catch (Exception e) {
                    System.err.println("[DoiSoatDongCa] Lỗi về màn hình đăng nhập: " + e.getMessage());
                }
            }
        });
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
