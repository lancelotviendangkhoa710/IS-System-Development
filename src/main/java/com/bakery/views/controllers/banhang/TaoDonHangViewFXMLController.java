package com.bakery.views.controllers.banhang;

import com.bakery.utils.QRGenerator;
import com.bakery.views.interfaces.banhang.IDonHangDialogFactory;
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

import com.bakery.model.dao.hethong.CauHinhGioiHanDAO;
import com.bakery.model.dto.hethong.CauHinhGioiHanDTO;
import com.bakery.services.khachhang.KhachHangService;
import com.bakery.model.dto.khachhang.KhachHangDTO;
import java.net.URL;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Dialog factory + FXML controller cho luong tao don hang.
 * Presenter chi biet IOrderDialogFactory va khong phu thuoc framework UI.
 */
public class TaoDonHangViewFXMLController implements IDonHangDialogFactory {
    private static final Logger LOGGER = Logger.getLogger(TaoDonHangViewFXMLController.class.getName());

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
    @FXML private Label lblNangLuc;

    private final CauHinhGioiHanDAO cauHinhGioiHanDAO = new CauHinhGioiHanDAO();

    private Window ownerWindow;
    private Stage dialogStage;

    private double tongTienPhaiTra;
    private IDonHangDialogFactory.TraCuuKhachHang customerLookup;
    private IDonHangDialogFactory.YeuCauDonHang result = IDonHangDialogFactory.YeuCauDonHang.cancelled();

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
            txtKhachDua.textProperty().addListener((obs, oldVal, newVal) -> capNhatHienThiThanhToan());
        }
        if (txtTienCoc != null) {
            txtTienCoc.textProperty().addListener((obs, oldVal, newVal) -> capNhatHienThiThanhToan());
        }
        // Listener đổi ngày giao → cập nhật biến đếm năng lực sản xuất
        if (dpNgayGiao != null) {
            dpNgayGiao.valueProperty().addListener((obs, oldVal, ngayMoi) -> capNhatNangLuc(ngayMoi));
        }
    }

    /** Truy vấn và hiển thị năng lực sản xuất cho ngày giao đã chọn (ngaySanXuat = ngayGiao - 1). */
    private void capNhatNangLuc(LocalDate ngayGiao) {
        if (lblNangLuc == null || ngayGiao == null) return;
        // Giả định chuẩn bị 1 ngày → sản xuất ngày trước ngày giao
        LocalDate ngaySanXuat = ngayGiao.minusDays(1);
        try {
            CauHinhGioiHanDTO nangLuc = cauHinhGioiHanDAO.layTheoNgay(ngaySanXuat);
            if (nangLuc == null) {
                lblNangLuc.setText("⚠ Chưa cài giới hạn cho ngày SX " + ngaySanXuat);
                lblNangLuc.getStyleClass().setAll("lbl-warning");
                if (btnConfirm != null) btnConfirm.setDisable(false);
                return;
            }
            int daNhan = nangLuc.getSoBanhDaNhan();
            int gioi = nangLuc.getGioiHanSoBanh();
            boolean day = daNhan >= gioi;
            lblNangLuc.setText(String.format("📦 %d/%d bánh%s", daNhan, gioi, day ? " — ĐẦY" : ""));
            lblNangLuc.getStyleClass().setAll(day ? "lbl-danger" : "lbl-success");
            if (btnConfirm != null) btnConfirm.setDisable(day);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Không tải được năng lực sản xuất", e);
            lblNangLuc.setText("");
        }
    }

    @Override
    public IDonHangDialogFactory.YeuCauDonHang showCreateOrderDialog(double tongTienPhaiTra, IDonHangDialogFactory.TraCuuKhachHang customerLookup) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/banhang/TaoDonHangDialog.fxml"));
            Parent root = loader.load();
            TaoDonHangViewFXMLController controller = loader.getController();

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
            stage.setMinWidth(600);
            stage.setMinHeight(550);
            controller.khoiTaoDialog(stage, tongTienPhaiTra, customerLookup);
            stage.showAndWait();

            return controller.result == null ? IDonHangDialogFactory.YeuCauDonHang.cancelled() : controller.result;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Loi mo dialog tao don hang", e);
            return IDonHangDialogFactory.YeuCauDonHang.cancelled();
        }
    }

    @Override
    public boolean showPaymentConfirmation(int maDon, double tongTien, double daCoc, double conLai) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/banhang/ThanhToanDialog.fxml"));
            Parent root = loader.load();
            ThanhToanDialogViewFXMLController controller = loader.getController();
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
            LOGGER.log(Level.SEVERE, "Loi mo dialog xac nhan thanh toan", e);
            return false;
        }
    }

    @Override
    public IDonHangDialogFactory.YeuCauHuyDonHang showCancelOrderDialog(int maDon, double depositAmount) {
        try {
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            if (ownerWindow != null) stage.initOwner(ownerWindow);
            stage.setTitle("Hủy đơn hàng #" + maDon);

            VBox root = new VBox(15);
            root.setPadding(new javafx.geometry.Insets(25));
            root.getStyleClass().add("bg-surface");
            root.setPrefWidth(450);

            // Tiêu đề — dùng CSS class thay vì inline style (fix UI spec VI3)
            Label lblTitle = new Label("Hủy đơn và hoàn cọc");
            lblTitle.getStyleClass().add("lbl-title-dialog");

            Label lblInfo = new Label("Số tiền khách đã đặt cọc: " + dinhDangTien(depositAmount));
            lblInfo.getStyleClass().add("lbl-body");

            VBox inputGroup1 = new VBox(8);
            Label lblReason = new Label("Lý do hủy đơn: *");
            lblReason.getStyleClass().add("lbl-body-bold");
            TextField txtReason = new TextField();
            txtReason.setPromptText("Nhập lý do hủy (ví dụ: Khách đổi ý, Hết nguyên liệu...)");
            txtReason.getStyleClass().add("text-field");
            inputGroup1.getChildren().addAll(lblReason, txtReason);

            VBox inputGroup2 = new VBox(8);
            Label lblRefund = new Label("Số tiền hoàn trả cho khách:");
            lblRefund.getStyleClass().add("lbl-body-bold");
            TextField txtRefund = new TextField(String.valueOf((long) depositAmount));
            txtRefund.setPromptText("Nhập số tiền hoàn trả...");
            txtRefund.getStyleClass().add("text-field");
            inputGroup2.getChildren().addAll(lblRefund, txtRefund);

            final IDonHangDialogFactory.YeuCauHuyDonHang[] finalResult = {IDonHangDialogFactory.YeuCauHuyDonHang.cancelled()};

            Button btnSubmit = new Button("✓ Xác nhận hủy đơn");
            btnSubmit.getStyleClass().add("btn-danger");
            btnSubmit.setMaxWidth(Double.MAX_VALUE);
            btnSubmit.setPrefHeight(40);
            btnSubmit.setOnAction(e -> {
                if (txtReason.getText().trim().isEmpty()) {
                    hienThiLoiValidate("Vui lòng nhập lý do hủy đơn.");
                    return;
                }
                double refund = parseTien(txtRefund.getText());
                finalResult[0] = new IDonHangDialogFactory.YeuCauHuyDonHang(true, txtReason.getText().trim(), refund);
                stage.close();
            });

            Button btnClose = new Button("Đóng / Bỏ qua");
            btnClose.getStyleClass().add("btn-secondary");
            btnClose.setMaxWidth(Double.MAX_VALUE);
            btnClose.setPrefHeight(40);
            btnClose.setOnAction(e -> stage.close());

            root.getChildren().addAll(lblTitle, lblInfo, inputGroup1, inputGroup2, btnSubmit, btnClose);

            Scene scene = new Scene(root);
            URL cssUrl = getClass().getResource("/css/bakery.css");
            if (cssUrl != null) scene.getStylesheets().add(cssUrl.toExternalForm());

            stage.setScene(scene);
            stage.showAndWait();
            return finalResult[0];
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Loi mo dialog huy don hang", e);
            return IDonHangDialogFactory.YeuCauHuyDonHang.cancelled();
        }
    }

    private void khoiTaoDialog(Stage stage, double tongTien, IDonHangDialogFactory.TraCuuKhachHang lookup) {
        this.dialogStage = stage;
        this.tongTienPhaiTra = tongTien;
        this.customerLookup = lookup;
        this.result = IDonHangDialogFactory.YeuCauDonHang.cancelled();

        this.maKH = null;
        this.tenKhach = "Khach vang lai";
        this.soDienThoai = "";

        lblStep.setText("Buoc 1 / 2");
        hienStep1();

        lblTongTien.setText(dinhDangTien(tongTienPhaiTra));
        lblKhachInfo.setText("Chua xac dinh - se tao don vang lai");
        lblKhachInfo.getStyleClass().setAll("lbl-muted");
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
            lblKhachInfo.getStyleClass().setAll("lbl-muted");
            return;
        }

        if (customerLookup == null) {
            maKH = null;
            tenKhach = "Khach vang lai";
            soDienThoai = sdt;
            lblKhachInfo.setText("Khong tim thay khach hang");
            lblKhachInfo.getStyleClass().setAll("lbl-danger");
            return;
        }

        try {
            String[] info = customerLookup.lookup(sdt);
            if (info != null && info.length >= 2) {
                maKH = Integer.parseInt(info[0]);
                tenKhach = info[1];
                soDienThoai = sdt;
                lblKhachInfo.setText("OK " + tenKhach);
                lblKhachInfo.getStyleClass().setAll("lbl-success");
            } else {
                maKH = null;
                tenKhach = "Khach chua dang ky";
                soDienThoai = sdt;
                lblKhachInfo.setText("Khong tim thay - dat don vang lai");
                lblKhachInfo.getStyleClass().setAll("lbl-danger");
            }
        } catch (Exception e) {
            maKH = null;
            tenKhach = "Khach vang lai";
            soDienThoai = sdt;
            lblKhachInfo.setText("Khong tim thay - dat don vang lai");
            lblKhachInfo.getStyleClass().setAll("lbl-danger");
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
        
        try {
            KhachHangService service = new KhachHangService();
            KhachHangDTO fullKh = service.timKhachHangTheoSoDienThoai(soDienThoai);
            moDialogKhachHang(fullKh);
        } catch (Exception e) {
            hienThiLoiValidate("Không thể tải thông tin khách hàng: " + e.getMessage());
        }
    }

    private void moDialogKhachHang(KhachHangDTO kh) {
        try {
            java.net.URL fxmlUrl = getClass().getResource("/fxml/khachhang/KhachHangDialog.fxml");
            if (fxmlUrl == null) throw new RuntimeException("Không tìm thấy KhachHangDialog.fxml");
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(fxmlUrl);
            javafx.scene.Scene scene = new javafx.scene.Scene(loader.load());
            KhachHangDialogViewFXMLController controller = loader.getController();
            
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

            KhachHangDTO kq = controller.getKetQua();
            if (kq != null) {
                this.maKH = kq.getMaKH();
                this.tenKhach = kq.getHoTen();
                this.soDienThoai = kq.getSdt();
                txtSDT.setText(this.soDienThoai);
                lblKhachInfo.setText("OK " + tenKhach);
                lblKhachInfo.getStyleClass().setAll("lbl-success");
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
        if (!btnImmediateFlow.isSelected() && !btnPreorderFlow.isSelected()) {
            btnImmediateFlow.setSelected(true);
        }
        boolean isImmediate = btnImmediateFlow.isSelected();
        panelImmediate.setManaged(isImmediate);
        panelImmediate.setVisible(isImmediate);
        panelPreorder.setManaged(!isImmediate);
        panelPreorder.setVisible(!isImmediate);
        capNhatHienThiThanhToan();
    }

    @FXML
    private void onChonHinhThuc() {
        if (!btnCash.isSelected() && !btnTransfer.isSelected()) {
            btnCash.setSelected(true);
        }
        capNhatHienThiThanhToan();
    }

    @FXML
    private void onChonKieuCoc() {
        if (!btnFullPay.isSelected() && !btnDeposit.isSelected()) {
            btnDeposit.setSelected(true);
        }

        double minCoc = tongTienPhaiTra * 0.5;
        lblCocToiThieu.setText("(tối thiểu " + dinhDangTien(minCoc) + ")");
        if (btnFullPay.isSelected()) {
            txtTienCoc.setText(String.valueOf((long) tongTienPhaiTra));
            txtTienCoc.setDisable(true);
        } else {
            txtTienCoc.setText(String.valueOf((long) minCoc));
            txtTienCoc.setDisable(false);
        }
        capNhatHienThiThanhToan();
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

        result = new IDonHangDialogFactory.YeuCauDonHang(
                true,
                maKH,
                tenKhach,
                soDienThoai,
                IDonHangDialogFactory.LoaiDonHang.IMMEDIATE,
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
        // Kiểm tra năng lực một lần nữa trước khi submit (Fail-Fast)
        capNhatNangLuc(ngay);
        if (btnConfirm != null && btnConfirm.isDisable()) {
            hienThiLoiValidate("Ngày sản xuất " + (ngay != null ? ngay.minusDays(1) : "") + " đã đạt công suất tối đa. Vui lòng chọn ngày giao khác.");
            return;
        }

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
            hienThiLoiValidate("Tiền cọc phải tối thiểu 50% (" + dinhDangTien(minCoc) + ").");
            return;
        }

        String hinhThucThanhToan;
        double soTienKhachDua = tienCoc;

        if (btnCash.isSelected()) {
            hinhThucThanhToan = "Tiền mặt";
            soTienKhachDua = parseTien(txtKhachDua.getText());
            if (soTienKhachDua < tienCoc) {
                hienThiLoiValidate("Số tiền khách đưa chưa đủ để đặt cọc.");
                return;
            }
        } else {
            hinhThucThanhToan = "Chuyển khoản";
        }

        result = new IDonHangDialogFactory.YeuCauDonHang(
                true,
                maKH,
                tenKhach,
                soDienThoai,
                IDonHangDialogFactory.LoaiDonHang.PREORDER,
                hinhThucThanhToan,
                soTienKhachDua,
                ngayGioNhan,
                diaChi,
                tienCoc
        );
        dongDialog();
    }

    private void capNhatHienThiThanhToan() {
        boolean isCash = btnCash.isSelected();
        panelTienMat.setVisible(isCash);
        panelTienMat.setManaged(isCash);
        panelQR.setVisible(!isCash);
        panelQR.setManaged(!isCash);

        double amountToPay = tongTienPhaiTra;
        if (btnPreorderFlow.isSelected()) {
            amountToPay = parseTien(txtTienCoc.getText());
        }

        if (isCash) {
            double khachDua = parseTien(txtKhachDua.getText());
            double chenhLech = khachDua - amountToPay;
            if (chenhLech >= 0) {
                lblTienThua.setText(dinhDangTien(chenhLech));
                lblTienThua.getStyleClass().setAll("lbl-tien-thua-success");
            } else {
                lblTienThua.setText("Thiếu " + dinhDangTien(-chenhLech));
                lblTienThua.getStyleClass().setAll("lbl-tien-thua-danger");
            }
        } else {
            lblQRAmount.setText(dinhDangTien(amountToPay));
            imgQR.setImage(taoQrImage(amountToPay, "DonHang"));
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
            LOGGER.log(Level.WARNING, "Loi tao ma QR thanh toan", e);
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
