package com.bakery.views.controllers.hethong;

import com.bakery.model.dto.nhansu.NhanVienDTO;
import com.bakery.model.enums.SystemModule;
import com.bakery.services.nhansu.PhanQuyenService;
import com.bakery.utils.UserSession;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Shell Controller - Trái tim của giao diện ứng dụng.
 * Quản lý Sidebar, Header và việc chuyển đổi giữa các phân hệ nghiệp vụ.
 */
public class MainViewFXMLController {
    private static final Logger LOGGER = Logger.getLogger(MainViewFXMLController.class.getName());

    @FXML private VBox vboxNavItems;
    @FXML private Label lblAvatar;
    @FXML private Label lblHoTen;
    @FXML private Label lblVaiTro;
    @FXML private Label lblTieuDeManHinh;
    @FXML private Label lblNhanVienHeader;
    @FXML private Label lblAvatarHeader;
    @FXML private StackPane contentArea;

    private final PhanQuyenService phanQuyenService = new PhanQuyenService();
    private NhanVienDTO currentUser;

    @FXML
    public void initialize() {
        this.currentUser = UserSession.getCurrentUser();
        if (this.currentUser == null) {
            Platform.runLater(this::onDangXuat);
            return;
        }

        thietLapThongTinNguoiDung();
        xayDungMenuDieuHuong();
        
        // Màn hình mặc định khi vào App: Dashboard
        loadView("/fxml/BangDieuKhienView.fxml", "Bảng điều khiển");
    }

    private void thietLapThongTinNguoiDung() {
        String hoTen = currentUser.getHoTen();
        String vaiTro = currentUser.getTenVaiTro();
        String initial = hoTen != null && !hoTen.isEmpty() ? hoTen.substring(0, 1).toUpperCase() : "?";

        lblHoTen.setText(hoTen);
        lblVaiTro.setText(vaiTro);
        lblAvatar.setText(initial);
        
        lblNhanVienHeader.setText(hoTen);
        lblAvatarHeader.setText(initial);
    }

    private void xayDungMenuDieuHuong() {
        vboxNavItems.getChildren().clear();
        Set<SystemModule> modules = phanQuyenService.layModulesDuocCap(currentUser);

        // Nút Tổng quan (Luôn có)
        taoNutMenu("📊 Tổng quan", "/fxml/BangDieuKhienView.fxml", "Bảng điều khiển");

        if (modules.contains(SystemModule.BAN_HANG)) {
            taoNutMenu("🛒 Bán hàng (POS)", "/fxml/DonHangView.fxml", "Hệ thống bán hàng");
            taoNutMenu("🚚 Theo dõi đơn hàng", "/fxml/TheoDoiDonHangView.fxml", "Theo dõi đơn hàng");
        }

        if (modules.contains(SystemModule.KHACH_HANG)) {
            taoNutMenu("🤝 Khách hàng", "/fxml/KhachHangView.fxml", "Quản lý khách hàng");
        }

        if (modules.contains(SystemModule.KHO)) {
            taoNutMenu("📦 Kho hàng", "/fxml/KhoView.fxml", "Quản lý kho nguyên liệu");
            taoNutMenu("🍞 Sản phẩm", "/fxml/SanPhamView.fxml", "Quản lý sản phẩm");
            taoNutMenu("📂 Danh mục", "/fxml/DanhMucSPView.fxml", "Danh mục sản phẩm");
            taoNutMenu("🍎 Nguyên liệu", "/fxml/NguyenLieuView.fxml", "Danh mục nguyên liệu");
            taoNutMenu("🏭 Nhà cung cấp", "/fxml/QuanLyNhaCungCapView.fxml", "Quản lý nhà cung cấp");
        }

        if (modules.contains(SystemModule.NHA_BEP)) {
            taoNutMenu("👨‍🍳 Màn hình bếp", "/fxml/ThoBepDashboardView.fxml", "Kitchen Display System");
        }

        if (modules.contains(SystemModule.NHAN_SU)) {
            taoNutMenu("👥 Nhân sự", "/fxml/QuanLyNhanVienView.fxml", "Quản lý nhân viên");
        }

        if (modules.contains(SystemModule.BAO_CAO)) {
            taoNutMenu("📈 Báo cáo", "/fxml/BaoCaoView.fxml", "Thống kê & Báo cáo");
        }
        
        taoNutMenu("💰 Sổ quỹ", "/fxml/SoQuyView.fxml", "Quản lý thu chi");
    }

    private void taoNutMenu(String text, String fxmlPath, String screenTitle) {
        Button btn = new Button(text);
        btn.getStyleClass().add("nav-item");
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        
        btn.setOnAction(e -> {
            // Highlight nút đang chọn
            vboxNavItems.getChildren().forEach(node -> node.getStyleClass().remove("nav-item-active"));
            btn.getStyleClass().add("nav-item-active");
            
            loadView(fxmlPath, screenTitle);
        });

        vboxNavItems.getChildren().add(btn);
    }

    public void loadView(String fxmlPath, String title) {
        try {
            lblTieuDeManHinh.setText(title);
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Node view = loader.load();
            
            // Xóa nội dung cũ và set nội dung mới
            contentArea.getChildren().setAll(view);
            
            LOGGER.log(Level.INFO, "Loaded view: {0}", fxmlPath);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Lỗi load view con: " + fxmlPath, e);
            hienThiThongBaoLoi("Lỗi hệ thống", "Không thể tải giao diện: " + title);
        }
    }

    @FXML
    private void onDangXuat() {
        try {
            UserSession.setCurrentUser(null);
            
            URL fxmlUrl = getClass().getResource("/fxml/DangNhapView.fxml");
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();

            Scene scene = new Scene(root);
            URL cssUrl = getClass().getResource("/css/bakery.css");
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
            }

            Stage stage = (Stage) vboxNavItems.getScene().getWindow();
            stage.setTitle("H3K Bakery - Đăng nhập");
            stage.setScene(scene);
            stage.setResizable(false);
            stage.centerOnScreen();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Lỗi đăng xuất", e);
        }
    }

    private void hienThiThongBaoLoi(String title, String msg) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
