package com.bakery.views.controllers.bep;

import com.bakery.views.controllers.BaseController;
import com.bakery.views.controllers.banhang.TheoDoiDonHangViewFXMLController;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

/**
 * BepViewFXMLController — Shell cho màn hình Quản lý Bếp.
 * Chỉ quản lý header và TabPane. Mỗi tab chứa FXML con riêng với controller
 * riêng.
 * Không xử lý dữ liệu trực tiếp — điều phối chọn tab theo yêu cầu từ MainMenu.
 */
public class BepViewFXMLController extends BaseController {

    @FXML
    private TabPane tabPaneBep;
    @FXML
    private Label lblThongBao;

    @FXML
    private Tab tabXuatKho;
    @FXML
    private Tab tabDonHangBep;
    @FXML
    private Tab tabKiemKe;
    @FXML
    private Tab tabCauHinhGioiHan;

    /**
     * Injected tự động bởi FXMLLoader — tương ứng fx:id="theoDoiDonHangBep" trong
     * FXML.
     */
    @FXML
    private TheoDoiDonHangViewFXMLController theoDoiDonHangBepController;

    @FXML
    public void initialize() {
        if (tabPaneBep != null && tabXuatKho != null) {
            tabPaneBep.getSelectionModel().select(tabXuatKho);
        }
        // Kích hoạt chế độ Bếp: tự động hiển thị đơn tùy chỉnh chưa hoàn thành
        if (theoDoiDonHangBepController != null) {
            theoDoiDonHangBepController.setBepMode(true);
        }
        // Auto-refresh đã bị TẮT ở màn hình bếp — nhân viên bếp tự F5 khi cần.
    }

    public void chuyenTab(String tabKey) {
        if (tabPaneBep == null)
            return;
        Tab target = switch (tabKey.toLowerCase()) {
            case "donhangbep" -> tabDonHangBep;
            case "kiemkekho", "kiemke", "kho" -> tabKiemKe;
            case "cauhinhgioihan", "gioihan" -> tabCauHinhGioiHan;
            default -> tabXuatKho;
        };
        if (target != null) {
            tabPaneBep.getSelectionModel().select(target);
        }
    }
}
