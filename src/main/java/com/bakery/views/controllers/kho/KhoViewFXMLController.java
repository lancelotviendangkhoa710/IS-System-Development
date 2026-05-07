package com.bakery.views.controllers.kho;

import com.bakery.views.controllers.BaseController;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

/**
 * KhoViewFXMLController — Controller shell cho màn hình Quản lý Kho.
 * Chỉ quản lý header và TabPane. Mỗi tab chứa FXML con riêng với controller riêng.
 * Không xử lý dữ liệu trực tiếp — điều phối việc chọn tab theo yêu cầu từ MainMenu.
 */
public class KhoViewFXMLController extends BaseController {

    @FXML private TabPane tabPaneKho;
    @FXML private Label lblThongBao;

    // Tab references — khớp với fx:id trong FXML
    @FXML private Tab tabKiemKe;
    @FXML private Tab tabNguyenLieu;
    @FXML private Tab tabSanPham;
    @FXML private Tab tabDanhMuc;
    @FXML private Tab tabNhapKho;
    @FXML private Tab tabXuatKho;
    @FXML private Tab tabNhaCungCap;

    @FXML
    public void initialize() {
        // Mặc định hiển thị tab Kiểm kê đầu tiên
        if (tabPaneKho != null && tabKiemKe != null) {
            tabPaneKho.getSelectionModel().select(tabKiemKe);
        }
    }

    /**
     * Được gọi từ MainMenuViewFXMLController để chuyển sang tab cụ thể.
     * Ví dụ: controller.chuyenTab("nhapkho")
     */
    public void chuyenTab(String tabKey) {
        if (tabPaneKho == null) return;
        Tab target = switch (tabKey.toLowerCase()) {
            case "nguyenlieu"  -> tabNguyenLieu;
            case "sanpham"     -> tabSanPham;
            case "danhmuc"     -> tabDanhMuc;
            case "nhapkho"     -> tabNhapKho;
            case "xuatkho"     -> tabXuatKho;
            case "nhacungcap"  -> tabNhaCungCap;
            default            -> tabKiemKe;
        };
        if (target != null) {
            tabPaneKho.getSelectionModel().select(target);
        }
    }

    @FXML
    private void onBack() {
        quayLaiMenuChinh(tabPaneKho);
    }
}
