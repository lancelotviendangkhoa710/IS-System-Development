package com.bakery.utils;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextInputDialog;
import java.net.URL;
import java.util.Optional;

/**
 * Tiện ích hiển thị thông báo (Alert) và hộp thoại trong JavaFX.
 * Mọi Alert/Dialog đều được gắn bakery.css để thống nhất brand màu Amber.
 */
public class DialogHelper {

    /**
     * Gắn bakery.css vào DialogPane — áp dụng Amber theme cho mọi Alert/Dialog.
     * Gọi ngay sau khi tạo Alert/Dialog, trước khi showAndWait().
     */
    public static void applyBakeryTheme(Dialog<?> dialog) {
        URL css = DialogHelper.class.getResource("/css/bakery.css");
        if (css != null) {
            dialog.getDialogPane().getStylesheets().add(css.toExternalForm());
        }
    }

    public static void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        applyBakeryTheme(alert);
        alert.showAndWait();
    }

    public static void showInfo(String title, String content) {
        showAlert(Alert.AlertType.INFORMATION, title, content);
    }

    public static void showError(String title, String content) {
        showAlert(Alert.AlertType.ERROR, title, content);
    }

    public static void showWarning(String title, String content) {
        showAlert(Alert.AlertType.WARNING, title, content);
    }

    public static boolean showConfirm(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        applyBakeryTheme(alert);
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    /**
     * Hiển thị hộp thoại xác nhận kèm theo ô nhập lý do.
     * Trả về Optional chứa lý do nếu nhấn OK, ngược lại trả về Optional.empty().
     */
    public static Optional<String> showReasonConfirmDialog(String title, String content, String reasonPrompt) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle(title);
        dialog.setHeaderText(content);
        dialog.setContentText(reasonPrompt);
        applyBakeryTheme(dialog);
        return dialog.showAndWait();
    }
}
