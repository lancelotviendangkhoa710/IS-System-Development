package com.bakery.main;

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
        Scene scene = new Scene(loadView(LOGIN_VIEW));
        primaryStage.setTitle("Amitie Bakery");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.centerOnScreen();
        primaryStage.show();
    }

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
