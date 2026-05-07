package com.bakery.views.controllers.kho;

import com.bakery.views.controllers.BaseController;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class KiemKeKhoViewFXMLController extends BaseController {
    @FXML private Label lblTitle;
    @FXML private TableView<Record> tblData;
    @FXML private TableColumn<Record, String> colDate, colUser, colContent, colStatus;
    public record Record(String date, String user, String content, String status) {}

    @FXML
    public void initialize() {
        lblTitle.setText("KIỂM KÊ KHO ĐỊNH KỲ");
        colDate.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().date()));
        colUser.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().user()));
        colContent.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().content()));
        colStatus.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().status()));
        tblData.setItems(FXCollections.observableArrayList());
        tblData.setPlaceholder(new Label("Chức năng kiểm kê kho đang được phát triển."));
    }

    @FXML private void onAction() { hienThiLoiLabel("Chức năng tạo đợt kiểm kê đang được phát triển."); }
    @FXML private void onBack() { quayLaiMenuChinh(lblTitle); }
}
