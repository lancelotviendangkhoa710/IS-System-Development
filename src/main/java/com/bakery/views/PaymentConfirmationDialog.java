package com.bakery.views;

import com.bakery.utils.QRGenerator;
import com.formdev.flatlaf.FlatClientProperties;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Locale;

/**
 * Dialog thanh toán hiện đại, cung cấp trải nghiệm premium cho thu ngân.
 * Hỗ trợ chọn hình thức thanh toán, tính toán tiền thừa, hiển thị QR code.
 */
public class PaymentConfirmationDialog extends JDialog {

    private static final Color COLOR_PRIMARY = new Color(0x92400E);
    private static final Color COLOR_SUCCESS = new Color(0x16A34A);
    private static final Color COLOR_BG = new Color(0xFDFBF7);

    private final double tongTien;
    private final String moTaDonHang;
    
    private double soTienThanhToan; // Có thể là tổng tiền hoặc tiền cọc
    private String hinhThucThanhToan = "Tiền mặt";
    private boolean isRetail = true; // True: Thanh toán hết, False: Đặt cọc
    
    private JLabel lblCanThu, lblTienThua;
    private JTextField txtKhachDua;
    private JToggleButton btnCash, btnTransfer;
    private JToggleButton btnFull, btnDeposit;
    private JPanel qrPanel;
    private JButton btnConfirm;
    
    private boolean confirmed = false;

    public PaymentConfirmationDialog(Window owner, double tongTien, String moTa) {
        super(owner, "Xác nhận Thanh toán", ModalityType.APPLICATION_MODAL);
        this.tongTien = tongTien;
        this.soTienThanhToan = tongTien;
        this.moTaDonHang = moTa;

        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(COLOR_BG);
        
        // 1. Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(COLOR_PRIMARY);
        header.setBorder(new EmptyBorder(20, 25, 20, 25));
        JLabel title = new JLabel("CHI TIẾT THANH TOÁN");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(Color.WHITE);
        header.add(title, BorderLayout.WEST);
        add(header, BorderLayout.NORTH);

        // 2. Body
        JPanel body = new JPanel(new GridBagLayout());
        body.setOpaque(false);
        body.setBorder(new EmptyBorder(25, 30, 25, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        // Loại giao dịch
        gbc.gridx = 0; gbc.gridy = 0;
        body.add(createSectionLabel("Loại giao dịch:"), gbc);
        
        JPanel typePanel = new JPanel(new GridLayout(1, 2, 10, 0));
        typePanel.setOpaque(false);
        btnFull = new JToggleButton("Bán lẻ (Trả hết)");
        btnDeposit = new JToggleButton("Đặt đơn (Cọc 50%)");
        styleToggle(btnFull, true);
        styleToggle(btnDeposit, false);
        
        ButtonGroup groupType = new ButtonGroup();
        groupType.add(btnFull);
        groupType.add(btnDeposit);
        btnFull.setSelected(true);
        
        typePanel.add(btnFull);
        typePanel.add(btnDeposit);
        gbc.gridx = 1;
        body.add(typePanel, gbc);

        // Phương thức thanh toán
        gbc.gridx = 0; gbc.gridy = 1;
        body.add(createSectionLabel("Phương thức:"), gbc);
        
        JPanel methodPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        methodPanel.setOpaque(false);
        btnCash = new JToggleButton("💵 Tiền mặt");
        btnTransfer = new JToggleButton("🏦 Chuyển khoản");
        styleToggle(btnCash, true);
        styleToggle(btnTransfer, false);
        
        ButtonGroup groupMethod = new ButtonGroup();
        groupMethod.add(btnCash);
        groupMethod.add(btnTransfer);
        btnCash.setSelected(true);
        
        methodPanel.add(btnCash);
        methodPanel.add(btnTransfer);
        gbc.gridx = 1;
        body.add(methodPanel, gbc);

        // Số tiền cần thu
        gbc.gridx = 0; gbc.gridy = 2;
        body.add(createSectionLabel("Số tiền cần thu:"), gbc);
        lblCanThu = new JLabel(formatMoney(tongTien));
        lblCanThu.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblCanThu.setForeground(COLOR_PRIMARY);
        gbc.gridx = 1;
        body.add(lblCanThu, gbc);

        // Tiền khách đưa (cho Tiền mặt)
        gbc.gridx = 0; gbc.gridy = 3;
        JLabel lblKhachDua = createSectionLabel("Khách đưa:");
        body.add(lblKhachDua, gbc);
        txtKhachDua = new JTextField();
        txtKhachDua.setFont(new Font("Segoe UI", Font.BOLD, 24));
        txtKhachDua.putClientProperty(FlatClientProperties.STYLE, "arc: 10; margin: 5,10,5,10");
        gbc.gridx = 1;
        body.add(txtKhachDua, gbc);

        // Tiền thừa
        gbc.gridx = 0; gbc.gridy = 4;
        JLabel lblThuaTitle = createSectionLabel("Tiền thừa:");
        body.add(lblThuaTitle, gbc);
        lblTienThua = new JLabel("0 đ");
        lblTienThua.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTienThua.setForeground(COLOR_SUCCESS);
        gbc.gridx = 1;
        body.add(lblTienThua, gbc);

        // QR Code (ẩn mặc định)
        qrPanel = new JPanel(new BorderLayout(0, 10));
        qrPanel.setOpaque(false);
        qrPanel.setVisible(false);
        JLabel lblQrImg = new JLabel();
        lblQrImg.setHorizontalAlignment(SwingConstants.CENTER);
        qrPanel.add(new JLabel("QUÉT MÃ ĐỂ THANH TOÁN", SwingConstants.CENTER), BorderLayout.NORTH);
        qrPanel.add(lblQrImg, BorderLayout.CENTER);
        
        gbc.gridx = 1; gbc.gridy = 3;
        gbc.gridheight = 3;
        body.add(qrPanel, gbc);

        add(body, BorderLayout.CENTER);

        // 3. Footer
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 20));
        footer.setOpaque(false);
        JButton btnCancel = new JButton("Hủy bỏ");
        btnCancel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        btnCancel.addActionListener(e -> dispose());
        
        btnConfirm = new JButton("XÁC NHẬN & IN HÓA ĐƠN");
        btnConfirm.setBackground(COLOR_SUCCESS);
        btnConfirm.setForeground(Color.WHITE);
        btnConfirm.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnConfirm.setPreferredSize(new Dimension(300, 50));
        btnConfirm.putClientProperty(FlatClientProperties.STYLE, "arc: 12");
        btnConfirm.addActionListener(e -> {
            confirmed = true;
            dispose();
        });

        footer.add(btnCancel);
        footer.add(btnConfirm);
        add(footer, BorderLayout.SOUTH);

        // Event listeners
        btnFull.addActionListener(e -> updateAmount(true));
        btnDeposit.addActionListener(e -> updateAmount(false));
        
        btnCash.addActionListener(e -> {
            hinhThucThanhToan = "Tiền mặt";
            lblKhachDua.setVisible(true);
            txtKhachDua.setVisible(true);
            lblThuaTitle.setVisible(true);
            lblTienThua.setVisible(true);
            qrPanel.setVisible(false);
            pack();
        });
        
        btnTransfer.addActionListener(e -> {
            hinhThucThanhToan = "Chuyển khoản";
            lblKhachDua.setVisible(false);
            txtKhachDua.setVisible(false);
            lblThuaTitle.setVisible(false);
            lblTienThua.setVisible(false);
            
            ImageIcon qr = QRGenerator.generateDefaultQR(soTienThanhToan, moTaDonHang);
            if (qr != null) {
                lblQrImg.setIcon(new ImageIcon(qr.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH)));
            }
            qrPanel.setVisible(true);
            pack();
        });

        txtKhachDua.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                calculateChange();
            }
        });

        pack();
        setMinimumSize(new Dimension(700, 600));
        setLocationRelativeTo(getOwner());
    }

    private void updateAmount(boolean isFull) {
        this.isRetail = isFull;
        this.soTienThanhToan = isFull ? tongTien : tongTien * 0.5;
        lblCanThu.setText(formatMoney(soTienThanhToan));
        if (btnTransfer.isSelected()) {
            btnTransfer.doClick(); // Refresh QR
        }
        calculateChange();
    }

    private void calculateChange() {
        try {
            double dua = Double.parseDouble(txtKhachDua.getText().replaceAll("[^0-9]", ""));
            double thừa = dua - soTienThanhToan;
            lblTienThua.setText(formatMoney(Math.max(0, thừa)));
            btnConfirm.setEnabled(thừa >= 0 || btnTransfer.isSelected());
        } catch (Exception e) {
            lblTienThua.setText("0 đ");
            btnConfirm.setEnabled(btnTransfer.isSelected());
        }
    }

    private JLabel createSectionLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl.setForeground(new Color(0x6B7280));
        return lbl;
    }

    private void styleToggle(JToggleButton btn, boolean selected) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.putClientProperty(FlatClientProperties.STYLE, "arc: 10; margin: 10,20,10,20;");
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private String formatMoney(double val) {
        return String.format(Locale.US, "%,.0f đ", val);
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public String getHinhThucThanhToan() {
        return hinhThucThanhToan;
    }

    public double getSoTienThanhToan() {
        return soTienThanhToan;
    }

    public boolean isRetail() {
        return isRetail;
    }
}
