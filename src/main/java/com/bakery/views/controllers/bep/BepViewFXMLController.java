package com.bakery.views.controllers.bep;

import com.bakery.views.controllers.BaseController;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

/**
 * BepViewFXMLController — Shell cho màn hình Quản lý Bếp.
 * Chỉ quản lý header và TabPane. Mỗi tab chứa FXML con riêng với controller riêng.
 * Không xử lý dữ liệu trực tiếp — điều phối chọn tab theo yêu cầu từ MainMenu.
 */
public class BepViewFXMLController extends BaseController {

    @FXML private TabPane tabPaneBep;
    @FXML private Label lblThongBao;

    @FXML private Tab tabXuatKho;
    @FXML private Tab tabDonHangBep;
    @FXML private Tab tabKds;
    @FXML private Tab tabCauHinhGioiHan;

    @FXML
    public void initialize() {
        if (tabPaneBep != null && tabXuatKho != null) {
            tabPaneBep.getSelectionModel().select(tabXuatKho);
        }
    }

    /**
     * Được gọi từ MainMenuViewFXMLController để chuyển sang tab cụ thể.
     * Ví dụ: controller.chuyenTab("donhangbep")
     */
    public void chuyenTab(String tabKey) {
        if (tabPaneBep == null) return;
        Tab target = switch (tabKey.toLowerCase()) {
            case "donhangbep" -> tabDonHangBep;
            case "kds"        -> tabKds;
            case "cauhinhgioihan", "gioihan" -> tabCauHinhGioiHan;
            default           -> tabXuatKho;
        };
        if (target != null) {
            tabPaneBep.getSelectionModel().select(target);
        }
    }
}
