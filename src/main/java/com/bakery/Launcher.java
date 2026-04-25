package com.bakery;

import com.bakery.utils.SessionManager;
import com.bakery.models.dto.NhanVienDTO;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

public class Launcher extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Khởi tạo phiên đăng nhập với nhân viên mặc định (ID = 1)
        NhanVienDTO demoUser = new NhanVienDTO();
        demoUser.setMaNV(2);  // Thay bằng số bạn vừa lấy ở trên
        demoUser.setHoTen("Admin CRM");
        SessionManager.setCurrentUser(demoUser);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bakery/views/CustomerInfoView.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root, 1366, 768);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/bakery/views/App.css")).toExternalForm());
        primaryStage.setTitle("Artisan Bakery Manager");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}