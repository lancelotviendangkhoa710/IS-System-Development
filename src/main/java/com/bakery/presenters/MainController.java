package com.bakery.presenters;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

import com.bakery.App;
import java.io.IOException;

public class MainController {

    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        try {
            App.setRoot("/fxml/SanPham");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
