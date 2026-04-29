package com.bakery.main;

import com.bakery.model.dto.NhanVienDTO;
import com.bakery.utils.UserSession;
import com.bakery.views.controllers.MainMenuViewFXMLController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public class App extends Application {
    public static final String LOGIN_VIEW = "/fxml/login.fxml";
    public static final String DASHBOARD_VIEW = "/fxml/Dashboard.fxml";
    public static final String CASHIER_DASHBOARD_VIEW = "/fxml/CashierDashboard.fxml";

    @Override
    public void start(Stage primaryStage) throws Exception {
        // TẠM BỎ QUA ĐĂNG NHẬP (MOCK DATA) — sẽ chuyển sang LOGIN_VIEW khi login UI sẵn sàng
        NhanVienDTO mockAdmin = new NhanVienDTO();
        mockAdmin.setMaNV(1);
        mockAdmin.setTenDangNhap("admin");
        mockAdmin.setHoTen("Admin Tester");
        mockAdmin.setMaVaiTro(1);
        mockAdmin.setTenVaiTro("Quản trị viên");
        UserSession.setCurrentUser(mockAdmin);

        URL fxmlUrl = getClass().getResource("/fxml/MainMenuView.fxml");
        if (fxmlUrl == null) {
            throw new RuntimeException("Khong tim thay file /fxml/MainMenuView.fxml trong resources!");
        }

        FXMLLoader loader = new FXMLLoader(fxmlUrl);
        Scene scene = new Scene(loader.load(), 1366, 768);

        MainMenuViewFXMLController controller = loader.getController();
        controller.khoiTaoThongTinDangNhap(mockAdmin);

        URL cssUrl = getClass().getResource("/css/bakery.css");
        if (cssUrl != null) {
            scene.getStylesheets().add(cssUrl.toExternalForm());
        }

        primaryStage.setTitle("H3K Bakery - Main Menu (Bypass Login)");
        primaryStage.setMinWidth(1280);
        primaryStage.setMinHeight(720);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Helper để load FXML view (dùng khi chuyển sang login flow thực tế)
    public static Parent loadView(String viewPath) throws Exception {
        URL fxmlLocation = App.class.getResource(viewPath);
        if (fxmlLocation == null) {
            throw new IllegalStateException("Khong tim thay file FXML: " + viewPath);
        }
        return FXMLLoader.load(fxmlLocation);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
