package com.bakery.views.controllers.kho;

import com.bakery.views.controllers.BaseController;
import com.bakery.views.controllers.hethong.AppShellController;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;



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
            tblStock.setPlaceholder(new javafx.scene.control.Label("Chức năng tổng hợp tồn kho đang được phát triển."));
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
