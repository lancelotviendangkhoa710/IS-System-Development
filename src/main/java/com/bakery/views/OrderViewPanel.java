package com.bakery.views;

import com.bakery.model.dto.CTDonHangDTO;
import com.bakery.model.dto.DonDatHangDTO;
import com.bakery.model.dto.SanPhamDTO;
import com.bakery.model.dto.KichCoBanhDTO;
import com.bakery.model.dto.CotBanhDTO;
import com.bakery.model.dto.NhanBanhDTO;
import com.bakery.model.dto.KieuTrangTriDTO;
import com.bakery.presenters.OrderPresenter;
import com.bakery.views.interfaces.IOrderView;
import com.formdev.flatlaf.FlatClientProperties;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Locale;

public class OrderViewPanel extends JPanel implements IOrderView {
    private static final ZoneId ZONE_VN = ZoneId.of("Asia/Ho_Chi_Minh");
    // BẢNG MÀU MODERN CREAMY BAKERY (ĐÃ CẬP NHẬT)
    private final Color COLOR_PRIMARY = new Color(217, 119, 6); // #D97706 - Honey Amber (Màu mật ong/bánh nướng)
    private final Color COLOR_PRIMARY_DARK = new Color(180, 83, 9); // #B45309 - Burnt Orange (Màu cam đậm)
    private final Color COLOR_PRIMARY_LIGHT = new Color(255, 247, 237); // #FFF7ED - Soft Cream (Màu kem nhẹ)
    private final Color COLOR_SIDEBAR = new Color(249, 250, 251); // #F9FAFB - Pearl White (Trắng ngọc trai)
    private final Color COLOR_SURFACE = new Color(250, 250, 249); // #FAFAF9 - Warm Stone (Màu đá ấm)
    private final Color COLOR_CARD = Color.WHITE;
    private final Color COLOR_TEXT = new Color(31, 41, 55); // #1F2937 - Slate Gray (Xám đá, dễ đọc hơn đen)
    private final Color COLOR_HEADING = new Color(69, 26, 3); // #451A03 - Deep Coffee (Nâu cà phê đậm)
    private final Color COLOR_SUB_TEXT = new Color(107, 114, 128); // #6B7280 - Muted Gray (Xám dịu)
    private final Color COLOR_DISABLED = new Color(243, 244, 246); // #F3F4F6 (Xám nhạt cho trạng thái tắt)
    private final Color COLOR_ACCENT = new Color(159, 18, 57); // #9F1239 - Berry Red (Đỏ dâu cho điểm nhấn)
    private final Color SUCCESS = new Color(5, 150, 105); // #059669 - Emerald (Xanh ngọc lục bảo)
    private final Color ERROR = new Color(225, 29, 72); // #E11D48 - Rose Red (Đỏ hoa hồng)

    private OrderPresenter presenter;

    private List<SanPhamDTO> localDanhSachSP = new ArrayList<>();
    private final List<CTDonHangDTO> localGioHangHienTai = new ArrayList<>();
    private final List<DonDatHangDTO> localDonTheoDoi = new ArrayList<>();
    private Map<Integer, String> localMapDanhMuc;

    private JTextField txtTimKiemSanPham, txtSoDienThoai, txtDiaChiGiao, txtTienCoc, txtTienKhachDua;
    private JButton btnLocTatCa, btnLocCake, btnLocCookie, btnLocBread, btnLocTuyChinh;
    private String danhMucHienTai = "ALL";
    private JPanel tileSanPham;
    private JPanel panelChuaSanPham;
    private CardLayout layoutTheSanPham;

    // Custom cake components
    private JComboBox<SanPhamDTO> cbCustomSp;
    private JComboBox<KichCoBanhDTO> cbCustomKichCo;
    private JComboBox<CotBanhDTO> cbCustomCotBanh;
    private JComboBox<NhanBanhDTO> cbCustomNhanBanh;
    private JComboBox<KieuTrangTriDTO> cbCustomTrangTri;
    private JTextArea txtCustomLoiChuc;
    private JTextArea txtCustomGhiChu;
    private JLabel lblGiaTuyChinh;
    private JButton btnThemTuyChinhVaoGio;

    private JTable tblGioHang;
    private DefaultTableModel modelBangGioHang;
    private JLabel lblTenKhachHang, lblThongBaoTab1, lblErrDiaChiGiao, lblErrNgayNhanBanh;
    private JLabel lblTongTienHang, lblTienGiamGia, lblTongThanhToan, lblCocToiThieu, lblConLai, lblTienThua;
    private JComboBox<String> cbHinhThucNhan, cbGioNhanBanh;
    private SwingDatePicker dpNgayNhanBanh;
    private JCheckBox chkXacNhanThuTien;
    private JButton btnTaoDonHang, btnThanhToan, btnTimKhach;

    private JTextField txtTimMaDon;
    private JLabel lblThongBaoTab2;
    private JButton btnTimTheoNgayGio;
    private SwingDatePicker dpNgayTheoDoi;
    private JComboBox<String> cbGioTu, cbGioDen;
    private JPanel panelChuaDon;
    private List<String> listTrangThai = new ArrayList<>();

    private JPanel panelThe;
    private CardLayout layoutThe;
    private JPanel navItemTaoDon, navItemTheoDoi;

    public OrderViewPanel() {
        setLayout(new BorderLayout());
        setBackground(COLOR_SURFACE);
        xayDungGiaoDien();
    }

    public void setPresenter(OrderPresenter presenter) {
        this.presenter = presenter;
        ganSuKien();
    }

    private void ganSuKien() {
        txtTimKiemSanPham.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                apDungBoLoc();
            }
        });
        btnTimKhach.addActionListener(e -> {
            if (presenter != null)
                presenter.timKhachHang(txtSoDienThoai.getText());
        });
        cbHinhThucNhan.addActionListener(e -> {
            boolean isDatHang = "Đặt hàng".equals(cbHinhThucNhan.getSelectedItem());
            txtDiaChiGiao.setEnabled(isDatHang);
            if (!isDatHang) {
                txtDiaChiGiao.setText("");
                lblErrDiaChiGiao.setText("");
            }
        });

        DocumentListener calcListener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                if (presenter != null)
                    presenter.capNhatGioHangVaTien();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                if (presenter != null)
                    presenter.capNhatGioHangVaTien();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                if (presenter != null)
                    presenter.capNhatGioHangVaTien();
            }
        };
        txtTienCoc.getDocument().addDocumentListener(calcListener);
        txtTienKhachDua.getDocument().addDocumentListener(calcListener);
        chkXacNhanThuTien.addActionListener(e -> {
            if (presenter != null)
                presenter.capNhatGioHangVaTien();
        });

        btnTaoDonHang.addActionListener(e -> {
            if (presenter != null)
                presenter.xuLyDatBanh();
        });
        btnThanhToan.addActionListener(e -> {
            if (presenter == null) {
                return;
            }
            if (!presenter.kiemTraNgayNhanHopLeChoThanhToan()) {
                return;
            }
            moDialogXacNhanThanhToan();
        });

        btnTimTheoNgayGio.addActionListener(e -> {
            if (presenter == null)
                return;
            String maSearch = txtTimMaDon.getText().trim();
            LocalDate ngay = dpNgayTheoDoi.getValue();
            LocalTime tu = cbGioTu.getSelectedIndex() == 0 ? null
                    : LocalTime.parse(cbGioTu.getSelectedItem().toString());
            LocalTime den = cbGioDen.getSelectedIndex() == 0 ? null
                    : LocalTime.parse(cbGioDen.getSelectedItem().toString());
            presenter.timKiemDonTheoDoi(maSearch, ngay, tu, den);
        });
    }

    public double getTienKhachDua() {
        return chuyenSoAnToan(txtTienKhachDua.getText());
    }

    public double getTienCoc() {
        return chuyenSoAnToan(txtTienCoc.getText());
    }

    public double getTongThanhToanHienTai() {
        return chuyenSoAnToan(lblTongThanhToan.getText().replace(" đ", "").replace(",", ""));
    }

    public String getDiaChiGiao() {
        return txtDiaChiGiao.getText().trim();
    }

    public String getSoDienThoai() {
        return txtSoDienThoai.getText().trim();
    }

    public Integer getHinhThucNhan() {
        return "Trực tiếp".equals(cbHinhThucNhan.getSelectedItem()) ? 1 : 2;
    }

    public boolean isXacNhanThuTien() {
        return chkXacNhanThuTien.isSelected();
    }

    public String getTrangThaiHienTaiTraCuu() {
        return "";
    }

    public LocalDateTime getNgayGioNhanBanh() {
        LocalDate date = dpNgayNhanBanh.getValue();
        LocalTime time = LocalTime.parse(cbGioNhanBanh.getSelectedItem().toString());
        return LocalDateTime.of(date, time);
    }

    public void hienThiThongTinKhach(String text, boolean isVip) {
        lblTenKhachHang.setText(text);
        lblTenKhachHang.setForeground(isVip ? SUCCESS : COLOR_PRIMARY);
    }

    public void lamMoiBaoCaoTien(double tongHang, double giamGia, double tongThanhToan, double minCoc, double conLai,
            double tienThua, boolean isThieuTienThua) {
        lblTongTienHang.setText(dinhDangTien(tongHang));
        lblTienGiamGia.setText(dinhDangTien(giamGia));
        lblTongThanhToan.setText(dinhDangTien(tongThanhToan));
        lblCocToiThieu.setText(dinhDangTien(minCoc));
        lblConLai.setText(dinhDangTien(conLai));

        if (txtTienKhachDua.getText().trim().isEmpty()) {
            lblTienThua.setText("0 đ");
            lblTienThua.setForeground(Color.BLACK);
        } else if (isThieuTienThua) {
            lblTienThua.setText("Thiếu " + dinhDangTien(Math.abs(tienThua)));
            lblTienThua.setForeground(ERROR);
        } else {
            lblTienThua.setText(dinhDangTien(tienThua));
            lblTienThua.setForeground(COLOR_PRIMARY);
        }
    }

    public void lamMoiBangGioHang(List<CTDonHangDTO> items, List<SanPhamDTO> originData) {
        modelBangGioHang.setRowCount(0);
        localGioHangHienTai.clear();
        for (CTDonHangDTO item : items) {
            String tenSp = originData.stream().filter(sp -> sp.getMaSP() == item.getMaSP()).map(SanPhamDTO::getTenSP)
                    .findFirst().orElse("SP");
            modelBangGioHang.addRow(new Object[] { tenSp, item.getSoLuong(), dinhDangTien(item.getDonGia()),
                    dinhDangTien(item.getDonGia() * item.getSoLuong()) });

            CTDonHangDTO clone = new CTDonHangDTO();
            clone.setMaSP(item.getMaSP());
            clone.setSoLuong(item.getSoLuong());
            clone.setDonGia(item.getDonGia());
            localGioHangHienTai.add(clone);
        }
    }

    public void batTatNutThanhToan(boolean state) {
        btnTaoDonHang.setEnabled(state);
        btnThanhToan.setEnabled(state);
    }

    public void hienThiLoi(String msg) {
        lblThongBaoTab1.setForeground(ERROR);
        lblThongBaoTab1.setText(msg);
    }

    public void hienThiThanhCong(String msg) {
        lblThongBaoTab1.setForeground(SUCCESS);
        lblThongBaoTab1.setText(msg);
    }

    public void hienThiLoiTraCuu(String msg) {
        lblThongBaoTab2.setForeground(ERROR);
        lblThongBaoTab2.setText(msg);
    }

    public void hienThiThongBaoTraCuu(String msg) {
        lblThongBaoTab2.setForeground(SUCCESS);
        lblThongBaoTab2.setText(msg);
    }

    public void hienThiKetQuaTraCuu(String kh, String tt, double tongTien) {
        // Not used
    }

    @Override
    public void showOrderDetails(DonDatHangDTO order) {
        // Not used
    }

    @Override
    public void showError(String msg) {
        hienThiLoi(msg);
    }

    public void lamMoiForm() {
        txtTienCoc.setText("");
        txtTienKhachDua.setText("");
        txtSoDienThoai.setText("");
        txtDiaChiGiao.setText("");
        chkXacNhanThuTien.setSelected(false);
        lblThongBaoTab1.setText("");
        lblErrDiaChiGiao.setText("");
        dpNgayNhanBanh.setSelectedDate(LocalDate.now(ZONE_VN));
        datGioNhanMacDinhTheoHienTai();
    }

    public void hienThiDanhSachSanPham(List<SanPhamDTO> ds, Map<Integer, String> dict) {
        this.localDanhSachSP = ds;
        this.localMapDanhMuc = dict;
        apDungBoLoc();
    }

    public void taiDanhSachTrangThai(List<String> list) {
        this.listTrangThai = list;
    }

    public void hienThiDuLieuTuyChinh(List<SanPhamDTO> spTuyChinh, List<KichCoBanhDTO> kichCo, List<CotBanhDTO> cotBanh,
            List<NhanBanhDTO> nhanBanh, List<KieuTrangTriDTO> trangTri) {
        cbCustomSp.removeAllItems();
        cbCustomKichCo.removeAllItems();
        cbCustomCotBanh.removeAllItems();
        cbCustomNhanBanh.removeAllItems();
        cbCustomTrangTri.removeAllItems();

        cbCustomKichCo.addItem(null);
        cbCustomCotBanh.addItem(null);
        cbCustomNhanBanh.addItem(null);
        cbCustomTrangTri.addItem(null);

        spTuyChinh.forEach(cbCustomSp::addItem);
        kichCo.forEach(cbCustomKichCo::addItem);
        cotBanh.forEach(cbCustomCotBanh::addItem);
        nhanBanh.forEach(cbCustomNhanBanh::addItem);
        trangTri.forEach(cbCustomTrangTri::addItem);

        // Setup Renderers
        cbCustomSp.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
                    boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof SanPhamDTO)
                    setText(((SanPhamDTO) value).getTenSP() + " (" + dinhDangTien(((SanPhamDTO) value).getGiaCoBan())
                            + ")");
                return this;
            }
        });
        cbCustomKichCo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
                    boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value == null)
                    setText("--- Không chọn ---");
                else if (value instanceof KichCoBanhDTO)
                    setText(((KichCoBanhDTO) value).getTenKC() + " (+" + dinhDangTien(((KichCoBanhDTO) value).getPhuPhi())
                            + ")");
                return this;
            }
        });
        cbCustomCotBanh.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
                    boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value == null)
                    setText("--- Không chọn ---");
                else if (value instanceof CotBanhDTO)
                    setText(((CotBanhDTO) value).getTenCot() + " (+" + dinhDangTien(((CotBanhDTO) value).getPhuPhi())
                            + ")");
                return this;
            }
        });
        cbCustomNhanBanh.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
                    boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value == null)
                    setText("--- Không chọn ---");
                else if (value instanceof NhanBanhDTO)
                    setText(((NhanBanhDTO) value).getTenNhan() + " (+" + dinhDangTien(((NhanBanhDTO) value).getPhuPhi())
                            + ")");
                return this;
            }
        });
        cbCustomTrangTri.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
                    boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value == null)
                    setText("--- Không chọn ---");
                else if (value instanceof KieuTrangTriDTO)
                    setText(((KieuTrangTriDTO) value).getTenTrangTri() + " (+"
                            + dinhDangTien(((KieuTrangTriDTO) value).getPhuPhi()) + ")");
                return this;
            }
        });
    }

    @Override
    public void hienThiDanhSachDonTheoDoi(List<DonDatHangDTO> dsDonTheoDoi) {
        if (panelChuaDon == null)
            return;
        panelChuaDon.removeAll();
        localDonTheoDoi.clear();
        for (DonDatHangDTO don : dsDonTheoDoi) {
            localDonTheoDoi.add(don);
            panelChuaDon.add(taoCardDonHang(don));
            panelChuaDon.add(Box.createVerticalStrut(10));
        }
        panelChuaDon.revalidate();
        panelChuaDon.repaint();
    }

    public void inPhieuHoaDon(String tieuDe, Integer maDon, Integer maHoaDon, LocalDateTime ngayLapHoaDon,
            double tongTien, double daThu, List<CTDonHangDTO> cart, List<SanPhamDTO> data, double pGiam) {
        String maDonStr = (maDon != null && maDon > 0) ? "#" + maDon : "N/A";
        String maHoaDonStr = (maHoaDon != null && maHoaDon > 0) ? "#" + maHoaDon : "N/A";
        String ngayLapHoaDonStr = ngayLapHoaDon == null ? "N/A"
                : ngayLapHoaDon.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        String khachHang = lblTenKhachHang.getText();
        String tienGiamGia = (pGiam > 0) ? lblTienGiamGia.getText() : null;
        String tienKhachDua = txtTienKhachDua.getText().trim();
        if (tienKhachDua.isEmpty())
            tienKhachDua = dinhDangTien(daThu);
        else
            tienKhachDua = dinhDangTien(chuyenSoAnToan(tienKhachDua));

        double tienThuaNum = chuyenSoAnToan(txtTienKhachDua.getText()) - daThu;
        String tienThua = (tienThuaNum > 0) ? dinhDangTien(tienThuaNum) : null;

        Receipt receipt = new Receipt(
                tieuDe, maDonStr, maHoaDonStr, ngayLapHoaDonStr, khachHang, cart, data,
                tienGiamGia, dinhDangTien(tongTien), dinhDangTien(daThu),
                tienKhachDua, tienThua);
        receipt.setVisible(true);
    }

    private void moDialogXacNhanThanhToan() {
        if (presenter == null)
            return;

        String orderId = taoMaThamChieuDonHang();
        double amount = getTongThanhToanHienTai();
        ImageIcon qrIcon = null;
        try {
            qrIcon = taoMaVietQR(amount, orderId);
            if (qrIcon != null) {
                // Resize QR to be smaller (200x200)
                qrIcon = new ImageIcon(qrIcon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH));
            }
        } catch (Exception ignored) {
        }

        PaymentConfirmationDialog dialog = new PaymentConfirmationDialog(
                SwingUtilities.getWindowAncestor(this),
                presenter,
                taoPanelHoaDonXacNhanV2(qrIcon),
                amount,
                orderId);
        dialog.setVisible(true);

        if (dialog.isSuccess()) {
            presenter.xuLySauKhiLuuDonThanhCong();
        }
    }

    private String taoMaThamChieuDonHang() {
        return "POS" + LocalDateTime.now(ZONE_VN).format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
    }

    private JPanel taoPanelHoaDonXacNhanV2(ImageIcon qrIcon) {
        String tieuDe = "XÁC NHẬN GIAO DỊCH";
        String maDonStr = "#" + taoMaThamChieuDonHang();
        String khachHang = lblTenKhachHang.getText();
        String tienGiamGia = lblTienGiamGia.getText();
        String tongTien = lblTongThanhToan.getText();
        String daThu = lblTongThanhToan.getText();
        String tienKhachDua = dinhDangTien(getTienKhachDua());
        String tienThua = lblTienThua.getText();

        return Receipt.taoPanelHoaDon(
                tieuDe,
                maDonStr,
                "N/A",
                LocalDateTime.now(ZONE_VN).format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                khachHang,
                localGioHangHienTai,
                localDanhSachSP,
                tienGiamGia,
                tongTien,
                daThu,
                tienKhachDua,
                tienThua,
                qrIcon);
    }

    private void apDungBoLoc() {
        if (tileSanPham == null || txtTimKiemSanPham == null)
            return;
        String textSearch = txtTimKiemSanPham.getText().trim().toLowerCase();
        tileSanPham.removeAll();
        for (SanPhamDTO item : localDanhSachSP) {
            String categoryName = (localMapDanhMuc != null) ? localMapDanhMuc.getOrDefault(item.getMaDM(), "Khac")
                    : "Khac";
            String normCat = java.text.Normalizer.normalize(categoryName, java.text.Normalizer.Form.NFD)
                    .replaceAll("\\p{M}", "").toUpperCase(Locale.ROOT);

            String finalCat = normCat.contains("CAKE") ? "Cake"
                    : (normCat.contains("COOKIE") ? "Cookie"
                            : (normCat.contains("BREAD") || normCat.contains("MI") ? "Bread" : "ALL"));

            boolean matchCategory = "ALL".equals(danhMucHienTai) || danhMucHienTai.equalsIgnoreCase(finalCat);
            boolean matchName = item.getTenSP().toLowerCase().contains(textSearch);

            if (matchCategory && matchName) {
                tileSanPham.add(taoCardSanPham(item));
            }
        }
        tileSanPham.revalidate();
        tileSanPham.repaint();
    }

    private void datBoLocHoatDong(String cat, JButton btn) {
        danhMucHienTai = cat;
        JButton[] btns = { btnLocTatCa, btnLocCake, btnLocCookie, btnLocBread, btnLocTuyChinh };
        for (JButton b : btns) {
            b.setBackground(COLOR_CARD);
            b.setForeground(COLOR_TEXT);
        }
        btn.setBackground(COLOR_PRIMARY);
        btn.setForeground(Color.WHITE);

        if ("CUSTOM".equals(cat)) {
            layoutTheSanPham.show(panelChuaSanPham, "CUSTOM_CAKE");
        } else {
            layoutTheSanPham.show(panelChuaSanPham, "PRODUCTS");
            apDungBoLoc();
        }
    }

    private java.awt.Image thuTaiTuTaiNguyen(String path) {
        if (path == null)
            return null;
        String resPath = path.startsWith("/") ? path : "/" + path;
        try (java.io.InputStream is = getClass().getResourceAsStream(resPath)) {
            if (is != null) {
                java.awt.Image image = javax.imageio.ImageIO.read(is);
                if (image != null) {
                    System.out.println("  -> Successfully loaded from: " + resPath);
                    return image;
                }
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    private JPanel taoCardSanPham(SanPhamDTO sp) {
        JPanel card = new JPanel(new BorderLayout());
        card.setPreferredSize(new Dimension(160, 200));
        card.setMaximumSize(new Dimension(160, 200));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setBackground(COLOR_CARD);
        card.putClientProperty(FlatClientProperties.STYLE, "arc: 12");
        card.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Phần hiển thị hình ảnh
        JLabel imgLabel = new JLabel("", SwingConstants.CENTER);
        imgLabel.setPreferredSize(new Dimension(140, 100));
        imgLabel.setOpaque(true);
        imgLabel.setBackground(COLOR_SURFACE);
        imgLabel.setForeground(COLOR_PRIMARY);

        java.awt.Image img = null;
        String hinhAnhPath = sp.getHinhAnh();

        if (hinhAnhPath != null && !hinhAnhPath.isEmpty()) {
            System.out.println("[DEBUG] Loading image for " + sp.getTenSP() + " (DB Path: " + hinhAnhPath + ")");
            try {
                String fileName = new java.io.File(hinhAnhPath).getName();

                // 1. Thử load từ Resources qua Stream (An toàn nhất cho Java Module)
                img = thuTaiTuTaiNguyen(hinhAnhPath);
                if (img == null)
                    img = thuTaiTuTaiNguyen("/" + fileName);

                // 2. ƯU TIÊN MAPPING THEO MÃ SẢN PHẨM (MaSP)
                if (img == null) {
                    int maSP = sp.getMaSP();
                    if (maSP == 1)
                        img = thuTaiTuTaiNguyen("/images/products/cake_vani_16.png");
                    else if (maSP == 2)
                        img = thuTaiTuTaiNguyen("/images/products/cake_socola_18.png");
                    else if (maSP == 3)
                        img = thuTaiTuTaiNguyen("/images/products/cake_redvelvet_16.png");

                    if (img == null)
                        img = thuTaiTuTaiNguyen("/cake" + maSP + ".jpg");
                    if (img == null)
                        img = thuTaiTuTaiNguyen("/cake" + maSP + ".png");
                    if (img != null)
                        System.out.println("  -> Successfully mapped to cake" + maSP + " based on MaSP.");
                }

                // 3. SMART FALLBACK: Nếu vẫn không thấy, thử tìm theo tên sản phẩm
                if (img == null) {
                    String tenSP = sp.getTenSP().toLowerCase();
                    String fallback = null;
                    if (tenSP.contains("vani"))
                        fallback = "/cake1.jpg";
                    else if (tenSP.contains("socola"))
                        fallback = "/cake2.png";
                    else if (tenSP.contains("velvet"))
                        fallback = "/cake3.jpg";
                    else if (tenSP.contains("tiramisu"))
                        fallback = "/cake4.jpg";
                    else if (tenSP.contains("cookie"))
                        fallback = "/cake2.png";
                    else if (tenSP.contains("bread") || tenSP.contains("mi"))
                        fallback = "/cake3.jpg";

                    if (fallback != null) {
                        img = thuTaiTuTaiNguyen(fallback);
                    }
                }
            } catch (Exception e) {
                System.err.println("  -> Error: " + e.getMessage());
            }
        }
        if (img == null)
            System.out.println("  -> FAILED to load image for " + sp.getTenSP());

        if (img != null) {
            int imgW = img.getWidth(null);
            int imgH = img.getHeight(null);
            if (imgW > 0 && imgH > 0) {
                double scale = Math.min(140.0 / imgW, 100.0 / imgH);
                int targetW = (int) (imgW * scale);
                int targetH = (int) (imgH * scale);
                java.awt.Image scaled = img.getScaledInstance(targetW, targetH, java.awt.Image.SCALE_SMOOTH);
                imgLabel.setIcon(new javax.swing.ImageIcon(scaled));
                imgLabel.setText(""); // Xóa text nếu load được ảnh
            }
        } else {
            imgLabel.setText("No Image");
            imgLabel.setFont(new java.awt.Font("Arial", java.awt.Font.ITALIC, 10));
        }

        JLabel lblName = new JLabel("<html><center>" + sp.getTenSP() + "</center></html>", SwingConstants.CENTER);
        lblName.putClientProperty(FlatClientProperties.STYLE, """
                font: bold 12
                """);

        JLabel lblPrice = new JLabel(dinhDangTien(sp.getGiaCoBan()), SwingConstants.CENTER);
        lblPrice.setForeground(COLOR_PRIMARY);
        lblPrice.putClientProperty(FlatClientProperties.STYLE, """
                font: bold 14
                """);

        JPanel info = new JPanel(new GridLayout(2, 1));
        info.setOpaque(false);
        info.add(lblName);
        info.add(lblPrice);

        JButton btnThem = new JButton("Thêm");
        taoKieuNut(btnThem, COLOR_ACCENT, Color.WHITE);
        btnThem.putClientProperty(FlatClientProperties.STYLE, """
                arc: 8;
                margin: 2,10,2,10;
                borderWidth: 0;
                focusWidth: 0;
                innerFocusWidth: 0;
                font: bold 10
                """);
        btnThem.addActionListener(e -> {
            if (presenter != null)
                presenter.themSanPhamVaoGio(sp);
        });

        card.add(imgLabel, BorderLayout.NORTH);
        card.add(info, BorderLayout.CENTER);
        card.add(btnThem, BorderLayout.SOUTH);
        return card;
    }

    private void xayDungGiaoDien() {
        add(taoPanelSidebar(), BorderLayout.WEST);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);
        centerPanel.add(taoHeader(), BorderLayout.NORTH);

        layoutThe = new CardLayout();
        panelThe = new JPanel(layoutThe);
        panelThe.setOpaque(false);

        panelThe.add(taoTabTaoDon(), "TAO_DON");
        panelThe.add(taoTabTheoDoiV2(), "THEO_DOI");

        centerPanel.add(panelThe, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        chuyenTab("TAO_DON");
    }

    private JPanel taoPanelSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(180, 0));
        sidebar.setBackground(COLOR_SIDEBAR);
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(new EmptyBorder(30, 14, 30, 14));

        JLabel logo = new JLabel("H3K Bakery");
        logo.setForeground(COLOR_PRIMARY);
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        logo.putClientProperty(FlatClientProperties.STYLE, """
                font: bold 19
                """);
        sidebar.add(logo);

        sidebar.add(Box.createVerticalStrut(40));

        navItemTaoDon = taoMucDieuHuong("POS");
        navItemTheoDoi = taoMucDieuHuong("Theo dõi");

        navItemTaoDon.setAlignmentX(Component.CENTER_ALIGNMENT);
        navItemTheoDoi.setAlignmentX(Component.CENTER_ALIGNMENT);

        navItemTaoDon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                chuyenTab("TAO_DON");
            }
        });
        navItemTheoDoi.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                chuyenTab("THEO_DOI");
            }
        });

        sidebar.add(navItemTaoDon);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(navItemTheoDoi);

        return sidebar;
    }

    private JPanel taoMucDieuHuong(String text) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 8));
        p.setOpaque(true);
        p.setBackground(COLOR_SIDEBAR);
        p.setMaximumSize(new Dimension(150, 40));
        p.setCursor(new Cursor(Cursor.HAND_CURSOR));
        p.putClientProperty(FlatClientProperties.STYLE, "arc: 10");

        JLabel l = new JLabel(text);
        l.setForeground(COLOR_SUB_TEXT);
        l.putClientProperty(FlatClientProperties.STYLE, """
                """);
        p.add(l);
        return p;
    }

    private void chuyenTab(String tabName) {
        layoutThe.show(panelThe, tabName);
        boolean isTaoDon = "TAO_DON".equals(tabName);
        capNhatKieuNav(navItemTaoDon, isTaoDon);
        capNhatKieuNav(navItemTheoDoi, !isTaoDon);
    }

    private void capNhatKieuNav(JPanel p, boolean active) {
        p.setBackground(active ? COLOR_PRIMARY_LIGHT : COLOR_SIDEBAR);
        JLabel l = (JLabel) p.getComponent(0);
        l.setForeground(active ? COLOR_PRIMARY : COLOR_SUB_TEXT);
        l.putClientProperty(FlatClientProperties.STYLE, active
                ? "font: bold"
                : "font: plain");
    }

    private JPanel taoHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(COLOR_CARD);
        header.setPreferredSize(new Dimension(0, 70));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_PRIMARY_LIGHT));

        JLabel title = new JLabel("POS - Bán hàng & Quản lý đơn");
        title.setForeground(COLOR_HEADING);
        title.setBorder(new EmptyBorder(0, 25, 0, 0));
        title.putClientProperty(FlatClientProperties.STYLE, """
                font: bold 18
                """);

        header.add(title, BorderLayout.WEST);
        return header;
    }

    private JPanel taoTabTaoDon() {
        JPanel dashboardBody = new JPanel(new GridBagLayout());
        dashboardBody.setOpaque(false);
        dashboardBody.setBorder(new EmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;

        gbc.gridx = 0;
        gbc.weightx = 0.65;
        dashboardBody.add(xayDungPanelSanPham(), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.35;
        gbc.insets = new Insets(0, 20, 0, 0);
        dashboardBody.add(xayDungPanelGioHang(), gbc);

        return dashboardBody;
    }

    private JPanel xayDungPanelSanPham() {
        JPanel panel = new JPanel(new BorderLayout(0, 20));
        panel.setOpaque(false);

        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filterBar.setOpaque(false);

        btnLocTatCa = new JButton("ALL");
        btnLocCake = new JButton("Cake");
        btnLocCookie = new JButton("Cookie");
        btnLocBread = new JButton("Bread");
        btnLocTuyChinh = new JButton("Bánh Tùy Chỉnh");

        JButton[] filterBtns = { btnLocTatCa, btnLocCake, btnLocCookie, btnLocBread, btnLocTuyChinh };
        String[] cats = { "ALL", "Cake", "Cookie", "Bread", "CUSTOM" };

        for (int i = 0; i < filterBtns.length; i++) {
            JButton b = filterBtns[i];
            String c = cats[i];
            taoKieuNut(b, COLOR_CARD, COLOR_TEXT);
            b.addActionListener(e -> datBoLocHoatDong(c, b));
            filterBar.add(b);
        }

        txtTimKiemSanPham = new JTextField(15);
        txtTimKiemSanPham.setPreferredSize(new Dimension(200, 30));
        taoKieuONhap(txtTimKiemSanPham);

        // FlatLaf enhancements for search input
        txtTimKiemSanPham.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Nhập tên sản phẩm...");
        txtTimKiemSanPham.putClientProperty(FlatClientProperties.TEXT_FIELD_SHOW_CLEAR_BUTTON, true);

        JPanel searchWrap = new JPanel(new BorderLayout(5, 0));
        searchWrap.setOpaque(false);
        searchWrap.add(txtTimKiemSanPham, BorderLayout.CENTER);
        filterBar.add(Box.createHorizontalStrut(20));
        filterBar.add(searchWrap);

        tileSanPham = new JPanel(new GridLayout(0, 3, 15, 15));
        tileSanPham.setOpaque(false);
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(tileSanPham, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(wrapper);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setWheelScrollingEnabled(true);

        layoutTheSanPham = new CardLayout();
        panelChuaSanPham = new JPanel(layoutTheSanPham);
        panelChuaSanPham.setOpaque(false);
        panelChuaSanPham.add(scrollPane, "PRODUCTS");
        panelChuaSanPham.add(xayDungPanelBanhTuyChinh(), "CUSTOM_CAKE");

        datBoLocHoatDong("ALL", btnLocTatCa);

        panel.add(filterBar, BorderLayout.NORTH);
        panel.add(panelChuaSanPham, BorderLayout.CENTER);
        return panel;
    }

    private JPanel xayDungPanelBanhTuyChinh() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);

        cbCustomSp = new JComboBox<>();
        cbCustomKichCo = new JComboBox<>();
        cbCustomCotBanh = new JComboBox<>();
        cbCustomNhanBanh = new JComboBox<>();
        cbCustomTrangTri = new JComboBox<>();
        txtCustomLoiChuc = new JTextArea(3, 20);
        txtCustomGhiChu = new JTextArea(3, 20);

        txtCustomLoiChuc.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT,
                "Nhập lời chúc muốn viết lên bánh...");
        txtCustomGhiChu.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT,
                "Ví dụ: Ít ngọt, trang trí thêm dâu...");

        lblGiaTuyChinh = new JLabel("0 đ", SwingConstants.RIGHT);
        lblGiaTuyChinh.setForeground(COLOR_PRIMARY);
        lblGiaTuyChinh.putClientProperty(FlatClientProperties.STYLE, """
                font: bold 18
                """);
        btnThemTuyChinhVaoGio = new JButton("Thêm vào giỏ");
        taoKieuNut(btnThemTuyChinhVaoGio, COLOR_ACCENT, Color.WHITE);

        // Listeners
        java.awt.event.ActionListener calcPriceListener = e -> tinhGiaTuyChinh();
        cbCustomSp.addActionListener(calcPriceListener);
        cbCustomKichCo.addActionListener(calcPriceListener);
        cbCustomCotBanh.addActionListener(calcPriceListener);
        cbCustomNhanBanh.addActionListener(calcPriceListener);
        cbCustomTrangTri.addActionListener(calcPriceListener);

        btnThemTuyChinhVaoGio.addActionListener(e -> {
            if (presenter == null)
                return;
            SanPhamDTO sp = (SanPhamDTO) cbCustomSp.getSelectedItem();
            if (sp == null) {
                hienThiLoi("Vui lòng chọn sản phẩm nền.");
                return;
            }
            KichCoBanhDTO kc = (KichCoBanhDTO) cbCustomKichCo.getSelectedItem();
            CotBanhDTO cot = (CotBanhDTO) cbCustomCotBanh.getSelectedItem();
            NhanBanhDTO nhan = (NhanBanhDTO) cbCustomNhanBanh.getSelectedItem();
            KieuTrangTriDTO tt = (KieuTrangTriDTO) cbCustomTrangTri.getSelectedItem();

            double donGia = chuyenSoAnToan(lblGiaTuyChinh.getText().replace(" đ", "").replace(",", ""));
            presenter.themBanhTuyChinhVaoGio(sp, 1, donGia,
                    kc != null ? kc.getMaKC() : null,
                    cot != null ? cot.getMaCot() : null,
                    nhan != null ? nhan.getMaNhan() : null,
                    tt != null ? tt.getMaTrangTri() : null,
                    txtCustomLoiChuc.getText().trim(),
                    txtCustomGhiChu.getText().trim());
            hienThiThanhCong("Đã thêm bánh tùy chỉnh vào giỏ!");
        });

        int row = 0;
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.2;
        panel.add(new JLabel("Sản phẩm nền:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = row++;
        gbc.weightx = 0.8;
        panel.add(cbCustomSp, gbc);

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.2;
        panel.add(new JLabel("Kích cỡ:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = row++;
        gbc.weightx = 0.8;
        panel.add(cbCustomKichCo, gbc);

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.2;
        panel.add(new JLabel("Cốt bánh:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = row++;
        gbc.weightx = 0.8;
        panel.add(cbCustomCotBanh, gbc);

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.2;
        panel.add(new JLabel("Nhân bánh:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = row++;
        gbc.weightx = 0.8;
        panel.add(cbCustomNhanBanh, gbc);

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.2;
        panel.add(new JLabel("Trang trí:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = row++;
        gbc.weightx = 0.8;
        panel.add(cbCustomTrangTri, gbc);

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.2;
        panel.add(new JLabel("Lời chúc:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = row++;
        gbc.weightx = 0.8;
        JScrollPane scrollLoiChuc = new JScrollPane(txtCustomLoiChuc);
        panel.add(scrollLoiChuc, gbc);

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.2;
        panel.add(new JLabel("Ghi chú:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = row++;
        gbc.weightx = 0.8;
        JScrollPane scrollGhiChu = new JScrollPane(txtCustomGhiChu);
        panel.add(scrollGhiChu, gbc);

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.2;
        panel.add(new JLabel("Giá dự tính:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = row++;
        gbc.weightx = 0.8;
        panel.add(lblGiaTuyChinh, gbc);

        gbc.gridx = 1;
        gbc.gridy = row++;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(btnThemTuyChinhVaoGio, gbc);

        JPanel outerPanel = new JPanel(new BorderLayout());
        outerPanel.setOpaque(false);
        outerPanel.add(panel, BorderLayout.NORTH);
        JScrollPane mainScroll = new JScrollPane(outerPanel);
        mainScroll.setBorder(null);
        mainScroll.setOpaque(false);
        mainScroll.getViewport().setOpaque(false);
        return outerPanel;
    }

    private void tinhGiaTuyChinh() {
        if (presenter == null)
            return;
        SanPhamDTO sp = (SanPhamDTO) cbCustomSp.getSelectedItem();
        if (sp == null) {
            lblGiaTuyChinh.setText("0 đ");
            return;
        }
        KichCoBanhDTO kc = (KichCoBanhDTO) cbCustomKichCo.getSelectedItem();
        CotBanhDTO cot = (CotBanhDTO) cbCustomCotBanh.getSelectedItem();
        NhanBanhDTO nhan = (NhanBanhDTO) cbCustomNhanBanh.getSelectedItem();
        KieuTrangTriDTO tt = (KieuTrangTriDTO) cbCustomTrangTri.getSelectedItem();

        double price = presenter.tinhGiaBanhTuyChinh(
                sp.getMaSP(),
                kc != null ? kc.getMaKC() : null,
                cot != null ? cot.getMaCot() : null,
                nhan != null ? nhan.getMaNhan() : null,
                tt != null ? tt.getMaTrangTri() : null);
        lblGiaTuyChinh.setText(dinhDangTien(price));
    }

    private JPanel xayDungPanelGioHang() {
        JPanel cart = new JPanel(new BorderLayout());
        cart.setBackground(COLOR_CARD);
        cart.putClientProperty(FlatClientProperties.STYLE, "arc: 12");
        cart.setBorder(new EmptyBorder(20, 20, 20, 20));

        // 1. Form Khách hàng & Cài đặt giao hàng
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 4, 4, 4);

        txtSoDienThoai = new JTextField(10);
        taoKieuONhap(txtSoDienThoai);
        txtSoDienThoai.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Nhập SĐT khách hàng");

        btnTimKhach = new JButton("Kiểm tra");
        taoKieuNut(btnTimKhach, COLOR_PRIMARY, Color.WHITE);

        JPanel phonePanel = new JPanel(new BorderLayout(5, 0));
        phonePanel.setOpaque(false);
        phonePanel.add(txtSoDienThoai, BorderLayout.CENTER);
        phonePanel.add(btnTimKhach, BorderLayout.EAST);

        lblTenKhachHang = new JLabel("Khách vãng lai");
        lblTenKhachHang.setForeground(COLOR_PRIMARY);
        lblTenKhachHang.putClientProperty(FlatClientProperties.STYLE, """
                font: bold italic 13
                """);

        cbHinhThucNhan = new JComboBox<>(new String[] { "Trực tiếp", "Đặt hàng" });
        txtDiaChiGiao = new JTextField();
        taoKieuONhap(txtDiaChiGiao);
        txtDiaChiGiao.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Nhập địa chỉ giao bánh");
        txtDiaChiGiao.setEnabled(false);
        lblErrDiaChiGiao = new JLabel();
        lblErrDiaChiGiao.setForeground(ERROR);

        dpNgayNhanBanh = new SwingDatePicker();
        cbGioNhanBanh = new JComboBox<>();
        for (int i = 0; i < 24; i++) {
            cbGioNhanBanh.addItem(String.format("%02d:00", i));
        }
        datGioNhanMacDinhTheoHienTai();

        JPanel timePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        timePanel.setOpaque(false);
        timePanel.add(dpNgayNhanBanh);
        timePanel.add(cbGioNhanBanh);
        lblErrNgayNhanBanh = new JLabel();
        lblErrNgayNhanBanh.setForeground(ERROR);

        themDongLuoi(formPanel, gbc, 0, "SĐT khách:", phonePanel);
        themDongLuoi(formPanel, gbc, 1, "Tên khách:", lblTenKhachHang);
        themDongLuoi(formPanel, gbc, 2, "Nhận bánh:", cbHinhThucNhan);
        themDongLuoi(formPanel, gbc, 3, "Địa chỉ:", txtDiaChiGiao);
        gbc.gridy = 4;
        gbc.gridx = 1;
        formPanel.add(lblErrDiaChiGiao, gbc);

        // 2. Table Giỏ Hàng
        String[] columns = { "Sản phẩm", "SL", "Đơn giá", "Tổng" };
        modelBangGioHang = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblGioHang = new JTable(modelBangGioHang);
        taoKieuBang(tblGioHang, 30);
        JScrollPane scrollTable = new JScrollPane(tblGioHang);
        scrollTable.setPreferredSize(new Dimension(0, 150));
        scrollTable.setBorder(BorderFactory.createLineBorder(COLOR_PRIMARY_LIGHT));

        JPanel tableControls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 5));
        tableControls.setOpaque(false);
        JButton btnNhapSL = new JButton("Nhập SL");
        taoKieuNut(btnNhapSL, COLOR_PRIMARY, Color.WHITE);
        JButton btnGiam = new JButton("SL (-)");
        taoKieuNut(btnGiam, COLOR_PRIMARY, Color.WHITE);
        JButton btnTang = new JButton("SL (+)");
        taoKieuNut(btnTang, COLOR_PRIMARY, Color.WHITE);
        JButton btnXoa = new JButton("Xóa");
        taoKieuNut(btnXoa, ERROR, Color.WHITE);

        btnNhapSL.addActionListener(e -> {
            int row = tblGioHang.getSelectedRow();
            if (row < 0) {
                hienThiLoi("Vui lòng chọn 1 sản phẩm");
                return;
            }
            String input = JOptionPane.showInputDialog(this, "Nhập số lượng:", "Cập nhật SL",
                    JOptionPane.QUESTION_MESSAGE);
            if (input != null && !input.trim().isEmpty()) {
                try {
                    int newQty = Integer.parseInt(input.trim());
                    if (newQty > 0)
                        presenter.thayDoiSoLuongMon(row, newQty - localGioHangHienTai.get(row).getSoLuong());
                    else
                        presenter.thayDoiSoLuongMon(row, 0);
                } catch (Exception ex) {
                    hienThiLoi("Số lượng không hợp lệ!");
                }
            }
        });
        btnGiam.addActionListener(e -> {
            if (presenter != null)
                presenter.thayDoiSoLuongMon(tblGioHang.getSelectedRow(), -1);
        });
        btnTang.addActionListener(e -> {
            if (presenter != null)
                presenter.thayDoiSoLuongMon(tblGioHang.getSelectedRow(), 1);
        });
        btnXoa.addActionListener(e -> {
            if (presenter != null)
                presenter.thayDoiSoLuongMon(tblGioHang.getSelectedRow(), 0);
        });

        tableControls.add(btnNhapSL);
        tableControls.add(btnGiam);
        tableControls.add(btnTang);
        tableControls.add(btnXoa);

        JPanel tableWrap = new JPanel(new BorderLayout());
        tableWrap.setOpaque(false);
        tableWrap.add(scrollTable, BorderLayout.CENTER);
        tableWrap.add(tableControls, BorderLayout.SOUTH);

        // 3. Payment Area
        JPanel bottomPanel = new JPanel(new GridBagLayout());
        bottomPanel.setOpaque(false);
        GridBagConstraints gbcPay = new GridBagConstraints();
        gbcPay.fill = GridBagConstraints.HORIZONTAL;
        gbcPay.insets = new Insets(8, 8, 8, 8);

        lblTongTienHang = new JLabel("0 đ");
        lblTongTienHang.putClientProperty(FlatClientProperties.STYLE, "font: bold 12");
        lblTienGiamGia = new JLabel("0 đ");
        lblTienGiamGia.setForeground(SUCCESS);
        lblTienGiamGia.putClientProperty(FlatClientProperties.STYLE, "font: bold 12");
        lblTongThanhToan = new JLabel("0 đ");
        lblTongThanhToan.putClientProperty(FlatClientProperties.STYLE, "font: bold 18");
        lblTongThanhToan.setForeground(ERROR);
        lblCocToiThieu = new JLabel("0 đ");
        lblCocToiThieu.putClientProperty(FlatClientProperties.STYLE, "font: bold 12");
        lblConLai = new JLabel("0 đ");
        lblConLai.setForeground(COLOR_PRIMARY);
        lblConLai.putClientProperty(FlatClientProperties.STYLE, "font: bold 12");
        lblTienThua = new JLabel("0 đ");
        lblTienThua.putClientProperty(FlatClientProperties.STYLE, "font: bold 12");

        txtTienCoc = new JTextField("0");
        txtTienKhachDua = new JTextField("0");
        taoKieuONhap(txtTienCoc);
        txtTienCoc.putClientProperty(FlatClientProperties.STYLE, "font: bold 12");
        taoKieuONhap(txtTienKhachDua);
        txtTienKhachDua.putClientProperty(FlatClientProperties.STYLE, "font: bold 12");
        chkXacNhanThuTien = new JCheckBox("Đã thu đủ tiền");
        chkXacNhanThuTien.setOpaque(false);
        chkXacNhanThuTien.putClientProperty(FlatClientProperties.STYLE, """
                font: bold 12
                """);

        int row = 0;
        themDongThanhToan(bottomPanel, gbcPay, row++, "Tiền hàng:", lblTongTienHang, "Giảm giá:", lblTienGiamGia);
        themDongThanhToan(bottomPanel, gbcPay, row++, "Cần trả:", lblTongThanhToan, "Cọc tối thiểu:", lblCocToiThieu);
        themDongThanhToan(bottomPanel, gbcPay, row++, "Khách đưa:", txtTienKhachDua, "Tiền thừa:", lblTienThua);
        themDongThanhToan(bottomPanel, gbcPay, row++, "Khách cọc:", txtTienCoc, "Còn nợ:", lblConLai);
        gbcPay.gridy = row++;
        gbcPay.gridx = 0;
        gbcPay.gridwidth = 4;
        bottomPanel.add(chkXacNhanThuTien, gbcPay);

        btnTaoDonHang = new JButton("ĐẶT BÁNH");
        taoKieuNut(btnTaoDonHang, COLOR_SURFACE, COLOR_PRIMARY);
        btnTaoDonHang.putClientProperty(FlatClientProperties.STYLE, """
                arc: 8;
                margin: 4,14,4,14;
                borderWidth: 0;
                focusWidth: 0;
                innerFocusWidth: 0;
                font: bold 14
                """);
        btnTaoDonHang.setPreferredSize(new Dimension(0, 40));

        btnThanhToan = new JButton("THANH TOÁN");
        taoKieuNut(btnThanhToan, COLOR_ACCENT, Color.WHITE);
        btnThanhToan.putClientProperty(FlatClientProperties.STYLE, """
                arc: 8;
                margin: 4,14,4,14;
                borderWidth: 0;
                focusWidth: 0;
                innerFocusWidth: 0;
                font: bold 18
                """);
        btnThanhToan.setPreferredSize(new Dimension(0, 55));

        JPanel actionPanel = new JPanel(new BorderLayout(0, 8));
        actionPanel.setOpaque(false);
        actionPanel.add(btnTaoDonHang, BorderLayout.NORTH);
        actionPanel.add(btnThanhToan, BorderLayout.CENTER);

        lblThongBaoTab1 = new JLabel("", SwingConstants.CENTER);
        lblThongBaoTab1.setForeground(COLOR_SUB_TEXT);
        lblThongBaoTab1.putClientProperty(FlatClientProperties.STYLE, """
                font: italic 12
                """);

        JPanel paymentWrap = new JPanel(new BorderLayout(0, 10));
        paymentWrap.setOpaque(false);
        paymentWrap.add(new JSeparator(), BorderLayout.NORTH);

        JPanel timeWrap = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        timeWrap.setOpaque(false);
        JLabel lblTime = new JLabel("Ngày/Giờ nhận:");
        lblTime.setForeground(COLOR_TEXT);
        lblTime.putClientProperty(FlatClientProperties.STYLE, """
                font: bold 13
                """);
        timeWrap.add(lblTime);
        timeWrap.add(timePanel);
        timeWrap.add(lblErrNgayNhanBanh);

        JPanel pnlCenter = new JPanel(new BorderLayout(0, 5));
        pnlCenter.setOpaque(false);
        pnlCenter.add(timeWrap, BorderLayout.NORTH);
        pnlCenter.add(bottomPanel, BorderLayout.CENTER);

        paymentWrap.add(pnlCenter, BorderLayout.CENTER);

        JPanel actWrap = new JPanel(new BorderLayout());
        actWrap.setOpaque(false);
        actWrap.add(lblThongBaoTab1, BorderLayout.NORTH);
        actWrap.add(actionPanel, BorderLayout.CENTER);
        paymentWrap.add(actWrap, BorderLayout.SOUTH);

        cart.add(formPanel, BorderLayout.NORTH);
        cart.add(tableWrap, BorderLayout.CENTER);
        cart.add(paymentWrap, BorderLayout.SOUTH);
        return cart;
    }

    private JScrollPane taoTabTheoDoiV2() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // FILTER SECTION
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        filterPanel.setOpaque(false);
        filterPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(COLOR_PRIMARY_LIGHT),
                "Tìm kiếm & Lọc đơn hàng", TitledBorder.LEFT, TitledBorder.TOP,
                javax.swing.UIManager.getFont("defaultFont") != null
                        ? javax.swing.UIManager.getFont("defaultFont").deriveFont(Font.BOLD, 14f)
                        : new Font("Segoe UI", Font.BOLD, 14),
                COLOR_PRIMARY));

        txtTimMaDon = new JTextField(10);
        taoKieuONhap(txtTimMaDon);
        txtTimMaDon.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Nhập mã đơn...");
        txtTimMaDon.putClientProperty(FlatClientProperties.TEXT_FIELD_SHOW_CLEAR_BUTTON, true);

        dpNgayTheoDoi = new SwingDatePicker();
        dpNgayTheoDoi.setSelectedDate(LocalDate.now(ZONE_VN));

        cbGioTu = new JComboBox<>();
        cbGioDen = new JComboBox<>();
        cbGioDen.addItem("23:59");

        for (int i = 0; i < 24; i++) {
            cbGioTu.addItem(String.format("%02d:00", i));
            cbGioDen.addItem(String.format("%02d:00", i));
            cbGioTu.addItem(String.format("%02d:30", i));
            cbGioDen.addItem(String.format("%02d:30", i));
        }

        btnTimTheoNgayGio = new JButton("Lọc / Tìm kiếm");
        taoKieuNut(btnTimTheoNgayGio, COLOR_PRIMARY, Color.WHITE);

        lblThongBaoTab2 = new JLabel();
        lblThongBaoTab2.setForeground(SUCCESS);
        lblThongBaoTab2.putClientProperty(FlatClientProperties.STYLE, """
                font: bold 12
                """);

        filterPanel.add(new JLabel("Mã đơn:"));
        filterPanel.add(txtTimMaDon);
        filterPanel.add(Box.createHorizontalStrut(10));
        filterPanel.add(new JLabel("Ngày:"));
        filterPanel.add(dpNgayTheoDoi);
        filterPanel.add(Box.createHorizontalStrut(10));
        filterPanel.add(new JLabel("Từ:"));
        filterPanel.add(cbGioTu);
        filterPanel.add(new JLabel("Đến:"));
        filterPanel.add(cbGioDen);
        filterPanel.add(Box.createHorizontalStrut(15));
        filterPanel.add(btnTimTheoNgayGio);
        filterPanel.add(lblThongBaoTab2);

        // CONTENT SECTION
        panelChuaDon = new JPanel();
        panelChuaDon.setLayout(new BoxLayout(panelChuaDon, BoxLayout.Y_AXIS));
        panelChuaDon.setOpaque(false);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(panelChuaDon, BorderLayout.NORTH);

        JScrollPane scrollDon = new JScrollPane(wrapper);
        scrollDon.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(COLOR_PRIMARY_LIGHT),
                "Danh sách đơn hàng", TitledBorder.LEFT, TitledBorder.TOP,
                javax.swing.UIManager.getFont("defaultFont") != null
                        ? javax.swing.UIManager.getFont("defaultFont").deriveFont(Font.BOLD, 14f)
                        : new Font("Segoe UI", Font.BOLD, 14),
                COLOR_PRIMARY));
        scrollDon.setOpaque(false);
        scrollDon.getViewport().setOpaque(false);
        scrollDon.getVerticalScrollBar().setUnitIncrement(16);

        panel.add(filterPanel, BorderLayout.NORTH);
        panel.add(scrollDon, BorderLayout.CENTER);

        return new JScrollPane(panel);
    }

    private JPanel taoCardDonHang(DonDatHangDTO don) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(COLOR_CARD);
        card.putClientProperty(FlatClientProperties.STYLE, "arc: 10");
        card.setBorder(new EmptyBorder(15, 15, 15, 15));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Info
        JPanel infoPanel = new JPanel(new GridLayout(2, 2, 10, 5));
        infoPanel.setOpaque(false);
        infoPanel.add(
                new JLabel("<html><span style='font-size:12px'><b>Mã đơn:</b> #" + don.getMaDon() + "</span></html>"));
        String kh = don.getMaKH() == null ? "Khách lẻ" : "Mã KH: " + don.getMaKH();
        infoPanel.add(new JLabel("<html><span style='font-size:12px'><b>Khách:</b> " + kh + "</span></html>"));

        String time = don.getNgayGioNhanBanh() != null
                ? don.getNgayGioNhanBanh().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                : "";
        infoPanel.add(new JLabel("<html><span style='font-size:12px'><b>Nhận lúc:</b> " + time + "</span></html>"));
        infoPanel.add(new JLabel("<html><span style='font-size:12px'><b>Tổng:</b> <font color='#DC2626'>"
                + dinhDangTien(don.getTongTienHDBan()) + "</font></span></html>"));

        // Action
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        actionPanel.setOpaque(false);

        JLabel lblStatus = new JLabel("TT: " + don.getTenTrangThai());
        lblStatus.setForeground(COLOR_ACCENT);
        lblStatus.putClientProperty(FlatClientProperties.STYLE, """
                font: bold 12
                """);

        JComboBox<String> cbStatus = new JComboBox<>();
        for (String s : listTrangThai)
            cbStatus.addItem(s);
        if (don.getTenTrangThai() != null)
            cbStatus.setSelectedItem(don.getTenTrangThai());

        JButton btnUpdate = new JButton("Cập nhật");
        taoKieuNut(btnUpdate, SUCCESS, Color.WHITE);
        btnUpdate.addActionListener(e -> {
            if (presenter != null) {
                presenter.capNhatTrangThai(String.valueOf(don.getMaDon()), cbStatus.getSelectedItem().toString(),
                        don.getTenTrangThai());
            }
        });

        actionPanel.add(lblStatus);
        actionPanel.add(new JLabel("-> Đổi:"));
        actionPanel.add(cbStatus);
        actionPanel.add(btnUpdate);

        card.add(infoPanel, BorderLayout.CENTER);
        card.add(actionPanel, BorderLayout.EAST);

        return card;
    }

    private void taoKieuNut(AbstractButton btn, Color bg, Color fg) {
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.putClientProperty(FlatClientProperties.STYLE, """
                arc: 8;
                margin: 4,14,4,14;
                borderWidth: 0;
                focusWidth: 0;
                innerFocusWidth: 0;
                font: bold
                """);
    }

    private void taoKieuONhap(JTextField txt) {
        txt.setForeground(COLOR_TEXT);
        txt.setBackground(COLOR_CARD);
        txt.putClientProperty(FlatClientProperties.STYLE, """
                arc: 6;
                margin: 4,8,4,8;
                """);
    }

    private void taoKieuBang(JTable table, int rowHeight) {
        table.setRowHeight(rowHeight);
        table.setForeground(COLOR_TEXT);
        table.setSelectionBackground(COLOR_PRIMARY_LIGHT);
        table.setSelectionForeground(COLOR_TEXT);
        table.setGridColor(COLOR_PRIMARY_LIGHT);
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);
        table.putClientProperty(FlatClientProperties.STYLE, """
                """);
        JTableHeader header = table.getTableHeader();
        header.setBackground(COLOR_SURFACE);
        header.setForeground(COLOR_HEADING);
        header.setReorderingAllowed(false);
        header.putClientProperty(FlatClientProperties.STYLE, """
                font: bold
                """);
    }

    private void themDongLuoi(JPanel panel, GridBagConstraints gbc, int row, String labelText, Component comp) {
        gbc.gridy = row;
        gbc.gridx = 0;
        gbc.weightx = 0;
        JLabel lbl = new JLabel(labelText);
        lbl.setForeground(COLOR_TEXT);
        lbl.putClientProperty(FlatClientProperties.STYLE, """
                font: bold 12
                """);
        panel.add(lbl, gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        panel.add(comp, gbc);
    }

    private void themDongThanhToan(JPanel pnl, GridBagConstraints gbc, int row, String lbl1, Component comp1, String lbl2,
            Component comp2) {
        gbc.gridy = row;
        gbc.gridx = 0;
        gbc.weightx = 0;
        pnl.add(new JLabel("<html><span style='font-size:11px; font-weight:bold'>" + lbl1 + "</span></html>"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.5;
        pnl.add(comp1, gbc);
        gbc.gridx = 2;
        gbc.weightx = 0;
        pnl.add(new JLabel("<html><span style='font-size:11px; font-weight:bold'>" + lbl2 + "</span></html>"), gbc);
        gbc.gridx = 3;
        gbc.weightx = 0.5;
        pnl.add(comp2, gbc);
    }

    private void datGioNhanMacDinhTheoHienTai() {
        if (cbGioNhanBanh == null)
            return;
        int gioHienTai = LocalTime.now(ZONE_VN).getHour();
        String gioMacDinh = String.format("%02d:00", gioHienTai);
        cbGioNhanBanh.setSelectedItem(gioMacDinh);
    }

    private double chuyenSoAnToan(String raw) {
        try {
            if (raw == null)
                return 0;
            return Double.parseDouble(raw.trim().replace(" đ", "").replaceAll("[,\\.\\s]", ""));
        } catch (Exception e) {
            return 0;
        }
    }

    private String dinhDangTien(double val) {
        return String.format(Locale.US, "%,.0f đ", val);
    }

    // =========================================================
    // LỚP NỘI BỘ: CUSTOM SWING DATE PICKER
    // =========================================================
    class SwingDatePicker extends JPanel {
        private JTextField txtNgay;
        private JButton btnChon;
        private JPopupMenu popup;
        private LocalDate ngayDaChon;

        public SwingDatePicker() {
            ngayDaChon = LocalDate.now(ZONE_VN);
            setLayout(new BorderLayout());

            txtNgay = new JTextField(10);
            txtNgay.setEditable(false);
            txtNgay.setBackground(COLOR_CARD);
            txtNgay.putClientProperty(FlatClientProperties.STYLE, """
                    arc: 6;
                    margin: 4,8,4,8;
                    """);
            txtNgay.setText(ngayDaChon.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

            btnChon = new JButton("📅");
            btnChon.setFocusPainted(false);
            btnChon.setBackground(COLOR_PRIMARY_LIGHT);
            btnChon.setForeground(COLOR_PRIMARY_DARK);
            btnChon.putClientProperty(FlatClientProperties.STYLE, "arc: 6");
            btnChon.addActionListener(e -> hienThiPopup());

            add(txtNgay, BorderLayout.CENTER);
            add(btnChon, BorderLayout.EAST);
        }

        public LocalDate getValue() {
            return ngayDaChon;
        }

        public void setSelectedDate(LocalDate date) {
            this.ngayDaChon = date;
            txtNgay.setText(date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            if (popup != null && popup.isVisible())
                popup.setVisible(false);
        }

        private void hienThiPopup() {
            popup = new JPopupMenu();
            popup.add(new DatePickerPane(this, ngayDaChon));
            popup.show(txtNgay, 0, txtNgay.getHeight());
        }
    }

    class DatePickerPane extends JPanel {
        private JLabel lblThangNam;
        private JPanel panelNgay;
        private LocalDate thangHienTai;
        private SwingDatePicker parent;

        public DatePickerPane(SwingDatePicker parent, LocalDate initDate) {
            this.parent = parent;
            this.thangHienTai = initDate.withDayOfMonth(1);
            setLayout(new BorderLayout());
            setBackground(Color.WHITE);
            setBorder(new LineBorder(COLOR_PRIMARY, 1));

            JButton btnPrev = new JButton("<");
            btnPrev.setBackground(Color.WHITE);
            btnPrev.setFocusPainted(false);
            JButton btnNext = new JButton(">");
            btnNext.setBackground(Color.WHITE);
            btnNext.setFocusPainted(false);
            lblThangNam = new JLabel("", SwingConstants.CENTER);
            lblThangNam.setForeground(COLOR_PRIMARY);
            lblThangNam.putClientProperty(FlatClientProperties.STYLE, """
                    font: bold 12
                    """);

            btnPrev.addActionListener(e -> {
                thangHienTai = thangHienTai.minusMonths(1);
                capNhatLich();
            });
            btnNext.addActionListener(e -> {
                thangHienTai = thangHienTai.plusMonths(1);
                capNhatLich();
            });

            JPanel pnlHeader = new JPanel(new BorderLayout());
            pnlHeader.setBackground(Color.WHITE);
            pnlHeader.add(btnPrev, BorderLayout.WEST);
            pnlHeader.add(lblThangNam, BorderLayout.CENTER);
            pnlHeader.add(btnNext, BorderLayout.EAST);

            add(pnlHeader, BorderLayout.NORTH);

            panelNgay = new JPanel(new GridLayout(0, 7, 2, 2));
            panelNgay.setBackground(Color.WHITE);
            panelNgay.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            add(panelNgay, BorderLayout.CENTER);
            capNhatLich();
        }

        private void capNhatLich() {
            panelNgay.removeAll();
            lblThangNam.setText(thangHienTai.format(DateTimeFormatter.ofPattern("MM/yyyy")));
            String[] days = { "T2", "T3", "T4", "T5", "T6", "T7", "CN" };
            for (String d : days) {
                JLabel lbl = new JLabel(d, SwingConstants.CENTER);
                lbl.setForeground(COLOR_TEXT);
                lbl.putClientProperty(FlatClientProperties.STYLE, """
                        font: bold 10
                        """);
                panelNgay.add(lbl);
            }

            int dayOfWeek = thangHienTai.getDayOfWeek().getValue();
            int daysInMonth = thangHienTai.lengthOfMonth();

            for (int i = 1; i < dayOfWeek; i++)
                panelNgay.add(new JLabel(""));
            for (int i = 1; i <= daysInMonth; i++) {
                int day = i;
                JButton btnDay = new JButton(String.valueOf(day));
                btnDay.setMargin(new Insets(2, 2, 2, 2));
                btnDay.setFocusPainted(false);
                btnDay.putClientProperty(FlatClientProperties.STYLE, "arc: 5");

                if (thangHienTai.withDayOfMonth(day).equals(LocalDate.now(ZONE_VN))) {
                    btnDay.setBackground(SUCCESS);
                    btnDay.setForeground(Color.WHITE);
                } else {
                    btnDay.setBackground(COLOR_SURFACE);
                    btnDay.setForeground(COLOR_TEXT);
                }

                btnDay.addActionListener(e -> parent.setSelectedDate(thangHienTai.withDayOfMonth(day)));
                panelNgay.add(btnDay);
            }

            Window window = SwingUtilities.getWindowAncestor(this);
            if (window != null)
                window.pack();

            panelNgay.revalidate();
            panelNgay.repaint();
        }
    }

    private ImageIcon taoMaVietQR(double amount, String orderId) {
        try {
            // Thông tin cấu hình
            String bankId = "vcb";
            String accountNo = "1049423992";
            String accountName = "VIEN DANG KHOA";

            String info = "DonHang_" + orderId;

            String urlString = String.format(
                    "https://img.vietqr.io/image/%s-%s-compact.jpg?amount=%.0f&addInfo=%s&accountName=%s",
                    bankId, accountNo, amount, info, accountName.replace(" ", "%20"));

            java.net.URL url = java.net.URI.create(urlString).toURL();
            Image image = javax.imageio.ImageIO.read(url);

            return new ImageIcon(image.getScaledInstance(300, 300, Image.SCALE_SMOOTH));
        } catch (Exception e) {
            System.out.println("Lỗi tạo QR: " + e.getMessage());
            return null;
        }

    }

    public void hienThiPopupQR(double amount, String orderId) {
        ImageIcon qrIcon = taoMaVietQR(amount, orderId);
        if (qrIcon != null) {
            JLabel lblQR = new JLabel(qrIcon);
            JOptionPane.showMessageDialog(this, lblQR, "Quét mã thanh toán Vietcombank", JOptionPane.PLAIN_MESSAGE);
        } else {
            hienThiLoi("Không thể kết nối máy chủ VietQR!");
        }
    }
}