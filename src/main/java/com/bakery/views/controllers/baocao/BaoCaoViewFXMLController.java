package com.bakery.views.controllers.baocao;

import com.bakery.services.baocao.ThongKeService;
import com.bakery.utils.JasperReportUtils;
import com.bakery.utils.ReportPathUtils;
import com.bakery.utils.UserSession;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class BaoCaoViewFXMLController extends BaseController {

    @FXML private Label lblAdminName;
    @FXML private LineChart<String, Number> revenueChart;
    @FXML private TableView<String[]> tableGiaoDich;
    @FXML private Label lblDoanhThu;
    @FXML private Label lblChenhLechDoanhThu;
    @FXML private Label lblLoiNhuan;
    @FXML private Label lblTongDon;
    @FXML private Label lblKhachHang;
    @FXML private VBox vboxBestSellers;
    @FXML private PieChart revenuePieChart;
    @FXML private BarChart<String, Number> revenueBarChart;
    
    @FXML private ComboBox<String> cbLoaiBaoCao;
    @FXML private DatePicker dpNgayBaoCao;
    @FXML private TabPane tabPaneBaoCao;
    @FXML private Tab tabThongKeKinhDoanh;
    @FXML private Tab tabSoQuyThuChi;
    @FXML private Tab tabGiamSatCa;
    @FXML private Tab tabQuanLyLoai;

    // Biểu đồ doanh thu theo tháng
    @FXML private BarChart<String, Number> monthlyBarChart;
    @FXML private ComboBox<Integer>        cbNamBieuDo;

    // Biểu đồ lợi nhuận theo tháng
    @FXML private BarChart<String, Number> monthlyProfitBarChart;
    @FXML private ComboBox<Integer>        cbNamBieuDoLoiNhuan;

    // Product Profit Tab Components
    @FXML private Tab tabLoiNhuanSanPham;
    @FXML private TableView<String[]> tblLoiNhuanSanPham;
    @FXML private TableColumn<String[], String> colLoiNhuanTenSP;
    @FXML private TableColumn<String[], String> colLoiNhuanTenDM;
    @FXML private TableColumn<String[], String> colLoiNhuanSoLuong;
    @FXML private TableColumn<String[], String> colLoiNhuanDoanhThu;
    @FXML private TableColumn<String[], String> colLoiNhuanGiaVon;
    @FXML private TableColumn<String[], String> colLoiNhuanLoiNhuan;
    @FXML private TableColumn<String[], String> colLoiNhuanTyle;

    @FXML private Label lblLoiNhuanSPSinhLoiNhat;
    @FXML private Label lblLoiNhuanSPSinhLoiNhatGiaTri;
    @FXML private Label lblLoiNhuanSPBanChayNhat;
    @FXML private Label lblLoiNhuanSPBanChayNhatGiaTri;
    @FXML private Label lblLoiNhuanBienLNTB;

    private ThongKeService thongKeService = new ThongKeService();

    @FXML
    public void initialize() {
        if (UserSession.getCurrentUser() != null) {
            String name = UserSession.getCurrentUser().getHoTen();
            if (name == null || name.isEmpty()) {
                name = UserSession.getCurrentUser().getTenDangNhap();
            }
            lblAdminName.setText(name);
        }

        setupLoiNhuanSanPhamTableColumns();
        setupFilters();
        refreshData();
        if (tabPaneBaoCao != null && tabThongKeKinhDoanh != null) {
            tabPaneBaoCao.getSelectionModel().select(tabThongKeKinhDoanh);
        }
        setupNamBieuDo();
    }

    /**
     * Chuyển tab trong BaoCaoView từ MainMenu.
     * tabKey: "thongke" | "soquy" | "giamsatca" | "quanlyloai"
     */
    public void chuyenTab(String tabKey) {
        if (tabPaneBaoCao == null || tabKey == null) return;
        Tab target = switch (tabKey.toLowerCase()) {
            case "soquy"       -> tabSoQuyThuChi;
            case "giamsatca"   -> tabGiamSatCa;
            case "quanlyloai" -> tabQuanLyLoai;
            default            -> tabThongKeKinhDoanh;
        };
        if (target != null) {
            tabPaneBaoCao.getSelectionModel().select(target);
        }
    }



    // ── Biểu đồ theo tháng ───────────────────────────────────────────────────

    /** Khởi tạo ComboBox năm và tải dữ liệu biểu đồ tháng lần đầu. */
    private void setupNamBieuDo() {
        int namHienTai = java.time.LocalDate.now().getYear();

        // ── Biểu đồ doanh thu ──────────────────────────────────────────────
        if (cbNamBieuDo != null) {
            for (int y = namHienTai; y >= namHienTai - 4; y--)
                cbNamBieuDo.getItems().add(y);
            cbNamBieuDo.setValue(namHienTai);
            cbNamBieuDo.valueProperty().addListener((obs, oldY, newY) -> {
                if (newY != null) capNhatBieuDoTheoThang(newY);
            });
            capNhatBieuDoTheoThang(namHienTai);
        }

        // ── Biểu đồ lợi nhuận ──────────────────────────────────────────────
        if (cbNamBieuDoLoiNhuan != null) {
            for (int y = namHienTai; y >= namHienTai - 4; y--)
                cbNamBieuDoLoiNhuan.getItems().add(y);
            cbNamBieuDoLoiNhuan.setValue(namHienTai);
            cbNamBieuDoLoiNhuan.valueProperty().addListener((obs, oldY, newY) -> {
                if (newY != null) capNhatBieuDoLoiNhuanTheoThang(newY);
            });
            capNhatBieuDoLoiNhuanTheoThang(namHienTai);
        }
    }

    /**
     * Tải dữ liệu doanh thu 12 tháng của năm chỉ định và cập nhật monthlyBarChart.
     * Chạy DB query trên Thread phụ, update UI qua Platform.runLater.
     */
    private void capNhatBieuDoTheoThang(int nam) {
        if (monthlyBarChart == null) return;
        new Thread(() -> {
            try {
                Map<String, Double> data = thongKeService.getDoanhThu12ThangTrongNam(nam);
                XYChart.Series<String, Number> series = new XYChart.Series<>();
                series.setName("Doanh thu " + nam);
                for (Map.Entry<String, Double> entry : data.entrySet()) {
                    series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
                }
                javafx.application.Platform.runLater(() -> {
                    monthlyBarChart.getData().clear();
                    monthlyBarChart.getData().add(series);
                    // Tô màu gradient amber → đậm dần theo tháng
                    String[] colors = {
                        "#FFCDB2","#FFB4A2","#E5989B","#B5838D",
                        "#D85A30","#C45226","#AE4520","#993C1D",
                        "#833219","#6D2A15","#572210","#3E1A0C"
                    };
                    int idx = 0;
                    for (XYChart.Data<String, Number> d : series.getData()) {
                        final String color = colors[idx % colors.length];
                        if (d.getNode() != null) {
                            d.getNode().setStyle("-fx-bar-fill: " + color + ";");
                        }
                        d.nodeProperty().addListener((obs, oldN, newN) -> {
                            if (newN != null) newN.setStyle("-fx-bar-fill: " + color + ";");
                        });
                        idx++;
                    }
                });
            } catch (Exception e) {
                javafx.application.Platform.runLater(() ->
                    hienThiCanhBao("Lỗi biểu đồ tháng", e.getMessage()));
            }
        }, "monthly-chart-loader").start();
    }

    /**
     * Tải dữ liệu lợi nhuận 12 tháng và cập nhật monthlyProfitBarChart.
     * Cột xanh lá = lợi nhuận dương, cột đỏ = lợi nhuận âm.
     * Chạy DB query trên Thread phụ, update UI qua Platform.runLater.
     */
    private void capNhatBieuDoLoiNhuanTheoThang(int nam) {
        if (monthlyProfitBarChart == null) return;
        new Thread(() -> {
            try {
                Map<String, Double> data = thongKeService.getLoiNhuan12ThangTrongNam(nam);
                XYChart.Series<String, Number> series = new XYChart.Series<>();
                series.setName("Lợi nhuận " + nam);
                for (Map.Entry<String, Double> entry : data.entrySet()) {
                    series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
                }
                javafx.application.Platform.runLater(() -> {
                    monthlyProfitBarChart.getData().clear();
                    monthlyProfitBarChart.getData().add(series);
                    // Xanh lá = dương, đỏ = âm
                    for (XYChart.Data<String, Number> d : series.getData()) {
                        double val = d.getYValue().doubleValue();
                        final String color = val >= 0 ? "#16A34A" : "#DC2626";
                        if (d.getNode() != null) {
                            d.getNode().setStyle("-fx-bar-fill: " + color + ";");
                        }
                        d.nodeProperty().addListener((obs, oldN, newN) -> {
                            if (newN != null) newN.setStyle("-fx-bar-fill: " + color + ";");
                        });
                    }
                });
            } catch (Exception e) {
                javafx.application.Platform.runLater(() ->
                    hienThiCanhBao("Lỗi biểu đồ lợi nhuận", e.getMessage()));
            }
        }, "monthly-profit-chart-loader").start();
    }

    // ── Bộ lọc báo cáo kinh doanh ────────────────────────────────────────────

    private void setupFilters() {
        cbLoaiBaoCao.setItems(FXCollections.observableArrayList("Ngày", "Tuần", "Tháng", "Quý", "Năm"));
        cbLoaiBaoCao.setValue("Ngày");
        dpNgayBaoCao.setValue(LocalDate.now());

        cbLoaiBaoCao.valueProperty().addListener((obs, oldVal, newVal) -> refreshData());
        dpNgayBaoCao.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && newVal.isAfter(LocalDate.now())) {
                hienThiCanhBao("Ngày không hợp lệ", "Bạn không thể xem báo cáo cho tương lai.");
                dpNgayBaoCao.setValue(LocalDate.now());
            } else {
                refreshData();
            }
        });
    }

    private void refreshData() {
        try {
            String loaiStr = cbLoaiBaoCao.getValue();
            LocalDate ngay = dpNgayBaoCao.getValue();
            if (ngay == null)
                ngay = LocalDate.now();

            String loai = "DAY";
            String giaTri = ngay.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

            if ("Tuần".equals(loaiStr)) {
                loai = "WEEK";
                // Tìm Thứ Hai của tuần chứa ngày được chọn
                java.time.LocalDate monday = ngay.with(java.time.DayOfWeek.MONDAY);
                giaTri = monday.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            } else if ("Tháng".equals(loaiStr)) {
                loai = "MONTH";
                giaTri = ngay.format(DateTimeFormatter.ofPattern("MM/yyyy"));
            } else if ("Quý".equals(loaiStr)) {
                loai = "QUARTER";
                int quarter = (ngay.getMonthValue() - 1) / 3 + 1;
                giaTri = quarter + "/" + ngay.getYear();
            } else if ("Năm".equals(loaiStr)) {
                loai = "YEAR";
                giaTri = String.valueOf(ngay.getYear());
            }

            double doanhThu = thongKeService.getDoanhThu(loai, giaTri);
            double giaVon   = thongKeService.getGiaVon(loai, giaTri);
            double loiNhuan = doanhThu - giaVon;
            int tongDon      = thongKeService.getTongDon(loai, giaTri);
            int khachTichDiem = thongKeService.getKhachTichDiem(loai, giaTri);
            lblDoanhThu.setText(String.format("%,.0fđ", doanhThu));
            lblLoiNhuan.setText(String.format("%,.0fđ", loiNhuan));
            if (lblTongDon   != null) lblTongDon.setText(String.valueOf(tongDon));
            if (lblKhachHang != null) lblKhachHang.setText(String.valueOf(khachTichDiem));

            updateChart(loai, giaTri);
            updateCategoryCharts(loai, giaTri);
            updateTable(loai, giaTri);
            loadTopSellers();
            updateLoiNhuanSanPhamTable(loai, giaTri);
        } catch (Exception e) {
            hienThiCanhBao("Lỗi tải dữ liệu", "Không thể tải dữ liệu báo cáo: " + e.getMessage());
        }
    }

    private void updateCategoryCharts(String loai, String giaTri) throws Exception {
        Map<String, Double> categoryData = thongKeService.getDoanhThuTheoDanhMuc(loai, giaTri);
        
        // Update Pie Chart
        revenuePieChart.getData().clear();
        for (Map.Entry<String, Double> entry : categoryData.entrySet()) {
            revenuePieChart.getData().add(new PieChart.Data(entry.getKey(), entry.getValue()));
        }

        // Update Bar Chart
        XYChart.Series<String, Number> barSeries = new XYChart.Series<>();
        for (Map.Entry<String, Double> entry : categoryData.entrySet()) {
            barSeries.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }
        revenueBarChart.getData().clear();
        revenueBarChart.getData().add(barSeries);

        String[] colors = {"#D85A30", "#3E442B", "#6D4C3D", "#A5A58D", "#B5838D", "#E5989B", "#FFB4A2", "#FFCDB2"};
        int index = 0;
        for (XYChart.Data<String, Number> data : barSeries.getData()) {
            String color = colors[index % colors.length];
            if (data.getNode() != null) {
                data.getNode().setStyle("-fx-bar-fill: " + color + ";");
            }
            data.nodeProperty().addListener((obs, oldNode, newNode) -> {
                if (newNode != null) {
                    newNode.setStyle("-fx-bar-fill: " + color + ";");
                }
            });
            index++;
        }
    }

    private void loadTopSellers() throws Exception {
        Map<String, Integer> top5 = thongKeService.getTop5BanChay();
        int maxQty = top5.values().stream().max(Integer::compareTo).orElse(1);
        if (maxQty == 0) maxQty = 1;

        vboxBestSellers.getChildren().removeIf(node -> node instanceof VBox); // Clear old list but keep title/region

        VBox listBestSellers = new VBox(16);
        for (Map.Entry<String, Integer> entry : top5.entrySet()) {
            VBox itemBox = new VBox(4);
            HBox titleRow = new HBox();
            Label lblName = new Label(entry.getKey());
            lblName.getStyleClass().add("best-seller-name");
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            Label lblQty = new Label(entry.getValue() + " cái");
            lblQty.getStyleClass().add("best-seller-qty");
            titleRow.getChildren().addAll(lblName, spacer, lblQty);

            ProgressBar progress = new ProgressBar((double) entry.getValue() / maxQty);
            progress.setPrefWidth(300);
            progress.setPrefHeight(8);
            progress.getStyleClass().add("progress-bar-amber");
            
            itemBox.getChildren().addAll(titleRow, progress);
            listBestSellers.getChildren().add(itemBox);
        }
        vboxBestSellers.getChildren().add(1, listBestSellers);
    }

    private void updateChart(String loai, String giaTri) throws Exception {
        Map<String, Double> chartData = thongKeService.getXuHuongDoanhThu(loai, giaTri);
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        // Nhãn series theo loại kỳ
        String seriesLabel = switch (loai) {
            case "DAY"     -> "Theo giờ (" + giaTri + ")";
            case "WEEK"    -> "Theo tuần (" + giaTri + ")";
            case "MONTH"   -> "Theo ngày (" + giaTri + ")";
            case "QUARTER" -> "Theo tháng (Q" + giaTri + ")";
            case "YEAR"    -> "Theo tháng (" + giaTri + ")";
            default        -> "Doanh Thu";
        };
        series.setName(seriesLabel);
        for (Map.Entry<String, Double> entry : chartData.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }
        revenueChart.getData().clear();
        revenueChart.getData().add(series);
    }

    private void updateTable(String loai, String giaTri) throws Exception {
        // Setup columns if first time
        if (tableGiaoDich.getColumns().get(0).getCellValueFactory() == null) {
            setupTableColumns();
        }
        List<String[]> data = thongKeService.getChiTietGiaoDich(loai, giaTri);
        tableGiaoDich.setItems(FXCollections.observableArrayList(data));
    }

    @SuppressWarnings("unchecked")
    private void setupTableColumns() {
        TableColumn<String[], String> colId = (TableColumn<String[], String>) tableGiaoDich.getColumns().get(0);
        colId.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()[0]));
        TableColumn<String[], String> colKhach = (TableColumn<String[], String>) tableGiaoDich.getColumns().get(1);
        colKhach.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()[1]));
        TableColumn<String[], String> colMon = (TableColumn<String[], String>) tableGiaoDich.getColumns().get(2);
        colMon.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()[2]));
        TableColumn<String[], String> colTien = (TableColumn<String[], String>) tableGiaoDich.getColumns().get(3);
        colTien.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()[3]));
        TableColumn<String[], String> colTrangThai = (TableColumn<String[], String>) tableGiaoDich.getColumns().get(4);
        colTrangThai.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()[4]));
    }

    private void setupLoiNhuanSanPhamTableColumns() {
        if (colLoiNhuanTenSP != null) {
            colLoiNhuanTenSP.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()[0]));
            colLoiNhuanTenDM.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()[1]));
            colLoiNhuanSoLuong.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()[2]));
            colLoiNhuanDoanhThu.setCellValueFactory(cellData -> {
                double val = Double.parseDouble(cellData.getValue()[3]);
                return new SimpleStringProperty(String.format("%,.0fđ", val));
            });
            colLoiNhuanGiaVon.setCellValueFactory(cellData -> {
                double val = Double.parseDouble(cellData.getValue()[4]);
                return new SimpleStringProperty(String.format("%,.0fđ", val));
            });
            colLoiNhuanLoiNhuan.setCellValueFactory(cellData -> {
                double val = Double.parseDouble(cellData.getValue()[5]);
                return new SimpleStringProperty(String.format("%,.0fđ", val));
            });
            colLoiNhuanTyle.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()[6]));
        }
    }

    private void updateLoiNhuanSanPhamTable(String loai, String giaTri) {
        if (tblLoiNhuanSanPham == null) return;
        new Thread(() -> {
            try {
                List<String[]> data = thongKeService.getLoiNhuanSanPham(loai, giaTri);
                
                // Calculate KPIs
                String bestProfitProduct = "Chưa có";
                double maxProfit = -Double.MAX_VALUE;
                double bestProfitVal = 0;

                String bestQtyProduct = "Chưa có";
                int maxQty = -1;

                double totalRevenue = 0;
                double totalProfit = 0;

                for (String[] row : data) {
                    String tenSP = row[0];
                    int qty = Integer.parseInt(row[2]);
                    double rev = Double.parseDouble(row[3]);
                    double prof = Double.parseDouble(row[5]);

                    totalRevenue += rev;
                    totalProfit += prof;

                    if (prof > maxProfit) {
                        maxProfit = prof;
                        bestProfitProduct = tenSP;
                        bestProfitVal = prof;
                    }

                    if (qty > maxQty) {
                        maxQty = qty;
                        bestQtyProduct = tenSP;
                    }
                }

                final String finalBestProfitProduct = bestProfitProduct;
                final double finalBestProfitVal = bestProfitVal;
                final String finalBestQtyProduct = bestQtyProduct;
                final int finalMaxQty = maxQty;
                final double finalAvgMargin = totalRevenue > 0 ? (totalProfit / totalRevenue) * 100 : 0.0;

                javafx.application.Platform.runLater(() -> {
                    tblLoiNhuanSanPham.setItems(FXCollections.observableArrayList(data));
                    
                    if (data.isEmpty()) {
                        lblLoiNhuanSPSinhLoiNhat.setText("Không có dữ liệu");
                        lblLoiNhuanSPSinhLoiNhatGiaTri.setText("0đ");
                        lblLoiNhuanSPBanChayNhat.setText("Không có dữ liệu");
                        lblLoiNhuanSPBanChayNhatGiaTri.setText("0 cái");
                        lblLoiNhuanBienLNTB.setText("0.0%");
                    } else {
                        lblLoiNhuanSPSinhLoiNhat.setText(finalBestProfitProduct);
                        lblLoiNhuanSPSinhLoiNhatGiaTri.setText(String.format("%,.0fđ", finalBestProfitVal));
                        lblLoiNhuanSPBanChayNhat.setText(finalBestQtyProduct);
                        lblLoiNhuanSPBanChayNhatGiaTri.setText(finalMaxQty + " cái");
                        lblLoiNhuanBienLNTB.setText(String.format("%.1f%%", finalAvgMargin));
                    }
                });
            } catch (Exception e) {
                javafx.application.Platform.runLater(() ->
                        hienThiCanhBao("Lỗi tải dữ liệu sản phẩm", e.getMessage()));
            }
        }, "product-profit-loader").start();
    }

    // onTaiBaoCaoNhanh đã được xóa — thay thế bởi Jasper PDF/Excel


    // ── JasperReports: Xuất PDF chuyên nghiệp ───────────────────────────────

    /**
     * Xuất báo cáo kinh doanh sang PDF bằng JasperReports.
     * Template: /reports/bao_cao/bao_cao_kinh_doanh.jrxml
     */
    @FXML
    private void onXuatJasperPDF() {
        xuatBaoCaoJasper(false);
    }

    /**
     * Xuất báo cáo kinh doanh sang Excel (.xlsx) bằng JasperReports.
     */
    @FXML
    private void onXuatJasperExcel() {
        xuatBaoCaoJasper(true);
    }

    /**
     * Logic chung: fill data → xuất PDF hoặc Excel.
     *
     * @param excel true = xuất Excel, false = xuất PDF
     */
    private void xuatBaoCaoJasper(boolean excel) {
        String loaiStr  = cbLoaiBaoCao.getValue() != null ? cbLoaiBaoCao.getValue() : "Ngày";
        LocalDate ngay  = dpNgayBaoCao.getValue() != null ? dpNgayBaoCao.getValue() : LocalDate.now();

        // Xác định kỳ báo cáo
        String loai = "DAY";
        String giaTri = ngay.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        if ("Tuần".equals(loaiStr)) {
            loai = "WEEK";
            java.time.LocalDate monday = ngay.with(java.time.DayOfWeek.MONDAY);
            giaTri = monday.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        } else if ("Tháng".equals(loaiStr)) { loai = "MONTH"; giaTri = ngay.format(DateTimeFormatter.ofPattern("MM/yyyy")); }
        else if ("Quý".equals(loaiStr))  { loai = "QUARTER"; giaTri = ((ngay.getMonthValue()-1)/3+1) + "/" + ngay.getYear(); }
        else if ("Năm".equals(loaiStr))  { loai = "YEAR";  giaTri = String.valueOf(ngay.getYear()); }

        String tieuDe    = "Báo cáo kinh doanh — " + loaiStr + " " + giaTri;
        String kyBaoCao  = loaiStr + " " + giaTri;
        String nguoiXuat = UserSession.getCurrentUser() != null
                ? UserSession.getCurrentUser().getHoTen() : "Hệ thống";

        // Build file đích
        String suffix = loaiStr + "_" + ngay.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        File outputFile = excel
                ? ReportPathUtils.buildExcelPath("BaoCaoKinhDoanh", suffix)
                : ReportPathUtils.buildPdfPath("BaoCaoKinhDoanh", suffix);

        // [PDF only] Snapshot biểu đồ LineChart TRÊN FX thread trước khi vào background
        final InputStream chartStream = excel ? null : snapshotBieuDo();

        final String finalLoai = loai;
        final String finalGiaTri = giaTri;

        new Thread(() -> {
            try {
                // Lấy data từ DB trong background thread
                double doanhThu = thongKeService.getDoanhThu(finalLoai, finalGiaTri);
                double giaVon   = thongKeService.getGiaVon(finalLoai, finalGiaTri);
                double loiNhuan = doanhThu - giaVon;
                List<String[]> rows = thongKeService.getChiTietGiaoDich(finalLoai, finalGiaTri);

                String strDoanhThu = String.format("%,.0f ₫", doanhThu);
                String strGiaVon   = String.format("%,.0f ₫", giaVon);
                String strLoiNhuan = String.format("%,.0f ₫", loiNhuan);
                String strTongGD   = String.valueOf(rows.size());

                if (excel) {
                    JasperReportUtils.xuatExcel(outputFile, tieuDe, kyBaoCao,
                            strDoanhThu, strGiaVon, strLoiNhuan, strTongGD, nguoiXuat, rows);
                } else {
                    // Truyền chartStream (nullable) vào PDF — biểu đồ sẽ được nhúng nếu != null
                    JasperReportUtils.xuatPDF(outputFile, tieuDe, kyBaoCao,
                            strDoanhThu, strGiaVon, strLoiNhuan, strTongGD, nguoiXuat, rows, chartStream);
                }

                javafx.application.Platform.runLater(() -> {
                    try {
                        if (java.awt.Desktop.isDesktopSupported())
                            java.awt.Desktop.getDesktop().open(outputFile);
                    } catch (Exception ignored) { }
                    hienThiThongTin("Xuất báo cáo thành công",
                        (excel ? "Excel" : "PDF") + " đã lưu tại:\n" + outputFile.getAbsolutePath());
                });

            } catch (Exception e) {
                javafx.application.Platform.runLater(() ->
                    hienThiThongBaoLoi("Lỗi xuất báo cáo",
                        "Không thể xuất " + (excel ? "Excel" : "PDF") + ": " + e.getMessage()));
            }
        }, "jasper-export").start();
    }

    /**
     * Chụp ảnh snapshot của revenueChart (LineChart) thành InputStream PNG.
     * PHẢI gọi trên FX Application Thread.
     * Trả null nếu chart chưa sẵn sàng hoặc có lỗi.
     */
    private InputStream snapshotBieuDo() {
        if (revenueChart == null) return null;
        try {
            javafx.scene.SnapshotParameters params = new javafx.scene.SnapshotParameters();
            params.setFill(javafx.scene.paint.Color.WHITE);
            javafx.scene.image.WritableImage img = revenueChart.snapshot(params, null);

            // Dùng reflection để tránh tham chiếu trực tiếp SwingFXUtils (unnamed module)
            // → vượt module boundary mà không cần requires javafx.swing trong module-info
            var swingFXUtilsClass = Class.forName("javafx.embed.swing.SwingFXUtils");
            var fromFXImageMethod = swingFXUtilsClass.getMethod("fromFXImage",
                    javafx.scene.image.Image.class,
                    Class.forName("java.awt.image.BufferedImage"));
            var bImg = fromFXImageMethod.invoke(null, img, null);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            // Gọi ImageIO.write qua reflection để tránh tham chiếu trực tiếp javax.imageio.ImageIO
            // → IDE không cần java.desktop visible; runtime luôn có java.desktop
            var imageIOClass  = Class.forName("javax.imageio.ImageIO");
            var renderedClass = Class.forName("java.awt.image.RenderedImage");
            var writeMethod   = imageIOClass.getMethod("write", renderedClass, String.class,
                                                       java.io.OutputStream.class);
            writeMethod.invoke(null, bImg, "png", baos);

            return new ByteArrayInputStream(baos.toByteArray());
        } catch (Exception e) {
            System.err.println("[BaoCao] Không thể snapshot biểu đồ: " + e.getMessage());
            return null;
        }
    }

    /**
     * Xuất báo cáo lịch sử mua hàng (chi tiết giao dịch) sang PDF.
     * Template: /reports/khach_hang/lich_su_mua_hang.jrxml
     */
    @FXML
    private void onXuatLichSuMuaHang() {
        String loaiStr  = cbLoaiBaoCao.getValue() != null ? cbLoaiBaoCao.getValue() : "Ngày";
        LocalDate ngay  = dpNgayBaoCao.getValue() != null ? dpNgayBaoCao.getValue() : LocalDate.now();

        String loai     = "DAY";
        String giaTri   = ngay.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        if ("Tháng".equals(loaiStr)) { loai = "MONTH"; giaTri = ngay.format(DateTimeFormatter.ofPattern("MM/yyyy")); }
        else if ("Quý".equals(loaiStr))  { loai = "QUARTER"; giaTri = ((ngay.getMonthValue()-1)/3+1) + "/" + ngay.getYear(); }
        else if ("Năm".equals(loaiStr))  { loai = "YEAR";  giaTri = String.valueOf(ngay.getYear()); }

        String tieuDe   = "Lịch sử mua hàng — " + loaiStr + " " + giaTri;
        String nguoiXuat = UserSession.getCurrentUser() != null
                ? UserSession.getCurrentUser().getHoTen() : "Hệ thống";
        String suffix    = "LichSu_" + ngay.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        File outputFile  = ReportPathUtils.buildPdfPath("LichSuMuaHang", suffix);
        final String finalLoai = loai;
        final String finalGiaTri = giaTri;

        new Thread(() -> {
            try {
                // getChiTietGiaoDich trả về {maDon, tenKhach, monHang, soTien, trangThai}
                // → cần bổ sung ngayMua và soLuong — dùng maDon và ngày kỳ làm placeholder
                List<String[]> raw = thongKeService.getChiTietGiaoDich(finalLoai, finalGiaTri);
                double tongTien = thongKeService.getDoanhThu(finalLoai, finalGiaTri);

                // Map sang format lịch sử: {maDon, ngayMua, tenKhach, monHang, soLuong, soTien, trangThai}
                java.util.List<String[]> rows = new java.util.ArrayList<>();
                for (String[] r : raw) {
                    rows.add(new String[]{
                        r.length > 0 ? r[0] : "",   // maDon
                        finalGiaTri,                  // ngayMua (kỳ báo cáo)
                        r.length > 1 ? r[1] : "",   // tenKhach
                        r.length > 2 ? r[2] : "",   // monHang
                        "1",                          // soLuong placeholder
                        r.length > 3 ? r[3] : "",   // soTien
                        r.length > 4 ? r[4] : ""    // trangThai
                    });
                }

                JasperReportUtils.xuatLichSuMuaHangPDF(
                        outputFile, tieuDe, finalGiaTri, finalGiaTri,
                        String.valueOf(rows.size()),
                        String.format("%,.0f ₫", tongTien),
                        nguoiXuat, rows);

                javafx.application.Platform.runLater(() -> {
                    try {
                        if (java.awt.Desktop.isDesktopSupported())
                            java.awt.Desktop.getDesktop().open(outputFile);
                    } catch (Exception ignored) { }
                    hienThiThongTin("Xuất lịch sử thành công",
                        "PDF đã lưu tại:\n" + outputFile.getAbsolutePath());
                });
            } catch (Exception e) {
                javafx.application.Platform.runLater(() ->
                    hienThiThongBaoLoi("Lỗi xuất lịch sử", e.getMessage()));
            }
        }, "jasper-lichsu").start();
    }

    @FXML
    private void onMoPOS() {
        transitionTo(lblAdminName, "/fxml/banhang/DonHangView.fxml", "H3K Bakery - POS", 1280, 720);
    }

    @FXML
    private void onMoInventory() {
        transitionTo(lblAdminName, "/fxml/kho/KhoView.fxml", "H3K Bakery - Inventory", 1366, 768);
    }

    /**
     * Lớp cha cho các controller thuộc phân hệ Khách hàng.
     * Gom các xử lý UI lặp lại để giảm độ phức tạp ở controller con.
     */
    public abstract static class AbstractCustomerController extends BaseController {

        protected Stage stage;

        public void setStage(Stage stage) {
            this.stage = stage;
        }

        protected void capNhatLoiTruongNhap(TextInputControl control, String error) {
            control.setStyle(error != null ? "-fx-border-color: red;" : "");
        }

        protected void hienThiLoi(String title, String message) {
            hienThiThongBaoLoi(title, message);
        }

        protected void hienThiThanhCong(String title, String message) {
            hienThiThongTin(title, message);
        }

        protected void dongForm() {
            if (stage != null) {
                stage.close();
            }
        }
    }
}
