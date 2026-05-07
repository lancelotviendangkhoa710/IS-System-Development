package com.bakery.views.controllers.kho;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import com.bakery.views.controllers.BaseController;

public class XuatKhoViewFXMLController extends BaseController {
    @FXML private Label lblTitle;
    @FXML private TableView<Record> tblData;
    @FXML private TableColumn<Record, String> colDate, colUser, colContent, colStatus;
    public record Record(String date, String user, String content, String status) {}
    @FXML public void initialize() {
        lblTitle.setText("QUẢN LÝ XUẤT KHO (DEMO)");
        colDate.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().date()));
        colUser.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().user()));
        colContent.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().content()));
        colStatus.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().status()));
        tblData.setItems(FXCollections.observableArrayList(
            new Record("2026-05-02", "Lê Văn Bếp", "Xuất 10kg Bột mì cho xưởng bánh", "Hoàn thành"),
            new Record("2026-05-04", "Lê Văn Bếp", "Xuất 5kg Bơ lạt cho xưởng bánh", "Hoàn thành"),
            new Record("2026-05-05", "Phạm Minh Kho", "Xuất 2kg Socola chip (Hỏng)", "Đã hủy")
        ));
    }
    @FXML private void onAction() { hienThiThanhCongLabel("Đã tạo phiếu xuất kho."); }
    @FXML private void onBack() { quayLaiMenuChinh(lblTitle); }
}
