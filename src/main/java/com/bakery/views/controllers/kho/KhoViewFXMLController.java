package com.bakery.views.controllers.kho;

import com.bakery.views.controllers.BaseController;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

/**
 * KhoViewFXMLController — Controller shell cho man hinh Quan ly Kho.
 * Chi quan ly header va TabPane. Moi tab chua FXML con rieng voi controller rieng.
 * Khong xu ly du lieu truc tiep — dieu phoi viec chon tab theo yeu cau tu MainMenu.
 */
public class KhoViewFXMLController extends BaseController {

    @FXML private TabPane tabPaneKho;
    @FXML private Label lblThongBao;

    @FXML private Tab tabNhaCungCap;
    @FXML private Tab tabKiemKe;
    @FXML private Tab tabNhapKho;

    @FXML
    public void initialize() {
        if (tabPaneKho != null && tabNhaCungCap != null) {
            tabPaneKho.getSelectionModel().select(tabNhaCungCap);
        }
    }

    /**
     * Duoc goi tu MainMenuViewFXMLController de chuyen sang tab cu the.
     * Vi du: controller.chuyenTab("kiemke")
     */
    public void chuyenTab(String tabKey) {
        if (tabPaneKho == null) return;
        Tab target = switch (tabKey.toLowerCase()) {
            case "kiemke", "nguyenlieu" -> tabKiemKe;
            case "nhapkho"              -> tabNhapKho;
            default                     -> tabNhaCungCap;
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
