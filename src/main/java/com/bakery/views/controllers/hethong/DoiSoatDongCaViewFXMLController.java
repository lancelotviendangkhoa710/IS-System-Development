package com.bakery.views.controllers.hethong;

// import com.bakery.App;
import com.bakery.model.dto.khachhang.KhachHangDTO;
import com.bakery.presenters.hethong.DoiSoatDongCaPresenter;
import com.bakery.services.hethong.DoiSoatService;
import com.bakery.utils.CurrencyFormatter;
import com.bakery.views.interfaces.khachhang.CustomerDeletedView;
import com.bakery.views.interfaces.hethong.IDoiSoatDongCaView;
import javafx.application.Platform;
import javafx.collections.FXCollections;
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
import java.time.LocalDateTime;
import java.util.List;

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

    /**
     * Controller cho màn hình Thùng rác.
     * Implement CustomerDeletedView interface - Presenter giao tiếp qua interface này.
     */
    public static class CustomerDeletedViewFXMLController extends BaoCaoViewFXMLController.AbstractCustomerController implements CustomerDeletedView {

        @FXML private TableView<KhachHangDTO> deletedTable;
        @FXML private TableColumn<KhachHangDTO, Integer> colMaKH;
        @FXML private TableColumn<KhachHangDTO, String> colTenKH;
        @FXML private TableColumn<KhachHangDTO, String> colSDT;
        @FXML private TableColumn<KhachHangDTO, LocalDateTime> colNgayXoa;
        @FXML private TableColumn<KhachHangDTO, String> colNguoiXoa;
        @FXML private TableColumn<KhachHangDTO, Void> colThaoTac;
        @FXML private Label lblPageInfo;
        @FXML private TextField searchField;

        private com.bakery.presenters.customer.CustomerDeletedPresenter presenter;

        @FXML
        public void initialize() {
            presenter = new com.bakery.presenters.customer.CustomerDeletedPresenter(this);
            setupColumns();
            deletedTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

            if (searchField != null) {
                searchField.textProperty().addListener((obs, oldVal, newVal) -> presenter.searchDeletedCustomers(newVal));
            }

            presenter.refreshDeletedCustomers();
        }

        @Override
        public void displayDeletedCustomers(List<KhachHangDTO> customers) {
            deletedTable.setItems(FXCollections.observableArrayList(customers));
            deletedTable.refresh();
        }

        @Override
        public void updatePaginationInfo(String pageInfo) {
            lblPageInfo.setText(pageInfo);
        }

        @Override
        public void showErrorAlert(String title, String message) {
            hienThiLoi(title, message);
        }

        @Override
        public void showSuccessAlert(String title, String message) {
            hienThiThanhCong(title, message);
        }

        @Override
        public boolean confirmRestore(String customerName) {
            return new Alert(
                    Alert.AlertType.CONFIRMATION,
                    "Khôi phục \"" + customerName + "\"?",
                    ButtonType.OK,
                    ButtonType.CANCEL
            ).showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
        }

        @Override
        public void setBusy(boolean busy) {
            deletedTable.setDisable(busy);
            if (searchField != null) {
                searchField.setDisable(busy);
            }
        }

        @Override
        public void closeDialog() {
            dongForm();
        }

        private void setupColumns() {
            colMaKH.setCellValueFactory(cd -> new javafx.beans.property.SimpleIntegerProperty(cd.getValue().getMaKH()).asObject());
            colTenKH.setCellValueFactory(cd -> new javafx.beans.property.SimpleStringProperty(cd.getValue().getHoTen()));
            colSDT.setCellValueFactory(cd -> new javafx.beans.property.SimpleStringProperty(cd.getValue().getSdt()));
            colNgayXoa.setCellValueFactory(cd -> new javafx.beans.property.SimpleObjectProperty<>(cd.getValue().getThoiDiemXoa()));
            colNgayXoa.setCellFactory(col -> new TableCell<KhachHangDTO, LocalDateTime>() {
                @Override
                protected void updateItem(LocalDateTime item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? "" : item.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
                }
            });
            colNguoiXoa.setCellValueFactory(cd -> new javafx.beans.property.SimpleStringProperty(
                    cd.getValue().getTenNguoiXoa() != null ? cd.getValue().getTenNguoiXoa() : "Mã NV: " + cd.getValue().getMaNX()));
            colThaoTac.setCellFactory(col -> new TableCell<KhachHangDTO, Void>() {
                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                        return;
                    }
                    KhachHangDTO kh = getTableRow().getItem();
                    if (kh == null) {
                        setGraphic(null);
                        return;
                    }
                    Button restore = new Button("🔄 Khôi phục");
                    restore.setOnAction(e -> {
                        if (confirmRestore(kh.getHoTen())) {
                            presenter.restoreCustomer(kh.getMaKH());
                        }
                    });
                    setGraphic(restore);
                }
            });
        }
    }
}
