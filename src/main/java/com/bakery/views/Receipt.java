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
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.DecimalFormat;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

public class Receipt extends JFrame {

    private static final Color COLOR_PRIMARY = new Color(165, 54, 13);
    private static final Color COLOR_TEXT_VARIANT = new Color(88, 66, 59);
    private static final Color COLOR_BORDER = new Color(224, 192, 182, 80);

    public Receipt(String tieuDe, String maDonStr, String maHoaDonStr, String ngayLapHoaDon, String khachHang,
            List<CTDonHangDTO> cart, List<SanPhamDTO> data,
            String tienGiamGia, String tongTien, String daThu,
            String tienKhachDua, String tienThua) {
        setTitle("Hóa đơn - H3k Bakery");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(new Color(240, 237, 234));

        setLayout(new BorderLayout());
        JPanel panelHoaDon = taoPanelHoaDon(
                tieuDe, maDonStr, maHoaDonStr, ngayLapHoaDon, khachHang, cart, data,
                tienGiamGia, tongTien, daThu, tienKhachDua, tienThua, null);
        add(panelHoaDon, BorderLayout.CENTER);

        JButton btnSavePdf = new JButton("Lưu PDF");
        btnSavePdf.addActionListener(e -> saveToPdf(panelHoaDon, maDonStr));
        JPanel bottomPanel = new JPanel();
        bottomPanel.add(btnSavePdf);
        add(bottomPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
    }

    private void saveToPdf(JPanel panelToPrint, String orderId) {
        try {
            File dir = new File("src/main/resources/hoadon");
            if (!dir.exists()) {
                dir.mkdirs();
            }

            BufferedImage image = new BufferedImage(panelToPrint.getWidth(), panelToPrint.getHeight(),
                    BufferedImage.TYPE_INT_RGB);
            Graphics2D g2 = image.createGraphics();
            panelToPrint.paint(g2);
            g2.dispose();

            PDDocument document = new PDDocument();
            PDPage page = new PDPage(new PDRectangle(panelToPrint.getWidth(), panelToPrint.getHeight()));
            document.addPage(page);

            PDImageXObject pdImage = LosslessFactory.createFromImage(document, image);
            PDPageContentStream contentStream = new PDPageContentStream(document, page);
            contentStream.drawImage(pdImage, 0, 0);
            contentStream.close();

            String filename = "HoaDon_" + orderId.replace("#", "") + "_" + System.currentTimeMillis() + ".pdf";
            File file = new File(dir, filename);
            document.save(file);
            document.close();

            JOptionPane.showMessageDialog(this, "Đã lưu hóa đơn tại:\n" + file.getAbsolutePath());
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi lưu PDF: " + ex.getMessage(), "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public static JPanel taoPanelHoaDon(String tieuDe, String maDonStr, String maHoaDonStr, String ngayLapHoaDon,
            String khachHang,
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

        JLabel lblBrand = new JLabel("H3k Bakery", SwingConstants.CENTER);
        lblBrand.setFont(new Font("Inter", Font.BOLD, 24));
        lblBrand.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblTitle = new JLabel(tieuDe, SwingConstants.CENTER);
        lblTitle.setFont(new Font("Inter", Font.BOLD, 20));
        lblTitle.setForeground(COLOR_PRIMARY);
        lblTitle.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, COLOR_BORDER));
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel metaPanel = new JPanel(new GridLayout(2, 2, 8, 6));
        metaPanel.setOpaque(false);
        metaPanel.add(createMetaItem("Mã đơn hàng", maDonStr, false));
        metaPanel.add(createMetaItem("Mã hóa đơn", maHoaDonStr, true));
        metaPanel.add(createMetaItem("Ngày lập hóa đơn", ngayLapHoaDon, false));
        metaPanel.add(createMetaItem("Khách hàng", khachHang, true));

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

            JLabel itemName = new JLabel(
                    "<html><b>" + tenSp + "</b> <font color='gray'>x" + item.getSoLuong() + "</font></html>");
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

        if (tienGiamGia != null && !tienGiamGia.isEmpty() && !tienGiamGia.equals("0 d")
                && !tienGiamGia.equals("0.0 d")) {
            summaryPanel.add(createSummaryRow("Giảm giá thành viên", "-" + tienGiamGia, false));
            summaryPanel.add(Box.createVerticalStrut(3));
        }
        summaryPanel.add(createSummaryRow("Tổng", tongTien, false));
        summaryPanel.add(Box.createVerticalStrut(5));
        summaryPanel.add(createSummaryRow("Đã thu", daThu, true));
        summaryPanel.add(Box.createVerticalStrut(5));
        summaryPanel.add(createSummaryRow("Khách đưa", tienKhachDua, false));
        if (tienThua != null && !tienThua.isEmpty()) {
            summaryPanel.add(new JSeparator());
            summaryPanel.add(createSummaryRow("Tiền thừa", tienThua, false));
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
        JLabel lblFooter1 = new JLabel("Cảm ơn quý khách!!", SwingConstants.CENTER);
        lblFooter1.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblFooter1.setFont(new Font("Inter", Font.ITALIC, 11));
        mainPanel.add(lblFooter1);
        JLabel lblFooter2 = new JLabel("Chúc quý khách ngon miệng", SwingConstants.CENTER);
        lblFooter2.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblFooter2.setFont(new Font("Inter", Font.ITALIC, 11));
        mainPanel.add(lblFooter2);

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
