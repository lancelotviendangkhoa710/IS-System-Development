package com.bakery.views.controllers.banhang;

import com.bakery.utils.DBConnect;
import com.bakery.utils.DialogHelper;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Controller màn hình Demo Phantom Read.
 *
 * Luồng (READ COMMITTED):
 *   1. Mở connection → setTransactionIsolation(READ_COMMITTED) → autoCommit=false
 *   2. Đọc lần 1: SELECT COUNT(*) FROM SANPHAM WHERE SOLUONGTON > 0
 *   3. Đếm ngược 5 giây — trong lúc này background thread UPDATE 1 sản phẩm về 0
 *   4. Đọc lần 2: cùng câu SQL → trả về con số KHÁC → PHANTOM READ!
 *
 * Luồng (SERIALIZABLE):
 *   Tương tự, nhưng lần 2 vẫn trả về con số GIỐNG lần 1 → Nhất quán!
 */
public class DemoPhantomReadViewFXMLController {

    // ── FXML bindings ────────────────────────────────────────────────────────
    @FXML private RadioButton rdoReadCommitted;
    @FXML private RadioButton rdoSerializable;
    @FXML private Button      btnBatDauDemo;
    @FXML private TextFlow    txtFlowLog;
    @FXML private Label       lblKetQua;
    @FXML private VBox        boxKetQua;
    @FXML private Label       lblLan1;
    @FXML private Label       lblLan2;
    @FXML private Label       lblNhanXet;
    @FXML private Label       lblDemNguoc;
    @FXML private ProgressBar progressBar;

    private static final int DELAY_GIAY = 5;
    private static final DateTimeFormatter GIO = DateTimeFormatter.ofPattern("HH:mm:ss");

    @FXML
    public void initialize() {
        ToggleGroup nhom = new ToggleGroup();
        rdoReadCommitted.setToggleGroup(nhom);
        rdoSerializable.setToggleGroup(nhom);
        rdoReadCommitted.setSelected(true);
        boxKetQua.setVisible(false);
        lblDemNguoc.setVisible(false);
        progressBar.setVisible(false);
    }

    // ── Xử lý nút Bắt đầu ──────────────────────────────────────────────────
    @FXML
    private void onBatDauDemo() {
        boolean dungSerializable = rdoSerializable.isSelected();
        txtFlowLog.getChildren().clear();
        boxKetQua.setVisible(false);
        btnBatDauDemo.setDisable(true);

        Task<int[]> task = taoTaskDemo(dungSerializable);

        task.setOnSucceeded(e -> {
            int[] ketQua = task.getValue(); // [lan1, lan2]
            hienThiKetQua(ketQua[0], ketQua[1], dungSerializable);
            btnBatDauDemo.setDisable(false);
            lblDemNguoc.setVisible(false);
            progressBar.setVisible(false);
        });
        task.setOnFailed(e -> {
            ghiLog("❌ Lỗi: " + task.getException().getMessage(), "#DC2626");
            btnBatDauDemo.setDisable(false);
            lblDemNguoc.setVisible(false);
            progressBar.setVisible(false);
        });

        progressBar.setVisible(true);
        lblDemNguoc.setVisible(true);
        new Thread(task).start();
    }

    // ── Task chạy trên background thread ───────────────────────────────────
    private Task<int[]> taoTaskDemo(boolean dungSerializable) {
        return new Task<>() {
            @Override
            protected int[] call() throws Exception {
                String tenMuc = dungSerializable ? "SERIALIZABLE" : "READ COMMITTED";
                logUi("🚀 Bắt đầu demo " + tenMuc, "#185FA5");
                logUi("─────────────────────────────────────────", "#9CA3AF");

                try (Connection conn = DBConnect.getConnection()) {
                    // Cài isolation level TRƯỚC khi tắt autoCommit
                    int level = dungSerializable
                            ? Connection.TRANSACTION_SERIALIZABLE
                            : Connection.TRANSACTION_READ_COMMITTED;
                    conn.setTransactionIsolation(level);
                    conn.setAutoCommit(false);
                    logUi("🔒 Isolation Level: " + tenMuc, "#6366F1");
                    logUi("📖 Transaction bắt đầu...", "#374151");

                    // ── Lần đọc 1 ──────────────────────────────────────────
                    int lan1 = demSanPhamConHang(conn);
                    logUi("📊 [Lần 1 - " + gioHienTai() + "] Sản phẩm còn hàng: " + lan1, "#059669");

                    // ── Đếm ngược 5 giây + background update ───────────────
                    logUi("⏳ Chờ " + DELAY_GIAY + " giây...", "#D97706");
                    Thread bgUpdate = taoThreadCapNhat();
                    bgUpdate.start();

                    for (int i = DELAY_GIAY; i > 0; i--) {
                        final int giay = i;
                        Platform.runLater(() -> {
                            lblDemNguoc.setText("⏱ " + giay + "s — Thu ngân 2 đang bán hàng...");
                            progressBar.setProgress((double)(DELAY_GIAY - giay + 1) / DELAY_GIAY);
                        });
                        Thread.sleep(1000);
                    }
                    bgUpdate.join();

                    // ── Lần đọc 2 ──────────────────────────────────────────
                    int lan2 = demSanPhamConHang(conn);
                    logUi("📊 [Lần 2 - " + gioHienTai() + "] Sản phẩm còn hàng: " + lan2, "#059669");
                    conn.commit();

                    if (lan1 != lan2) {
                        logUi("⚠ PHANTOM READ XẢY RA! Lần 1=" + lan1 + " ≠ Lần 2=" + lan2, "#DC2626");
                    } else {
                        logUi("✅ Nhất quán! Cả 2 lần đều thấy: " + lan1, "#059669");
                    }

                    return new int[]{lan1, lan2};
                }
            }
        };
    }

    // ── Thread phụ: bán 1 sản phẩm (connection riêng, commit ngay) ──────────
    private Thread taoThreadCapNhat() {
        return new Thread(() -> {
            try (Connection conn2 = DBConnect.getConnection()) {
                // Chờ 2s để T1 đã đọc lần 1 xong
                Thread.sleep(2000);
                logUi("🛒 [Thu ngân 2] Đang bán hàng → cập nhật tồn kho...", "#7C3AED");
                // Trừ 1 đơn vị từ sản phẩm đầu tiên còn hàng (không cần tạo đơn thật)
                String sql = "UPDATE SANPHAM SET SOLUONGTON = SOLUONGTON - 1 " +
                             "WHERE MASP = (SELECT MASP FROM SANPHAM WHERE SOLUONGTON > 0 AND ROWNUM = 1)";
                try (PreparedStatement ps = conn2.prepareStatement(sql)) {
                    ps.executeUpdate();
                }
                conn2.commit(); // Commit ngay với connection riêng
                logUi("✔ [Thu ngân 2] Commit xong — tồn kho đã thay đổi trong DB!", "#7C3AED");
            } catch (Exception e) {
                logUi("❌ [Thu ngân 2] Lỗi: " + e.getMessage(), "#DC2626");
            }
        }, "DemoPhantomRead-BgUpdate");
    }

    // ── Đọc số sản phẩm còn hàng trên connection đang mở ───────────────────
    private int demSanPhamConHang(Connection conn) throws SQLException {
        String sql = "SELECT COUNT(*) AS TONG FROM SANPHAM WHERE SOLUONGTON > 0";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt("TONG") : 0;
        }
    }

    // ── Hiển thị kết quả so sánh ────────────────────────────────────────────
    private void hienThiKetQua(int lan1, int lan2, boolean dungSerializable) {
        boxKetQua.setVisible(true);
        lblLan1.setText("Lần đọc 1:  " + lan1 + " sản phẩm");
        lblLan2.setText("Lần đọc 2:  " + lan2 + " sản phẩm");

        if (lan1 != lan2) {
            lblNhanXet.setText("⚠ PHANTOM READ! Cùng 1 query, cùng transaction\nnhưng trả về 2 kết quả khác nhau!");
            lblNhanXet.setStyle("-fx-text-fill: #DC2626; -fx-font-size: 14px; -fx-font-weight: bold;");
            lblKetQua.setText("❌ READ COMMITTED — Dữ liệu KHÔNG nhất quán");
            lblKetQua.setStyle("-fx-text-fill: #DC2626; -fx-font-weight: bold;");
        } else {
            lblNhanXet.setText("✅ Nhất quán! Transaction nhìn thấy snapshot cố định\ntừ lúc bắt đầu — không bị ảnh hưởng bởi commit khác.");
            lblNhanXet.setStyle("-fx-text-fill: #059669; -fx-font-size: 14px; -fx-font-weight: bold;");
            lblKetQua.setText("✅ SERIALIZABLE — Dữ liệu NHẤT QUÁN");
            lblKetQua.setStyle("-fx-text-fill: #059669; -fx-font-weight: bold;");
        }
    }

    // ── Helpers ─────────────────────────────────────────────────────────────
    private void logUi(String msg, String hex) {
        Platform.runLater(() -> ghiLog(msg, hex));
    }

    private void ghiLog(String msg, String hex) {
        Text t = new Text("[" + gioHienTai() + "] " + msg + "\n");
        t.setFill(Color.web(hex));
        t.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 13px;");
        txtFlowLog.getChildren().add(t);
    }

    private String gioHienTai() {
        return LocalTime.now().format(GIO);
    }
}
