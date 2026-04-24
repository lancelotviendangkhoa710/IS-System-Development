package com.bakery.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;

public class MainController {
    @FXML private StackPane contentArea;
    @FXML private Button btnCustomers;
    @FXML private Button btnDashboard;
    @FXML private Button btnOrders;
    @FXML private Button btnProducts;
    @FXML private Button btnSettings;
    @FXML private Button btnLogout;

    @FXML
    public void initialize() {
        // Load CustomerInfoView mặc định khi khởi động
        navigateTo("CustomerInfoView.fxml");

        btnCustomers.setOnAction(e -> navigateTo("CustomerInfoView.fxml"));
        // Các nút khác sẽ được gắn sau
    }

    /**
     * Điều hướng đến một view FXML khác và truyền MainController cho controller con (nếu có).
     */
    public void navigateTo(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bakery/views/" + fxmlFile));
            Parent view = loader.load();
            Object controller = loader.getController();
            // Nếu controller cần tham chiếu MainController (ví dụ để chuyển trang)
            if (controller instanceof CustomerInfoController) {
                ((CustomerInfoController) controller).setMainController(this);
            } else if (controller instanceof CustomerDeletedController) {
                ((CustomerDeletedController) controller).setMainController(this);
            }
            contentArea.getChildren().setAll(view);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}