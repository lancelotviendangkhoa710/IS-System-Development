package com.bakery.views.controllers.banhang;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class LyDoXacNhanDialogViewFXMLController {

    @FXML private Label    lblTitle;
    @FXML private Label    lblMessage;
    @FXML private Label    lblPrompt;
    @FXML private TextArea txtReason;
    @FXML private Label    lblError;

    private String result = null;

    // ── API cho màn hình gọi ──────────────────────────────────────────────────

    public void setDialogData(String title, String message, String promptText) {
        lblTitle.setText(title);
        lblMessage.setText(message);
        if (promptText != null && !promptText.isBlank()) {
            lblPrompt.setText(promptText);
        }
    }

    public String getReason() {
        return result;
    }

    // ── FXML events ───────────────────────────────────────────────────────────

    @FXML
    private void onConfirm() {
        String text = txtReason.getText().trim();
        if (text.isEmpty()) {
            lblError.setVisible(true);
            lblError.setManaged(true);
            txtReason.setStyle(
                    "-fx-font-size: 13px; -fx-font-family: 'Segoe UI';" +
                    "-fx-background-radius: 8px; -fx-border-radius: 8px;" +
                    "-fx-border-color: #DC2626; -fx-border-width: 1.5;" +
                    "-fx-background-color: white;");
            txtReason.requestFocus();
            return;
        }
        result = text;
        closeStage();
    }

    @FXML
    private void onCancel() {
        result = null;
        closeStage();
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private void closeStage() {
        Stage stage = (Stage) lblTitle.getScene().getWindow();
        stage.close();
    }
}
