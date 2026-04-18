package com.bakery.controllers;

import com.bakery.dto.DonDatHangDTO;
import com.bakery.dto.KhachHangDTO;
import com.bakery.dto.SanPhamDTO;
import com.bakery.dto.TrangThaiDonDTO;
import com.bakery.dto.YeuCauChiTietDonHangDTO;
import com.bakery.dto.YeuCauTaoDonHangDTO;
import com.bakery.services.OrderService;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public class OrderController {
    private static final int MOCK_CURRENT_USER_ID = 1;
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private final OrderService orderService = new OrderService();
    private final ObservableList<GioHangItemFX> gioHangItems = FXCollections.observableArrayList();
    private final ObservableList<SanPhamFX> tatCaSanPham = FXCollections.observableArrayList();
    private final FilteredList<SanPhamFX> sanPhamLoc = new FilteredList<>(tatCaSanPham, item -> true);
    private final Map<String, Integer> mapTrangThaiMoi = new HashMap<>();
    private final Map<Integer, String> mapDanhMuc = new HashMap<>();
    private Integer maKhachHangDangChon;

    @FXML
    private TextField txtTimKiemSanPham;
    @FXML
    private ToggleButton btnLocTatCa;
    @FXML
    private ToggleButton btnLocCake;
    @FXML
    private ToggleButton btnLocCookie;
    @FXML
    private TilePane tileSanPham;

    @FXML
    private TableView<GioHangItemFX> tblGioHang;
    @FXML
    private TableColumn<GioHangItemFX, String> colTenSp;
    @FXML
    private TableColumn<GioHangItemFX, Number> colSoLuong;
    @FXML
    private TableColumn<GioHangItemFX, Number> colDonGia;
    @FXML
    private TableColumn<GioHangItemFX, Number> colThanhTien;
    @FXML
    private TableColumn<GioHangItemFX, Void> colXoa;

    @FXML
    private TextField txtSoDienThoai;
    @FXML
    private Label lblTenKhachHang;
    @FXML
    private ComboBox<String> cbHinhThucNhan;
    @FXML
    private TextField txtDiaChiGiao;
    @FXML
    private Label lblErrDiaChiGiao;
    @FXML
    private DatePicker dpNgayNhanBanh;
    @FXML
    private ComboBox<String> cbGioNhanBanh;
    @FXML
    private Label lblErrNgayNhanBanh;
    @FXML
    private TextField txtTienCoc;
    @FXML
    private TextField txtTienKhachDua;
    @FXML
    private Label lblTongTien;
    @FXML
    private Label lblTienThua;
    @FXML
    private TextField txtMaHoaDonThanhToan;
    @FXML
    private Label lblThongBaoTab1;

    @FXML
    private TextField txtMaDonTraCuu;
    @FXML
    private Label lblErrTraCuu;
    @FXML
    private TextField txtKhachHangReadonly;
    @FXML
    private TextField txtTrangThaiHienTaiReadonly;
    @FXML
    private TextField txtTongTienReadonly;
    @FXML
    private ComboBox<String> cbTrangThaiMoi;
    @FXML
    private Label lblErrTrangThaiMoi;
    @FXML
    private Label lblThongBaoTab2;

    @FXML
    private void initialize() {
        initComboBoxes();
        initCartTable();
        taiDuLieuBanDauTuDB();
        initProductCatalog();
        initListeners();
        txtSoDienThoai.setOnAction(event -> onTimThemKhachClick());
        capNhatTongTienVaTienThua();
    }

    @FXML
    private void onLocTatCaClick() {
        locTheoDanhMuc("ALL");
    }

    @FXML
    private void onLocCakeClick() {
        locTheoDanhMuc("Cake");
    }

    @FXML
    private void onLocCookieClick() {
        locTheoDanhMuc("Cookie");
    }

    @FXML
    private void onTimThemKhachClick() {
        String sdt = txtSoDienThoai.getText() == null ? "" : txtSoDienThoai.getText().trim();
        if (sdt.isEmpty()) {
            maKhachHangDangChon = null;
            lblTenKhachHang.setText("Khach vang lai");
            lblTenKhachHang.setStyle("-fx-text-fill: #9f630e; -fx-font-style: italic;");
            return;
        }

        try {
            KhachHangDTO khachHang = orderService.timKhachHangTheoSoDienThoai(sdt);
            if (khachHang != null) {
                maKhachHangDangChon = khachHang.getMaKH();
                lblTenKhachHang.setText(khachHang.getHoTen());
                lblTenKhachHang.setStyle("-fx-text-fill: #9f630e; -fx-font-style: italic;");
                return;
            }
        } catch (Exception e) {
            // Theo rule nghiep vu: khong chan thao tac tao don khi khong tim thay khach.
        }

        maKhachHangDangChon = null;
        if (!sdt.matches("^0\\d{9,10}$")) {
            lblTenKhachHang.setText("Khach vang lai (SDT chua hop le)");
        } else {
            lblTenKhachHang.setText("Khach vang lai");
        }
        lblTenKhachHang.setStyle("-fx-text-fill: #9f630e; -fx-font-style: italic;");
    }

    private void taiDuLieuBanDauTuDB() {
        tatCaSanPham.clear();
        mapDanhMuc.clear();
        mapTrangThaiMoi.clear();

        List<SanPhamDTO> dsSanPham = orderService.layDanhSachSanPhamPOS();
        for (SanPhamDTO sanPham : dsSanPham) {
            tatCaSanPham.add(new SanPhamFX(
                    sanPham.getMaSP(),
                    sanPham.getMaDM(),
                    sanPham.getTenSP(),
                    sanPham.getGiaCoBan(),
                    mapTenDanhMucChoLoc(sanPham.getMaDM())));
            mapDanhMuc.putIfAbsent(sanPham.getMaDM(), "DM " + sanPham.getMaDM());
        }

        mapDanhMuc.putAll(orderService.layMapDanhMucSanPham());
        for (SanPhamFX sanPhamFX : tatCaSanPham) {
            sanPhamFX.setDanhMuc(mapTenDanhMucChoLoc(sanPhamFX.getMaDanhMuc()));
        }

        try {
            List<TrangThaiDonDTO> dsTrangThai = orderService.layDanhSachTrangThaiDon();
            List<String> options = new ArrayList<>();
            for (TrangThaiDonDTO trangThai : dsTrangThai) {
                String ten = trangThai.getTenTrangThai();
                String normalized = normalizeForCompare(ten);
                if ("DA_COC".equals(normalized)
                        || "DANG_SAN_XUAT".equals(normalized)
                        || "CHO_GIAO".equals(normalized)
                        || "CHO_KHACH_LAY".equals(normalized)
                        || "HOAN_THANH".equals(normalized)
                        || "HUY".equals(normalized)
                        || "HOAN_HANG".equals(normalized)) {
                    mapTrangThaiMoi.put(ten, trangThai.getMaTrangThai());
                    options.add(ten);
                }
            }
            cbTrangThaiMoi.setItems(FXCollections.observableArrayList(options));
        } catch (Exception e) {
            lblThongBaoTab2.setStyle("-fx-text-fill: #c0392b;");
            lblThongBaoTab2.setText("Khong tai duoc danh sach trang thai: " + e.getMessage());
        }
    }

    private String mapTenDanhMucChoLoc(int maDanhMuc) {
        String tenDanhMuc = mapDanhMuc.get(maDanhMuc);
        if (tenDanhMuc == null || tenDanhMuc.trim().isEmpty()) {
            return "Khac";
        }
        String normalized = normalizeForCompare(tenDanhMuc);
        if (normalized.contains("CAKE")) {
            return "Cake";
        }
        if (normalized.contains("COOKIE")) {
            return "Cookie";
        }
        return tenDanhMuc;
    }

    private String normalizeForCompare(String raw) {
        if (raw == null) {
            return "";
        }
        return java.text.Normalizer.normalize(raw, java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toUpperCase(Locale.ROOT)
                .replace(' ', '_');
    }

    @FXML
    private void onHinhThucNhanChanged() {
        boolean laDatHang = "Đặt hàng".equals(cbHinhThucNhan.getValue());
        txtDiaChiGiao.setDisable(!laDatHang);
        if (!laDatHang) {
            txtDiaChiGiao.clear();
            lblErrDiaChiGiao.setText("");
        }
    }

    @FXML
    private void onTaoDonHangClick() {
        lblErrDiaChiGiao.setText("");
        lblErrNgayNhanBanh.setText("");
        lblThongBaoTab1.setText("");

        if (gioHangItems.isEmpty()) {
            lblThongBaoTab1.setStyle("-fx-text-fill: #c0392b;");
            lblThongBaoTab1.setText("Giỏ hàng đang trống.");
            return;
        }

        if (dpNgayNhanBanh.getValue() == null || cbGioNhanBanh.getValue() == null) {
            lblErrNgayNhanBanh.setText("Vui lòng chọn ngày và giờ nhận bánh.");
            return;
        }

        Integer hinhThucNhan = null;
        if ("Trực tiếp".equals(cbHinhThucNhan.getValue())) {
            hinhThucNhan = 1;
        } else if ("Đặt hàng".equals(cbHinhThucNhan.getValue())) {
            hinhThucNhan = 2;
            if (txtDiaChiGiao.getText() == null || txtDiaChiGiao.getText().trim().isEmpty()) {
                lblErrDiaChiGiao.setText("Đơn đặt hàng bắt buộc có địa chỉ giao.");
                return;
            }
        }

        try {
            YeuCauTaoDonHangDTO request = taoYeuCauDonHang(hinhThucNhan);
            int maDonMoi = orderService.taoDonHang(request);
            lblThongBaoTab1.setStyle("-fx-text-fill: #9f630e;");
            lblThongBaoTab1.setText("Tạo đơn thành công. Mã đơn: " + maDonMoi + ".");
            txtMaDonTraCuu.setText(String.valueOf(maDonMoi));
        } catch (Exception e) {
            lblThongBaoTab1.setStyle("-fx-text-fill: #c0392b;");
            lblThongBaoTab1.setText(e.getMessage());
        }
    }

    @FXML
    private void onThanhToanClick() {
        lblThongBaoTab1.setText("");
        try {
            int maHoaDon = parseSoNguyenDuong(txtMaHoaDonThanhToan.getText(), "Mã hóa đơn");
            double tongTien = tinhTongTien();
            double tienKhachDua = parseSoKhongAm(txtTienKhachDua.getText(), "Tiền khách đưa");
            orderService.thanhToanDon(maHoaDon, maKhachHangDangChon, tongTien, tienKhachDua);

            lblThongBaoTab1.setStyle("-fx-text-fill: #9f630e;");
            lblThongBaoTab1.setText("Thanh toán thành công.");
        } catch (Exception e) {
            lblThongBaoTab1.setStyle("-fx-text-fill: #c0392b;");
            lblThongBaoTab1.setText(e.getMessage());
        }
    }

    @FXML
    private void onTraCuuDonHangClick() {
        lblErrTraCuu.setText("");
        lblErrTrangThaiMoi.setText("");
        lblThongBaoTab2.setText("");

        int maDon;
        try {
            maDon = parseSoNguyenDuong(txtMaDonTraCuu.getText(), "Mã đơn");
        } catch (Exception e) {
            lblErrTraCuu.setText(e.getMessage());
            return;
        }

        try {
            DonDatHangDTO tomTat = orderService.layTomTatDonHang(maDon);
            String tenTrangThai = orderService.theoDoiDonHang(maDon);

            txtKhachHangReadonly.setText(tomTat.getMaKH() == null ? "Khách lẻ" : "Mã KH: " + tomTat.getMaKH());
            txtTrangThaiHienTaiReadonly.setText(tenTrangThai);
            txtTongTienReadonly.setText(formatTien(tomTat.getTongTienHDBan()));
            lblThongBaoTab2.setStyle("-fx-text-fill: #9f630e;");
            lblThongBaoTab2.setText("Tra cứu đơn hàng thành công.");
        } catch (Exception e) {
            lblThongBaoTab2.setStyle("-fx-text-fill: #c0392b;");
            lblThongBaoTab2.setText(e.getMessage());
        }
    }

    @FXML
    private void onCapNhatTrangThaiClick() {
        lblErrTrangThaiMoi.setText("");
        lblThongBaoTab2.setText("");

        int maDon;
        try {
            maDon = parseSoNguyenDuong(txtMaDonTraCuu.getText(), "Mã đơn");
        } catch (Exception e) {
            lblErrTraCuu.setText(e.getMessage());
            return;
        }

        String trangThaiMoi = cbTrangThaiMoi.getValue();
        if (trangThaiMoi == null || trangThaiMoi.trim().isEmpty()) {
            lblErrTrangThaiMoi.setText("Vui lòng chọn trạng thái mới.");
            return;
        }

        Integer maTrangThaiMoi = mapTrangThaiMoi.get(trangThaiMoi);
        if (maTrangThaiMoi == null) {
            lblErrTrangThaiMoi.setText("Không map được mã trạng thái mới.");
            return;
        }

        try {
            orderService.chuyenTrangThaiDon(
                    maDon,
                    maTrangThaiMoi,
                    MOCK_CURRENT_USER_ID,
                    null,
                    txtTrangThaiHienTaiReadonly.getText(),
                    trangThaiMoi);

            txtTrangThaiHienTaiReadonly.setText(trangThaiMoi);
            lblThongBaoTab2.setStyle("-fx-text-fill: #9f630e;");
            lblThongBaoTab2.setText("Cập nhật trạng thái thành công.");
        } catch (Exception e) {
            lblThongBaoTab2.setStyle("-fx-text-fill: #c0392b;");
            lblThongBaoTab2.setText(e.getMessage());
        }
    }

    private void initComboBoxes() {
        cbHinhThucNhan.setItems(FXCollections.observableArrayList("Trực tiếp", "Đặt hàng"));
        cbHinhThucNhan.setValue("Trực tiếp");
        txtDiaChiGiao.setDisable(true);

        cbGioNhanBanh.setItems(FXCollections.observableArrayList(
                "08:00", "09:00", "10:00", "11:00", "12:00", "13:00",
                "14:00", "15:00", "16:00", "17:00", "18:00", "19:00", "20:00"));
        cbGioNhanBanh.setValue("10:00");
        dpNgayNhanBanh.setValue(LocalDate.now().plusDays(1));
    }

    private void initCartTable() {
        tblGioHang.setItems(gioHangItems);

        colTenSp.setCellValueFactory(cellData -> cellData.getValue().tenSanPhamProperty());
        colDonGia.setCellValueFactory(cellData -> cellData.getValue().donGiaProperty());
        colThanhTien.setCellValueFactory(cellData -> cellData.getValue().thanhTienProperty());
        colSoLuong.setCellValueFactory(cellData -> cellData.getValue().soLuongProperty());

        colDonGia.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Number item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : formatTien(item.doubleValue()));
            }
        });

        colThanhTien.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Number item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : formatTien(item.doubleValue()));
            }
        });

        colSoLuong.setCellFactory(col -> new TableCell<>() {
            private final Button btnMinus = new Button("-");
            private final Button btnPlus = new Button("+");
            private final Label lblQty = new Label();
            private final HBox box = new HBox(6, btnMinus, lblQty, btnPlus);

            {
                box.setAlignment(Pos.CENTER);
                btnMinus.setStyle("-fx-background-color: #f4efe9; -fx-border-color: #9f630e; -fx-text-fill: #9f630e;");
                btnPlus.setStyle("-fx-background-color: #f4efe9; -fx-border-color: #9f630e; -fx-text-fill: #9f630e;");

                btnMinus.setOnAction(evt -> {
                    GioHangItemFX item = getTableView().getItems().get(getIndex());
                    if (item.getSoLuong() > 1) {
                        item.setSoLuong(item.getSoLuong() - 1);
                        capNhatTongTienVaTienThua();
                        tblGioHang.refresh();
                    }
                });

                btnPlus.setOnAction(evt -> {
                    GioHangItemFX item = getTableView().getItems().get(getIndex());
                    item.setSoLuong(item.getSoLuong() + 1);
                    capNhatTongTienVaTienThua();
                    tblGioHang.refresh();
                });
            }

            @Override
            protected void updateItem(Number item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getIndex() < 0 || getIndex() >= getTableView().getItems().size()) {
                    setGraphic(null);
                } else {
                    GioHangItemFX row = getTableView().getItems().get(getIndex());
                    lblQty.setText(String.valueOf(row.getSoLuong()));
                    setGraphic(box);
                }
            }
        });

        colXoa.setCellFactory(col -> new TableCell<>() {
            private final Button btnDelete = new Button("X");

            {
                btnDelete.setStyle("-fx-background-color: transparent; -fx-border-color: #9f630e; -fx-text-fill: #9f630e;");
                btnDelete.setOnAction(evt -> {
                    GioHangItemFX item = getTableView().getItems().get(getIndex());
                    gioHangItems.remove(item);
                    capNhatTongTienVaTienThua();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btnDelete);
            }
        });
    }

    private void initProductCatalog() {
        renderProductCards();
    }

    private void initListeners() {
        txtTimKiemSanPham.textProperty().addListener((obs, oldVal, newVal) -> apDungBoLoc());

        gioHangItems.addListener((ListChangeListener<GioHangItemFX>) c -> capNhatTongTienVaTienThua());
        txtTienCoc.textProperty().addListener((obs, oldVal, newVal) -> capNhatTongTienVaTienThua());
        txtTienKhachDua.textProperty().addListener((obs, oldVal, newVal) -> capNhatTongTienVaTienThua());
    }

    private void apDungBoLoc() {
        String textSearch = txtTimKiemSanPham.getText() == null ? "" : txtTimKiemSanPham.getText().trim().toLowerCase(Locale.ROOT);
        String category = btnLocCake.isSelected() ? "Cake" : (btnLocCookie.isSelected() ? "Cookie" : "ALL");

        sanPhamLoc.setPredicate(item -> {
            boolean matchCategory = "ALL".equals(category) || category.equalsIgnoreCase(item.getDanhMuc());
            boolean matchName = item.getTenSanPham().toLowerCase(Locale.ROOT).contains(textSearch);
            return matchCategory && matchName;
        });
        renderProductCards();
    }

    private void locTheoDanhMuc(String danhMuc) {
        if ("Cake".equals(danhMuc)) {
            btnLocCake.setSelected(true);
            btnLocCookie.setSelected(false);
            btnLocTatCa.setSelected(false);
        } else if ("Cookie".equals(danhMuc)) {
            btnLocCake.setSelected(false);
            btnLocCookie.setSelected(true);
            btnLocTatCa.setSelected(false);
        } else {
            btnLocCake.setSelected(false);
            btnLocCookie.setSelected(false);
            btnLocTatCa.setSelected(true);
        }

        apDungBoLoc();
    }

    private void renderProductCards() {
        tileSanPham.getChildren().clear();
        for (SanPhamFX sp : sanPhamLoc) {
            tileSanPham.getChildren().add(createProductCard(sp));
        }
    }

    private Node createProductCard(SanPhamFX sanPham) {
        VBox card = new VBox(8);
        card.setPrefWidth(170);
        card.setMaxWidth(170);
        card.setPadding(new Insets(10));
        card.setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: #9f630e;" +
                        "-fx-border-width: 1.2;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;");

        StackPane imageHolder = new StackPane();
        imageHolder.setMinHeight(85);
        imageHolder.setStyle(
                "-fx-background-color: #f4efe9;" +
                        "-fx-border-color: #9f630e;" +
                        "-fx-border-radius: 6;" +
                        "-fx-background-radius: 6;");
        Label imageLabel = new Label("IMG");
        imageLabel.setStyle("-fx-text-fill: #9f630e; -fx-font-weight: bold;");
        imageHolder.getChildren().add(imageLabel);

        Label lblTen = new Label(sanPham.getTenSanPham());
        lblTen.setWrapText(true);
        lblTen.setStyle("-fx-text-fill: #4a3007; -fx-font-weight: bold;");

        Label lblGia = new Label(formatTien(sanPham.getGiaBan()));
        lblGia.setStyle("-fx-text-fill: #9f630e;");

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Button btnThem = new Button("Thêm vào giỏ");
        btnThem.setMaxWidth(Double.MAX_VALUE);
        btnThem.setStyle("-fx-background-color: #9f630e; -fx-text-fill: white;");
        btnThem.setOnAction(evt -> themSanPhamVaoGio(sanPham));

        card.getChildren().addAll(imageHolder, lblTen, lblGia, spacer, btnThem);
        return card;
    }

    private void themSanPhamVaoGio(SanPhamFX sanPham) {
        GioHangItemFX existed = gioHangItems.stream()
                .filter(item -> item.getMaSanPham() == sanPham.getMaSanPham())
                .findFirst()
                .orElse(null);

        if (existed != null) {
            existed.setSoLuong(existed.getSoLuong() + 1);
        } else {
            gioHangItems.add(new GioHangItemFX(sanPham.getMaSanPham(), sanPham.getTenSanPham(), sanPham.getGiaBan(), 1));
        }
        capNhatTongTienVaTienThua();
        tblGioHang.refresh();
    }

    private void capNhatTongTienVaTienThua() {
        double tongTien = tinhTongTien();
        lblTongTien.setText(formatTien(tongTien));

        double tienKhachDua = parseDoubleSafe(txtTienKhachDua.getText());
        double tienThua = tienKhachDua - tongTien;
        lblTienThua.setText(formatTien(tienThua));
        lblTienThua.setStyle(tienThua < 0
                ? "-fx-text-fill: #c0392b; -fx-font-weight: bold;"
                : "-fx-text-fill: #9f630e; -fx-font-weight: bold;");
    }

    private double tinhTongTien() {
        return gioHangItems.stream().mapToDouble(GioHangItemFX::getThanhTien).sum();
    }

    private YeuCauTaoDonHangDTO taoYeuCauDonHang(Integer hinhThucNhan) throws Exception {
        YeuCauTaoDonHangDTO request = new YeuCauTaoDonHangDTO();

        String gio = cbGioNhanBanh.getValue();
        if (gio == null || dpNgayNhanBanh.getValue() == null) {
            throw new IllegalArgumentException("Thiếu ngày giờ nhận bánh.");
        }
        LocalDateTime ngayNhan = LocalDateTime.of(dpNgayNhanBanh.getValue(), LocalTime.parse(gio, TIME_FORMATTER));

        request.setNgayGioNhanBanh(ngayNhan);
        request.setMaKH(maKhachHangDangChon);
        request.setMaNVLap(MOCK_CURRENT_USER_ID); // TODO: Session.getCurrentUser().getId()
        request.setMaTrangThai(0); // De Service resolve theo status MOI_DAT/CHO_XU_LY tu DB.
        request.setTienDaCoc(parseSoKhongAm(txtTienCoc.getText(), "Tiền cọc"));
        request.setHinhThucNhan(hinhThucNhan);
        request.setDiaChiGiao("Đặt hàng".equals(cbHinhThucNhan.getValue())
                ? txtDiaChiGiao.getText() == null ? null : txtDiaChiGiao.getText().trim()
                : null);
        request.setItems(gioHangItems.stream()
                .map(this::mapToChiTietDTO)
                .collect(Collectors.toList()));
        return request;
    }

    private YeuCauChiTietDonHangDTO mapToChiTietDTO(GioHangItemFX item) {
        YeuCauChiTietDonHangDTO dto = new YeuCauChiTietDonHangDTO();
        dto.setMaSP(item.getMaSanPham());
        dto.setSoLuong(item.getSoLuong());
        dto.setDonGia(item.getDonGia());
        dto.setCustom(false);
        dto.setGhiChu("");
        dto.setPhuKien("");
        return dto;
    }

    private int parseSoNguyenDuong(String raw, String tenField) throws Exception {
        try {
            int val = Integer.parseInt(raw == null ? "" : raw.trim());
            if (val <= 0) {
                throw new NumberFormatException("<=0");
            }
            return val;
        } catch (NumberFormatException ex) {
            throw new Exception(tenField + " phải là số nguyên > 0.");
        }
    }

    private double parseSoKhongAm(String raw, String tenField) throws Exception {
        if (raw == null || raw.trim().isEmpty()) {
            return 0;
        }
        try {
            double val = Double.parseDouble(raw.trim());
            if (val < 0) {
                throw new NumberFormatException("<0");
            }
            return val;
        } catch (NumberFormatException ex) {
            throw new Exception(tenField + " phải là số >= 0.");
        }
    }

    private double parseDoubleSafe(String raw) {
        if (raw == null || raw.trim().isEmpty()) {
            return 0;
        }
        try {
            return Double.parseDouble(raw.trim());
        } catch (NumberFormatException ex) {
            return 0;
        }
    }

    private String formatTien(double value) {
        String formatted = String.format(Locale.US, "%,.0f", value);
        return formatted + " đ";
    }

    public static class SanPhamFX {
        private final int maSanPham;
        private final int maDanhMuc;
        private final String tenSanPham;
        private final double giaBan;
        private String danhMuc;

        public SanPhamFX(int maSanPham, int maDanhMuc, String tenSanPham, double giaBan, String danhMuc) {
            this.maSanPham = maSanPham;
            this.maDanhMuc = maDanhMuc;
            this.tenSanPham = tenSanPham;
            this.giaBan = giaBan;
            this.danhMuc = danhMuc;
        }

        public int getMaSanPham() {
            return maSanPham;
        }

        public int getMaDanhMuc() {
            return maDanhMuc;
        }

        public String getTenSanPham() {
            return tenSanPham;
        }

        public double getGiaBan() {
            return giaBan;
        }

        public String getDanhMuc() {
            return danhMuc;
        }

        public void setDanhMuc(String danhMuc) {
            this.danhMuc = danhMuc;
        }
    }

    public static class GioHangItemFX {
        private final int maSanPham;
        private final StringProperty tenSanPham = new SimpleStringProperty();
        private final DoubleProperty donGia = new SimpleDoubleProperty();
        private final IntegerProperty soLuong = new SimpleIntegerProperty();
        private final DoubleProperty thanhTien = new SimpleDoubleProperty();

        public GioHangItemFX(int maSanPham, String tenSanPham, double donGia, int soLuong) {
            this.maSanPham = maSanPham;
            this.tenSanPham.set(tenSanPham);
            this.donGia.set(donGia);
            this.soLuong.set(soLuong);
            this.thanhTien.bind(this.donGia.multiply(this.soLuong));
        }

        public int getMaSanPham() {
            return maSanPham;
        }

        public String getTenSanPham() {
            return tenSanPham.get();
        }

        public StringProperty tenSanPhamProperty() {
            return tenSanPham;
        }

        public double getDonGia() {
            return donGia.get();
        }

        public DoubleProperty donGiaProperty() {
            return donGia;
        }

        public int getSoLuong() {
            return soLuong.get();
        }

        public IntegerProperty soLuongProperty() {
            return soLuong;
        }

        public void setSoLuong(int value) {
            soLuong.set(value);
        }

        public double getThanhTien() {
            return thanhTien.get();
        }

        public DoubleProperty thanhTienProperty() {
            return thanhTien;
        }
    }
}
