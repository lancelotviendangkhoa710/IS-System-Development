package com.bakery.views.controllers;

import com.bakery.utils.QRGenerator;
import com.bakery.views.interfaces.IOrderDialogFactory;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.net.URL;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.Locale;

/**
 * Dialog factory + FXML controller cho luong tao don hang.
 * Presenter chi biet IOrderDialogFactory va khong phu thuoc framework UI.
 */
public class CreateOrderViewFXMLController implements IOrderDialogFactory {

    @FXML private Label lblStep;
    @FXML private VBox step1Panel;
    @FXML private VBox step2Panel;
    @FXML private Button btnBack;
    @FXML private Button btnNext;
    @FXML private Button btnConfirm;

    @FXML private TextField txtSDT;
    @FXML private Label lblKhachInfo;

    @FXML private Label lblTongTien;
    @FXML private ToggleButton btnImmediateFlow;
    @FXML private ToggleButton btnPreorderFlow;

    @FXML private VBox panelImmediate;
    @FXML private ToggleButton btnCash;
    @FXML private ToggleButton btnTransfer;
    @FXML private VBox panelTienMat;
    @FXML private TextField txtKhachDua;
    @FXML private Label lblTienThua;
    @FXML private VBox panelQR;
    @FXML private ImageView imgQR;
    @FXML private Label lblQRAmount;

    @FXML private VBox panelPreorder;
    @FXML private DatePicker dpNgayGiao;
    @FXML private ComboBox<String> cbGioGiao;
    @FXML private TextField txtDiaChiGiao;
    @FXML private ToggleButton btnFullPay;
    @FXML private ToggleButton btnDeposit;
    @FXML private TextField txtTienCoc;
    @FXML private Label lblCocToiThieu;

    private Window ownerWindow;
    private Stage dialogStage;

    private double tongTienPhaiTra;
    private CustomerLookup customerLookup;
    private OrderRequest result = OrderRequest.cancelled();

    private Integer maKH;
    private String tenKhach = "Khach vang lai";
    private String soDienThoai = "";

    private static final NumberFormat FMT_TIEN = NumberFormat.getNumberInstance(Locale.of("vi", "VN"));

    static {
        FMT_TIEN.setMaximumFractionDigits(0);
    }

    public void setOwnerWindow(Window ownerWindow) {
        this.ownerWindow = ownerWindow;
    }

    @FXML
    private void initialize() {
        if (cbGioGiao != null && cbGioGiao.getItems().isEmpty()) {
            for (int h = 7; h <= 21; h++) {
                cbGioGiao.getItems().add(String.format("%02d:00", h));
                if (h < 21) {
                    cbGioGiao.getItems().add(String.format("%02d:30", h));
                }
            }
            cbGioGiao.setValue("09:00");
        }

        if (txtKhachDua != null) {
            txtKhachDua.textProperty().addListener((obs, oldVal, newVal) -> capNhatTienThua());
        }
    }

    @Override
    public OrderRequest showCreateOrderDialog(double tongTienPhaiTra, CustomerLookup customerLookup) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/CreateOrderDialog.fxml"));
            Parent root = loader.load();
            CreateOrderViewFXMLController controller = loader.getController();

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            if (ownerWindow != null) {
                stage.initOwner(ownerWindow);
            }

            Scene scene = new Scene(root);
            URL cssUrl = getClass().getResource("/css/bakery.css");
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
            }

            stage.setTitle("Tao don hang");
            stage.setScene(scene);
            stage.setMinWidth(520);
            stage.setMinHeight(420);
            controller.khoiTaoDialog(stage, tongTienPhaiTra, customerLookup);
            stage.showAndWait();

            return controller.result == null ? OrderRequest.cancelled() : controller.result;
        } catch (Exception e) {
            return OrderRequest.cancelled();
        }
    }

    @Override
    public boolean showPaymentConfirmation(int maDon, double tongTien, double daCoc, double conLai) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PaymentDialog.fxml"));
            Parent root = loader.load();
            PaymentDialogViewFXMLController controller = loader.getController();
            controller.initData(maDon, tongTien, daCoc, conLai);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            if (ownerWindow != null) {
                stage.initOwner(ownerWindow);
            }

            Scene scene = new Scene(root);
            URL cssUrl = getClass().getResource("/css/bakery.css");
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
            }

            stage.setTitle("Xac nhan thanh toan");
            stage.setScene(scene);
            stage.setResizable(false);
            stage.showAndWait();
            return controller.isConfirmed();
        } catch (Exception e) {
            return false;
        }
    }

    private void khoiTaoDialog(Stage stage, double tongTien, CustomerLookup lookup) {
        this.dialogStage = stage;
        this.tongTienPhaiTra = tongTien;
        this.customerLookup = lookup;
        this.result = OrderRequest.cancelled();

        this.maKH = null;
        this.tenKhach = "Khach vang lai";
        this.soDienThoai = "";

        lblStep.setText("Buoc 1 / 2");
        hienStep1();

        lblTongTien.setText(dinhDangTien(tongTienPhaiTra));
        lblKhachInfo.setText("Chua xac dinh - se tao don vang lai");
        lblKhachInfo.setStyle("-fx-text-fill: #6B7280;");
        txtSDT.clear();

        btnImmediateFlow.setSelected(true);
        btnPreorderFlow.setSelected(false);
        onChonLoaiDon();

        btnCash.setSelected(true);
        btnTransfer.setSelected(false);
        onChonHinhThuc();

        btnDeposit.setSelected(true);
        btnFullPay.setSelected(false);
        if (dpNgayGiao != null) {
            dpNgayGiao.setValue(LocalDate.now().plusDays(1));
        }
        onChonKieuCoc();
    }

    @FXML
    private void onTimKhach() {
        String sdt = txtSDT.getText() == null ? "" : txtSDT.getText().trim();
        if (sdt.isEmpty()) {
            maKH = null;
            tenKhach = "Khach vang lai";
            soDienThoai = "";
            lblKhachInfo.setText("Khong co SDT - don vang lai");
            lblKhachInfo.setStyle("-fx-text-fill: #6B7280;");
            return;
        }

        if (customerLookup == null) {
            maKH = null;
            tenKhach = "Khach vang lai";
            soDienThoai = sdt;
            lblKhachInfo.setText("Khong tim thay khach hang");
            lblKhachInfo.setStyle("-fx-text-fill: #DC2626; -fx-font-weight: bold;");
            return;
        }

        try {
            String[] info = customerLookup.lookup(sdt);
            if (info != null && info.length >= 2) {
                maKH = Integer.parseInt(info[0]);
                tenKhach = info[1];
                soDienThoai = sdt;
                lblKhachInfo.setText("OK " + tenKhach + " - thanh vien giam 10%");
                lblKhachInfo.setStyle("-fx-text-fill: #16A34A; -fx-font-weight: bold;");
            } else {
                maKH = null;
                tenKhach = "Khach chua dang ky";
                soDienThoai = sdt;
                lblKhachInfo.setText("Khong tim thay - dat don vang lai");
                lblKhachInfo.setStyle("-fx-text-fill: #DC2626; -fx-font-weight: bold;");
            }
        } catch (Exception e) {
            maKH = null;
            tenKhach = "Khach vang lai";
            soDienThoai = sdt;
            lblKhachInfo.setText("Khong tim thay - dat don vang lai");
            lblKhachInfo.setStyle("-fx-text-fill: #DC2626; -fx-font-weight: bold;");
        }
    }

    @FXML
    private void onThemKhachHang() {
        moDialogKhachHang(null);
    }

    @FXML
    private void onSuaKhachHang() {
        if (maKH == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("Vui lòng tìm khách hàng trước khi sửa.");
            alert.showAndWait();
            return;
        }
        
        com.bakery.model.dto.KhachHangDTO kh = new com.bakery.model.dto.KhachHangDTO();
        kh.setMaKH(maKH);
        kh.setHoTen(tenKhach);
        kh.setSdt(soDienThoai);
        // We don't have all details here, but the dialog will load them if needed or we can just pass what we have.
        // Actually, KhachHangDAO.timKhachHangBangSDT is better.
        
        com.bakery.model.dao.KhachHangDAO dao = new com.bakery.model.dao.KhachHangDAO();
        com.bakery.model.dto.KhachHangDTO fullKh = dao.timKhachHangBangSDT(soDienThoai);
        moDialogKhachHang(fullKh);
    }

    private void moDialogKhachHang(com.bakery.model.dto.KhachHangDTO kh) {
        try {
            java.net.URL fxmlUrl = getClass().getResource("/fxml/KhachHangDialog.fxml");
            if (fxmlUrl == null) throw new RuntimeException("Không tìm thấy KhachHangDialog.fxml");
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(fxmlUrl);
            javafx.scene.Scene scene = new javafx.scene.Scene(loader.load());
            KhachHangDialogController controller = loader.getController();
            
            if (kh != null) {
                controller.khoiTaoChinhSua(kh);
            }

            Stage dialog = new Stage();
            dialog.setTitle(kh == null ? "H3K Bakery - Thêm Khách Hàng" : "H3K Bakery - Sửa Khách Hàng");
            dialog.setScene(scene);
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initOwner(lblKhachInfo.getScene().getWindow());
            dialog.setResizable(false);
            dialog.showAndWait();

            com.bakery.model.dto.KhachHangDTO kq = controller.getKetQua();
            if (kq != null) {
                this.maKH = kq.getMaKH();
                this.tenKhach = kq.getHoTen();
                this.soDienThoai = kq.getSdt();
                txtSDT.setText(this.soDienThoai);
                lblKhachInfo.setText("OK " + tenKhach + " - thành viên giảm 10%");
                lblKhachInfo.setStyle("-fx-text-fill: #16A34A; -fx-font-weight: bold;");
            }
        } catch (Exception ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Lỗi mở dialog: " + ex.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void onNext() {
        hienStep2();
    }

    @FXML
    private void onBack() {
        hienStep1();
    }

    @FXML
    private void onConfirm() {
        if (btnImmediateFlow.isSelected()) {
            xuLyXacNhanThanhToanNgay();
            return;
        }
        xuLyXacNhanDatTruoc();
    }

    @FXML
    private void onChonLoaiDon() {
        if (btnImmediateFlow.isFocused()) {
            btnImmediateFlow.setSelected(true);
            btnPreorderFlow.setSelected(false);
        } else if (btnPreorderFlow.isFocused()) {
            btnImmediateFlow.setSelected(false);
            btnPreorderFlow.setSelected(true);
        }

        if (!btnImmediateFlow.isSelected() && !btnPreorderFlow.isSelected()) {
            btnImmediateFlow.setSelected(true);
        }

        boolean isImmediate = btnImmediateFlow.isSelected();
        panelImmediate.setManaged(isImmediate);
        panelImmediate.setVisible(isImmediate);
        panelPreorder.setManaged(!isImmediate);
        panelPreorder.setVisible(!isImmediate);
    }

    @FXML
    private void onChonHinhThuc() {
        if (btnCash.isFocused()) {
            btnCash.setSelected(true);
            btnTransfer.setSelected(false);
        } else if (btnTransfer.isFocused()) {
            btnCash.setSelected(false);
            btnTransfer.setSelected(true);
        }

        if (!btnCash.isSelected() && !btnTransfer.isSelected()) {
            btnCash.setSelected(true);
        }

        boolean isCash = btnCash.isSelected();
        panelTienMat.setManaged(isCash);
        panelTienMat.setVisible(isCash);
        panelQR.setManaged(!isCash);
        panelQR.setVisible(!isCash);

        if (!isCash) {
            lblQRAmount.setText(dinhDangTien(tongTienPhaiTra));
            imgQR.setImage(taoQrImage(tongTienPhaiTra, "DonHang"));
        } else {
            capNhatTienThua();
        }
    }

    @FXML
    private void onChonKieuCoc() {
        if (btnFullPay.isFocused()) {
            btnFullPay.setSelected(true);
            btnDeposit.setSelected(false);
        } else if (btnDeposit.isFocused()) {
            btnFullPay.setSelected(false);
            btnDeposit.setSelected(true);
        }

        if (!btnFullPay.isSelected() && !btnDeposit.isSelected()) {
            btnDeposit.setSelected(true);
        }

        double minCoc = tongTienPhaiTra * 0.5;
        lblCocToiThieu.setText("(toi thieu " + dinhDangTien(minCoc) + ")");
        if (btnFullPay.isSelected()) {
            txtTienCoc.setText(String.valueOf((long) tongTienPhaiTra));
            txtTienCoc.setDisable(true);
        } else {
            txtTienCoc.setText(String.valueOf((long) minCoc));
            txtTienCoc.setDisable(false);
        }
    }

    private void xuLyXacNhanThanhToanNgay() {
        double soTienKhachDua = tongTienPhaiTra;
        String hinhThucThanhToan;

        if (btnCash.isSelected()) {
            hinhThucThanhToan = "Tien mat";
            soTienKhachDua = parseTien(txtKhachDua.getText());
            if (soTienKhachDua < tongTienPhaiTra) {
                hienThiLoiValidate("So tien khach dua chua du.");
                return;
            }
        } else {
            hinhThucThanhToan = "Chuyen khoan";
        }

        result = new OrderRequest(
                true,
                maKH,
                tenKhach,
                soDienThoai,
                OrderType.IMMEDIATE,
                hinhThucThanhToan,
                soTienKhachDua,
                LocalDateTime.now(),
                "",
                tongTienPhaiTra
        );
        dongDialog();
    }

    private void xuLyXacNhanDatTruoc() {
        LocalDate ngay = dpNgayGiao.getValue();
        String gio = cbGioGiao.getValue();

        if (ngay == null || gio == null || gio.isBlank()) {
            hienThiLoiValidate("Vui long chon ngay gio nhan banh.");
            return;
        }

        LocalDateTime ngayGioNhan;
        try {
            ngayGioNhan = LocalDateTime.of(ngay, LocalTime.parse(gio));
        } catch (DateTimeParseException e) {
            hienThiLoiValidate("Gio nhan banh khong hop le.");
            return;
        }

        if (ngayGioNhan.isBefore(LocalDateTime.now())) {
            hienThiLoiValidate("Ngay gio nhan banh khong duoc nam trong qua khu.");
            return;
        }

        String diaChi = txtDiaChiGiao.getText() == null ? "" : txtDiaChiGiao.getText().trim();
        if (diaChi.isEmpty()) {
            hienThiLoiValidate("Bat buoc nhap dia chi giao banh.");
            return;
        }

        double tienCoc = parseTien(txtTienCoc.getText());
        double minCoc = tongTienPhaiTra * 0.5;
        if (tienCoc < minCoc) {
            hienThiLoiValidate("Tien coc phai toi thieu 50% (" + dinhDangTien(minCoc) + ").");
            return;
        }

        result = new OrderRequest(
                true,
                maKH,
                tenKhach,
                soDienThoai,
                OrderType.PREORDER,
                null,
                0,
                ngayGioNhan,
                diaChi,
                tienCoc
        );
        dongDialog();
    }

    private void capNhatTienThua() {
        if (lblTienThua == null) {
            return;
        }
        double khachDua = parseTien(txtKhachDua.getText());
        double chenhLech = khachDua - tongTienPhaiTra;
        if (chenhLech >= 0) {
            lblTienThua.setText(dinhDangTien(chenhLech));
            lblTienThua.setStyle("-fx-text-fill: #16A34A; -fx-font-size: 16px; -fx-font-weight: bold;");
        } else {
            lblTienThua.setText("Thieu " + dinhDangTien(-chenhLech));
            lblTienThua.setStyle("-fx-text-fill: #DC2626; -fx-font-size: 16px; -fx-font-weight: bold;");
        }
    }

    private Image taoQrImage(double amount, String orderId) {
        try {
            String qrUrl = QRGenerator.generateDefaultQRUrl(amount, orderId);
            if (qrUrl == null || qrUrl.isBlank()) {
                return null;
            }
            return new Image(qrUrl, true);
        } catch (Exception e) {
            return null;
        }
    }

    private double parseTien(String value) {
        if (value == null) {
            return 0;
        }
        String digitsOnly = value.replaceAll("[^0-9]", "");
        if (digitsOnly.isEmpty()) {
            return 0;
        }
        try {
            return Double.parseDouble(digitsOnly);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private String dinhDangTien(double amount) {
        return FMT_TIEN.format(Math.round(amount)) + " d";
    }

    private void hienStep1() {
        lblStep.setText("Buoc 1 / 2");
        step1Panel.setVisible(true);
        step1Panel.setManaged(true);
        step2Panel.setVisible(false);
        step2Panel.setManaged(false);
        btnBack.setDisable(true);
        btnBack.setVisible(true);
        btnNext.setVisible(true);
        btnConfirm.setVisible(false);
    }

    private void hienStep2() {
        lblStep.setText("Buoc 2 / 2");
        step1Panel.setVisible(false);
        step1Panel.setManaged(false);
        step2Panel.setVisible(true);
        step2Panel.setManaged(true);
        btnBack.setDisable(false);
        btnBack.setVisible(true);
        btnNext.setVisible(false);
        btnConfirm.setVisible(true);
    }

    private void dongDialog() {
        if (dialogStage != null) {
            dialogStage.close();
        }
    }

    private void hienThiLoiValidate(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        if (dialogStage != null) {
            alert.initOwner(dialogStage);
        }
        alert.setHeaderText("Du lieu khong hop le");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
