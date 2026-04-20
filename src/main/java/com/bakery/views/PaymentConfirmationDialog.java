package com.bakery.views;

import com.bakery.presenters.OrderPresenter;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Window;

public class PaymentConfirmationDialog extends JDialog {
    private static final Color COLOR_PRIMARY = new Color(165, 54, 13);

    private final OrderPresenter orderPresenter;
    private boolean success = false;

    public PaymentConfirmationDialog(Window owner, OrderPresenter orderPresenter, JPanel invoicePanel, double amount, String orderId) {
        super(owner, "Xác nhận thanh toán", ModalityType.APPLICATION_MODAL);
        this.orderPresenter = orderPresenter;

        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(240, 237, 234));

        add(invoicePanel, BorderLayout.CENTER);
        add(taoNutHoanThanh(), BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(owner);
    }

    public boolean isSuccess() {
        return success;
    }

    private JButton taoNutHoanThanh() {
        JButton btnHoanThanh = new JButton("HOÀN THÀNH GIAO DỊCH");
        btnHoanThanh.setPreferredSize(new Dimension(0, 56));
        btnHoanThanh.setBackground(COLOR_PRIMARY);
        btnHoanThanh.setForeground(Color.WHITE);
        btnHoanThanh.setFont(new Font("Arial", Font.BOLD, 16));
        btnHoanThanh.setFocusPainted(false);
        btnHoanThanh.setBorder(BorderFactory.createEmptyBorder());

        btnHoanThanh.addActionListener(e -> {
            btnHoanThanh.setEnabled(false);
            boolean isSaved = orderPresenter.xuLyLuuDonHangVaoDB();
            if (isSaved) {
                success = true;
                dispose();
            } else {
                btnHoanThanh.setEnabled(true);
            }
        });

        return btnHoanThanh;
    }
}
