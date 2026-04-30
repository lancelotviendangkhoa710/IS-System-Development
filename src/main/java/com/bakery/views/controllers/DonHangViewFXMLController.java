package com.bakery.views.controllers;

import com.bakery.model.dto.CTDonHangDTO;
import com.bakery.model.dto.CotBanhDTO;
import com.bakery.model.dto.DonDatHangDTO;
import com.bakery.model.dto.HoaDonDTO;
import com.bakery.model.dto.KichCoBanhDTO;
import com.bakery.model.dto.KieuTrangTriDTO;
import com.bakery.model.dto.NhanVienDTO;
import com.bakery.model.dto.NhanBanhDTO;
import com.bakery.model.dto.SanPhamDTO;

import com.bakery.presenters.DonHangPresenter;
import com.bakery.services.PhanQuyenService;
import com.bakery.services.DonHangService;
import com.bakery.utils.UserSession;
import com.bakery.views.interfaces.IDonHangView;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
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
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.InputStream;
import java.net.URL;
import java.text.Normalizer;
import java.text.NumberFormat;
import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.function.Function;

/**
 * Controller cho giao diện Bán hàng (Order View).
 * Quản lý quy trình chọn sản phẩm, tùy chỉnh bánh theo yêu cầu, giỏ hàng và
 * thanh toán.
 */
public class DonHangViewFXMLController implements IDonHangView, Initializable {

    @FXML
    private Button btnNavPOS;
    @FXML
    private Button btnNavTheoDoi;
    @FXML
    private Label lblHeaderTitle;
    @FXML
    private Label lblThongBaoHeader;
    @FXML
    private Label lblNguoiDungDangNhap;
    @FXML
    private Label lblQuyenDangNhap;

    @FXML
    private HBox tabTaoDon;

    @FXML
    private ToggleButton btnLocTatCa;
    @FXML
    private ToggleButton btnLocCake;
    @FXML
    private ToggleButton btnLocCookie;
    @FXML
    private ToggleButton btnLocBread;
    @FXML
    private ToggleButton btnLocTuyChinh;
    @FXML
    private TextField txtTimKiemSanPham;

    @FXML
    private ScrollPane scrollSanPham;
    @FXML
    private FlowPane tileSanPham;
    @FXML
    private ScrollPane scrollTuyChinh;
    @FXML
    private ComboBox<SanPhamDTO> cbCustomSp;
    @FXML
    private ComboBox<KichCoBanhDTO> cbCustomKichCo;
    @FXML
    private ComboBox<CotBanhDTO> cbCustomCotBanh;
    @FXML
    private ComboBox<NhanBanhDTO> cbCustomNhanBanh;
    @FXML
    private ComboBox<KieuTrangTriDTO> cbCustomTrangTri;
    @FXML
    private TextArea txtCustomLoiChuc;
    @FXML
    private TextArea txtCustomGhiChu;
    @FXML
    private Spinner<Integer> spCustomSoLuong;
    @FXML
    private Label lblGiaTuyChinh;

    @FXML
    private TableView<CTDonHangDTO> tblGioHang;
    @FXML
    private TableColumn<CTDonHangDTO, String> colTenSP;
    @FXML
    private TableColumn<CTDonHangDTO, Integer> colSoLuong;
    @FXML
    private TableColumn<CTDonHangDTO, String> colDonGia;
    @FXML
    private TableColumn<CTDonHangDTO, String> colThanhTien;
    @FXML
    private TableColumn<CTDonHangDTO, Void> colXoa;

    @FXML
    private Label lblTongTienHang;
    @FXML
    private Label lblTienGiamGia;
    @FXML
    private Label lblTongThanhToan;
    @FXML
    private Label lblCocToiThieu;
    @FXML
    private Label lblThongBaoTab1;
    @FXML
    private Button btnThanhToan;

    private static final NumberFormat FMT_TIEN = NumberFormat.getNumberInstance(Locale.of("vi", "VN"));

    static {
        FMT_TIEN.setMaximumFractionDigits(0);
    }

    private final TaoDonHangViewFXMLController dialogFactory = new TaoDonHangViewFXMLController();
    private final PhanQuyenService authorizationService = new PhanQuyenService();
    private DonHangPresenter presenter;

    private final List<SanPhamDTO> danhSachSanPham = new ArrayList<>();
    private final Map<Integer, SanPhamDTO> mapSanPhamById = new HashMap<>();
    private final Map<Integer, String> mapDanhMuc = new HashMap<>();

    private final ObservableList<CTDonHangDTO> gioHangModel = FXCollections.observableArrayList();

    private String danhMucDangLoc = "ALL";
    private double tongThanhToanHienTai = 0.0;

    private com.bakery.model.dto.KhachHangDTO khachHangHienTai = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        capNhatThongTinNguoiDung();
        khoiTaoBoLocDanhMuc();
        khoiTaoBangGioHang();
        khoiTaoComboTuyChinh();
        khoiTaoSpinnerSoLuong();
        khoiTaoTabMacDinh();
        ganSuKienNhapLieu();

        presenter = new DonHangPresenter(this, new DonHangService(), dialogFactory);
        presenter.taiDuLieuBanDau();
    }

    public void apDungThongTinDangNhap(NhanVienDTO nhanVien) {
        UserSession.setCurrentUser(nhanVien);
        capNhatThongTinNguoiDung();
    }

    @FXML
    private void onBackToMenu() {
        try {
            URL fxmlUrl = getClass().getResource("/fxml/MainMenuView.fxml");
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Scene scene = new Scene(loader.load(), 1280, 720);

            MainMenuViewFXMLController controller = loader.getController();
            controller.khoiTaoThongTinDangNhap(UserSession.getCurrentUser());

            URL cssUrl = getClass().getResource("/css/bakery.css");
            if (cssUrl != null)
                scene.getStylesheets().add(cssUrl.toExternalForm());

            Stage stage = (Stage) btnNavPOS.getScene().getWindow();
            stage.setTitle("H3K Bakery - Management Console");
            stage.setScene(scene);
            stage.centerOnScreen();
        } catch (Exception ex) {
            System.err.println("Lỗi quay lại Menu: " + ex.getMessage());
        }
    }

    @FXML
    private void onNavPOS() {
        hienTabPos();
    }

    @FXML
    public void onNavTheoDoi() {
        try {
            URL fxmlUrl = getClass().getResource("/fxml/TheoDoiDonHangView.fxml");
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();

            Scene scene = new Scene(root, 1366, 768);
            URL cssUrl = getClass().getResource("/css/bakery.css");
            if (cssUrl != null)
                scene.getStylesheets().add(cssUrl.toExternalForm());

            Stage stage = (Stage) btnNavTheoDoi.getScene().getWindow();
            stage.setTitle("H3K Bakery - Theo Dõi Đơn");
            stage.setScene(scene);
            stage.centerOnScreen();
        } catch (Exception ex) {
            lblThongBaoHeader.setText("Không thể mở Theo dõi đơn: " + ex.getMessage());
            ex.printStackTrace();
        }
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
    private void onThanhToan() {
        if (presenter == null) {
            return;
        }
        presenter.moDialogTaoDon();
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
                txtCustomGhiChu.getText() == null ? "" : txtCustomGhiChu.getText().trim());
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
        // Thông tin khách hàng đã được tách ra module riêng
    }

    @Override
    public void capNhatKhachHangHienTai(com.bakery.model.dto.KhachHangDTO kh) {
        this.khachHangHienTai = kh;
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
    public void hienThiLoiTraCuu(String msg) {
        // Implement as needed for Order Tracking tab
    }

    @Override
    public void hienThiThongBaoTraCuu(String msg) {
        // Implement as needed
    }

    @Override
    public void hienThiKetQuaTraCuu(String kh, String tt, double tongTien) {
        // Implement as needed
    }

    @Override
    public void taiDanhSachTrangThai(List<String> list) {
        // Implement for filter dropdown
    }

    @Override
    public void hienThiDanhSachDonTheoDoi(List<DonDatHangDTO> dsDonTheoDoi) {
        // Implement card rendering for tracking
    }

    @Override
    public void showOrderDetails(DonDatHangDTO order) {
        // Implement detail dialog
    }

    @Override
    public void hienThiLoi(String msg) {
        lblThongBaoTab1.getStyleClass().setAll("lbl-danger");
        lblThongBaoTab1.setText(msg);
    }

    @Override
    public void hienThiThanhCong(String msg) {
        lblThongBaoTab1.getStyleClass().setAll("lbl-success");
        lblThongBaoTab1.setText(msg);
    }

    @Override
    public void lamMoiForm() {
        khachHangHienTai = null;
        lblThongBaoTab1.setText("");
        txtCustomLoiChuc.clear();
        txtCustomGhiChu.clear();
        spCustomSoLuong.getValueFactory().setValue(1);
    }

    @Override
    public void inPhieuHoaDon(String tieuDe, HoaDonDTO hd, DonDatHangDTO don,
            List<CTDonHangDTO> cart, List<SanPhamDTO> data, double pGiam,
            double khachDua, double tienThua, boolean laDonCoc) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/HoaDonView.fxml"));
            Parent root = loader.load();

            HoaDonViewFXMLController controller = loader.getController();

            String tenKhach = (khachHangHienTai != null) ? khachHangHienTai.getHoTen() : "Khách hàng";

            double soTienGiam = (pGiam > 0 && pGiam < 1) ? (hd.getTongTienThanhToan().doubleValue() * pGiam / (1 - pGiam)) : 0;

            controller.setReceiptData(tieuDe, hd, don, cart, data, tenKhach, khachDua, tienThua, soTienGiam, laDonCoc);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle(tieuDe);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            hienThiLoi("Không thể in hóa đơn: " + e.getMessage());
            System.err.println("[DonHangViewFXMLController] Lỗi: " + e.getMessage());
        }
    }

    @Override
    public void inHoaDonHoanThanh(DonDatHangDTO don, HoaDonDTO hd, List<CTDonHangDTO> dsItems,
            double khachDua, double tienThua, boolean laDonCoc) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/HoaDonView.fxml"));
            Parent root = loader.load();

            HoaDonViewFXMLController controller = loader.getController();

            String tenKhach = "Khách lẻ";
            if (don != null) {
                tenKhach = (don.getMaKH() == null) ? "Khách lẻ" : "KH #" + don.getMaKH();
            } else if (khachHangHienTai != null) {
                tenKhach = khachHangHienTai.getHoTen();
            }

            controller.setReceiptData("HÓA ĐƠN HOÀN THÀNH", hd, don, dsItems, new ArrayList<>(mapSanPhamById.values()),
                    tenKhach, khachDua, tienThua, 0.0, laDonCoc);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("HÓA ĐƠN HOÀN THÀNH");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            hienThiLoi("Không thể in hóa đơn hoàn thành: " + e.getMessage());
        }
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
                dinhDangTien((param.getValue().getDonGia() != null ? param.getValue().getDonGia().doubleValue() : 0.0)
                        * param.getValue().getSoLuong())));

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

    private void khoiTaoComboTuyChinh() {
        caiDatHienThiCombo(cbCustomSp, sp -> sp.getTenSP() + " (" + dinhDangTien(sp.getGiaCoBan()) + ")",
                "--- Chọn loại bánh ---");
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
        tabTaoDon.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                dialogFactory.setOwnerWindow(newScene.getWindow());
            }
        });
    }

    private void hienTabPos() {
        tabTaoDon.setVisible(true);
        tabTaoDon.setManaged(true);
        datNavActive(btnNavPOS, btnNavTheoDoi);
        lblHeaderTitle.setText("POS — Bán hàng & Quản lý đơn");
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
            String tenDanhMuc = mapDanhMuc.getOrDefault(sanPham.getMaDM(), "Khác");
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

        javafx.scene.layout.StackPane imageContainer = new javafx.scene.layout.StackPane();
        imageContainer.getChildren().add(imageView);

        Label lblStock = new Label();
        lblStock.getStyleClass().add("lbl-stock-badge");
        javafx.scene.layout.StackPane.setAlignment(lblStock, Pos.TOP_RIGHT);
        javafx.scene.layout.StackPane.setMargin(lblStock, new Insets(4));

        Button btnThem = new Button("Thêm");
        btnThem.getStyleClass().add("btn-primary");
        btnThem.setMaxWidth(Double.MAX_VALUE);

        if (sanPham.getSoLuongTon() <= 0) {
            lblStock.getStyleClass().add("stock-empty");
            lblStock.setText("Hết");
            card.setOpacity(0.5);
            btnThem.setDisable(true);
        } else {
            lblStock.getStyleClass().add("stock-available");
            lblStock.setText("Kho: " + (int) sanPham.getSoLuongTon());
        }
        imageContainer.getChildren().add(lblStock);

        Label ten = new Label(sanPham.getTenSP());
        ten.getStyleClass().add("lbl-body-bold");
        ten.setWrapText(true);
        ten.setMaxWidth(140);

        Label gia = new Label(dinhDangTien(sanPham.getGiaCoBan()));
        gia.getStyleClass().add("lbl-primary");

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        btnThem.setOnAction(event -> {
            if (presenter != null) {
                presenter.themSanPhamVaoGio(sanPham);
            }
        });

        card.getChildren().addAll(imageContainer, ten, gia, spacer, btnThem);
        card.setOnMouseClicked(event -> {
            if (presenter != null && sanPham.getSoLuongTon() > 0) {
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
                "/images/products/" + path);
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
                getMaTrangTri());
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

    private String mapDanhMucLoc(String tenDanhMuc) {
        if (tenDanhMuc == null)
            return "ALL";
        String normalized = Normalizer.normalize(tenDanhMuc, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toUpperCase(Locale.ROOT);
        if (normalized.contains("CAKE"))
            return "Cake";
        if (normalized.contains("COOKIE"))
            return "Cookie";
        if (normalized.contains("BREAD") || normalized.contains("MI"))
            return "Bread";
        return "ALL";
    }

    private String layTenSanPham(int maSP) {
        SanPhamDTO sp = mapSanPhamById.get(maSP);
        return sp == null ? "SP #" + maSP : sp.getTenSP();
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

    private String dinhDangTien(java.math.BigDecimal amount) {
        if (amount == null)
            return FMT_TIEN.format(0) + " đ";
        return FMT_TIEN.format(amount.doubleValue()) + " đ";
    }

    private void capNhatThongTinNguoiDung() {
        NhanVienDTO nhanVien = UserSession.getCurrentUser();
        if (nhanVien == null) {
            return;
        }

        String tenHienThi = nhanVien.getHoTen() == null || nhanVien.getHoTen().isBlank()
                ? nhanVien.getTenDangNhap()
                : nhanVien.getHoTen();
        boolean laAdmin = authorizationService.laAdmin(nhanVien);
        String tenVaiTro = nhanVien.getTenVaiTro();
        String quyenHienThi = (tenVaiTro != null && !tenVaiTro.isBlank())
                ? (laAdmin ? tenVaiTro + " - Full Access" : tenVaiTro + " - Role Access")
                : (laAdmin ? "Admin Access" : "Role Access");

        if (lblNguoiDungDangNhap != null) {
            lblNguoiDungDangNhap.setText(tenHienThi);
        }
        if (lblQuyenDangNhap != null) {
            lblQuyenDangNhap.setText(quyenHienThi);
        }
    }
}
