package com.bakery.views;

import com.bakery.models.dao.NhanVienDAO;
import com.bakery.models.dto.NhanVienDTO;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

public class LoginFrame extends JFrame {
    private final JTextField txtUsername = new JTextField(20);
    private final JPasswordField txtPassword = new JPasswordField(20);
    private final JCheckBox chkShowPassword = new JCheckBox("Hiện mật khẩu");
    private final JButton btnLogin = new JButton("Đăng nhập");
    private final JButton btnClear = new JButton("Làm mới");
    private final NhanVienDAO nhanVienDAO = new NhanVienDAO();
    private char defaultEchoChar;

    public LoginFrame() {
        initComponents();
        initEvents();
    }

    private void initComponents() {
        setTitle("Amitié Management - Đăng nhập");
        setSize(420, 280);
        setMinimumSize(new Dimension(420, 280));
        setLocationRelativeTo(null);
        setContentPane(buildContent());

        defaultEchoChar = txtPassword.getEchoChar();
        getRootPane().setDefaultButton(btnLogin);
    }

    private JPanel buildContent() {
        JPanel content = new JPanel(new BorderLayout());
        content.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel lblTitle = new JLabel("Đăng nhập hệ thống", SwingConstants.CENTER);
        lblTitle.setFont(lblTitle.getFont().deriveFont(Font.BOLD, 22f));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        content.add(lblTitle, BorderLayout.NORTH);
        content.add(buildFormPanel(), BorderLayout.CENTER);

        return content;
    }

    private JPanel buildFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Tên đăng nhập"), gbc);

        gbc.gridx = 1;
        panel.add(txtUsername, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Mật khẩu"), gbc);

        gbc.gridx = 1;
        panel.add(txtPassword, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        panel.add(chkShowPassword, gbc);

        JPanel actionPanel = new JPanel();
        actionPanel.add(btnLogin);
        actionPanel.add(btnClear);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        panel.add(actionPanel, gbc);

        return panel;
    }

    private void initEvents() {
        btnLogin.addActionListener(e -> handleLogin());
        btnClear.addActionListener(e -> clearForm());
        chkShowPassword.addActionListener(e -> togglePasswordVisibility());
    }

    private void handleLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (username.isBlank()) {
            showError("Tên đăng nhập không được để trống.");
            txtUsername.requestFocusInWindow();
            return;
        }

        if (password.isBlank()) {
            showError("Mật khẩu không được để trống.");
            txtPassword.requestFocusInWindow();
            return;
        }

        btnLogin.setEnabled(false);

        try {
            NhanVienDTO nhanVien = nhanVienDAO.kiemTraDangNhap(username, password);
            JOptionPane.showMessageDialog(
                    this,
                    "Đăng nhập thành công. Xin chào, " + nhanVien.getHoTen() + ".",
                    "Thành công",
                    JOptionPane.INFORMATION_MESSAGE
            );

            // Điểm nối tiếp theo: đóng LoginFrame và mở MainFrame của bạn.
            clearForm();
        } catch (Exception ex) {
            showError(ex.getMessage());
        } finally {
            btnLogin.setEnabled(true);
        }
    }

    private void clearForm() {
        txtUsername.setText("");
        txtPassword.setText("");
        chkShowPassword.setSelected(false);
        txtPassword.setEchoChar(defaultEchoChar);
        txtUsername.requestFocusInWindow();
    }

    private void togglePasswordVisibility() {
        txtPassword.setEchoChar(chkShowPassword.isSelected() ? (char) 0 : defaultEchoChar);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Lỗi đăng nhập", JOptionPane.ERROR_MESSAGE);
    }

    public static void open() {
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
