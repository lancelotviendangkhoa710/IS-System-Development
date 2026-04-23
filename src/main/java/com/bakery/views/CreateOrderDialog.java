package com.bakery.views;

import com.bakery.utils.QRGenerator;
import com.formdev.flatlaf.FlatClientProperties;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Locale;

/**
 * Wizard 2 bước tạo đơn hàng mới:
 * Bước 1: Xác định khách hàng
 * Bước 2: Chọn loại đơn (Thanh toán ngay / Đặt trước)
 */
public class CreateOrderDialog extends JDialog {

    // --- Màu sắc ---
    private static final Color COLOR_PRIMARY = new Color(0x92400E);
    private static final Color COLOR_SUCCESS = new Color(0x16A34A);
    private static final Color COLOR_DANGER  = new Color(0xDC2626);
    private static final Color COLOR_BG      = new Color(0xFDFBF7);
    private static final Color COLOR_SURFACE = Color.WHITE;
    private static final Color COLOR_MUTED   = new Color(0x6B7280);

    // --- Kết quả trả về ---
    public enum OrderType { IMMEDIATE, PREORDER }

    public static class OrderRequest {
        public boolean confirmed = false;
        // Khách hàng
        public Integer maKH = null;
        public String tenKhach = "Khách vãng lai";
        public String soDienThoai = "";
        // Loại đơn
        public OrderType orderType;
        // Thanh toán ngay
        public String hinhThucThanhToan; // "Tiền mặt" | "Chuyển khoản"
        public double soTienKhachDua = 0;
        // Đặt trước
        public LocalDateTime ngayGioNhan;
        public String diaChiGiao = "";
        public double tienCoc = 0; // 0 = full payment
    }

    // --- State ---
    private final OrderRequest result = new OrderRequest();
    private final double tongTienPhaiTra;


    // Callback để tra cứu KH từ presenter
    public interface CustomerLookup {
        /** Trả về [maKH, tenKH] nếu tìm thấy, null nếu không */
        String[] lookup(String sdt);
    }
    private final CustomerLookup customerLookup;

    // --- Bước hiện tại ---

    private CardLayout cardLayout;
    private JPanel cardPanel;

    // Bước 1
    private JTextField txtSDT;
    private JLabel lblKhachInfo;
    private JButton btnTimKhach;

    // Bước 2 — chọn loại
    private JToggleButton btnImmediateFlow, btnPreorderFlow;

    // Bước 2A — Thanh toán ngay
    private JToggleButton btnCash, btnTransfer;
    private JTextField txtKhachDua;
    private JLabel lblTienThua;
    private JPanel panelQR;

    // Bước 2B — Đặt trước
    private JSpinner spNgayGiao;       // Date picker
    private JComboBox<String> cbGioGiao; // Time picker
    private JTextField txtDiaChiGiao;
    private JToggleButton btnFullPay, btnDeposit;
    private JTextField txtTienCoc;
    private JLabel lblCocToiThieu;

    private JButton btnNext, btnBack, btnConfirm;
    private JLabel lblStep;

    public CreateOrderDialog(Window owner, double tongTienPhaiTra,
                             CustomerLookup customerLookup) {
        super(owner, "Tạo Đơn Hàng", ModalityType.APPLICATION_MODAL);
        this.tongTienPhaiTra = tongTienPhaiTra;
        this.customerLookup = customerLookup;

        buildUI();
        pack();
        setMinimumSize(new Dimension(520, 420));
        setLocationRelativeTo(owner);
    }

    private void buildUI() {
        setBackground(COLOR_BG);
        getContentPane().setBackground(COLOR_BG);
        setLayout(new BorderLayout(0, 0));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(COLOR_PRIMARY);
        header.setBorder(new EmptyBorder(16, 20, 16, 20));
        JLabel lblTitle = new JLabel("🛒 Tạo đơn hàng mới");
        lblTitle.setForeground(Color.WHITE);
        lblTitle.putClientProperty(FlatClientProperties.STYLE, "font: bold 18 $defaultFont;");
        lblStep = new JLabel("Bước 1 / 2");
        lblStep.setForeground(new Color(0xFDE68A));
        lblStep.putClientProperty(FlatClientProperties.STYLE, "font: 12 $defaultFont;");
        header.add(lblTitle, BorderLayout.WEST);
        header.add(lblStep, BorderLayout.EAST);

        // Cards
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setBackground(COLOR_BG);
        cardPanel.add(buildStep1Panel(), "step1");
        cardPanel.add(buildStep2Panel(), "step2");

        // Footer
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        footer.setBackground(COLOR_BG);
        footer.setBorder(new EmptyBorder(0, 0, 5, 0));

        btnBack = styleButton(new JButton("← Quay lại"), COLOR_SURFACE, COLOR_PRIMARY);
        btnBack.setEnabled(false);
        btnNext = styleButton(new JButton("Tiếp theo →"), COLOR_PRIMARY, Color.WHITE);
        btnConfirm = styleButton(new JButton("✓ Xác nhận"), COLOR_SUCCESS, Color.WHITE);
        btnConfirm.setVisible(false);

        btnBack.addActionListener(e -> goBack());
        btnNext.addActionListener(e -> goNext());
        btnConfirm.addActionListener(e -> confirm());

        footer.add(btnBack);
        footer.add(btnNext);
        footer.add(btnConfirm);

        add(header, BorderLayout.NORTH);
        add(cardPanel, BorderLayout.CENTER);
        add(footer, BorderLayout.SOUTH);
    }

    // =========================================================
    // BƯỚC 1: KHÁCH HÀNG
    // =========================================================
    private JPanel buildStep1Panel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(COLOR_BG);
        panel.setBorder(new EmptyBorder(24, 32, 24, 32));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 6, 6, 6);
        g.fill = GridBagConstraints.HORIZONTAL;

        addLabel(panel, g, 0, "Số điện thoại khách:");
        txtSDT = new JTextField(16);
        styleInput(txtSDT, "Nhập SĐT để tìm thành viên...");
        btnTimKhach = styleButton(new JButton("🔍 Tìm"), COLOR_PRIMARY, Color.WHITE);
        btnTimKhach.addActionListener(e -> lookupCustomer());
        txtSDT.addActionListener(e -> lookupCustomer());

        JPanel sdtRow = new JPanel(new BorderLayout(6, 0));
        sdtRow.setOpaque(false);
        sdtRow.add(txtSDT, BorderLayout.CENTER);
        sdtRow.add(btnTimKhach, BorderLayout.EAST);
        addField(panel, g, 0, sdtRow);

        addLabel(panel, g, 1, "Khách hàng:");
        lblKhachInfo = new JLabel("Chưa xác định — sẽ tạo đơn vãng lai");
        lblKhachInfo.setForeground(COLOR_MUTED);
        lblKhachInfo.putClientProperty(FlatClientProperties.STYLE, "font: italic 13 $defaultFont;");
        addField(panel, g, 1, lblKhachInfo);

        // Ghi chú
        JLabel note = new JLabel("<html><i style='color:gray'>Bỏ qua SĐT → đặt hàng vãng lai. Có SĐT → áp dụng ưu đãi thành viên (-10%).</i></html>");
        g.gridy = 2; g.gridx = 0; g.gridwidth = 2;
        panel.add(note, g);

        return panel;
    }

    private void lookupCustomer() {
        String sdt = txtSDT.getText().trim();
        if (sdt.isEmpty()) {
            result.maKH = null;
            result.tenKhach = "Khách vãng lai";
            result.soDienThoai = "";
            lblKhachInfo.setText("Không có SĐT — đơn vãng lai");
            lblKhachInfo.setForeground(COLOR_MUTED);
            return;
        }
        String[] info = customerLookup.lookup(sdt);
        if (info != null) {
            result.maKH = Integer.parseInt(info[0]);
            result.tenKhach = info[1];
            result.soDienThoai = sdt;
            lblKhachInfo.setText("✓ " + info[1] + " — Thành viên (giảm 10%)");
            lblKhachInfo.setForeground(COLOR_SUCCESS);
        } else {
            result.maKH = null;
            result.tenKhach = "Khách chưa đăng ký";
            result.soDienThoai = sdt;
            lblKhachInfo.setText("Không tìm thấy — đặt hàng vãng lai");
            lblKhachInfo.setForeground(COLOR_DANGER);
        }
    }

    // =========================================================
    // BƯỚC 2: LOẠI ĐƠN + THANH TOÁN
    // =========================================================
    private JPanel buildStep2Panel() {
        JPanel outer = new JPanel(new BorderLayout(0, 12));
        outer.setBackground(COLOR_BG);
        outer.setBorder(new EmptyBorder(16, 24, 16, 24));

        // Toggle chọn loại đơn
        ButtonGroup bgType = new ButtonGroup();
        btnImmediateFlow = buildToggle("💵 Thanh toán ngay", bgType);
        btnPreorderFlow  = buildToggle("📅 Đặt trước (Preorder)", bgType);
        btnImmediateFlow.setSelected(true);

        JPanel typeRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        typeRow.setOpaque(false);
        typeRow.add(btnImmediateFlow);
        typeRow.add(btnPreorderFlow);

        // Inner cards
        CardLayout innerCard = new CardLayout();
        JPanel innerPanel = new JPanel(innerCard);
        innerPanel.setBackground(COLOR_BG);
        innerPanel.add(buildImmediatePanel(), "immediate");
        innerPanel.add(buildPreorderPanel(), "preorder");

        btnImmediateFlow.addActionListener(e -> innerCard.show(innerPanel, "immediate"));
        btnPreorderFlow.addActionListener(e -> innerCard.show(innerPanel, "preorder"));

        // Tóm tắt tiền
        String tongStr = String.format(Locale.US, "%,.0f đ", tongTienPhaiTra);
        JLabel lblSum = new JLabel("Tổng cần trả: " + tongStr);
        lblSum.putClientProperty(FlatClientProperties.STYLE, "font: bold 15 $defaultFont;");
        lblSum.setForeground(COLOR_DANGER);

        outer.add(lblSum, BorderLayout.NORTH);
        outer.add(typeRow, BorderLayout.NORTH); // override with combined
        JPanel topArea = new JPanel(new BorderLayout(0, 8));
        topArea.setOpaque(false);
        topArea.add(lblSum, BorderLayout.NORTH);
        topArea.add(typeRow, BorderLayout.SOUTH);
        outer.add(topArea, BorderLayout.NORTH);
        outer.add(innerPanel, BorderLayout.CENTER);

        return outer;
    }

    // Panel 2A: Thanh toán ngay
    private JPanel buildImmediatePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(COLOR_SURFACE);
        panel.putClientProperty(FlatClientProperties.STYLE, "arc: 10;");
        panel.setBorder(new EmptyBorder(16, 20, 16, 20));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 6, 6, 6);
        g.fill = GridBagConstraints.HORIZONTAL;

        // Hình thức thanh toán
        addLabel(panel, g, 0, "Hình thức:");
        ButtonGroup bg = new ButtonGroup();
        btnCash     = buildToggle("💵 Tiền mặt", bg);
        btnTransfer = buildToggle("🏦 Chuyển khoản", bg);
        btnCash.setSelected(true);
        JPanel htRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        htRow.setOpaque(false);
        htRow.add(btnCash); htRow.add(btnTransfer);
        addField(panel, g, 0, htRow);

        // Tiền khách đưa (hiện cho tiền mặt)
        addLabel(panel, g, 1, "Khách đưa:");
        txtKhachDua = new JTextField("0", 12);
        styleInput(txtKhachDua, "Số tiền khách đưa...");
        addField(panel, g, 1, txtKhachDua);

        addLabel(panel, g, 2, "Tiền thừa:");
        lblTienThua = new JLabel("0 đ");
        lblTienThua.putClientProperty(FlatClientProperties.STYLE, "font: bold 14 $defaultFont;");
        addField(panel, g, 2, lblTienThua);

        // QR panel (ẩn lúc đầu)
        panelQR = new JPanel(new BorderLayout());
        panelQR.setOpaque(false);
        panelQR.setVisible(false);
        g.gridy = 3; g.gridx = 0; g.gridwidth = 2;
        panel.add(panelQR, g);

        // Logic toggle hiện/ẩn input tiền mặt
        btnCash.addActionListener(e -> {
            txtKhachDua.setEnabled(true);
            lblTienThua.setVisible(true);
            panelQR.setVisible(false);
        });
        btnTransfer.addActionListener(e -> {
            txtKhachDua.setEnabled(false);
            lblTienThua.setVisible(false);
            loadQR(panelQR, tongTienPhaiTra);
            panelQR.setVisible(true);
            pack();
        });

        // Tính tiền thừa khi gõ
        txtKhachDua.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { calcTienThua(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { calcTienThua(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) {}
            private void calcTienThua() {
                try {
                    double khachDua = Double.parseDouble(txtKhachDua.getText().replaceAll("[^0-9]", ""));
                    double thua = khachDua - tongTienPhaiTra;
                    if (thua >= 0) {
                        lblTienThua.setText(fmt(thua));
                        lblTienThua.setForeground(COLOR_SUCCESS);
                    } else {
                        lblTienThua.setText("Thiếu " + fmt(-thua));
                        lblTienThua.setForeground(COLOR_DANGER);
                    }
                } catch (Exception ex) {
                    lblTienThua.setText("0 đ");
                }
            }
        });

        return panel;
    }

    // Panel 2B: Đặt trước
    private JPanel buildPreorderPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(COLOR_SURFACE);
        panel.setBorder(new EmptyBorder(16, 20, 16, 20));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 6, 6, 6);
        g.fill = GridBagConstraints.HORIZONTAL;

        // --- Ngày nhận: JSpinner với SpinnerDateModel ---
        addLabel(panel, g, 0, "Ngày nhận bánh:");
        java.util.Calendar calMin = java.util.Calendar.getInstance();
        calMin.add(java.util.Calendar.DAY_OF_MONTH, 1); // Tối thiểu ngày mai
        spNgayGiao = new JSpinner(new SpinnerDateModel(
                calMin.getTime(), calMin.getTime(), null, java.util.Calendar.DAY_OF_MONTH));
        spNgayGiao.setEditor(new JSpinner.DateEditor(spNgayGiao, "dd/MM/yyyy"));
        spNgayGiao.putClientProperty(FlatClientProperties.STYLE, "arc: 6;");
        addField(panel, g, 0, spNgayGiao);

        // --- Giờ nhận: JComboBox ---
        addLabel(panel, g, 1, "Giờ nhận:");
        cbGioGiao = new JComboBox<>();
        for (int h = 7; h <= 21; h++) {
            cbGioGiao.addItem(String.format("%02d:00", h));
            if (h < 21) cbGioGiao.addItem(String.format("%02d:30", h));
        }
        cbGioGiao.setSelectedItem("09:00");
        cbGioGiao.putClientProperty(FlatClientProperties.STYLE, "arc: 6;");
        addField(panel, g, 1, cbGioGiao);

        // --- Địa chỉ ---
        addLabel(panel, g, 2, "Địa chỉ giao:");
        txtDiaChiGiao = new JTextField(20);
        styleInput(txtDiaChiGiao, "Nhập địa chỉ (bắt buộc)");
        addField(panel, g, 2, txtDiaChiGiao);

        // --- Loại thanh toán ---
        addLabel(panel, g, 3, "Thanh toán:");
        ButtonGroup bg = new ButtonGroup();
        btnFullPay = buildToggle("Thanh toán đủ", bg);
        btnDeposit = buildToggle("Cọc 50%", bg);
        btnDeposit.setSelected(true);
        JPanel payRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        payRow.setOpaque(false);
        payRow.add(btnFullPay); payRow.add(btnDeposit);
        addField(panel, g, 3, payRow);

        // --- Tiền cọc ---
        addLabel(panel, g, 4, "Tiền cọc:");
        double coc50 = tongTienPhaiTra * 0.5;
        txtTienCoc = new JTextField(fmt(coc50), 12);
        styleInput(txtTienCoc, "Số tiền cọc...");
        lblCocToiThieu = new JLabel("(tối thiểu " + fmt(coc50) + ")");
        lblCocToiThieu.setForeground(COLOR_MUTED);
        lblCocToiThieu.putClientProperty(FlatClientProperties.STYLE, "font: 11 $defaultFont;");
        JPanel cocRow = new JPanel(new BorderLayout(6, 0));
        cocRow.setOpaque(false);
        cocRow.add(txtTienCoc, BorderLayout.CENTER);
        cocRow.add(lblCocToiThieu, BorderLayout.EAST);
        addField(panel, g, 4, cocRow);

        btnFullPay.addActionListener(e -> {
            txtTienCoc.setText(fmt(tongTienPhaiTra));
            txtTienCoc.setEnabled(false);
        });
        btnDeposit.addActionListener(e -> {
            txtTienCoc.setText(fmt(coc50));
            txtTienCoc.setEnabled(true);
        });

        return panel;
    }

    // =========================================================
    // ĐIỀU HƯỚNG
    // =========================================================
    private void goNext() {
        cardLayout.show(cardPanel, "step2");
        lblStep.setText("Bước 2 / 2");
        btnBack.setEnabled(true);
        btnNext.setVisible(false);
        btnConfirm.setVisible(true);
    }

    private void goBack() {
        cardLayout.show(cardPanel, "step1");
        lblStep.setText("Bước 1 / 2");
        btnBack.setEnabled(false);
        btnNext.setVisible(true);
        btnConfirm.setVisible(false);
    }

    private void confirm() {
        // Thu thập dữ liệu bước 2
        if (btnImmediateFlow.isSelected()) {
            result.orderType = OrderType.IMMEDIATE;
            result.hinhThucThanhToan = btnCash.isSelected() ? "Tiền mặt" : "Chuyển khoản";
            if (btnCash.isSelected()) {
                try {
                    result.soTienKhachDua = Double.parseDouble(txtKhachDua.getText().replaceAll("[^0-9]", ""));
                    if (result.soTienKhachDua < tongTienPhaiTra) {
                        JOptionPane.showMessageDialog(this, "Số tiền khách đưa chưa đủ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                } catch (Exception e) {
                    result.soTienKhachDua = tongTienPhaiTra;
                }
            } else {
                result.soTienKhachDua = tongTienPhaiTra;
            }
            result.ngayGioNhan = LocalDateTime.now();
        } else {
            result.orderType = OrderType.PREORDER;
            // Đọc ngày từ JSpinner
            try {
                java.util.Date datePicked = (java.util.Date) spNgayGiao.getValue();
                LocalDate ngay = datePicked.toInstant()
                        .atZone(java.time.ZoneId.systemDefault()).toLocalDate();
                String gioStr = (String) cbGioGiao.getSelectedItem();
                LocalTime gio = LocalTime.parse(gioStr);
                result.ngayGioNhan = LocalDateTime.of(ngay, gio);
                if (result.ngayGioNhan.isBefore(LocalDateTime.now())) {
                    JOptionPane.showMessageDialog(this, "Ngày/giờ nhận không được trong quá khứ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Ngày/giờ nhận không hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            result.diaChiGiao = txtDiaChiGiao.getText().trim();
            if (result.diaChiGiao.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Bắt buộc nhập địa chỉ giao bánh!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            // Tiền cọc
            try {
                result.tienCoc = Double.parseDouble(txtTienCoc.getText().replaceAll("[^0-9]", ""));
                double minCoc = tongTienPhaiTra * 0.5;
                if (result.tienCoc < minCoc) {
                    JOptionPane.showMessageDialog(this, "Tiền cọc phải tối thiểu 50% (" + fmt(minCoc) + ")!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (Exception e) {
                result.tienCoc = tongTienPhaiTra * 0.5;
            }
        }
        result.confirmed = true;
        dispose();
    }

    // =========================================================
    // TIỆN ÍCH
    // =========================================================
    private void loadQR(JPanel container, double amount) {
        container.removeAll();
        ImageIcon qr = QRGenerator.generateDefaultQR(amount, "DonHang");
        if (qr != null) {
            Image scaled = qr.getImage().getScaledInstance(160, 160, Image.SCALE_SMOOTH);
            JLabel lblQR = new JLabel(new ImageIcon(scaled), SwingConstants.CENTER);
            JLabel lblAmt = new JLabel(fmt(amount), SwingConstants.CENTER);
            lblAmt.putClientProperty(FlatClientProperties.STYLE, "font: bold 14 $defaultFont;");
            lblAmt.setForeground(COLOR_PRIMARY);
            container.add(lblQR, BorderLayout.CENTER);
            container.add(lblAmt, BorderLayout.SOUTH);
        }
        container.revalidate();
        container.repaint();
    }

    private JToggleButton buildToggle(String text, ButtonGroup bg) {
        JToggleButton btn = new JToggleButton(text);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.putClientProperty(FlatClientProperties.STYLE, "arc: 8; font: bold $defaultFont;");
        bg.add(btn);
        return btn;
    }

    private JButton styleButton(JButton btn, Color bg, Color fg) {
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.putClientProperty(FlatClientProperties.STYLE, "arc: 8; font: bold $defaultFont; margin: 6,16,6,16;");
        return btn;
    }

    private void styleInput(JTextField f, String placeholder) {
        f.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, placeholder);
        f.putClientProperty(FlatClientProperties.STYLE, "arc: 6; margin: 4,8,4,8; font: $defaultFont;");
    }

    private void addLabel(JPanel p, GridBagConstraints g, int row, String text) {
        g.gridy = row; g.gridx = 0; g.weightx = 0; g.gridwidth = 1;
        JLabel lbl = new JLabel(text);
        lbl.putClientProperty(FlatClientProperties.STYLE, "font: bold 12 $defaultFont;");
        p.add(lbl, g);
    }

    private void addField(JPanel p, GridBagConstraints g, int row, Component comp) {
        g.gridy = row; g.gridx = 1; g.weightx = 1; g.gridwidth = 1;
        p.add(comp, g);
    }

    private String fmt(double val) {
        return String.format(Locale.US, "%,.0f đ", val);
    }

    public OrderRequest getResult() { return result; }
}
