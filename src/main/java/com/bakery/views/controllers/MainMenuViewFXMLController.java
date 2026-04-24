package com.bakery.views.controllers;

import com.bakery.model.dto.NhanVienDTO;
import com.bakery.model.dao.ThongKeDAO;
import com.bakery.services.AuthorizationService;
import com.bakery.services.AuthorizationService.ModuleKey;
import com.bakery.utils.UserSession;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Map;
import java.util.Set;

public class MainMenuViewFXMLController {
    @FXML private Label lblTenNguoiDung;
    @FXML private Label lblVaiTro;
    @FXML private Label lblThongBao;
    @FXML private Label lblBannerName;

    @FXML private Button btnPos;
    @FXML private Button btnInventory;
    @FXML private Button btnStaff;
    @FXML private Button btnReports;
    @FXML private Button btnKds;
    @FXML private Button btnAuditLogs;
    @FXML private HBox hboxBestSellersMenu;

    private final AuthorizationService authorizationService = new AuthorizationService();
    private final ThongKeDAO thongKeDAO = new ThongKeDAO();
    private NhanVienDTO currentUser;

    public void khoiTaoThongTinDangNhap(NhanVienDTO nhanVien) {
        this.currentUser = nhanVien != null ? nhanVien : UserSession.getCurrentUser();
        if (this.currentUser == null) {
            lblThongBao.setText("Khong tim thay thong tin dang nhap.");
            return;
        }

        UserSession.setCurrentUser(this.currentUser);
        boolean laAdmin = authorizationService.laAdmin(this.currentUser);
        Set<ModuleKey> modulesDuocCap = authorizationService.layModulesDuocCap(this.currentUser);

        lblTenNguoiDung.setText(this.currentUser.getHoTen() == null ? this.currentUser.getTenDangNhap() : this.currentUser.getHoTen());
        if (lblBannerName != null) {
            lblBannerName.setText(lblTenNguoiDung.getText());
        }
        lblVaiTro.setText(xayDungNhanQuyen(this.currentUser, laAdmin));
        capNhatTrangThaiNut(btnPos, modulesDuocCap.contains(ModuleKey.POS));
        capNhatTrangThaiNut(btnInventory, modulesDuocCap.contains(ModuleKey.INVENTORY));
        capNhatTrangThaiNut(btnStaff, modulesDuocCap.contains(ModuleKey.STAFF));
        capNhatTrangThaiNut(btnReports, modulesDuocCap.contains(ModuleKey.REPORTS));
        capNhatTrangThaiNut(btnKds, modulesDuocCap.contains(ModuleKey.KDS));
        capNhatTrangThaiNut(btnAuditLogs, modulesDuocCap.contains(ModuleKey.AUDIT_LOGS));

        loadBestSellers();
    }

    private void loadBestSellers() {
        if (hboxBestSellersMenu == null) return;
        hboxBestSellersMenu.getChildren().clear();
        
        Map<String, Integer> top5 = thongKeDAO.getTop5BanChay();
        for (Map.Entry<String, Integer> entry : top5.entrySet()) {
            VBox card = new VBox(8);
            card.setStyle("-fx-background-color: #ffffff; -fx-padding: 16 24; -fx-background-radius: 12; -fx-border-color: #E5E7EB; -fx-border-radius: 12; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 10, 0, 0, 2);");
            card.setPrefWidth(200);

            Label lblIcon = new Label("🧁");
            lblIcon.setStyle("-fx-font-size: 24px; -fx-background-color: #FFF3ED; -fx-padding: 8 12; -fx-background-radius: 12;");

            Label lblName = new Label(entry.getKey());
            lblName.setStyle("-fx-font-weight: 700; -fx-font-size: 14px; -fx-text-fill: #1b1c1a;");

            Label lblQty = new Label(entry.getValue() + " đã bán");
            lblQty.setStyle("-fx-font-weight: bold; -fx-text-fill: #D85A30; -fx-font-size: 12px;");

            card.getChildren().addAll(lblIcon, lblName, lblQty);
            hboxBestSellersMenu.getChildren().add(card);
        }
    }

    @FXML
    private void onMoPOS() {
        moScene(btnPos, "/fxml/OrderView.fxml", "H3K Bakery - POS", 1280, 720, "Khong the mo POS: ");
    }

    @FXML
    private void onMoInventory() {
        moScene(btnInventory, "/fxml/InventoryView.fxml", "H3K Bakery - Inventory", 1366, 768, "Khong the mo Inventory: ");
    }

    @FXML
    private void onMoReports() {
        moScene(btnReports, "/fxml/ReportsView.fxml", "H3K Bakery - Thống kê Kinh doanh", 1366, 768, "Khong the mo Thống kê: ");
    }

    @FXML
    private void onThemNhanVien() {
        try {
            URL fxmlUrl = getClass().getResource("/fxml/ThemNhanVienDialog.fxml");
            if (fxmlUrl == null) throw new RuntimeException("Không tìm thấy ThemNhanVienDialog.fxml");
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Scene scene = new Scene(loader.load());
            URL cssUrl = getClass().getResource("/css/bakery.css");
            if (cssUrl != null) scene.getStylesheets().add(cssUrl.toExternalForm());
            Stage dialog = new Stage();
            dialog.setTitle("H3K Bakery - Thêm Nhân Viên");
            dialog.setScene(scene);
            dialog.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            dialog.initOwner(lblTenNguoiDung.getScene().getWindow());
            dialog.setResizable(false);
            dialog.showAndWait();
        } catch (Exception ex) {
            lblThongBao.setText("Lỗi mở dialog thêm nhân viên: " + ex.getMessage());
        }
    }
    @FXML
    private void onModuleChuaSanSang() {
        lblThongBao.setText("Chuc nang dang phat trien.");
    }

    private void moScene(Button source, String fxmlPath, String title, int width, int height, String errorPrefix) {
        try {
            URL fxmlUrl = getClass().getResource(fxmlPath);
            if (fxmlUrl == null) {
                throw new RuntimeException("Khong tim thay " + fxmlPath);
            }
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Scene scene = new Scene(loader.load(), width, height);
            if ("/fxml/OrderView.fxml".equals(fxmlPath)) {
                OrderViewFXMLController controller = loader.getController();
                controller.apDungThongTinDangNhap(UserSession.getCurrentUser());
            }
            URL cssUrl = getClass().getResource("/css/bakery.css");
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
            }
            Stage stage = (Stage) source.getScene().getWindow();
            stage.setTitle(title);
            stage.setScene(scene);
            stage.centerOnScreen();
        } catch (Exception ex) {
            lblThongBao.setText(errorPrefix + ex.getMessage());
        }
    }

    private void capNhatTrangThaiNut(Button button, boolean duocCapQuyen) {
        button.setDisable(!duocCapQuyen);
        button.setOpacity(duocCapQuyen ? 1.0 : 0.45);
        if (button.getParent() != null) {
            button.getParent().setDisable(!duocCapQuyen);
        }
    }

    private String xayDungNhanQuyen(NhanVienDTO nhanVien, boolean laAdmin) {
        String tenVaiTro = nhanVien.getTenVaiTro();
        if (tenVaiTro != null && !tenVaiTro.isBlank()) {
            return laAdmin ? tenVaiTro + " - Full Access" : tenVaiTro + " - Role Access";
        }
        return laAdmin ? "Admin Access" : "Role Access";
    }
}
