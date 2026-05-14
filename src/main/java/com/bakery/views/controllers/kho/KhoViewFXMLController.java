package com.bakery.views.controllers.kho;

import com.bakery.services.nhansu.PhanQuyenService;
import com.bakery.utils.UserSession;
import com.bakery.views.controllers.BaseController;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

/**
 * KhoViewFXMLController — Controller shell cho man hinh Quan ly Kho.
 * Chi quan ly header va TabPane. Moi tab chua FXML con rieng voi controller
 * rieng.
 * Khong xu ly du lieu truc tiep — dieu phoi viec chon tab theo yeu cau tu
 * MainMenu.
 */
public class KhoViewFXMLController extends BaseController {

    @FXML
    private TabPane tabPaneKho;
    @FXML
    private Label lblThongBao;

    @FXML
    private Tab tabTheKho;
    @FXML
    private Tab tabNhaCungCap;
    @FXML
    private Tab tabKiemKe;
    @FXML
    private Tab tabNhapKho;
    @FXML
    private Tab tabTraCuuNguonGoc;

    @FXML
    public void initialize() {
        apDungPhanQuyen();
        if (tabPaneKho != null && tabTheKho != null) {
            tabPaneKho.getSelectionModel().select(tabTheKho);
        }
    }

    /**
     * Kiem tra quyen NHAP_KHO cua nguoi dung hien tai.
     * Neu khong co quyen (vi du: Tho Bep), an tab Nhap kho de bao ve read-only.
     */
    private void apDungPhanQuyen() {
        PhanQuyenService phanQuyenService = new PhanQuyenService();
        boolean coQuyenNhapKho = phanQuyenService.coTinhNang(
                UserSession.getCurrentUser(),
                PhanQuyenService.TinhNangHeThong.NHAP_KHO);
        if (!coQuyenNhapKho && tabNhapKho != null) {
            tabNhapKho.setDisable(true);
            tabPaneKho.getTabs().remove(tabNhapKho);
        }
    }

    public void chuyenTab(String tabKey) {
        if (tabPaneKho == null)
            return;
        Tab target = switch (tabKey.toLowerCase()) {
            case "thekho" -> tabTheKho;
            case "nhacungcap" -> tabNhaCungCap;
            case "kiemke", "nguyenlieu" -> tabKiemKe;
            case "nhapkho" -> tabNhapKho;
            case "tracuunguongoc", "traceability" -> tabTraCuuNguonGoc;
            default -> tabTheKho;
        };
        if (target != null && tabPaneKho.getTabs().contains(target)) {
            tabPaneKho.getSelectionModel().select(target);
        }
    }

    @FXML
    private void onBack() {
        quayLaiMenuChinh(tabPaneKho);
    }
}
