package com.bakery.views.controllers.kho;

import com.bakery.model.dto.kho.SanPhamDTO;
import javafx.fxml.FXML;
import javafx.scene.control.TabPane;

/**
 * Controller shell cho QuanLySanPhamView — chứa TabPane 4 tab.
 * Nhiệm vụ: đồng bộ sản phẩm đang chọn ở Tab 1 sang Tab 3 (Công thức).
 */
public class QuanLySanPhamViewFXMLController {

    @FXML private TabPane tabPaneChinh;

    // Controllers của các FXML được include — JavaFX tự inject theo quy tắc "fx:id + Controller"
    @FXML private SanPhamViewFXMLController sanPhamContentController;
    @FXML private CongThucViewFXMLController congThucContentController;

    @FXML
    public void initialize() {
        // Tiêm CongThucController vào SanPhamController để chuyenSangTabCongThuc gọi thẳng
        if (sanPhamContentController != null && congThucContentController != null) {
            sanPhamContentController.setCongThucController(congThucContentController);
        }

        tabPaneChinh.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            if (newTab == null) return;
            String tabId = newTab.getId();
            // Quay về Tab SP → reload để lấy giá vốn mới nhất từ DB
            if ("tabSanPham".equals(tabId) && sanPhamContentController != null) {
                sanPhamContentController.lamMoiDanhSach();
            }
        });
    }
}
