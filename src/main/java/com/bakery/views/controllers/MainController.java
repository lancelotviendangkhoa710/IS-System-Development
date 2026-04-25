package com.bakery.views.controllers;

import com.bakery.App;
import com.bakery.presenters.MainPresenter;
import com.bakery.presenters.ModuleDef;
import com.bakery.views.interfaces.IMainView;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class MainController implements IMainView {

    @FXML private VBox       vboxNavItems;
    @FXML private Label      lblHoTen;
    @FXML private Label      lblVaiTro;
    @FXML private Label      lblAvatar;
    @FXML private Label      lblTieuDeManHinh;
    @FXML private Label      lblNhanVienHeader;
    @FXML private Label      lblAvatarHeader;
    @FXML private StackPane  contentArea;

    private final MainPresenter presenter = new MainPresenter(this);

    private Button nutNavDangActive;

    @FXML
    public void initialize() {
        presenter.onInitialize();
    }

    @FXML
    private void onDangXuat() {
        presenter.onDangXuat();
    }

    // ── IMainView ─────────────────────────────────────────────────────────────

    @Override
    public void setHoTen(String hoTen) {
        Platform.runLater(() -> {
            lblHoTen.setText(hoTen);
            lblNhanVienHeader.setText(hoTen);
        });
    }

    @Override
    public void setVaiTro(String tenVaiTro) {
        Platform.runLater(() -> lblVaiTro.setText(tenVaiTro));
    }

    @Override
    public void setAvatar(String kyTu) {
        Platform.runLater(() -> {
            lblAvatar.setText(kyTu);
            lblAvatarHeader.setText(kyTu);
        });
    }

    @Override
    public void buildMenu(List<ModuleDef> modules) {
        Platform.runLater(() -> {
            vboxNavItems.getChildren().clear();
            for (ModuleDef module : modules) {
                Button btn = new Button(module.label());
                btn.getStyleClass().add("nav-item");
                btn.setMaxWidth(Double.MAX_VALUE);
                btn.setOnAction(e -> chuyenManHinh(module, btn));
                vboxNavItems.getChildren().add(btn);
            }
            if (!vboxNavItems.getChildren().isEmpty()) {
                ((Button) vboxNavItems.getChildren().get(0)).fire();
            }
        });
    }

    @Override
    public void navigateToLogin() {
        Platform.runLater(() -> App.hienThiManHinh("/fxml/LoginView.fxml", 1024, 680));
    }

    // ── Điều hướng nội bộ (pure UI) ──────────────────────────────────────────

    private void chuyenManHinh(ModuleDef module, Button navButton) {
        if (nutNavDangActive != null) {
            nutNavDangActive.getStyleClass().remove("nav-item-active");
            if (!nutNavDangActive.getStyleClass().contains("nav-item"))
                nutNavDangActive.getStyleClass().add("nav-item");
        }
        navButton.getStyleClass().remove("nav-item");
        navButton.getStyleClass().add("nav-item-active");
        nutNavDangActive = navButton;

        lblTieuDeManHinh.setText(module.label());

        try {
            URL url = getClass().getResource(module.fxmlPath());
            if (url == null) {
                contentArea.getChildren().setAll(taoPlaceholder(module.label()));
                return;
            }
            Parent content = FXMLLoader.load(url);
            contentArea.getChildren().setAll(content);
        } catch (IOException e) {
            e.printStackTrace();
            contentArea.getChildren().setAll(taoPlaceholder(module.label()));
        }
    }

    private VBox taoPlaceholder(String tenManHinh) {
        VBox box = new VBox(16);
        box.setAlignment(Pos.CENTER);
        box.getStyleClass().add("placeholder-panel");
        VBox.setMargin(box, new Insets(0));

        Label lblTen = new Label(tenManHinh);
        lblTen.getStyleClass().add("text-title-card");
        lblTen.setStyle("-fx-text-fill: #92400E;");

        Label lblHint = new Label("Màn hình này đang được phát triển...");
        lblHint.getStyleClass().add("text-secondary");

        box.getChildren().addAll(lblTen, lblHint);
        return box;
    }
}
