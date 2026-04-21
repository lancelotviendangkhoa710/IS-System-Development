package com.bakery.views;

import com.bakery.models.dao.NhanVienDAO;
import com.bakery.models.dto.NhanVienDTO;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.text.NumberFormatter;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.NumberFormat;

public class RegisterFrame extends JFrame {
    private final JTextField txtHoTen = new JTextField(22);
    private final JTextField txtSoDienThoai = new JTextField(22);
    private final JTextField txtTenDangNhap = new JTextField(22);
    private final JPasswordField txtMatKhau = new JPasswordField(22);
    private final JPasswordField txtXacNhanMatKhau = new JPasswordField(22);
    private final JFormattedTextField txtMaVaiTro = createRoleField();
    private final JButton btnRegister = new JButton("Tao tai khoan");
    private final JButton btnClear = new JButton("Lam moi");
    private final JButton btnBack = new JButton("Quay lai");
    private final NhanVienDAO nhanVienDAO = new NhanVienDAO();

    public RegisterFrame() {
        initComponents();
        initEvents();
    }

    private void initComponents() {
        setTitle("Dang ky tai khoan");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(520, 420);
        setMinimumSize(new Dimension(520, 420));
        setLocationRelativeTo(null);
        setContentPane(buildContent());
        getRootPane().setDefaultButton(btnRegister);
    }

    private JPanel buildContent() {
        JPanel content = new JPanel(new BorderLayout());
        content.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel lblTitle = new JLabel("Dang ky tai khoan nhan vien", JLabel.CENTER);
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

        addRow(panel, gbc, 0, "Ho ten", txtHoTen);
        addRow(panel, gbc, 1, "So dien thoai", txtSoDienThoai);
        addRow(panel, gbc, 2, "Ten dang nhap", txtTenDangNhap);
        addRow(panel, gbc, 3, "Mat khau", txtMatKhau);
        addRow(panel, gbc, 4, "Xac nhan mat khau", txtXacNhanMatKhau);
        addRow(panel, gbc, 5, "Ma vai tro", txtMaVaiTro);

        JPanel actionPanel = new JPanel();
        actionPanel.add(btnRegister);
        actionPanel.add(btnClear);
        actionPanel.add(btnBack);

        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        panel.add(actionPanel, gbc);

        return panel;
    }

    private void addRow(JPanel panel, GridBagConstraints gbc, int row, String label, java.awt.Component field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        panel.add(new JLabel(label), gbc);

        gbc.gridx = 1;
        panel.add(field, gbc);
    }

    private void initEvents() {
        btnRegister.addActionListener(e -> handleRegister());
        btnClear.addActionListener(e -> clearForm());
        btnBack.addActionListener(e -> dispose());
    }

    private void handleRegister() {
        String hoTen = txtHoTen.getText().trim();
        String soDienThoai = txtSoDienThoai.getText().trim();
        String tenDangNhap = txtTenDangNhap.getText().trim();
        String matKhau = new String(txtMatKhau.getPassword());
        String xacNhanMatKhau = new String(txtXacNhanMatKhau.getPassword());
        Number maVaiTroValue = (Number) txtMaVaiTro.getValue();

        if (hoTen.isBlank()) {
            showError("Ho ten khong duoc de trong.");
            txtHoTen.requestFocusInWindow();
            return;
        }

        if (soDienThoai.isBlank()) {
            showError("So dien thoai khong duoc de trong.");
            txtSoDienThoai.requestFocusInWindow();
            return;
        }

        if (tenDangNhap.isBlank()) {
            showError("Ten dang nhap khong duoc de trong.");
            txtTenDangNhap.requestFocusInWindow();
            return;
        }

        if (matKhau.isBlank()) {
            showError("Mat khau khong duoc de trong.");
            txtMatKhau.requestFocusInWindow();
            return;
        }

        if (matKhau.length() < 6) {
            showError("Mat khau phai co it nhat 6 ky tu.");
            txtMatKhau.requestFocusInWindow();
            return;
        }

        if (!matKhau.equals(xacNhanMatKhau)) {
            showError("Mat khau xac nhan khong khop.");
            txtXacNhanMatKhau.requestFocusInWindow();
            return;
        }

        if (maVaiTroValue == null || maVaiTroValue.intValue() <= 0) {
            showError("Ma vai tro phai lon hon 0.");
            txtMaVaiTro.requestFocusInWindow();
            return;
        }

        NhanVienDTO nhanVien = new NhanVienDTO();
        nhanVien.setHoTen(hoTen);
        nhanVien.setSdt(soDienThoai);
        nhanVien.setTenDangNhap(tenDangNhap);
        nhanVien.setMatKhau(matKhau);
        nhanVien.setMaVaiTro(maVaiTroValue.intValue());
        nhanVien.setTrangThaiLamViec(1);

        btnRegister.setEnabled(false);

        try {
            int maNhanVien = nhanVienDAO.themNhanVien(nhanVien);
            JOptionPane.showMessageDialog(
                    this,
                    "Dang ky thanh cong. Ma nhan vien moi: " + maNhanVien,
                    "Thanh cong",
                    JOptionPane.INFORMATION_MESSAGE
            );
            clearForm();
        } catch (Exception ex) {
            showError(ex.getMessage());
        } finally {
            btnRegister.setEnabled(true);
        }
    }

    private void clearForm() {
        txtHoTen.setText("");
        txtSoDienThoai.setText("");
        txtTenDangNhap.setText("");
        txtMatKhau.setText("");
        txtXacNhanMatKhau.setText("");
        txtMaVaiTro.setValue(1);
        txtHoTen.requestFocusInWindow();
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Loi dang ky", JOptionPane.ERROR_MESSAGE);
    }

    private JFormattedTextField createRoleField() {
        NumberFormatter formatter = new NumberFormatter(NumberFormat.getIntegerInstance());
        formatter.setValueClass(Integer.class);
        formatter.setMinimum(1);
        formatter.setAllowsInvalid(false);

        JFormattedTextField field = new JFormattedTextField(formatter);
        field.setColumns(22);
        field.setValue(1);
        return field;
    }

    public static void open() {
        SwingUtilities.invokeLater(() -> new RegisterFrame().setVisible(true));
    }
}
