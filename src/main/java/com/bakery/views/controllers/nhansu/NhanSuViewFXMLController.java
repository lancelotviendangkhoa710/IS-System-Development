package com.bakery.views.controllers.nhansu;

import com.bakery.utils.FXMLLoaderUtil;
import com.bakery.views.controllers.BaseController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.StackPane;

/**
 * Controller cho NhanSuView — TabPane gồm 4 tab:
 * Tab 1: Quản lý nhân viên (QuanLyNhanVienView)
 * Tab 2: Nhân viên & Vai trò — gán vai trò cho nhân viên (MaTranPhanQuyenView)
 * Tab 3: Quyền vai trò (PhanQuyenVaiTroView)
 * Tab 4: Chấm công (ChamCongView)
 */
public class NhanSuViewFXMLController extends BaseController {

    @FXML private TabPane tabPane;
    @FXML private Tab tabNhanVien;
    @FXML private Tab tabPhanQuyen;
    @FXML private Tab tabVaiTro;
    @FXML private Tab tabChamCong;
    @FXML private StackPane stackNhanVien;
    @FXML private StackPane stackPhanQuyen;
    @FXML private StackPane stackVaiTro;
    @FXML private StackPane stackChamCong;

    @FXML
    public void initialize() {
        // Load Tab 1 ngay khi khởi tạo
        taiTabNhanVien();

        // Tab 2 load lazy khi user click lần đầu
        tabPhanQuyen.setOnSelectionChanged(evt -> {
            if (tabPhanQuyen.isSelected() && stackPhanQuyen.getChildren().isEmpty()) {
                taiTabPhanQuyen();
            }
        });

        // Tab 3 load lazy khi user click lần đầu
        tabVaiTro.setOnSelectionChanged(evt -> {
            if (tabVaiTro.isSelected() && stackVaiTro.getChildren().isEmpty()) {
                taiTabVaiTro();
            }
        });

        // Tab 4: Chấm công — load lazy
        tabChamCong.setOnSelectionChanged(evt -> {
            if (tabChamCong.isSelected() && stackChamCong.getChildren().isEmpty()) {
                taiTabChamCong();
            }
        });
    }

    /** Load QuanLyNhanVienView vào Tab 1. */
    private void taiTabNhanVien() {
        Node view = FXMLLoaderUtil.loadFXML("/fxml/nhansu/QuanLyNhanVienView.fxml");
        if (view != null) stackNhanVien.getChildren().setAll(view);
    }

    /** Load MaTranPhanQuyenView vào Tab 2 (lazy) — gán vai trò cho nhân viên. */
    private void taiTabPhanQuyen() {
        Node view = FXMLLoaderUtil.loadFXML("/fxml/hethong/MaTranPhanQuyenView.fxml");
        if (view != null) stackPhanQuyen.getChildren().setAll(view);
    }

    /** Load PhanQuyenVaiTroView vào Tab 3 (lazy). */
    private void taiTabVaiTro() {
        Node view = FXMLLoaderUtil.loadFXML("/fxml/hethong/PhanQuyenVaiTroView.fxml");
        if (view != null) stackVaiTro.getChildren().setAll(view);
    }

    /** Load ChamCongView vào Tab 4 (lazy). */
    private void taiTabChamCong() {
        Node view = FXMLLoaderUtil.loadFXML("/fxml/nhansu/ChamCongView.fxml");
        if (view != null) stackChamCong.getChildren().setAll(view);
    }

    /** Cho phép điều hướng trực tiếp đến tab cụ thể từ bên ngoài. */
    public void chuyenTab(String tabKey) {
        switch (tabKey) {
            case "nhanvien"  -> tabPane.getSelectionModel().select(tabNhanVien);
            case "phanquyen" -> tabPane.getSelectionModel().select(tabPhanQuyen);
            case "vaitro"    -> tabPane.getSelectionModel().select(tabVaiTro);
            case "chamcong"  -> tabPane.getSelectionModel().select(tabChamCong);
            default          -> tabPane.getSelectionModel().selectFirst();
        }
    }
}
