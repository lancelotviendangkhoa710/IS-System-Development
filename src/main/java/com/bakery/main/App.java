package com.bakery.main;

import com.bakery.model.dto.NhanVienDTO;
import com.bakery.utils.UserSession;
import com.bakery.views.controllers.MainMenuViewFXMLController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // TẠM BỎ QUA ĐĂNG NHẬP (MOCK DATA)
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

    public static void main(String[] args) {
        launch(args);
    }
}
