package com.bakery.main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public class App extends Application {
    public static final String LOGIN_VIEW = "/fxml/DangNhapView.fxml";
    public static final String MAIN_MENU_VIEW = "/fxml/MainMenuView.fxml";
    public static final String APP_SHELL_VIEW = "/fxml/AppShell.fxml";
    public static final String DASHBOARD_VIEW = "/fxml/BangDieuKhienView.fxml";
    public static final String THU_NGAN_DASHBOARD_VIEW = "/fxml/ThuNganDashboardView.fxml";
    public static final String THO_BEP_DASHBOARD_VIEW = "/fxml/ThoBepDashboardView.fxml";
    public static final String THU_KHO_DASHBOARD_VIEW = "/fxml/ThuKhoDashboardView.fxml";

    @Override
    public void start(Stage primaryStage) throws Exception {
        // =====================================================================
        // TẠM THỜI BYPASS MÀN HÌNH ĐĂNG NHẬP ĐỂ TEST NHANH CÁC TÍNH NĂNG
        // Khi muốn bật lại màn hình đăng nhập, hãy uncomment đoạn code gốc bên dưới.
        // =====================================================================
        
        // 1. Tạo đối tượng NhanVienDTO giả lập (mock admin)
        com.bakery.model.dto.nhansu.NhanVienDTO mockAdmin = new com.bakery.model.dto.nhansu.NhanVienDTO();
        mockAdmin.setMaNV(1);
        mockAdmin.setHoTen("Viên Đăng Khoa");
        mockAdmin.setTenDangNhap("admin");
        mockAdmin.setTrangThaiLamViec(1);
        mockAdmin.getDanhSachMaVaiTro().add(1);
        mockAdmin.getDanhSachTenVaiTro().add("Quản lý");

        // 2. Mock quyền trong SessionContext
        java.util.Set<String> mockQuyen = new java.util.HashSet<>(java.util.Arrays.asList(
            "SYSTEM", "HR", "CRM", "INVENTORY", "POS", "REPORTS"
        ));
        com.bakery.utils.SessionContext.AuthSession mockSession = new com.bakery.utils.SessionContext.AuthSession(
            1, 1, "admin", "Viên Đăng Khoa", "Quản lý", mockQuyen
        );
        com.bakery.utils.SessionContext.createSession(mockSession);

        // 3. Mock UserSession & Token cho Watchdog hoạt động
        com.bakery.utils.UserSession.setCurrentUser(mockAdmin);
        com.bakery.utils.UserSession.setCurrentToken("bypass_login_token");

        // 4. Khởi chạy trực tiếp AppShell
        FXMLLoader loader = new FXMLLoader(getClass().getResource(APP_SHELL_VIEW));
        Parent root = loader.load();

        com.bakery.views.controllers.hethong.AppShellController controller = loader.getController();
        if (controller != null) {
            controller.setNhanVienInfo(mockAdmin);
        }

        Scene scene = new Scene(root, 1400, 900);
        URL cssUrl = getClass().getResource("/css/bakery.css");
        if (cssUrl != null) {
            scene.getStylesheets().add(cssUrl.toExternalForm());
        }

        primaryStage.setTitle("H3K Bakery - Hệ thống Quản lý");
        primaryStage.setResizable(true);
        primaryStage.centerOnScreen();
        primaryStage.setScene(scene);
        primaryStage.show();

        /*
        // --- ĐOẠN CODE GỐC KHỞI ĐỘNG TỪ MÀN HÌNH ĐĂNG NHẬP ---
        FXMLLoader loader = new FXMLLoader(getClass().getResource(LOGIN_VIEW));
        Parent root = loader.load();

        Scene scene = new Scene(root);
        URL cssUrl = getClass().getResource("/css/bakery.css");
        if (cssUrl != null) {
            scene.getStylesheets().add(cssUrl.toExternalForm());
        }

        primaryStage.setTitle("H3K Bakery - Đăng nhập");
        primaryStage.setResizable(false);
        primaryStage.centerOnScreen();
        primaryStage.setScene(scene);
        primaryStage.show();
        */
    }

    public static Parent loadView(String viewPath) throws Exception {
        URL fxmlLocation = App.class.getResource(viewPath);
        if (fxmlLocation == null) {
            throw new IllegalStateException("Không tìm thấy file FXML: " + viewPath);
        }
        return FXMLLoader.load(fxmlLocation);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
