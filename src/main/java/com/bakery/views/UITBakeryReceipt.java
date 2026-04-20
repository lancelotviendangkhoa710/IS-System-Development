package com.bakery.views;

import com.bakery.model.dto.CTDonHangDTO;
import com.bakery.model.dto.SanPhamDTO;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.text.DecimalFormat;
import java.util.List;

public class UITBakeryReceipt extends JFrame {

    private static final Color COLOR_PRIMARY = new Color(165, 54, 13);
    private static final Color COLOR_TEXT_VARIANT = new Color(88, 66, 59);
    private static final Color COLOR_BORDER = new Color(224, 192, 182, 80);

    public UITBakeryReceipt(String tieuDe, String maDonStr, String khachHang,
                            List<CTDonHangDTO> cart, List<SanPhamDTO> data,
                            String tienGiamGia, String tongTien, String daThu,
                            String tienKhachDua, String tienThua) {
        setTitle("Hoa don - UIT Bakery");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(new Color(240, 237, 234));

        setLayout(new java.awt.GridBagLayout());
        add(taoPanelHoaDon(
                tieuDe, maDonStr, khachHang, cart, data,
                tienGiamGia, tongTien, daThu, tienKhachDua, tienThua, null
        ));
        pack();
        setLocationRelativeTo(null);
    }

    public static JPanel taoPanelHoaDon(String tieuDe, String maDonStr, String khachHang,
                                        List<CTDonHangDTO> cart, List<SanPhamDTO> data,
                                        String tienGiamGia, String tongTien, String daThu,
                                        String tienKhachDua, String tienThua,
                                        ImageIcon qrIcon) {
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
                g2d.setColor(COLOR_PRIMARY);
                g2d.fillRoundRect(0, 0, getWidth(), 10, 25, 25);
                g2d.fillRect(0, 5, getWidth(), 5);
                g2d.dispose();
            }
        };
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setOpaque(false);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel lblBrand = new JLabel("UIT Bakery", SwingConstants.CENTER);
        lblBrand.setFont(new Font("Inter", Font.BOLD, 24));
        lblBrand.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblTitle = new JLabel(tieuDe, SwingConstants.CENTER);
        lblTitle.setFont(new Font("Inter", Font.BOLD, 20));
        lblTitle.setForeground(COLOR_PRIMARY);
        lblTitle.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, COLOR_BORDER));
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel metaPanel = new JPanel(new GridLayout(1, 2));
        metaPanel.setOpaque(false);
        metaPanel.add(createMetaItem("MA DON HANG", maDonStr, false));
        metaPanel.add(createMetaItem("KHACH HANG", khachHang, true));

        JPanel itemsContainer = new JPanel();
        itemsContainer.setLayout(new BoxLayout(itemsContainer, BoxLayout.Y_AXIS));
        itemsContainer.setOpaque(false);
        itemsContainer.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        DecimalFormat df = new DecimalFormat("#,### d");
        for (CTDonHangDTO item : cart) {
            String tenSp = data.stream()
                    .filter(sp -> sp.getMaSP() == item.getMaSP())
                    .map(SanPhamDTO::getTenSP)
                    .findFirst()
                    .orElse("SP");

            JPanel itemPanel = new JPanel(new BorderLayout());
            itemPanel.setOpaque(false);
            itemPanel.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));

            JLabel itemName = new JLabel("<html><b>" + tenSp + "</b> <font color='gray'>x" + item.getSoLuong() + "</font></html>");
            JLabel itemPrice = new JLabel(df.format(item.getDonGia() * item.getSoLuong()).replace(",", "."));
            itemPrice.setFont(new Font("Inter", Font.BOLD, 13));

            itemPanel.add(itemName, BorderLayout.WEST);
            itemPanel.add(itemPrice, BorderLayout.EAST);
            itemsContainer.add(itemPanel);
        }

        JPanel summaryPanel = new JPanel();
        summaryPanel.setLayout(new BoxLayout(summaryPanel, BoxLayout.Y_AXIS));
        summaryPanel.setBackground(new Color(246, 243, 240));
        summaryPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        if (tienGiamGia != null && !tienGiamGia.isEmpty() && !tienGiamGia.equals("0 d") && !tienGiamGia.equals("0.0 d")) {
            summaryPanel.add(createSummaryRow("GIAM GIA TV", "-" + tienGiamGia, false));
            summaryPanel.add(Box.createVerticalStrut(3));
        }
        summaryPanel.add(createSummaryRow("TONG", tongTien, false));
        summaryPanel.add(Box.createVerticalStrut(5));
        summaryPanel.add(createSummaryRow("DA THU", daThu, true));
        summaryPanel.add(Box.createVerticalStrut(5));
        summaryPanel.add(createSummaryRow("KHACH DUA", tienKhachDua, false));
        if (tienThua != null && !tienThua.isEmpty()) {
            summaryPanel.add(new JSeparator());
            summaryPanel.add(createSummaryRow("TIEN THUA", tienThua, false));
        }

        mainPanel.add(lblBrand);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(lblTitle);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(metaPanel);
        mainPanel.add(itemsContainer);
        mainPanel.add(summaryPanel);

        if (qrIcon != null) {
            mainPanel.add(Box.createVerticalStrut(10));
            JLabel lblQrTitle = new JLabel("QR THANH TOAN", SwingConstants.CENTER);
            lblQrTitle.setFont(new Font("Inter", Font.BOLD, 11));
            lblQrTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel lblQr = new JLabel(qrIcon);
            lblQr.setAlignmentX(Component.CENTER_ALIGNMENT);
            lblQr.setBorder(BorderFactory.createLineBorder(new Color(224, 192, 182)));

            mainPanel.add(lblQrTitle);
            mainPanel.add(Box.createVerticalStrut(5));
            mainPanel.add(lblQr);
        }

        mainPanel.add(Box.createVerticalStrut(15));
        JLabel lblFooter = new JLabel("Cam on quy khach!", SwingConstants.CENTER);
        lblFooter.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblFooter.setFont(new Font("Inter", Font.ITALIC, 11));
        mainPanel.add(lblFooter);

        return mainPanel;
    }

    private static JPanel createMetaItem(String label, String value, boolean alignRight) {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        JLabel l1 = new JLabel(label);
        l1.setFont(new Font("Inter", Font.BOLD, 9));
        l1.setForeground(COLOR_TEXT_VARIANT);
        JLabel l2 = new JLabel(value);
        l2.setFont(new Font("Inter", Font.BOLD, 14));

        if (alignRight) {
            l1.setHorizontalAlignment(SwingConstants.RIGHT);
            l2.setHorizontalAlignment(SwingConstants.RIGHT);
        }
        p.add(l1, BorderLayout.NORTH);
        p.add(l2, BorderLayout.CENTER);
        return p;
    }

    private static JPanel createSummaryRow(String label, String value, boolean isHighlight) {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        JLabel l1 = new JLabel(label);
        l1.setFont(new Font("Inter", isHighlight ? Font.BOLD : Font.PLAIN, 12));
        if (isHighlight) {
            l1.setForeground(COLOR_PRIMARY);
        }

        JLabel l2 = new JLabel(value);
        l2.setFont(new Font("Inter", Font.BOLD, 14));
        if (isHighlight) {
            l2.setForeground(COLOR_PRIMARY);
        }

        p.add(l1, BorderLayout.WEST);
        p.add(l2, BorderLayout.EAST);
        return p;
    }
}
