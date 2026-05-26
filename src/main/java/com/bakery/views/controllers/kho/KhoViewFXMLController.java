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
     * Phân quyền tab KhoView theo vai trò:
     * - Thợ Bếp (NHA_BEP): chỉ xem "Thẻ kho" + "Truy xuất nguồn gốc" (read-only
     * hoàn toàn).
     * - Thủ Kho (THU_KHO): xem Thẻ kho, Kiểm kê, Truy xuất; ẩn NhaCungCap &
     * NhapKho.
     * - Quản lý / Admin: full access tất cả tab.
     */
    private void apDungPhanQuyen() {
        PhanQuyenService svc = new PhanQuyenService();
        com.bakery.model.dto.nhansu.NhanVienDTO user = UserSession.getCurrentUser();

        boolean laThoBep = svc.laThoBep(user);
        boolean laThuKho = svc.laThuKho(user);
        boolean coQuyenNhapKho = svc.coTinhNang(user, PhanQuyenService.TinhNangHeThong.NHAP_KHO);

        // Ẩn Nhập kho nếu không có quyền (Thợ Bếp / Thủ Kho)
        if (!coQuyenNhapKho && tabNhapKho != null) {
            tabNhapKho.setDisable(true);
            tabPaneKho.getTabs().remove(tabNhapKho);
        }

        if (laThoBep) {
            // Thợ Bếp: không được vào tab nhập kho, truy vết nguồn gốc, nhà cung cấp và kiểm kê
            xoaTab(tabNhaCungCap);
            xoaTab(tabKiemKe);
            xoaTab(tabNhapKho);
            xoaTab(tabTraCuuNguonGoc);
        } else if (laThuKho) {
            // Thủ Kho: có màn hình NhaCungCap riêng → ẩn tab này trong KhoView
            xoaTab(tabNhaCungCap);
        }
    }

    /** Ẩn và xóa 1 tab ra khỏi TabPane an toàn. */
    private void xoaTab(Tab tab) {
        if (tab != null) {
            tab.setDisable(true);
            tabPaneKho.getTabs().remove(tab);
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

}
