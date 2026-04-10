package com.bakery.main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("/views/Login"), 1120, 760);
        stage.setScene(scene);
        stage.setMinWidth(980);
        stage.setMinHeight(700);
        stage.setTitle("Bakery Management System");
        stage.show();
    }

    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    public static void showLogin() throws IOException {
        setRoot("/views/Login");
    }

    public static void showRegister() throws IOException {
        setRoot("/views/Register");
    }

    public static void showDashboard() throws IOException {
        setRoot("/views/Dashboard");
    }

    public static void showHome() throws IOException {
        setRoot("/views/Home");
    }

    public static void showChangePassword() throws IOException {
        setRoot("/views/ChangePassword");
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }
}
