package com.bakery;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

/**
 * Entry point của ứng dụng JavaFX.
 *
 * Flow khởi động:
 *   App.main() → launch() → start(Stage)
 *       → kiểm tra session → LoginView hoặc MainView
 *
 * Để chạy trong dev:  mvn javafx:run
 * Để đóng gói:        mvn javafx:jlink  (tạo custom JRE)
 */
public class App extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        primaryStage.setTitle("La Boulangerie — Hệ thống Quản lý Tiệm Bánh");
        primaryStage.setMinWidth(1024);
        primaryStage.setMinHeight(680);

        hienThiManHinh("/fxml/LoginView.fxml", 1280, 800);

        primaryStage.show();
    }

    /**
     * Load và hiển thị một màn hình FXML lên Stage chính.
     * Gọi từ các Controller để điều hướng giữa các màn hình.
     *
     * Ví dụ:  App.hienThiManHinh("/fxml/MainView.fxml", 1280, 800);
     */
    public static void hienThiManHinh(String fxmlPath, double width, double height) {
        try {
            URL url = App.class.getResource(fxmlPath);
            if (url == null) {
                hienThiManHinhTamThoi();
                return;
            }
            Parent root = FXMLLoader.load(url);
            Scene scene = new Scene(root, width, height);

            // Nạp CSS amber theme toàn cục
            URL css = App.class.getResource("/css/amber.css");
            if (css != null) scene.getStylesheets().add(css.toExternalForm());

            primaryStage.setScene(scene);
            primaryStage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Placeholder khi chưa có FXML — hiện logo + tên app */
    private static void hienThiManHinhTamThoi() {
        javafx.scene.layout.VBox root = new javafx.scene.layout.VBox(12);
        root.setAlignment(javafx.geometry.Pos.CENTER);
        root.setStyle("-fx-background-color: #FDFBF7;");

        javafx.scene.control.Label lblTen = new javafx.scene.control.Label("La Boulangerie");
        lblTen.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 36px; "
                + "-fx-font-weight: bold; -fx-text-fill: #92400E;");

        javafx.scene.control.Label lblMoTa = new javafx.scene.control.Label(
                "Hệ thống Quản lý Tiệm Bánh");
        lblMoTa.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 16px; "
                + "-fx-text-fill: #6B7280;");

        javafx.scene.control.Label lblHint = new javafx.scene.control.Label(
                "Đang thiết lập giao diện...");
        lblHint.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 12px; "
                + "-fx-text-fill: #9CA3AF;");

        root.getChildren().addAll(lblTen, lblMoTa, lblHint);

        Scene scene = new Scene(root, 1280, 800);
        primaryStage.setScene(scene);
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
