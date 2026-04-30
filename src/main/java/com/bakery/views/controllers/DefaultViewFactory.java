package com.bakery.views.controllers;

import com.bakery.model.dto.khachhang.KhachHangDTO;
import com.bakery.views.interfaces.ViewFactory;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.util.Objects;

public class DefaultViewFactory implements ViewFactory {

    private final Window ownerWindow;

    public DefaultViewFactory(Window ownerWindow) {
        this.ownerWindow = ownerWindow;
    }

    @Override
    public void openAddCustomerDialog(Runnable onAddedCallback) {
        openKhachHangTongHop(onAddedCallback);
    }

    @Override
    public void openUpdateCustomerDialog(KhachHangDTO customer, Runnable onUpdatedCallback) {
        openKhachHangTongHop(onUpdatedCallback);
    }

    @Override
    public void openDeletedCustomersDialog(Runnable onClosedCallback) {
        openKhachHangTongHop(onClosedCallback);
    }

    @Override
    public void openMembershipTierDialog(Runnable onClosedCallback) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MembershipTierView.fxml"));
            Parent root = loader.load();

            Stage stage = createModalStage("Quản lý Hạng Thành viên", root);
            stage.setOnHiding(event -> {
                if (onClosedCallback != null) {
                    onClosedCallback.run();
                }
            });
            stage.showAndWait();
        } catch (IOException e) {
            System.err.println("Loi mo dialog Quan ly hang thanh vien: " + e.getMessage());
        }
    }

    private Stage createModalStage(String title, Parent root) {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(ownerWindow);
        stage.setTitle(title);
        stage.setScene(createSceneWithGlobalCss(root));
        return stage;
    }

    private Scene createSceneWithGlobalCss(Parent root) {
        Scene scene = new Scene(root);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/App.css")).toExternalForm());
        return scene;
    }

    private void openKhachHangTongHop(Runnable onClosedCallback) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/KhachHangView.fxml"));
            Parent root = loader.load();
            Stage stage = createModalStage("Quản lý Khách hàng", root);
            stage.setOnHiding(event -> {
                if (onClosedCallback != null) {
                    onClosedCallback.run();
                }
            });
            stage.showAndWait();
        } catch (IOException e) {
            System.err.println("Loi mo man hinh quan ly khach hang: " + e.getMessage());
        }
    }
}
