package com.bakery.views.controllers;

import com.bakery.model.dto.CTDonHangDTO;
import com.bakery.model.dto.CotBanhDTO;
import com.bakery.model.dto.DonDatHangDTO;
import com.bakery.model.dto.HoaDonDTO;
import com.bakery.model.dto.KichCoBanhDTO;
import com.bakery.model.dto.KieuTrangTriDTO;
import com.bakery.model.dto.NhanBanhDTO;
import com.bakery.model.dto.SanPhamDTO;
import com.bakery.presenters.OrderPresenter;
import com.bakery.services.OrderService;
import com.bakery.views.Receipt;
import com.bakery.views.interfaces.IOrderView;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.io.InputStream;
import java.net.URL;
import java.text.Normalizer;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.function.Function;

public class OrderController implements IOrderView, Initializable {

    @FXML private Button btnNavPOS;
    @FXML private Button btnNavTheoDoi;
    @FXML private Label lblHeaderTitle;
    @FXML private Label lblThongBaoHeader;

    @FXML private HBox tabTaoDon;
    @FXML private VBox tabTheoDoi;

    @FXML private ToggleButton btnLocTatCa;
    @FXML private ToggleButton btnLocCake;
    @FXML private ToggleButton btnLocCookie;
    @FXML private ToggleButton btnLocBread;
    @FXML private ToggleButton btnLocTuyChinh;
    @FXML private TextField txtTimKiemSanPham;

    @FXML private ScrollPane scrollSanPham;
    @FXML private FlowPane tileSanPham;
    @FXML private ScrollPane scrollTuyChinh;
    @FXML private ComboBox<SanPhamDTO> cbCustomSp;
    @FXML private ComboBox<KichCoBanhDTO> cbCustomKichCo;
    @FXML private ComboBox<CotBanhDTO> cbCustomCotBanh;
    @FXML private ComboBox<NhanBanhDTO> cbCustomNhanBanh;
    @FXML private ComboBox<KieuTrangTriDTO> cbCustomTrangTri;
    @FXML private TextArea txtCustomLoiChuc;
    @FXML private TextArea txtCustomGhiChu;
    @FXML private Spinner<Integer> spCustomSoLuong;
    @FXML private Label lblGiaTuyChinh;

    @FXML private TextField txtSoDienThoai;
    @FXML private Label lblTenKhachHang;
    @FXML private TableView<CTDonHangDTO> tblGioHang;
    @FXML private TableColumn<CTDonHangDTO, String> colTenSP;
    @FXML private TableColumn<CTDonHangDTO, Integer> colSoLuong;
    @FXML private TableColumn<CTDonHangDTO, String> colDonGia;
    @FXML private TableColumn<CTDonHangDTO, String> colThanhTien;
    @FXML private TableColumn<CTDonHangDTO, Void> colXoa;

    @FXML private Label lblTongTienHang;
    @FXML private Label lblTienGiamGia;
    @FXML private Label lblTongThanhToan;
    @FXML private Label lblCocToiThieu;
    @FXML private Label lblThongBaoTab1;
    @FXML private Button btnThanhToan;

    @FXML private TextField txtTimMaDon;
    @FXML private DatePicker dpNgayTheoDoi;
    @FXML private ComboBox<String> cbGioTu;
    @FXML private ComboBox<String> cbGioDen;
    @FXML private Label lblThongBaoTab2;
    @FXML private VBox panelChuaDon;

    private static final NumberFormat FMT_TIEN = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
    private static final DateTimeFormatter FMT_NGAY_GIO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final String CSS_ERROR = "-fx-text-fill: #DC2626; -fx-font-weight: bold;";
    private static final String CSS_SUCCESS = "-fx-text-fill: #16A34A; -fx-font-weight: bold;";
    private static final String CSS_INFO = "-fx-text-fill: #2563EB; -fx-font-weight: bold;";
    private static final String CSS_WARN = "-fx-text-fill: #D97706; -fx-font-weight: bold;";

    static {
        FMT_TIEN.setMaximumFractionDigits(0);
    }

    private final CreateOrderController dialogFactory = new CreateOrderController();
    private OrderPresenter presenter;

    private final List<SanPhamDTO> danhSachSanPham = new ArrayList<>();
    private final Map<Integer, SanPhamDTO> mapSanPhamById = new HashMap<>();
    private final Map<Integer, String> mapDanhMuc = new HashMap<>();
    private final List<String> danhSachTrangThai = new ArrayList<>();
    private final ObservableList<CTDonHangDTO> gioHangModel = FXCollections.observableArrayList();

    private String danhMucDangLoc = "ALL";
    private double tongThanhToanHienTai = 0.0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        khoiTaoBoLocDanhMuc();
        khoiTaoBangGioHang();
        khoiTaoComboTheoDoi();
        khoiTaoComboTuyChinh();
        khoiTaoSpinnerSoLuong();
        khoiTaoTabMacDinh();
        ganSuKienNhapLieu();

        presenter = new OrderPresenter(this, new OrderService());
        presenter.setDialogFactory(dialogFactory);
        presenter.taiDuLieuBanDau();
    }

    @FXML
    private void onNavPOS() {
        hienTabPos();
    }

    @FXML
    private void onNavTheoDoi() {
        hienTabTheoDoi();
    }

    @FXML
    private void onLocDanhMuc() {
        ToggleGroup group = btnLocTatCa.getToggleGroup();
        if (group == null || group.getSelectedToggle() == null) {
            btnLocTatCa.setSelected(true);
        }
        ToggleButton selected = (ToggleButton) group.getSelectedToggle();
        danhMucDangLoc = selected == null ? "ALL" : String.valueOf(selected.getUserData());
        apDungBoLocSanPham();
    }

    @FXML
    private void onTimKhachHang() {
        if (presenter == null) {
            return;
        }
        presenter.timKhachHang(txtSoDienThoai.getText());
    }

    @FXML
    private void onThanhToan() {
        if (presenter == null) {
            return;
        }
        presenter.moDialogTaoDon();
    }

    @FXML
    private void onTimKiemDon() {
        if (presenter == null) {
            return;
        }
        String maDon = txtTimMaDon.getText() == null ? "" : txtTimMaDon.getText().trim();
        LocalDate ngay = dpNgayTheoDoi.getValue() != null ? dpNgayTheoDoi.getValue() : LocalDate.now();
        LocalTime gioTu = parseGioTheoDoi(cbGioTu.getValue());
        LocalTime gioDen = parseGioTheoDoi(cbGioDen.getValue());
        presenter.timKiemDonTheoDoi(maDon, ngay, gioTu, gioDen);
    }

    @FXML
    private void onTuyChinhChanged() {
        capNhatGiaBanhTuyChinh();
    }

    @FXML
    private void onThemBanhTuyChinh() {
        if (presenter == null) {
            return;
        }
        SanPhamDTO sp = cbCustomSp.getValue();
        if (sp == null) {
            hienThiLoi("Vui lòng chọn loại bánh tùy chỉnh.");
            return;
        }

        int soLuong = spCustomSoLuong.getValue();
        Integer maKC = getMaKichCo();
        Integer maCot = getMaCot();
        Integer maNhan = getMaNhan();
        Integer maTrangTri = getMaTrangTri();

        double donGia = presenter.tinhGiaBanhTuyChinh(sp.getMaSP(), maKC, maCot, maNhan, maTrangTri);
        presenter.themBanhTuyChinhVaoGio(
                sp,
                soLuong,
                donGia,
                maKC,
                maCot,
                maNhan,
                maTrangTri,
                txtCustomLoiChuc.getText() == null ? "" : txtCustomLoiChuc.getText().trim(),
                txtCustomGhiChu.getText() == null ? "" : txtCustomGhiChu.getText().trim()
        );
    }

    @Override
    public void lamMoiBangGioHang(List<CTDonHangDTO> items, List<SanPhamDTO> originData) {
        gioHangModel.setAll(items);
        tblGioHang.refresh();
    }

    @Override
    public void lamMoiBaoCaoTien(double tongHang, double giamGia, double tongThanhToan,
                                 double minCoc, double conLai, double tienThua, boolean isThieuTienThua) {
        lblTongTienHang.setText(dinhDangTien(tongHang));
        lblTienGiamGia.setText(dinhDangTien(giamGia));
        lblTongThanhToan.setText(dinhDangTien(tongThanhToan));
        lblCocToiThieu.setText(dinhDangTien(minCoc));
        tongThanhToanHienTai = tongThanhToan;
    }

    @Override
    public void batTatNutThanhToan(boolean state) {
        btnThanhToan.setDisable(!state);
    }

    @Override
    public void hienThiThongTinKhach(String text, boolean isVip) {
        lblTenKhachHang.setText(text);
        lblTenKhachHang.setStyle(isVip ? CSS_SUCCESS : CSS_WARN);
    }

    @Override
    public void hienThiDanhSachSanPham(List<SanPhamDTO> ds, Map<Integer, String> dict) {
        danhSachSanPham.clear();
        danhSachSanPham.addAll(ds);
        mapDanhMuc.clear();
        mapDanhMuc.putAll(dict);
        mapSanPhamById.clear();
        for (SanPhamDTO sanPham : ds) {
            mapSanPhamById.put(sanPham.getMaSP(), sanPham);
        }
        apDungBoLocSanPham();
    }

    @Override
    public void hienThiDuLieuTuyChinh(List<SanPhamDTO> spTuyChinh, List<KichCoBanhDTO> kichCo,
                                      List<CotBanhDTO> cotBanh, List<NhanBanhDTO> nhanBanh,
                                      List<KieuTrangTriDTO> trangTri) {
        cbCustomSp.setItems(FXCollections.observableArrayList(spTuyChinh));
        if (!spTuyChinh.isEmpty()) {
            cbCustomSp.getSelectionModel().selectFirst();
        }

        ObservableList<KichCoBanhDTO> listKichCo = FXCollections.observableArrayList();
        listKichCo.add(null);
        listKichCo.addAll(kichCo);
        cbCustomKichCo.setItems(listKichCo);
        cbCustomKichCo.getSelectionModel().selectFirst();

        ObservableList<CotBanhDTO> listCot = FXCollections.observableArrayList();
        listCot.add(null);
        listCot.addAll(cotBanh);
        cbCustomCotBanh.setItems(listCot);
        cbCustomCotBanh.getSelectionModel().selectFirst();

        ObservableList<NhanBanhDTO> listNhan = FXCollections.observableArrayList();
        listNhan.add(null);
        listNhan.addAll(nhanBanh);
        cbCustomNhanBanh.setItems(listNhan);
        cbCustomNhanBanh.getSelectionModel().selectFirst();

        ObservableList<KieuTrangTriDTO> listTrangTri = FXCollections.observableArrayList();
        listTrangTri.add(null);
        listTrangTri.addAll(trangTri);
        cbCustomTrangTri.setItems(listTrangTri);
        cbCustomTrangTri.getSelectionModel().selectFirst();

        capNhatGiaBanhTuyChinh();
    }

    @Override
    public void taiDanhSachTrangThai(List<String> list) {
        danhSachTrangThai.clear();
        danhSachTrangThai.addAll(list);
    }

    @Override
    public void hienThiDanhSachDonTheoDoi(List<DonDatHangDTO> dsDonTheoDoi) {
        panelChuaDon.getChildren().clear();
        for (DonDatHangDTO don : dsDonTheoDoi) {
            panelChuaDon.getChildren().add(taoCardTheoDoi(don));
        }
    }

    @Override
    public void showOrderDetails(DonDatHangDTO order) {
        String noiDung = "Mã đơn: #" + order.getMaDon() + "\n"
                + "Trạng thái: " + order.getTenTrangThai() + "\n"
                + "Khách hàng: " + (order.getMaKH() == null ? "Khách lẻ" : "KH #" + order.getMaKH()) + "\n"
                + "Thời gian nhận: " + (order.getNgayGioNhanBanh() == null
                ? "N/A" : order.getNgayGioNhanBanh().format(FMT_NGAY_GIO)) + "\n"
                + "Tổng tiền: " + dinhDangTien(order.getTongTienHDBan()) + "\n"
                + "Đã cọc: " + dinhDangTien(order.getTienDaCoc());
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("Chi tiết đơn hàng");
        alert.setContentText(noiDung);
        alert.showAndWait();
    }

    @Override
    public void hienThiLoi(String msg) {
        lblThongBaoTab1.setStyle(CSS_ERROR);
        lblThongBaoTab1.setText(msg);
    }

    @Override
    public void hienThiThanhCong(String msg) {
        lblThongBaoTab1.setStyle(CSS_SUCCESS);
        lblThongBaoTab1.setText(msg);
    }

    @Override
    public void hienThiLoiTraCuu(String msg) {
        lblThongBaoTab2.setStyle(CSS_ERROR);
        lblThongBaoTab2.setText(msg);
    }

    @Override
    public void hienThiThongBaoTraCuu(String msg) {
        lblThongBaoTab2.setStyle(CSS_SUCCESS);
        lblThongBaoTab2.setText(msg);
    }

    @Override
    public void hienThiKetQuaTraCuu(String kh, String tt, double tongTien) {
        lblThongBaoTab2.setStyle(CSS_INFO);
        lblThongBaoTab2.setText("KQ: " + kh + " | " + tt + " | " + dinhDangTien(tongTien));
    }

    @Override
    public void lamMoiForm() {
        txtSoDienThoai.clear();
        lblTenKhachHang.setText("Khách vãng lai");
        lblTenKhachHang.setStyle("-fx-text-fill: #6B7280;");
        lblThongBaoTab1.setText("");
        txtCustomLoiChuc.clear();
        txtCustomGhiChu.clear();
        spCustomSoLuong.getValueFactory().setValue(1);
    }

    @Override
    public void inPhieuHoaDon(String tieuDe, Integer maDon, Integer maHoaDon,
                              LocalDateTime ngayLapHoaDon, double tongTien, double daThu,
                              List<CTDonHangDTO> cart, List<SanPhamDTO> data, double pGiam) {
        String maDonStr = maDon == null ? "N/A" : "#" + maDon;
        String maHoaDonStr = maHoaDon == null ? "N/A" : "#" + maHoaDon;
        String ngayLap = ngayLapHoaDon == null ? "N/A" : ngayLapHoaDon.format(FMT_NGAY_GIO);
        String khachHang = lblTenKhachHang.getText() == null ? "Khách hàng" : lblTenKhachHang.getText();
        String tienGiam = pGiam > 0 ? lblTienGiamGia.getText() : null;

        Receipt receipt = new Receipt(
                tieuDe,
                maDonStr,
                maHoaDonStr,
                ngayLap,
                khachHang,
                cart,
                data,
                tienGiam,
                dinhDangTien(tongTien),
                dinhDangTien(daThu),
                "N/A",
                null,
                null
        );
        receipt.setVisible(true);
    }

    @Override
    public void inHoaDonHoanThanh(DonDatHangDTO don, HoaDonDTO hd, List<CTDonHangDTO> dsItems) {
        String maDonStr = "#" + don.getMaDon();
        String maHoaDonStr = "#" + hd.getMaHD();
        String ngayLap = hd.getNgayXuatHd() == null ? LocalDateTime.now().format(FMT_NGAY_GIO)
                : hd.getNgayXuatHd().format(FMT_NGAY_GIO);
        String khachHang = don.getMaKH() == null ? "Khách lẻ" : "KH #" + don.getMaKH();

        Receipt receipt = new Receipt(
                "HÓA ĐƠN HOÀN THÀNH",
                maDonStr,
                maHoaDonStr,
                ngayLap,
                khachHang,
                dsItems,
                new ArrayList<>(mapSanPhamById.values()),
                null,
                dinhDangTien(don.getTongTienHDBan()),
                dinhDangTien(hd.getTongTienThanhToan()),
                "N/A",
                null,
                null
        );
        receipt.setVisible(true);
    }

    @Override
    public LocalDateTime getNgayGioNhanBanh() {
        return LocalDateTime.now();
    }

    @Override
    public double getTongThanhToanHienTai() {
        return tongThanhToanHienTai;
    }

    private void khoiTaoBoLocDanhMuc() {
        ToggleGroup group = new ToggleGroup();
        btnLocTatCa.setToggleGroup(group);
        btnLocCake.setToggleGroup(group);
        btnLocCookie.setToggleGroup(group);
        btnLocBread.setToggleGroup(group);
        btnLocTuyChinh.setToggleGroup(group);
        btnLocTatCa.setSelected(true);
    }

    private void khoiTaoBangGioHang() {
        tblGioHang.setItems(gioHangModel);

        colTenSP.setCellValueFactory(param -> new ReadOnlyStringWrapper(layTenSanPham(param.getValue().getMaSP())));
        colSoLuong.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().getSoLuong()));
        colDonGia.setCellValueFactory(param -> new ReadOnlyStringWrapper(dinhDangTien(param.getValue().getDonGia())));
        colThanhTien.setCellValueFactory(param -> new ReadOnlyStringWrapper(
                dinhDangTien(param.getValue().getDonGia() * param.getValue().getSoLuong())));

        colSoLuong.setCellFactory(col -> new TableCell<>() {
            private final Button btnTru = taoNutSoLuong("-");
            private final Button btnCong = taoNutSoLuong("+");
            private final Label lbl = new Label();
            private final HBox wrapper = new HBox(6, btnTru, lbl, btnCong);

            {
                wrapper.setAlignment(Pos.CENTER);
                btnTru.setOnAction(event -> {
                    if (presenter != null && getIndex() >= 0) {
                        presenter.thayDoiSoLuongMon(getIndex(), -1);
                    }
                });
                btnCong.setOnAction(event -> {
                    if (presenter != null && getIndex() >= 0) {
                        presenter.thayDoiSoLuongMon(getIndex(), +1);
                    }
                });
            }

            @Override
            protected void updateItem(Integer soLuong, boolean empty) {
                super.updateItem(soLuong, empty);
                if (empty || soLuong == null) {
                    setGraphic(null);
                    return;
                }
                lbl.setText(String.valueOf(soLuong));
                setGraphic(wrapper);
            }
        });

        colXoa.setCellFactory(col -> new TableCell<>() {
            private final Button btnXoa = new Button("✕");

            {
                btnXoa.getStyleClass().add("btn-danger");
                btnXoa.setPadding(new Insets(2, 8, 2, 8));
                btnXoa.setOnAction(event -> {
                    if (presenter != null && getIndex() >= 0) {
                        presenter.thayDoiSoLuongMon(getIndex(), 0);
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btnXoa);
            }
        });
    }

    private void khoiTaoComboTheoDoi() {
        dpNgayTheoDoi.setValue(LocalDate.now());
        cbGioTu.getItems().clear();
        cbGioDen.getItems().clear();
        cbGioTu.getItems().add("Tất cả");
        cbGioDen.getItems().add("Tất cả");
        for (int h = 0; h < 24; h++) {
            cbGioTu.getItems().add(String.format("%02d:00", h));
            cbGioDen.getItems().add(String.format("%02d:00", h));
            if (h < 23) {
                cbGioTu.getItems().add(String.format("%02d:30", h));
                cbGioDen.getItems().add(String.format("%02d:30", h));
            }
        }
        cbGioTu.setValue("Tất cả");
        cbGioDen.setValue("Tất cả");
    }

    private void khoiTaoComboTuyChinh() {
        caiDatHienThiCombo(cbCustomSp, sp -> sp.getTenSP() + " (" + dinhDangTien(sp.getGiaCoBan()) + ")", "--- Chọn loại bánh ---");
        caiDatHienThiCombo(cbCustomKichCo,
                kc -> kc.getTenKC() + " (+" + dinhDangTien(kc.getPhuPhi()) + ")",
                "--- Không chọn ---");
        caiDatHienThiCombo(cbCustomCotBanh,
                cot -> cot.getTenCot() + " (+" + dinhDangTien(cot.getPhuPhi()) + ")",
                "--- Không chọn ---");
        caiDatHienThiCombo(cbCustomNhanBanh,
                nhan -> nhan.getTenNhan() + " (+" + dinhDangTien(nhan.getPhuPhi()) + ")",
                "--- Không chọn ---");
        caiDatHienThiCombo(cbCustomTrangTri,
                tt -> tt.getTenTrangTri() + " (+" + dinhDangTien(tt.getPhuPhi()) + ")",
                "--- Không chọn ---");
    }

    private <T> void caiDatHienThiCombo(ComboBox<T> combo, Function<T, String> hienThi, String macDinh) {
        combo.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : (item == null ? macDinh : hienThi.apply(item)));
            }
        });
        combo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? macDinh : (item == null ? macDinh : hienThi.apply(item)));
            }
        });
    }

    private void khoiTaoSpinnerSoLuong() {
        spCustomSoLuong.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 99, 1));
    }

    private void khoiTaoTabMacDinh() {
        hienTabPos();
        scrollSanPham.setManaged(true);
        scrollSanPham.setVisible(true);
        scrollTuyChinh.setManaged(false);
        scrollTuyChinh.setVisible(false);
    }

    private void ganSuKienNhapLieu() {
        txtTimKiemSanPham.textProperty().addListener((obs, oldVal, newVal) -> apDungBoLocSanPham());
        txtSoDienThoai.setOnAction(event -> onTimKhachHang());
        tabTaoDon.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                dialogFactory.setOwnerWindow(newScene.getWindow());
            }
        });
    }

    private void hienTabPos() {
        tabTaoDon.setVisible(true);
        tabTaoDon.setManaged(true);
        tabTheoDoi.setVisible(false);
        tabTheoDoi.setManaged(false);
        datNavActive(btnNavPOS, btnNavTheoDoi);
        lblHeaderTitle.setText("POS — Bán hàng & Quản lý đơn");
    }

    private void hienTabTheoDoi() {
        tabTaoDon.setVisible(false);
        tabTaoDon.setManaged(false);
        tabTheoDoi.setVisible(true);
        tabTheoDoi.setManaged(true);
        datNavActive(btnNavTheoDoi, btnNavPOS);
        lblHeaderTitle.setText("Theo dõi đơn hàng");
        if (presenter != null) {
            presenter.timKiemDonTheoDoi(
                    txtTimMaDon.getText() == null ? "" : txtTimMaDon.getText().trim(),
                    dpNgayTheoDoi.getValue() == null ? LocalDate.now() : dpNgayTheoDoi.getValue(),
                    parseGioTheoDoi(cbGioTu.getValue()),
                    parseGioTheoDoi(cbGioDen.getValue())
            );
        }
    }

    private void datNavActive(Button active, Button inactive) {
        active.getStyleClass().remove("nav-item-active");
        active.getStyleClass().add("nav-item-active");
        inactive.getStyleClass().remove("nav-item-active");
    }

    private void apDungBoLocSanPham() {
        if ("CUSTOM".equalsIgnoreCase(danhMucDangLoc)) {
            scrollSanPham.setVisible(false);
            scrollSanPham.setManaged(false);
            scrollTuyChinh.setVisible(true);
            scrollTuyChinh.setManaged(true);
            return;
        }
        scrollSanPham.setVisible(true);
        scrollSanPham.setManaged(true);
        scrollTuyChinh.setVisible(false);
        scrollTuyChinh.setManaged(false);

        String tuKhoa = txtTimKiemSanPham.getText() == null ? "" : txtTimKiemSanPham.getText().trim().toLowerCase();
        tileSanPham.getChildren().clear();

        for (SanPhamDTO sanPham : danhSachSanPham) {
            String tenDanhMuc = mapDanhMuc.getOrDefault(sanPham.getMaDM(), "Khac");
            String danhMucChuan = mapDanhMucLoc(tenDanhMuc);

            boolean khopDanhMuc = "ALL".equalsIgnoreCase(danhMucDangLoc)
                    || danhMucDangLoc.equalsIgnoreCase(danhMucChuan);
            boolean khopTen = sanPham.getTenSP() != null
                    && sanPham.getTenSP().toLowerCase().contains(tuKhoa);

            if (khopDanhMuc && khopTen) {
                tileSanPham.getChildren().add(taoCardSanPham(sanPham));
            }
        }
    }

    private Node taoCardSanPham(SanPhamDTO sanPham) {
        VBox card = new VBox(8);
        card.getStyleClass().add("product-card");
        card.setAlignment(Pos.TOP_CENTER);
        card.setPadding(new Insets(10));

        ImageView imageView = new ImageView();
        imageView.setFitWidth(130);
        imageView.setFitHeight(100);
        imageView.setPreserveRatio(true);

        Image anh = taiAnhSanPham(sanPham.getHinhAnh());
        if (anh != null) {
            imageView.setImage(anh);
        }

        Label ten = new Label(sanPham.getTenSP());
        ten.getStyleClass().add("lbl-body-bold");
        ten.setWrapText(true);
        ten.setMaxWidth(140);

        Label gia = new Label(dinhDangTien(sanPham.getGiaCoBan()));
        gia.getStyleClass().add("lbl-primary");

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Button btnThem = new Button("Thêm");
        btnThem.getStyleClass().add("btn-primary");
        btnThem.setMaxWidth(Double.MAX_VALUE);
        btnThem.setOnAction(event -> {
            if (presenter != null) {
                presenter.themSanPhamVaoGio(sanPham);
            }
        });

        card.getChildren().addAll(imageView, ten, gia, spacer, btnThem);
        card.setOnMouseClicked(event -> {
            if (presenter != null) {
                presenter.themSanPhamVaoGio(sanPham);
            }
        });
        return card;
    }

    private Image taiAnhSanPham(String path) {
        if (path == null || path.isBlank()) {
            return null;
        }
        List<String> candidates = List.of(
                path,
                "/" + path,
                "/images/" + path,
                "/images/products/" + path
        );
        for (String candidate : candidates) {
            try (InputStream in = getClass().getResourceAsStream(candidate)) {
                if (in != null) {
                    return new Image(in);
                }
            } catch (Exception ignored) {
                // Không chặn UI nếu thiếu ảnh sản phẩm.
            }
        }
        return null;
    }

    private void capNhatGiaBanhTuyChinh() {
        if (presenter == null) {
            return;
        }
        SanPhamDTO sp = cbCustomSp.getValue();
        if (sp == null) {
            lblGiaTuyChinh.setText(dinhDangTien(0));
            return;
        }
        double gia = presenter.tinhGiaBanhTuyChinh(
                sp.getMaSP(),
                getMaKichCo(),
                getMaCot(),
                getMaNhan(),
                getMaTrangTri()
        );
        lblGiaTuyChinh.setText(dinhDangTien(gia));
    }

    private Integer getMaKichCo() {
        KichCoBanhDTO dto = cbCustomKichCo.getValue();
        return dto == null ? null : dto.getMaKC();
    }

    private Integer getMaCot() {
        CotBanhDTO dto = cbCustomCotBanh.getValue();
        return dto == null ? null : dto.getMaCot();
    }

    private Integer getMaNhan() {
        NhanBanhDTO dto = cbCustomNhanBanh.getValue();
        return dto == null ? null : dto.getMaNhan();
    }

    private Integer getMaTrangTri() {
        KieuTrangTriDTO dto = cbCustomTrangTri.getValue();
        return dto == null ? null : dto.getMaTrangTri();
    }

    private Node taoCardTheoDoi(DonDatHangDTO don) {
        VBox card = new VBox(10);
        card.getStyleClass().add("order-card");

        HBox header = new HBox(8);
        Label lblMaDon = new Label("#" + don.getMaDon());
        lblMaDon.getStyleClass().add("lbl-body-bold");

        Label badge = new Label(don.getTenTrangThai() == null ? "N/A" : don.getTenTrangThai());
        badge.getStyleClass().addAll("badge", mapStyleTrangThai(don.getTenTrangThai()));

        Region pushRight = new Region();
        HBox.setHgrow(pushRight, Priority.ALWAYS);
        header.getChildren().addAll(lblMaDon, pushRight, badge);

        Label lblKhach = new Label("Khách: " + (don.getMaKH() == null ? "Khách lẻ" : "KH #" + don.getMaKH()));
        lblKhach.getStyleClass().add("lbl-small");
        Label lblNgayNhan = new Label("Nhận lúc: " + (don.getNgayGioNhanBanh() == null
                ? "N/A" : don.getNgayGioNhanBanh().format(FMT_NGAY_GIO)));
        lblNgayNhan.getStyleClass().add("lbl-small");
        Label lblTongTien = new Label("Tổng: " + dinhDangTien(don.getTongTienHDBan()));
        lblTongTien.getStyleClass().add("lbl-primary");

        HBox action = new HBox(8);
        action.setAlignment(Pos.CENTER_LEFT);
        ComboBox<String> cbTrangThai = new ComboBox<>();
        cbTrangThai.setItems(FXCollections.observableArrayList(danhSachTrangThai));
        cbTrangThai.setValue(don.getTenTrangThai());
        cbTrangThai.setPrefWidth(180);

        Button btnCapNhat = new Button("Cập nhật");
        btnCapNhat.getStyleClass().add("btn-primary");
        btnCapNhat.setOnAction(event -> {
            if (presenter == null || cbTrangThai.getValue() == null) {
                return;
            }
            presenter.capNhatTrangThai(
                    String.valueOf(don.getMaDon()),
                    cbTrangThai.getValue(),
                    don.getTenTrangThai()
            );
        });

        Button btnChiTiet = new Button("Chi tiết");
        btnChiTiet.getStyleClass().add("btn-secondary");
        btnChiTiet.setOnAction(event -> showOrderDetails(don));

        action.getChildren().addAll(cbTrangThai, btnCapNhat, btnChiTiet);

        card.getChildren().addAll(header, lblKhach, lblNgayNhan, lblTongTien, action);
        return card;
    }

    private String mapStyleTrangThai(String trangThai) {
        if (trangThai == null) {
            return "badge-new";
        }
        String normalized = Normalizer.normalize(trangThai, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .replace("đ", "d")
                .replace("Đ", "D")
                .toUpperCase(Locale.ROOT);
        if (normalized.contains("HOAN THANH")) return "badge-done";
        if (normalized.contains("HUY")) return "badge-cancelled";
        if (normalized.contains("DANG SAN XUAT")) return "badge-processing";
        if (normalized.contains("CHO GIAO")) return "badge-shipping";
        if (normalized.contains("CHO KHACH LAY")) return "badge-pickup";
        if (normalized.contains("DA COC")) return "badge-deposited";
        return "badge-new";
    }

    private String mapDanhMucLoc(String tenDanhMuc) {
        String normalized = Normalizer.normalize(tenDanhMuc, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toUpperCase(Locale.ROOT);
        if (normalized.contains("CAKE")) return "Cake";
        if (normalized.contains("COOKIE")) return "Cookie";
        if (normalized.contains("BREAD") || normalized.contains("MI")) return "Bread";
        return "ALL";
    }

    private String layTenSanPham(int maSP) {
        SanPhamDTO sp = mapSanPhamById.get(maSP);
        return sp == null ? "SP #" + maSP : sp.getTenSP();
    }

    private LocalTime parseGioTheoDoi(String value) {
        if (value == null || value.isBlank() || "Tất cả".equalsIgnoreCase(value)) {
            return null;
        }
        return LocalTime.parse(value);
    }

    private Button taoNutSoLuong(String text) {
        Button button = new Button(text);
        button.getStyleClass().add("btn-qty");
        button.setPadding(new Insets(2, 6, 2, 6));
        return button;
    }

    private String dinhDangTien(double amount) {
        return FMT_TIEN.format(amount) + " đ";
    }
}
