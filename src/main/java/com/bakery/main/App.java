package com.bakery.main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.net.URL;

public class App extends Application {
    public static final String LOGIN_VIEW = "/fxml/hethong/DangNhapView.fxml";
    public static final String MAIN_MENU_VIEW = "/fxml/hethong/MainMenuView.fxml";
    public static final String APP_SHELL_VIEW = "/fxml/hethong/AppShell.fxml";
    public static final String DASHBOARD_VIEW = "/fxml/hethong/BangDieuKhienView.fxml";
    public static final String THU_NGAN_DASHBOARD_VIEW = "/fxml/hethong/ThuNganDashboardView.fxml";
    public static final String THO_BEP_DASHBOARD_VIEW = "/fxml/hethong/ThoBepDashboardView.fxml";
    public static final String THU_KHO_DASHBOARD_VIEW = "/fxml/hethong/ThuKhoDashboardView.fxml";

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

        // Bind background image to scale with window resize / fullscreen
        ImageView bgImageView = (ImageView) root.lookup("#bgImageView");
        if (bgImageView != null) {
            bgImageView.fitWidthProperty().bind(scene.widthProperty());
            bgImageView.fitHeightProperty().bind(scene.heightProperty());
        }

        primaryStage.setTitle("H3K Bakery - Đăng nhập");
        primaryStage.setResizable(true);
        primaryStage.setMinWidth(1280);
        primaryStage.setMinHeight(720);
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
