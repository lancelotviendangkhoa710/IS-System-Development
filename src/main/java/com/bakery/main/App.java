package com.bakery.main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        URL fxmlUrl = getClass().getResource("/fxml/LoginView.fxml");
        if (fxmlUrl == null) {
            throw new RuntimeException("Khong tim thay file /fxml/LoginView.fxml trong resources!");
        }

        FXMLLoader loader = new FXMLLoader(fxmlUrl);
        Scene scene = new Scene(loader.load(), 1366, 768);

        URL cssUrl = getClass().getResource("/css/bakery.css");
        if (cssUrl != null) {
            scene.getStylesheets().add(cssUrl.toExternalForm());
        }

        primaryStage.setTitle("H3K Bakery - Dang nhap");
        primaryStage.setMinWidth(1280);
        primaryStage.setMinHeight(720);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
