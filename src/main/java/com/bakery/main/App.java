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
        // Khởi động từ màn hình đăng nhập
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
