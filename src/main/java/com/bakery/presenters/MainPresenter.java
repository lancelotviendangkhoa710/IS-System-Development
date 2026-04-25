package com.bakery.presenters;

import com.bakery.utils.SessionContext;
import com.bakery.views.interfaces.IMainView;

import java.util.Arrays;
import java.util.List;

public class MainPresenter {

    private static final int CASHIER = 45;
    private static final int MANAGER = 44;
    private static final int BAKER   = 46;

    private static final List<ModuleDef> MODULES = List.of(
            new ModuleDef("dashboard", "Tổng quan & Đối soát", new int[]{MANAGER, CASHIER}, "/fxml/DashboardView.fxml"),
            new ModuleDef("pos",       "Bán hàng",             new int[]{MANAGER, CASHIER}, "/fxml/PosView.fxml"),
            new ModuleDef("orders",    "Đơn hàng",             new int[]{MANAGER, CASHIER, BAKER}, "/fxml/OrdersView.fxml"),
            new ModuleDef("inventory", "Kho & Nguyên liệu",    new int[]{MANAGER, BAKER},   "/fxml/InventoryView.fxml"),
            new ModuleDef("products",  "Sản phẩm & Công thức", new int[]{MANAGER, BAKER},   "/fxml/ProductsView.fxml"),
            new ModuleDef("customers", "Khách hàng",           new int[]{MANAGER, CASHIER}, "/fxml/CustomersView.fxml"),
            new ModuleDef("cashbook",  "Sổ Quỹ",               new int[]{MANAGER, CASHIER}, "/fxml/CashbookView.fxml"),
            new ModuleDef("hr",        "Nhân sự",              new int[]{MANAGER},          "/fxml/HrView.fxml"),
            new ModuleDef("reports",   "Báo cáo",              new int[]{MANAGER},          "/fxml/ReportsView.fxml"),
            new ModuleDef("suppliers", "Nhà Cung Cấp",         new int[]{MANAGER},          "/fxml/SuppliersView.fxml"),
            new ModuleDef("account",   "Tài khoản",            new int[]{MANAGER, CASHIER, BAKER}, "/fxml/AccountView.fxml")
    );

    private final IMainView view;

    public MainPresenter(IMainView view) {
        this.view = view;
    }

    public void onInitialize() {
        SessionContext session = SessionContext.getInstance();
        String hoTen = session.getHoTen();
        int maVaiTro = session.getMaVaiTro();

        view.setHoTen(hoTen);
        view.setVaiTro(getTenVaiTro(maVaiTro));
        view.setAvatar(layKyTuDau(hoTen));
        view.buildMenu(layModuleTheoVaiTro(maVaiTro));
    }

    public void onDangXuat() {
        SessionContext.getInstance().dangXuat();
        view.navigateToLogin();
    }

    private List<ModuleDef> layModuleTheoVaiTro(int maVaiTro) {
        return MODULES.stream()
                .filter(m -> Arrays.stream(m.roles()).anyMatch(r -> r == maVaiTro))
                .toList();
    }

    private String getTenVaiTro(int maVaiTro) {
        return switch (maVaiTro) {
            case MANAGER -> "Quản lý";
            case CASHIER -> "Thu ngân";
            case BAKER   -> "Thợ bánh";
            default      -> "Nhân viên";
        };
    }

    private String layKyTuDau(String hoTen) {
        if (hoTen == null || hoTen.isBlank()) return "?";
        String[] parts = hoTen.trim().split("\\s+");
        return String.valueOf(parts[parts.length - 1].charAt(0)).toUpperCase();
    }
}
