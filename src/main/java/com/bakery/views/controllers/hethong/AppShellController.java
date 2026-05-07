package com.bakery.views.controllers.hethong;

import com.bakery.model.dto.nhansu.NhanVienDTO;
import com.bakery.utils.FXMLLoaderUtil;
import javafx.fxml.FXML;
import javafx.scene.Node;

import java.util.logging.Logger;

/**
 * Controller chính điều phối việc chuyển đổi giữa các phân hệ (AppShell).
 * Nó ủy quyền việc quản lý vùng hiển thị (contentArea) cho MainMenuViewFXMLController.
 */
public class AppShellController {
    private static final Logger LOGGER = Logger.getLogger(AppShellController.class.getName());

    @FXML
    private MainMenuViewFXMLController mainMenuController;

    private static AppShellController instance;

    public AppShellController() {
        instance = this;
    }

    public static AppShellController getInstance() {
        return instance;
    }

    @FXML
    public void initialize() {
        LOGGER.info("AppShell initialized.");
        if (mainMenuController != null) {
            mainMenuController.setAppShellController(this);
            // Load màn hình mặc định (Dashboard)
            loadView("/fxml/DashboardView.fxml");
        } else {
            LOGGER.severe("mainMenuController is null! Check AppShell.fxml fx:include.");
        }
    }

    public void loadView(String fxmlPath) {
        if (mainMenuController == null || mainMenuController.getContentArea() == null) {
            LOGGER.warning("Cannot load view because mainMenuController or contentArea is null.");
            return;
        }
        
        LOGGER.info("AppShell loading view: " + fxmlPath);
        Node view = FXMLLoaderUtil.loadFXML(fxmlPath);
        if (view != null) {
            mainMenuController.getContentArea().getChildren().setAll(view);
        }
    }

    public void setNhanVienInfo(NhanVienDTO nv) {
        if (mainMenuController != null) {
            mainMenuController.khoiTaoThongTinDangNhap(nv);
        }
    }
}
