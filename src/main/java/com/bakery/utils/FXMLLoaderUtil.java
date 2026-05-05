package com.bakery.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Tiện ích hỗ trợ nạp các tệp FXML một cách nhất quán và an toàn.
 */
public class FXMLLoaderUtil {
    private static final Logger LOGGER = Logger.getLogger(FXMLLoaderUtil.class.getName());

    public static Node loadFXML(String resourcePath) {
        try {
            URL url = FXMLLoaderUtil.class.getResource(resourcePath);
            if (url == null) {
                throw new IOException("Không tìm thấy tài nguyên: " + resourcePath);
            }
            return FXMLLoader.load(url);
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.log(Level.SEVERE, "Lỗi nạp FXML: " + resourcePath, e);
            showErrorAlert("Lỗi ứng dụng", "Không thể nạp màn hình: " + resourcePath, e.getMessage());
            return null;
        }
    }

    public static <T> T loadFXML(String resourcePath, Object controller) {
        try {
            URL url = FXMLLoaderUtil.class.getResource(resourcePath);
            if (url == null) {
                throw new IOException("Không tìm thấy tài nguyên: " + resourcePath);
            }
            FXMLLoader loader = new FXMLLoader(url);
            if (controller != null) {
                loader.setController(controller);
            }
            return loader.load();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Lỗi nạp FXML với controller: " + resourcePath, e);
            showErrorAlert("Lỗi ứng dụng", "Không thể nạp màn hình: " + resourcePath, e.getMessage());
            return null;
        }
    }

    public static FXMLLoader getLoader(String resourcePath) {
        URL url = FXMLLoaderUtil.class.getResource(resourcePath);
        if (url == null) {
            LOGGER.log(Level.SEVERE, "Không tìm thấy tài nguyên: {0}", resourcePath);
            return null;
        }
        return new FXMLLoader(url);
    }

    private static void showErrorAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
