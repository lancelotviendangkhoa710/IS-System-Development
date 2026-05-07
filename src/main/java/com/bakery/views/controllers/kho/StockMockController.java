package com.bakery.views.controllers.kho;

import com.bakery.views.controllers.BaseController;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;

/**
 * StockMockController — toàn bộ mock data đã bị xóa.
 * Màn hình Nhập kho, Xuất kho, Kiểm kê cần được triển khai
 * qua các controller riêng kết nối với DAO thật.
 */
public class StockMockController extends BaseController {
    @FXML private Label lblTitle;
    @FXML private TableView<?> tblData;

    public void setType(String type) {
        if (lblTitle != null) lblTitle.setText("QUẢN LÝ " + type.toUpperCase());
        if (tblData != null) tblData.setPlaceholder(new Label("Chưa có dữ liệu " + type + " từ cơ sở dữ liệu."));
    }

    @FXML private void onAction() {
        if (lblThongBao != null) lblThongBao.setText("Chức năng đang được phát triển.");
    }

    @FXML private void onBack() { quayLaiMenuChinh(lblTitle); }
}
