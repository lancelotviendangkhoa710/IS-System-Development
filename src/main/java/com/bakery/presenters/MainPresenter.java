package com.bakery.presenters;

import com.bakery.model.dto.ModuleDef;
import com.bakery.model.dto.NhanVienDTO;
import com.bakery.model.enums.SystemModule;
import com.bakery.services.AuthorizationService;
import com.bakery.utils.SessionContext;
import com.bakery.utils.UserSession;
import com.bakery.views.interfaces.IMainView;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Presenter cho màn hình chính (Main View).
 * Quản lý menu điều hướng dựa trên quyền hạn (SystemModule) của người dùng.
 */
public class MainPresenter {

    // Danh sách tất cả các Module có trong hệ thống
    private static final List<ModuleDef> ALL_MODULES = List.of(
            new ModuleDef(SystemModule.POS,       "Bán hàng",             "/fxml/PosView.fxml"),
            new ModuleDef(SystemModule.POS,       "Theo dõi đơn",         "/fxml/OrderTrackingView.fxml"),
            new ModuleDef(SystemModule.INVENTORY, "Kho & Nguyên liệu",    "/fxml/InventoryView.fxml"),
            new ModuleDef(SystemModule.REPORTS,   "Báo cáo & Thống kê",   "/fxml/ReportsView.fxml"),
            new ModuleDef(SystemModule.STAFF,     "Nhân sự & Phân quyền", "/fxml/StaffView.fxml"),
            new ModuleDef(SystemModule.KDS,       "Nhà bếp (KDS)",        "/fxml/KdsView.fxml"),
            new ModuleDef(SystemModule.CRM,       "Khách hàng",           "/fxml/CustomersView.fxml"),
            new ModuleDef(SystemModule.AUDIT_LOGS, "Nhật ký hệ thống",     "/fxml/AuditLogsView.fxml"),
            new ModuleDef(SystemModule.SYSTEM,    "Cấu hình hệ thống",    "/fxml/SystemView.fxml")
    );

    private final IMainView view;
    private final AuthorizationService authService;

    public MainPresenter(IMainView view) {
        this.view = view;
        this.authService = new AuthorizationService();
    }

    public void onInitialize() {
        SessionContext session = SessionContext.getInstance();
        String hoTen = session.getHoTen();
        
        // Lấy thông tin nhân viên từ Session (ưu tiên UserSession hoặc SessionContext)
        NhanVienDTO user = UserSession.getCurrentUser();
        
        String displayName = hoTen != null ? hoTen : (user != null ? user.getHoTen() : "Unknown");
        view.setHoTen(displayName);
        view.setVaiTro(user != null ? user.getTenVaiTro() : "Nhân viên");
        view.setAvatar(layKyTuDau(displayName));

        // Xây dựng menu dựa trên danh sách module được cấp phép
        view.buildMenu(layModuleDuocCap(user));
    }

    public void onDangXuat() {
        SessionContext.getInstance().dangXuat();
        UserSession.clear();
        view.navigateToLogin();
    }

    private List<ModuleDef> layModuleDuocCap(NhanVienDTO user) {
        if (user == null) return List.of();
        
        // Lấy tập hợp các module mà user được phép truy cập từ AuthorizationService
        Set<SystemModule> grantedModules = authService.layModulesDuocCap(user);
        
        List<ModuleDef> allowed = new ArrayList<>();
        for (ModuleDef def : ALL_MODULES) {
            if (grantedModules.contains(def.module())) {
                allowed.add(def);
            }
        }
        return allowed;
    }

    private String layKyTuDau(String hoTen) {
        if (hoTen == null || hoTen.isBlank() || hoTen.equals("Unknown")) return "?";
        String[] parts = hoTen.trim().split("\\s+");
        return String.valueOf(parts[parts.length - 1].charAt(0)).toUpperCase();
    }
}
