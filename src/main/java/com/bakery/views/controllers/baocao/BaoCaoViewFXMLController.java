package com.bakery.views.controllers.baocao;

import com.bakery.services.baocao.ThongKeService;
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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.io.PrintWriter;
import java.io.File;

public class BaoCaoViewFXMLController extends BaseController {

    @FXML private Label lblAdminName;
    @FXML private LineChart<String, Number> revenueChart;
    @FXML private TableView<String[]> tableGiaoDich;
    @FXML private Label lblDoanhThu;
    @FXML private Label lblChenhLechDoanhThu;
    @FXML private Label lblLoiNhuan;
    @FXML private Label lblTongDon;
    @FXML private VBox vboxBestSellers;
    @FXML private PieChart revenuePieChart;
    @FXML private BarChart<String, Number> revenueBarChart;
    
    @FXML private ComboBox<String> cbLoaiBaoCao;
    @FXML private DatePicker dpNgayBaoCao;

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

        setupFilters();
        refreshData();
    }

    private void setupFilters() {
        cbLoaiBaoCao.setItems(FXCollections.observableArrayList("Ngày", "Tháng", "Quý", "Năm"));
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

            if ("Tháng".equals(loaiStr)) {
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
            lblDoanhThu.setText(String.format("%,.0fđ", doanhThu));
            lblLoiNhuan.setText(String.format("%,.0fđ", doanhThu * 0.3));

            updateChart(loai, giaTri);
            updateCategoryCharts(loai, giaTri);
            updateTable(loai, giaTri);
            loadTopSellers();
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
            lblName.setStyle("-fx-font-weight: 600; -fx-font-size: 14px; -fx-text-fill: #1b1c1a;");
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            Label lblQty = new Label(entry.getValue() + " cái");
            lblQty.setStyle("-fx-text-fill: #D85A30; -fx-font-weight: bold; -fx-font-size: 11px;");
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
        series.setName("Doanh Thu (" + giaTri + ")");
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

    @FXML
    private void onTaiBaoCaoNhanh() {
        String loaiStr = cbLoaiBaoCao.getValue();
        LocalDate ngay = dpNgayBaoCao.getValue();
        String filename = "BaoCao_" + loaiStr + "_" + ngay.toString() + ".txt";
        
        try (PrintWriter writer = new PrintWriter(new File(filename))) {
            writer.println("H3K BAKERY - BÁO CÁO DOANH THU");
            writer.println("Loại: " + loaiStr);
            writer.println("Thời gian: " + ngay.toString());
            writer.println("----------------------------------");
            writer.println("TỔNG DOANH THU: " + lblDoanhThu.getText());
            writer.println("LỢI NHUẬN ƯỚC TÍNH: " + lblLoiNhuan.getText());
            writer.println("\nDANH SÁCH GIAO DỊCH:");
            for (String[] row : tableGiaoDich.getItems()) {
                writer.println(String.join(" | ", row));
            }
            hienThiThongTin("Thành công", "Đã lưu báo cáo nhanh vào file: " + filename);
        } catch (Exception e) {
            hienThiThongBaoLoi("Lỗi", "Không thể lưu báo cáo: " + e.getMessage());
        }
    }


    @FXML
    private void onVeMenu() {
        quayLaiMenuChinh(lblAdminName);
    }

    @FXML
    private void onMoPOS() {
        transitionTo(lblAdminName, "/fxml/DonHangView.fxml", "H3K Bakery - POS", 1280, 720);
    }

    @FXML
    private void onMoInventory() {
        transitionTo(lblAdminName, "/fxml/KhoView.fxml", "H3K Bakery - Inventory", 1366, 768);
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
