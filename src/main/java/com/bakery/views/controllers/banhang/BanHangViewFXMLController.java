package com.bakery.views.controllers.banhang;

import com.bakery.views.controllers.BaseController;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

/**
 * BanHangViewFXMLController — Shell cho màn hình Bán hàng.
 * Chỉ quản lý header và TabPane.
 * Tab 1: Tạo đơn POS (DonHangView)
 * Tab 2: Theo dõi đơn hàng (TheoDoiDonHangView)
 */
public class BanHangViewFXMLController extends BaseController {

    @FXML private TabPane tabPaneBanHang;
    @FXML private Label lblThongBao;

    @FXML private Tab tabTaoDon;
    @FXML private Tab tabTheoDoiDon;

    @FXML
    public void initialize() {
        if (tabPaneBanHang != null && tabTaoDon != null) {
            tabPaneBanHang.getSelectionModel().select(tabTaoDon);
        }
    }

    /**
     * Được gọi từ bên ngoài để chuyển sang tab cụ thể.
     * Ví dụ: controller.chuyenTab("theodoidon")
     */
    public void chuyenTab(String tabKey) {
        if (tabPaneBanHang == null) return;
        Tab target = switch (tabKey.toLowerCase()) {
            case "theodoidon", "theodoi" -> tabTheoDoiDon;
            default -> tabTaoDon;
        };
        if (target != null) {
            tabPaneBanHang.getSelectionModel().select(target);
        }
    }

}
