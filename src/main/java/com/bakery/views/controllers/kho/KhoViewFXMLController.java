package com.bakery.views.controllers.kho;

import com.bakery.views.controllers.BaseController;
import com.bakery.views.controllers.hethong.AppShellController;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.List;

/**
 * Controller cho KhoView.
 * Hiển thị bảng tồn kho tổng hợp với Mock Data.
 */
public class KhoViewFXMLController extends BaseController {

    @FXML private TableView<StockItem> tblStock;
    @FXML private TableColumn<StockItem, String> colName;
    @FXML private TableColumn<StockItem, String> colType;
    @FXML private TableColumn<StockItem, String> colAmount;
    @FXML private TableColumn<StockItem, String> colStatus;

    public record StockItem(String name, String type, String amount, String status) {}

    @FXML
    public void initialize() {
        if (colName != null) {
            colName.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().name()));
            colType.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().type()));
            colAmount.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().amount()));
            colStatus.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().status()));

            tblStock.setItems(FXCollections.observableArrayList(
                new StockItem("Bột mì đa dụng", "Nguyên liệu", "85.5 Kg", "Đủ hàng"),
                new StockItem("Bơ lạt Anchor", "Nguyên liệu", "5.2 Kg", "Sắp hết"),
                new StockItem("Bánh Kem Bắp", "Sản phẩm", "12 Cái", "Đủ hàng"),
                new StockItem("Trứng gà ta", "Nguyên liệu", "15 Quả", "Cần nhập thêm"),
                new StockItem("Socola chip", "Nguyên liệu", "1.5 Kg", "Bình thường")
            ));
        }
    }

    @FXML
    private void onVeMenu() {
        quayLaiMenuChinh(tblStock);
    }

    @FXML
    private void onMoPOS() {
        AppShellController.getInstance().loadView("/fxml/DonHangView.fxml");
    }

    @FXML
    private void onActionTam() {
        if (lblThongBao != null) {
            lblThongBao.setText("Chế độ Demo: Đã cập nhật trạng thái tồn kho.");
        }
    }
}
