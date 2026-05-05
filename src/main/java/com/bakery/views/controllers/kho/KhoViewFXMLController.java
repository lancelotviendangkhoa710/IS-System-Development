package com.bakery.views.controllers.kho;

import com.bakery.views.controllers.BaseController;
import com.bakery.views.controllers.hethong.AppShellController;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class KhoViewFXMLController extends BaseController {

    @FXML
    private void onVeMenu() {
        AppShellController.getInstance().loadView("/fxml/DashboardView.fxml");
    }

    @FXML
    private void onMoPOS() {
        AppShellController.getInstance().loadView("/fxml/DonHangView.fxml");
    }

    @FXML
    private void onActionTam() {
        if (lblThongBao != null) {
            lblThongBao.setText("Đã ghi nhận thao tác kho (demo giao diện).");
        }
    }
}
