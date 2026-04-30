package com.bakery.views.controllers;

import com.bakery.models.dto.KhachHangDTO;
import com.bakery.views.interfaces.ViewFactory;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.util.Objects;

/**
 * Implementation của ViewFactory - quản lý việc khởi tạo và mở các dialog.
 * Presenter sẽ gọi methods của interface này để mở dialog thay vì tự làm.
 */
public class DefaultViewFactory implements ViewFactory {

    private final Window ownerWindow;

    public DefaultViewFactory(Window ownerWindow) {
        this.ownerWindow = ownerWindow;
    }

    private Scene createSceneWithGlobalCss(Parent root) {
        Scene scene = new Scene(root);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/App.css")).toExternalForm());
        return scene;
    }

    @Override
    public void openAddCustomerDialog(Runnable onAddedCallback) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/CustomerAddView.fxml"));
            Parent root = loader.load();
            CustomerAddController controller = loader.getController();

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(ownerWindow);
            stage.setTitle("Thêm khách hàng mới");
            stage.setScene(createSceneWithGlobalCss(root));

            // Presenter và Factory được set trong Controller
            stage.setOnHiding(event -> {
                if (controller.isSaved() && onAddedCallback != null) {
                    onAddedCallback.run();
                }
            });

            stage.showAndWait();
        } catch (IOException e) {
            System.err.println("Lỗi mở dialog Thêm khách hàng: " + e.getMessage());
        }
    }

    @Override
    public void openUpdateCustomerDialog(KhachHangDTO customer, Runnable onUpdatedCallback) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/CustomerUpdateView.fxml"));
            Parent root = loader.load();
            CustomerUpdateController controller = loader.getController();

            // Truyền dữ liệu khách hàng vào Controller
            controller.loadCustomer(customer);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(ownerWindow);
            stage.setTitle("Cập nhật khách hàng");
            stage.setScene(createSceneWithGlobalCss(root));

            stage.setOnHiding(event -> {
                if (controller.isUpdated() && onUpdatedCallback != null) {
                    onUpdatedCallback.run();
                }
            });

            stage.showAndWait();
        } catch (IOException e) {
            System.err.println("Lỗi mở dialog Cập nhật khách hàng: " + e.getMessage());
        }
    }

    @Override
    public void openDeletedCustomersDialog(Runnable onClosedCallback) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/CustomerDeletedView.fxml"));
            Parent root = loader.load();
            CustomerDeletedController controller = loader.getController();

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(ownerWindow);
            stage.setTitle("Thùng rác - Khách hàng đã xóa");
            stage.setScene(createSceneWithGlobalCss(root));

            stage.setOnHiding(event -> {
                if (onClosedCallback != null) {
                    onClosedCallback.run();
                }
            });

            stage.showAndWait();
        } catch (IOException e) {
            System.err.println("Lỗi mở dialog Thùng rác: " + e.getMessage());
        }
    }

    @Override
    public void openMembershipTierDialog(Runnable onClosedCallback) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MembershipTierView.fxml"));
            Parent root = loader.load();
            MembershipTierController controller = loader.getController();

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(ownerWindow);
            stage.setTitle("Quản lý Hạng Thành viên");
            stage.setScene(createSceneWithGlobalCss(root));

            stage.setOnHiding(event -> {
                if (onClosedCallback != null) {
                    onClosedCallback.run();
                }
            });

            stage.showAndWait();
        } catch (IOException e) {
            System.err.println("Lỗi mở dialog Quản lý Hạng Thành viên: " + e.getMessage());
        }
    }
}
