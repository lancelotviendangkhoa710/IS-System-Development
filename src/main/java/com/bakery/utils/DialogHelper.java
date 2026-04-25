package com.bakery.utils;

import com.bakery.views.controllers.ReasonConfirmDialogController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.Optional;

public class DialogHelper {

    /**
     * Hiển thị dialog xác nhận có nhập lý do bắt buộc.
     *
     * @param title      Tiêu đề dialog (hiển thị màu đỏ).
     * @param message    Nội dung câu hỏi xác nhận.
     * @param promptText Nhãn cho ô nhập lý do.
     * @return Optional chứa lý do nếu user xác nhận, empty nếu user huỷ.
     */
    public static Optional<String> showReasonConfirmDialog(
            String title, String message, String promptText) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    DialogHelper.class.getResource("/fxml/ReasonConfirmDialog.fxml"));
            Parent root = loader.load();

            ReasonConfirmDialogController controller = loader.getController();
            controller.setDialogData(title, message, promptText);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.TRANSPARENT);
            stage.setResizable(false);

            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);
            stage.setScene(scene);
            stage.showAndWait();

            return Optional.ofNullable(controller.getReason());
        } catch (IOException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }
}
