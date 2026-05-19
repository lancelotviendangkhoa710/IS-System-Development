package com.bakery.views.controllers.banhang;

import com.bakery.utils.DemoConfig;
import com.bakery.utils.DemoConfig.ConcurrencyMode;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;

/**
 * DemoControlPanelViewFXMLController — Bảng điều khiển Demo Mode.
 * 2 chế độ: PRODUCTION (bản gốc, có FOR UPDATE) và BUG_DEMO (không FOR UPDATE, có delay).
 */
public class DemoControlPanelViewFXMLController {

    @FXML private ToggleButton tglProduction;
    @FXML private ToggleButton tglBugDemo;
    @FXML private Label       lblCheDo;
    @FXML private Label       lblMoTa;

    @FXML
    public void initialize() {
        capNhatUI();
    }

    @FXML
    private void onChonProduction() {
        DemoConfig.setCheDo(ConcurrencyMode.PRODUCTION);
        capNhatUI();
    }

    @FXML
    private void onChonBugDemo() {
        DemoConfig.setCheDo(ConcurrencyMode.BUG_DEMO);
        capNhatUI();
    }

    @FXML
    private void onMoDemoPhantomRead() {
        try {
            URL url = getClass().getResource("/fxml/banhang/DemoPhantomReadView.fxml");
            if (url == null) return;
            FXMLLoader loader = new FXMLLoader(url);
            VBox root = loader.load();

            URL css = getClass().getResource("/css/bakery.css");
            Scene scene = new Scene(root, 800, 640);
            if (css != null) scene.getStylesheets().add(css.toExternalForm());

            Stage stage = new Stage();
            stage.setTitle("👻 Demo: Phantom Read");
            stage.initModality(Modality.NONE);
            stage.setScene(scene);
            stage.setResizable(true);
            stage.show();
        } catch (Exception e) {
            System.err.println("[DemoControlPanel] Lỗi mở DemoPhantomReadView: " + e.getMessage());
        }
    }

    private void capNhatUI() {
        ConcurrencyMode cheDo = DemoConfig.getCheDo();
        tglProduction.setSelected(cheDo == ConcurrencyMode.PRODUCTION);
        tglBugDemo.setSelected(cheDo == ConcurrencyMode.BUG_DEMO);

        lblCheDo.setText(cheDo.name());
        switch (cheDo) {
            case PRODUCTION -> {
                lblCheDo.setStyle("-fx-text-fill: #059669; -fx-font-weight: bold; -fx-font-size: 15px;");
                lblMoTa.setText("Gọi: PROC_TAODONHANG\nCó FOR UPDATE — kho không bao giờ âm.\nKhông có delay → tốc độ bình thường.");
            }
            case BUG_DEMO -> {
                lblCheDo.setStyle("-fx-text-fill: #DC2626; -fx-font-weight: bold; -fx-font-size: 15px;");
                lblMoTa.setText("Gọi: PROC_TAODONHANG_BUG\nKHÔNG có FOR UPDATE → 2 thu ngân đọc cùng lúc.\nCó delay ~3s → đủ thời gian để T2 chen vào.");
            }
        }
    }

    // ── Static helper ────────────────────────────────────────────────────────
    public static void moPanel(Stage owner) {
        try {
            URL url = DemoControlPanelViewFXMLController.class
                    .getResource("/fxml/banhang/DemoControlPanelView.fxml");
            if (url == null) return;
            FXMLLoader loader = new FXMLLoader(url);
            VBox root = loader.load();

            URL css = DemoControlPanelViewFXMLController.class.getResource("/css/bakery.css");
            Scene scene = new Scene(root, 520, 560);
            if (css != null) scene.getStylesheets().add(css.toExternalForm());

            Stage stage = new Stage();
            stage.setTitle("🎬 Demo Control Panel");
            stage.initModality(Modality.WINDOW_MODAL);
            if (owner != null) stage.initOwner(owner);
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
        } catch (Exception e) {
            System.err.println("[DemoControlPanel] Lỗi mở panel: " + e.getMessage());
        }
    }
}
