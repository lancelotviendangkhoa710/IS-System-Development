package com.bakery.views.controllers;

import com.bakery.model.dao.ThongKeDAO;
import com.bakery.utils.UserSession;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
import java.util.Map;

public class ReportsViewFXMLController {

    @FXML private Label lblAdminName;
    @FXML private LineChart<String, Number> revenueChart;
    @FXML private TableView<String[]> tableGiaoDich;
    @FXML private Label lblDoanhThu;
    @FXML private Label lblChenhLechDoanhThu;
    @FXML private Label lblLoiNhuan;
    @FXML private Label lblTongDon;
    @FXML private VBox vboxBestSellers;

    private ThongKeDAO thongKeDAO = new ThongKeDAO();

    @FXML
    public void initialize() {
        if (UserSession.getCurrentUser() != null) {
            String name = UserSession.getCurrentUser().getHoTen();
            if (name == null || name.isEmpty()) {
                name = UserSession.getCurrentUser().getTenDangNhap();
            }
            lblAdminName.setText(name);
        }

        // Fetch real data
        double doanhThuHomNay = thongKeDAO.getDoanhThuHomNay();
        double doanhThuHomQua = thongKeDAO.getDoanhThuHomQua();
        int tongDon = thongKeDAO.getTongSoDonHomNay();
        
        lblDoanhThu.setText(String.format("%,.0fđ", doanhThuHomNay));
        lblLoiNhuan.setText(String.format("%,.0fđ", doanhThuHomNay * 0.3)); // mock profit
        lblTongDon.setText(String.valueOf(tongDon));
        
        if (doanhThuHomQua > 0) {
            double chenhLech = doanhThuHomNay - doanhThuHomQua;
            String text = String.format("So với %,.0fđ hôm qua (", doanhThuHomQua);
            if (chenhLech > 0) text += "+" + String.format("%,.0fđ)", chenhLech);
            else text += String.format("%,.0fđ)", chenhLech);
            lblChenhLechDoanhThu.setText(text);
        } else {
            lblChenhLechDoanhThu.setText("So với 0đ hôm qua");
        }

        Map<String, Double> chartData = thongKeDAO.getDoanhThu7NgayQua();
        XYChart.Series<String, Number> series1 = new XYChart.Series<>();
        series1.setName("7 Ngày Gần Nhất");
        for (Map.Entry<String, Double> entry : chartData.entrySet()) {
            series1.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }
        revenueChart.getData().clear();
        revenueChart.getData().add(series1);

        Map<String, Integer> top5 = thongKeDAO.getTop5BanChay();
        int maxQty = top5.values().stream().max(Integer::compareTo).orElse(1);
        if (maxQty == 0) maxQty = 1;

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

        // Mock data for TableView
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

        List<String[]> recentTx = thongKeDAO.getGiaoDichMoiNhat();
        tableGiaoDich.setItems(FXCollections.observableArrayList(recentTx));
    }

    @FXML
    private void onVeMenu() {
        moScene("/fxml/MainMenuView.fxml", "H3K Bakery - Menu chuc nang", 1366, 768);
    }

    @FXML
    private void onMoPOS() {
        moScene("/fxml/OrderView.fxml", "H3K Bakery - POS", 1280, 720);
    }

    @FXML
    private void onMoInventory() {
        moScene("/fxml/InventoryView.fxml", "H3K Bakery - Inventory", 1366, 768);
    }

    private void moScene(String fxmlPath, String title, int width, int height) {
        try {
            URL fxmlUrl = getClass().getResource(fxmlPath);
            if (fxmlUrl == null) {
                throw new RuntimeException("Khong tim thay " + fxmlPath);
            }
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Scene scene = new Scene(loader.load(), width, height);
            
            if ("/fxml/MainMenuView.fxml".equals(fxmlPath)) {
                MainMenuViewFXMLController controller = loader.getController();
                controller.khoiTaoThongTinDangNhap(UserSession.getCurrentUser());
            }
            
            URL cssUrl = getClass().getResource("/css/bakery.css");
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
            }
            Stage stage = (Stage) lblAdminName.getScene().getWindow();
            stage.setTitle(title);
            stage.setScene(scene);
            stage.centerOnScreen();
        } catch (Exception ex) {
            System.err.println("Lỗi mở scene: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
