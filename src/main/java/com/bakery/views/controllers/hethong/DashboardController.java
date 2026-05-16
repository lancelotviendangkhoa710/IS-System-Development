package com.bakery.views.controllers.hethong;

import com.bakery.model.dto.nhansu.NhanVienDTO;
import com.bakery.services.baocao.ThongKeService;
import com.bakery.utils.UserSession;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Duration;

import java.net.URL;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Logger;

/**
 * Dashboard mới — hiển thị biểu đồ doanh thu 7 ngày + bảng thống kê theo ngày.
 * Không còn card điều hướng — navigation qua AppShell sidebar.
 */
public class DashboardController implements Initializable {

    private static final Logger LOGGER = Logger.getLogger(DashboardController.class.getName());
    private static final NumberFormat NF_TIEN = NumberFormat.getNumberInstance(Locale.forLanguageTag("vi-VN"));

    static { NF_TIEN.setMaximumFractionDigits(0); }

    // ── FXML ────────────────────────────────────────────────────────────────
    @FXML private Label    lblBannerName;
    @FXML private Label    lblThongBao;
    @FXML private Label    lblCapNhatChart;
    @FXML private Label    lblCapNhatBang;

    @FXML private LineChart<String, Number>    chartDoanhThu;
    @FXML private CategoryAxis               axisNgay;
    @FXML private NumberAxis                 axisDoanhThu;

    @FXML private TableView<String[]>        tblThongKe;
    @FXML private TableColumn<String[], String> colNgay;
    @FXML private TableColumn<String[], String> colDoanhThu;
    @FXML private TableColumn<String[], String> colDonHoanThanh;
    @FXML private TableColumn<String[], String> colDonHuy;
    @FXML private TableColumn<String[], String> colTongDon;

    // ── State ────────────────────────────────────────────────────────────────
    private final ThongKeService thongKeService = new ThongKeService();
    private final ObservableList<String[]> dsThongKe = FXCollections.observableArrayList();

    /** Timeline animation pulse cho BarChart — chạy liên tục để chart sinh động. */
    private Timeline animationTimeline;

    // ── Lifecycle ────────────────────────────────────────────────────────────

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        khoiTaoTable();
        khoiTaoChart();
        hienThiTenUser();
        taiDuLieuAsync();
        batDauAnimationChart();
    }

    // ── Setup ────────────────────────────────────────────────────────────────

    private void hienThiTenUser() {
        NhanVienDTO user = UserSession.getCurrentUser();
        if (lblBannerName != null && user != null) {
            lblBannerName.setText(user.getHoTen() != null ? user.getHoTen() : user.getTenDangNhap());
        }
    }

    private void khoiTaoChart() {
        axisNgay.setLabel("Ngày");
        axisDoanhThu.setLabel("Doanh thu (đ)");
        axisDoanhThu.setForceZeroInRange(true);
        axisDoanhThu.setTickLabelFormatter(new javafx.util.StringConverter<Number>() {
            @Override public String toString(Number n) {
                if (n == null) return "0";
                double v = n.doubleValue();
                if (v >= 1_000_000) return NF_TIEN.format(v / 1_000_000) + "M";
                if (v >= 1_000)     return NF_TIEN.format(v / 1_000) + "K";
                return NF_TIEN.format(v);
            }
            @Override public Number fromString(String s) { return 0; }
        });
        chartDoanhThu.setLegendVisible(false);
        chartDoanhThu.setAnimated(true);
        chartDoanhThu.setCreateSymbols(true);
    }

    private void khoiTaoTable() {
        colNgay.setCellValueFactory(c -> new SimpleStringProperty(c.getValue()[0]));
        colDoanhThu.setCellValueFactory(c -> {
            try {
                double v = Double.parseDouble(c.getValue()[1]);
                return new SimpleStringProperty(NF_TIEN.format(v) + " đ");
            } catch (NumberFormatException e) {
                return new SimpleStringProperty(c.getValue()[1]);
            }
        });
        colDonHoanThanh.setCellValueFactory(c -> new SimpleStringProperty(c.getValue()[2]));
        colDonHuy.setCellValueFactory(c -> new SimpleStringProperty(c.getValue()[3]));
        colTongDon.setCellValueFactory(c -> new SimpleStringProperty(c.getValue()[4]));
        tblThongKe.setItems(dsThongKe);
        tblThongKe.setPlaceholder(new Label("Đang tải dữ liệu..."));
    }

    // ── Data Loading ─────────────────────────────────────────────────────────

    /**
     * Load dữ liệu thống kê từ DB trên background thread.
     * Cập nhật UI trên FX thread qua Platform.runLater().
     */
    private void taiDuLieuAsync() {
        Thread t = new Thread(() -> {
            try {
                List<String[]> dsRaw = thongKeService.getThongKeTheoNgay();
                Platform.runLater(() -> {
                    capNhatChart(dsRaw);
                    capNhatBang(dsRaw);
                    String gio = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
                    if (lblCapNhatChart != null) lblCapNhatChart.setText("Cập nhật lúc " + gio);
                    if (lblCapNhatBang  != null) lblCapNhatBang.setText("Cập nhật lúc " + gio);
                    if (lblThongBao    != null) lblThongBao.setText("");
                });
            } catch (Exception ex) {
                LOGGER.warning("[Dashboard] Loi tai du lieu: " + ex.getMessage());
                Platform.runLater(() -> {
                    if (lblThongBao != null) {
                        lblThongBao.getStyleClass().setAll("lbl-danger");
                        lblThongBao.setText("⚠ Không thể tải dữ liệu thống kê: " + ex.getMessage());
                    }
                });
            }
        }, "dash-load");
        t.setDaemon(true);
        t.start();
    }

    private void capNhatChart(List<String[]> dsRaw) {
        // Map ngày → doanh thu từ data DB
        Map<String, Double> mapData = new LinkedHashMap<>();
        for (String[] row : dsRaw) {
            try { mapData.put(row[0], Double.parseDouble(row[1])); }
            catch (NumberFormatException ignored) {}
        }

        // Pre-fill đủ 7 nhãn ngày (DD/MM) để trục X luôn hiện đủ
        DateTimeFormatter fmtNhan = DateTimeFormatter.ofPattern("dd/MM");
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Doanh thu");
        for (int i = 6; i >= 0; i--) {
            String nhan = LocalDate.now().minusDays(i).format(fmtNhan);
            series.getData().add(new XYChart.Data<>(nhan, mapData.getOrDefault(nhan, 0.0)));
        }
        chartDoanhThu.getData().setAll(series);
    }

    private void capNhatBang(List<String[]> dsRaw) {
        dsThongKe.setAll(dsRaw);
        tblThongKe.setPlaceholder(new Label("Không có dữ liệu trong 7 ngày gần nhất."));
    }

    // ── Animation ─────────────────────────────────────────────────────────────

    /**
     * Animation pulse liên tục: mỗi 3s, fade-out → fade-in toàn bộ chart
     * để tạo hiệu ứng "đang live". Gọi 1 lần trong initialize().
     */
    private void batDauAnimationChart() {
        if (chartDoanhThu == null) return;

        FadeTransition fadeOut = new FadeTransition(Duration.millis(600), chartDoanhThu);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.6);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(600), chartDoanhThu);
        fadeIn.setFromValue(0.6);
        fadeIn.setToValue(1.0);

        SequentialTransition pulse = new SequentialTransition(fadeOut, fadeIn);

        // Lặp lại mỗi 3 giây để chart "thở"
        animationTimeline = new Timeline(
                new KeyFrame(Duration.seconds(3), evt -> pulse.playFromStart())
        );
        animationTimeline.setCycleCount(Animation.INDEFINITE);
        animationTimeline.play();

        // Tự dừng khi chart bị remove khỏi scene (AppShell thay nội dung)
        chartDoanhThu.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene == null && animationTimeline != null) {
                animationTimeline.stop();
                LOGGER.info("[Dashboard] Animation dừng — scene removed.");
            }
        });
    }
}
