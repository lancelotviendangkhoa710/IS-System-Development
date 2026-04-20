package com.bakery.views;

import com.bakery.model.dto.CTDonHangDTO;
import com.bakery.model.dto.DonDatHangDTO;
import com.bakery.model.dto.SanPhamDTO;
import com.bakery.presenters.OrderPresenter;
import com.bakery.views.interfaces.IOrderView;

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
import java.net.URL;

public class OrderViewPanel extends JPanel implements IOrderView {
    private static final ZoneId ZONE_VN = ZoneId.of("Asia/Ho_Chi_Minh");
    // Bảng màu chuẩn H3K Bakery (.agents/UI.md)
    private final Color COLOR_PRIMARY = new Color(146, 64, 14);       // #92400E
    private final Color COLOR_PRIMARY_DARK = new Color(120, 53, 15);  // #78350F
    private final Color COLOR_PRIMARY_LIGHT = new Color(254, 243, 199); // #FEF3C7
    private final Color COLOR_SIDEBAR = new Color(253, 251, 247);     // #FDFBF7
    private final Color COLOR_SURFACE = new Color(255, 251, 235);     // #FFFBEB
    private final Color COLOR_CARD = Color.WHITE;
    private final Color COLOR_TEXT = new Color(17, 24, 39);           // #111827
    private final Color COLOR_HEADING = new Color(69, 26, 3);         // #451A03
    private final Color COLOR_SUB_TEXT = new Color(107, 114, 128);    // #6B7280
    private final Color COLOR_DISABLED = new Color(243, 244, 246);    // #F3F4F6
    private final Color COLOR_ACCENT = new Color(180, 83, 9);         // #B45309
    private final Color SUCCESS = new Color(22, 163, 74);             // #16A34A
    private final Color ERROR = new Color(220, 38, 38);               // #DC2626

    private OrderPresenter presenter;

    private List<SanPhamDTO> localDanhSachSP = new ArrayList<>();
    private final List<CTDonHangDTO> localGioHangHienTai = new ArrayList<>();
    private final List<DonDatHangDTO> localDonTheoDoi = new ArrayList<>();
    private Map<Integer, String> localMapDanhMuc;

    private JTextField txtTimKiemSanPham, txtSoDienThoai, txtDiaChiGiao, txtTienCoc, txtTienKhachDua;
    private JButton btnLocTatCa, btnLocCake, btnLocCookie, btnLocBread;
    private String currentCategory = "ALL";
    private JPanel tileSanPham;
    private JTable tblGioHang;
    private DefaultTableModel cartTableModel;
    private JLabel lblTenKhachHang, lblThongBaoTab1, lblErrDiaChiGiao, lblErrNgayNhanBanh;
    private JLabel lblTongTienHang, lblTienGiamGia, lblTongThanhToan, lblMinDeposit, lblConLai, lblTienThua;
    private JComboBox<String> cbHinhThucNhan, cbGioNhanBanh, cbTrangThaiMoi;
    private SwingDatePicker dpNgayNhanBanh;
    private JCheckBox chkXacNhanThuTien;
    private JButton btnTaoDonHang, btnThanhToan, btnTimKhach;

    private JTextField txtMaDonTraCuu, txtKhachHangReadonly, txtTrangThaiHienTaiReadonly, txtTongTienReadonly;
    private JLabel lblErrTraCuu, lblErrTrangThaiMoi, lblThongBaoTab2;
    private JButton btnTraCuu, btnLuuCapNhat, btnTimTheoNgayGio;
    private SwingDatePicker dpNgayTheoDoi;
    private JComboBox<String> cbGioTheoDoi;
    private JTable tblDonTheoDoi;
    private DefaultTableModel theoDoiTableModel;

    private JPanel cardPanel;
    private CardLayout cardLayout;
    private JPanel navItemTaoDon, navItemTheoDoi;

    public OrderViewPanel() {
        setLayout(new BorderLayout());
        setBackground(COLOR_SURFACE);
        buildUI();
    }

    public void setPresenter(OrderPresenter presenter) {
        this.presenter = presenter;
        bindEvents();
    }

    private void bindEvents() {
        txtTimKiemSanPham.addKeyListener(new KeyAdapter() { @Override public void keyReleased(KeyEvent e) { apDungBoLoc(); } });
        btnTimKhach.addActionListener(e -> { if(presenter != null) presenter.timKhachHang(txtSoDienThoai.getText()); });
        cbHinhThucNhan.addActionListener(e -> {
            boolean isDatHang = "Đặt hàng".equals(cbHinhThucNhan.getSelectedItem());
            txtDiaChiGiao.setEnabled(isDatHang);
            if (!isDatHang) { txtDiaChiGiao.setText(""); lblErrDiaChiGiao.setText(""); }
        });

        DocumentListener calcListener = new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { if(presenter != null) presenter.capNhatGioHangVaTien(); }
            @Override public void removeUpdate(DocumentEvent e) { if(presenter != null) presenter.capNhatGioHangVaTien(); }
            @Override public void changedUpdate(DocumentEvent e) { if(presenter != null) presenter.capNhatGioHangVaTien(); }
        };
        txtTienCoc.getDocument().addDocumentListener(calcListener);
        txtTienKhachDua.getDocument().addDocumentListener(calcListener);
        chkXacNhanThuTien.addActionListener(e -> { if(presenter != null) presenter.capNhatGioHangVaTien(); });

        btnTaoDonHang.addActionListener(e -> { if(presenter != null) presenter.xuLyDatBanh(); });
        btnThanhToan.addActionListener(e -> {
            if (presenter == null) {
                return;
            }
            if (!presenter.kiemTraNgayNhanHopLeChoThanhToan()) {
                return;
            }
            moDialogXacNhanThanhToan();
        });
        /* btnThanhToan.addActionListener(e -> {
            if(presenter != null) {
                // 1. Lấy thông tin hiện tại
                double tongTien = getTongThanhToanHienTai();
                String maDon = txtMaDonTraCuu.getText().isEmpty() ? "TEMP" : txtMaDonTraCuu.getText();
                // Luong moi da hien thi QR trong PaymentConfirmationDialog.
                // Luong moi dung PaymentConfirmationDialog, khong luu DB tai listener cu.
            }
        }); */
        btnTraCuu.addActionListener(e -> { if(presenter != null) presenter.traCuuDonHang(txtMaDonTraCuu.getText()); });
        btnTimTheoNgayGio.addActionListener(e -> {
            if (presenter == null) return;
            String gioRaw = cbGioTheoDoi.getSelectedItem() == null ? "Tat ca" : cbGioTheoDoi.getSelectedItem().toString();
            LocalTime gioTheoDoi = "Tat ca".equalsIgnoreCase(gioRaw) ? null : LocalTime.parse(gioRaw);
            presenter.timKiemDonTheoDoi(dpNgayTheoDoi.getValue(), gioTheoDoi);
        });
        btnLuuCapNhat.addActionListener(e -> {
            if(presenter != null && cbTrangThaiMoi.getSelectedItem() != null)
                presenter.capNhatTrangThai(txtMaDonTraCuu.getText(), cbTrangThaiMoi.getSelectedItem().toString());
        });
        tblDonTheoDoi.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedRow = tblDonTheoDoi.getSelectedRow();
                if (selectedRow < 0 || selectedRow >= localDonTheoDoi.size()) return;
                DonDatHangDTO don = localDonTheoDoi.get(selectedRow);
                txtMaDonTraCuu.setText(String.valueOf(don.getMaDon()));
                txtKhachHangReadonly.setText(don.getMaKH() == null ? "Khach le" : "Ma KH: " + don.getMaKH());
                txtTrangThaiHienTaiReadonly.setText(don.getTenTrangThai() == null ? "" : don.getTenTrangThai());
                txtTongTienReadonly.setText(formatTien(don.getTongTienHDBan()));
                lblErrTraCuu.setText("");
            }
        });
    }

    public double getTienKhachDua() { return parseDoubleSafe(txtTienKhachDua.getText()); }
    public double getTienCoc() { return parseDoubleSafe(txtTienCoc.getText()); }
    public double getTongThanhToanHienTai() { return parseDoubleSafe(lblTongThanhToan.getText().replace(" đ", "").replace(",", "")); }
    public String getDiaChiGiao() { return txtDiaChiGiao.getText().trim(); }
    public Integer getHinhThucNhan() { return "Trực tiếp".equals(cbHinhThucNhan.getSelectedItem()) ? 1 : 2; }
    public boolean isXacNhanThuTien() { return chkXacNhanThuTien.isSelected(); }
    public String getTrangThaiHienTaiTraCuu() { return txtTrangThaiHienTaiReadonly.getText(); }

    public LocalDateTime getNgayGioNhanBanh() {
        LocalDate date = dpNgayNhanBanh.getValue();
        LocalTime time = LocalTime.parse(cbGioNhanBanh.getSelectedItem().toString());
        return LocalDateTime.of(date, time);
    }

    public void hienThiThongTinKhach(String text, boolean isVip) {
        lblTenKhachHang.setText(text);
        lblTenKhachHang.setForeground(isVip ? SUCCESS : COLOR_PRIMARY);
    }

    public void lamMoiBaoCaoTien(double tongHang, double giamGia, double tongThanhToan, double minCoc, double conLai, double tienThua, boolean isThieuTienThua) {
        lblTongTienHang.setText(formatTien(tongHang));
        lblTienGiamGia.setText(formatTien(giamGia));
        lblTongThanhToan.setText(formatTien(tongThanhToan));
        lblMinDeposit.setText(formatTien(minCoc));
        lblConLai.setText(formatTien(conLai));

        if (txtTienKhachDua.getText().trim().isEmpty()) {
            lblTienThua.setText("0 đ"); lblTienThua.setForeground(Color.BLACK);
        } else if (isThieuTienThua) {
            lblTienThua.setText("Thiếu " + formatTien(Math.abs(tienThua))); lblTienThua.setForeground(ERROR);
        } else {
            lblTienThua.setText(formatTien(tienThua)); lblTienThua.setForeground(COLOR_PRIMARY);
        }
    }

    public void lamMoiBangGioHang(List<CTDonHangDTO> items, List<SanPhamDTO> originData) {
        cartTableModel.setRowCount(0);
        localGioHangHienTai.clear();
        for (CTDonHangDTO item : items) {
            String tenSp = originData.stream().filter(sp -> sp.getMaSP() == item.getMaSP()).map(SanPhamDTO::getTenSP).findFirst().orElse("SP");
            cartTableModel.addRow(new Object[]{tenSp, item.getSoLuong(), formatTien(item.getDonGia()), formatTien(item.getDonGia() * item.getSoLuong())});

            CTDonHangDTO clone = new CTDonHangDTO();
            clone.setMaSP(item.getMaSP());
            clone.setSoLuong(item.getSoLuong());
            clone.setDonGia(item.getDonGia());
            localGioHangHienTai.add(clone);
        }
    }

    public void batTatNutThanhToan(boolean state) {
        btnTaoDonHang.setEnabled(state); btnThanhToan.setEnabled(state);
    }

    public void hienThiLoi(String msg) { lblThongBaoTab1.setForeground(ERROR); lblThongBaoTab1.setText(msg); }
    public void hienThiThanhCong(String msg) { lblThongBaoTab1.setForeground(SUCCESS); lblThongBaoTab1.setText(msg); }
    public void hienThiLoiTraCuu(String msg) { lblThongBaoTab2.setForeground(ERROR); lblThongBaoTab2.setText(msg); lblErrTraCuu.setText(msg); }
    public void hienThiThongBaoTraCuu(String msg) { lblThongBaoTab2.setForeground(SUCCESS); lblThongBaoTab2.setText(msg); lblErrTraCuu.setText(""); }

    public void hienThiKetQuaTraCuu(String kh, String tt, double tongTien) {
        if(kh != null) txtKhachHangReadonly.setText(kh);
        if(tt != null) txtTrangThaiHienTaiReadonly.setText(tt);
        if(tongTien >= 0) txtTongTienReadonly.setText(formatTien(tongTien));
    }

    @Override
    public void showOrderDetails(DonDatHangDTO order) {
        if (order == null) {
            return;
        }
        txtMaDonTraCuu.setText(String.valueOf(order.getMaDon()));
        txtKhachHangReadonly.setText(order.getMaKH() == null ? "Khách lẻ" : "Mã KH: " + order.getMaKH());
        txtTongTienReadonly.setText(formatTien(order.getTongTienHDBan()));
    }

    @Override
    public void showError(String msg) {
        hienThiLoi(msg);
    }

    public void lamMoiForm() {
        txtTienCoc.setText(""); txtTienKhachDua.setText(""); txtSoDienThoai.setText(""); txtDiaChiGiao.setText("");
        chkXacNhanThuTien.setSelected(false); lblThongBaoTab1.setText(""); lblErrDiaChiGiao.setText("");
        dpNgayNhanBanh.setSelectedDate(LocalDate.now(ZONE_VN));
        datGioNhanMacDinhTheoHienTai();
    }

    public void hienThiDanhSachSanPham(List<SanPhamDTO> ds, Map<Integer, String> dict) {
        this.localDanhSachSP = ds;
        this.localMapDanhMuc = dict;
        apDungBoLoc();
    }

    public void taiDanhSachTrangThai(List<String> list) {
        cbTrangThaiMoi.removeAllItems();
        list.forEach(cbTrangThaiMoi::addItem);
    }

    @Override
    public void hienThiDanhSachDonTheoDoi(List<DonDatHangDTO> dsDonTheoDoi) {
        theoDoiTableModel.setRowCount(0);
        localDonTheoDoi.clear();
        for (DonDatHangDTO don : dsDonTheoDoi) {
            localDonTheoDoi.add(don);
            String ngayGioNhan = don.getNgayGioNhanBanh() == null ? "" : don.getNgayGioNhanBanh().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
            theoDoiTableModel.addRow(new Object[]{
                    don.getMaDon(),
                    ngayGioNhan,
                    don.getMaKH() == null ? "Khach le" : ("Ma KH: " + don.getMaKH()),
                    don.getTenTrangThai(),
                    formatTien(don.getTongTienHDBan())
            });
        }
    }

    public void inPhieuHoaDon(String tieuDe, Integer maDon, Integer maHoaDon, LocalDateTime ngayLapHoaDon,
                              double tongTien, double daThu, List<CTDonHangDTO> cart, List<SanPhamDTO> data, double pGiam) {
        String maDonStr = (maDon != null && maDon > 0) ? "#" + maDon : "N/A";
        String maHoaDonStr = (maHoaDon != null && maHoaDon > 0) ? "#" + maHoaDon : "N/A";
        String ngayLapHoaDonStr = ngayLapHoaDon == null ? "N/A" : ngayLapHoaDon.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        String khachHang = lblTenKhachHang.getText();
        String tienGiamGia = (pGiam > 0) ? lblTienGiamGia.getText() : null;
        String tienKhachDua = txtTienKhachDua.getText().trim();
        if (tienKhachDua.isEmpty()) tienKhachDua = formatTien(daThu); else tienKhachDua = formatTien(parseDoubleSafe(tienKhachDua));
        
        double tienThuaNum = parseDoubleSafe(txtTienKhachDua.getText()) - daThu;
        String tienThua = (tienThuaNum > 0) ? formatTien(tienThuaNum) : null;

        UITBakeryReceipt receipt = new UITBakeryReceipt(
                tieuDe, maDonStr, maHoaDonStr, ngayLapHoaDonStr, khachHang, cart, data,
                tienGiamGia, formatTien(tongTien), formatTien(daThu), 
                tienKhachDua, tienThua
        );
        receipt.setVisible(true);
    }

    private void moDialogXacNhanThanhToan() {
        if (presenter == null) return;

        String orderId = taoMaThamChieuDonHang();
        double amount = getTongThanhToanHienTai();
        ImageIcon qrIcon = null;
        try {
            qrIcon = generateVietQR(amount, orderId);
            if (qrIcon != null) {
                // Resize QR to be smaller (200x200)
                qrIcon = new ImageIcon(qrIcon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH));
            }
        } catch (Exception ignored) {}

        PaymentConfirmationDialog dialog = new PaymentConfirmationDialog(
                SwingUtilities.getWindowAncestor(this),
                presenter,
                taoPanelHoaDonXacNhanV2(qrIcon),
                amount,
                orderId
        );
        dialog.setVisible(true);

        if (dialog.isSuccess()) {
            presenter.xuLySauKhiLuuDonThanhCong();
        }
    }

    private String taoMaThamChieuDonHang() {
        String maDonTraCuu = (txtMaDonTraCuu == null || txtMaDonTraCuu.getText().trim().isEmpty()) ? "" : txtMaDonTraCuu.getText().trim();
        if (!maDonTraCuu.isEmpty()) return maDonTraCuu;
        return "POS" + LocalDateTime.now(ZONE_VN).format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
    }

    private JPanel taoPanelHoaDonXacNhanV2(ImageIcon qrIcon) {
        String tieuDe = "XAC NHAN GIAO DICH";
        String maDonStr = "#" + taoMaThamChieuDonHang();
        String khachHang = lblTenKhachHang.getText();
        String tienGiamGia = lblTienGiamGia.getText();
        String tongTien = lblTongThanhToan.getText();
        String daThu = lblTongThanhToan.getText();
        String tienKhachDua = formatTien(getTienKhachDua());
        String tienThua = lblTienThua.getText();

        return UITBakeryReceipt.taoPanelHoaDon(
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
                qrIcon
        );
    }

    private void apDungBoLoc() {
        if (tileSanPham == null || txtTimKiemSanPham == null) return;
        String textSearch = txtTimKiemSanPham.getText().trim().toLowerCase();
        tileSanPham.removeAll();
        for (SanPhamDTO item : localDanhSachSP) {
            String categoryName = (localMapDanhMuc != null) ? localMapDanhMuc.getOrDefault(item.getMaDM(), "Khac") : "Khac";
            String normCat = java.text.Normalizer.normalize(categoryName, java.text.Normalizer.Form.NFD)
                    .replaceAll("\\p{M}", "").toUpperCase(Locale.ROOT);

            String finalCat = normCat.contains("CAKE") ? "Cake" : (normCat.contains("COOKIE") ? "Cookie" : (normCat.contains("BREAD") || normCat.contains("MI") ? "Bread" : "ALL"));

            boolean matchCategory = "ALL".equals(currentCategory) || currentCategory.equalsIgnoreCase(finalCat);
            boolean matchName = item.getTenSP().toLowerCase().contains(textSearch);

            if (matchCategory && matchName) {
                tileSanPham.add(createProductCard(item));
            }
        }
        tileSanPham.revalidate(); tileSanPham.repaint();
    }

    private void setActiveFilter(String cat, JButton btn) {
        currentCategory = cat;
        JButton[] btns = {btnLocTatCa, btnLocCake, btnLocCookie, btnLocBread};
        for (JButton b : btns) {
            b.setBackground(COLOR_CARD);
            b.setForeground(COLOR_TEXT);
        }
        btn.setBackground(COLOR_PRIMARY);
        btn.setForeground(Color.WHITE);
        apDungBoLoc();
    }

    private java.awt.Image tryLoadFromResource(String path) {
        if (path == null) return null;
        String resPath = path.startsWith("/") ? path : "/" + path;
        try (java.io.InputStream is = getClass().getResourceAsStream(resPath)) {
            if (is != null) {
                java.awt.Image image = javax.imageio.ImageIO.read(is);
                if (image != null) {
                    System.out.println("  -> Successfully loaded from: " + resPath);
                    return image;
                }
            }
        } catch (Exception ignored) {}
        return null;
    }

    private JPanel createProductCard(SanPhamDTO sp) {
        JPanel card = new JPanel(new BorderLayout());
        card.setPreferredSize(new Dimension(160, 200));
        card.setMaximumSize(new Dimension(160, 200));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setBackground(COLOR_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0,0,0,10), 1),
            new EmptyBorder(10, 10, 10, 10)
        ));

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
                String nameWithoutExt = fileName.contains(".") ? fileName.substring(0, fileName.lastIndexOf('.')) : fileName;

                // 1. Thử load từ Resources qua Stream (An toàn nhất cho Java Module)
                img = tryLoadFromResource(hinhAnhPath);
                if (img == null) img = tryLoadFromResource("/" + fileName);

                // 2. ƯU TIÊN MAPPING THEO MÃ SẢN PHẨM (MaSP)
                if (img == null) {
                    int maSP = sp.getMaSP();
                    img = tryLoadFromResource("/cake" + maSP + ".jpg");
                    if (img == null) img = tryLoadFromResource("/cake" + maSP + ".png");
                    if (img != null) System.out.println("  -> Successfully mapped to cake" + maSP + " based on MaSP.");
                }

                // 3. SMART FALLBACK: Nếu vẫn không thấy, thử tìm theo tên sản phẩm
                if (img == null) {
                    String tenSP = sp.getTenSP().toLowerCase();
                    String fallback = null;
                    if (tenSP.contains("vani")) fallback = "/cake1.jpg";
                    else if (tenSP.contains("socola")) fallback = "/cake2.png";
                    else if (tenSP.contains("velvet")) fallback = "/cake3.jpg";
                    else if (tenSP.contains("tiramisu")) fallback = "/cake4.jpg";
                    else if (tenSP.contains("cookie")) fallback = "/cake2.png";
                    else if (tenSP.contains("bread") || tenSP.contains("mi")) fallback = "/cake3.jpg";

                    if (fallback != null) {
                        img = tryLoadFromResource(fallback);
                    }
                }
            } catch (Exception e) {
                System.err.println("  -> Error: " + e.getMessage());
            }
        }
        if (img == null) System.out.println("  -> FAILED to load image for " + sp.getTenSP());

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

        lblName.setFont(new Font("Arial", Font.BOLD, 12));
        
        JLabel lblPrice = new JLabel(formatTien(sp.getGiaCoBan()), SwingConstants.CENTER);
        lblPrice.setForeground(COLOR_PRIMARY);
        lblPrice.setFont(new Font("Arial", Font.BOLD, 14));

        JPanel info = new JPanel(new GridLayout(2, 1));
        info.setOpaque(false);
        info.add(lblName);
        info.add(lblPrice);
        
        JButton btnThem = new JButton("Thêm");
        btnThem.setBackground(COLOR_ACCENT);
        btnThem.setForeground(Color.WHITE);
        btnThem.setFocusPainted(false);
        btnThem.setFont(new Font("Arial", Font.BOLD, 10));
        btnThem.setBorderPainted(false);
        btnThem.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnThem.addActionListener(e -> { if(presenter != null) presenter.themSanPhamVaoGio(sp); });

        card.add(imgLabel, BorderLayout.NORTH);
        card.add(info, BorderLayout.CENTER);
        card.add(btnThem, BorderLayout.SOUTH);
        return card;
    }

    private void buildUI() {
        add(createSidebar(), BorderLayout.WEST);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);
        centerPanel.add(createHeader(), BorderLayout.NORTH);
        
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setOpaque(false);
        
         cardPanel.add(createTabTaoDon(), "TAO_DON");
        cardPanel.add(createTabTheoDoiV2(), "THEO_DOI");
        
        centerPanel.add(cardPanel, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);
        
        switchTab("TAO_DON");
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(180, 0));
        sidebar.setBackground(COLOR_SIDEBAR);
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(new EmptyBorder(30, 14, 30, 14));

        JLabel logo = new JLabel("H3K Bakery");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 19));
        logo.setForeground(COLOR_PRIMARY);
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(logo);

        sidebar.add(Box.createVerticalStrut(40));

        navItemTaoDon = createNavItem("POS");
        navItemTheoDoi = createNavItem("Theo dõi");
        
        navItemTaoDon.setAlignmentX(Component.CENTER_ALIGNMENT);
        navItemTheoDoi.setAlignmentX(Component.CENTER_ALIGNMENT);

        navItemTaoDon.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { switchTab("TAO_DON"); }
        });
        navItemTheoDoi.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { switchTab("THEO_DOI"); }
        });

        sidebar.add(navItemTaoDon);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(navItemTheoDoi);

        return sidebar;
    }

    private JPanel createNavItem(String text) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 8));
        p.setOpaque(true);
        p.setBackground(COLOR_SIDEBAR);
        p.setMaximumSize(new Dimension(150, 40));
        p.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        l.setForeground(COLOR_SUB_TEXT);
        p.add(l);
        return p;
    }

    private void switchTab(String tabName) {
        cardLayout.show(cardPanel, tabName);
        boolean isTaoDon = "TAO_DON".equals(tabName);
        updateNavStyle(navItemTaoDon, isTaoDon);
        updateNavStyle(navItemTheoDoi, !isTaoDon);
    }

    private void updateNavStyle(JPanel p, boolean active) {
        p.setBackground(active ? COLOR_PRIMARY_LIGHT : COLOR_SIDEBAR);
        JLabel l = (JLabel) p.getComponent(0);
        l.setFont(new Font("Segoe UI", active ? Font.BOLD : Font.PLAIN, 13));
        l.setForeground(active ? COLOR_PRIMARY : COLOR_SUB_TEXT);
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(COLOR_CARD);
        header.setPreferredSize(new Dimension(0, 70));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_PRIMARY_LIGHT));
        
        JLabel title = new JLabel("POS - Bán hàng & Quản lý đơn");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(COLOR_HEADING);
        title.setBorder(new EmptyBorder(0, 25, 0, 0));
        
        header.add(title, BorderLayout.WEST);
        return header;
    }

    private JPanel createTabTaoDon() {
        JPanel dashboardBody = new JPanel(new GridBagLayout());
        dashboardBody.setOpaque(false);
        dashboardBody.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;

        gbc.gridx = 0; gbc.weightx = 0.65;
        dashboardBody.add(buildProductPanel(), gbc);

        gbc.gridx = 1; gbc.weightx = 0.35; gbc.insets = new Insets(0, 20, 0, 0);
        dashboardBody.add(buildCartPanel(), gbc);

        return dashboardBody;
    }

    private JPanel buildProductPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 20));
        panel.setOpaque(false);

        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filterBar.setOpaque(false);
        
        btnLocTatCa = new JButton("ALL");
        btnLocCake = new JButton("Cake");
        btnLocCookie = new JButton("Cookie");
        btnLocBread = new JButton("Bread");
        
        JButton[] filterBtns = {btnLocTatCa, btnLocCake, btnLocCookie, btnLocBread};
        String[] cats = {"ALL", "Cake", "Cookie", "Bread"};
        
        for(int i=0; i<filterBtns.length; i++) {
            JButton b = filterBtns[i];
            String c = cats[i];
            styleButton(b, COLOR_CARD, COLOR_TEXT);
            b.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(COLOR_PRIMARY_LIGHT),
                    new EmptyBorder(5, 15, 5, 15)
            ));
            b.addActionListener(e -> setActiveFilter(c, b));
            filterBar.add(b);
        }
        
        txtTimKiemSanPham = new JTextField(15);
        txtTimKiemSanPham.setPreferredSize(new Dimension(200, 30));
        styleInput(txtTimKiemSanPham);
        JPanel searchWrap = new JPanel(new BorderLayout(5, 0));
        searchWrap.setOpaque(false);
        JLabel lblSearch = new JLabel("Tìm kiếm:");
        lblSearch.setForeground(COLOR_TEXT);
        searchWrap.add(lblSearch, BorderLayout.WEST);
        searchWrap.add(txtTimKiemSanPham, BorderLayout.CENTER);
        filterBar.add(Box.createHorizontalStrut(20));
        filterBar.add(searchWrap);

        tileSanPham = new JPanel(new GridLayout(0, 3, 15, 15));
        tileSanPham.setOpaque(false);
        
        setActiveFilter("ALL", btnLocTatCa);

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

        panel.add(filterBar, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildCartPanel() {
        JPanel cart = new JPanel(new BorderLayout());
        cart.setBackground(COLOR_CARD);
        cart.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_PRIMARY_LIGHT),
            new EmptyBorder(20, 20, 20, 20)
        ));

        // 1. Form Khách hàng & Cài đặt giao hàng
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL; gbc.insets = new Insets(4, 4, 4, 4);

        txtSoDienThoai = new JTextField(10);
        styleInput(txtSoDienThoai);
        btnTimKhach = new JButton("Kiểm tra");
        styleButton(btnTimKhach, COLOR_PRIMARY, Color.WHITE);

        JPanel phonePanel = new JPanel(new BorderLayout(5, 0));
        phonePanel.setOpaque(false);
        phonePanel.add(txtSoDienThoai, BorderLayout.CENTER);
        phonePanel.add(btnTimKhach, BorderLayout.EAST);

        lblTenKhachHang = new JLabel("Khách vãng lai");
        lblTenKhachHang.setForeground(COLOR_PRIMARY);
        lblTenKhachHang.setFont(new Font("Segoe UI", Font.BOLD | Font.ITALIC, 13));

        cbHinhThucNhan = new JComboBox<>(new String[]{"Trực tiếp", "Đặt hàng"});
        txtDiaChiGiao = new JTextField();
        styleInput(txtDiaChiGiao);
        txtDiaChiGiao.setEnabled(false);
        txtDiaChiGiao.setBackground(COLOR_DISABLED);
        lblErrDiaChiGiao = new JLabel(); lblErrDiaChiGiao.setForeground(ERROR);

        dpNgayNhanBanh = new SwingDatePicker();
        cbGioNhanBanh = new JComboBox<>();
        for (int i = 0; i < 24; i++) {
            cbGioNhanBanh.addItem(String.format("%02d:00", i));
        }
        datGioNhanMacDinhTheoHienTai();

        JPanel timePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        timePanel.setOpaque(false);
        timePanel.add(dpNgayNhanBanh); timePanel.add(cbGioNhanBanh);
        lblErrNgayNhanBanh = new JLabel(); lblErrNgayNhanBanh.setForeground(ERROR);

        addGridRow(formPanel, gbc, 0, "SĐT khách:", phonePanel);
        addGridRow(formPanel, gbc, 1, "Tên khách:", lblTenKhachHang);
        addGridRow(formPanel, gbc, 2, "Nhận bánh:", cbHinhThucNhan);
        addGridRow(formPanel, gbc, 3, "Địa chỉ:", txtDiaChiGiao);
        gbc.gridy = 4; gbc.gridx = 1; formPanel.add(lblErrDiaChiGiao, gbc);
        // Moved Giờ ngày nhận to bottom

        // 2. Table Giỏ Hàng
        String[] columns = {"Sản phẩm", "SL", "Đơn giá", "Tổng"};
        cartTableModel = new DefaultTableModel(columns, 0) { @Override public boolean isCellEditable(int row, int column) { return false; } };
        tblGioHang = new JTable(cartTableModel); 
        styleTable(tblGioHang, 30);
        JScrollPane scrollTable = new JScrollPane(tblGioHang);
        scrollTable.setPreferredSize(new Dimension(0, 150));
        scrollTable.setBorder(BorderFactory.createLineBorder(COLOR_PRIMARY_LIGHT));

        JPanel tableControls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 5));
        tableControls.setOpaque(false);
        JButton btnNhapSL = new JButton("Nhập SL"); styleButton(btnNhapSL, COLOR_PRIMARY, Color.WHITE);
        JButton btnGiam = new JButton("SL (-)"); styleButton(btnGiam, COLOR_PRIMARY, Color.WHITE);
        JButton btnTang = new JButton("SL (+)"); styleButton(btnTang, COLOR_PRIMARY, Color.WHITE);
        JButton btnXoa = new JButton("Xóa"); styleButton(btnXoa, ERROR, Color.WHITE);

        btnNhapSL.addActionListener(e -> {
            int row = tblGioHang.getSelectedRow();
            if (row < 0) { hienThiLoi("Vui lòng chọn 1 sản phẩm"); return; }
            String input = JOptionPane.showInputDialog(this, "Nhập số lượng:", "Cập nhật SL", JOptionPane.QUESTION_MESSAGE);
            if (input != null && !input.trim().isEmpty()) {
                try {
                    int newQty = Integer.parseInt(input.trim());
                    if (newQty > 0) presenter.thayDoiSoLuongMon(row, newQty - localGioHangHienTai.get(row).getSoLuong());
                    else presenter.thayDoiSoLuongMon(row, 0);
                } catch (Exception ex) { hienThiLoi("Số lượng không hợp lệ!"); }
            }
        });
        btnGiam.addActionListener(e -> { if(presenter != null) presenter.thayDoiSoLuongMon(tblGioHang.getSelectedRow(), -1); });
        btnTang.addActionListener(e -> { if(presenter != null) presenter.thayDoiSoLuongMon(tblGioHang.getSelectedRow(), 1); });
        btnXoa.addActionListener(e -> { if(presenter != null) presenter.thayDoiSoLuongMon(tblGioHang.getSelectedRow(), 0); });

        tableControls.add(btnNhapSL); tableControls.add(btnGiam); tableControls.add(btnTang); tableControls.add(btnXoa);

        JPanel tableWrap = new JPanel(new BorderLayout());
        tableWrap.setOpaque(false);
        tableWrap.add(scrollTable, BorderLayout.CENTER);
        tableWrap.add(tableControls, BorderLayout.SOUTH);

        // 3. Payment Area
        JPanel bottomPanel = new JPanel(new GridBagLayout());
        bottomPanel.setOpaque(false);
        GridBagConstraints gbcPay = new GridBagConstraints();
        gbcPay.fill = GridBagConstraints.HORIZONTAL; gbcPay.insets = new Insets(8, 8, 8, 8);

        Font boldFont = new Font("Arial", Font.BOLD, 12);
        lblTongTienHang = new JLabel("0 đ"); lblTongTienHang.setFont(boldFont);
        lblTienGiamGia = new JLabel("0 đ"); lblTienGiamGia.setForeground(SUCCESS); lblTienGiamGia.setFont(boldFont);
        lblTongThanhToan = new JLabel("0 đ"); lblTongThanhToan.setFont(new Font("Arial", Font.BOLD, 18)); lblTongThanhToan.setForeground(ERROR);
        lblMinDeposit = new JLabel("0 đ"); lblMinDeposit.setFont(boldFont);
        lblConLai = new JLabel("0 đ"); lblConLai.setForeground(COLOR_PRIMARY); lblConLai.setFont(boldFont);
        lblTienThua = new JLabel("0 đ"); lblTienThua.setFont(boldFont);

        txtTienCoc = new JTextField("0"); txtTienKhachDua = new JTextField("0");
        styleInput(txtTienCoc); txtTienCoc.setFont(boldFont);
        styleInput(txtTienKhachDua); txtTienKhachDua.setFont(boldFont);
        chkXacNhanThuTien = new JCheckBox("Đã thu đủ tiền");
        chkXacNhanThuTien.setOpaque(false);
        chkXacNhanThuTien.setFont(new Font("Segoe UI", Font.BOLD, 12));

        int row = 0;
        addPaymentRow(bottomPanel, gbcPay, row++, "Tiền hàng:", lblTongTienHang, "Giảm giá:", lblTienGiamGia);
        addPaymentRow(bottomPanel, gbcPay, row++, "Cần trả:", lblTongThanhToan, "Cọc tối thiểu:", lblMinDeposit);
        addPaymentRow(bottomPanel, gbcPay, row++, "Khách đưa:", txtTienKhachDua, "Tiền thừa:", lblTienThua);
        addPaymentRow(bottomPanel, gbcPay, row++, "Khách cọc:", txtTienCoc, "Còn nợ:", lblConLai);
        gbcPay.gridy = row++; gbcPay.gridx = 0; gbcPay.gridwidth = 4; bottomPanel.add(chkXacNhanThuTien, gbcPay);

        btnTaoDonHang = new JButton("ĐẶT BÁNH");
        styleButton(btnTaoDonHang, COLOR_SURFACE, COLOR_PRIMARY);
        btnTaoDonHang.setBorder(BorderFactory.createLineBorder(COLOR_PRIMARY, 2));
        btnTaoDonHang.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnTaoDonHang.setPreferredSize(new Dimension(0, 40));
        
        btnThanhToan = new JButton("THANH TOÁN");
        styleButton(btnThanhToan, COLOR_ACCENT, Color.WHITE);
        btnThanhToan.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnThanhToan.setPreferredSize(new Dimension(0, 55));

        JPanel actionPanel = new JPanel(new BorderLayout(0, 8));
        actionPanel.setOpaque(false);
        actionPanel.add(btnTaoDonHang, BorderLayout.NORTH); 
        actionPanel.add(btnThanhToan, BorderLayout.CENTER);
        
        lblThongBaoTab1 = new JLabel("", SwingConstants.CENTER);
        lblThongBaoTab1.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblThongBaoTab1.setForeground(COLOR_SUB_TEXT);

        JPanel paymentWrap = new JPanel(new BorderLayout(0, 10));
        paymentWrap.setOpaque(false);
        paymentWrap.add(new JSeparator(), BorderLayout.NORTH);
        
        JPanel timeWrap = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        timeWrap.setOpaque(false);
        JLabel lblTime = new JLabel("Ngày/Giờ nhận:");
        lblTime.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTime.setForeground(COLOR_TEXT);
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

    private JScrollPane createTabTheoDoiV2() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        filterPanel.setOpaque(false);
        filterPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(COLOR_PRIMARY_LIGHT),
                "Tìm kiếm đơn theo ngày/giờ nhận",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 14),
                COLOR_PRIMARY
        ));
        dpNgayTheoDoi = new SwingDatePicker();
        dpNgayTheoDoi.setSelectedDate(LocalDate.now(ZONE_VN));
        cbGioTheoDoi = new JComboBox<>();
        cbGioTheoDoi.addItem("Tất cả");
        for (int i = 0; i < 24; i++) {
            cbGioTheoDoi.addItem(String.format("%02d:00", i));
        }
        btnTimTheoNgayGio = new JButton("Tìm");
        styleButton(btnTimTheoNgayGio, COLOR_PRIMARY, Color.WHITE);
        filterPanel.add(new JLabel("Ngày:"));
        filterPanel.add(dpNgayTheoDoi);
        filterPanel.add(new JLabel("Giờ:"));
        filterPanel.add(cbGioTheoDoi);
        filterPanel.add(btnTimTheoNgayGio);

        String[] columns = {"Mã đơn", "Ngày giờ nhận", "Khách hàng", "Trạng thái", "Tổng tiền"};
        theoDoiTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblDonTheoDoi = new JTable(theoDoiTableModel);
        styleTable(tblDonTheoDoi, 28);
        JScrollPane scrollDon = new JScrollPane(tblDonTheoDoi);
        scrollDon.setPreferredSize(new Dimension(0, 250));
        scrollDon.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(COLOR_PRIMARY_LIGHT),
                "Danh sách đơn chưa hoàn thành",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 14),
                COLOR_PRIMARY
        ));

        JPanel content = new JPanel(new GridBagLayout());
        content.setBackground(COLOR_CARD);
        content.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_PRIMARY_LIGHT),
                BorderFactory.createTitledBorder(null, "Cập nhật trạng thái đơn", TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 16), COLOR_PRIMARY)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        txtMaDonTraCuu = new JTextField(15);
        styleInput(txtMaDonTraCuu);
        btnTraCuu = new JButton("Tra cứu");
        styleButton(btnTraCuu, COLOR_PRIMARY, Color.WHITE);
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        searchPanel.setOpaque(false);
        searchPanel.add(txtMaDonTraCuu);
        searchPanel.add(btnTraCuu);

        lblErrTraCuu = new JLabel();
        lblErrTraCuu.setForeground(ERROR);
        txtKhachHangReadonly = new JTextField(20);
        txtKhachHangReadonly.setEditable(false);
        styleReadonlyField(txtKhachHangReadonly);
        txtTrangThaiHienTaiReadonly = new JTextField(20);
        txtTrangThaiHienTaiReadonly.setEditable(false);
        styleReadonlyField(txtTrangThaiHienTaiReadonly);
        txtTongTienReadonly = new JTextField(20);
        txtTongTienReadonly.setEditable(false);
        styleReadonlyField(txtTongTienReadonly);

        cbTrangThaiMoi = new JComboBox<>();
        btnLuuCapNhat = new JButton("Lưu cập nhật ✓");
        styleButton(btnLuuCapNhat, SUCCESS, Color.WHITE);
        JPanel updatePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        updatePanel.setOpaque(false);
        updatePanel.add(cbTrangThaiMoi);
        updatePanel.add(btnLuuCapNhat);

        lblErrTrangThaiMoi = new JLabel();
        lblErrTrangThaiMoi.setForeground(ERROR);
        lblThongBaoTab2 = new JLabel();
        lblThongBaoTab2.setForeground(SUCCESS);

        int row = 0;
        addGridRow(content, gbc, row++, "Mã đơn:", searchPanel);
        gbc.gridy = row++;
        gbc.gridx = 1;
        content.add(lblErrTraCuu, gbc);
        addGridRow(content, gbc, row++, "Khách hàng:", txtKhachHangReadonly);
        addGridRow(content, gbc, row++, "Trạng thái hiện tại:", txtTrangThaiHienTaiReadonly);
        addGridRow(content, gbc, row++, "Tổng tiền đơn:", txtTongTienReadonly);
        content.add(new JSeparator(), gbc);
        row++;
        addGridRow(content, gbc, row++, "Trạng thái mới:", updatePanel);
        gbc.gridy = row++;
        gbc.gridx = 1;
        content.add(lblErrTrangThaiMoi, gbc);
        gbc.gridy = row++;
        gbc.gridx = 1;
        content.add(lblThongBaoTab2, gbc);

        panel.add(filterPanel, BorderLayout.NORTH);
        panel.add(scrollDon, BorderLayout.CENTER);
        panel.add(content, BorderLayout.SOUTH);

        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        return scrollPane;
    }

    private void styleButton(AbstractButton btn, Color bg, Color fg) {
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setBorder(BorderFactory.createEmptyBorder(7, 14, 7, 14));
        btn.setBorderPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void styleInput(JTextField txt) {
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txt.setForeground(COLOR_TEXT);
        txt.setBackground(COLOR_CARD);
        txt.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_PRIMARY_LIGHT),
                new EmptyBorder(5, 8, 5, 8)
        ));
    }

    private void styleReadonlyField(JTextField txt) {
        styleInput(txt);
        txt.setForeground(COLOR_SUB_TEXT);
        txt.setBackground(COLOR_DISABLED);
        txt.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_PRIMARY_LIGHT),
                new EmptyBorder(5, 8, 5, 8)
        ));
    }

    private void styleTable(JTable table, int rowHeight) {
        table.setRowHeight(rowHeight);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setForeground(COLOR_TEXT);
        table.setSelectionBackground(COLOR_PRIMARY_LIGHT);
        table.setSelectionForeground(COLOR_TEXT);
        table.setGridColor(COLOR_PRIMARY_LIGHT);
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);
        JTableHeader header = table.getTableHeader();
        header.setBackground(COLOR_SURFACE);
        header.setForeground(COLOR_HEADING);
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setReorderingAllowed(false);
    }

    private void addGridRow(JPanel panel, GridBagConstraints gbc, int row, String labelText, Component comp) {
        gbc.gridy = row; gbc.gridx = 0; gbc.weightx = 0;
        JLabel lbl = new JLabel(labelText);
        lbl.setForeground(COLOR_TEXT);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        panel.add(lbl, gbc);
        gbc.gridx = 1; gbc.weightx = 1; panel.add(comp, gbc);
    }

    private void addPaymentRow(JPanel pnl, GridBagConstraints gbc, int row, String lbl1, Component comp1, String lbl2, Component comp2) {
        gbc.gridy = row;
        gbc.gridx = 0; gbc.weightx = 0; pnl.add(new JLabel("<html><span style='font-size:11px; font-weight:bold'>" + lbl1 + "</span></html>"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.5; pnl.add(comp1, gbc);
        gbc.gridx = 2; gbc.weightx = 0; pnl.add(new JLabel("<html><span style='font-size:11px; font-weight:bold'>" + lbl2 + "</span></html>"), gbc);
        gbc.gridx = 3; gbc.weightx = 0.5; pnl.add(comp2, gbc);
    }

    private void datGioNhanMacDinhTheoHienTai() {
        if (cbGioNhanBanh == null) return;
        int gioHienTai = LocalTime.now(ZONE_VN).getHour();
        String gioMacDinh = String.format("%02d:00", gioHienTai);
        cbGioNhanBanh.setSelectedItem(gioMacDinh);
    }

    private double parseDoubleSafe(String raw) {
        try {
            if (raw == null) return 0;
            return Double.parseDouble(raw.trim().replace(" đ", "").replaceAll("[,\\.\\s]", ""));
        } catch (Exception e) {
            return 0;
        }
    }
    private String formatTien(double val) { return String.format(Locale.US, "%,.0f đ", val); }

    // =========================================================
    // LỚP NỘI BỘ: CUSTOM SWING DATE PICKER
    // =========================================================
    class SwingDatePicker extends JPanel {
        private JTextField txtDate;
        private JButton btnPick;
        private JPopupMenu popup;
        private LocalDate selectedDate;

        public SwingDatePicker() {
            selectedDate = LocalDate.now(ZONE_VN);
            setLayout(new BorderLayout());

            txtDate = new JTextField(10);
            txtDate.setEditable(false);
            txtDate.setBackground(COLOR_CARD);
            txtDate.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            txtDate.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(COLOR_PRIMARY_LIGHT),
                    new EmptyBorder(4, 8, 4, 8)
            ));
            txtDate.setText(selectedDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

            btnPick = new JButton("📅");
            btnPick.setFocusPainted(false);
            btnPick.setBackground(COLOR_PRIMARY_LIGHT);
            btnPick.setForeground(COLOR_PRIMARY_DARK);
            btnPick.addActionListener(e -> showPopup());

            add(txtDate, BorderLayout.CENTER);
            add(btnPick, BorderLayout.EAST);
        }

        public LocalDate getValue() { return selectedDate; }

        public void setSelectedDate(LocalDate date) {
            this.selectedDate = date;
            txtDate.setText(date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            if (popup != null && popup.isVisible()) popup.setVisible(false);
        }

        private void showPopup() {
            popup = new JPopupMenu();
            popup.add(new DatePickerPane(this, selectedDate));
            popup.show(txtDate, 0, txtDate.getHeight());
        }
    }

    class DatePickerPane extends JPanel {
        private JLabel lblMonthYear;
        private JPanel pnlDays;
        private LocalDate currentMonth;
        private SwingDatePicker parent;

        public DatePickerPane(SwingDatePicker parent, LocalDate initDate) {
            this.parent = parent;
            this.currentMonth = initDate.withDayOfMonth(1);
            setLayout(new BorderLayout());
            setBackground(Color.WHITE);
            setBorder(new LineBorder(COLOR_PRIMARY, 1));

            JButton btnPrev = new JButton("<"); btnPrev.setBackground(Color.WHITE); btnPrev.setFocusPainted(false);
            JButton btnNext = new JButton(">"); btnNext.setBackground(Color.WHITE); btnNext.setFocusPainted(false);
            lblMonthYear = new JLabel("", SwingConstants.CENTER);
            lblMonthYear.setFont(new Font("Segoe UI", Font.BOLD, 12));
            lblMonthYear.setForeground(COLOR_PRIMARY);

            btnPrev.addActionListener(e -> { currentMonth = currentMonth.minusMonths(1); updateCalendar(); });
            btnNext.addActionListener(e -> { currentMonth = currentMonth.plusMonths(1); updateCalendar(); });

            JPanel pnlHeader = new JPanel(new BorderLayout());
            pnlHeader.setBackground(Color.WHITE);
            pnlHeader.add(btnPrev, BorderLayout.WEST);
            pnlHeader.add(lblMonthYear, BorderLayout.CENTER);
            pnlHeader.add(btnNext, BorderLayout.EAST);

            add(pnlHeader, BorderLayout.NORTH);

            pnlDays = new JPanel(new GridLayout(0, 7, 2, 2));
            pnlDays.setBackground(Color.WHITE);
            pnlDays.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            add(pnlDays, BorderLayout.CENTER);
            updateCalendar();
        }

        private void updateCalendar() {
            pnlDays.removeAll();
            lblMonthYear.setText(currentMonth.format(DateTimeFormatter.ofPattern("MM/yyyy")));
            String[] days = {"T2", "T3", "T4", "T5", "T6", "T7", "CN"};
            for (String d : days) {
                JLabel lbl = new JLabel(d, SwingConstants.CENTER);
                lbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
                lbl.setForeground(COLOR_TEXT);
                pnlDays.add(lbl);
            }

            int dayOfWeek = currentMonth.getDayOfWeek().getValue();
            int daysInMonth = currentMonth.lengthOfMonth();

            for (int i = 1; i < dayOfWeek; i++) pnlDays.add(new JLabel(""));
            for (int i = 1; i <= daysInMonth; i++) {
                int day = i;
                JButton btnDay = new JButton(String.valueOf(day));
                btnDay.setMargin(new Insets(2, 2, 2, 2));
                btnDay.setFocusPainted(false);

                if (currentMonth.withDayOfMonth(day).equals(LocalDate.now(ZONE_VN))) {
                    btnDay.setBackground(SUCCESS); btnDay.setForeground(Color.WHITE);
                } else {
                    btnDay.setBackground(COLOR_SURFACE); btnDay.setForeground(COLOR_TEXT);
                }

                btnDay.addActionListener(e -> parent.setSelectedDate(currentMonth.withDayOfMonth(day)));
                pnlDays.add(btnDay);
            }

            Window window = SwingUtilities.getWindowAncestor(this);
            if (window != null) window.pack();

            pnlDays.revalidate();
            pnlDays.repaint();
        }
    }
    // Thêm vào cuối lớp OrderViewPanel
    private ImageIcon generateVietQR(double amount, String orderId) {
        try {
            // Thông tin cấu hình (Thiếu chủ thay STK của mình vào đây)
            String bankId = "vcb";
            String accountNo = "1049423992"; // Số tài khoản VCB của thiếu chủ
            String accountName = "VIEN DANG KHOA"; // Tên không dấu

            String info = "DonHang_" + orderId;


            String urlString = String.format(
                    "https://img.vietqr.io/image/%s-%s-compact.jpg?amount=%.0f&addInfo=%s&accountName=%s",
                    bankId, accountNo, amount, info, accountName.replace(" ", "%20")
            );

            java.net.URL url = new java.net.URL(urlString);
            Image image = javax.imageio.ImageIO.read(url);

            // Resize ảnh cho vừa vặn với cửa sổ thông báo (ví dụ 300x300)
            return new ImageIcon(image.getScaledInstance(300, 300, Image.SCALE_SMOOTH));
        } catch (Exception e) {
            System.out.println("Lỗi tạo QR: " + e.getMessage());
            return null;
        }

    }
    public void hienThiPopupQR(double amount, String orderId) {
        ImageIcon qrIcon = generateVietQR(amount, orderId);
        if (qrIcon != null) {
            JLabel lblQR = new JLabel(qrIcon);
            JOptionPane.showMessageDialog(this, lblQR, "Quét mã thanh toán Vietcombank", JOptionPane.PLAIN_MESSAGE);
        } else {
            hienThiLoi("Không thể kết nối máy chủ VietQR!");
        }
    }
}
