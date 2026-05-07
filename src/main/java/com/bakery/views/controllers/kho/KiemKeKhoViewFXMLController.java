package com.bakery.views.controllers.kho;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import com.bakery.views.controllers.BaseController;

public class KiemKeKhoViewFXMLController extends BaseController {
    @FXML private Label lblTitle;
    @FXML private TableView<Record> tblData;
    @FXML private TableColumn<Record, String> colDate, colUser, colContent, colStatus;
    public record Record(String date, String user, String content, String status) {}
    @FXML public void initialize() {
        lblTitle.setText("KIỂM KÊ KHO ĐỊNH KỲ (DEMO)");
        colDate.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().date()));
        colUser.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().user()));
        colContent.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().content()));
        colStatus.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().status()));
        tblData.setItems(FXCollections.observableArrayList(
            new Record("2026-04-30", "Phạm Minh Kho", "Kiểm kê định kỳ tháng 4", "Khớp dữ liệu"),
            new Record("2026-05-05", "Phạm Minh Kho", "Kiểm kê đột xuất nguyên liệu", "Chờ xác nhận"),
            new Record("2026-05-05", "Hệ thống", "Tự động chốt tồn kho cuối ngày", "Hoàn thành")
        ));
    }
    @FXML private void onAction() { hienThiThanhCongLabel("Đã bắt đầu đợt kiểm kê mới."); }
    @FXML private void onBack() { quayLaiMenuChinh(lblTitle); }
}
