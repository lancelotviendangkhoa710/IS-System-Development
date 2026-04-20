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
    // Bảng màu từ thiết kế Tailwind
    private final Color COLOR_PRIMARY = new Color(165, 54, 13);
    private final Color COLOR_SIDEBAR = new Color(246, 244, 236);
    private final Color COLOR_SURFACE = new Color(251, 249, 242);
    private final Color COLOR_CARD = Color.WHITE;
    private final Color COLOR_TEXT = new Color(27, 28, 24);
    private final Color COLOR_ACCENT = new Color(253, 174, 149);
    private final Color SUCCESS = new Color(39, 174, 96);
    private final Color ERROR = new Color(192, 57, 43);

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
        btnThanhToan.addActionListener(e -> moDialogXacNhanThanhToan());
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

    public void inPhieuHoaDon(String tieuDe, int maDon, double tongTien, double daThu, List<CTDonHangDTO> cart, List<SanPhamDTO> data, double pGiam) {
        String maDonStr = (maDon != -1) ? "#" + maDon : "N/A";
        String khachHang = lblTenKhachHang.getText();
        String tienGiamGia = (pGiam > 0) ? lblTienGiamGia.getText() : null;
        String tienKhachDua = txtTienKhachDua.getText().trim();
        if (tienKhachDua.isEmpty()) tienKhachDua = formatTien(daThu); else tienKhachDua = formatTien(parseDoubleSafe(tienKhachDua));
        
        double tienThuaNum = parseDoubleSafe(txtTienKhachDua.getText()) - daThu;
        String tienThua = (tienThuaNum > 0) ? formatTien(tienThuaNum) : null;

        UITBakeryReceipt receipt = new UITBakeryReceipt(
                tieuDe, maDonStr, khachHang, cart, data, 
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
            b.setBackground(Color.WHITE);
            b.setForeground(COLOR_TEXT);
        }
        btn.setBackground(COLOR_PRIMARY);
        btn.setForeground(Color.WHITE);
        apDungBoLoc();
    }

    private JPanel createProductCard(SanPhamDTO sp) {
        JPanel card = new JPanel(new BorderLayout());
        card.setPreferredSize(new Dimension(160, 180));
        card.setMaximumSize(new Dimension(160, 180));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setBackground(COLOR_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0,0,0,10), 1),
            new EmptyBorder(10, 10, 10, 10)
        ));

        JLabel imgPlaceholder = new JLabel("IMG", SwingConstants.CENTER);
        imgPlaceholder.setPreferredSize(new Dimension(0, 100));
        imgPlaceholder.setOpaque(true);
        imgPlaceholder.setBackground(COLOR_SURFACE);
        imgPlaceholder.setForeground(COLOR_PRIMARY);
        
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
        btnThem.setForeground(COLOR_PRIMARY);
        btnThem.setFocusPainted(false);
        btnThem.setBorderPainted(false);
        btnThem.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnThem.addActionListener(e -> { if(presenter != null) presenter.themSanPhamVaoGio(sp); });

        card.add(imgPlaceholder, BorderLayout.NORTH);
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
        sidebar.setPreferredSize(new Dimension(250, 0));
        sidebar.setBackground(COLOR_SIDEBAR);
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(new EmptyBorder(30, 20, 30, 20));

        JLabel logo = new JLabel("UIT Bakery");
        logo.setFont(new Font("Arial", Font.BOLD, 21));
        logo.setForeground(COLOR_PRIMARY);
        sidebar.add(logo);

        sidebar.add(Box.createVerticalStrut(40));

        navItemTaoDon = createNavItem("Tạo đơn & Thanh toán");
        navItemTheoDoi = createNavItem("Theo dõi & Cập nhật");
        
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
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        p.setOpaque(true);
        p.setBackground(COLOR_SIDEBAR);
        p.setMaximumSize(new Dimension(250, 45));
        p.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        JLabel l = new JLabel(text);
        l.setFont(new Font("Arial", Font.PLAIN, 14));
        l.setForeground(new Color(88, 66, 59));
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
        p.setBackground(active ? new Color(234, 232, 225) : COLOR_SIDEBAR);
        JLabel l = (JLabel) p.getComponent(0);
        l.setFont(new Font("Arial", active ? Font.BOLD : Font.PLAIN, 14));
        l.setForeground(active ? COLOR_PRIMARY : new Color(89, 66, 59));
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setPreferredSize(new Dimension(0, 70));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(224, 192, 182)));
        
        JLabel title = new JLabel("POS - Bán hàng & Quản lý đơn");
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setForeground(COLOR_PRIMARY);
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
            b.setFocusPainted(false);
            b.setBackground(Color.WHITE);
            b.setForeground(COLOR_TEXT);
            b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0,0,0,20)),
                new EmptyBorder(5, 15, 5, 15)
            ));
            b.addActionListener(e -> setActiveFilter(c, b));
            filterBar.add(b);
        }
        
        txtTimKiemSanPham = new JTextField(15);
        txtTimKiemSanPham.setPreferredSize(new Dimension(200, 30));
        JPanel searchWrap = new JPanel(new BorderLayout(5, 0));
        searchWrap.setOpaque(false);
        searchWrap.add(new JLabel("Tìm kiếm:"), BorderLayout.WEST);
        searchWrap.add(txtTimKiemSanPham, BorderLayout.CENTER);
        filterBar.add(Box.createHorizontalStrut(20));
        filterBar.add(searchWrap);

        tileSanPham = new JPanel(new GridLayout(0, 3, 15, 15));
        tileSanPham.setOpaque(false);
        
        setActiveFilter("ALL", btnLocTatCa);

        JScrollPane scrollPane = new JScrollPane(tileSanPham);
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
            BorderFactory.createLineBorder(new Color(0,0,0,20)),
            new EmptyBorder(20, 20, 20, 20)
        ));

        // 1. Form Khách hàng & Cài đặt giao hàng
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL; gbc.insets = new Insets(4, 4, 4, 4);

        txtSoDienThoai = new JTextField(10);
        btnTimKhach = new JButton("Kiểm tra");
        styleButton(btnTimKhach, COLOR_PRIMARY, Color.WHITE);

        JPanel phonePanel = new JPanel(new BorderLayout(5, 0));
        phonePanel.setOpaque(false);
        phonePanel.add(txtSoDienThoai, BorderLayout.CENTER);
        phonePanel.add(btnTimKhach, BorderLayout.EAST);

        lblTenKhachHang = new JLabel("Khách vãng lai");
        lblTenKhachHang.setForeground(COLOR_PRIMARY);
        lblTenKhachHang.setFont(new Font("Arial", Font.BOLD | Font.ITALIC, 13));

        cbHinhThucNhan = new JComboBox<>(new String[]{"Trực tiếp", "Đặt hàng"});
        txtDiaChiGiao = new JTextField(); txtDiaChiGiao.setEnabled(false);
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
        addGridRow(formPanel, gbc, 5, "Giờ ngày nhận:", timePanel);
        gbc.gridy = 6; gbc.gridx = 1; formPanel.add(lblErrNgayNhanBanh, gbc);

        // 2. Table Giỏ Hàng
        String[] columns = {"Sản phẩm", "SL", "Đơn giá", "Tổng"};
        cartTableModel = new DefaultTableModel(columns, 0) { @Override public boolean isCellEditable(int row, int column) { return false; } };
        tblGioHang = new JTable(cartTableModel); 
        tblGioHang.setRowHeight(30);
        tblGioHang.setFont(new Font("Arial", Font.PLAIN, 12));
        JScrollPane scrollTable = new JScrollPane(tblGioHang);
        scrollTable.setPreferredSize(new Dimension(0, 150));
        scrollTable.setBorder(BorderFactory.createLineBorder(new Color(0,0,0,20)));

        JPanel tableControls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 5));
        tableControls.setOpaque(false);
        JButton btnGiam = new JButton("SL (-)"); styleButton(btnGiam, Color.LIGHT_GRAY, COLOR_TEXT);
        JButton btnTang = new JButton("SL (+)"); styleButton(btnTang, Color.LIGHT_GRAY, COLOR_TEXT);
        JButton btnXoa = new JButton("Xóa"); styleButton(btnXoa, ERROR, Color.WHITE);

        btnGiam.addActionListener(e -> { if(presenter != null) presenter.thayDoiSoLuongMon(tblGioHang.getSelectedRow(), -1); });
        btnTang.addActionListener(e -> { if(presenter != null) presenter.thayDoiSoLuongMon(tblGioHang.getSelectedRow(), 1); });
        btnXoa.addActionListener(e -> { if(presenter != null) presenter.thayDoiSoLuongMon(tblGioHang.getSelectedRow(), 0); });

        tableControls.add(btnGiam); tableControls.add(btnTang); tableControls.add(btnXoa);

        JPanel tableWrap = new JPanel(new BorderLayout());
        tableWrap.setOpaque(false);
        tableWrap.add(scrollTable, BorderLayout.CENTER);
        tableWrap.add(tableControls, BorderLayout.SOUTH);

        // 3. Payment Area
        JPanel bottomPanel = new JPanel(new GridBagLayout());
        bottomPanel.setOpaque(false);
        GridBagConstraints gbcPay = new GridBagConstraints();
        gbcPay.fill = GridBagConstraints.HORIZONTAL; gbcPay.insets = new Insets(4, 5, 4, 5);

        lblTongTienHang = new JLabel("0 đ");
        lblTienGiamGia = new JLabel("0 đ"); lblTienGiamGia.setForeground(SUCCESS);
        lblTongThanhToan = new JLabel("0 đ"); lblTongThanhToan.setFont(new Font("Arial", Font.BOLD, 18)); lblTongThanhToan.setForeground(COLOR_PRIMARY);
        lblMinDeposit = new JLabel("0 đ");
        lblConLai = new JLabel("0 đ"); lblConLai.setForeground(COLOR_PRIMARY);
        lblTienThua = new JLabel("0 đ");

        txtTienCoc = new JTextField("0"); txtTienKhachDua = new JTextField("0");
        chkXacNhanThuTien = new JCheckBox("Đã thu đủ tiền");
        chkXacNhanThuTien.setOpaque(false); chkXacNhanThuTien.setFont(new Font("Arial", Font.BOLD, 12));

        int row = 0;
        addPaymentRow(bottomPanel, gbcPay, row++, "Tiền hàng:", lblTongTienHang, "Giảm giá:", lblTienGiamGia);
        addPaymentRow(bottomPanel, gbcPay, row++, "Cần trả:", lblTongThanhToan, "Cọc tối thiểu:", lblMinDeposit);
        addPaymentRow(bottomPanel, gbcPay, row++, "Khách đưa:", txtTienKhachDua, "Tiền thừa:", lblTienThua);
        addPaymentRow(bottomPanel, gbcPay, row++, "Khách cọc:", txtTienCoc, "Còn nợ:", lblConLai);
        gbcPay.gridy = row++; gbcPay.gridx = 0; gbcPay.gridwidth = 4; bottomPanel.add(chkXacNhanThuTien, gbcPay);

        btnTaoDonHang = new JButton("ĐẶT BÁNH");
        btnTaoDonHang.setBackground(COLOR_ACCENT); btnTaoDonHang.setForeground(COLOR_PRIMARY); btnTaoDonHang.setFont(new Font("Arial", Font.BOLD, 14));
        btnTaoDonHang.setPreferredSize(new Dimension(0, 45));
        
        btnThanhToan = new JButton("THANH TOÁN");
        btnThanhToan.setBackground(COLOR_PRIMARY); btnThanhToan.setForeground(Color.BLACK); btnThanhToan.setFont(new Font("Arial", Font.BOLD, 14));
        btnThanhToan.setPreferredSize(new Dimension(0, 45));

        JPanel actionPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        actionPanel.setOpaque(false);
        actionPanel.add(btnTaoDonHang); actionPanel.add(btnThanhToan);
        
        lblThongBaoTab1 = new JLabel("", SwingConstants.CENTER); lblThongBaoTab1.setFont(new Font("Arial", Font.ITALIC, 12));

        JPanel paymentWrap = new JPanel(new BorderLayout(0, 10));
        paymentWrap.setOpaque(false);
        paymentWrap.add(new JSeparator(), BorderLayout.NORTH);
        paymentWrap.add(bottomPanel, BorderLayout.CENTER);
        
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

    private JPanel createTabTheoDoi() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel content = new JPanel(new GridBagLayout());
        content.setBackground(COLOR_CARD);
        content.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0,0,0,20)),
            BorderFactory.createTitledBorder(null, "Cập nhật trạng thái đơn", TitledBorder.LEFT, TitledBorder.TOP, new Font("Arial", Font.BOLD, 16), COLOR_PRIMARY)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL; gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        txtMaDonTraCuu = new JTextField(15);
        btnTraCuu = new JButton("Tra cứu"); styleButton(btnTraCuu, COLOR_PRIMARY, Color.WHITE);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        searchPanel.setOpaque(false);
        searchPanel.add(txtMaDonTraCuu); searchPanel.add(btnTraCuu);

        lblErrTraCuu = new JLabel(); lblErrTraCuu.setForeground(ERROR);
        txtKhachHangReadonly = new JTextField(20); txtKhachHangReadonly.setEditable(false); txtKhachHangReadonly.setBackground(COLOR_SURFACE);
        txtTrangThaiHienTaiReadonly = new JTextField(20); txtTrangThaiHienTaiReadonly.setEditable(false); txtTrangThaiHienTaiReadonly.setBackground(COLOR_SURFACE);
        txtTongTienReadonly = new JTextField(20); txtTongTienReadonly.setEditable(false); txtTongTienReadonly.setBackground(COLOR_SURFACE);

        cbTrangThaiMoi = new JComboBox<>();
        btnLuuCapNhat = new JButton("Lưu cập nhật"); styleButton(btnLuuCapNhat, COLOR_PRIMARY, Color.WHITE);

        JPanel updatePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        updatePanel.setOpaque(false);
        updatePanel.add(cbTrangThaiMoi); updatePanel.add(btnLuuCapNhat);

        lblErrTrangThaiMoi = new JLabel(); lblErrTrangThaiMoi.setForeground(ERROR);
        lblThongBaoTab2 = new JLabel(); lblThongBaoTab2.setForeground(SUCCESS);

        int row = 0;
        addGridRow(content, gbc, row++, "Mã đơn:", searchPanel);
        gbc.gridy = row++; gbc.gridx = 1; content.add(lblErrTraCuu, gbc);
        addGridRow(content, gbc, row++, "Khách hàng:", txtKhachHangReadonly);
        addGridRow(content, gbc, row++, "Trạng thái hiện tại:", txtTrangThaiHienTaiReadonly);
        addGridRow(content, gbc, row++, "Tổng tiền đơn:", txtTongTienReadonly);
        content.add(new JSeparator(), gbc); row++;
        addGridRow(content, gbc, row++, "Trạng thái mới:", updatePanel);
        gbc.gridy = row++; gbc.gridx = 1; content.add(lblErrTrangThaiMoi, gbc);
        gbc.gridy = row++; gbc.gridx = 1; content.add(lblThongBaoTab2, gbc);

        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setOpaque(false);
        wrap.add(content, BorderLayout.NORTH);
        panel.add(wrap, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createTabTheoDoiV2() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        filterPanel.setOpaque(false);
        filterPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(0, 0, 0, 20)),
                "Tim kiem don theo ngay/gio nhan",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 14),
                COLOR_PRIMARY
        ));
        dpNgayTheoDoi = new SwingDatePicker();
        dpNgayTheoDoi.setSelectedDate(LocalDate.now(ZONE_VN));
        cbGioTheoDoi = new JComboBox<>();
        cbGioTheoDoi.addItem("Tat ca");
        for (int i = 0; i < 24; i++) {
            cbGioTheoDoi.addItem(String.format("%02d:00", i));
        }
        btnTimTheoNgayGio = new JButton("Tim");
        styleButton(btnTimTheoNgayGio, COLOR_PRIMARY, Color.WHITE);
        filterPanel.add(new JLabel("Ngay:"));
        filterPanel.add(dpNgayTheoDoi);
        filterPanel.add(new JLabel("Gio:"));
        filterPanel.add(cbGioTheoDoi);
        filterPanel.add(btnTimTheoNgayGio);

        String[] columns = {"Ma don", "Ngay gio nhan", "Khach hang", "Trang thai", "Tong tien"};
        theoDoiTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblDonTheoDoi = new JTable(theoDoiTableModel);
        tblDonTheoDoi.setRowHeight(28);
        JScrollPane scrollDon = new JScrollPane(tblDonTheoDoi);
        scrollDon.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(0, 0, 0, 20)),
                "Danh sach don chua hoan thanh",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 14),
                COLOR_PRIMARY
        ));

        JPanel content = new JPanel(new GridBagLayout());
        content.setBackground(COLOR_CARD);
        content.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 0, 0, 20)),
                BorderFactory.createTitledBorder(null, "Cap nhat trang thai don", TitledBorder.LEFT, TitledBorder.TOP, new Font("Arial", Font.BOLD, 16), COLOR_PRIMARY)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        txtMaDonTraCuu = new JTextField(15);
        btnTraCuu = new JButton("Tra cuu");
        styleButton(btnTraCuu, COLOR_PRIMARY, Color.WHITE);
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        searchPanel.setOpaque(false);
        searchPanel.add(txtMaDonTraCuu);
        searchPanel.add(btnTraCuu);

        lblErrTraCuu = new JLabel();
        lblErrTraCuu.setForeground(ERROR);
        txtKhachHangReadonly = new JTextField(20);
        txtKhachHangReadonly.setEditable(false);
        txtKhachHangReadonly.setBackground(COLOR_SURFACE);
        txtTrangThaiHienTaiReadonly = new JTextField(20);
        txtTrangThaiHienTaiReadonly.setEditable(false);
        txtTrangThaiHienTaiReadonly.setBackground(COLOR_SURFACE);
        txtTongTienReadonly = new JTextField(20);
        txtTongTienReadonly.setEditable(false);
        txtTongTienReadonly.setBackground(COLOR_SURFACE);

        cbTrangThaiMoi = new JComboBox<>();
        btnLuuCapNhat = new JButton("Luu cap nhat");
        styleButton(btnLuuCapNhat, COLOR_PRIMARY, Color.WHITE);
        JPanel updatePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        updatePanel.setOpaque(false);
        updatePanel.add(cbTrangThaiMoi);
        updatePanel.add(btnLuuCapNhat);

        lblErrTrangThaiMoi = new JLabel();
        lblErrTrangThaiMoi.setForeground(ERROR);
        lblThongBaoTab2 = new JLabel();
        lblThongBaoTab2.setForeground(SUCCESS);

        int row = 0;
        addGridRow(content, gbc, row++, "Ma don:", searchPanel);
        gbc.gridy = row++;
        gbc.gridx = 1;
        content.add(lblErrTraCuu, gbc);
        addGridRow(content, gbc, row++, "Khach hang:", txtKhachHangReadonly);
        addGridRow(content, gbc, row++, "Trang thai hien tai:", txtTrangThaiHienTaiReadonly);
        addGridRow(content, gbc, row++, "Tong tien don:", txtTongTienReadonly);
        content.add(new JSeparator(), gbc);
        row++;
        addGridRow(content, gbc, row++, "Trang thai moi:", updatePanel);
        gbc.gridy = row++;
        gbc.gridx = 1;
        content.add(lblErrTrangThaiMoi, gbc);
        gbc.gridy = row++;
        gbc.gridx = 1;
        content.add(lblThongBaoTab2, gbc);

        panel.add(filterPanel, BorderLayout.NORTH);
        panel.add(scrollDon, BorderLayout.CENTER);
        panel.add(content, BorderLayout.SOUTH);
        return panel;
    }

    private void styleButton(AbstractButton btn, Color bg, Color fg) {
        btn.setBackground(bg); btn.setForeground(fg); btn.setFocusPainted(false); btn.setOpaque(true); btn.setBorderPainted(false); btn.setFont(new Font("Arial", Font.BOLD, 12)); btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void addGridRow(JPanel panel, GridBagConstraints gbc, int row, String labelText, Component comp) {
        gbc.gridy = row; gbc.gridx = 0; gbc.weightx = 0;
        JLabel lbl = new JLabel(labelText); lbl.setForeground(COLOR_TEXT); lbl.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(lbl, gbc);
        gbc.gridx = 1; gbc.weightx = 1; panel.add(comp, gbc);
    }

    private void addPaymentRow(JPanel pnl, GridBagConstraints gbc, int row, String lbl1, Component comp1, String lbl2, Component comp2) {
        gbc.gridy = row;
        gbc.gridx = 0; gbc.weightx = 0; pnl.add(new JLabel("<html><span style='font-size:10px'>" + lbl1 + "</span></html>"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.5; pnl.add(comp1, gbc);
        gbc.gridx = 2; gbc.weightx = 0; pnl.add(new JLabel("<html><span style='font-size:10px'>" + lbl2 + "</span></html>"), gbc);
        gbc.gridx = 3; gbc.weightx = 0.5; pnl.add(comp2, gbc);
    }

    private void datGioNhanMacDinhTheoHienTai() {
        if (cbGioNhanBanh == null) return;
        int gioHienTai = LocalTime.now(ZONE_VN).getHour();
        String gioMacDinh = String.format("%02d:00", gioHienTai);
        cbGioNhanBanh.setSelectedItem(gioMacDinh);
    }

    private double parseDoubleSafe(String raw) { try { return Double.parseDouble(raw.trim()); } catch (Exception e) { return 0; } }
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
            txtDate.setBackground(Color.WHITE);
            txtDate.setFont(new Font("Arial", Font.PLAIN, 13));
            txtDate.setText(selectedDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

            btnPick = new JButton("📅");
            btnPick.setFocusPainted(false);
            btnPick.setBackground(Color.LIGHT_GRAY);
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
            lblMonthYear.setFont(new Font("Arial", Font.BOLD, 12));
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
                lbl.setFont(new Font("Arial", Font.BOLD, 10));
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
