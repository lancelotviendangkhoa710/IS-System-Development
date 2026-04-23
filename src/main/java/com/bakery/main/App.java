package com.bakery.main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

/**
 * Entry point của ứng dụng JavaFX.
 * Thay thế OrderUiSmokeTestApp (Swing).
 * Việc khởi tạo Presenter và inject factory được thực hiện trong OrderController.initialize().
 */
public class App extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        URL fxmlUrl = getClass().getResource("/fxml/OrderView.fxml");
        if (fxmlUrl == null) {
            throw new RuntimeException("Không tìm thấy file /fxml/OrderView.fxml trong resources!");
        }

        FXMLLoader loader = new FXMLLoader(fxmlUrl);
        Scene scene = new Scene(loader.load(), 1280, 720);

        // Áp dụng Amber Design System CSS
        URL cssUrl = getClass().getResource("/css/bakery.css");
        if (cssUrl != null) {
            scene.getStylesheets().add(cssUrl.toExternalForm());
        }

        primaryStage.setTitle("La Boulangerie — POS & Quản lý đơn hàng");
        primaryStage.setMinWidth(1024);
        primaryStage.setMinHeight(640);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
